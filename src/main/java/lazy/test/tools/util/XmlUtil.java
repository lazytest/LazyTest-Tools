package lazy.test.tools.util;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * <b>工具说明：</b>提供从类导出到XML、从List导出到XML、从Map导出到XML，以及从XML导入到类、List、Map的工具 </br>
 * <b>使用说明：</b>推荐使用导出工具导出到XML，再稍做修改，来构造预期结果文件</br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class XmlUtil {

    private static Logger LOG = LoggerFactory.getLogger(XmlUtil.class);

    private static XStream xStream = new XStream();


    /**
     * <b>方法说明：</b>从指定路径获取Xml数据，以List形式导入，文件后缀必须为".xml" </br>
     * 
     * @param filePath xml文件的绝对路径，必传 </br>
     * 
     * @return ArrayList
     */
    public static List readListFromXml(String filePath) {
        File file = FileUtil.getFileByPath(filePath);
        if (!file.exists() || !file.getName().endsWith(".xml")) {
            LOG.error("Xml不存在，或不以.xml结尾");
            return  Collections.EMPTY_LIST;
        }
        return  (ArrayList)xStream.fromXML(file);
    }
    /**
     * <b>方法说明：</b>以XML形式，导出List到指定路径" </br>
     * 
     * @param filePath  xml文件的绝对路径</br>
     * 
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public static boolean writeListToXml(List list, String filePath) {
        File file = FileUtil.createFileByPath(filePath);
        if (!file.exists()) {
            filePath = System.getProperty("user.dir") + File.separator + filePath;
            FileUtil.createFileByPath(filePath);
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            xStream.toXML(list, out);
        } catch (FileNotFoundException e) {
            LOG.error("写入List到Xml出错e ", e);
            return false;
        }
        return true;
    }
    /**
     * <b>方法说明：</b>从指路径获取Xml数据，以Object形式导入，文件后缀必须为".xml" </br>
     * 
     * @param filePath  xml文件的绝对路径</br>
     * 
     * @return Object
     */
    public static Object readObjectFromXml(String filePath) {
        File file = FileUtil.getFileByPath(filePath);
        if (!file.exists() || !file.getName().endsWith(".xml")) {
            LOG.error("Xml不存在，或不以.xml结尾");
            return null;
        }
        return xStream.fromXML(file);

    }


    /**
     * <b>方法说明：</b>以XML形式，导出Object到指定路径" </br>
     * 
     * @param object  写入的对象</br>
     * @param filePath  xml文件的绝对路径</br>
     * 
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public static boolean writeObjectToXml(Object object, String filePath) {
        File file = FileUtil.createFileByPath(filePath);
        if (!file.exists()) {
            filePath = System.getProperty("user.dir") + File.separator + filePath;
            FileUtil.createFileByPath(filePath);
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            xStream.toXML(object, out);
        } catch (FileNotFoundException e) {
            LOG.error("写入object到Xml出错e ", e);
            return false;
        }
        return true;
    }
    /**
     * <b>方法说明：</b>从指路径获取Xml数据，以HashMap形式导入，文件后缀必须为".xml" </br>
     * 
     * @param filePath  兑取的路径</br>
     * 
     * @return HashMap
     */
    public static Map readMapFromXml(String filePath) {
        File file = FileUtil.getFileByPath(filePath);
        if (!file.exists() || !file.getName().endsWith(".xml")) {
            LOG.error("Xml不存在，或不以.xml结尾");
            return Collections.emptyMap();
        }
        return (HashMap) xStream.fromXML(file);
    }
    /**
     * <b>方法说明：</b>将map对象导出到Xml</br>
     * 
     * @param filePath  导出的路径</br>
     * 
     * @return boolean
     *         <ul>
     *         <li>true 执行成功</li>
     *         <li>false 执行失败</li>
     *         </ul>
     */
    public static boolean writeMapToXml(Map map, String filePath) {
        File file = FileUtil.createFileByPath(filePath);
        if (!file.exists()) {
            filePath = System.getProperty("user.dir") + File.separator + filePath;
            FileUtil.createFileByPath(filePath);
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            xStream.toXML(map, out);
        } catch (FileNotFoundException e) {
            LOG.error("写入map到Xml出错e ", e);
            return false;
        }
        return true;
    }

    /**
     * <b>方法说明：</b>从String读取Xml </br>
     * 
     * @param xmlString  xml字符串</br>
     * 
     * @return Object
     */
    public static Object readObjectFromXmlString(String xmlString) {
        return xStream.fromXML(xmlString);

    }

    /**
     * <b>方法说明：</b>从Object获取Xml字符串 </br>
     * 
     * @param object  要转换的对象</br>
     * 
     * @return String
     */
    public static String readObjectFromXmlString(Object object) {
        return xStream.toXML(object);

    }
}

