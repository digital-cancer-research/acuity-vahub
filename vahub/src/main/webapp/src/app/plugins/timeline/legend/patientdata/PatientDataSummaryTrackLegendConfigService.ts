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
export class PatientDataSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Patient data',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: '1 = # Patient reported event',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(1, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '2 <= # Patient reported events <= 5',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(3, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '5 < # Patient reported events <= 10',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(6, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '10 < # Patient reported events',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(11, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                }
            ]
        };
    }
}
