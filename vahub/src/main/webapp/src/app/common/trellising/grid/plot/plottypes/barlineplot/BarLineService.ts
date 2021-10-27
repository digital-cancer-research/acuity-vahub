import {Injectable} from '@angular/core';
import {isUndefined} from 'lodash';

import {TextUtils} from '../../../../../utils/TextUtils';
import OutputBarChartData = Request.OutputBarChartData;
import OutputOvertimeData = InMemory.OutputOvertimeData;
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;
import OutputBarChartEntry = Request.OutputBarChartEntry;
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

export interface BarLineCategories {
    category: string;
    rank: number;
}
export interface BarLineSeries {
    name: string;
    data: number[][];
    color: string;
    type: string;
    events?: {
        click?: (e: ChartMouseEvent) => boolean;
    };
}
export interface BarLineData {
    categories: string[];
    series: BarLineSeries[];
    lines: BarLineSeries[];
}

@Injectable()
export class BarLineService {

    splitServerData(data: OutputOvertimeData): BarLineData {
        const categoryPairs = this.categoryRankPairs(data);
        const categories: string[] = data.categories.map(TextUtils.stringOrEmpty);
        const series: BarLineSeries[] = data.series.map(x => this.preformSeries(x, categoryPairs));
        const lines: BarLineSeries[] = data.lines.map(x => this.preformSeries(x, categoryPairs));
        return {
            categories: categories,
            series: series,
            lines: lines
        };
    }

    private preformSeries(data: any, categories: BarLineCategories[]): BarLineSeries {
        const values: number[][] = categories.map((value) => {
            const match: OutputBarChartEntry = data.series.find((serie) => {
                return serie.category === value.category;
            });
            if (match) {
                return [value.rank, match.value];
            } else {
                return [value.rank, 0];
            }
        });
        return {
            name: data.name ? data.name : 'All',
            data: values,
            color: data.color,
            type: 'barline'
        };
    }

    private categoryRankPairs(data: OutputOvertimeData): BarLineCategories[] {
        const categories: BarLineCategories[] = data.categories.map((value) => {
            return {category: value, rank: undefined};
        });
        data.series.forEach((series: OutputBarChartData) => {
            series.series.forEach((value) => {
                const match = categories.find(category => {
                    return category.category === value.category;
                });
                if (match && isUndefined(match.rank)) {
                    match.rank = value.rank - 1;
                }
            });
        });

        data.lines.forEach((series: OutputOvertimeLineChartData) => {
            series.series.forEach((value) => {
                const match = categories.find(category => {
                    return category.category === value.category;
                });
                if (match && isUndefined(match.rank)) {
                    match.rank = value.rank - 1;
                }
            });
        });
        categories.forEach((category, index) => {
            if (!category.rank && index > 0) {
                category.rank = categories[index - 1].rank + 1;
            }
        });
        return categories;
    }
}
