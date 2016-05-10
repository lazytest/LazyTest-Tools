package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

import lazy.test.tools.util.JsonUtil;

/**
 * <b>Date: 15-12-14</b></br>
 * <b>Time: 下午2:22</b></br>
 */
public class JsonClassAssertUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonClassAssertUtil.class);
    /**
     * 类和HashMap比较
     * 
     * @param  resultJson  需要比较的json
     * @param  t 		         要比较的类
     * 
     * @return void
     */
    public static <T> void compareJsonWithClass(String resultJson,T t) {
        Map<String,String> resultMap = JsonUtil.json2HashMap(resultJson);
        Map<String, String> classMap = JsonUtil.json2HashMap(JsonUtil.object2JSONNoFeatures(t));
        if (classMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        } else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        for (String key : resultMap.keySet()) {
            logger.info("当前校验字段：" + key + "\nresultJson结果：" + resultMap.get(key) + "\n类结果：" + classMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key));
            }
            String valueExpect = StringUtils.strip(classMap.get(key)) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }
}
