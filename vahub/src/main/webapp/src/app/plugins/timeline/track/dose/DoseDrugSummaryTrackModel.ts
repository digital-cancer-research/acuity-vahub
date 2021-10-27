import {Injectable} from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';
import {AbstractDoseSummaryTrackModel} from './AbstractDoseSummaryTrackModel';

@Injectable()
export class DoseDrugSummaryTrackModel extends AbstractDoseSummaryTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return true;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: DoseTrackUtils.DOSE_TRACK_NAME,
                level: DoseTrackUtils.DRUG_SUMMARY_SUB_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        return event;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        return event;
    }
}
