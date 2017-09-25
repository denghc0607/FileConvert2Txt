package FileConvert2Txt;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * @author denghc
 * @desc txt文件转换格式化文本
 */
public class TxtToTxt {
	
	public static void main(String[] args) {
		try {
			byte[] content=IOUtils.toByteArray(new FileInputStream(new File("c:/test.txt")));
			String str=getTextFromByte(content);
			System.err.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getTextFromByte(byte[] fileContent) throws Exception {
		String content = null;
		try {
			content = new String(fileContent, Charset.forName("GBK"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("文件内容抽取失败！");
		} 
		if (content == null || content.length() <= 0) {
			return null;
		}
		// 格式排版
		StringBuffer sb = new StringBuffer();
		content = content.replaceAll("[ ]{1,}", " ");
		content = content.replaceAll("[\\?]{1,}", "");
		String spliter = System.getProperty("line.separator");
		String[] lines = content.split(spliter);
		if (lines.length <= 1) {
			spliter = "\n";
			lines = content.split(spliter);
		}
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() == 0 || isPageNumber(line)) {
				continue;
			}
			line = "    " + line + spliter;
			sb.append(line);
		}

		return sb.toString();
	}
	
	private static boolean isPageNumber(String line) {
		boolean flag = false;
		line = line.replaceAll("[\\s]+", "");
		if (line.length() < 11) {
			if (!flag) {
				Pattern p2 = Pattern.compile("-[0-9\\s]{1,}-");
				Matcher m2 = p2.matcher(line);
				flag = m2.matches();
			}
			if (!flag) {
				Pattern p3 = Pattern.compile("第[\\s0-9]{1,}页");
				Matcher m3 = p3.matcher(line);
				flag = m3.matches();
			}
			if (!flag) {
				Pattern p4 = Pattern.compile("[0-9\\s]{1,}-[0-9\\s]{1,}");
				Matcher m4 = p4.matcher(line);
				flag = m4.matches();
			}
			if(!flag){
				Pattern p4 = Pattern.compile("[0-9\\s]{1,}/[0-9\\s]{1,}");
				Matcher m4 = p4.matcher(line);
				flag = m4.matches();
			}
		}

		return flag;
	}
}
