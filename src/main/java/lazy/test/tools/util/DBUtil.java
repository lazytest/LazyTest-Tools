package lazy.test.tools.util;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * <b>工具说明：</b>提供以SQL，或sql+参数的方式执行数据库操作的工具 </br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;支持mysql、oracle、CDS </br>
 * <b>使用说明</b>：不同的数据库使用对应的数据源。
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class DBUtil {


    private JdbcTemplate jdbcTemplate;


    public DBUtil(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * <b>方法说明：</b>update方法用于执行新增、修改、删除等语句</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     *              例如:insert into auto_test values(?,?,?)</br>
     * @param list  参数，必传 </br>
     * 
     * return Integer 影响的记录数
     */
    public Integer update(String sql,List list) {
        return jdbcTemplate.update(sql, list.toArray());
    }
    /**
     * <b>方法说明：</b>方法用于执行删除语句</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     * @param list  参数，必传 </br>
     * 
     * return Integer 影响的记录数
     */
    public Integer delete(String sql,List list) {
        return update(sql, list);
    }
    /**
     * <b>方法说明：</b>方法用于执行新增语句</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     * @param list  参数，必传 </br>
     * 
     * return Integer 影响的记录数
     */
    public Integer insert(String sql,List list) {
        return update(sql, list);
    }

    /**
     * <b>方法说明：</b>批量执行更新语句</br>
     * 
     * @param sql  数据库执行语句组，必传 </br>
     *              例如:String[] params = {"insert into auto_test values(3,33,333)",
     *                                     "insert into auto_test values(4,44,444)"};</br>
     * 
     * return Integer 影响的记录数
     */
    public int[] batchUpdate(String[] sql) {
        return   jdbcTemplate.batchUpdate(sql);
    }

    /**
     * <b>方法说明：</b>查询单条记录(有且仅有一条满足条件的记录)</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     *              例如:select *  from auto_test  where id =?</br>
     * @param list  参数，必传 </br>
     * 
     * return Map<String, Object> 查询结果
     */
    public Map<String, Object> queryMapByParams(String sql,List list) {
        return   jdbcTemplate.queryForMap(sql, list.toArray());
    }

    /**
     * <b>方法说明：</b>查询多条数据</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     *              例如:select *  from auto_test  where name =?</br>
     * @param list  参数，必传 </br>
     * 
     * return List<Map<String, Object>> 查询结果
     */
    public List<Map<String, Object>> queryListByParams(String sql,List list) {
        return   jdbcTemplate.queryForList(sql, list.toArray());
    }

    /**
     * <b>方法说明：</b>查询符合条件的数据条数</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     *              例如:select count(*)  from auto_test  where name =?</br>
     * @param list  参数，必传 </br>
     * 
     * return int  符合条件的数据条数
     */
    public int count(String sql,List list) {
        return   jdbcTemplate.queryForInt(sql, list.toArray());
    }
    /**
     * <b>方法说明：</b>可以用于执行任何SQL语句</br>
     * 
     * @param sql  数据库执行语句，必传 </br>
     */
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

}
