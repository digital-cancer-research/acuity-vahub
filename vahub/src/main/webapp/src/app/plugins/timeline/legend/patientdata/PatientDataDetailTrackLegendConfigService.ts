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

import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {PatientDataTrackUtils} from '../../track/patientdata/PatientDataTrackUtils';

@Injectable()
export class PatientDataDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Patient data details',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Patient data measurement',
                    color: PatientDataTrackUtils.assignColour(1, PatientDataTrackUtils.LINECHART_LEVEL_TRACK_EXPANSION_LEVEL)
                }
            ]
        };
    }
}
