package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class ChordCalculationObject implements Serializable {
    private String start;
    private String end;
    private int width;
    private Map<String, List<ChordContributor>> contributors;

    public ChordCalculationObject(String start, String end, int width, Map<String, List<ChordContributor>> contributors) {
        this.start = start;
        this.end = end;
        this.width = width;
        this.contributors = new HashMap<>(contributors);
    }

    public static ChordCalculationObject merge(ChordCalculationObject o1, ChordCalculationObject o2) {
        o1.setWidth(o1.getWidth() + o2.getWidth());
        Map<String, List<ChordContributor>> contributors1 = o1.getContributors();
        o2.getContributors().forEach((k, v) -> contributors1.merge(k, v, (v1, v2) -> Stream.of(v1, v2)
                .flatMap(Collection::stream)
                .collect(toList())));
        return o1;
    }

    public enum Attributes implements GroupByOption<ChordCalculationObject> {
        START(EntityAttribute.attribute("START", ChordCalculationObject::getStart)),
        END(EntityAttribute.attribute("END", ChordCalculationObject::getEnd));

        @Getter
        private final EntityAttribute<ChordCalculationObject> attribute;

        Attributes(EntityAttribute<ChordCalculationObject> attribute) {
            this.attribute = attribute;
        }
    }
}
