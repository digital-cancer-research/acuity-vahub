package com.acuity.visualisations.rawdatamodel.service.timeline.data.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@AllArgsConstructor
public class PatientDataTests implements Serializable {
    private String testName;
    private List<PatientDataTestsDetails> details;
}
