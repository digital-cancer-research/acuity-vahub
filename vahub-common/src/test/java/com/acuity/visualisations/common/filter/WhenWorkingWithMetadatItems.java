package com.acuity.visualisations.common.filter;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 *
 * @author Glen
 */
public class WhenWorkingWithMetadatItems {

    @Test
    public void shouldBuildAndReadMetadataItem() {

        MetadataItem mi = new MetadataItem("labs");
        mi.add("listOfUser", newArrayList("glen", "sam"));
        mi.add("labsList", newArrayList("glen", "sam"));
        mi.addProperty("string1", "string");
        mi.addProperty("hasData", true);
        mi.addProperty("count", 11);

        MetadataItem readItem = MetadataItem.read(mi.build());

        assertThat(readItem).isEqualToComparingFieldByField(mi);
    }
}
