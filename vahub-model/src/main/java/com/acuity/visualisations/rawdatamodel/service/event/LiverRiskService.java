package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverRiskGroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import org.springframework.stereotype.Service;

@Service
public class LiverRiskService extends BaseEventService<LiverRiskRaw, LiverRisk, LiverRiskGroupByOption> {
}
