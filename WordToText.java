/**
 * 
 */
package FileConvert2Txt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.juno.scheduler.util.ConvertException;

/**
 * @author denghc
 * @desc word转换格式化文本
 */
public class WordToText implements IFileToText {

	@Override
	public String fileToText(byte[] byts,int flag)throws ConvertException{
		if (byts == null || byts.length <= 0) {
			return null;
		}
		InputStream is = new ByteArrayInputStream(byts);
		String content = null;
		try {
			switch (flag) {
			case 2:// word 2007
					// OPCPackage opcPackage =
					// POIXMLDocument.openPackage("2007.docx");
				OPCPackage opcPackage = OPCPackage.open(is);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
				content = extractor.getText();
				break;
			default:// word 2003 
				WordExtractor we_d = new WordExtractor(is);
				content = we_d.getText();
			}
		} catch (Exception e) {
			throw new ConvertException("抽取word格式文本错误！");
		}
		// 排版处理
		if (content == null || content.length() <= 0) {
			return null;
		}
		String spilter = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		String[] strelines = content.split(spilter);
		if (strelines.length <= 1) {
			spilter = "\n";
			strelines = content.split(spilter);
		}
		for (int i = 0; i < strelines.length; i++) {
			String temp = strelines[i].trim().replaceAll("[ ]{1,}", " ");
			if (NumberUtils.isNumber(temp) || temp.length() == 0) {
				continue;
			}
			if ("PAGE".equals(temp)) {
				continue;
			}
			if (sb.length() == 0) {
				sb.setLength(0);
				temp = "    " + temp;
			}
			sb.append(temp + spilter + "    ");
		}
		return sb.toString();
	}

}
