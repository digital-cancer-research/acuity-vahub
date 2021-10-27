import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractDoseSummaryTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
    }

    getTrackName(): string {
        return DoseTrackUtils.DOSE_TRACK_NAME;
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.plotOptions.color = DoseTrackUtils.assignColour(dataPoint.metadata.percentChange.perAdmin);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            tooltip: this.createTooltip(dataPoint),
            drugDoses: dataPoint.metadata.drugDoses,
            periodType: dataPoint.metadata.periodType,
            subsequentPeriodType: dataPoint.metadata.subsequentPeriodType
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        const drugDoses = dataPoint.metadata.drugDoses ? dataPoint.metadata.drugDoses.filter(e => e.dose > 0) : <any>[];

        if (drugDoses.length > 0) {
            let tooltip = 'Active dosing';

            drugDoses.forEach(drugDose => {
                tooltip += `<br/><b>${drugDose.drug}: </b> ${drugDose.dose} ${drugDose.doseUnit} ${drugDose.frequency.name}`;
            });

            return tooltip;
        } else {
            return 'Inactive dosing';
        }

    }
}
