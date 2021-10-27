import {Injectable} from '@angular/core';
import {TextUtils} from '../../../../../utils/TextUtils';
import * as _ from 'lodash';
import OutputBarChartEntry = Request.OutputBarChartEntry;
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;

export interface LinePlot {
    name: string;
    color: string;
    data: {
        x: number,
        y: number
    }[];
}

@Injectable()
export class LinePlotService {

    public splitServerData(series: OutputOvertimeLineChartData[]): { data: LinePlot[], categories: string[] } {
        if (!series) {
            return;
        }

        const allSeries = _.flatMap(series, x => x.series);
        const categories = allSeries.map(x => ({ category: x.category, rank: x.rank - 1 }))
            .reduce((acc, x) => {
                if (!_.find(acc, x)) {
                    acc.push(x);
                }
                return acc;
            }, []).sort((a, b) => a.rank < b.rank ? -1 : 1)
            .map(x => ({category: x.category, rank: x.rank}));

        const data = series.map((subSeries) => {
            return this.splitPlotData(subSeries.name, subSeries.color, subSeries.series, categories);
        });
        return {
            data: data,
            categories: categories.map(x => x.category).map(TextUtils.stringOrEmpty)
        };
    }

    private splitPlotData(name: string, color: string,
                          data: OutputBarChartEntry[], categories: {category: string, rank: number }[]): LinePlot {
        const completeSeries = categories.map(category => {
            const match = _.find(data, { 'category': category.category });
            if (match) {
                return {
                    x: category.rank,
                    y: match.value
                };
            } else {
                return {
                    x: category.rank,
                    y: 0
                };
            }
        });

        return {
            name: name ? name : 'All',
            color: color,
            data: completeSeries
        };
    }
}
