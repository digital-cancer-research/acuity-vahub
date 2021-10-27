package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.DeathGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.springframework.stereotype.Service;

@Service
public class DeathService extends BaseEventService<DeathRaw, Death, DeathGroupByOptions> {

}
