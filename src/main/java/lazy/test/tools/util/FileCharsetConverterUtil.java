package lazy.test.tools.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * <b>Date: 15-12-24</b></br>
 * <b>Time: 下午3:04</b></br>
 */
public class FileCharsetConverterUtil {


    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param fileName
     *            要转换的文件
     * @param fromCharsetName
     *            源文件的编码
     * @param toCharsetName
     *            要转换的编码
     *            
     * @throws Exception
     */
    public static void convert(String fileName, String fromCharsetName,String toCharsetName) throws Exception {
        convert(new File(fileName), fromCharsetName, toCharsetName, null);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param file
     *            要转换的文件或目录
     * @param fromCharsetName
     *            源文件的编码
     * @param toCharsetName
     *            要转换的编码
     *            
     * @throws Exception
     */
    public static void convert(File file, String fromCharsetName,String toCharsetName) throws Exception {
        convert(file, fromCharsetName, toCharsetName, null);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param fileName
     *            要转换的文件或目录
     * @param fromCharsetName
     *            源文件的编码
     * @param toCharsetName
     *            要转换的编码
     * @param filter
     *            文件名过滤器
     *            
     * @throws Exception
     */
    public static void convert(String fileName, String fromCharsetName,String toCharsetName, FilenameFilter filter) throws Exception {
        convert(new File(fileName), fromCharsetName, toCharsetName, filter);
    }

    /**
     * 把指定文件或目录转换成指定的编码
     *
     * @param file
     *            要转换的文件或目录
     * @param fromCharsetName
     *            源文件的编码
     * @param toCharsetName
     *            要转换的编码
     * @param filter
     *            文件名过滤器
     *            
     * @throws Exception
     */
    public static void convert(File file, String fromCharsetName, String toCharsetName, FilenameFilter filter) throws Exception {
        if (file.isDirectory()) {
            File[] fileList = null;
            if (filter == null) {
                fileList = file.listFiles();
            } else {
                fileList = file.listFiles(filter);
            }
            for (File f : fileList) {
                convert(f, fromCharsetName, toCharsetName, filter);
            }
        } else {
            if (filter == null|| filter.accept(file.getParentFile(), file.getName())) {
                String fileContent = getFileContentFromCharset(file,fromCharsetName);
                saveFile2Charset(file, toCharsetName, fileContent);
            }
        }
    }

    /**
     * 以指定编码方式读取文件，返回文件内容
     *
     * @param file
     *            要转换的文件
     * @param fromCharsetName
     *            源文件的编码
     *            
     * @return String 文件内容
     * 
     * @throws Exception
     */
    public static String getFileContentFromCharset(File file,String fromCharsetName) throws Exception {
        if (!Charset.isSupported(fromCharsetName)) {
            throw new UnsupportedCharsetException(fromCharsetName);
        }
        InputStream inputStream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(inputStream,
                fromCharsetName);
        char[] chs = new char[(int) file.length()];
        reader.read(chs);
        String str = new String(chs).trim();
        reader.close();
        return str;
    }

    /**
     * 以指定编码方式写文本文件，存在会覆盖
     *
     * @param file
     *            要写入的文件
     * @param toCharsetName
     *            要转换的编码
     * @param content
     *            文件内容
     *            
     * @throws Exception
     */
    public static void saveFile2Charset(File file, String toCharsetName,  String content) throws Exception {
        if (!Charset.isSupported(toCharsetName)) {
            throw new UnsupportedCharsetException(toCharsetName);
        }
        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outWrite = new OutputStreamWriter(outputStream,
                toCharsetName);
        outWrite.write(content);
        outWrite.close();
    }

    /**
     * 判断指定文件是否是UTF8编码
     * 
     * @param String fileName 文件名（包含路径）
     * 
     * @return boolean true - UTF8, false - 非UTF8
     */
    public static boolean isUtf8(String fileName) {
        byte[] first3Bytes = new byte[3];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1){
                return false;
            }else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB&& first3Bytes[2] == (byte) 0xBF) {
                return true;
            }
            bis.reset();
            int loc = 0;
            while ((read = bis.read()) != -1) {
                loc++;
                if (read >= 0xF0)
                    break;
                if (0x80 <= read && read <= 0xBF)
                    break;
                if (0xC0 <= read && read <= 0xDF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF)// 双字节 (0xC0 - 0xDF)
                        continue;
                    else
                        break;
                } else if (0xE0 <= read && read <= 0xEF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            return true;
                        } else
                            break;
                    } else
                        break;
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //    public static void main(String[] args) throws Exception {
//        convert("D:\\cd_auto\\AutoTestUtil\\src\\test\\java\\resources\\data\\auto_test.csv",
//                "UTF-8", "GBK");
//    }
}
