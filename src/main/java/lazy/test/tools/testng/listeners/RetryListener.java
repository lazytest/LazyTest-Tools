/**
* @Title: RetryListener.java
* @date 2016年4月18日
*/
package lazy.test.tools.testng.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.ITestAnnotation;
import org.testng.collections.Lists;

public class RetryListener extends TestListenerAdapter implements IAnnotationTransformer{
	private static final Logger logger = LoggerFactory.getLogger(RetryListener.class);
	private static List<ITestResult> retryTestResults = Lists.newArrayList();
    @SuppressWarnings("rawtypes")
	@Override
    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor, Method method) {
        //modify annotation to set retry analyzer
        iTestAnnotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
    
    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        //tag success cases
        List<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
        Set<Integer> passedHashCodes = new HashSet<Integer>();
        for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
            passedHashCodes.add(hashCode(passedTest));
        }
        //tag retry cases
        Set<Integer> failedHashCodes = new HashSet<Integer>();
        for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
            int failedHashCode = hashCode(failedTest);
            if (failedHashCodes.contains(failedHashCode) || passedHashCodes.contains(failedHashCode)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedHashCodes.add(failedHashCode);
            }
        }
        Set<Integer> skippedHashCodes = new HashSet<Integer>();
        for (ITestResult skippedTest : testContext.getSkippedTests().getAllResults()) {
            int skippedHashCode = hashCode(skippedTest);
            if (failedHashCodes.contains(skippedHashCode) || skippedHashCodes.contains(skippedHashCode) || passedHashCodes.contains(skippedHashCode)) {
                testsToBeRemoved.add(skippedTest);
            } else {
            	skippedHashCodes.add(skippedHashCode);
            }
        }
        //remove all retry cases
        for (Iterator<ITestResult> iterator =
             testContext.getFailedTests().getAllResults().iterator(); iterator.hasNext();) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
            	retryTestResults.add(testResult);
                logger.debug("Remove repeat fail test: " + testResult.getTestClass().getName()+"#"+testResult.getName());
                iterator.remove();
            }
        }
        for (Iterator<ITestResult> iterator =
                testContext.getSkippedTests().getAllResults().iterator(); iterator.hasNext();) {
               ITestResult testResult = iterator.next();
               if (testsToBeRemoved.contains(testResult)) {
               	retryTestResults.add(testResult);
                   logger.debug("Remove repeat skipped test: " + testResult.getTestClass().getName()+"#"+testResult.getName());
                   iterator.remove();
               }
           }
    }

    /**
     * 根据class.hashCode + method.hashCode + parameters.hashCode判断重跑case
     * @param result
     * @return method hashcode
     */
    private int hashCode(ITestResult result) {
        int id = result.getTestClass().hashCode();
        id = id + result.getMethod().hashCode();
        id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
    }
    
    public static List<ITestResult> getRetryTestResults(){
    	return retryTestResults;
    }
}