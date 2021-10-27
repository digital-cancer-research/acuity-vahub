/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
