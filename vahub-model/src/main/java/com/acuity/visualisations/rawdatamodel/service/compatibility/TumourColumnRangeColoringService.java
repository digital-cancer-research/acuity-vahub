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

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.DEFAULT_GROUP_TO_LOWER_CASE;

@Service
public class TumourColumnRangeColoringService extends CategoryColoringService {

    @Override
    public String getColor(Object colorByValue, Object colorByOption) {
        String color;
        switch ((colorByValue == null ? "" : colorByValue.toString()).toLowerCase()) {
            case DEFAULT_EMPTY_VALUE_TO_LOWER_CASE:
                color = AeColoringService.BLUE;
                break;
            case ALL_TO_LOWER_CASE:
                color = Colors.LIGHTSEAGREEN.getCode();
                break;
            case DEFAULT_GROUP_TO_LOWER_CASE:
                color = COLORS_NO_GREEN[9];
                break;
            default:
                color = getColorFromMap(colorByValue, colorByOption);
                break;
        }
        return color;
    }

    @Override
    protected String[] getStandardColors() {
        return COLORS_NO_GREEN;
    }

    @SuppressWarnings("unchecked")
    public void generateColors(Datasets datasets, List colorByOptions) {
        ((List<TrellisOptions>) colorByOptions)
                .forEach(colorByOption -> colorByOption.getTrellisOptions()
                        .forEach(opt -> {
                            String datasetColorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets, colorByOption
                                    .getTrellisedBy().getGroupByOptionAndParams()
                                    .getGroupByOption());
                            getColor(opt, datasetColorByOption);
                        }));
    }

}
