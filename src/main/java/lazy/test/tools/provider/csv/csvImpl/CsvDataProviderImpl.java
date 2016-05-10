package lazy.test.tools.provider.csv.csvImpl;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lazy.test.tools.provider.csv.CsvDataProvider;
import lazy.test.tools.util.DBUtil;
import lazy.test.tools.util.FileCharsetConverterUtil;


/**
 * Date: 15-11-19
 * Time: 上午10:41
 */
public class CsvDataProviderImpl implements CsvDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataProviderImpl.class);

    protected DataSourceDatabaseTester dataSourceDatabaseTester;

    private DataSource dataSource;

    private final String UTF_8 = "UTF-8";

    private final String GBK = "GBK";
    
    private enum DBType { ORACLE, MYSQL, CDSSQLSERVER };

    public CsvDataProviderImpl(DataSource dataSource) {
        dataSourceDatabaseTester = new DataSourceDatabaseTester(dataSource);
        dataSourceDatabaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        this.dataSource = dataSource;
    }

    @Override
    public boolean importSingleTableToMysql(String csvFolder) {
        return importCsvToDb(csvFolder, DBType.MYSQL);
    }

    @Override
    public boolean importMultiTableToMysql(String csvFolder) {
        return importCsvToDb(csvFolder, DBType.MYSQL);
    }

    @Override
    public boolean importSingleTableToOracle(String csvFolder, String schema) {
        dataSourceDatabaseTester.setSchema(schema);
        return importCsvToDb(csvFolder, DBType.ORACLE);
    }

    @Override
    public boolean importMultiTableToOracle(String csvFolder, String schema) {
        dataSourceDatabaseTester.setSchema(schema);
        return importCsvToDb(csvFolder, DBType.ORACLE);
    }

