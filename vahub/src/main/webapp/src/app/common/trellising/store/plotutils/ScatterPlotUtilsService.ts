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
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

export class ScatterPlotUtilsService extends BaseChartUtilsService {

    protected getXZoomRanges(plots: List<IPlot>): {min: number, max: number} {
        return plots.reduce((range: any, plot: IPlot) => {
            if (!plot.get('data').isEmpty()) {
                const scatterChartData = <OutputScatterPlotEntry[]>plot.get('data').get('data').toJS();
                const values = scatterChartData && scatterChartData.map(entry => entry.x);
                if (values) {
                    range.min = !range.min || _.min(values) < range.min ? _.min(values) : range.min;
                    range.max = !range.max || _.max(values) > range.max ? _.max(values) : range.max;
                }
            }
            return range;
        }, {min: undefined, max: undefined});
    }

    protected getYZoomRanges(plots: List<IPlot>): {min: number, max: number} {
        return plots.reduce((range: any, plot: IPlot) => {
            if (!plot.get('data').isEmpty()) {
                const scatterChartData = <OutputScatterPlotEntry[]>plot.get('data').get('data').toJS();
                const values = scatterChartData.map(entry => entry.y);
                if (values) {
                    range.min = !range.min || _.min(values) < range.min ? _.min(values) : range.min;
                    range.max = !range.max || _.max(values) > range.max ? _.max(values) : range.max;
                }
            }
            return range;
        }, {min: undefined, max: undefined});
    }
}
