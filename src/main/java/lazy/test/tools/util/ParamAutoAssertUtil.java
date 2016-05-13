package lazy.test.tools.util;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ParamAutoAssertUtil {

    private static final String HEAD_CLASS_NAME = "CLASS_NAME";

    private static final String HEAD_METHOD_NAME = "METHOD_NAME";

    private static final String HEAD_PARAM_NAME = "PARAM_NAME";

    private static final String HEAD_BEAN_ID = "BEAN_ID";

    private static final String HEAD_DEFAULT_KEY = "DEFAULT_KEY";

    private static final String HEAD_RUN_FLAG = "RUN_FLAG";

    private static final String HEAD_SET_KEY = "SET_KEY";

    private static final String HEAD_RESULT_KEY = "RESULT_KEY";

    private static final String ERROR_INPUT_DIR_INVALID = "目标地址不是一个文件目录, 无法进行解析";

    private static final String ERROR_INPUT_NO_FILE_FOUND = "目标地址下无配置文件, 无法进行解析";

    /**
     * 执行批量参数校验, 调用对象无容器环境, 可直接反射并执行方法
     * @param config 存放配置文件的目录
     * @return List 错误信息返回, 执行无错误则返回无内容的List
     */
    public static List<String> paramBatchAssert(File config) {
        List<String> list = new ArrayList<String>();
        if (!config.isDirectory()) {
            list.add(ERROR_INPUT_DIR_INVALID);
            return list;
        }

        List<File> configList = filterConfigFile(config);
        if (configList == null) {
            list.add(ERROR_INPUT_NO_FILE_FOUND);
            return list;
        }

        for (File file : configList) {
            List<String> testList = paramSingleAssert(file);
            if (!testList.isEmpty()) {
                list.addAll(testList);
            }
        }
        return list;
    }

    /**
     * 执行批量参数校验, 调用对象依赖Spring容器环境, 需使用容器获取对象执行方法
     * @param config 存放配置文件的目录
     * @return List 错误信息返回, 执行无错误则返回无内容的List
     */
    public static List<String> paramBatchAssert(File config, ApplicationContext context) {
        List<String> list = new ArrayList<String>();
        if (!config.isDirectory()) {
            list.add(ERROR_INPUT_DIR_INVALID);
            return list;
        }

        List<File> configList = filterConfigFile(config);
        if (configList == null) {
            list.add(ERROR_INPUT_NO_FILE_FOUND);
            return list;
        }

        for (File file : configList) {
            List<String> testList = paramSingleAssert(file, context);
            if (!testList.isEmpty()) {
                list.addAll(testList);
            }
        }
        return list;
    }

    /**
     * 执行单个参数校验, 调用对象无容器环境, 可直接反射并执行方法
     * @param config 配置文件地址
     * @return List 错误信息返回, 执行无错误则返回无内容的List
     */
    public static List<String> paramSingleAssert(File config) {
        List<String> list = new ArrayList<String>();

        List<String[]> csvDataList = null;
        List<ParamTestRunner> runnerList = null;
        try {
            csvDataList = readConfigData(config);
            runnerList = prepareTestInfoData(csvDataList);
        } catch (Exception e) {
            list.add("解析配置文件出错! rootCause -> " + e.getMessage());
        }
        if (runnerList != null && !runnerList.isEmpty()) {
            for (ParamTestRunner runner : runnerList) {
                List<String> result = runner.execute(null);
                if (result != null && !result.isEmpty()) {
                    list.addAll(result);
                }
            }
        }
        return list;
    }

    /**
     * 执行批量参数校验, 调用对象依赖Spring容器环境, 需使用容器获取对象执行方法
     * @param config 配置文件地址
     * @return List 错误信息返回, 执行无错误则返回无内容的List
     */
    public static List<String> paramSingleAssert(File config, ApplicationContext context) {
        List<String> list = new ArrayList<String>();

        List<String[]> csvDataList = null;
        List<ParamTestRunner> runnerList = null;
        try {
            csvDataList = readConfigData(config);
            runnerList = prepareTestInfoData(csvDataList);
        } catch (Exception e) {
            list.add("解析配置文件出错! rootCause -> " + e.getMessage());
        }
        if (runnerList != null && !runnerList.isEmpty()) {
            for (ParamTestRunner runner : runnerList) {
                List<String> result = runner.execute(context);
                if (result != null && !result.isEmpty()) {
                    list.addAll(result);
                }
            }
        }
        return list;
    }

    private static List<ParamTestRunner> prepareTestInfoData(List<String[]> csvDataList) {
        String className = null;
        String methodName = null;
        String beanId = null;
        List<String> paramNameList = new ArrayList<String>();
        Map<String, String> defaultValueMap = new HashMap<String, String>();

        List<ParamTestRunner> list = new ArrayList<ParamTestRunner>();
        String[] headColArray = csvDataList.get(0);
        for (int i = 0; i < headColArray.length; i++) {
            for (int j = 1; j < csvDataList.size(); j++) {
                String[] dataColArray = csvDataList.get(j);
                if (StringUtils.isBlank(dataColArray[i])) {
                    continue;
                }
                if (StringUtils.contains(headColArray[i], HEAD_CLASS_NAME)) {
                    className = StringUtils.strip(dataColArray[i]);
                }
                if (StringUtils.contains(headColArray[i], HEAD_METHOD_NAME)) {
                    methodName = StringUtils.strip(dataColArray[i]);
                }
                if (StringUtils.contains(headColArray[i], HEAD_PARAM_NAME)) {
                    paramNameList.add(StringUtils.strip(dataColArray[i]));
                }
                if (StringUtils.contains(headColArray[i], HEAD_BEAN_ID)) {
                    beanId = StringUtils.strip(dataColArray[i]);
                }
                if (StringUtils.contains(headColArray[i], HEAD_DEFAULT_KEY)) {
                    defaultValueMap.put(StringUtils.strip(dataColArray[i]), StringUtils.strip(dataColArray[i + 1]));
                }
            }
        }
        for (int i = 1; i < csvDataList.size(); i++) {
            ParamTestRunner runner = new ParamTestRunner();
            runner.setClassName(className);
            runner.setMethodName(methodName);
            runner.setBeanId(beanId);
            runner.getParamNameList().addAll(paramNameList);
            runner.getDefaultValueMap().putAll(defaultValueMap);

            for (int j = 0; j < headColArray.length; j++) {
                String[] dataColArray = csvDataList.get(i);
                if (StringUtils.contains(headColArray[j], HEAD_RUN_FLAG)) {
                    runner.setTestRun(StringUtils.equals(StringUtils.strip(dataColArray[j]), "1"));
                }
                if (StringUtils.contains(headColArray[j], HEAD_SET_KEY)) {
                    if (StringUtils.isNotBlank(dataColArray[j])) {
                        runner.getTestValueMap().put(StringUtils.strip(dataColArray[j]), dataColArray[j + 1]);
                    }
                }
                if (StringUtils.contains(headColArray[j], HEAD_RESULT_KEY)) {
                    if (StringUtils.isNotBlank(dataColArray[j]) && StringUtils.isNotBlank(dataColArray[j + 1])) {
                        runner.getTestResultMap().put(StringUtils.strip(dataColArray[j]), StringUtils.strip(dataColArray[j + 1]));
                    }
                }
            }
            if (runner.isTestRun()) {
                list.add(runner);
            }
        }
        return list;
    }

    private static List<String[]> readConfigData(File config) throws Exception{
        InputStreamReader reader = new InputStreamReader(new FileInputStream(config), "GBK");
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> result = csvReader.readAll();
        if (result == null || result.size() <= 1) {
            throw new Exception("配置文件无相关验证信息, 请确认文件内容. 文件名: " + config.getName() + ", 文件路径: " + config.getAbsolutePath());
        }
        return result;
    }

    private static List<File> filterConfigFile(File config) {
        File[] fileArray = config.listFiles();
        if (fileArray == null || fileArray.length == 0) {
            return null;
        }
        List<File> fileList = new ArrayList<File>();
        for (File file : fileArray) {
            if (!file.getName().endsWith(".csv")) {
                continue;
            }
            fileList.add(file);
        }
        if (fileList.isEmpty()) {
            return null;
        }
        return fileList;
    }
}

