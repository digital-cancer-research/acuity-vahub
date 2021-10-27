package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.BoxPlotOutlier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class OutputBoxPlotOutlier implements Serializable {
    private String x;
    private Double outlierValue;
    private String subjectId;

    public static OutputBoxPlotOutlier of(BoxPlotOutlier outlier, String x) {
        Validate.notNull(outlier);
        return new OutputBoxPlotOutlier(x, outlier.getOutlierValue(), outlier.getSubjectId());
    }
}
