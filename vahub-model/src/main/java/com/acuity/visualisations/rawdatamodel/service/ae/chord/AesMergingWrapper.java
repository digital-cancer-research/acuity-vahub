package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.getMaxDate;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.getMinDate;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
class AesMergingWrapper {

    private List<Ae> aes = new ArrayList<>();
    private String subjectCode;
    private Date startDate;
    private Date endDate;

    AesMergingWrapper(Ae adverseEvent) {
        this.startDate = adverseEvent.getStartDate();
        this.endDate = adverseEvent.getEndDate();
        this.subjectCode = adverseEvent.getSubjectCode();
        aes.add(adverseEvent);
    }

    AesMergingWrapper(AesMergingWrapper wrapper1, AesMergingWrapper wrapper2) {
        this.startDate = getMinDate(wrapper1.getStartDate(), wrapper2.getStartDate());
        this.endDate = getMaxDate(wrapper1.getEndDate(), wrapper2.getEndDate());
        this.subjectCode = wrapper1.getSubjectCode();
        aes.addAll(wrapper1.getAes());
        aes.addAll(wrapper2.getAes());
    }
}
