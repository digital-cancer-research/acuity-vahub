import {Injectable} from '@angular/core';
import {BarChartSeriesOptions, RawEvent} from '../IChartEvent';
import {IChartEventService} from '../IChartEventService';

@Injectable()
export class LineChartEventService implements IChartEventService {

    private static DEFAULT_MARKER_SYMBOL = 'circle';
    private static DEFAULT_LINE_CHART_COLOUR = '#7CB5EC';

    createPlotDataSeries(data: RawEvent[], categories: string[]): any[] {
        const series: BarChartSeriesOptions[] = [
            {
                name: 'events',
                data: [],
                color: LineChartEventService.DEFAULT_LINE_CHART_COLOUR
            }
        ];

        const seriesData: any[] = [];

        // Iterate through data and assign correctly formatted objects to the correct series.
        data.forEach((event: RawEvent) => {
            seriesData.push(this.parseEventWithoutDuration(event));
        });

        series[0].data = seriesData;

        return series;
    }

    /**
     * To parse none durational events, this requires the metadata of the event to
     * contain a field called 'value', this 'value' will be used as the y value
     */
    private parseEventWithoutDuration(eventWithoutDuration: RawEvent): any {
        return {
            x: eventWithoutDuration.start.dayHour,
            xAsString: eventWithoutDuration.start.dayHourAsString,
            studyXAsString: eventWithoutDuration.start.studyDayHourAsString,
            y: eventWithoutDuration.metadata['value'],
            tooltip: eventWithoutDuration.metadata['tooltip'],
            marker: {
                symbol: (eventWithoutDuration.plotOptions && eventWithoutDuration.plotOptions.markerSymbol)
                    ? eventWithoutDuration.plotOptions.markerSymbol
                    : LineChartEventService.DEFAULT_MARKER_SYMBOL,
                enabled: true,
                fillColor: (eventWithoutDuration.plotOptions && eventWithoutDuration.plotOptions.fillColor)
                    ? eventWithoutDuration.plotOptions.fillColor
                    : eventWithoutDuration.metadata.color,
                radius: (eventWithoutDuration.plotOptions && eventWithoutDuration.plotOptions.radius || undefined)
            },
            metadata: eventWithoutDuration.metadata
        };
    }
}
