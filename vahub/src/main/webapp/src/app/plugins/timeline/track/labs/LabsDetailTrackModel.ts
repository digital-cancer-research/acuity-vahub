import { Injectable } from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {AbstractLabsTrackModel} from './AbstractLabsTrackModel';

@Injectable()
export class LabsDetailTrackModel extends AbstractLabsTrackModel {

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
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: LabsTrackUtils.LABS_TRACK_NAME,
            level: LabsTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.code;
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        // lab code
        let tooltip = 'Labcode: <b>' + dataPoint.metadata.code + '</b><br/>';
        tooltip += super.createTooltip(dataPoint);

        return tooltip;
    }

}
