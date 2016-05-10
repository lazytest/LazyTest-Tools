package lazy.test.tools.util;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.dataset.csv.CsvParser;
import org.dbunit.dataset.csv.CsvParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * <b>工具说明：</b>提供有表头（第一行）的CSV，导入数据的工具 </br>
 * <b>使用说明</b></br>
 * &emsp;&emsp;&emsp;&emsp;&emsp;第一行，为表头，第二行开始为数据 </br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class CsvUtil {

    private static Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    /**
     * <b>方法说明：</b>从指定路径的路径读取CSV的表头</br>
     *           csv文件存储格式:例如
     *          customerId,telephone
     *          "123213123123123","18009991111"
     * @param csvDirectory  csv文件或者文件夹的绝对路径，必传 </br>
     * @return List
     */
    public static List getTableHeaderFromCsv(String csvDirectory) {
        CsvParser csvParser = new CsvParserImpl();
        File file = new File(csvDirectory);
        List list = Collections.EMPTY_LIST;
        if (!file.exists()) {
        	if(!csvDirectory.startsWith("/")) {
        		csvDirectory = "/" + csvDirectory;
        	}
        	
        	file = new File(CsvUtil.class.getResource(csvDirectory).getPath());
        	
        	if (!file.exists()) {
        		return list;
        	}
        }
        try {
            list = csvParser.parse(file);
        } catch (Exception e) {
            logger.error("获取Header时,读取CSV出错,请检查csv格式 e", e);
            e.printStackTrace();
            return list;
        }
        if (list.size() > 0) {
        	List headerArray = (List)list.get(0);
        	
        	for(Object header : headerArray) {
        		header = ((String)header).trim();
        	}
        	
            return headerArray;
        }
        return list;
    }


    /**
     * <b>方法说明：</b>从指定路径的路径获取Csv数据</br>
     *           csv文件存储格式:例如
     *          customerId,telephone
     *          "123213123123123","18009991111"
     * @param csvDirectory  csv文件或者文件夹的绝对路径，必传 </br>
     * @return List
     *         <ul>
     *         <li>成功返回 List<HashMap></li>
     *         <li>失败返回 空List</li>
     *         </ul>
     */
    public static List<Map<String,String>> getDataFromCsv(String csvDirectory) {
        File file = new File(csvDirectory);
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        if (!file.exists()) {
        	if(!csvDirectory.startsWith("/")) {
        		csvDirectory = "/" + csvDirectory;
        	}
        	
        	file = new File(CsvUtil.class.getResource(csvDirectory).getPath());
        	if (!file.exists()) {
        		return resultList;
        	}
        }
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathFile) {
                    return StringUtils.endsWith(pathFile.getName(), ".csv");
                }
            });
            for (File temp : files) {
                List<Map<String,String>> tempList = getDataFromCsv(temp.getAbsolutePath());
                if (!tempList.isEmpty()) {
                    resultList.addAll(tempList);
                }
            }
        }
        if (file.exists() && file.isFile()) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            CSVReader csvReader = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis, "GBK");
                csvReader = new CSVReader(isr);
                List<String[]> result = csvReader.readAll();
                resultList = convertCSVData(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(csvReader);
                IOUtils.closeQuietly(isr);
                IOUtils.closeQuietly(fis);
            }
        }
        return resultList;
    }

    private static List<Map<String, String>> convertCSVData(List<String[]> result) {
        String[] headArray = result.get(0);
        for(int i = 0; i < headArray.length; i++) {
        	headArray[i] = headArray[i].trim(); 
        }
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        for (int i = 1; i < result.size(); i++) {
            Map<String, String> rowMap = new HashMap<String, String>();
            String[] dataRow = result.get(i);
            for (int j = 0; j < headArray.length; j++) {
                rowMap.put(headArray[j], dataRow[j]);
            }
            if (!rowMap.isEmpty()) {
                resultList.add(rowMap);
            }
        }
        return resultList;
    }

}
