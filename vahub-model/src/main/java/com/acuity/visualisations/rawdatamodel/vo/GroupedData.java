package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class GroupedData<K extends SubjectAwareWrapper, G> {
    private Map<G, List<Subject>> totalSubjects;
    private Map<G, List<K>> events;

}
