package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChemotherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import org.springframework.stereotype.Service;

@Service
public class ChemotherapyService extends BaseEventService<ChemotherapyRaw, Chemotherapy, ChemotherapyGroupByOptions> {
}
