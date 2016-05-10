package lazy.test.tools.util.asserts;

import lazy.test.tools.util.HtmlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Assert HTML text elements
 * Find by XPATH and assert with text equaling or regular expression matching
 *
 */
public class HtmlAssertUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(HtmlAssertUtil.class);
	
	/**
	 * Assert text equaling from HTML by XPATH and given value
	 * @param html	HTML
	 * @param xPath	XPATH of the targeting element
	 * @param text	Expected value
	 */
	public static void assertEquals(String html, String xPath, String text) {
		String actualValue = HtmlUtil.getHtmlValueByXpath(html, xPath);
		
		logger.info("HTML text assert - expected value : {0}", text);
		logger.info("HTML text assert -  actual  value : {0}", actualValue);
		
		org.testng.Assert.assertEquals(actualValue, text);
	}
	
	/**
	 * Assert regex matching from HTML by XPATH and given regular expression
	 * @param html	HTML
	 * @param xPath	XPATH of the targeting element
	 * @param regEx	Expected regular expression
	 */
	public static void assertRegMatch(String html, String xPath, String regEx) {
		String actualValue = HtmlUtil.getHtmlValueByXpath(html, xPath);
		
		logger.info("HTML text assert - regex  value : {0}", regEx);
		logger.info("HTML text assert - actual value : {0}", actualValue);
		
		org.testng.Assert.assertTrue(actualValue.matches(regEx));
	}
}
