/**
* @Title: IExcelDataProvider.java
* @Description: excel导入数据库
* @date 2016年3月15日
*/
package lazy.test.tools.provider.excel;




/**
 * 
 * <b>工具用法</b>:提供以excel(支持:97-2003[.xls]导入数据库或导出(支持整表导出和限定查询条件导出)数据库数据到excel </br>
 * <b>使用说明</b>:1. 支持单文件多表导入：excel表中sheet名对应表名，sheet中数据为需要导入的数据</br>
 * &emsp;&emsp;&emsp;&emsp;2. 支持数据回滚删除(按标志位生成删除语句)</br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;2.1 表头中带<code>##</code><按照此列进行唯一标志删除[此列数据需一致]:e.g:表头 CREATED##)</br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;2.2 表头中带<code>#</code>(将此列数据作为主键删除[生成多条sql]:e.g:表头 ID#)</br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;第一行，对应数据库表列名，第二行开始为数据，cell值若为空则插入null,若要插入空串需填入<code>"'"</code> </br>
 * 
 */
public interface IExcelDataProvider {
	
	 /**
     * <b>方法说明：</b>从指定路径获取excel存入MySql（支持CDS数据源)数据库 </br>
     * <b>使用说明：</b> </br>
     * 1.导入的文件为excel文件(支持.xls/.xlsx)</br>
     * 2.excel中的sheet名为表名</br>
     * @param excelFolder  excel 文件或者文件夹的绝对路径，必传 </br>
     * @return int
     *         插入的数据条数
     */
	public int importToMysql(String excelFolder);
	
	 /**
     * <b>方法说明：</b>从指定路径获取excel存入oracle（支持CDS数据源)数据库 </br>
     * <b>使用说明：</b> </br>
     * 1.导入的文件为excel文件(支持.xls/.xlsx)</br>
     * 2.excel中的sheet名为表名</br>
     * @param excelFolder  excel 文件或者文件夹的绝对路径，必传 </br>
     * @return int
     *         插入的数据条数
     */
	public int importToOracle(String excelFolder);
	
	 /**
     * <b>方法说明：</b>从指定路径获取excel存入cds（支持CDS数据源)数据库 </br>
     * <b>使用说明：</b> </br>
     * 1.导入的文件为excel文件(支持.xls/.xlsx)</br>
     * 2.excel中的sheet名为表名</br>
     * @param excelFolder  excel 文件或者文件夹的绝对路径，必传 </br>
     * @return int
     *         插入的数据条数
     */
	public int importToCds(String excelFolder);
	
	 /**
     * <b>方法说明：</b>清除excel表格中倒入的数据 </br>
     * <b>使用说明：</b> </br>
     * 				无
     * 1.按唯一列删除 列名末尾加##(CREATED##)</br>
     * 2.按指定列主键删除  列名末尾加#(ID#)</br>
	 * @param excelFolder
	 * @return int
	 *         删除的数据
	 */
	public int clearData(String excelFolder);
	
	 /**
     * <b>方法说明：</b> 按条件查询和数量限定导出数据库到指定目录 - 支持mysql</br>
     * <b>使用说明：</b> </br>
     * 				无
	 * @param tableName 数据库表名</br>
	 * @param whereClause where限定语句 (e.g: CREATED = 'zhaoyuebing' AND) </br>
	 * @param limit 限制导出的数据条数 </br>
	 * @param desDir 目标文件保存绝对路径 D:\\ </br>
	 * @return 导出的数据条数
	 */
	public int exportChoosenDataToExcel(String tableName,String whereClause,int limit,String desDir);
	
	 /**
     * <b>方法说明：</b> 按条件查询导出数据库到指定目录</br>
     * <b>使用说明：</b> </br>
     * 				无
	 * @param tableName 数据库表名</br>
	 * @param whereClause where限定语句 (e.g: CREATED = 'zhaoyuebing' AND) </br>
	 * @param desDir 目标文件保存绝对路径 D:\\ </br>
	 * @return 导出的数据条数
	 */
	public int exportChoosenDataToExcel(String tableName,String whereClause,String desDir);
	
	 /**
     * <b>方法说明：</b> 按数量限定导出数据库到指定目录 - 支持mysql</br>
     * <b>使用说明：</b> </br>
     * 				无
	 * @param tableName 数据库表名</br>
	 * @param limit 限制导出的数据条数 </br>
	 * @param desDir 目标文件保存绝对路径 D:\\ </br>
	 * @return 导出的数据条数
	 */
	public int exportSingleTableToExcel(String tableName,int limit,String desDir);
	
	 /**
     * <b>方法说明：</b> 导出数据库到指定目录</br>
     * <b>使用说明：</b> </br>
     * 				无
	 * @param tableName 数据库表名</br>
	 * @param desDir 目标文件保存绝对路径 D:\\ </br>
	 * @return 导出的数据条数
	 */
	public int exportSingleTableToExcel(String tableName,String desDir);
	
	 /**
     * <b>方法说明：</b> 导出多表数据到指定目录(存放在多个excel文件中)</br>
     * <b>使用说明：</b> </br>
     * 				无
	 * @param tableNames 数据库表名列表</br>
	 * @param desDir 目标文件保存绝对路径 D:\\ </br>
	 * @return 导出的数据条数
	 */
	public int exportMultipleTableToExcel(String[] tableNames,String desDir);

	
}
