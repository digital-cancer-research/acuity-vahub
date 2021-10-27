import {Injectable} from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {PlotOptions, RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {getUrlWrappedMarkerSymbol} from '../../chart/TimelineUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';
import {StatusTrackUtils} from './StatusTrackUtils';

@Injectable()
export class StatusTrackModel extends AbstractTrackModel {

    constructor(private barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(StatusTrackUtils.STATUS_SUMMARY_TRACK_NAME);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return false;
    }

    getTrackName(): string {
        return StatusTrackUtils.STATUS_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: this.getTrackName(),
            level: StatusTrackUtils.SUMMARY_TRACK_EXPANSION_LEVEL,
        };

        return plotConfig;
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: StatusTrackUtils.STATUS_SUMMARY_TRACK_NAME,
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            end: {
                date: dataPoint.end.date,
                dayHour: dataPoint.end.dayHour,
                dayHourAsString: dataPoint.end.dayHourAsString,
                studyDayHourAsString: dataPoint.end.studyDayHourAsString,
                doseDayHour: dataPoint.end.doseDayHour
            },
            plotOptions: { color: StatusTrackUtils.assignColour(dataPoint.metadata.type) },
            metadata: {
                duration: dataPoint.metadata.duration,
            }
        };
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: StatusTrackUtils.STATUS_SUMMARY_TRACK_NAME,
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            plotOptions: <PlotOptions>{
                markerSymbol: getUrlWrappedMarkerSymbol(dataPoint.metadata.type),
                height: 20,
                width: 20,
            },
            metadata: {}
        };
    }
}
