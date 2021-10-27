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
import {RouterStateSnapshot} from '@angular/router';
import {getPluginSummary, getServerPath} from '../../common/utils/Utils';
import {TrackName} from '../timeline/store/ITimeline';
import {Track} from '../../common/trellising/store/services/TimelineConfigService';

export interface ParcedUrl {
    destinationTabSummary: any;
    subject: string;
    acuityDatasetId: number;
}

@Injectable()
export class UrlParcerService {

    constructor() {
    }

    getParcedUrl(state: RouterStateSnapshot): ParcedUrl {
        const [empty, baseUrl, pluginUrl, tabUrl] = state.url.split('/');
        const tabSummary = getPluginSummary(pluginUrl, tabUrl);
        return <ParcedUrl>{
            destinationTabSummary: tabSummary,
            subject: state.root.queryParams['subject'],
            acuityDatasetId: parseInt(state.root.queryParams['acuitydatasetid'], 10)
        };
    }

    getInitialSingleSubjectTimelineState(): Track[] {
        return <any>[
            {
                name: TrackName.SUMMARY,
                selected: true,
                order: 1,
                expansionLevel: 1
            },
            {
                name: TrackName.DOSE,
                selected: true,
                order: 2,
                expansionLevel: 1
            },
            {
                name: TrackName.AES,
                selected: true,
                order: 3,
                expansionLevel: 2
            },
            {
                name: TrackName.HEALTHCARE_ENCOUNTERS,
                selected: true,
                order: 4,
                expansionLevel: 1
            },
            {
                name: TrackName.LABS,
                selected: true,
                order: 5,
                expansionLevel: 1
            },
            {
                name: TrackName.SPIROMETRY,
                selected: true,
                order: 6,
                expansionLevel: 1
            },
            {
                name: TrackName.CONMEDS,
                selected: true,
                order: 7,
                expansionLevel: 1
            },
            {
                name: TrackName.EXACERBATION,
                selected: true,
                order: 8,
                expansionLevel: 1
            },
            {
                name: TrackName.VITALS,
                selected: true,
                order: 9,
                expansionLevel: 1
            },
            {
                name: TrackName.ECG,
                selected: true,
                order: 10,
                expansionLevel: 1
            }
        ];
    }
}
