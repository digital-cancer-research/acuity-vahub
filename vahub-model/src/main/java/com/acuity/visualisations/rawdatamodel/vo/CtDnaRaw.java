package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_THREE_DECIMAL_PLACES;
import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_TWO_DECIMAL_PLACES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@AcuityEntity(version = 9)
public class CtDnaRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(columnName = "sampleDate", order = 1, displayName = "Sample date")
    private Date sampleDate;
    @Column(columnName = "visitNumber", order = 2, displayName = "Visit number")
    private Double visitNumber;
    @Column(columnName = "visitName", order = 3, displayName = "Visit name")
    private String visitName;
    @Column(columnName = "gene", order = 4, displayName = "Gene")
    private String gene;
    private String mutation;
    // Reported Variant Allele Frequency (VAF)
    private Double reportedVaf;
    private Double reportedVafCalculated;
    private Double reportedVafCalculatedPercent;
    private Double reportedVafPercent;

    @Column(columnName = "mutation", order = 5, displayName = "Mutation")
    public String normalizedMutation() {
        return isNoMutationsDetected() ? NO_MUTATIONS_DETECTED : mutation;
    }

    @Column(columnName = "trackedMutation", order = 6, displayName = "Tracked mutation")
    private String trackedMutation;

    @Column(columnName = "reportedVafPercent", order = 7, displayName = "Variant allele frequency (percentage)")
    public String normalizedReportedVafPercent() {
        return reportedVafPercent == null ? null : String.format(FORMATTING_TWO_DECIMAL_PLACES, reportedVafPercent);
    }

    @Column(columnName = "reportedVaf", order = 8, displayName = "Variant allele frequency (fraction)")
    public String normalizedReportedVaf() {
        return reportedVafPercent == null ? null : String.format(FORMATTING_THREE_DECIMAL_PLACES, reportedVaf);
    }

    private boolean isNoMutationsDetected() {
        return reportedVaf == null;
    }
}
