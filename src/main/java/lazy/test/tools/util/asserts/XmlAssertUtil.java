package lazy.test.tools.util.asserts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lazy.test.tools.util.XmlUtil;

/**
 * Date: 15-11-30
 * Time: 下午3:46
 */
public class XmlAssertUtil {


    private static final Logger logger = LoggerFactory.getLogger(XmlAssertUtil.class);


    /**
     * 比较Xml中的一条数据与传入的数据是否一致
     * 
     * @param resultMap  实际从数据库查询出的结果
     * @param expectFilePath 从Xml中取到的预期结果
     * 
     * @return void
     */
    public static <T> void AssertDataFromXml(Map<String,T> resultMap, String expectFilePath) {
        //取出预期文件
        Map<String, Object> expectMap = XmlUtil.readMapFromXml(expectFilePath);
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
        //校验所有value
        for (String key : expectMap.keySet()) {
            Assert.assertTrue(resultMap.containsKey(key), "要校验的结果中不包含需要校验的列：" + key);
            logger.info("当前校验字段：" + key + "\n预期结果：" + expectMap.get(key) + "\n实际结果：" + resultMap.get(key));
            String valueResult = "null";
            if (resultMap.get(key) != null) {
                valueResult = StringUtils.strip(resultMap.get(key).toString());
            }
            String valueExpect = StringUtils.strip(expectMap.get(key).toString()) ;
            if (StringUtils.equals("\"\"", valueExpect)) {
                valueExpect = "";
            }
            Assert.assertEquals(valueResult, valueExpect);
        }
    }

    /**
     * 比较Xml中的一条数据与传入的数据中指定的字段是否一致
     * 
     * @param resultMap  		实际从数据库查询出的结果
     * @param expectXmlFilePath 从Xml中取到的预期结果
     * @param needAssertFields  需要校验的数据库字段list
     * 
     * @return void
     */
    public static <T> void AssertDataFromXml(Map<String,T> resultMap, String expectXmlFilePath, String[] needAssertFields) {
        //取出预期文件
        Map<String, Object> expectMap = XmlUtil.readMapFromXml(expectXmlFilePath);
        if (expectMap.isEmpty()) {
            Assert.assertTrue(resultMap.isEmpty());
        }else {
            Assert.assertTrue(!resultMap.isEmpty());
        }
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


    /**
     * 从Xml中比较多条数据中指定的数据
     * 
     * @param field4key			作为key的数据库字段名
     * @param resultMapList		实际结果
     * @param expectXmlFilePath	预期结果
     * @param needAssertFields	需要校验的数据库字段list
     * 
     * @return void
     */
    public static <T> void AssertMultiDataFromXmlByFields(String field4key, List<Map<String,T>> resultMapList, String expectXmlFilePath, String[] needAssertFields) {
        //取出预期文件
        List<HashMap<String, Object>> exceptMapList = (List<HashMap<String, Object>>) XmlUtil.readObjectFromXml(expectXmlFilePath);
        if (exceptMapList.isEmpty()) {
            Assert.assertTrue(resultMapList.isEmpty());
        } else {
            Assert.assertTrue(!resultMapList.isEmpty());
        }
        //转换成以field4key为key的map
        HashMap<String, HashMap<String, Object>> expectMapMap = new HashMap<String, HashMap<String, Object>>();
        for (HashMap<String, Object> exceptMap : exceptMapList) {
            expectMapMap.put(exceptMap.get(field4key).toString(), exceptMap);
        }
        //转换结果为mapmap
        HashMap<String,Map<String,T>> resultMapMap = new HashMap<String, Map<String,T>>();
        for (Map<String,T> resultMap : resultMapList) {
            resultMapMap.put(resultMap.get(field4key).toString(), resultMap);
        }
        Map<String,T> resultMap;
        Map<String, Object> exceptMap;
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
                    String valueExpect = StringUtils.strip(exceptMap.get(needAssertField).toString()) ;
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

    /**
     * 从Xml中比较多条数据中的数据
     * 
     * @param field4key 		作为key的数据库字段名
     * @param resultMapList		实际结果
     * @param expectXmlFilePath	预期结果
     * 
     * @return void
     */
    public static <T> void AssertMultiDataFromXml(String field4key, List<Map<String,T>> resultMapList, String expectXmlFilePath) {
        //取出预期文件
        List<Map<String, Object>> exceptMapList = (List<Map<String, Object>>) XmlUtil.readObjectFromXml(expectXmlFilePath);
        if (exceptMapList.isEmpty()) {
            Assert.assertTrue(resultMapList.isEmpty());
        } else {
            Assert.assertTrue(!resultMapList.isEmpty());
        }
        //转换成以field4key为key的map
        Map<String, Map<String, Object>> expectMapMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> exceptMap : exceptMapList) {
            expectMapMap.put(exceptMap.get(field4key).toString(), exceptMap);
        }
        //转换结果为mapmap
        Map<String, Map<String,T>> resultMapMap = new HashMap<String, Map<String,T>>();
        for (Map<String,T> resultMap : resultMapList) {
            resultMapMap.put(resultMap.get(field4key).toString(), resultMap);
        }
        Map<String,T> resultMap;
        Map<String, Object> exceptMap;
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
                    String valueExpect = StringUtils.strip(exceptMap.get(key).toString()) ;
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


}
