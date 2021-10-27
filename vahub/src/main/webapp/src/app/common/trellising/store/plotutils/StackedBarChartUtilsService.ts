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
import {ILegend, ILegendEntry, IPlot, ITrellises, LegendSymbol, TabId, TrellisCategory} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';
import OutputBarChartData = Request.OutputBarChartData;
import OutputBarChartEntry = Request.OutputBarChartEntry;

export class StackedBarChartUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        let legendEntries: any;
        let title = _.filter(<ITrellises[]>trellises.toJS(),
            {'category': TrellisCategory.NON_MANDATORY_SERIES}).map(x => x.trellisedBy).join(', ');
        title = this.getLegendTitle(tabId, title);
        plotsDatas.forEach((plot: IPlot) => {
            const barChart: any = plot.get('data').toJS();
            legendEntries = _.uniqBy(barChart.map((data) => {
                return <ILegendEntry>{
                    label: data.name,
                    color: data.color,
                    symbol: LegendSymbol.CIRCLE
                };
            }), e => JSON.stringify(e));

            if (title) {
                const currentEntry = _.find(legends, {'title': title});
                if (currentEntry) {
                    legends.forEach((legend: ILegend) => {
                        if (legend.title === title) {
                            const currentEntries = legend.entries;
                            legend.entries = _.uniqBy(_.flatten([currentEntries, legendEntries]), 'label');
                        }
                    });
                } else {
                    legends.push({entries: legendEntries, title: title});
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
            const barChartCategoriesSize = plot.get('data').first().get('categories').size;
            if (barChartCategoriesSize) {
                range.max = (barChartCategoriesSize - 1) > range.max ? (barChartCategoriesSize - 1) : range.max;
            }
        }
        return range;
    }

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        const stacks: number[] = plot.get('data').toJS().reduce((stack: { [key: string]: number }, plot1: OutputBarChartData) => {
            if (!stack) {
                stack = {};
                plot1.categories.forEach((category) => {
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