@SuppressWarnings({"unchecked", "rawtypes"})
class ParamTestRunner {

    private static final String AST_PARSE_TYPE_SIMPLE = "AST_PARSE_TYPE_SIMPLE";

    private static final String AST_PARSE_TYPE_PRIMITIVE = "AST_PARSE_TYPE_PRIMITIVE";

    private static final String AST_PARSE_TYPE_VARIABLE = "AST_PARSE_TYPE_VARIABLE";

    private static final String AST_PARSE_TYPE_ARRAY = "AST_PARSE_TYPE_ARRAY";

    private static final String AST_PARSE_KEY_PARAM_CLASS = "AST_PARSE_KEY_PARAM_CLASS";

    private static final String AST_PARSE_TYPE_PARAM = "AST_PARSE_TYPE_PARAM";

    /**config info*/
    private String className;

    private String methodName;

    private List<String> paramNameList;

    private String beanId;

    private Map<String, String> defaultValueMap;

    private boolean testRun;

    private Map<String, String> testValueMap;

    private Map<String, String> testResultMap;

    /**generate info*/
    private Object targetObj;

    private List<Object> paramObjList = new ArrayList<Object>();

    private List<Class> paramTypeList = new ArrayList<Class>();

    private Object resultObj;

    public List<String> execute(ApplicationContext context) {
        List<String> list = new ArrayList<String>();
        if (!testRun) {
            list.add("测试用例设置为忽略, 跳过此测试. 测试信息: " + toString());
            return list;
        }
        try {
            init(context);
        } catch (Exception e) {
            list.add("测试对象创建初始化错误! rootCause -> " + e.getMessage());
        }
        try {
            setParamDefault();
        } catch (Exception e) {
            list.add("测试参数默认值初始化错误! rootCause -> " + e.getMessage());
        }
        try {
            setParamTest();
        } catch (Exception e) {
            list.add("测试参数测试值初始化错误! rootCause -> " + e.getMessage());
        }
        try {
            executeAndAssert();
        } catch (Exception e) {
            list.add("测试用例验证错误! rootCause -> " + e.getMessage());
        }
        return list;
    }

