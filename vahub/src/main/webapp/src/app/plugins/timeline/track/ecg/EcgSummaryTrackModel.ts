import {Injectable} from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {EcgTrackUtils} from './EcgTrackUtils';
import {AbstractEcgTrackModel} from './AbstractEcgTrackModel';

@Injectable()
export class EcgSummaryTrackModel extends AbstractEcgTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(EcgTrackUtils.SUMMARY_SUB_TRACK_NAME);
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
            track: EcgTrackUtils.ECG_TRACK_NAME,
            level: EcgTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL,
        };
        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = EcgTrackUtils.assignSummaryTrackSymbol(dataPoint.metadata);
        event.group = EcgTrackUtils.SUMMARY_SUB_TRACK_NAME;
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        const maxValuePercentChange = (dataPoint.metadata.maxValuePercentChange || dataPoint.metadata.maxValuePercentChange === 0) ? (dataPoint.metadata.maxValuePercentChange + ' %') : 'N/A';
        tooltip += 'Max value percent change: <b>' + maxValuePercentChange + '</b><br />';
        const changeFromBaseline = (dataPoint.metadata.qtcfChange || dataPoint.metadata.qtcfChange === 0) ? (dataPoint.metadata.qtcfChange + ' ' + dataPoint.metadata.qtcfUnit) : 'N/A';
        tooltip += 'QTcF change: <b>' + changeFromBaseline + '</b><br />';
        const qtcfValue = (dataPoint.metadata.qtcfValue || dataPoint.metadata.qtcfValue === 0) ? (dataPoint.metadata.qtcfValue + ' ' + dataPoint.metadata.qtcfUnit) : 'N/A';
        tooltip += 'QTcF value: <b>' + qtcfValue + '</b>';
        return tooltip;
    }
}
