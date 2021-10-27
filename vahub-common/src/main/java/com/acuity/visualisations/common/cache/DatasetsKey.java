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
