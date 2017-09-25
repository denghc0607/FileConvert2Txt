package FileConvert2Txt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.juno.scheduler.util.ConvertException;
/**
 * @author denghc
 * @desc Excel转格式化的TXT
 */
public class ExcelToText implements IFileToText{
	// 创建文件输入流
	private BufferedReader reader = null;
	// 文件二进制输入流
	private InputStream is = null;
	// 当前的Sheet
	private int currSheet;
	// 当前位置
	private int currPosition;
	// Sheet数量
	private int numOfSheets;
	// HSSFWorkbook xls
	HSSFWorkbook workbook = null;
	//xlsx
	XSSFWorkbook xworkbook = null;
	
	private StringBuffer outStr=new StringBuffer();
	// 设置Cell之间以空格分割
	private static String EXCEL_LINE_DELIMITER = "\t";
	private static String NEXTLINE = "\r\n";
	// 设置最大列数
	private static int MAX_EXCEL_COLUMNS = 64;

	// 函数readLine读取文件的一行
	public void readLine(int flag) throws IOException {
		// 如果是XLS文件则通过POI提供的API读取文件
		if (flag==1){ // xls
			while (true) {
				// 根据currSheet值获得当前的sheet
				HSSFSheet sheet = workbook.getSheetAt(currSheet);
				// 判断当前行是否到但前Sheet的结尾
//				 System.out.println(sheet.getLastRowNum());
				if (currPosition > sheet.getLastRowNum()) {
					// 当前行位置清零
					currPosition = 0;
					// 判断是否还有Sheet
					if (currSheet != numOfSheets - 1) {
						currSheet++;
						// 得到下一张Sheet
						sheet = workbook.getSheetAt(currSheet);
					} else {
						return;
					}
				}
				// 获取当前行数
				int row = currPosition;
				currPosition++;
				// 读取当前行数据
				outStr.append(getLine(sheet, row));
			}
		}else if (flag==2) { //xlsx
			while (true) {
				// 根据currSheet值获得当前的sheet
				XSSFSheet sheet = xworkbook.getSheetAt(currSheet);
				// 判断当前行是否到但前Sheet的结尾
				// System.out.println(sheet.getLastRowNum());
				if (currPosition > sheet.getLastRowNum()) {
					// 当前行位置清零
					currPosition = 0;
					// 判断是否还有Sheet
					if (currSheet != numOfSheets - 1) {
						currSheet++;
						// 得到下一张Sheet
						sheet = xworkbook.getSheetAt(currSheet);
					} else {
						return;
					}
				}
				// 获取当前行数
				int row = currPosition;
				currPosition++;
				// 读取当前行数据
				outStr.append(getXLine(sheet, row));
			}
		}
	}

	// 函数getLine返回Sheet的一行数据

