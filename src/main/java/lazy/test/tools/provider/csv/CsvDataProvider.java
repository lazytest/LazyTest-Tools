package lazy.test.tools.provider.csv;

import java.util.List;


/**
 * <b>工具说明：</b>提供以CSV的形式，从数据库导出到指定文件，或从指定文件导入到数据库的数据构造、清理工具 </br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;支持mysql、oracle、CDS </br>
 * <b>使用说明</b>：CSV文件名以 "表名.csv" 命名，若需要加注释可以命名为 "注释#表名.csv"</br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;第一行，对应数据库表头，第二行开始为数据 </br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;只有表头，没数据，报错； </br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;只有数据，没表头，报错； </br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public interface CsvDataProvider {


    /**
     * <b>方法说明：</b>从指定路径获取CSV存入MySql（支持CDS数据源)数据库 </br>
     * <b>使用说明：</b>> </br>
     * 1.导入的文件为csv文件</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * @param csvFolder  csv文件或者文件夹的绝对路径，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean importSingleTableToMysql(String csvFolder);

    /**
     * <b>方法说明：</b>从指定文件夹获取CSV存入MySql数据库 </br>
     * <b>使用说明：</b></br>
     * 1.导入的文件可以是csv文件，或者是存有多个csv文件的文件夹</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * 3.如果是导入单个csv文件，传入csv文件的路径即可</br>
     * 4.如果是导入多个csv文件，只需传入存放csv文件的文件夹路径即可</br>
     * @param csvFolder  csv文件或者文件夹的绝对路径，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean importMultiTableToMysql(String csvFolder);

    /**
     * <b>方法说明：</b>从指定路径获取CSV存入Oracle数据库 </br>
     * <b>使用说明：</b> </br>
     * 1.导入的文件为csv文件</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * @param csvFolder csv文件或者文件夹的绝对路径，必传 </br>
     * @param schema  oracle数据库schema，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean importSingleTableToOracle(String csvFolder, String schema);

    /**
     * <b>方法说明：</b>从指定文件夹获取CSV存入Oracle数据库 </br>
     * <b>使用说明：</b></br>
     * 1.导入的文件可以是csv文件，或者是存有多个csv文件的文件夹</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * 3.如果是导入单个csv文件，传入csv文件的路径即可</br>
     * 4.如果是导入多个csv文件，只需传入存放csv文件的文件夹路径即可</br>
     * @param csvFolder csv文件或者文件夹的绝对路径，必传 </br>
     * @param schema  oracle数据库schema，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean importMultiTableToOracle(String csvFolder, String schema);

    /**
     * <b>方法说明：</b>从指定路径获取CSV存入CDS数据库 </br>
     * <b>使用说明：</b>传入文件夹路径则导入整个文件夹的csv文件</br>
     * 1.导入的文件为csv文件或者装有多个Csv的文件夹</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * @param filePath csv文件或者文件夹的路径，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public boolean importCsvToCds(String filePath);

    /**
     * <b>方法说明：</b>从指定路径获取CSV存入sqlServer数据库 </br>
     * <b>使用说明：</b>传入文件夹路径则导入整个文件夹的csv文件</br>
     * 1.导入的文件为csv文件或者装有多个Csv的文件夹</br>
     * 2.csv命名即为"表名.csv"，也可命名为"***#表名.csv",即任意字符串加上#和目标表名.csv"</br>
     * @param filePath csv文件或者文件夹的路径，必传 </br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public boolean importCsvToSqlServer(String filePath);

//    /**
//     * <b>方法说明：</b>从数据库回滚本次插入的数据 </br>
//     * <b>使用说明：</b></br>
//     * 一个测试用例运行完后调用此方法，可以删除之前插入的数据</br>
//     * @return boolean
//     *         <ul>
//     *         <li>true 执行成功</li>
//     *         <li>false 执行失败</li>
//     *         </ul>
//     */
//    public  boolean rollBackCurrentThread();


    /**
     * <b>方法说明：</b>导出单张表数据到指定的路径下 </br>
     * @param       tableName  需要导出的表名，必传 </br>
     * @param       desDir 导出的目标文件夹，绝对路径,必传，如：D:\\</br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean exportSingleTableDataToCsv(String tableName, String desDir);

    /**
     * <b>方法说明：</b>导出多张表数据到指定的路径下 </br>
     * @param       tableNames  需要导出的表列表，必传 </br>
     * @param       desDir 导出的目标文件夹，绝对路径,必传，如：D:\\</br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean exportMultiTableDataToCsv(List<String> tableNames, String desDir);

    /**
     * <b>方法说明：</b>导出单张表数据到指定的路径下 </br>
     * @param		schema Schema
     * @param       tableName  需要导出的表名，必传 </br>
     * @param       desDir 导出的目标文件夹，绝对路径,必传，如：D:\\</br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean exportSingleTableDataToCsvForOracle(String schema, String tableName, String desDir);

    /**
     * <b>方法说明：</b>导出多张表数据到指定的路径下 </br>
     * @param		schema Schema
     * @param       tableNames  需要导出的表列表，必传 </br>
     * @param       desDir 导出的目标文件夹，绝对路径,必传，如：D:\\</br>
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public  boolean exportMultiTableDataToCsvForOracle(String schema, List<String> tableNames, String desDir);
}
