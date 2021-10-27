package com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary;

import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * Status phase event, start and end of phase event and its phase type
 *
 * @author ksnd199
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudyPhase extends EventInterval implements HasStartEndDate {

    @Override
    public Date getEndDate() {
        return getEnd().getDate();
    }

    @Override
    public Date getStartDate() {
        return getStart().getDate();
    }

    public enum PhaseType {
        RANDOMISED_DRUG,
        RUN_IN,
        ON_STUDY_DRUG; // no randomisation
    }
    
    private PhaseType phaseType;
}
