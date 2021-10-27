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
export class DoseDrugDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Drug dose summary',
            items: [
                {
                    type: TrackLegendType.LINE,
                    text: 'Dosing event',
                    color: DoseTrackUtils.DRUG_DETAIL_COLOUR
                },
                {
                    type: TrackLegendType.LINE_AND_CIRCLE,
                    text: 'Discontinuation',
                    color: DoseTrackUtils.DRUG_DETAIL_COLOUR
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '10',
                    width: '14',
                    src: TimelineUtils.STEP3_ONGOING_SYMBOL
                }
            ]
        };
    }
}
