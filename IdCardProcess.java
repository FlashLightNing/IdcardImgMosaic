package simplecode;

import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;


public class IdCardProcess implements BaseType {

	private static final int yuzhi = 150;
	private static final int fushitimes = 14;
	private static final int pengzhangtimes = 10;
	private static final int numsofQuyumax = 18;
	private static boolean isSavePic = true;//是否生成中间步骤的图片
	private String selaparate = "";

	@Override
	public String process(String src, String public_path, String selaparate) {
		if (StringUtils.isNull(src))
			return "";

		List<EachCount> list = ImageUtils.getContouresList(src, public_path,
				selaparate, isSavePic, yuzhi, fushitimes, pengzhangtimes);
		if (list.size() == 0)
			return "";
		sortList(list);

		IplImage RawImage = cvLoadImage(src);

		IplImage GrayImage = cvCreateImage(cvGetSize(RawImage), IPL_DEPTH_8U, 1);
		ImageUtils.huiduhua(RawImage, GrayImage, false, null);

		IplImage finalImage = RawImage;
		String finalImgPath = public_path + selaparate + "final.jpg";

		// 对每一个连通图区域进行处理和根据条件筛选
		for (int i = 0; i < list.size(); i++) {
			EachCount e = list.get(i);
			// 过滤加码区域内的连通图
			boolean state = getMosaicQuyu(i, public_path+selaparate, GrayImage, e.getX(),
					e.getY(), (int) e.getWidth(), (int) e.getHeight());

			// 如果处理到第5个还没有符合要求的，就认为处理失败了
			if (i == 5) {
				ImageUtils.releaseImage(RawImage, GrayImage, finalImage);
				list = null;
				return "";
			}
			if (state) {
				// 设置打码区域
				cvSetImageROI(
						RawImage,
						cvRect(e.getX(), e.getY(), (int) e.getWidth(),
								(int) e.getHeight()));
				Size s = new Size(101, 101);
				Mat beforeMat = cvarrToMat(RawImage);
				Mat afterMat = cvarrToMat(finalImage);
				GaussianBlur(beforeMat, afterMat, s, 0);
				cvResetImageROI(RawImage);
				System.out.println("state=true...idcard");
				ImageUtils.saveImg(finalImgPath, finalImage);

				ImageUtils.releaseMat(beforeMat, afterMat);
				ImageUtils.releaseImage(RawImage, GrayImage, finalImage);

				list = null;
				return finalImgPath;
			}

		}

		ImageUtils.releaseImage(RawImage, GrayImage, finalImage);
		list = null;
		return "";
	}

	@Override
	public boolean getMosaicQuyu(int index, String public_path,
			IplImage greyImgImage, int location_x, int location_y,
			double maxWidth, double maxhight) {
		// ***************将每一个连通图区域都生成quyuindex图片
		cvSetImageROI(greyImgImage,
				cvRect(location_x, location_y, (int) (maxWidth), (int) (maxhight)));
		IplImage quyu = cvCloneImage(greyImgImage);
		cvResetImageROI(greyImgImage);
		// ************************

		// 二值化处理
		IplImage erdu = cvCloneImage(quyu);
		
		ImageUtils.twoValueLast(quyu, erdu, 180, true, public_path + selaparate
				+ "quyu_twoValue.jpg");
		
		
		IplImage dest = cvLoadImage(public_path + selaparate
				+ "quyu_twoValue.jpg");
		CvMemStorage storage = ImageUtils.getCvMemStorage();
		CvSeq contour = ImageUtils.getCVseq();
		int coun = cvFindContours(erdu, storage, contour,
				Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL,
				CV_CHAIN_APPROX_SIMPLE);//CV_RETR_CCOMP   CV_RETR_LIST
		//cvSaveImage(public_path+"/number_lianton1111"+index+".jpg", erdu);
		//cvDrawContours(erdu, contour, cvScalar(255), cvScalar(100), 1);
		
		//cvSaveImage(public_path+"/number_lianton22"+index+".jpg", erdu);
		System.out.println("count="+coun);
		ImageUtils.clearMenStorage(storage);


//		int quyuWidth = dest.width();
//		int quyuHeight = dest.height();
//		System.out.println(quyuWidth+",,"+quyuHeight);
//		for (; contour != null; contour = contour.h_next()) {
//			CvRect r = cvBoundingRect(contour, 0);
//			cvRectangle(dest, cvPoint(r.x(),r.y()),cvPoint(r.x()+r.width(),r.y()+r.height()),CV_RGB(255, 0, 0),1,CV_AA,0);
//			ImageUtils.closeRect(r);
//			sum++;
//		}
		
//		cvSaveImage(public_path+"/dest"+index+".png", dest);
		ImageUtils.releaseImage(quyu, erdu, dest);
		if (coun ==numsofQuyumax) {
			return true;
		}
		return false;
	}
	

	public static void sortList(List<EachCount> list) {
		Collections.sort(list,myComparator);
	}
	
	static Comparator<EachCount> myComparator =new Comparator<EachCount>() {

		@Override
		public int compare(EachCount o1, EachCount o2) {
			double cha1 = o1.getWidth() > o1.getHeight() ? o1.getWidth()
					/ o1.getHeight() : o1.getHeight() / o1.getWidth();

			double cha2 = o2.getWidth() > o2.getHeight() ? o2.getWidth()
					/ o2.getHeight() : o2.getHeight() / o2.getWidth();

			if (cha1 > cha2)
				return -1;
			if (cha1 < cha2)
				return 1;

			return 0;
		}
	};

	/**过滤区域。原先的方法获取的个数不准确，所以需要对每个轮廓进行筛选
	 * @param rect
	 * @param quyuWidth
	 * @param quyuHeight
	 * @return
	 */
	public static boolean filterContour(CvRect rect, int quyuWidth,
			int quyuHeight) {
		double width = rect.width();
		double hight = rect.height();

		// 长宽比
		double widthRate = width / quyuWidth;
		double hightRate = hight / quyuHeight;
		if (widthRate > 0.9 || hightRate > 0.9
				|| (widthRate < 0.1 && hightRate < 0.1) || (widthRate > 0.07)
				|| widthRate < 0.02) {
			return false;
		}

		return true;
	}

}
