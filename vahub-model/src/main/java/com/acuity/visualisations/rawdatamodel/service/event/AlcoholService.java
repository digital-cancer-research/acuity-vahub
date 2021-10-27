package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AlcoholGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import org.springframework.stereotype.Service;

@Service
public class AlcoholService extends BaseEventService<AlcoholRaw, Alcohol, AlcoholGroupByOptions> {
}
