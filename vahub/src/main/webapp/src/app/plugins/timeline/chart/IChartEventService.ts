import {RawEvent} from './IChartEvent';

export interface IChartEventService {

    createPlotDataSeries(data: RawEvent[], categories: string[]): any[];
}
