package lazy.test.tools.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * <b>工具说明：</b>提供从Property文件加载配置，或修改Property文件中key的值 的工具 </br>
 * <b>使用说明：</b>推荐使用导出工具导出到XML，再稍做修改，来构造预期结果文件</br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class PropertyUtil {
    private static Logger LOG = LoggerFactory.getLogger(PropertyUtil.class);

    /**
     * <b>方法说明：</b>从指定路径加载Property文件 </br>
     * 
     * @param filePath  Property文件的路径</br>
     * 
     * @return Properties 返回加载到的配置
     */
    public synchronized static Properties loadProperties(String filePath) {
        InputStream in = null;
        Properties properties = new Properties();
        try {
            in = new FileInputStream(FileUtil.getFileByPath(filePath));
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }
    /**
     * <b>方法说明：</b>从指定Property文件对象加载Property文件 </br>
     * 
     * @param file  Property文件对象</br>
     * 
     * @return Properties 返回加载到的配置
     */
    public synchronized static Properties loadProperties(File file) {
        InputStream in = null;
        Properties properties = new Properties();
        try {
            in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    /**
     * <b>方法说明：</b>从指定Property文件对象加载Property文件中某个具体配置的value </br>
     * 
     * @param filePath  Property文件的路径</br>
     * @param key  需要获取的配置的key</br>
     * 
     * @return String 返回加载到的某个配置的value
     */
    public synchronized static String readProperties(String key, String filePath) {
        Properties properties = loadProperties(filePath);
        String value = properties.getProperty(key);
        return value;
    }
    /**
     * <b>方法说明：</b>修改并保存指定Property文件对象中某个具体配置的value </br>
     * 
     * @param key  需要获取的配置的key</br>
     * @param value  需要保存的值 </br>
     * 
     * @param filePath  Property文件的路径 </br>
     */
    public synchronized static void storeProperties(String key, String value, String filePath) {
        File file = FileUtil.getFileByPath(filePath);
        LOG.info("properties path: " + file.getAbsolutePath());
        Properties properties = loadProperties(file);
        properties.setProperty(key, value);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            properties.store(fos, "setted key: " + key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
