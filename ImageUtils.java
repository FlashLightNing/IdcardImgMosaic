package simplecode;

import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvErode;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;


/**
 * 图像相关的一些工具方法，如保存图像等
 * 
 *
 */
public class ImageUtils {

	private ImageUtils() {
	}


	/**
	 * 灰度化
	 * 
	 * @param RawImage
	 * @param GrayImage
	 */
	public static void huiduhua(IplImage RawImage, IplImage GrayImage,
			boolean isSaveImg, String imgPath) {
		cvCvtColor(RawImage, GrayImage, CV_BGR2GRAY);
		if (isSaveImg) {
			saveImg(imgPath, GrayImage);
		}

	}

	public static void releaseImage(IplImage... img) {
		try {
			for (int i = 0; i < img.length; i++) {
				if (img[i] != null) {
					cvReleaseImage(img[i]);
					img[i] = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void releaseMat(Mat... mat) {
		try {
			for (int i = 0; i < mat.length; i++) {
				if (mat[i] != null)
					mat[i].deallocate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void clearCvseq(CvSeq seq) {
		try {
			cvClearSeq(seq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CvMemStorage getCvMemStorage() {
		return CvMemStorage.create();
	}

	public static void clearMenStorage(CvMemStorage memStorage) {
		try {
			if (memStorage != null) {
				cvClearMemStorage(memStorage);
				memStorage = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CvSeq getCVseq() {
		return new CvSeq(null);
	}

	public static Mat getMat(IplImage img) {
		return cvarrToMat(img);
	}

	public static void closeRect(CvRect rect) {
		try {
			if (rect != null)
				rect.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<EachCount> getContouresList(String src,
			String public_path, String selaparate, boolean isSavePic,
			int twoValueyuzhi, int fushitimes, int pengzhangtimes) {

		IplImage rawImage = cvLoadImage(src);

		// 灰度化
		IplImage grayImage = cvCreateImage(cvGetSize(rawImage), IPL_DEPTH_8U, 1);
		String grayImagePath = public_path + "gray.jpg";

		// 2值化
		IplImage twoValue = cvCreateImage(cvGetSize(rawImage), IPL_DEPTH_8U, 1);
		String twoValuePath = public_path + "twoValue.jpg";

		// 腐蚀
		IplImage fushi = cvCreateImage(cvGetSize(rawImage), IPL_DEPTH_8U, 1);
		String fushiPath = public_path + "fushi.jpg";

		// 膨胀
		IplImage pengzhang = cvCreateImage(cvGetSize(rawImage), IPL_DEPTH_8U, 1);

		String pengzhangPath = public_path + "pengzhang.jpg";
		ImageUtils.huiduhua(rawImage, grayImage, isSavePic, grayImagePath);

		ImageUtils.twoValue(grayImage, twoValue, twoValueyuzhi, isSavePic,
				twoValuePath);

		ImageUtils.fushi(twoValue, fushi, fushitimes, isSavePic, fushiPath);

		ImageUtils.pengzhang(fushi, pengzhang, pengzhangtimes, isSavePic,
				pengzhangPath);

		IplImage lianton = cvCloneImage(pengzhang);

		CvMemStorage storage = CvMemStorage.create();

		CvSeq contour = ImageUtils.getCVseq();
		int liantonNums = cvFindContours(lianton, storage, contour,
				Loader.sizeof(CvContour.class), CV_RETR_LIST,
				CV_CHAIN_APPROX_SIMPLE);

		ImageUtils.clearMenStorage(storage);

		List<EachCount> list = new LinkedList<EachCount>();

		if (liantonNums == 0) {
			ImageUtils.releaseImage(rawImage, grayImage, twoValue, fushi,
					pengzhang, lianton);
			return list;
		}

		while (contour != null && !contour.isNull()) {
			CvRect rect = cvBoundingRect(contour, 0);
			list.add(new EachCount(rect.x(), rect.y(), rect.width(), rect
					.height(), 0));
			contour = contour.h_next();
			ImageUtils.closeRect(rect);
		}

		ImageUtils.releaseImage(rawImage, grayImage, twoValue, fushi,
				pengzhang, lianton);
		return list;

	}

	/**
	 * 二值化
	 * 
	 * @param GrayImage
	 *            灰度图像
	 * @param twoValue
	 *            得到的二值化图像
	 * @param value
	 *            阈值 THRESH_BINARY 当前点值大于阈值时，取Maxval,也就是第四个参数
	 */
	public static void twoValue(IplImage GrayImage, IplImage twoValue,
			int value, boolean isSaveImg, String path) {
		cvThreshold(GrayImage, twoValue, value, 255, CV_THRESH_BINARY
				| CV_THRESH_OTSU);// 2值化
		if (isSaveImg) {
			saveImg(path, twoValue);
		}
	}
	
	/**
	 * 只对最后一步做的二值化
	 * 
	 * @param GrayImage
	 *            灰度图像
	 * @param twoValue
	 *            得到的二值化图像
	 * @param value
	 *            阈值 THRESH_BINARY 当前点值大于阈值时，取Maxval,也就是第四个参数
	 */
	public static void twoValueLast(IplImage GrayImage, IplImage twoValue,
			int value, boolean isSaveImg, String path) {
		cvThreshold(GrayImage, twoValue, value, 255, CV_THRESH_BINARY_INV
				| CV_THRESH_OTSU);// 2值化
		if (isSaveImg) {
			saveImg(path, twoValue);
		}
	}

	/**
	 * 腐蚀处理
	 * 
	 * @param twoValue
	 * @param fushi
	 * @param times
	 *            腐蚀次数
	 */
	public static void fushi(IplImage twoValue, IplImage fushi, int times,
			boolean isSaveImg, String path) {
		cvErode(twoValue, fushi, null, times);// 第3个参数是用于腐蚀的结构元素，若null,则使用3*3
		if (isSaveImg) {
			saveImg(path, fushi);
		}
	}

	/**
	 * 膨胀处理
	 * 
	 * @param fushi
	 * @param pengz
	 * @param times
	 * @param isSaveImg
	 * @param path
	 */
	public static void pengzhang(IplImage fushi, IplImage pengz, int times,
			boolean isSaveImg, String path) {
		cvDilate(fushi, pengz, null, times);// 第3个参数是用于腐蚀的结构元素，若null,则使用3*3

		if (isSaveImg) {
			saveImg(path, pengz);
		}
	}

	public static void saveImg(String path, IplImage img) {
		cvSaveImage(path, img);
	}

	public static boolean saveToFile(String imageUrl, String imageName) {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection conn = null;
		URL url = null;
		byte[] buf = new byte[1024];
		int size = 0;
		boolean isDownload = false;
		try {
			url = new URL(imageUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			bis = new BufferedInputStream(conn.getInputStream());
			fos = new FileOutputStream(imageName);
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.flush();
			isDownload = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (bis != null)
					bis.close();

				conn.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isDownload;
	}

}
