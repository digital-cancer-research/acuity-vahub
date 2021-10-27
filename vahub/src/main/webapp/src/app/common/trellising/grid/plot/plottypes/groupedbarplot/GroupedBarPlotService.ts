import {Injectable} from '@angular/core';
import {TextUtils} from '../../../../../utils/TextUtils';
import OutputBarChartData = Request.OutputBarChartData;
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

export interface BarChartSeriesData {
    name: string;
    data: {
        x: number;
        y: number;
        rank?: number;
    }[];
    color: string;
    events?: {
        click?: (event: ChartMouseEvent) => boolean;
    };
}
export interface BarChartPlotData {
    categories: string[];
    series: BarChartSeriesData[];
}

@Injectable()
export class GroupedBarPlotService {
    public splitServerData(data: OutputBarChartData[]): BarChartPlotData {
        const categories: string[] = data[0].categories.map(x => {
            return TextUtils.stringOrEmpty(x);
        });
        const series = data.map((value) => {
            const values = value.series.map((value1) => {
                return {
                    x: value1.rank - 1,
                    y: value1.value,
                    rank: value1.rank,
                };
            });
            return {
                name: value.name ? value.name : 'All',
                data: values,
                // if it's not ColoredOutputBarChartData, color just will be null. probably not the best solution.
                color: (<ColoredOutputBarChartData>value).color
            };
        });

        return {
            categories: categories,
            series: series
        };
    }

}
