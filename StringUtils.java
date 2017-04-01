package simplecode;

import java.util.Random;

import org.joda.time.DateTime;

public class StringUtils {

	public static boolean isNull(String string){
		if(string ==null ||string.trim().length()==0)
			return true;
		return false;
	}
	
	public static boolean isNotNull(String string){
		return !isNull(string);
	}
	
	public static String getRandom(int num){
		String all ="qwertyuioplkjhgfdsazxcvbnm";
		StringBuilder sb =new StringBuilder("");
		for(int i=0;i<num;i++){
			int loca =new Random().nextInt(26);
			sb.append(all.charAt(loca));
		}
		return sb.toString();
	}
	
	public static String getTime(){
		DateTime time =new DateTime();
		return time.toString("yyyyMMdd-HH-mm-ss-SSS");
	}
	
	public static String getImgName(int num){
		return getTime()+getRandom(num);
	}
}
