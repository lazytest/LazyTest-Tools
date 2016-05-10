package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

import lazy.test.tools.util.JsonUtil;

/**
 * 相同格式的Jason和Jason的嵌套比较，准确报出哪层的哪个数据的不相同；
 b)     不同格式的Jason和Jason的比较，需要提供Key.key到Key.key的对应关系，可指定要比较的字段；
 从class直接转换的Jason和Jason的嵌套比较，准确报出哪层的哪个数据的不相同；
 b)     不同格式的Jason和class的比较，需要提供Key.key到Key.key的对应关系，可指定要比较的字段；
 */
public class JsonAssertUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonAssertUtil.class);
    /**
     * 相同格式的Jason和Jason的比较
     * 
     * @param  resultJson 		需要比较的json
     * @param  expectJson 		期望的json
     * 
     * @return void
     */
    public static void compareSameJson(String resultJson, String expectJson) {
        Map<String,String> resultMap = JsonUtil.json2HashMap(resultJson);
        Map<String,String> expectMap = JsonUtil.json2HashMap(expectJson);
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        for (String key : expectMap.keySet()) {
                logger.info("当前校验字段：" + key + "\nexpectJson预期结果：" + expectMap.get(key) + "\nresultJson实际结果：" + resultMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key));
            }
            String valueExpect = StringUtils.strip(expectMap.get(key)) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }
    /**
     * 不同格式的Jason和Jason的比较
     * 
     * @param  resultJson 		需要比较的json
     * @param  expectJson 		期望的json
     * @param  needAssertFields 		需要比较的字段
     * 
     * @return void
     */
    public static <T> void compareDifJson(String resultJson, String expectJson,String[] needAssertFields) {
        Map<String,T> resultMap = JsonUtil.json2HashMap(resultJson);
        Map<String,T> expectMap = JsonUtil.json2HashMap(expectJson);
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        if (needAssertFields.length > 0) {
            for (String needAssertField : needAssertFields) {
                needAssertField = needAssertField.trim();
                Assert.assertTrue(resultMap.containsKey(needAssertField), "resultJson中不包含需要校验的列：" + needAssertField);
                Assert.assertTrue(expectMap.containsKey(needAssertField), "expectJson中不包含需要校验的列：" + needAssertField);
                logger.info("当前校验字段：" + needAssertField + "\n预期结果：" + expectMap.get(needAssertField) + "\n实际结果：" + resultMap.get(needAssertField));
                String valueResult = "null";
                if (resultMap.get(needAssertField) != null) {
                    valueResult = StringUtils.strip(resultMap.get(needAssertField).toString());
                }
                String valueExpect = StringUtils.strip(expectMap.get(needAssertField).toString()) ;
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

