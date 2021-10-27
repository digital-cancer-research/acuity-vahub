package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import org.springframework.stereotype.Component;

@Component
public class AlcoholDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<AlcoholRaw, Alcohol> {

  @Override
  protected Alcohol getWrapperInstance(AlcoholRaw event, Subject subject) {
    return new Alcohol(event, subject);
  }

  @Override
  protected Class<AlcoholRaw> rawDataClass() {
    return AlcoholRaw.class;
  }
}
