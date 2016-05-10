package lazy.test.tools.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * <b>工具说明：</b>提供文件的操作工能,创建文件,查找文件 </br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class FileUtil {
    private static Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * <b>方法说明：</b>按文件路径创建文件对象，首先尝试从文件系统路径查找，找不到从ClassPath查找</br>
     *
     * @param filePath 文件路径
     * 
     * @return File 文件对象
     */
    public static File getFileByPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {// 不存在尝试从ClassPath加载
            file = getFileFromClassPath(filePath);
        } else {
            LOG.debug("# getFileByPath({})={}", filePath, file.getAbsoluteFile());
        }
        return file;
    }

    /**
     * <b>方法说明：</b>从classpath中查找文件
     * 
     * @param filePath 文件路径
     * 
     * @return File 文件对象
     */
    public static File getFileFromClassPath(String filePath) {
        File file = new File(filePath);
        URL url = ClassLoader.getSystemResource(filePath);
        if (url != null) {
            file = new File(url.getFile());
            LOG.debug("# getFileFromClassPath({})={}", filePath, file.getAbsoluteFile());
        } else {
            LOG.warn("# getFileFromClassPath({})...not exist!", filePath);
        }
        return file;
    }

    /**
     * <b>方法说明：</b>创建不存在的文件
     * 
     * @param filePath 文件路径
     * 
     * @return 创建好的文件
     */
    public static File createFileByPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File dir = new File(file.getParent());
                if (!dir.exists())
                    dir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
