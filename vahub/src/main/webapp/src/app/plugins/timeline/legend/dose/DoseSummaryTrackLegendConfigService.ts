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
import {DoseTrackUtils} from '../../track/dose/DoseTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class DoseSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Dose summary',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: 'No dose reduction',
                    height: '15',
                    width: '15',
                    color: DoseTrackUtils.assignColour(0)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Inactive dosing (100% reduction)',
                    height: '15',
                    width: '15',
                    color: DoseTrackUtils.assignColour(-100)
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
