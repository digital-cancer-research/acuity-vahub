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
import {
    BaseChartUtilsService,
    ScatterPlotUtilsService,
    StackedBarChartUtilsService,
    RangePlotUtilsService,
    LineChartUtilsService,
    GroupedBarChartUtilsService,
    BoxPlotUtilsService,
    BarLineChartUtilsService,
    SimpleLinePlotUtilsService,
    HeatmapPlotUtilsService,
    WaterfallPlotUtilsService,
    ColumnRangeUtilsService,
    ChordDiagramUtilsService
} from '../plotutils/index';
import {IPlot, PlotType} from '../ITrellising';


@Injectable()
export class PlotUtilsFactory {
    private static stackedBarChartUtilsService = new StackedBarChartUtilsService();
    private static scatterPlotUtilsService = new ScatterPlotUtilsService();
    private static rangePlotUtilsService = new RangePlotUtilsService();
    private static lineChartUtilsService = new LineChartUtilsService();
    private static groupedBarChartUtilsService = new GroupedBarChartUtilsService();
    private static boxPlotUtilsService = new BoxPlotUtilsService();
    private static barLineChartUtilsService = new BarLineChartUtilsService();
    private static baseChartUtilsService = new BaseChartUtilsService();
    private static simpleLinePlotUtilsService = new SimpleLinePlotUtilsService();
    private static heatmapPlotUtilsService = new HeatmapPlotUtilsService();
    private static waterfallPlotUtilsService = new WaterfallPlotUtilsService();
    private static columnRangeUtilsService = new ColumnRangeUtilsService();
    private static chordDiagramUtilsService = new ChordDiagramUtilsService();

    public static getPlotUtilsService(plot: IPlot): BaseChartUtilsService {
        if (plot) {
            switch (plot.get('plotType')) {
                case PlotType.STACKED_BARCHART:
                    return this.stackedBarChartUtilsService;
                case PlotType.SCATTERPLOT:
                    return this.scatterPlotUtilsService;
                case PlotType.RANGEPLOT:
                case PlotType.JOINEDRANGEPLOT:
                    return this.rangePlotUtilsService;
                case PlotType.LINECHART:
                    return this.lineChartUtilsService;
                case PlotType.GROUPED_BARCHART:
                    return this.groupedBarChartUtilsService;
                case PlotType.BOXPLOT:
                    return this.boxPlotUtilsService;
                case PlotType.BARLINECHART:
                    return this.barLineChartUtilsService;
                case PlotType.HEATMAP:
                    return this.heatmapPlotUtilsService;
                case PlotType.SIMPLE_LINEPLOT:
                    return this.simpleLinePlotUtilsService;
                case PlotType.WATERFALL:
                    return this.waterfallPlotUtilsService;
                case PlotType.ERRORPLOT:
                    return this.baseChartUtilsService;
                case PlotType.COLUMNRANGE:
                    return this.columnRangeUtilsService;
                case PlotType.CHORD:
                    return this.chordDiagramUtilsService;
                default:
                    console.warn(`Plot type is undefined ${JSON.stringify(plot)}`);
                    return this.baseChartUtilsService;
            }
        } else {
            console.warn(`Plot is undefined ${JSON.stringify(plot)}`);
            return this.baseChartUtilsService;
        }
    }
}
