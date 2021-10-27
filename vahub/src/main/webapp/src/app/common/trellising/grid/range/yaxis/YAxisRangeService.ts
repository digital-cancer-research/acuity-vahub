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
import {List} from 'immutable';
import {isNil, max, maxBy, min, reject} from 'lodash';

import {IPlot, PlotType, TabId, TrellisCategory} from '../../../index';
import {Trellises} from '../../plot/services/AxisLabelService';
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import RangeChartSeries = Request.RangeChartSeries;
import OutputRangeChartEntry = Request.OutputRangeChartEntry;
import OutputBoxplotEntry = Request.OutputBoxplotEntry;
import OutputHeatMapData = InMemory.OutputHeatMapData;

export interface Range {
    min: number;
    max: number;
}

@Injectable()
export class YAxisRangeService {
    plotTypes = PlotType;
    tabIds = TabId;

    public getRange(row: number, col: number, columns: number, limit: number, plots: List<IPlot>, tabId: TabId): Range {
        if (!columns) {
            return;
        }
        const index = row * columns + col;
        const plot: IPlot = plots.get(index, undefined);
        if (!plot) {
            return;
        }
        const plotJS: any = plot.toJS();
        const plotTrellis: Trellises[] = plotJS.trellising;
        const plotData: any = plotJS.data;
        if (!plotTrellis || !plotData) {
            return;
        }

        const measurementTrellis = plotTrellis.find((trellis: Trellises) => {
            return trellis.category === TrellisCategory.MANDATORY_TRELLIS;
        });
        const plotMeasurement: string = measurementTrellis ? measurementTrellis.trellisOption : undefined;

        const plotsWithMeasurement: List<IPlot> = plots.filter((value, key) => {
            const thisPlot = value.toJS();
            //Only include visible plots
            if (key >= limit) {
                return false;
            }
            //Include plots with the same MANDATORY_TRELLIS
            const trellis: Trellises[] = thisPlot.trellising;
            if (!trellis) {
                return false;
            } else if (!plotMeasurement) {
                return true;
            } else {
                return trellis.find((trellisis: Trellises) => {
                    return trellisis.category === TrellisCategory.MANDATORY_TRELLIS &&
                        trellisis.trellisOption === plotMeasurement;
                }) !== undefined;
            }
        }).toList();

        return this.getYRange(plotsWithMeasurement, tabId);

    }

    private getYRange(plots: List<IPlot>, tabId: TabId): Range {
        return plots.reduce((range, plot) => {
            let plotRange: Range = {min: 0, max: 100};
            const plotType: PlotType = plot.get('plotType');

            if (!plot.get('data').isEmpty()) {
                const plotData: any = plot.get('data').toJS();
                switch (plotType) {
                    case this.plotTypes.BOXPLOT:
                        plotRange = this.getBoxPlotRange(plotData);
                        break;
                    case this.plotTypes.WATERFALL:
                        plotRange = this.getWaterfallPlotRange(plotData);
                        break;
                    case this.plotTypes.RANGEPLOT:
                    case this.plotTypes.JOINEDRANGEPLOT:
                        plotRange = this.getRangePlotRange(plotData);
                        break;
                    case this.plotTypes.SCATTERPLOT:
                        plotRange = this.getScatterPlotRange(plotData);
                        if (tabId === this.tabIds.LIVER_HYSLAW || tabId === this.tabIds.SINGLE_SUBJECT_LIVER_HYSLAW) {
                            plotRange.min = 0.0;
                            plotRange.max = plotRange.max > 4.0 ? plotRange.max : 4.0;
                        }
                        break;
                    case this.plotTypes.ERRORPLOT:
                        plotRange = this.getErrorPlotRange(plotData);
                        break;
                    case this.plotTypes.HEATMAP:
                        plotRange = this.getHeatmapPlotRange(plotData);
                        break;
                    default:
                        break;
                }
                range.min = (range.min === undefined || range.min === null || range.min > plotRange.min) ? plotRange.min : range.min;
                range.max = (range.max === undefined || range.max === null || range.max < plotRange.max) ? plotRange.max : range.max;
            }
            return range;
        }, {min: undefined, max: undefined});
    }

    private getBoxPlotRange(data: OutputBoxplotEntry[]): Range {
        return data.reduce((range: Range, stat: OutputBoxplotEntry) => {
            let yValues: number[] = [];
            yValues.push(stat.lowerQuartile, stat.lowerWhisker, stat.median, stat.upperQuartile, stat.upperWhisker);
            stat.outliers.forEach((outlier) => {
                yValues.push(outlier.outlierValue);
            });
            yValues = reject(yValues, v => isNil(v));
            if (yValues.length === 0) {
                return {min: range.min, max: range.max};
            } else {
                const minY = min(yValues) - 0.05 * (max(yValues) - min(yValues));
                const maxY = max(yValues) + 0.05 * (max(yValues) - min(yValues));
                range.min = ((!range.min && range.min !== 0) || range.min > minY) ? minY : range.min;
                range.max = ((!range.max && range.max !== 0) || range.max < maxY) ? maxY : range.max;
                return range;
            }
        }, {min: undefined, max: undefined});
    }

    private getRangePlotRange(data: RangeChartSeries<any, any>[]): Range {
        const range = data.reduce((range1: Range, series: RangeChartSeries<any, any>) => {
            return series.data
                .filter(x => {
                    return x.y !== null;
                })
                .reduce((sRange: Range, entry: OutputRangeChartEntry) => {
                    const entryMin = entry.min === null ? entry.y : entry.min;
                    const entryMax = entry.max === null ? entry.y : entry.max;
                    sRange.min = (sRange.min === undefined || sRange.min === null || sRange.min > entryMin) ? entryMin : sRange.min;
                    sRange.max = (sRange.max === undefined || sRange.max === null || sRange.max < entryMax) ? entryMax : sRange.max;
                    return sRange;
                }, range1);
        }, {min: undefined, max: undefined});

        return {
            min: range.min - 0.05 * Math.abs(range.max - range.min),
            max: range.max + 0.05 * Math.abs(range.max - range.min)
        };
    }

    private getWaterfallPlotRange(data): Range {
        const maxYPoint = maxBy(data.entries, (item: any) => {
            return item.y;
        });
        return <Range>{
            min: -100,
            max: maxYPoint.y > 100 ? maxYPoint.y : 100
        };
    }

    private getScatterPlotRange(data: TrellisedScatterPlot<any, any>): Range {
        return data.data.reduce((range: Range, entry: any) => {
            range.min = (range.min === undefined || range.min === null || range.min > entry.y) ? entry.y : range.min;
            range.max = (range.max === undefined || range.max === null || range.max < entry.y) ? entry.y : range.max;
            return range;
        }, {min: undefined, max: undefined});
    }

    private getErrorPlotRange(data: InMemory.ShiftPlotData): Range {
        return data.data.reduce((range: Range, entry: InMemory.OutputShiftPlotEntry) => {
            range.min = (range.min === undefined || range.min === null || range.min > entry.low) ? entry.low : range.min;
            range.max = (range.max === undefined || range.max === null || range.max < entry.high) ? entry.high : range.max;
            return range;
        }, {min: undefined, max: undefined});
    }

    private getHeatmapPlotRange(data: OutputHeatMapData): Range {
        return <Range>{
            min: 0,
            max: data.ycategories.length - 1
        };
    }
}
