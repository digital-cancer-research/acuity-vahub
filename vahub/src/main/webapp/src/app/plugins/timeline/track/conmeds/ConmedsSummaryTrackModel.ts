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
import {ConmedsTrackUtils} from './ConmedsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class ConmedsSummaryTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(ConmedsTrackUtils.CONMEDS_SUMMARY_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return false;
    }

    getTrackName(): string {
        return ConmedsTrackUtils.CONMEDS_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: ConmedsTrackUtils.CONMEDS_TRACK_NAME,
                level: ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = ConmedsTrackUtils.CONMEDS_SUMMARY_SUB_TRACK_NAME;
        event.plotOptions.color = ConmedsTrackUtils.assignColour(dataPoint.metadata.numberOfConmeds, ConmedsTrackUtils.CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL);
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            numberOfConmeds: dataPoint.metadata.numberOfConmeds,
            duration: dataPoint.metadata.duration,
            conmeds: dataPoint.metadata.conmeds,
            tooltip: this.createTooltip(dataPoint)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Event: <b>Number of conmeds</b><br/>';
        tooltip += 'Included Conmeds:<br/>';
        let oneLineCounter = 0;
        dataPoint.metadata.conmeds.forEach(conmed => {
            oneLineCounter++;
            if (conmed.conmed !== null) {
                tooltip += `<b>${conmed.conmed.toLowerCase()}</b>, `;
            }
            if (oneLineCounter === 5) {
                tooltip += '<br>';
                oneLineCounter = 0;
            }
        });
        if (oneLineCounter !== 0) {
            tooltip = tooltip.slice(0, -2);
            tooltip += '<br>';
        }
        //tooltip = tooltip.slice(0, -2);
        tooltip += 'Number of conmeds: <b>' + dataPoint.metadata.numberOfConmeds + '</b><br/>';
        tooltip += '<table class="table table-condensed conmeds-tooltip-table"><thead><tr><th>Conmed</th><th>Dose</th><th>Frequency</th><th>Indication</th></tr></thead>';
        dataPoint.metadata.conmeds.forEach((conmed) => {
            const dose = conmed.doses.length > 0 ? conmed.doses.join(',</br>') : 'N/A';
            const frequency = conmed.frequencies.length > 0 ? conmed.frequencies.join(',</br>') : 'N/A';
            const indication = conmed.indications.length > 0 ? conmed.indications.join(',</br>') : 'N/A';
            tooltip += `<tr><td>${conmed.conmed.toLowerCase()}</td>`;
            tooltip += `<td>${dose}</td>`;
            tooltip += `<td>${frequency}</td>`;
            tooltip += `<td>${indication}</td></tr>`;
        });
        tooltip += '</table>';
        return tooltip;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = ConmedsTrackUtils.CONMEDS_SUMMARY_SUB_TRACK_NAME;
        return event;
    }
}