    private void init(ApplicationContext context) throws Exception{
        if (context != null && StringUtils.isBlank(beanId)) {
            throw new Exception("基于容器配置的对象无beanId, className: " + className + ", methodName: " + methodName);
        }
        //初始化测试对象
        if (context != null) {
            targetObj = context.getBean(beanId);
        } else {
            targetObj = createObject(className);
        }
        //初始化参数对象
        if (paramNameList.isEmpty()) {
            Method method = findNameMatchedMethod();
            Type[] paramTypeArray = method.getGenericParameterTypes();
            if (paramTypeArray != null && paramTypeArray.length != 0) {
                for (Type type : paramTypeArray) {
                    if (type instanceof Class) {
                        paramNameList.add(((Class) type).getName());
                    } else {
                        paramNameList.add(type.toString());
                    }
                }
            }
        }
        for (String paramStr : paramNameList) {
            Object obj = createObject(StringUtils.substringBefore(paramStr, "_{"));
            fillAllStringField(obj, obj.getClass().getSimpleName());
            paramObjList.add(obj);
            paramTypeList.add(obj.getClass());
        }
        //替换动态数据
        if (defaultValueMap != null) {
            replaceDynamicValue(defaultValueMap);
        }
        if (testValueMap != null) {
            replaceDynamicValue(testValueMap);
        }
    }

    private void setParamDefault() throws Exception {
        if (defaultValueMap == null || defaultValueMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : defaultValueMap.entrySet()) {
            Object param = getGenerateParamByKey(entry.getKey());
            String paramArgKey = StringUtils.substringAfter(entry.getKey(), ".");
            String value = null;
            if (!StringUtils.equalsIgnoreCase(entry.getValue(), "null")) {
                value = entry.getValue();
            }
            if (StringUtils.equals(entry.getValue(), "\"\"")) {
                value = "";
            }
            
            try {
            	BeanUtils.setProperty(param, paramArgKey, value);
            } catch (Exception e) {
            	PropertyUtils.setProperty(param, paramArgKey, value);
            }
        }
    }

    private void setParamTest() throws Exception {
        if (testValueMap == null || testValueMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : testValueMap.entrySet()) {
            if (!StringUtils.contains(entry.getKey(), ".")) {
                setGenerateParamByKey(entry.getKey(), entry.getValue());
                continue;
            }/* else if (StringUtils.contains(entry.getKey(), ".") 
            		&& StringUtils.contains(entry.getKey(), "[")
            		&& StringUtils.contains(entry.getKey(), "]")) {
            	setGenerateParamByKeyComplex(entry.getKey(), entry.getValue());
            	continue;
            }*/
            Object param = getGenerateParamByKey(entry.getKey());
            String paramArgKey = StringUtils.substringAfter(entry.getKey(), ".");
            String value = null;
            if (!StringUtils.equalsIgnoreCase(entry.getValue(), "null")) {
                value = entry.getValue();
            }
            if (entry.getValue().matches(" *\" *\" *")) {
                value = StringUtils.replace(StringUtils.strip(entry.getValue()), "\"", "");
            }
            
            if(StringUtils.isNotBlank(value)) {
            	value = StringUtils.strip(value);
            }
            
            try {
            	BeanUtils.setProperty(param, paramArgKey, value);
            } catch (Exception e) {
            	PropertyUtils.setProperty(param, paramArgKey, value);
            }
        }
    }

