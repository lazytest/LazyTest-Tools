package lazy.test.tools.provider.csv.csvImpl;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.common.handlers.IllegalInputCharacterException;
import org.dbunit.dataset.common.handlers.PipelineException;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.csv.CsvParser;
import org.dbunit.dataset.csv.CsvParserException;
import org.dbunit.dataset.csv.CsvParserImpl;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 15-11-24
 * Time: 上午10:32
 */
public class OverrideCsvProducer implements IDataSetProducer {
    private static final Logger logger = LoggerFactory.getLogger(OverrideCsvProducer.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();

    private IDataSetConsumer _consumer = EMPTY_CONSUMER;

    private String _theDirectory;


    public OverrideCsvProducer(File theDirectory) {
        _theDirectory = theDirectory.getAbsolutePath();
    }

    public OverrideCsvProducer(String theDirectory) {
        _theDirectory = theDirectory;
    }

    @Override
    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {
        logger.debug("setConsumer(consumer) - start");
        _consumer = consumer;
    }

    @Override
    public void produce() throws DataSetException {
        logger.debug("produce() - start");

        _consumer.startDataSet();
        try {
            List<String> tableList = getTables(_theDirectory);
            for (String table :tableList) {
                try {
                    produceFromFile(new File(_theDirectory, table + ".csv"));
                } catch (CsvParserException e) {
                    throw new DataSetException("error producing dataset for table '" + table + "'", e);
                } catch (DataSetException e) {
                    throw new DataSetException("error producing dataset for table '" + table + "'", e);
                }

            }
            _consumer.endDataSet();
        } catch (IOException e) {
            throw new DataSetException("error getting list of tables", e);
        }
    }


    private void produceFromFile(File theDataFile) throws DataSetException, CsvParserException {
        logger.debug("produceFromFile(theDataFile={}) - start", theDataFile);

        try {
            CsvParser parser = new CsvParserImpl();
            List readData = parser.parse(theDataFile);
            List readColumns = ((List) readData.get(0));
            Column[] columns = new Column[readColumns.size()];

            for (int i = 0; i < readColumns.size(); i++) {
                columns[i] = new Column((String) readColumns.get(i), DataType.UNKNOWN);
            }
            String tableName =theDataFile.getName().substring(0, theDataFile.getName().indexOf(".csv"));
            if (tableName.contains("#")) {   //支持表名前加"#"功能
                tableName = tableName.substring(tableName.lastIndexOf("#") + 1,tableName.length());
            }
            ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
            _consumer.startTable(metaData);
            for (int i = 1 ; i < readData.size(); i++) {
                List rowList = (List)readData.get(i);
                Object[] row = rowList.toArray();
                for(int col = 0; col < row.length; col++) {
                    row[col] = row[col].equals(CsvDataSetWriter.NULL) ? null : row[col];
                }
                _consumer.row(row);
            }
            _consumer.endTable();
        } catch (PipelineException e) {
            throw new DataSetException(e);
        } catch (IllegalInputCharacterException e) {
            throw new DataSetException(e);
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }
    /**
     * Get a list of tables that this producer will create
     * @return a list of Strings
     * @throws java.io.IOException when IO on the base URL has issues.
     */
    public  List<String> getTables(String _theDir) throws IOException {
        logger.debug("getTables(File={} - start", _theDir);
        List<String> tableNames = new ArrayList<String>();
        File fileDir=new File(_theDir);
        String fileName = fileDir.getName();
        if (fileDir.isFile()&&fileName.endsWith(".csv")) {
                tableNames.add(fileName.substring(0, fileName.lastIndexOf(".")));
            _theDirectory = fileDir.getParent();     //路径是csv文件，需改为存csv文件的文件夹路径
        }else if(fileDir.isDirectory()) {
            File[] tempList = fileDir.listFiles();
            if (tempList!=null&&tempList.length > 0) {
                for (File file : tempList) {
                    if (file.isFile()) {
                        String name = file.getName();
                        if (name.endsWith(".csv")){
                                tableNames.add(name.substring(0, name.lastIndexOf(".")));
                        }
                    }
                }
            }
        }
        return tableNames;
    }

}
