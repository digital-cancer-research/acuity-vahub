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
import {AesTrackUtils} from '../../track/aes/AesTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';
import {EMPTY} from '../../../../common/trellising/store';

@Injectable()
export class AesTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Adverse events',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 1',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(1, false, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 1 (No end date)',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(1, true, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 2',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(2, false, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 2 (No end date)',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(2, true, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 3',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(3, false, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 3 (No end date)',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(3, true, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 4',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(4, false, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 4 (No end date)',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(4, true, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'CTC Grade 5',
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(5, false, false)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: EMPTY,
                    height: '15',
                    width: '15',
                    color: AesTrackUtils.assignColour(-1, false, false)
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ONGOING_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Withdrawal/Completion',
                    height: '15',
                    width: '15',
                    src: TimelineUtils.WITHDRAWAL_COMPLETION_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Death',
                    height: '15',
                    width: '15',
                    src: TimelineUtils.DEATH_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Last Visit Recorded in the System',
                    height: '15',
                    width: '15',
                    src: TimelineUtils.LAST_VISIT_END_SYMBOL
                }
            ]
        };
    }
}
