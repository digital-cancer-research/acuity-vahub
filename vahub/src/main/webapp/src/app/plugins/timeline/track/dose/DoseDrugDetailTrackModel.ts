import {Injectable} from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {DoseTrackSteppedLineChartEventService} from './DoseTrackSteppedLineChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class DoseDrugDetailTrackModel extends AbstractTrackModel {

    constructor(protected doseTrackSteppedLineChartPlotEventService: DoseTrackSteppedLineChartEventService) {
        super(doseTrackSteppedLineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    getTrackName(): string {
        return DoseTrackUtils.DOSE_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: DoseTrackUtils.DOSE_TRACK_NAME,
                level: DoseTrackUtils.DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        event.plotOptions.color = DoseTrackUtils.assignColour(dataPoint.metadata.percentChange.perAdmin);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            active: dataPoint.metadata.periodType === 'ACTIVE' || dataPoint.metadata.percentChange.perAdmin !== -100,
            drugDoses: dataPoint.metadata.drugDoses,
            periodType: dataPoint.metadata.periodType,
            subsequentPeriodType: dataPoint.metadata.subsequentPeriodType,
            special: dataPoint.metadata.special
        };
        return event;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            special: true
        };
        return event;
    }
}
