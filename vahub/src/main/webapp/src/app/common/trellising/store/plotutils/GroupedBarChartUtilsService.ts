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

import * as  _ from 'lodash';

import {IPlot} from '../ITrellising';
import {StackedBarChartUtilsService} from './StackedBarChartUtilsService';
import OutputBarChartData = Request.OutputBarChartData;

export class GroupedBarChartUtilsService extends StackedBarChartUtilsService {

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        const groupedMaxValue: number = plot.getIn(['data']).toJS().reduce((maxValue: number, data: OutputBarChartData) => {
            const maxSeriesValue = _.max(data.series.map(x => x.value)) * 1.1; // Add 10% to allow selection
            maxValue = maxValue < maxSeriesValue ? maxSeriesValue : maxValue;
            return maxValue;
        }, 0);
        range.max = groupedMaxValue > range.max ? groupedMaxValue : range.max;
        return range;
    }
}
