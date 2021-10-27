package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.googlecode.cqengine.attribute.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.cqengine.query.QueryFactory.nullableAttribute;
import static org.assertj.core.api.Assertions.assertThat;

public class AttributesTest {

    @Test
    public void testGetString() {
        SomeEntity e = getEntity();
        Attribute stringAttr = nullableAttribute("stringField", SomeEntity::getStringField);
        assertThat(Attributes.getString(stringAttr, e)).isEqualTo(e.getStringField());
    }

    @Test
    public void testGetInt() {
        SomeEntity e = getEntity();
        Attribute intAttr = nullableAttribute("intField", SomeEntity::getIntField);
        assertThat(Attributes.getInt(intAttr, e)).isEqualTo(e.getIntField());
    }

    @Test
    public void testGetDouble() {
        SomeEntity e = getEntity();
        Attribute doubleAttr = nullableAttribute("doubleField", SomeEntity::getDoubleField);
        assertThat(Attributes.getDouble(doubleAttr, e)).isEqualTo(e.getDoubleField());
    }

    @Test
    public void testGetDate() {
        SomeEntity e = getEntity();
        Attribute dateAttr = nullableAttribute("dateField", SomeEntity::getDateField);
        assertThat(Attributes.getDate(dateAttr, e)).isEqualTo(e.getDateField());
    }

    @Test
    public void testGetEmptyValueWhenGroupByOptionIsNull() {
        ChartGroupByOptions.GroupByOptionAndParams groupByOptionAndParams = new ChartGroupByOptions.GroupByOptionAndParams();
        assertThat((String) Attributes.get(groupByOptionAndParams, getEntity())).isEqualTo(Attributes.DEFAULT_EMPTY_VALUE);
    }

    @AllArgsConstructor
    @Data
    private static class SomeEntity {
        private String stringField;
        private int intField;
        private Date dateField;
        private double doubleField;
    }

    private SomeEntity getEntity() {
        return new SomeEntity("str", 2, new Date(5 * 24 * 60 * 60 * 1000), 3.14);
    }

    private SomeEntity getAnotherEntity() {
        return new SomeEntity("str2", 3, new Date(5 * 24 * 60 * 60 * 1000), 5.14);
    }
}
