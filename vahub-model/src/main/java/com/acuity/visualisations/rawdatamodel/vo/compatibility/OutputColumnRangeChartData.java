package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputColumnRangeChartData implements Serializable {

    private List<String> categories = new ArrayList<>();
    private List<OutputColumnRangeChartEntry> data = new ArrayList<>();
    private List<OutputMarkEntry> diagnosisDates = new ArrayList<>();
    private List<OutputMarkEntry> progressionDates = new ArrayList<>();

}
