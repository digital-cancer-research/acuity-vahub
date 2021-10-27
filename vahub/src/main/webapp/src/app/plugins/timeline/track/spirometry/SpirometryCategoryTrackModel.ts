import { Injectable } from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {SpirometryTrackUtils} from './SpirometryTrackUtils';
import {AbstractSpirometryTrackModel} from './AbstractSpirometryTrackModel';
import * as  _ from 'lodash';

@Injectable()
export class SpirometryCategoryTrackModel extends AbstractSpirometryTrackModel {

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
            track: SpirometryTrackUtils.SPIROMETRY_TRACK_NAME,
            level: SpirometryTrackUtils.CATEGORY_SUB_TRACK_EXPANSION_LEVEL,
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = SpirometryTrackUtils.assignCategoryTrackSymbol(dataPoint.metadata);
        event.group = dataPoint.metadata.code;
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = super.createTooltip(dataPoint);
        tooltip += 'Lung function code: <b>' + dataPoint.metadata.code + '</b><br/>';
        tooltip += 'Baseline value: <b>' + (!_.isNull(dataPoint.metadata.baselineValue) ? (dataPoint.metadata.baselineValue + ' ' + dataPoint.metadata.unit) : 'N/A') + '</b><br />';
        const changeFromBaseline = !_.isNull(dataPoint.metadata.valueChangeFromBaseline) ? (dataPoint.metadata.valueChangeFromBaseline + ' ' + dataPoint.metadata.unitChangeFromBaseline) : 'N/A';
        tooltip += 'Change from baseline: <b>' + changeFromBaseline + '</b><br />';
        const percentChangeFromBaseline = !_.isNull(dataPoint.metadata.valuePercentChangeFromBaseline) ? (dataPoint.metadata.valuePercentChangeFromBaseline + ' ' + dataPoint.metadata.unitPercentChangeFromBaseline) : 'N/A';
        tooltip += '% change from baseline: <b>' + percentChangeFromBaseline + '</b><br />';
        const raw = !_.isNull(dataPoint.metadata.value) ? (dataPoint.metadata.value + ' ' + dataPoint.metadata.unit) : 'N/A';
        tooltip += 'Raw value: <b>' + raw + '</b>';
        return tooltip;
    }
}
