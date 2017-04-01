package simplecode;

/**
 *每一个区域
 */
public class EachCount {

	int x;
	int y;
	double width;
	double height;
	int count;
	
	public EachCount(){}
	public EachCount(int x,int y,double width,double height,int count){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.count=count;
	}
	
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EachCount3 [x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append("]");
		return builder.toString();
	}
}
