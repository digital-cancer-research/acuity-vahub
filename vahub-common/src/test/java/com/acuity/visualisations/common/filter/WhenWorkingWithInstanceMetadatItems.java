package com.acuity.visualisations.common.filter;

import com.acuity.visualisations.common.study.metadata.InstanceMetadata;
import com.acuity.visualisations.common.study.metadata.MetadataItem;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Glen
 */
public class WhenWorkingWithInstanceMetadatItems {

    @Test
    public void shouldBuildAndReadInstanceMetadata() {

        MetadataItem mi = new MetadataItem("aes");
        mi.add("listOfUser", newArrayList("glen", "sam"));
        mi.add("aesList", newArrayList("glen", "sam"));
        mi.addProperty("string1", "string");
        mi.addProperty("hasData", true);
        mi.addProperty("count", 11);

        MetadataItem mil = new MetadataItem("labs");
        mil.add("listOfUser", newArrayList("glen1", "sam1"));
        mil.add("labsList", newArrayList("glen1", "sam1"));

        MetadataItem mib = new MetadataItem("biomarker");
        mib.addProperty("hasData", true);
        mib.addProperty("count", 10);

        InstanceMetadata instanceMetadata = new InstanceMetadata();
        instanceMetadata.add(mi);
        instanceMetadata.add(mil);
        instanceMetadata.add(mib);
        instanceMetadata.addItemProperty("exposure", "spotfireModules", newArrayList("module1", "module3"));
        instanceMetadata.addItemProperty("biomarker", "spotfireModules", newArrayList("module1", "module2"));

        InstanceMetadata readInstanceMetadata = InstanceMetadata.read(instanceMetadata.build());
        String metadataString = instanceMetadata.build();
        System.out.println(metadataString);
        System.out.println(readInstanceMetadata.build());
        assertThatJson(metadataString).node("aes").isPresent();
        assertThatJson(metadataString).node("labs").isPresent();
        assertThatJson(metadataString).node("biomarker").isPresent();
        assertThatJson(metadataString).node("exposure").isPresent();
        assertThatJson(metadataString).node("exposure.spotfireModules").isEqualTo(newArrayList("module1", "module3"));
        assertThatJson(metadataString).node("biomarker.spotfireModules").isEqualTo(newArrayList("module1", "module2"));
        assertThatJson(metadataString).node("biomarker.count").isEqualTo(10);
        assertThatJson(metadataString).node("biomarker.hasData").isEqualTo(true);
        assertThat(readInstanceMetadata).isEqualToComparingFieldByField(instanceMetadata);
    }
}
