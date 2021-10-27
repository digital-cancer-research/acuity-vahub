package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.SubjectExtGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.SubjectExtRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import org.springframework.stereotype.Service;

@Service
public class SubjectExtService extends BaseEventService<SubjectExtRaw, SubjectExt, SubjectExtGroupByOptions> {

}
