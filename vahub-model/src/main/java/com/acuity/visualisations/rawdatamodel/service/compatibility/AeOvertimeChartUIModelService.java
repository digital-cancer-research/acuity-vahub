package com.acuity.visualisations.rawdatamodel.service.compatibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AeOvertimeChartUIModelService extends OvertimeChartUIModelService {

    private AeColoringService aeColoringService;

    public AeOvertimeChartUIModelService(@Autowired AeColoringService aeColoringService, @Autowired BarChartColoringService coloringService) {
        super(coloringService);
        this.aeColoringService = aeColoringService;
    }

    @Override
    public String getColor(int counter, String severity) {
        return aeColoringService.getAeColor(severity);
    }

}
