package com.acuity.visualisations.common.cache;

import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DatasetsRequest;

/**
 * Finds datasets object
 *
 * @author ksnd199
 */
public final class DatasetsFinder {

    private DatasetsFinder() {
    }
    
    /*
     * Generate a DatasetsKey based on the specified parameters.
     */
    public static Datasets findDatasetsObject(Object... params) {
        for (Object arg : params) {
            if (arg != null) {
                if (arg instanceof DatasetsRequest) {
                    DatasetsRequest dsr = (DatasetsRequest) arg;
                    return dsr.getDatasetsObject();
                }

                if (arg instanceof Datasets) {
                    Datasets ds = (Datasets) arg;
                    return ds;
                }

                if (arg instanceof Dataset) {
                    Dataset ds = (Dataset) arg;
                    return new Datasets(ds);
                }
            }
        }
        return null;
    }

    public static boolean hasDatasets(Object... elements) {
        return findDatasetsObject(elements) != null;
    }
}
