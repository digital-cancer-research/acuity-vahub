import { Injectable } from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {SpirometryTrackUtils} from './SpirometryTrackUtils';
import {AbstractSpirometryTrackModel} from './AbstractSpirometryTrackModel';
import * as  _ from 'lodash';

@Injectable()
export class SpirometrySummaryTrackModel extends AbstractSpirometryTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(SpirometryTrackUtils.SUMMARY_SUB_TRACK_NAME);
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
            track: SpirometryTrackUtils.SPIROMETRY_TRACK_NAME,
            level: SpirometryTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL,
        };
        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = SpirometryTrackUtils.assignSummaryTrackSymbol(dataPoint.metadata);
        event.group = SpirometryTrackUtils.SUMMARY_SUB_TRACK_NAME;
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        tooltip += 'Max value percent change: <b>' + (!_.isNull(dataPoint.metadata.maxValuePercentChange) ? dataPoint.metadata.maxValuePercentChange + ' %' : 'N/A') + '</b>';
        return tooltip;
    }
}
