package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

import lazy.test.tools.util.JsonUtil;
import lazy.test.tools.util.ReflectUtil;

/**
 * <b>Date: 15-12-14</b></br>
 * <b>Time: 下午2:37</b></br>
 */
public class ClassAssertUtil {

    private static Logger logger = LoggerFactory.getLogger(ClassAssertUtil.class);

    /**
     * 根据需要比较的字段来比较两个类中的属性
     * 
     * @param object             需要进行比较的对象
     * @param object2            需要进行比较的对象2
     * @param needAssertFields   需要校验的数据库字段list    
     *                            l
     * @return void
     */
    public static void compareObjectByCorrespondingField(Object object, Object object2,String[] needAssertFields) {
        Map<String, String> classMap = JsonUtil.json2HashMap(JsonUtil.object2JSONNoFeatures(object));
        Map<String, String> classMap2 = JsonUtil.json2HashMap(JsonUtil.object2JSONNoFeatures(object2));
        if (needAssertFields.length > 0) {
            for (String needAssertField : needAssertFields) {
                needAssertField = needAssertField.trim();
                Assert.assertTrue(classMap.containsKey(needAssertField), "要校验的结果中不包含需要校验的列：" + needAssertField);
                logger.info("当前校验字段：" + needAssertField + "\nobject结果：" + classMap.get(needAssertField) + "\nobject2结果：" + classMap2.get(needAssertField));
                String valueResult = "null";
                if (classMap.get(needAssertField) != null) {
                    valueResult = StringUtils.strip(classMap.get(needAssertField));
                }
                String valueExpect = StringUtils.strip(classMap2.get(needAssertField)) ;
                if (StringUtils.equals("\"\"", valueExpect)) {
                    valueExpect = "";
                }
                Assert.assertEquals(valueResult, valueExpect);
            }
        } else {
            logger.error("需要校验的数据库字段needAssertFields为空,结束比较 needAssertFields ：" + needAssertFields);
        }
    }


}
