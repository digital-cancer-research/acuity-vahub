package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverDiagGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.springframework.stereotype.Service;

@Service
public class LiverDiagService extends BaseEventService<LiverDiagRaw, LiverDiag, LiverDiagGroupByOptions> {

}
