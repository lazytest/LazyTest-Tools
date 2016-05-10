package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

import lazy.test.tools.util.JsonUtil;

/**
 * <b>Date: 15-12-14</b></br>
 * <b>Time: 下午2:00</b></br>
 */
public class JsonHashMapAssertUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonHashMapAssertUtil.class);
    /**
     * Json和HashMap比较
     * 
     * @param  resultJson 		需要比较的json
     * @param  expectMap 		期望的expectMap
     * 
     * @return void
     */
    public static <T> void compareJsonWithMap(String resultJson, Map<String,T> expectMap) {
        Map<String,String> resultMap = JsonUtil.json2HashMap(resultJson);
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        for (String key : expectMap.keySet()) {
            logger.info("当前校验字段：" + key + "\nexpectMap结果：" + expectMap.get(key) + "\nresultJson结果：" + resultMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key));
            }
            String valueExpect = StringUtils.strip(expectMap.get(key).toString()) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }

}
