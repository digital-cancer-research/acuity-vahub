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
export class EcgSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'ECG summary',
            items: [
                {
                    type: TrackLegendType.GRADIENT,
                    text: '% change from baseline',
                    colorStart: EcgTrackUtils.MINIMAL_COLOUR,
                    color: EcgTrackUtils.BASELINE_COLOUR,
                    colorEnd: EcgTrackUtils.MAXIMAL_COLOUR
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'High QTcF, significant or abnormal evaluation',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'ECG Measurement',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'High QTcF, significant or abnormal evaluation at baseline',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'ECG Measurement at baseline',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
