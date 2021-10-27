package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.NicotineGroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import org.springframework.stereotype.Service;

@Service
public class NicotineService extends BaseEventService<NicotineRaw, Nicotine, NicotineGroupByOption> {
}
