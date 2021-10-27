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
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {ExacerbationsTrackUtils} from '../../track/exacerbations/ExacerbationsTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class ExacerbationsTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Exacerbations',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Mild (known end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(3, true)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Mild (no end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(3, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Moderate (known end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(2, true)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Moderate (no end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(2, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Severe (known end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(1, true)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Severe (no end date)',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(1, false)
                },

                {
                    type: TrackLegendType.ACUITY,
                    text: 'Unknown',
                    height: '15',
                    width: '15',
                    color: ExacerbationsTrackUtils.assignColour(null, true)
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ONGOING_SYMBOL
                }
            ]
        };
    }
}
