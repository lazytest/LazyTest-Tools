package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Map;

/**
 *   提供HASHMAP和HASHMAP的比较（非嵌套），需要提供key到key的对应关系，默认为同名，可指定要比较的字段；
 b)  提供HASHMAP和HASHMAP的嵌套比较，需要提供Key.key到Key.key的对应关系，可指定要比较的字段，场景较少，优先级低；
 */
public class HashMapAssertUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashMapAssertUtil.class);
    
    /**
     * HASHMAP和HASHMAP的比较
     * 
     * @param  resultMap 		需要比较的map
     * @param  expectMap 		期望的map
     * 
     * @return void
     */
    public static <T> void compareHashMap(Map<String,T> resultMap, Map<String,T> expectMap) {
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        } else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        for (String key : expectMap.keySet()) {
            logger.info("当前校验字段：" + key + "\n预期结果：" + expectMap.get(key) + "\n实际结果：" + resultMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key).toString());
            }
            
            String valueExpect = "null";
            if (expectMap.get(key) != null) {
            	valueExpect = StringUtils.strip(expectMap.get(key).toString()) ;
	            if (StringUtils.equals("\"\"", valueExpect)) {
	                valueExpect = "";
	            }
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }

    /**
     * HASHMAP和HASHMAP的比较
     * 
     * @param  resultMap 		需要比较的map
     * @param  expectMap 		期望的map
     * @param  needAssertFields 需要比较的字段
     * 
     * @return void
     */
    public static <T> void compareHashMap(Map<String,T> resultMap, Map<String,T> expectMap, String[] needAssertFields) {
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        } else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        
        if (needAssertFields.length > 0) {
        	for (String needAssertField : needAssertFields) {
        		needAssertField = needAssertField.trim();
                Assert.assertTrue(resultMap.containsKey(needAssertField), "要校验的结果中不包含需要校验的列：" + needAssertField);
                Assert.assertTrue(expectMap.containsKey(needAssertField), "要校验的预期中不包含需要校验的列：" + needAssertField);
                
                String valueResult = "null";
                if (resultMap.get(needAssertField) != null) {
                    valueResult = StringUtils.strip(resultMap.get(needAssertField).toString());
                }
                
                String valueExpect = "null";
                if (expectMap.get(needAssertField) != null) {
                	valueExpect = StringUtils.strip(expectMap.get(needAssertField).toString()) ;
                }
                
                Assert.assertEquals(valueResult, valueExpect);
        	}
        }
    }
}
