package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

import lazy.test.tools.util.ReflectUtil;

public class ClassHashMapAssertUtil {

    private static Logger logger = LoggerFactory.getLogger(ClassHashMapAssertUtil.class);


    /**
     * 根据属性和数据库字段的对应关系Map来比较对象和数据库查询结果
     * 
     * @param object                  需要进行比较的对象
     * @param dbResultMap             从数据库取出的要比较的结果
     * @param correspondingFieldMap   对象属性和表字段的对应关系Map
     *                                     1.只需包含需要比较字段的对应关系</br>
     *                                     2.其中key为对象的属性名,value为对应数据库字段名 </br>
     *                                     3.例如: Entry: key="userName",value="user_name" </br>      
     *                                                                  l
     * @return void
     */
    public static <T> void compareObjectWithDbMapByCorrespondingField(Object object, Map<String, T> dbResultMap, Map<String, String> correspondingFieldMap) {
        if (dbResultMap.isEmpty()) {
            Assert.assertTrue(object==null);
        }else {
            Assert.assertTrue(object!=null);
        }
        for (String key : correspondingFieldMap.keySet()) {
            logger.info("当前校验字段：" + key + "\ndbResultMap预期结果：" + dbResultMap.get(correspondingFieldMap.get(key)).toString() + "\nobject实际结果：" + ReflectUtil.getFiledByFieldName(object, key));
            String valueResult = "null";
            if (dbResultMap.get(key) != null) {
                valueResult = StringUtils.strip(dbResultMap.get(key).toString());
            }
            String valueExpect = StringUtils.strip(ReflectUtil.getFiledByFieldName(object, key).toString()) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);

        }
    }


}
