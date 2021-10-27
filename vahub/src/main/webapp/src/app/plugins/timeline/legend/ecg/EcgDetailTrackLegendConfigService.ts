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

import {Injectable} from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {EcgTrackUtils} from '../../track/ecg/EcgTrackUtils';

@Injectable()
export class EcgDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'ECG detail',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'ECG Measurement',
                    height: '10',
                    width: '10',
                    color: EcgTrackUtils.ECG_MEASUREMENT_COLOUR
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Baseline',
                    color: EcgTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Reference Line',
                    color: EcgTrackUtils.REFERENCE_LINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'High QTcF, significant or abnormal evaluation',
                    height: '10',
                    width: '10',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                }

            ]
        };
    }
}
