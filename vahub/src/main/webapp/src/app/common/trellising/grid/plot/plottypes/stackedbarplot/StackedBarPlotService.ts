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

import {Injectable} from '@angular/core';
import {TextUtils} from '../../../../../utils/TextUtils';
import * as  _ from 'lodash';
import OutputBarChartData = Request.OutputBarChartData;
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

export interface BarChartPlotData {
    categories: any[];
    series: { name: string, data: number[][], color: string }[];
}

@Injectable()
export class StackedBarPlotService {
    public splitServerData(data: OutputBarChartData[]): BarChartPlotData {
        const categories: string[] = data[0].categories.map(x => {
            return TextUtils.stringOrEmpty(x);
        });
        const series = data.map((value) => {
            const values: any[] = value.series.map((value1) => {
                    return {
                        x: value1.rank ? value1.rank - 1 : value1.rank,
                        y: value1.value,
                        tooltip: (<any>value1).tooltip,
                        percentOfSubjects: (<any>value1).percentOfSubjects,
                        totalSubjects: value1.totalSubjects
                    };
            }).filter((row) => row.x != null);
            return {
                name: value.name ? value.name : 'All',
                data: _.sortBy(values, 'x'),
                // if not colored, color just will be null. probably not the best solution.
                color: (<ColoredOutputBarChartData>value).color
            };
        });

        return {
            categories: categories,
            series: series
        };
    }
}
