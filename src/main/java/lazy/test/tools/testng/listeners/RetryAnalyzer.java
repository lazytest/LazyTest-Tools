/**
* @Title: RetryAnalyzer.java
* @date 2016年4月18日
*/
package lazy.test.tools.testng.listeners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class RetryAnalyzer implements IRetryAnalyzer {
    private final static Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 1;
    //
    private static int maxRetryCount =  0;
    //default 0 means it will not retry if there is no config or parse error.
    private final static int DEFAULT_RETRY_COUNT = 0;
    private static boolean executeAfterClass = false;
    private static boolean executeBeforeClass = false;
    private static Set<Class<?>> classSet = new HashSet<Class<?>>();
    static{
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("testng");
        } catch (Exception e) {
            logger.error("No testng.properties was found in your classpath,use default retrytime = ["
                    + DEFAULT_RETRY_COUNT + "].");
        }
        if (null == resourceBundle) {
            maxRetryCount = DEFAULT_RETRY_COUNT;
            logger.error("No testng.properties was found in your classpath,use default retrytime = ["
                    + DEFAULT_RETRY_COUNT + "].");
        } else {
            try {
                int retrytime = Integer.parseInt(resourceBundle.getString("com.jdfinance.testng.retrytime"));
                maxRetryCount = retrytime;
            } catch (NumberFormatException e) {
                logger.error("Error occured when parse value,use default retrytime = [" + DEFAULT_RETRY_COUNT + "].");
                maxRetryCount = DEFAULT_RETRY_COUNT;
            }
            executeAfterClass = Boolean.valueOf(resourceBundle.getString("com.jdfinance.testng.executeAfterClass"));
            executeBeforeClass = Boolean.valueOf(resourceBundle.getString("com.jdfinance.testng.executeBeforeClass"));
        }
        // 对于TestNG或者JUnit使用spring容器的，父类止于该类，否则止于Object
        classSet.add(AbstractTestNGSpringContextTests.class);
        classSet.add(AbstractJUnit4SpringContextTests.class);
        classSet.add(Object.class);
    }

    public boolean retry(ITestResult iTestResult) {
        if (retryCount <= maxRetryCount) {
            /* Use this way to remove the testResult will be incorrect when one case retry more than one time
             * i don't know why,so i choose another way to solve it. 
             * */
        	//if retry,remove the testResult just run to ensure the result correctly.
        	iTestResult.getTestContext().getFailedTests().getAllResults().remove(iTestResult);
            logger.warn(String.format("[Retry   ]>>>> [%s#%s] - retrying %s times.",
                    iTestResult.getTestClass().getRealClass().getName(),iTestResult.getName(),retryCount));
            Reporter.setCurrentTestResult(iTestResult);
            retryCount++;
            //the instance of the test method
            Object obj = iTestResult.getInstance();
            //if need,Execute @AfterClass method to clear data
            if(executeAfterClass){
                invoke(getAnnotationMethod(iTestResult,AfterClass.class),obj);
            }
            //if need,Execute @BeforeClass method to prepare data
            if(executeBeforeClass){
                invoke(getAnnotationMethod(iTestResult,BeforeClass.class),obj);
            }
            return true;
        }
        return false;
    }

    /**
     * invoke method 
     * @param methods methods to be invoke
     * @param obj  instance of the method
     */
    private void invoke(Method[] methods,Object obj){
        for (Method method:methods) {
            try {
            	method.setAccessible(true);
                method.invoke(obj);
                logger.info("Successfully invoke method [" +
                        method.getDeclaringClass().getName()+"#"+ method.getName() +"]");
            } catch (IllegalAccessException e) {
                logger.error("Error occured when invoke method.",e);
            } catch (InvocationTargetException e) {
                logger.error("Error occured when invoke method.",e);
            }
        }
    }
    /**
     * get class's @Annotation methods
     * @param testResult
     * @return methods
     */
    private Method[] getAnnotationMethod(ITestResult testResult, Class<? extends Annotation> annotationClass){
        Class<?> testClass = testResult.getTestClass().getRealClass();
        List<Method> methodList = new ArrayList<Method>();
        for(Class<?> clazz = testClass; !classSet.contains(clazz); clazz = clazz.getSuperclass()){
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method:methods) {
                if(method.getAnnotation(annotationClass) != null){
                    methodList.add(method);
                }
            }
        }
        //revere list to ensure parent's beforeClass invoke first
        if(annotationClass.equals(BeforeClass.class)){
        	Collections.reverse(methodList);
        }
        return methodList.toArray(new Method[methodList.size()]);
    }
}
