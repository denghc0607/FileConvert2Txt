/**
 * 
 */
package FileConvert2Txt;

import org.juno.scheduler.util.ConvertException;

public interface IFileToText {
	public String fileToText(byte[] byts,int flag)throws ConvertException;
}
