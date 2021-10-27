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

import {min, max, flatten} from 'lodash';
import {Series} from '../../../../../../vahub-charts/types/interfaces';

export class SelectionService {
    static alterCategoricalSelectionX(xMin: number, xMax: number): { xMin: number, xMax: number } {
        xMax = Math.ceil(xMax - 0.5) + 0.49;
        xMin = Math.floor(xMin + 0.5) - 0.49;
        return {xMin: xMin, xMax: xMax};
    }

    static listCategories(xAxis: any): string[] {
        const minVal = Math.floor(xAxis.min + 0.5);
        const maxVal = Math.floor(xAxis.max + 1.5);
        return xAxis.axis.categories.slice(minVal, maxVal);
    }

    static getAllSelection(series: Array<Series>, hasYAxis: boolean = false, isXCategorical: boolean = false, isYCategorical: boolean = false): any {
        let yMin = min(series.map((x: any) => {
            return x.dataMin;
        })) || 0;
        let yMax = max(series.map((x: any) => {
            return x.dataMax;
        })) || 0;
        let xMin = <number>min(series.map((x: any) => {
            return min(x.data.map(y => y.x));
        })) || 0;
        let xMax = <number>max(series.map((x: any) => {
            return max(x.data.map(y => y.x));
        })) || 0;
        const selectedBars = !hasYAxis ? flatten(series.map((serie: any) => {
            return serie.data.map((point: any) => {
                return {
                    category: point.category,
                    series: serie.name === 'All' ? null : serie.name || point.name
                };
            });
        })) : flatten(series.map((serie: any) => {
            return serie.data.map((point: any) => {
                return {
                    category: point.category,
                    yAxis: point.y,
                    series: serie.name === 'All' ? null : serie.name || point.name
                };
            });
        }));
        if (isXCategorical) {
            xMax += 1;
            xMin -= 1;
        } else {
            const xRange = (xMax - xMin) > 0 ? xMax - xMin : 0.001;
            xMin -= xRange * 0.005;
            xMax += xRange * 0.005;
        }
        if (isYCategorical) {
            yMax += 1;
            yMin -= 1;
        } else {
            const yRange = (yMax - yMin) > 0 ? yMax - yMin : 0.001;
            yMin -= yRange * 0.005;
            yMax += yRange * 0.005;
        }

        return {
            range: {xMin, xMax, yMin, yMax},
            selectedBars: selectedBars
        };
    }
}
