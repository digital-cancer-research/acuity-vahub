/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dao.ConmedRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ConmedDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<ConmedRaw, Conmed> {

    @Autowired
    private ConmedRepository conmedRepository;

    @Override
    protected Conmed getWrapperInstance(ConmedRaw event, Subject subject) {
        return new Conmed(event, subject);
    }

    @Override
    protected Class<ConmedRaw> rawDataClass() {
        return ConmedRaw.class;
    }

    @Override
    protected Collection<ConmedRaw> getData(Dataset ds) {
        return new ArrayList<>(conmedRepository.getRawData(ds.getId()));
    }
}
