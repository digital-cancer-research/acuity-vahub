package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.dod.AmlCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.CBioCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.SsvCommonService;
import com.acuity.visualisations.rawdatamodel.service.filters.AbstractEventFilterService;
import com.acuity.visualisations.rawdatamodel.service.filters.PopulationRawDataFilterService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.MedicalHistoryGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MedicalHistoryService extends BaseEventService<MedicalHistoryRaw, MedicalHistory, MedicalHistoryGroupByOptions> {

    public MedicalHistoryService(DoDCommonService doDCommonService,
                                 SsvCommonService ssvCommonService,
                                 AmlCommonService amlCommonService,
                                 CBioCommonService cBioCommonService,
                                 List<SubjectAwareDatasetsDataProvider<MedicalHistoryRaw, MedicalHistory>> eventDataProviders,
                                 PopulationDatasetsDataProvider populationDatasetsDataProvider,
                                 AbstractEventFilterService<MedicalHistory, Filters<MedicalHistory>> eventFilterService,
                                 PopulationRawDataFilterService populationFilterService) {
        super(
                doDCommonService,
                ssvCommonService,
                amlCommonService,
                cBioCommonService,
                eventDataProviders,
                populationDatasetsDataProvider,
                eventFilterService,
                populationFilterService);
    }

    static final Set<String> CURRENT = new HashSet<>(Arrays.asList("yes", "current"));
    static final Set<String> PAST = new HashSet<>(Arrays.asList("no", "past"));
}