    private void executeAndAssert() throws Exception {
        if (testResultMap == null || testResultMap.isEmpty()) {
            throw new Exception("检验结果未配置, 跳过测试");
        }
        try {
            int size = paramTypeList.size();
            resultObj = MethodUtils.invokeMethod(targetObj, methodName, paramObjList.toArray(), paramTypeList.toArray(new Class[size]));
        } catch (Exception e) {
            throw new Exception("测试用例执行出错! rootCause -> 类名: " + className + ", 方法名: " + methodName);
        }

        for (Map.Entry<String, String> entry : testResultMap.entrySet()) {
            Object val = PropertyUtils.getNestedProperty(resultObj, entry.getKey());
            if (!StringUtils.equals(entry.getValue(), String.valueOf(val))) {
                StringBuilder sb = new StringBuilder("测试用例验证出错! rootCause -> ");
                sb.append("类名: " + className + ", ");
                sb.append("方法名: " + methodName + "; ");
                sb.append("期望值: " + entry.getKey() + " = " + entry.getValue() + ", ");
                sb.append("实际值: " + entry.getKey() + " = " + val);
                throw new Exception(sb.toString());
            }
        }
    }

    private void replaceDynamicValue(Map<String, String> map) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.contains(entry.getValue(), "${")) {
                String[] array = StringUtils.substringsBetween(entry.getValue(), "${", "}");
                for (String num : array) {
                    if (StringUtils.isBlank(num) || !StringUtils.isNumeric(num)) {
                        String value = sdf.format(new Date());
                        map.put(entry.getKey(), StringUtils.replace(entry.getValue(), "${" + num + "}", value));
                        continue;
                    }
                    String value = sdf.format(new Date());
                    int length = Integer.parseInt(num);
                    if (length - value.length() > 0) {
                        Random random = new Random();
                        String raw = "10";
                        for (int i = 0; i < length - value.length(); i++) {
                            raw += "0";
                        }
                        value = value + random.nextInt(Integer.parseInt(raw));
                    }
                    value = StringUtils.right(value, length);
                    map.put(entry.getKey(), StringUtils.replace(entry.getValue(), "${" + num + "}", value));
                }
            }
        }
    }

    private Object getGenerateParamByKey(String key) throws Exception{
    	String paramObjName = StringUtils.substringBefore(key, "[");
    	
    	if (!StringUtils.contains(paramObjName, ".")) {
    		if (StringUtils.startsWith(key, "[")) {
		        if (StringUtils.contains(key, "[") && StringUtils.contains(key, "]")) {
		//        	String param
		        	
		            String paramArgIndex = StringUtils.substringBetween(key, "[", "]");
		            return paramObjList.get(Integer.valueOf(paramArgIndex));
		        }
    		} else {
    			int index = Integer.valueOf(StringUtils.substringBetween(key, "[", "]"));
    			
    			for (int i = 0; i < paramNameList.size(); i++) {
	                String paramName = StringUtils.substringBetween(paramNameList.get(i), "_{", "}");
	                if (StringUtils.equals(paramName, StringUtils.substringBefore(key, "["))) {
	                    return ((List<?>)paramObjList.get(i)).get(index);
	                }
	            }
    		}
    	} else {
    		for (int i = 0; i < paramNameList.size(); i++) {
                String paramName = StringUtils.substringBetween(paramNameList.get(i), "_{", "}");
                if (StringUtils.equals(paramName, StringUtils.substringBefore(key, "."))) {
                    return paramObjList.get(i);
                }
            }
    	}
        throw new Exception("无法识别参数key, 请配置并遵循org.apache.commons.BeanUtils取值key规则");
    }

    private void setGenerateParamByKey(String key, String rawValue) throws Exception{
        String value = null;
        if (!StringUtils.equalsIgnoreCase(rawValue, "null")) {
            value = rawValue;
        }
        if (StringUtils.equals(rawValue, "\"\"")) {
            value = "";
        }

        if (StringUtils.contains(key, "[") && StringUtils.contains(key, "]")) {
        	if (StringUtils.startsWith(key, "[")) {
	            String paramArgIndex = StringUtils.substringBetween(key, "[", "]");
	            paramObjList.set(Integer.valueOf(paramArgIndex), value);
	            return;
        	} else {
        		int index = Integer.valueOf(StringUtils.substringBetween(key, "[", "]"));
    			
    			for (int i = 0; i < paramNameList.size(); i++) {
	                String paramName = StringUtils.substringBetween(paramNameList.get(i), "_{", "}");
	                if (StringUtils.equals(paramName, StringUtils.substringBefore(key, "["))) {
	                    ((List<Object>)paramObjList.get(i)).set(index, value);
	                    return;
	                }
	            }
        	}
        } else {
            for (int i = 0; i < paramNameList.size(); i++) {
                String paramName = StringUtils.substringBetween(paramNameList.get(i), "_{", "}");
                if (StringUtils.equals(paramName, key)) {
                    paramObjList.set(i, value);
                    return;
                }
            }
        }
        throw new Exception("无法识别参数key, 请配置并遵循org.apache.commons.BeanUtils取值key规则");
    }

    private Method findNameMatchedMethod() throws Exception {
        Method[] methodArray = targetObj.getClass().getMethods();
        for (Method method : methodArray) {
            if (StringUtils.equals(method.getName(), methodName)) {
                return method;
            }
        }
        throw new Exception("无匹配的测试方法, 请检查配置");
    }

    private Object createObject(String type) throws Exception {
        Map<String, Object> map = convertClassTypeMap(type);
        return createObject(map);
    }

    private Object createObject(Map<String, Object> map) throws Exception {
        if (map.containsKey(AST_PARSE_TYPE_PRIMITIVE)) {
            String className = (String)map.get(AST_PARSE_TYPE_PRIMITIVE);
            Object obj = createInstance(className);
            fillDefaultAttributes(obj);
            return obj;
        }
        if (map.containsKey(AST_PARSE_TYPE_VARIABLE)) {
            return "object";
        }
        if (map.containsKey(AST_PARSE_TYPE_SIMPLE)) {
            String className = (String)map.get(AST_PARSE_TYPE_SIMPLE);
            Object obj = createInstance(className);
            fillDefaultAttributes(obj);
            return obj;
        }
        if (map.containsKey(AST_PARSE_TYPE_ARRAY)) {
            String arrayType = searchArrayType((List<Map>) map.get(AST_PARSE_TYPE_ARRAY));
            Class clazz = Class.forName(arrayType);
            Object result = Array.newInstance(clazz, 1);
            Map<String, Object> temp = ((List<Map<String, Object>>)map.get(AST_PARSE_TYPE_ARRAY)).get(0);

            Object obj = createObject(temp);
            Array.set(result, 0, obj);
            return result;
        }
        if (map.containsKey(AST_PARSE_TYPE_PARAM)) {
            Object param = null;
            List paramArg = new ArrayList();
            for (Map<String, Object> temp : (List<Map<String, Object>>)map.get(AST_PARSE_TYPE_PARAM)) {
                if (temp.containsKey(AST_PARSE_KEY_PARAM_CLASS)) {
                    String paramType = (String)temp.get(AST_PARSE_KEY_PARAM_CLASS);
                    param = createInstance(paramType);
                    fillDefaultAttributes(param);
                    continue;
                }
                paramArg.add(createObject(temp));
            }
            return buildParameterObject(param, paramArg);
        }
        return map;
    }

    private Object createInstance(String className) throws Exception {
        String type = findMappingResult(className);
        Class clazz = Class.forName(type);

        if (clazz.isPrimitive() || isDecorationType(clazz)) {
            if (char.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)) {
                return '0';
            } else {
                Constructor primitiveConstructor = clazz.getConstructor(String.class);
                return primitiveConstructor.newInstance("0");
            }
        }
        if (clazz.isEnum()) {
            return clazz.getEnumConstants()[0];
        }
        if (clazz.isAssignableFrom(Object.class)) {
            return "object";
        }
        if (clazz.isAssignableFrom(BigDecimal.class) || clazz.isAssignableFrom(BigInteger.class)) {
        	return clazz.getConstructor(String.class).newInstance("0");
        }

        Constructor constructor = findMinVarsConstructor(clazz);
        Type[] paramTypeArray = constructor.getGenericParameterTypes();
        if (paramTypeArray.length == 0) {
            return constructor.newInstance();
        }
        List paramList = new ArrayList();
        for (Type paramType : paramTypeArray) {
            paramList.add(genGenericTypeObject(paramType));
        }
        return constructor.newInstance(paramList.toArray());
    }

    private Map<String, Object> convertClassTypeMap(String type) throws Exception{
        type = StringUtils.strip(type);

        Map<String, Object> map = new HashMap<String, Object>();

        //Map<String, String>
        if (isPrimitiveType(type)) {
            map.put(AST_PARSE_TYPE_PRIMITIVE, type);
            return map;
        }
        //Map<K, V>
        if (isVariableType(type)) {
            map.put(AST_PARSE_TYPE_VARIABLE, type);
            return map;
        }
        //Map<String, String>
        if (isSimpleType(type)) {
            map.put(AST_PARSE_TYPE_SIMPLE, type);
            return map;
        }
        //Map<String, List<Map>>递归, 最后一级以Map<String, String>结束
        if (isArrayType(type)) {
            String subStr = StringUtils.substringBeforeLast(type, "[");
            List<Map> loopList = new ArrayList<Map>();
            loopList.add(convertClassTypeMap(subStr));
            map.put(AST_PARSE_TYPE_ARRAY, loopList);
            return map;
        }
        //Map<String, List<Map>>递归, 最后一级以Map<String, String>结束
        if (isParameterType(type)) {
            String rawName = StringUtils.substringBefore(type, "<");
            List<Map> loopList = new ArrayList<Map>();

            Map<String, String> rawNameMap = new HashMap<String, String>();
            rawNameMap.put(AST_PARSE_KEY_PARAM_CLASS, StringUtils.strip(rawName));
            loopList.add(rawNameMap);

            String paramStr = StringUtils.strip(StringUtils.substring(type, rawName.length() + 1, type.length() -1));
            for (String temp : splitRawParamString(paramStr)) {
                loopList.add(convertClassTypeMap(temp));
            }
            map.put(AST_PARSE_TYPE_PARAM, loopList);
            return map;
        }

        throw new Exception("不支持的参数类型");
    }

    private void fillDefaultAttributes(Object instance) throws Exception {
        if (instance.getClass().getName().startsWith("java") || instance.getClass().getName().startsWith("javax") ||instance.getClass().getName().startsWith("com.sun")) {
            return;
        }
        if (instance.getClass().isEnum()) {
            return;
        }

        List<Field> fieldList = fetchAllFieldList(instance.getClass());

        if (fieldList.isEmpty()) {
            return;
        }

        for (Field field : fieldList) {
            field.setAccessible(true);
            try {
                Object fieldObj = genGenericTypeObject(field.getGenericType());
                field.set(instance, fieldObj);
            } catch (Exception e) {
                throw e;
            } finally {
                field.setAccessible(false);
            }
        }
    }

    private List<String> splitRawParamString(String paramStr) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(paramStr)) {
            return list;
        }
        if (!StringUtils.contains(paramStr, ",")) {
            list.add(StringUtils.strip(paramStr));
            return list;
        }

        String[] strArray = StringUtils.split(paramStr, ",");
        int cnt = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            if (StringUtils.contains(strArray[i], "<") && !StringUtils.contains(strArray[i], ">")) {
                sb.append(strArray[i]).append(",");
                cnt++;
            } else if (StringUtils.contains(strArray[i], ">") && !StringUtils.contains(strArray[i], "<")) {
                sb.append(strArray[i]);
                cnt--;
            } else {
                sb.append(strArray[i]);
            }
            if (cnt == 0) {
                list.add(StringUtils.strip(sb.toString()));
                sb = new StringBuffer();
                continue;
            }
            if (checkMatch(sb.toString())) {
                list.add(StringUtils.strip(sb.toString()));
                sb = new StringBuffer();
                cnt = 0;
                continue;
            }
            if (i == strArray.length - 1) {
                for (int j = 0; j < cnt - 1; j++) {
                    sb.append(">");
                }
                list.add(StringUtils.strip(sb.toString()));
            }
        }

        return list;
    }

    private Constructor findMinVarsConstructor(Class clazz) throws Exception {
        Constructor[] array = clazz.getConstructors();
        int varNum = Integer.MAX_VALUE;
        int arrayLoc = -1;
        boolean findConstructor = false;
        for (int i = 0; i < array.length; i++) {
            Class[] paramTypeArray = array[i].getParameterTypes();
            if (paramTypeArray.length == 0) {
                return array[i];
            }
            if (paramTypeArray.length < varNum) {
                findConstructor = true;
                varNum = paramTypeArray.length;
                arrayLoc = i;
            }
        }
        if (findConstructor) {
            return array[arrayLoc];
        }
        throw new Exception("测试方法参数无构造方法");
    }

    private Object genGenericTypeObject(Type type) throws Exception {
        if (type instanceof ParameterizedType) {
            return genParamTypeObject((ParameterizedType) type);
        }
        if (type instanceof TypeVariable) {
            return new Object();
        }
        if (type instanceof Class) {
            Class fieldClass = (Class) type;
            if (fieldClass.isArray()) {
                Class componentClass = fieldClass.getComponentType();
                Object arrayObj = Array.newInstance(componentClass, 1);
                Object componentObj = createInstance(componentClass.getName());
                fillDefaultAttributes(componentObj);
                Array.set(arrayObj, 0, componentObj);
                return arrayObj;
            }
            Object obj = createInstance(fieldClass.getName());
            fillDefaultAttributes(obj);
            return obj;
        }
        return null;
    }

    private Object genParamTypeObject(ParameterizedType type) throws Exception {
        Class rawClass = (Class)type.getRawType();
        Object rawObj = createInstance(rawClass.getName());
        Type[] argTypeArray = type.getActualTypeArguments();
        List argList = new ArrayList();
        for (Type argType : argTypeArray) {
            if (argType instanceof Class) {
                Object arg = createInstance(((Class) argType).getName());
                fillDefaultAttributes(arg);
                argList.add(arg);
            }
            if (argType instanceof ParameterizedType) {
                Object arg = genParamTypeObject((ParameterizedType) argType);
                argList.add(arg);
            }
        }

        return buildParameterObject(rawObj, argList);
    }

    private Object buildParameterObject(Object param, List paramArg) throws Exception {
        if (param instanceof List) {
            ((List)param).addAll(paramArg);
            return param;
        }
        if (param instanceof Set) {
            ((Set)param).addAll(paramArg);
            return param;
        }
        if (param instanceof Map) {
            ((Map)param).put(paramArg.get(0), paramArg.get(1));
            return param;
        }

        Class clazz = param.getClass();

        TypeVariable<Class>[] typeVarArray = clazz.getTypeParameters();
        if (typeVarArray != null && typeVarArray.length > 0) {
            for(int i = 0; i < typeVarArray.length; i++) {
                Field field = searchFieldByTypeName(clazz, typeVarArray[i].getName());
                if (field == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(param, paramArg.get(i));
                } catch (Exception e) {
                    throw e;
                } finally {
                    field.setAccessible(false);
                }
            }
        }
        return param;
    }

    private Field searchFieldByTypeName(Class clazz, String name) {
        Field[] fieldArray = clazz.getDeclaredFields();
        for (Field field : fieldArray) {
            Type type = field.getGenericType();
            if (type instanceof TypeVariable && StringUtils.equals(name, ((TypeVariable) type).getName())) {
                return field;
            }
        }
        return null;
    }

    private String searchArrayType(List<Map> list) throws Exception {
        Map<String, Object> map = list.get(0);
        if (map.containsKey(AST_PARSE_TYPE_ARRAY)) {
            searchArrayType((List<Map>) map.get(AST_PARSE_TYPE_ARRAY));
        }
        if (map.containsKey(AST_PARSE_TYPE_PARAM)) {
            for (Map<String, Object> temp : (List<Map<String, Object>>)map.get(AST_PARSE_TYPE_PARAM)) {
                if (temp.containsKey(AST_PARSE_KEY_PARAM_CLASS)) {
                    return (String)temp.get(AST_PARSE_KEY_PARAM_CLASS);
                }
            }
        }
        if (map.containsKey(AST_PARSE_TYPE_SIMPLE)) {
            return (String)map.get(AST_PARSE_TYPE_SIMPLE);
        }
        throw new Exception("创建参数对象失败");
    }

    private void fillAllStringField(Object obj, String fieldName) throws Exception{
        Class clazz = obj.getClass();
        if (clazz.isArray()) {
            if (isStringType(clazz.getComponentType())) {
                Array.set(obj, 0, fieldName);
            } else {
                fillAllStringField(Array.get(obj, 0), fieldName);
            }
            return;
        }
        if (obj instanceof Map) {
            List removeList = new ArrayList();
            Set<Map.Entry> entrySet = ((Map) obj).entrySet();
            for (Map.Entry entry : entrySet) {
                if (isStringType(entry.getKey().getClass())) {
                    removeList.add(entry.getKey());
                } else {
                    fillAllStringField(entry.getKey(), fieldName);
                }
            }
            for (Map.Entry entry : entrySet) {
                if (isStringType(entry.getValue().getClass())) {
                    ((Map) obj).put(entry.getKey(), fieldName);
                } else {
                    fillAllStringField(entry.getValue(), fieldName);
                }
            }
            for (Object tempKey : removeList) {
                ((Map) obj).put(fieldName, ((Map) obj).get(tempKey));
                ((Map) obj).remove(tempKey);
            }
            return;
        }
        if (obj instanceof Set) {
            List removeList = new ArrayList();
            for (Object temp : (Set) obj) {
                if (isStringType(temp.getClass())) {
                    removeList.add(temp);
                } else {
                    fillAllStringField(temp, fieldName);
                }
            }
            if (!removeList.isEmpty()) {
                ((Set) obj).add(fieldName);
                ((Set) obj).removeAll(removeList);
            }
            return;
        }
        if (obj instanceof List) {
            List removeList = new ArrayList();
            for (Object temp : (List) obj) {
                if (isStringType(temp.getClass())) {
                    removeList.add(temp);
                } else {
                    fillAllStringField(((List) obj).get(0), fieldName);
                }
            }
            if (!removeList.isEmpty()) {
                ((List) obj).add(fieldName);
                ((List) obj).removeAll(removeList);
            }
            return;
        }
        if (clazz.isEnum() || clazz.isPrimitive() || isDecorationType(clazz)) {
            return;
        }
        if (clazz.getName().startsWith("java") || clazz.getName().startsWith("javax") || clazz.getName().startsWith("com.sun")) {
            return;
        }
        List<Field> fieldList = fetchAllFieldList(clazz);
        for (Field field : fieldList) {
            try {
                field.setAccessible(true);
                if (String.class.isAssignableFrom(field.getType())) {
                    field.set(obj, field.getName());
                } else {
                    fillAllStringField(field.get(obj), field.getName());
                }
            } catch (Exception e) {
                throw e;
            } finally {
                field.setAccessible(false);
            }
        }
    }

    private boolean isStringType(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    private List<Field> fetchAllFieldList(Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        Field[] thisFieldArray = clazz.getDeclaredFields();
        if (thisFieldArray != null && thisFieldArray.length != 0) {
        	for(Field field : thisFieldArray) {
        		int modifier = field.getModifiers();
        		
        		if(Modifier.isFinal(modifier)) {
            		continue;
            	}
        		
        		fieldList.add(field);
        	}
        }

        Class superClass = clazz.getSuperclass();
        if (superClass != null && !StringUtils.equals(superClass.getName(), "java.lang.Object")) {
            fieldList.addAll(fetchAllFieldList(superClass));
        }
        return fieldList;
    }

    private boolean checkMatch(String paramStr) {
        return StringUtils.countMatches(paramStr, "<") == StringUtils.countMatches(paramStr, ">");
    }

    private boolean isPrimitiveType(String type) {
        return StringUtils.indexOf(type, ".") == -1 && StringUtils.equals(type, StringUtils.lowerCase(type));
    }

    private static boolean isVariableType(String type) {
        return StringUtils.indexOf(type, ".") == -1 && StringUtils.equals(type, StringUtils.upperCase(type)) && StringUtils.length(type) == 1;
    }

    private boolean isArrayType(String type) {
        return StringUtils.contains(type, "[") && StringUtils.contains(type, "]");
    }

    private boolean isParameterType(String type) {
        return StringUtils.contains(type, "<") && StringUtils.contains(type, ">");
    }

    private boolean isSimpleType(String type) {
        return type.matches("^\\w+[\\.\\w]*\\.\\w+$");
    }

    private boolean isDecorationType(Class clazz) {
        boolean flag = false;
        if (Boolean.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Character.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Byte.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Short.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Integer.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Long.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Float.class.isAssignableFrom(clazz)) {
            flag = true;
        } else if (Double.class.isAssignableFrom(clazz)) {
            flag = true;
        }
        return flag;
    }

    private String findMappingResult(String className) {
        if (StringUtils.equals(className, "java.util.Map")) {
            return "java.util.HashMap";
        }
        if (StringUtils.equals(className, "java.util.List")) {
            return "java.util.ArrayList";
        }
        if (StringUtils.equals(className, "java.util.Set")) {
            return "java.util.HashSet";
        }
        if (StringUtils.equals(className, "boolean")) {
            return "java.lang.Boolean";
        }
        if (StringUtils.equals(className, "char")) {
            return "java.lang.Character";
        }
        if (StringUtils.equals(className, "byte")) {
            return "java.lang.Byte";
        }
        if (StringUtils.equals(className, "short")) {
            return "java.lang.Short";
        }
        if (StringUtils.equals(className, "int")) {
            return "java.lang.Integer";
        }
        if (StringUtils.equals(className, "long")) {
            return "java.lang.Long";
        }
        if (StringUtils.equals(className, "float")) {
            return "java.lang.Float";
        }
        if (StringUtils.equals(className, "double")) {
            return "java.lang.Double";
        }
        return className;
    }

    public boolean isTestRun() {
        return testRun;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setTestRun(boolean testRun) {
        this.testRun = testRun;
    }

    public List<String> getParamNameList() {
        if (paramNameList == null) {
            synchronized (this) {
                if (paramNameList == null) {
                    paramNameList = new ArrayList<String>();
                }
            }
        }
        return paramNameList;
    }

    public Map<String, String> getDefaultValueMap() {
        if (defaultValueMap == null) {
            synchronized (this) {
                if (defaultValueMap == null) {
                    defaultValueMap = new HashMap<String, String>();
                }
            }
        }
        return defaultValueMap;
    }

    public Map<String, String> getTestValueMap() {
        if (testValueMap == null) {
            synchronized (this) {
                if (testValueMap == null) {
                    testValueMap = new HashMap<String, String>();
                }
            }
        }
        return testValueMap;
    }

    public Map<String, String> getTestResultMap() {
        if (testResultMap == null) {
            synchronized (this) {
                if (testResultMap == null) {
                    testResultMap = new HashMap<String, String>();
                }
            }
        }
        return testResultMap;
    }

    @Override
    public String toString() {
        return "ParamTestRunner{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramNameList=" + paramNameList +
                ", beanId='" + beanId + '\'' +
                ", defaultValueMap=" + defaultValueMap +
                ", testRun=" + testRun +
                ", testValueMap=" + testValueMap +
                ", testResultMap=" + testResultMap +
                '}';
    }
}