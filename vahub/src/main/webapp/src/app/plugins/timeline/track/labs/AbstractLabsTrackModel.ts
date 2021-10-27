import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {IChartEventService} from '../../chart/IChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractLabsTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return LabsTrackUtils.LABS_TRACK_NAME;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.plotOptions = LabsTrackUtils.assignSymbol(dataPoint.metadata.numAboveReferenceRange,
            dataPoint.metadata.numBelowReferenceRange,
            dataPoint.metadata.numAboveSeverityThreshold,
            dataPoint.metadata.numBelowSeverityThreshold);
        event.metadata.tooltip = this.createTooltip(dataPoint);
        return event;
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        // visit number
        let tooltip = 'Visit # <b>' + dataPoint.metadata.visitNumber + '</b>';

        // number of events out of range
        tooltip += '<br />Labs above normal limit: <b>' + dataPoint.metadata.numAboveReferenceRange + '</b>';
        tooltip += '<br />Labs below normal limit: <b>' + dataPoint.metadata.numBelowReferenceRange + '</b>';
        tooltip += '<br />Labs above severity threshold: <b>' + dataPoint.metadata.numAboveSeverityThreshold + '</b>';
        tooltip += '<br />Labs below severity threshold: <b>' + dataPoint.metadata.numBelowSeverityThreshold + '</b>';

        return tooltip;
    }
}
