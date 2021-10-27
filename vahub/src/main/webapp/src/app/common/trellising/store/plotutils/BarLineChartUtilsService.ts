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

import {ITrellises, TrellisCategory, IPlot, TabId, ILegend, ILegendEntry, LegendSymbol} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';
import OutputBarChartData = Request.OutputBarChartData;
import OutputBarChartEntry = Request.OutputBarChartEntry;
import OutputOvertimeData = InMemory.OutputOvertimeData;
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

export class BarLineChartUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        let legendEntries: any;
        let title = _.filter(<ITrellises[]>trellises.toJS(), {'category': TrellisCategory.NON_MANDATORY_SERIES})
            .map(x => x.trellisedBy).join(', ');
        title = this.getLegendTitle(tabId, title);
        plotsDatas.forEach((plot: IPlot) => {
            const barLineChartData: OutputOvertimeData = plot.get('data').toJS();
            legendEntries = _.uniqBy(barLineChartData.series.map((data) => {
                return <ILegendEntry>{
                    label: data.name,
                    color: (<ColoredOutputBarChartData>data).color,
                    symbol: LegendSymbol.CIRCLE
                };
            }), e => JSON.stringify(e));

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
                legends.push({entries: legendEntries, title: 'All'});
            }
        });
        return _.uniqBy(legends, e => JSON.stringify(e));
    }

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        if (!plot.get('data').isEmpty()) {
            const chartCategoriesSize = plot.getIn(['data', 'categories']).size;
            if (chartCategoriesSize) {
                range.max = (chartCategoriesSize - 1) > range.max ? (chartCategoriesSize - 1) : range.max;
            }
        }
        return range;
    }

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        if (plot.get('data').isEmpty()) {
            return range;
        }
        const categories: string[] = plot.getIn(['data', 'categories']).toJS();
        const stacks: number[] = plot.getIn(['data', 'series']).toJS()
            .reduce((stack: { [key: string]: number }, plot1: OutputBarChartData) => {
                if (!stack) {
                    stack = {};
                    categories.forEach((category) => {
                        stack[category] = 0;
                    });
                }
                plot1.series.forEach((v: OutputBarChartEntry, index: number) => {
                    stack[v.category] += v.value;
                });
                return stack;
            }, undefined);
        if (stacks) {
            const values = [];
            for (const key in stacks) {
                if (stacks[key] > 0) {
                    values.push(stacks[key]);
                }
            }
            const maxValue = _.max(values);
            range.max = maxValue > range.max ? maxValue : range.max;
        }
        return range;
    }
}
