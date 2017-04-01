package simplecode;

import org.bytedeco.javacpp.opencv_core.IplImage;

public interface BaseType {
	/**处理
	 * @param src
	 * @param public_path
	 * @param selaparate
	 * @return
	 */
	String process(String src,String public_path,String selaparate);
	/**得到每一个可能需要打码的区域
	 * @param index
	 * @param public_path
	 * @param greyImgImage
	 * @param location_x
	 * @param location_y
	 * @param maxWidth
	 * @param maxhight
	 * @return
	 */
	boolean getMosaicQuyu(int index,String public_path,IplImage greyImgImage ,int location_x,int location_y,double maxWidth,double maxhight );
}
