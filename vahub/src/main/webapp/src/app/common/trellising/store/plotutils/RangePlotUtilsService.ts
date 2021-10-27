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

import {
    ITrellises, TrellisCategory, IPlot, TabId, ILegend, ILegendEntry, LegendSymbol,
    IRangePlot
} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';
import {RangePlotService} from '../../grid/plot/plottypes/rangeplot/RangePlotService';
import {JoinedRangePlotService} from '../../grid/plot/plottypes/joinedrangeplot/JoinedRangePlotService';
import RangeChartSeries = Request.RangeChartSeries;
import ColoredRangeChartSeries = Request.ColoredRangeChartSeries;
import OutputRangeChartEntry = Request.OutputRangeChartEntry;

export class RangePlotUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        let legendEntries: any;
        let title = _.filter(<ITrellises[]>trellises.toJS(), {'category': TrellisCategory.NON_MANDATORY_SERIES})
            .map(x => x.trellisedBy).join(', ');
        title = this.getLegendTitle(tabId, title);
        plotsDatas.forEach((plot: IPlot) => {
            const rangePlot: IRangePlot = plot.get('data').toJS();

            legendEntries = <any>rangePlot.map((data: RangeChartSeries<any, any>) => {
                let color: string;

                if ((<ColoredRangeChartSeries>data).color) {
                    color = (<ColoredRangeChartSeries>data).color;
                } else if (tabId === TabId.SINGLE_SUBJECT_RENAL_LINEPLOT) {
                    color = JoinedRangePlotService.renalColourPalette(<any>data.name);
                } else {
                    color = RangePlotService.colourPalette(<any>data.name);
                }

                return <ILegendEntry>{
                    label: data.name,
                    color,
                    symbol: LegendSymbol.CIRCLE
                };
            });

            if (title) {
                const currentEntry = _.find(legends, {'title': title});
                if (currentEntry) {
                    legends.forEach((legend: ILegend) => {
                        if (legend.title === title) {
                            const currentEntries = legend.entries;
                            legend.entries = _.uniqBy(_.flatten([currentEntries, legendEntries]), 'label').reverse();
                        }
                    });
                } else {
                    legends.push({entries: legendEntries.reverse(), title: title});
                }
            } else {
                legendEntries.forEach(entry => entry.label = 'All');
                legendEntries = _.uniqBy(legendEntries, e => JSON.stringify(e));
                legends.push({entries: legendEntries, title: 'All'});
            }
        });
        return _.uniqBy(legends, e => JSON.stringify(e));
    }

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        const rangePlotData = <IRangePlot>plot.get('data');
        const series = <RangeChartSeries<any, any>[]>rangePlotData.toJS();
        const categories = _.chain(series)
            .map(s => s.data)
            .flatten()
            .map('x')
            .uniq()
            .sortBy()
            .value();
        if (categories) {
            range.max = (categories.length - 1) > range.max ? (categories.length - 1) : range.max;
        }
        return range;
    }

    protected getContinuousXZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        const rangePlotData = <IRangePlot>plot.get('data');
        const series = <RangeChartSeries<any, any>[]>rangePlotData.toJS();
        return series.reduce((plotRange: any, data: RangeChartSeries<any, any>) => {
            return data.data.reduce((sRange, entry: OutputRangeChartEntry) => {
                /**
                 * change it so that continious zoom for range plot
                 * doesn't care about ranks
                 * just as continious zoom for boxplot
                 * @ref BoxPlotUtilsService.getContinuousXZoomRanges
                 */
                const x = parseFloat(entry.x);

                if (entry.x > sRange.max) {
                    sRange.max = x;
                }
                if (entry.x < sRange.min) {
                    sRange.min = x;
                }
                return sRange;
            }, plotRange);
        }, <{ max: number, min: number }>{
            max: RangePlotUtilsService.DEFAULT_MAX,
            min: RangePlotUtilsService.DEFAULT_MIN
        });
    }
}
