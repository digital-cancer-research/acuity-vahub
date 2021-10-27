import {Injectable} from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {ConmedsTrackUtils} from './ConmedsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class ConmedsClassTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(ConmedsTrackUtils.MEDICATION_CLASS_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return true;
    }

    getTrackName(): string {
        return ConmedsTrackUtils.CONMEDS_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: ConmedsTrackUtils.CONMEDS_TRACK_NAME,
                level: ConmedsTrackUtils.MEDICATION_CLASS_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.conmedClass;
        event.plotOptions.color = ConmedsTrackUtils.assignColour(dataPoint.metadata.numberOfConmeds, ConmedsTrackUtils.MEDICATION_CLASS_TRACK_EXPANSION_LEVEL);
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            duration: dataPoint.metadata.duration,
            numberOfConmeds: dataPoint.metadata.numberOfConmeds,
            conmeds: dataPoint.metadata.conmeds,
            conmedClass: dataPoint.metadata.conmedClass,
            tooltip: this.createTooltip(dataPoint)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Event: <b>Conmed class</b><br/>';
        tooltip += `Conmed class:<b>${dataPoint.metadata.conmedClass}</b></br>`;
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
        tooltip += `Number of conmeds: <b>${dataPoint.metadata.numberOfConmeds}</b><br/>`;
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
        event.group = dataPoint.metadata.conmedClass;
        return event;
    }

}
