package simplecode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.alibaba.fastjson.JSON;

public class ImageMosaic {

	static {
		String imgFolder = "D:/imgFolder";
		System.out.println("--------" + imgFolder);
		File f = new File(imgFolder);
		if (!f.exists()) {
			boolean state = f.mkdir();
			if (!state) {
				System.err.println("create imgfolder error..path=" + imgFolder);
			}

		}
	}

	public void getMosaicImg() {

		String imgName = StringUtils.getImgName(5);
		String imgFolder = "D:/imgFolder/";
		try {
			String localImgPath ="E:/pic/online/idcard/sample5/sample.jpeg";// 下载到本地的图片的地址
			BaseType t = null;
			String result = "";
			t = new IdCardProcess();

			result = t.process(localImgPath, imgFolder, imgName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		new ImageMosaic().getMosaicImg();
	}
}
