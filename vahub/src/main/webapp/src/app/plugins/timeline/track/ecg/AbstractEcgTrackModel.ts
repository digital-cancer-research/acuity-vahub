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

import {ITrackDataPoint} from '../../store/ITimeline';
import {IChartEventService} from '../../chart/IChartEventService';
import {EcgTrackUtils} from './EcgTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractEcgTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return EcgTrackUtils.ECG_TRACK_NAME;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Visit # <b>' + (dataPoint.metadata.visitNumber || 'N/A') + '</b><br />';
        tooltip += 'Sex: <b>' + (dataPoint.metadata.sex || 'N/A') + '</b><br />';
        tooltip += 'Date of baseline: <b>' + (dataPoint.metadata.baseline ? dataPoint.metadata.baseline.dayHourAsString : 'N/A') + '</b><br />';
        tooltip += 'Significant: <b>' + (dataPoint.metadata.significant || 'N/A') + '</b><br />';
        tooltip += 'Abnormal: <b>' + (dataPoint.metadata.abnormality || 'N/A') + '</b><br/>';
        return tooltip;
    }
}