//    @Override
//    public boolean rollBackCurrentThread() {
//        dataSourceDatabaseTester.setTearDownOperation(DatabaseOperation.DELETE);
//        try {
//            dataSourceDatabaseTester.onTearDown();
//        } catch (Exception e) {
//            logger.error("回滚数据失败 e", e);
//            return false;
//        }
//        return true;
//    }

    @Override
    public boolean importCsvToSqlServer(String filePath) {
        return importCsvToCds(filePath);
    }

    @Override
    public boolean exportSingleTableDataToCsv(String tableName, String desDir) {
        QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(dataSourceDatabaseTester.getConnection());
        } catch (Exception e) {
            logger.error("导出CSV时,获取数据库连接失败.e", e);
            return false;
        }
        try {
            dataSet.addTable(tableName);
            File file = new File(desDir);
            CsvDataSetWriter.write(dataSet, file);
        } catch (Exception e) {
            logger.error("导出CSV时,写入数据失败. e", e);
            return false;
        }
        deleteTableTxt(desDir);
        List<String> list = new ArrayList<String>();
        list.add(tableName);
        convertCsvDirCharSet(desDir, UTF_8, GBK, list);
        return true;
    }

    @Override
    public boolean exportMultiTableDataToCsv(List<String> tableNames, String desDir) {
        QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(dataSourceDatabaseTester.getConnection());
        } catch (Exception e) {
            logger.error("导出CSV时,获取数据库连接失败.e", e);
            return false;
        }
        try {
            for (String tableName : tableNames) {
                dataSet.addTable(tableName);
            }
            File file = new File(desDir);
            CsvDataSetWriter.write(dataSet, file);
        } catch (Exception e) {
            logger.error("导出CSV时,写入数据失败. e", e);
            return false;
        }
        deleteTableTxt(desDir);
        convertCsvDirCharSet(desDir, UTF_8, GBK, tableNames);
        return true;
    }

    @Override
    public boolean importCsvToCds(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists()) {
            if (!fileDir.startsWith("/")) {
                fileDir = "/" + fileDir;
            }
            URL url = CsvDataProviderImpl.class.getResource(fileDir);
            if (url == null) {
                logger.error("获取路径失败,请检查路径.fileDir{}", fileDir);
                return false;
            }
            String path = url.getPath();
            if (path != null && path.contains("%23")) { //如果有#需要转换url编码
                path = path.replace("%23", "#");
            }
            file = new File(path);
            if (!file.exists()) {
                logger.error("文件不存在,请检查路径.fileDir{}", fileDir);
                return false;
            }
        }
        String fileName = file.getName();
        List<File> files = new ArrayList<File>();
        if (file.isFile() && fileName.endsWith(".csv")) {
            files.add(file);
        } else if (file.isDirectory()) {
            File[] tempList = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".csv");
                }
            });
            if (tempList != null && tempList.length > 0) {
                files = Arrays.asList(tempList);
            }
        }
        return doCdsImport(files);
    }

    private boolean importCsvToDb(String filePath, DBType dbType) {
    	File file = new File(filePath);
        if (!file.exists()) {
            if (!filePath.startsWith("/")) {
            	filePath = "/" + filePath;
            }
            URL url = CsvDataProviderImpl.class.getResource(filePath);
            if (url == null) {
                logger.error("获取路径失败,请检查路径.fileDir{}", filePath);
                return false;
            }
            String path = url.getPath();
            if (path != null && path.contains("%23")) { //如果有#需要转换url编码
                path = path.replace("%23", "#");
            }
            file = new File(path);
            if (!file.exists()) {
                logger.error("文件不存在,请检查路径.fileDir{}", filePath);
                return false;
            }
        }
        String fileName = file.getName();
        List<File> files = new ArrayList<File>();
        if (file.isFile() && fileName.endsWith(".csv")) {
            files.add(file);
        } else if (file.isDirectory()) {
            File[] tempList = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".csv");
                }
            });
            if (tempList != null && tempList.length > 0) {
                files = Arrays.asList(tempList);
            }
        }
        return doImport(files, dbType);

    }

    private boolean doImport(List<File> files, DBType dbType) {
    	DBUtil dbUtil = new DBUtil(dataSource);
        for (File file : files) {
            String tableName = file.getName().substring(0, file.getName().indexOf(".csv"));
            if (tableName.contains("#")) {   //支持表名前加"#"功能
                tableName = tableName.substring(tableName.lastIndexOf("#") + 1, tableName.length());
            }
            List<String[]> lines = null;
            InputStreamReader isr = null;
            CSVReader reader = null;
            List<String> sqlList = null;
            try {
                isr = new InputStreamReader(FileUtils.openInputStream(file), GBK);
                reader = new CSVReader(isr);
                lines = reader.readAll();
                sqlList = convertLinesToSqlList(lines, tableName, dbType);
            } catch (Exception e) {
                logger.error("读取Csv文件出错. e", e);
                return false;
            } finally {
                IOUtils.closeQuietly(isr);
                IOUtils.closeQuietly(reader);
            }
            if (sqlList.isEmpty()) {
                return false;
            }
            String[] sqlLis = sqlList.toArray(new String[sqlList.size()]);
            dbUtil.batchUpdate(sqlLis);
        }
        return true;
    }

    private boolean doCdsImport(List<File> files) {
        DBUtil dbUtil = new DBUtil(dataSource);
        for (File file : files) {
            String tableName = file.getName().substring(0, file.getName().indexOf(".csv"));
            if (tableName.contains("#")) {   //支持表名前加"#"功能
                tableName = tableName.substring(tableName.lastIndexOf("#") + 1, tableName.length());
            }
            List<String[]> lines = null;
            InputStreamReader isr = null;
            CSVReader reader = null;
            List<String> sqlList = null;
            try {
                isr = new InputStreamReader(FileUtils.openInputStream(file), GBK);
                reader = new CSVReader(isr);
                lines = reader.readAll();
                sqlList = convertLinesToSqlList(lines, tableName, DBType.CDSSQLSERVER);
            } catch (Exception e) {
                logger.error("读取Csv文件出错. e", e);
                return false;
            } finally {
                IOUtils.closeQuietly(isr);
                IOUtils.closeQuietly(reader);
            }
            if (sqlList.isEmpty()) {
                return false;
            }
            String[] sqlLis = sqlList.toArray(new String[sqlList.size()]);
            dbUtil.batchUpdate(sqlLis);
        }
        return true;
    }

    //由于不需要表名的列表,故删除
    private void deleteTableTxt(String desDir) {
        File file = new File(desDir + "/table-ordering.txt");
        if (!file.exists()) {
            URL url = CsvDataProviderImpl.class.getResource(desDir);
            if (url == null) {
                logger.error("获取路径失败,请检查路径.fileDir{}", desDir);
                return;
            }
            String path = url.getPath() + "/table-ordering.txt";
            file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    private void convertCsvDirCharSet(String desDir, String fromCharSet, String toCharSet, List<String> tableNames) {
        File file = new File(desDir);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] tempList = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith(".csv");
                }
            });
            if (tempList != null) {
                for (File file1 : tempList) {
                    if (tableNames == null || tableNames.isEmpty()) {
                            doConvert(file1, fromCharSet, toCharSet);
                    } else {
                        for (String sr : tableNames) {
                            if (file1.getName().substring(0, file1.getName().indexOf(".csv")).equals(sr)) {
                                doConvert(file1, fromCharSet, toCharSet);
                            }
                        }
                    }
                }
            }
        } else {
            doConvert(file, fromCharSet, toCharSet);
        }

    }


    private void doConvert(File file, String fromCharSet, String toCharSet) {
        try {
            FileCharsetConverterUtil.convert(file, fromCharSet, toCharSet, new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".csv");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(file + "导出完成,在转换编码格式时出错. e", e);
        }
    }


    private List<String> convertLinesToSqlList(List<String[]> lines, String tableName, DBType dbType) {
        List<String> sqlList = new ArrayList<String>();
        if (lines.isEmpty() || lines.size() == 1) {
            logger.error("csv没有数据,请检查文件" + lines.toString());
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
                	if (str.equals("null")) {
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
                	if (str.equals("null")) {
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

	@Override
	public boolean exportSingleTableDataToCsvForOracle(String schema,
			String tableName, String desDir) {
		dataSourceDatabaseTester.setSchema(schema);
		
		QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(dataSourceDatabaseTester.getConnection());
        } catch (Exception e) {
            logger.error("导出CSV时,获取数据库连接失败.e", e);
            return false;
        }
        try {
            dataSet.addTable(tableName);
            File file = new File(desDir);
            CsvDataSetWriter.write(dataSet, file);
        } catch (Exception e) {
            logger.error("导出CSV时,写入数据失败. e", e);
            return false;
        }
        deleteTableTxt(desDir);
        List<String> list = new ArrayList<String>();
        list.add(tableName);
        convertCsvDirCharSet(desDir, UTF_8, GBK, list);
        return true;
	}

	@Override
	public boolean exportMultiTableDataToCsvForOracle(String schema,
			List<String> tableNames, String desDir) {
		dataSourceDatabaseTester.setSchema(schema);
		
		QueryDataSet dataSet = null;
        try {
            dataSet = new QueryDataSet(dataSourceDatabaseTester.getConnection());
        } catch (Exception e) {
            logger.error("导出CSV时,获取数据库连接失败.e", e);
            return false;
        }
        try {
            for (String tableName : tableNames) {
                dataSet.addTable(tableName);
            }
            File file = new File(desDir);
            CsvDataSetWriter.write(dataSet, file);
        } catch (Exception e) {
            logger.error("导出CSV时,写入数据失败. e", e);
            return false;
        }
        deleteTableTxt(desDir);
        convertCsvDirCharSet(desDir, UTF_8, GBK, tableNames);
        return true;
	}

//    private void fileChannelCopy(File s, File t) {
//        FileInputStream fi = null;
//        FileOutputStream fo = null;
//        FileChannel in = null;
//        FileChannel out = null;
//        try {
//            fi = new FileInputStream(s);
//            fo = new FileOutputStream(t);
//            in = fi.getChannel();//得到对应的文件通道
//            out = fo.getChannel();//得到对应的文件通道
//            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(fi);
//            IOUtils.closeQuietly(fo);
//            IOUtils.closeQuietly(in);
//            IOUtils.closeQuietly(out);
//        }
//    }

//    public static void main(String[] args) {
//        File file = new File("D:\\cd_auto\\AutoTestUtil\\src\\main\\resources\\data\\ACCOUNT_SERVICEpdc_account_request.csv");
//        File file1 = null;
//        try {
//            file1 = File.createTempFile("tempTest", ".csv", new File("D:\\"));
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        fileChannelCopy(file, file1);
//        file1.delete();
//    }
}
