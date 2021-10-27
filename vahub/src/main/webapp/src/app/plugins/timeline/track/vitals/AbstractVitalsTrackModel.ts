import {ITrackDataPoint} from '../../store/ITimeline';
import {IChartEventService} from '../../chart/IChartEventService';
import {VitalsTrackUtils} from './VitalsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractVitalsTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return VitalsTrackUtils.VITALS_TRACK_NAME;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Visit # <b>' + (dataPoint.metadata.visitNumber || 'N/A') + '</b><br />';
        tooltip += 'Sex: <b>' + (dataPoint.metadata.sex || 'N/A') + '</b><br />';
        tooltip += 'Date of baseline: <b>' + (dataPoint.metadata.baseline ? dataPoint.metadata.baseline.dayHourAsString : 'N/A') + '</b><br />';
        return tooltip;
    }
}
