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
import {ConmedsTrackUtils} from '../../track/conmeds/ConmedsTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class ConmedsTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Conmeds',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: '1 = # conmeds',
                    height: '15',
                    width: '15',
                    color: ConmedsTrackUtils.assignColour(1, ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '2 <= # conmeds <= 5',
                    height: '15',
                    width: '15',
                    color: ConmedsTrackUtils.assignColour(3, ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '5 < # conmeds <= 10',
                    height: '15',
                    width: '15',
                    color: ConmedsTrackUtils.assignColour(6, ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '10 < # conmeds',
                    height: '15',
                    width: '15',
                    color: ConmedsTrackUtils.assignColour(11, ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL)
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
