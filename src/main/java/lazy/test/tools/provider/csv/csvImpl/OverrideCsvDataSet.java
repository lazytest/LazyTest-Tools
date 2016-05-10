package lazy.test.tools.provider.csv.csvImpl;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Date: 15-11-24
 * Time: 上午10:37
 * To change this template use File | Settings | File Templates.
 */
public class OverrideCsvDataSet extends CachedDataSet {
    public OverrideCsvDataSet(File dir) throws DataSetException {
        super(new OverrideCsvProducer(dir));
    }
}