	private String getLine(HSSFSheet sheet, int row) {

		// 根据行数取得Sheet的一行

		HSSFRow rowline = sheet.getRow(row);

		// 创建字符创缓冲区

		StringBuffer buffer = new StringBuffer();

		// 获取当前行的列数
		int filledColumns;
		try {
			filledColumns = rowline.getLastCellNum();
		} catch (NullPointerException e) {
			return "";
		}
		HSSFCell cell = null;
		// 循环遍历所有列
		for (int i = 0; i < filledColumns; i++) {
			// 取得当前Cell
			cell = rowline.getCell((short) i);
			String cellvalue = null;
			if (cell != null) {
				// 判断当前Cell的Type
				switch (cell.getCellType()) {
				// 如果当前Cell的Type为NUMERIC
				case HSSFCell.CELL_TYPE_NUMERIC: {
					// 判断当前的cell是否为Date
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// 如果是Date类型则，取得该Cell的Date值
						Date date = cell.getDateCellValue();
						// 把Date转换成本地格式的字符串
						cellvalue = cell.getDateCellValue().toString();
					}
					// 如果是纯数字
					else {
						// 取得当前Cell的数值
//						Integer num = new Integer((int) cell.getNumericCellValue());
//						cellvalue = String.valueOf(num);
						cellvalue = String.valueOf(cell.getNumericCellValue());
					}
					break;
				}
				// 如果当前Cell的Type为STRIN
				case HSSFCell.CELL_TYPE_STRING:
					// 取得当前的Cell字符串
					cellvalue = cell.getRichStringCellValue().toString().replaceAll("'", "''");
					break;
				// 默认的Cell值
				default:
					cellvalue = "\t";
				}
			} else {
				cellvalue = "";
			}
			// 在每个字段之间插入分割符
			buffer.append(cellvalue).append(EXCEL_LINE_DELIMITER);
		}
		// 以字符串返回该行的数据
		return buffer.append(NEXTLINE).toString();
	}
	
	private String getXLine(XSSFSheet sheet, int row) {

		// 根据行数取得Sheet的一行

		XSSFRow rowline = sheet.getRow(row);

		// 创建字符创缓冲区

		StringBuffer buffer = new StringBuffer();

		// 获取当前行的列数
		int filledColumns;
		try {
			filledColumns = rowline.getLastCellNum();
		} catch (NullPointerException e) {
			return "";
		}
		XSSFCell cell = null;
		// 循环遍历所有列
		for (int i = 0; i < filledColumns; i++) {
			// 取得当前Cell
			cell = rowline.getCell((short) i);
			String cellvalue = null;
			if (cell != null) {
				// 判断当前Cell的Type
				switch (cell.getCellType()) {
				// 如果当前Cell的Type为NUMERIC
				case XSSFCell.CELL_TYPE_NUMERIC: {
					// 判断当前的cell是否为Date
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// 如果是Date类型则，取得该Cell的Date值
						Date date = cell.getDateCellValue();
						// 把Date转换成本地格式的字符串
						cellvalue = cell.getDateCellValue().toString();
					}
					// 如果是纯数字
					else {
						// 取得当前Cell的数值
//						Integer num = new Integer((int) cell.getNumericCellValue());
//						cellvalue = String.valueOf(num);
						cellvalue = String.valueOf(cell.getNumericCellValue());
					}
					break;
				}
				// 如果当前Cell的Type为STRIN
				case XSSFCell.CELL_TYPE_STRING:
					// 取得当前的Cell字符串
					cellvalue = cell.getRichStringCellValue().toString().replaceAll("'", "''");
					break;
				// 默认的Cell值
				default:
					cellvalue = "\t";
				}
			} else {
				cellvalue = "";
			}
			// 在每个字段之间插入分割符
			buffer.append(cellvalue).append(EXCEL_LINE_DELIMITER);
		}
		// 以字符串返回该行的数据
		return buffer.append(NEXTLINE).toString();
	}

	// close函数执行流的关闭操作
	public void close() {
		// 如果is不为空，则关闭InputSteam文件输入流
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				is = null;
			}
		}
		// 如果reader不为空则关闭BufferedReader文件输入流
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				reader = null;
			}
		}
	}

	@Override
	public String fileToText(byte[] byts, int flag) throws ConvertException{
		if (byts == null || byts.length <= 0) {
			return null;
		}
		// 设置开始行为0
		currPosition = 0;
		// 设置当前位置为0
		currSheet = 0;
		// 创建文件输入流
		is = new ByteArrayInputStream(byts);
		String contentTxt=null;
		try {
			switch (flag) {
			case 1://xls
				// 如果是Excel文件则创建HSSFWorkbook读取
				workbook = new HSSFWorkbook(is);
				// 设置Sheet数
				numOfSheets = workbook.getNumberOfSheets();
				readLine(flag);
				break;
			case 2://xlsx
				xworkbook = new XSSFWorkbook(is);
				numOfSheets = xworkbook.getNumberOfSheets();
				readLine(flag);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw new ConvertException("File Type Not Supported");
		}
		return outStr==null?null:outStr.toString();
	}

}