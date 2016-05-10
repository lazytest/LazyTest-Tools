/**
 * @Title: ExcelDataProviderImpl.java
 * @Description: TODO
 * @date 2016年3月15日
 */
package lazy.test.tools.provider.excel.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lazy.test.tools.provider.excel.IExcelDataProvider;
import lazy.test.tools.util.DBUtil;
import lazy.test.tools.util.DateUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExcelDataProviderImpl implements IExcelDataProvider {

	private static final Logger logger = LoggerFactory.getLogger(ExcelDataProviderImpl.class);
	
	//默认后缀
	private final static String DEFAULT_FILE_SUFFIX = ".xls";
	//最大导出数量
	private final static int MAX_EXPORT_VALUE = 10000;

	enum DBType {
		MYSQL, ORACLE, CDSSQLSERVER
	}
	private static final int LevelOne = 1; //清除级别 1:##按照指定列清除(优先级更高  如果有则按此方式)
	private static final int LevelTwo = 2; //清除界别 2:# 按照指定列作为主键一一清除
	private DataSource dataSource;

	// spring注解生成bean时候通过 <constructor-arg
	// ref="dataSource"></constructor-arg>注入数据源
	public ExcelDataProviderImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int importToMysql(String excelFolder) {
		return importExcelToDb(excelFolder, DBType.MYSQL);
	}
	@Override
	public int importToOracle(String excelFolder) {
		return importExcelToDb(excelFolder, DBType.ORACLE);
	}

	@Override
	public int importToCds(String excelFolder) {
		return importExcelToDb(excelFolder, DBType.CDSSQLSERVER);
	}
	@Override
	public int clearData(String excelFolder) {
		List<File> files = getFileList(excelFolder);
		if (CollectionUtils.isEmpty(files)) {return 0;}
		return doClearData(files);
	}
	
	private int doClearData(List<File> files) {
		HSSFWorkbook workbook = null;
		InputStream inputStream = null;
		int count = 0;
		DBUtil dbUtil = new DBUtil(dataSource);
		for (File file : files) {
			try {
				inputStream = FileUtils.openInputStream(file);
				workbook = new HSSFWorkbook(inputStream);
			} catch (IOException e) {
				logger.error("读取execl文件出错. e", e);
				continue;
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		
			//遍历sheet
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				//如果把Sheet的列放在最前面，数据就导不进去咯
				if(null == sheet || workbook.getSheetName(sheetIndex).contains("Sheet")) {break;}
				//sheet名作为表名
				String tableName = workbook.getSheetName(sheetIndex);
				//一个sheet中读取的数据
				List<String[]> sheetData = new ArrayList<String[]>();
				//生成的sql
				List<String> sqlList = null;
				//首行的长度，后面的数据长度会根据首行的长度去获取数据
				int titlRowLength = 0;
				//遍历行
				for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
					//处理一行数据
					HSSFRow row = sheet.getRow(rowIndex);
					if(null == row) continue;
					if(rowIndex==0){
//						titlRowLength = row.getPhysicalNumberOfCells();
						//表头作为字段名
						for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
							HSSFCell cell = row.getCell(i);
							if(cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK){
								titlRowLength++;
							}else{
								break;
							}
						}
					}
					//根据首行长度生成数组，否则若刚行有cell为null，则长度会与首行不一致
					String[] rowData = new String[titlRowLength];
					//取数据也只取titleRow的长度,多余的cell忽略
					for (int cellIndex = 0; cellIndex < titlRowLength; cellIndex++) {
						HSSFCell cell = row.getCell(cellIndex);
						rowData[cellIndex] = getStringCellValue(cell);
					}
					sheetData.add(rowData);
				}
				//清除数据
				ensureListNotNull(sheetData);
				sqlList = generateClearSql(sheetData, tableName);
				if(!sqlList.isEmpty() && sqlList.size()>0){
					int[] callBack = dbUtil.batchUpdate(sqlList.toArray(new String[sqlList.size()]));
					count = counter(count,callBack);//统计插入数据条数
				}else{
					logger.warn("{} sheet中未发现数据清除标志，默认不清除.",tableName);
				}
			}
		}
		return count;
	}

	private int importExcelToDb(String excelFolder, DBType dbType) {
		List<File> files = getFileList(excelFolder);
		if (CollectionUtils.isEmpty(files)) {return 0;}
		return doImport(files, dbType);
	}
	
	//获取文件信息
	private List<File> getFileList(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			if (!filePath.startsWith("/")) {
				filePath = "/" + filePath;
			}
			URL url = ExcelDataProviderImpl.class.getResource(filePath);
			if (url == null) {
				String message = String.format("获取路径失败,请检查路径.fileDir{%s}", filePath);
				logger.error(message);
//				throw new IllegalPathStateException(message);
				return null;
			}
			String path = url.getPath();
			if (path != null && path.contains("%23")) { // 如果有#需要转换url编码
				path = path.replace("%23", "#");
			}
			file = new File(path);
			if (!file.exists()) {
				String message = String.format("文件不存在,请检查路径.fileDir{%s}", filePath);
				logger.error(message);
//				throw new IllegalPathStateException(message);
				return null;
			}
		}
		String fileName = file.getName();
		List<File> files = new ArrayList<File>();
		if (file.isFile() && fileName.endsWith(".xls")) {
			files.add(file);
		} else if (file.isDirectory()) {
			File[] tempList = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().endsWith(".xls");
				}
			});
			if (tempList != null && tempList.length > 0) {
				files = Arrays.asList(tempList);
			}
		}
		return files;
	}
	
	private int doImport(List<File> files, DBType dbType) {
		HSSFWorkbook workbook = null;
		InputStream inputStream = null;
		int count = 0;
		DBUtil dbUtil = new DBUtil(dataSource);
		for (File file : files) {
			try {
				inputStream = FileUtils.openInputStream(file);
				workbook = new HSSFWorkbook(inputStream);
			} catch (IOException e) {
				logger.error("读取execl文件出错. e", e);
				continue;
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
			
			//遍历sheet
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				//如果把Sheet的列放在最前面，数据就导不进去咯
				if(null == sheet || workbook.getSheetName(sheetIndex).contains("Sheet")) {break;}
				//sheet名作为表名
				String tableName = workbook.getSheetName(sheetIndex);
				//一个sheet中读取的数据
				List<String[]> sheetData = new ArrayList<String[]>();
				//生成的sql
				List<String> sqlList = null;
				//首行的长度，后面的数据长度会根据首行的长度去获取数据
				int titlRowLength = 0;
				//遍历行
				for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
					//处理一行数据
					HSSFRow row = sheet.getRow(rowIndex);
					if(null == row) continue;
					if(rowIndex==0){
//						titlRowLength = row.getPhysicalNumberOfCells();
						//表头作为字段名
						for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
							HSSFCell cell = row.getCell(i);
							if(cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK){
								titlRowLength++;
							}else{
								break;
							}
						}
					}
					//根据首行长度生成数组，否则若刚行有cell为null，则长度会与首行不一致
					String[] rowData = new String[titlRowLength];
					//取数据也只取titleRow的长度,多余的cell忽略
					for (int cellIndex = 0; cellIndex < titlRowLength; cellIndex++) {
						HSSFCell cell = row.getCell(cellIndex);
						//第一行数据中去除表名末尾#和##
						if(rowIndex == 0){
							rowData[cellIndex] = getStringCellValue(cell).replace("#", "");
						}else{
							rowData[cellIndex] = getStringCellValue(cell);
						}
					}
					sheetData.add(rowData);
				}
				ensureListNotNull(sheetData);
				sqlList = convertLinesToSqlList(sheetData, tableName, dbType);
				if(!sqlList.isEmpty() && sqlList.size() > 0){
					int[] callBack = dbUtil.batchUpdate(sqlList.toArray(new String[sqlList.size()]));
					count = counter(count,callBack);//统计插入数据条数
				}
			}
		}
		return count;
	}
	
	private List<String[]> ensureListNotNull(List<String[]> sheetData){
		List<String[]> targetData = new ArrayList<String[]>();
		for (String[] rowData : sheetData) {
			boolean isNotNull = false;
			for (int i = 0; i < (rowData.length>3?3:rowData.length); i++) {
				if(!"null".equals(rowData[i])){
					isNotNull =  true;
					break;
				}
			}
			if(isNotNull){
				targetData.add(rowData);
			}
		}
		return targetData;
	}
	private int counter(int count,int[] callBack){
		for (int i = 0; i < callBack.length; i++) {
			count += callBack[i];
		}
		return count;
	}
	
	private String getStringCellValue(HSSFCell cell) {
		if (null == cell) { return "null"; }
		String strCell = "";
		switch (cell.getCellType()) {
    		case HSSFCell.CELL_TYPE_STRING:
    			strCell = cell.getRichStringCellValue().getString().replace("'", "\\'");//单引号转义
    			break;
    		case HSSFCell.CELL_TYPE_NUMERIC:
    			//日期处理 转成string yyyy-MM-dd HH:mm:ss
    			if(HSSFDateUtil.isCellDateFormatted(cell)){
    				strCell = DateUtil.dateYMdHmsToString(cell.getDateCellValue());
    			}else{
    				//修复int超长。。
    				strCell = doubleToString(cell.getNumericCellValue());
    			}
    			break;
    		case HSSFCell.CELL_TYPE_BOOLEAN:
    			strCell = String.valueOf(cell.getBooleanCellValue());
    			break;
    		case HSSFCell.CELL_TYPE_BLANK://空白行 默认为null
    			strCell = "null";
    			break;
    		default:
    			strCell = "";
    			break;
		}
		return strCell;
	}
	
	 private List<String> convertLinesToSqlList(List<String[]> lines, String tableName, DBType dbType) {
	        List<String> sqlList = new ArrayList<String>();
	        if (lines.isEmpty() || lines.size() == 1) {
	            logger.error("excel没有数据,请检查文件" + lines.toString());
	            return sqlList;
	        }
	        String preSql = "insert into " + tableName + "(";
	        String[] arr = lines.get(0);
	        for (int j = 0; j < arr.length; j++) {
	            String str = arr[j];
	            if (j == arr.length - 1) {
	                preSql = preSql + str + ")";
	            } else {
	                preSql = preSql + str + ",";
	            }
	        }
	        preSql = preSql + "values(";
	        for (int i = 0; i < lines.size(); i++) {
	            String sql = preSql;
	            String[] arrLine = lines.get(i);
	            if (i == 0) {
	                continue;
	            }
	            for (int j = 0; j < arrLine.length; j++) {
	                String str = arrLine[j];
	                if (j == arrLine.length - 1) {
	                	if ("null".equals(str)) {
	                		sql = sql + "null" + ")";
	                	} else if (dbType.equals(DBType.CDSSQLSERVER) && StringUtils.isNumeric(str)&&!str.startsWith("0")) {    //cds的Sql数字不能加引号
	                        sql = sql + str + ")";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d")) {
	                    	sql = sql + "TO_DATE('" + str + "','YYYY-MM-DD HH24:MI:SS')" + ")";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\.\\d+")) {
	                    	sql = sql + "TO_TIMESTAMP('" + str + "','YYYY-MM-DD HH24:MI:SS.FF')" + ")";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}/((0[1-9]|1[0-2])/(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])/(29|30)|(0[13578]|1[02])/31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)/02/29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d")) {
	                    	sql = sql + "TO_DATE('" + str + "','YYYY/MM/DD HH24:MI:SS')" + ")";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}/((0[1-9]|1[0-2])/(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])/(29|30)|(0[13578]|1[02])/31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)/02/29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\.\\d+")) {
	                    	sql = sql + "TO_TIMESTAMP('" + str + "','YYYY/MM/DD HH24:MI:SS.FF')" + ")";
	                    } else {
	                        sql = sql + "'" + str + "')";
	                    }
	                } else {
	                	if ("null".equals(str)) {
	                		sql = sql + "null" + ",";
	                	} else if (dbType.equals(DBType.CDSSQLSERVER) && StringUtils.isNumeric(str)&&!str.startsWith("0")) {
	                        sql = sql + str + ",";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d")) {
	                    	sql = sql + "TO_DATE('" + str + "','YYYY-MM-DD HH24:MI:SS')" + ",";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\.\\d+")) {
	                    	sql = sql + "TO_TIMESTAMP('" + str + "','YYYY-MM-DD HH24:MI:SS.FF')" + ",";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}/((0[1-9]|1[0-2])/(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])/(29|30)|(0[13578]|1[02])/31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)/02/29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d")) {
	                    	sql = sql + "TO_DATE('" + str + "','YYYY/MM/DD HH24:MI:SS')" + ",";
	                    } else if (dbType.equals(DBType.ORACLE) && str.matches("((?!0000)[0-9]{4}/((0[1-9]|1[0-2])/(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])/(29|30)|(0[13578]|1[02])/31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)/02/29) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\.\\d+")) {
	                    	sql = sql + "TO_TIMESTAMP('" + str + "','YYYY/MM/DD HH24:MI:SS.FF')" + ",";
	                    } else {
	                        sql = sql + "'" + str + "'" + ",";
	                    }
	                }
	            }
	            sqlList.add(sql);
	        }
	        return sqlList;
	    }
	 
	private List<String> generateClearSql(List<String[]> sheetData, String tableName) {

		List<String> sqlList = new ArrayList<String>();
		if (sheetData.isEmpty() || sheetData.size() == 1) {
			logger.error("excel没有数据,请检查文件" + sheetData.toString());
			return sqlList;
		}
		int clearLevel = 0;
		String key = null;
		int keyIndex = 0;
		String[] titieRow = sheetData.get(0);
		for (int i = 0; i < titieRow.length; i++) {
			if (StringUtils.isNotEmpty(titieRow[i]) && titieRow[i].endsWith("##")) {
				clearLevel = LevelOne;
				key = titieRow[i].replace("#", "");//清除标志
				keyIndex = i;
				break;
			} else if (StringUtils.isNotEmpty(titieRow[i]) && titieRow[i].endsWith("#")) {
				clearLevel = LevelTwo;
				key = titieRow[i].replace("#", "");//清除标志
				keyIndex = i;
			}
		}
		switch (clearLevel) {
		case LevelOne:
			String sqlLevelOne = "DELETE FROM " + tableName + " where " + key + " = '" + sheetData.get(1)[keyIndex] + "'";
			sqlList.add(sqlLevelOne);
			break;
		case LevelTwo:
			for (int i = 1; i < sheetData.size(); i++) {
				String sqlLevelTwo = "DELETE FROM " + tableName + " where " + key + " = '" + sheetData.get(i)[keyIndex] + "'";
				sqlList.add(sqlLevelTwo);
			}
			break;
		default:
			break;
		}
		return sqlList;
	}

	@Override
	public int exportChoosenDataToExcel(String tableName, String whereClause, int limit, String desDir) {
		return doExport(tableName,whereClause,limit,desDir);
	}

	@Override
	public int exportChoosenDataToExcel(String tableName, String whereClause, String desDir) {
		return doExport(tableName,whereClause,0,desDir);
	}
	
	@Override
	public int exportSingleTableToExcel(String tableName, int limit, String desDir) {
		return doExport(tableName,null,limit,desDir);
	}
	
	@Override
	public int exportSingleTableToExcel(String tableName, String desDir) {
		return doExport(tableName,null,0,desDir);
	}

	@Override
	public int exportMultipleTableToExcel(String[] tableNames, String desDir) {
		int count = 0;
		for (String tableName : tableNames) {
			count += doExport(tableName,null,0,desDir);
		}
		return count;
	}
	
	private int doExport(String tableName,String whereClause,int limit,String desDir){
		StringBuilder sb = new StringBuilder("SELECT * FROM ");
		sb.append(tableName);

		if(!StringUtils.isEmpty(whereClause)){
			sb.append(" where ");
			sb.append(whereClause);
		}
		sb.append(" limit ");
		if(limit == 0){
			sb.append(MAX_EXPORT_VALUE);
		}else{
			sb.append(limit);
		}
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String,Object>> dataList = jdbcTemplate.queryForList(sb.toString());
		if(CollectionUtils.isEmpty(dataList)){
			logger.warn("execute sql {} . query result {} .",sb.toString(),dataList);
			return 0;
		}
		writeMap2Excel(dataList,tableName,desDir);
		return dataList.size()>MAX_EXPORT_VALUE?MAX_EXPORT_VALUE:dataList.size();
	}
	/**
	 * 
	 * 将查询出来的Map写入excel
	 * @param dataList
	 * @param tableName
	 * @param desDir
	 * @return 
	 * 
	 */
	private void writeMap2Excel(List<Map<String,Object>> dataList,String tableName,String desDir){
		if(CollectionUtils.isEmpty(dataList)){return;}
		
		if(StringUtils.isEmpty(desDir)){
			throw new IllegalArgumentException(
					String.format("destination directory can not be empty . desDir {%s}", desDir));
		}
		
		//文件名 保存为.xls
		String fileName = desDir.endsWith("/") ? desDir : desDir + "/" + tableName + "_" + DateUtil.getTimeSnapshot()
				+ DEFAULT_FILE_SUFFIX;
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(tableName);
		HSSFRow titleRow = sheet.createRow(0);
		Map<String,Object> forTitle = dataList.get(0);
		
		short i = 0;
		for(String columnName:forTitle.keySet()){
			HSSFCell cell = titleRow.createCell(i);
			writeCellValue(columnName,cell);
			i++;
		}
		
		short j = 1;
		for (Map<String, Object> map : dataList) {
			short k = 0;
			HSSFRow row = sheet.createRow(j);
			for(Object data:map.values()){
				HSSFCell cell = row.createCell(k);
				writeCellValue(data,cell);
				k++;
			}
			j++;
		}
		OutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			logger.error("File not found {}",e);
		}
		try {
			workbook.write(o);
			o.flush();
			o.close();
		} catch (IOException e) {
			logger.error("Error occured when write stream to excel. {}",e);
		} finally{
			IOUtils.closeQuietly(o);
		}
	}
	
	private void writeCellValue(Object data,HSSFCell cell){
		if(null == data){
			//null则不写
		}else if(data instanceof java.sql.Date){
			//java.sql.Date转java.util.Date
			Date date = new Date( ((java.sql.Date) data).getTime());
			cell.setCellValue(new HSSFRichTextString(DateUtil.dateYMdHmsToString(date)));
		}else if(data instanceof java.util.Date){
			cell.setCellValue(new HSSFRichTextString(DateUtil.dateYMdHmsToString((Date)data)));
		}else{
			cell.setCellValue(new HSSFRichTextString(String.valueOf(data)));
		}
	}
	
	private String doubleToString(double d){
		 String i = DecimalFormat.getInstance().format(d);  
         return i.replaceAll(",", "");  
	}
}
