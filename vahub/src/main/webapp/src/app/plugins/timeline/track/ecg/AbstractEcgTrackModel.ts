import {ITrackDataPoint} from '../../store/ITimeline';
import {IChartEventService} from '../../chart/IChartEventService';
import {EcgTrackUtils} from './EcgTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractEcgTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return EcgTrackUtils.ECG_TRACK_NAME;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Visit # <b>' + (dataPoint.metadata.visitNumber || 'N/A') + '</b><br />';
        tooltip += 'Sex: <b>' + (dataPoint.metadata.sex || 'N/A') + '</b><br />';
        tooltip += 'Date of baseline: <b>' + (dataPoint.metadata.baseline ? dataPoint.metadata.baseline.dayHourAsString : 'N/A') + '</b><br />';
        tooltip += 'Significant: <b>' + (dataPoint.metadata.significant || 'N/A') + '</b><br />';
        tooltip += 'Abnormal: <b>' + (dataPoint.metadata.abnormality || 'N/A') + '</b><br/>';
        return tooltip;
    }
}
