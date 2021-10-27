package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.SeriousAeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import org.springframework.stereotype.Service;

@Service
public class SeriousAeService extends BaseEventService<SeriousAeRaw, SeriousAe, SeriousAeGroupByOptions> {
}
