package lazy.test.tools.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;

/**
 * <b>Date: 15-12-11</b></br>
 * <b>Time: 下午4:44</b></br>
 * <b>获取html指定Xpath路径的值</br>
 */
public class HtmlUtil {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtil.class);
    /**
     * <b>方法说明：</b>根据xpath路径获取html指定的值</br>
     *
     * @param html  html字符串 </br>
     * @param xPath xpath路径 </br>
     * 
     * @return String 所需字符串
     */
    public static String getHtmlValueByXpath(String html,String xPath) {
        Document doc2 = null;
        try {
            doc2 = Jsoup.parse(html);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析html出错 e", e);
        }
        return Xsoup.compile(xPath).evaluate(doc2).get();
    }



}
