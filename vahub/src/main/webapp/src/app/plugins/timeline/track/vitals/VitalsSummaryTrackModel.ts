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
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {VitalsTrackUtils} from './VitalsTrackUtils';
import {AbstractVitalsTrackModel} from './AbstractVitalsTrackModel';
import * as  _ from 'lodash';

@Injectable()
export class VitalsSummaryTrackModel extends AbstractVitalsTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(VitalsTrackUtils.SUMMARY_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return false;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: VitalsTrackUtils.VITALS_TRACK_NAME,
            level: VitalsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL,
        };
        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = VitalsTrackUtils.assignSummaryTrackSymbol(dataPoint.metadata);
        event.group = VitalsTrackUtils.SUMMARY_SUB_TRACK_NAME;
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        tooltip += 'Max value percent change: <b>' + (!_.isNull(dataPoint.metadata.maxValuePercentChange) ? dataPoint.metadata.maxValuePercentChange + ' %' : 'N/A') + '</b>';
        return tooltip;
    }
}
