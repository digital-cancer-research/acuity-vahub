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
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;

export class LineChartUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        let legendEntries: any;
        let title = _.filter(<ITrellises[]>trellises.toJS(), {'category': TrellisCategory.NON_MANDATORY_SERIES})
            .map(x => x.trellisedBy).join(', ');
        title = this.getLegendTitle(tabId, title);
        plotsDatas.forEach((plot: IPlot) => {
            const lineChart: any = plot.get('data').toJS();
            legendEntries = _.uniqBy(lineChart.map((data) => {
                return <ILegendEntry> {
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

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        const lineChartCategories = <string[]>_.chain(plot.get('data').toJS())
            .flatMap(x => x.series)
            .map(x => x.category)
            .uniq()
            .value();
        if (lineChartCategories) {
            range.max = (lineChartCategories.length - 1) > range.max ? (lineChartCategories.length - 1) : range.max;
        }
        return range;
    }

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        const maxValue: number = plot.getIn(['data']).toJS().reduce((maxValue1: number, data: OutputOvertimeLineChartData) => {
            const maxSeriesValue = _.max(data.series.map(x => x.value)) * 1.1; // Add 10% to allow selection
            maxValue1 = maxValue1 < maxSeriesValue ? maxSeriesValue : maxValue1;
            return maxValue1;
        }, 0);
        range.max = maxValue > range.max ? maxValue : range.max;
        return range;
    }
}
