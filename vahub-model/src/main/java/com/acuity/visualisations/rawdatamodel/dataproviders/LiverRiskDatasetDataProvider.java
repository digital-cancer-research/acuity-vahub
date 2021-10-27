package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import org.springframework.stereotype.Component;

@Component
public class LiverRiskDatasetDataProvider extends SubjectAwareDatasetsRegularDataProvider<LiverRiskRaw, LiverRisk> {

  @Override
  protected LiverRisk getWrapperInstance(LiverRiskRaw event, Subject subject) {
    return new LiverRisk(event, subject);
  }

  @Override
  protected Class<LiverRiskRaw> rawDataClass() {
    return LiverRiskRaw.class;
  }
}
