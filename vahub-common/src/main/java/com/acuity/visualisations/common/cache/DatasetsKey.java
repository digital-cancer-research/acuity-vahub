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

import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author ksnd199
 */
public class DatasetsKey implements Serializable {

    private final Object[] params;
    private final int hashCode;
    private Datasets datasets;

    public DatasetsKey(Datasets datasets, Object... elements) {
        Assert.notNull(datasets, "Datasets must not be null");
        if (elements == null || elements.length == 0) {
            elements = new Object[]{datasets};
        }
        this.params = new Object[elements.length];
        System.arraycopy(elements, 0, this.params, 0, elements.length);

        this.datasets = datasets;
        this.hashCode = Arrays.deepHashCode(this.params);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || (obj instanceof DatasetsKey && Arrays.deepEquals(this.params, ((DatasetsKey) obj).params)));
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
    }

    public Datasets getDatasets() {
        return datasets;
    }
}
