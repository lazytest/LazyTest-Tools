package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lazy.test.tools.util.CsvUtil;

/**
 * Date: 15-11-30
 * Time: 下午3:46
 */
public class CsvAssertUtil {


    private static final Logger logger = LoggerFactory.getLogger(CsvAssertUtil.class);


    /**
     * 比较Csv中的一条数据与传入的数据是否一致,csv格式须为GBK格式,若不是可以使用FileCharsetConverterUtil进行转换
     * 
     * @param  resultMap  实际从数据库查询出的结果
     * @param  expectCsvFilePath 从Csv中取到的预期结果
     * 
     * @return void
     */
    public static <T> void assertCsv2HashMap(Map<String,T> resultMap, String expectCsvFilePath) {
        List<Map<String, String>> list = CsvUtil.getDataFromCsv(expectCsvFilePath);
        if (list.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        } else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        Map<String, String> expectMap = list.get(0);
        //校验所有value
        for (String key : expectMap.keySet()) {
            Assert.assertTrue(resultMap.containsKey(key), "要校验的结果中不包含需要校验的列：" + key);
            logger.info("当前校验字段：" + key + "\n预期结果：" + expectMap.get(key) + "\n实际结果：" + resultMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key).toString());
            }
            String valueExpect =StringUtils.strip(expectMap.get(key)) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }

    /**
     * 比较Csv中的一条数据与传入的数据中指定的字段是否一致,若不是可以使用FileCharsetConverterUtil进行转换
     * 
     * @param  resultMap  实际从数据库查询出的结果
     * @param  expectCsvFilePath 从Csv中取到的预期结果
     * @param  needAssertFields  需要校验的数据库字段list
     * 
     * @return void
     */
    public static <T> void assertCsv2HashMapFields(Map<String,T> resultMap, String expectCsvFilePath, String[] needAssertFields) {
        //取出预期文件
        List<Map<String, String>> list = CsvUtil.getDataFromCsv(expectCsvFilePath);
        if (list.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        Map<String, String> expectMap = list.get(0);
        if (needAssertFields.length > 0) {
            //校验所有value
            for (String needAssertField : needAssertFields) {
                needAssertField = needAssertField.trim();
                Assert.assertTrue(resultMap.containsKey(needAssertField), "要校验的结果中不包含需要校验的列：" + needAssertField);
                logger.info("当前校验字段：" + needAssertField + "\n预期结果：" + expectMap.get(needAssertField) + "\n实际结果：" + resultMap.get(needAssertField));
                String valueResult = "null";
                if (resultMap.get(needAssertField) != null) {
                    valueResult = StringUtils.strip(resultMap.get(needAssertField).toString());
                }
                String valueExpect =StringUtils.strip(expectMap.get(needAssertField)) ;
                if (StringUtils.equals("\"\"", valueExpect)) {
                    valueExpect = "";
                }
                Assert.assertEquals(valueResult, valueExpect);
            }
        } else {
            logger.error("需要校验的数据库字段needAssertFields为空,结束比较 needAssertFields ：" + needAssertFields);
        }

    }

    /**
     * 从csv中比较多条数据库中的数据,若不是可以使用FileCharsetConverterUtil进行转换
     * 
     * @param  field4key 		            作为key的数据库字段名
     * @param  resultMapList 		实际结果
     * @param  expectCsvFilePath	预期结果
     * 
     * @return void
     */
    public static <T> void assertCsv2HashMapList(String field4key, List<Map<String,T>> resultMapList, String expectCsvFilePath) {
        //取出预期文件
        List<Map<String, String>> list = CsvUtil.getDataFromCsv(expectCsvFilePath);
        if (list.isEmpty()) {
            Assert.assertTrue(resultMapList.isEmpty());
        }else {
            Assert.assertTrue(!resultMapList.isEmpty());
        }
        //转换成以field4key为key的map
        Map<String, Map<String, String>> expectMapMap = new HashMap<String, Map<String, String>>();
        for (Map<String, String> exceptMap : list) {
            expectMapMap.put(exceptMap.get(field4key), exceptMap);
        }
        //转换结果为mapmap
        Map<String, Map<String, T>> resultMapMap = new HashMap<String, Map<String, T>>();
        for (Map<String,T> resultMap : resultMapList) {
            resultMapMap.put(resultMap.get(field4key).toString(), resultMap);
        }
        Map<String, T> resultMap;
        Map<String, String> exceptMap;
        for (String keyP : resultMapMap.keySet()) {
            resultMap = resultMapMap.get(keyP);
            exceptMap = expectMapMap.get(keyP);
            if (exceptMap.size() > 0 && resultMap.size() > 0) {
                for (String key : exceptMap.keySet()) {
                    Assert.assertTrue(resultMap.containsKey(key), "要校验的结果中不包含需要校验的列：" + key);
                    logger.info("当前校验字段：" + key + "\n预期结果：" + exceptMap.get(key) + "\n实际结果：" + resultMap.get(key));
                    String valueResult = "null";
                    if (resultMap.get(key) != null) {
                        valueResult = StringUtils.strip(resultMap.get(key).toString());
                    }
                    String valueExpect =StringUtils.strip(exceptMap.get(key)) ;
                    if (StringUtils.equals("\"\"", valueExpect)) {
                        valueExpect = "";
                    }
                    Assert.assertEquals(valueResult, valueExpect);
                }
            } else {
                logger.error("需要比较的结果map为空,resultMap.size : " + resultMap.size() + "\n或预期map为空,exceptMap.size : " + exceptMap.size());
            }

        }
    }

    /**
     * 从csv中比较多条数据中指定的数据,若不是可以使用FileCharsetConverterUtil进行转换
     * 
     * @param @param field4key 			作为key的数据库字段名
     * @param @param resultMapList 		实际结果
     * @param @param exceptFilePath 	预期结果
     * @param @param needAssertFields   需要校验的数据库字段list
     * 
     * @return void
     */
    public static <T> void assertCsv2HashMapListByFields(String field4key, List<Map<String,T>> resultMapList, String expectCsvFilePath, String[] needAssertFields) {
        //取出预期文件
        List<Map<String, String>> list = CsvUtil.getDataFromCsv(expectCsvFilePath);
        if (list.isEmpty()) {
            Assert.assertTrue(resultMapList.isEmpty());
        } else {
            Assert.assertTrue(!resultMapList.isEmpty());
        }

        //转换成以field4key为key的map
        Map<String, Map<String, String>> expectMapMap = new HashMap<String, Map<String, String>>();
        for (Map<String, String> exceptMap : list) {
            expectMapMap.put(exceptMap.get(field4key), exceptMap);
        }
        //转换结果为mapmap
        Map<String, Map<String,T>> resultMapMap = new HashMap<String, Map<String,T>>();
        for (Map<String,T> resultMap : resultMapList) {
            resultMapMap.put(resultMap.get(field4key).toString(), resultMap);
        }
        Map<String,T> resultMap;
        Map<String, String> exceptMap;
        for (String keyP : resultMapMap.keySet()) {
            resultMap = resultMapMap.get(keyP);
            exceptMap = expectMapMap.get(keyP);
            if (needAssertFields.length > 0 && exceptMap.size() > 0 && resultMap.size() > 0) {
                for (String needAssertField : needAssertFields) {
                    needAssertField = needAssertField.trim();
                    Assert.assertTrue(resultMap.containsKey(needAssertField), "要校验的结果中不包含需要校验的列：" + needAssertField);
                    logger.info("当前校验字段：" + needAssertField + "\n预期结果：" + exceptMap.get(needAssertField) + "\n实际结果：" + resultMap.get(needAssertField));
                    String valueResult = "null";
                    if (resultMap.get(needAssertField) != null) {
                        valueResult = StringUtils.strip(resultMap.get(needAssertField).toString());
                    }
                    String valueExpect =StringUtils.strip(exceptMap.get(needAssertField)) ;
                    if (StringUtils.equals("\"\"", valueExpect)) {
                        valueExpect = "";
                    }
                    Assert.assertEquals(valueResult, valueExpect);
                }
            } else {
                logger.error("需要比较的结果map为空,无法进行比较 needAssertFields ：" + needAssertFields + "\n预期map.size ：" + exceptMap.size() + "\n实际map.size：" + resultMap.size());
            }
        }
    }


}
