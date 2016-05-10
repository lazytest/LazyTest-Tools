/**
* @Title: ProgressTracker.java
* @date 2016年4月18日
*/
package lazy.test.tools.testng.listeners;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class ProgressTracker implements IInvokedMethodListener,ISuiteListener{
    private final static Logger logger = LoggerFactory.getLogger(ProgressTracker.class);

    private long startTime = 0;

    private int totalExecuted = 0;

    public static int totalRun = 0;
    
    private Set<IInvokedMethod> exceptionInvokedMethods = new HashSet<IInvokedMethod>();
    private static Set<ITestResult> exceptionTestRuselts = new HashSet<ITestResult>();
    private Set<String> ranMethod = new HashSet<String>();

    private final static int DEFAULT_WARN_TIME = 20;

    private static int warnTimeFromConfig = 0;

    static{
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle("testng");
        } catch (Exception e) {
            logger.error("No testng.properties was found in your classpath,use default warn time value = ["
                    + DEFAULT_WARN_TIME + "].");
        }
        if (null == resourceBundle || resourceBundle.getString("com.jdfinance.testng.warntime") == null) {
            warnTimeFromConfig = DEFAULT_WARN_TIME;
        } else {
            try {
                int warnTime = Integer.parseInt(resourceBundle.getString("com.jdfinance.testng.warntime"));
                warnTimeFromConfig = warnTime;
            } catch (NumberFormatException e) {
                logger.error("Error occured when parse value,use default warn time value = [" + DEFAULT_WARN_TIME + "].");
                warnTimeFromConfig = DEFAULT_WARN_TIME;
            }
        }
    }

    @Override
    public synchronized void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (invokedMethod.isTestMethod()) {
            ITestNGMethod m = invokedMethod.getTestMethod();
            String methodName = m.getConstructorOrMethod().getName();
            String className = m.getTestClass().getRealClass().getName();
            logger.info(String.format("[Begin   ]>>>> [%s#%s]\n",className,methodName));
            if (startTime == 0) {
                startTime = Calendar.getInstance().getTimeInMillis();
            }
            ++totalExecuted;
        }
    }

    @Override
    public synchronized void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (invokedMethod.isTestMethod()) {

            ITestNGMethod m = invokedMethod.getTestMethod();
            String methodName = m.getConstructorOrMethod().getName();
            String className = m.getTestClass().getRealClass().getName();
            String storeKey = className + "#" + methodName;
            int status = testResult.getStatus();
            String statusText = "Unknown";
            switch (status) {
                case ITestResult.FAILURE:
                    statusText = "Failed";
                    break;
                case ITestResult.SUCCESS:
                    statusText = "Passed";
                    break;
                case ITestResult.SKIP:
                    statusText = "Skipped";
                    break;
            }

            // total use time this run
            long elapsedTime = (Calendar.getInstance().getTimeInMillis() - startTime) / 1000;
            // ms
            long elapsedMs = (Calendar.getInstance().getTimeInMillis() - startTime) % 1000;
            // count every case elapse time
            long thisElapsedTime = (Calendar.getInstance().getTimeInMillis() - testResult.getStartMillis()) / 1000;
            // ms of every case
            long thisElapsedMs = (Calendar.getInstance().getTimeInMillis() - testResult.getStartMillis()) % 1000;
            // if one case run time bigger than the configuration time and never run before,then mark it
            if ((thisElapsedTime >= warnTimeFromConfig) && !ranMethod.contains(storeKey)) {
            	exceptionInvokedMethods.add(invokedMethod);
            	exceptionTestRuselts.add(testResult);
            }
            // total run added when one case run many time
            if (ranMethod.contains(storeKey)) {
                ++totalRun;
            } else {
                ranMethod.add(storeKey);
            }
            // remain case count
            int remainingTestCount = totalRun - totalExecuted;
            // estimate remain time,this may not correctly because it's average time.
            long remainingTime = (elapsedTime / totalExecuted) * remainingTestCount;

            logger.info(String.format("[Progress]>>>> [%s (%s/%s)],Total Elapsed Time:{%s::%s},Estimated Time Remaining:{%s}\n",
                    countProgressPersentStr(totalExecuted, totalRun),totalExecuted,totalRun,timeToString(elapsedTime),
                    toThreeDigitsStr(elapsedMs),timeToString(remainingTime)));
            if(ITestResult.SUCCESS == testResult.getStatus()){
            	  logger.info(String.format("[Finish  ]>>>> [%s#%s],Elapsed Time:{%s::%s},[%s]\n",className,methodName,
                          timeToString(thisElapsedTime),toThreeDigitsStr(thisElapsedMs),statusText));
            }else{
            	 logger.error(String.format("[Finish  ]>>>> [%s#%s],Elapsed Time:{%s::%s},[%s]\nThrowable message:{%s}",className,methodName,
                         timeToString(thisElapsedTime),toThreeDigitsStr(thisElapsedMs),statusText,testResult.getThrowable().getMessage()));
            }
          
        }
    }

    @Override
    public void onStart(ISuite suite) {
        //get all @Test annotation method
        List<ITestNGMethod> iTestNGMethods = suite.getAllMethods();
       /* for (ITestNGMethod iTestNGMethod:iTestNGMethods) {
            //add retry analyzer to every method
            iTestNGMethod.setRetryAnalyzer(new RetryAnalyzer());
        }*/
        //tell progressTracker the total case to be run
        ProgressTracker.totalRun = iTestNGMethods.size();
    }

    @Override
    public void onFinish(ISuite suite) {
        //print all method that elapse time more than configuration time
        if(exceptionInvokedMethods.size()>0){
            logger.error("There are cases elapsed time more than [" + ProgressTracker.getWarnTime() + "](sec).");
            for (IInvokedMethod invokedMethod : exceptionInvokedMethods) {
                ITestNGMethod testNGMethod =  invokedMethod.getTestMethod();
                String methodName = testNGMethod.getConstructorOrMethod().getName();
                String className = testNGMethod.getTestClass().getRealClass().getName();
                String fullMethodName = className + "#" + methodName;
                ITestResult testResult = invokedMethod.getTestResult();
                int status = testResult.getStatus();
                String statusText = "Unknown";
                switch (status) {
                    case ITestResult.FAILURE:
                        statusText = "Failed";
                        break;
                    case ITestResult.SUCCESS:
                        statusText = "Passed";
                        break;
                    case ITestResult.SKIP:
                        statusText = "Skipped";
                        break;
                }
                logger.error(fullMethodName + "\t[" + statusText+"]\t["+ (testResult.getEndMillis()-testResult.getStartMillis())/1000+"](sec)");
            }
        }
    }
    
    private String countProgressPersentStr(long executedTestCount, long totalTestCount) {
        return Math.round((double) executedTestCount * 100 / (double) totalTestCount) + "%";
    }

    private String timeToString(long valueInSeconds) {
        if (valueInSeconds < 0)
            valueInSeconds = 0;// avoid return value under zero
        long hours = valueInSeconds / 3600;
        valueInSeconds = valueInSeconds % 3600;

        long minutes = valueInSeconds / 60;
        valueInSeconds = valueInSeconds % 60;

        return toTwoDigitsStr(hours) + ":" + toTwoDigitsStr(minutes) + ":" + toTwoDigitsStr(valueInSeconds);
    }

    private String toTwoDigitsStr(long value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    private String toThreeDigitsStr(long value) {
        if (value < 10) {
            return "00" + value;
        } else if (10 <= value && value < 100) {
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public static int getWarnTime() {
        return warnTimeFromConfig;
    }
    
	public static Set<ITestResult> getExceptionTestRuselts() {
		return exceptionTestRuselts;
	}

    
    
}
