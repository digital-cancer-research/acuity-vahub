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
