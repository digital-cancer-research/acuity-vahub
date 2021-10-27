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
import {IPlot, PlotType, TabId, TrellisCategory, TrellisDesign} from '../../../index';
import {List} from 'immutable';
import {Trellises} from '../../plot/services/AxisLabelService';
import * as  _ from 'lodash';
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import RangeChartSeries = Request.RangeChartSeries;
import OutputBoxplotEntry = Request.OutputBoxplotEntry;
import OutputHeatMapData = InMemory.OutputHeatMapData;

export interface Range {
    min: number;
    max: number;
}

@Injectable()
export class XAxisRangeService {
    plotTypes = PlotType;
    tabIds = TabId;
    trellisDesigns = TrellisDesign;

    public getRange(row: number, col: number, columns: number, limit: number, plots: List<IPlot>,
                    tabId: TabId, trellisDesign: TrellisDesign): Range {
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

        if (trellisDesign === this.trellisDesigns.CATEGORICAL_OVER_TIME) {
            return this.getXRangeFromCategories(plot);
        }

        const measurementTrellis = _.find(plotTrellis, { 'category': TrellisCategory.MANDATORY_TRELLIS });
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
                return _.find(trellis, { 'category': TrellisCategory.MANDATORY_TRELLIS, 'trellisOption': plotMeasurement }) !== undefined;
            }
        }).toList();

        return this.getXRange(plotsWithMeasurement, tabId);
    }

    private getXRangeFromCategories(plot: IPlot): Range {
        const plotType: PlotType = plot.get('plotType');
        const plotData: any = plot.get('data').toJS();
        let length: number;
        switch (plotType) {
            case this.plotTypes.BOXPLOT:
                length = plotData.map((entry: OutputBoxplotEntry) => {
                    return entry.x;
                }).length;
                return { min: 0, max: length - 1 };
            case this.plotTypes.JOINEDRANGEPLOT:
            case this.plotTypes.RANGEPLOT:
                length = _.chain(plotData)
                    .map((entry: RangeChartSeries<any, any>) =>
                        entry.data
                    ).flatten()
                    .map('x')
                    .uniq()
                    .sortBy(x => parseFloat(<string>x))
                    .value().length;
                return { min: 0, max: length - 1 };
            case this.plotTypes.SIMPLE_LINEPLOT:
                length = _.chain(plotData)
                    .map('series')
                    .flatten()
                    .map('x')
                    .uniq()
                    .sortBy(x => parseFloat(<string>x))
                    .value().length;
                return { min: 0, max: length - 1 };
            default:
                return;
        }
    }

    private getXRange(plots: List<IPlot>, tabId: TabId): Range {
        const range = plots.reduce((range1, plot) => {
            let plotRange: Range = { min: 0, max: 100 };
            const plotType: PlotType = plot.get('plotType');
            if (!plot.get('data').isEmpty()) {
                const plotData: any = plot.get('data').toJS();
                switch (plotType) {
                    case this.plotTypes.BOXPLOT:
                        plotRange = this.getBoxPlotRange(plotData);
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
                range1.min = (!range1.min || range1.min > plotRange.min) ? plotRange.min : range1.min;
                range1.max = (!range1.max || range1.max < plotRange.max) ? plotRange.max : range1.max;
            }
            return range1;
        }, { min: undefined, max: undefined });
        if (range.min === range.max && range.min !== undefined) {
            range.min -= 1.0;
            range.max += 1.0;
        }
        return range;
    }

    private getBoxPlotRange(data: OutputBoxplotEntry[]): Range {
        // Cast to any to stop lodash typings from complaining
        const min = (<any>_.chain(data).map('x').map(Number)).min().value();
        const max = (<any>_.chain(data).map('x').map(Number)).max().value();
        return { min: min, max: max };
    }

    private getScatterPlotRange(data: TrellisedScatterPlot<any, any>): Range {
        return data.data.reduce((range: Range, entry: any) => {
            range.min = (!range.min || range.min > entry.x) ? entry.x : range.min;
            range.max = (!range.max || range.max < entry.x) ? entry.x : range.max;
            return range;
        }, { min: undefined, max: undefined });
    }

    private getErrorPlotRange(data: InMemory.ShiftPlotData): Range {
                return data.data.reduce((range: Range, entry: InMemory.OutputShiftPlotEntry) => {
            range.min = (range.min > entry.x) && (entry.x !== null && entry.x !== undefined) ? entry.x : range.min;
            range.max = (range.max < entry.x) ? entry.x : range.max;
            return range;
        }, { min: Infinity, max: -Infinity });
    }

    private getHeatmapPlotRange(data: OutputHeatMapData): Range {
        return <Range>{
            min: 0,
            max: data.xcategories.length === 1 ? 1 : data.xcategories.length - 1
        };
    }

}
