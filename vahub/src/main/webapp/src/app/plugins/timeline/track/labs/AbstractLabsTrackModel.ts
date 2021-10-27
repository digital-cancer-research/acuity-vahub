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
import {RawEvent} from '../../chart/IChartEvent';
import {IChartEventService} from '../../chart/IChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractLabsTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return LabsTrackUtils.LABS_TRACK_NAME;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = LabsTrackUtils.assignSymbol(dataPoint.metadata.numAboveReferenceRange,
            dataPoint.metadata.numBelowReferenceRange,
            dataPoint.metadata.numAboveSeverityThreshold,
            dataPoint.metadata.numBelowSeverityThreshold);
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        // visit number
        let tooltip = 'Visit # <b>' + dataPoint.metadata.visitNumber + '</b>';

        // number of events out of range
        tooltip += '<br />Labs above normal limit: <b>' + dataPoint.metadata.numAboveReferenceRange + '</b>';
        tooltip += '<br />Labs below normal limit: <b>' + dataPoint.metadata.numBelowReferenceRange + '</b>';
        tooltip += '<br />Labs above severity threshold: <b>' + dataPoint.metadata.numAboveSeverityThreshold + '</b>';
        tooltip += '<br />Labs below severity threshold: <b>' + dataPoint.metadata.numBelowSeverityThreshold + '</b>';

        return tooltip;
    }
}
