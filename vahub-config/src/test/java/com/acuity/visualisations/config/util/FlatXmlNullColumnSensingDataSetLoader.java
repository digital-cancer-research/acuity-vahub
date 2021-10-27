package com.acuity.visualisations.config.util;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * Extend the FlatXmlDataSetLoader to enable column sensing
 */
public class FlatXmlNullColumnSensingDataSetLoader extends AbstractDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

        builder.setColumnSensing(true);

        try (InputStream inputStream = resource.getInputStream()) {

            IDataSet dataSet =  builder.build(inputStream);
            ReplacementDataSet nullReplacementDataSet = new ReplacementDataSet(dataSet);
            nullReplacementDataSet.addReplacementObject("[NULL]", null);

            return nullReplacementDataSet;
        }
    }
}
