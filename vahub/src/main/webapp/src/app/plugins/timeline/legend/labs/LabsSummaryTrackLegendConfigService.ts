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
import {LabsTrackUtils} from '../../track/labs/LabsTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class LabsSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Labs summary',
            items: [
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs above limit of normal',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ABOVE_REFERENCE_RANGE
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs below limit of normal',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.BELOW_REFERENCE_RANGE
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs above and below limit of normal',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ABOVE_BELOW_REFERENCE_RANGE
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs above limit of normal and outside severity threshold',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ABOVE_SEVERITY_THESHOLD
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs below limit of normal and outside severity threshold',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.BELOW_SEVERITY_THESHOLD
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'One or more labs above and below limit of normal and outside severity threshold',
                    height: '10',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ABOVE_BELOW_SEVERITY_THESHOLD
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'No labs changes beyond limits of normal',
                    height: '10',
                    width: '10',
                    color: LabsTrackUtils.NORMAL_LAB_SYMBOL.fillColor
                },
            ]
        };
    }
}
