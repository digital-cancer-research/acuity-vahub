/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Injectable } from '@angular/core';
import { TextUtils } from '../../../../../utils/TextUtils';
import * as  _ from 'lodash';
import RangeChartSeries = Request.RangeChartSeries;

export interface RangePlotData {
    ranges: number[][];
    averages: RangePlotPoint[];
    name: string;
}

export interface RangePlot {
    data: RangePlotData[];
    categories: string[];
}

export interface RangePlotPoint {
    x?: number;
    y: number;
    dataPoints: number;
    stdErr: number;
    ranges?: number[];
    name?: string;
    marker?: {
        lineColor: string;
    };
}

@Injectable()
export class JoinedRangePlotService {
    static colourPalette(index: number): string {
        switch (index) {
            case 0:
                return '#88CCEE';
            case 1:
                return '#44AA99';
            case 2:
                return '#DDCC77';
            case 3:
                return '#CC6677';
            case 4:
                return '#AA4499';
            case 5:
                return '#882255';
            case 6:
                return '#117733';
            case 7:
                return '#332288';
            default:
                return 'black';
        }
    }

    static renalColourPalette(cdkStage: string): string {
        switch (cdkStage) {
            case 'CKD Stage 1':
                return '#b4da50';
            case 'CKD Stage 2':
                return '#f7d533';
            case 'CKD Stage 3':
                return '#fe8c01';
            case 'CKD Stage 4':
                return '#d8181c';
            case 'CKD Stage 5':
                return '#000000';
            default:
                return 'grey';
        }
    }

    private getMarker(name: string): {lineColor: string} {
        return {
            lineColor: JoinedRangePlotService.renalColourPalette(name)
        };
    }

    public splitServerData(series: RangeChartSeries<any, any>[], isCategorical: boolean): RangePlot {
        let categories: string[] = isCategorical ? _.chain(series)
            .map(s => s.data)
            .flatten()
            .sortBy(x => x.xrank)
            .map(x => x.x)
            .uniq()
            .value() : undefined;
        const points = [];
        series.forEach(subSeries => {
            subSeries.data.forEach(point => {
                const namedPoint = <any>point;
                namedPoint.name = subSeries.name;
                points.push(namedPoint);
            });
        });
        const formattedData = _.sortBy(points, (d) => d.xrank).filter((x: any) => x.y !== null);
        const data = [this.splitRangePlotData('All', formattedData, categories)];
        if (isCategorical) {
            categories = categories.map(TextUtils.stringOrEmpty);
        }
        return {
            data: data,
            categories: categories
        };
    }

    private splitRangePlotData(name: string, entries: any[], categories: string[]): RangePlotData {
        let ranges: number[][];
        let averages: RangePlotPoint[];

        if (!categories) {
            ranges = entries.filter(x =>  x.y !== null).map((x) => {
                return [Number(x.x), x.min ? x.min : x.y, x.max ? x.max : x.y];
            });

            averages = entries.filter(x =>  x.y !== null).map((x) => {
                return <RangePlotPoint>{
                    x: Number(x.x),
                    y: x.y,
                    dataPoints: x.dataPoints,
                    stdErr: x.stdErr,
                    ranges: ((x.min || x.max) && (x.min !== x.max)) ? [x.min, x.max] : [x.y, x.y],
                    marker: this.getMarker(x.name),
                    name: x.name
                };
            });
        } else {
            ranges = [];
            categories.forEach((category, index) => {
                const x = entries.find(point => {
                    return point.x === category;
                });
                if (x && x.min !== null && x.max !== null) {
                    return ranges.push([index, x.min, x.max]);
                } else if (x && x.y !== null) {
                    return ranges.push([index, x.y, x.y]);
                }
            });

            averages = [];
            categories.forEach((category, index) => {
                const x = entries.find(point => {
                    return point.x === category;
                });
                if (x && x.y !== null) {
                    averages.push(<RangePlotPoint>{
                        x: index,
                        y: x.y,
                        dataPoints: x.dataPoints,
                        stdErr: x.stdErr,
                        ranges: ((x.min || x.max) && (x.min !== x.max)) ? [x.min, x.max] : [x.y, x.y],
                        marker: this.getMarker(x.name),
                        name: x.name
                    });
                }
            });
        }

        return {
            ranges: ranges,
            averages: averages,
            name: name ? name : 'All'
        };
    }

}
