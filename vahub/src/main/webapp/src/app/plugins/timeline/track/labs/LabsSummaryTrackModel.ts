import { Injectable } from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {AbstractLabsTrackModel} from './AbstractLabsTrackModel';

@Injectable()
export class LabsSummaryTrackModel extends AbstractLabsTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(LabsTrackUtils.SUMMARY_SUB_TRACK_NAME);
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
            track: LabsTrackUtils.LABS_TRACK_NAME,
            level: LabsTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL,
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = LabsTrackUtils.SUMMARY_SUB_TRACK_NAME;
        return event;
    }
}
