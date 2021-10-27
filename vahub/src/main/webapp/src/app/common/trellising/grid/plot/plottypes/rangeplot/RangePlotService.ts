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
import { hashCodeFromString } from '../../../../../utils/Utils';
import { BaseChartUtilsService } from '../../../../store/plotutils/BaseChartUtilsService';
import RangeChartSeries = Request.RangeChartSeries;
import ColoredRangeChartSeries = Request.ColoredRangeChartSeries;
import {RangePlotPoint, RangePlotData, RangePlot} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class RangePlotService {
    static colourPalette(label: string): string {
        if ((/all/i).test(label)) {
            return '#88CCEE';
        }
        if ((/placebo/i).test(label)) {
            return '#00AAFF';
        }

        const hash = Math.abs(hashCodeFromString(label));
        return BaseChartUtilsService.getColor(hash);
    }

    static getCategoricalBin(x: number): { min: number, max: number } {
        const nearest = Math.round(x);
        return { min: nearest - 0.49, max: nearest + 0.49 };
    }

    public splitServerData(series: RangeChartSeries<any, any>[], isCategorical: boolean): RangePlot {
        let categories: string[] = isCategorical ? _.chain(series)
            .map(s => s.data)
            .flatten()
            .sortBy(x => x.xrank)
            .map(x => x.x)
            .uniq()
            .value() : undefined;
        const data = series.map((subSeries) => this.splitRangePlotData(subSeries.name,
            subSeries.data,
            (<ColoredRangeChartSeries>subSeries).color,
            categories));
        if (isCategorical) {
            categories = categories.map(TextUtils.stringOrEmpty);
        }
        return {
            data,
            categories
        };
    }

    private splitRangePlotData(name: string, entries: any[], color: string, categories: string[]): RangePlotData {
        let ranges: number[][];
        let averages: RangePlotPoint[];

        if (!categories) {
            ranges = entries.filter(x => x.y !== null).map((x) => {
                return [Number(x.x), x.min ? x.min : x.y, x.max ? x.max : x.y];
            });

            averages = entries.filter(x => x.y !== null).map((x) => {
                return <RangePlotPoint>{
                    x: Number(x.x),
                    y: x.y,
                    dataPoints: x.dataPoints,
                    stdErr: x.stdErr,
                    ranges: ((x.min || x.max) && (x.min !== x.max)) ? [x.min, x.max] : [x.y, x.y],
                    name: x.name
                };
            });
        } else {
            ranges = [];
            categories.forEach((category, index) => {
                const x = _.find(entries, (_x) => {
                    return _x.x === category;
                });
                if (x && x.min !== null && x.max !== null) {
                    return ranges.push([index, x.min, x.max]);
                } else if (x && x.y !== null) {
                    return ranges.push([index, x.y, x.y]);
                }
            });

            averages = [];
            categories.forEach((category, index) => {
                const x = _.find(entries, (_x) => {
                    return _x.x === category;
                });
                if (x && x.y !== null) {
                    averages.push(<RangePlotPoint>{
                        x: index,
                        y: x.y,
                        dataPoints: x.dataPoints,
                        stdErr: x.stdErr,
                        ranges: ((x.min || x.max) && (x.min !== x.max)) ? [x.min, x.max] : [x.y, x.y],
                        name: x.name
                    });
                }
            });
        }

        return {
            ranges: ranges,
            averages: averages,
            name: name ? name : 'All',
            color
        };
    }
}
