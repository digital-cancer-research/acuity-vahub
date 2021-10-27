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

import {IBoxPlot, IPlot} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import OutputBoxplotEntry = Request.OutputBoxplotEntry;

export class BoxPlotUtilsService extends BaseChartUtilsService {

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        const boxPlotData = <IBoxPlot>plot.get('data');
        const stats = <OutputBoxplotEntry[]>boxPlotData.toJS();
        if (stats) {
            range.max = (stats.length - 1) > range.max ? (stats.length - 1) : range.max;
        }
        return range;
    }

    protected getContinuousXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        const boxPlotData = <IBoxPlot>plot.get('data');
        const stats = <OutputBoxplotEntry[]>boxPlotData.toJS();
        const plotRange = stats.reduce((plotRange1: any, stat: OutputBoxplotEntry) => {
            const x = parseFloat(stat.x);
            if (x > plotRange1.max) {
                plotRange1.max = x;
            }
            if (x < plotRange1.min) {
                plotRange1.min = x;
            }
            return plotRange1;
        }, <{ max: number, min: number }>{max: BoxPlotUtilsService.DEFAULT_MAX, min: BoxPlotUtilsService.DEFAULT_MIN});
        if (plotRange.min === plotRange.max && plotRange.max !== undefined) {
            const plotPad = (Math.abs(plotRange.min)) / 10;
            plotRange.min -= plotPad;
            plotRange.max += plotPad;
        }
        return plotRange;
    }
}
