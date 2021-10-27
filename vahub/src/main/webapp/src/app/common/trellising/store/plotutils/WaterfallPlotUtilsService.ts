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
    ITrellises,
    IPlot,
    TabId,
    ILegend,
    ILegendEntry,
    LegendSymbol,
    TrellisDesign,
    ZoomRanges
} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';

export class WaterfallPlotUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        let legendEntries: any;
        const title = this.getLegendTitle(tabId, trellises);
        plotsDatas.forEach((plot: IPlot) => {
            const WaterfallPlotData: any = plot.get('data').toJS();
            legendEntries = _.uniqBy(WaterfallPlotData.entries.map((data) => {
                const legendEntry: ILegendEntry = {
                    label: data.name,
                    color: data.color,
                    symbol: LegendSymbol.CIRCLE
                };
                return legendEntry;
            }), e => JSON.stringify(e));
            const uniqEntriesByColor = [];
            _.chain(legendEntries)
                .groupBy('color')
                .forEach((item) => {
                    for (let i = 1; i < item.length; i++) {
                        item[0].label += ` / ${item[i].label}`;
                    }
                    uniqEntriesByColor.push(item[0]);
                })
                .value();
            legends.push({entries: uniqEntriesByColor, title: title});
        });
        return _.uniqBy(legends, e => JSON.stringify(e));
    }

    calculateZoomRanges(plots: List<IPlot>, trellisDesign: TrellisDesign, tabId: TabId): ZoomRanges {
        const xMax = plots.reduce((size, plot): number => {
            return size + plot.getIn(['data', 'xcategories']).size;
        }, 0);

        const [yMin, yMax] = plots
            .map(plot => plot.getIn(['data', 'entries']))
            .flatten(1)
            .map(entry => entry.get('y'))
            .flatten(1)
            .reduce((accumulator, currentValue) => {
                return [
                    Math.min(currentValue, accumulator[0]),
                    Math.max(currentValue, accumulator[1])
                ];
            }, [0, 0]);

        return {
            x: {
                min: 0,
                max: xMax - 1
            },
            // By default, the plot should show at least the range -100%<y<+100% so that the scale of the bars is obvious.
            // In cases where there are y values > 100% (i.e. tumours could grow by more than 100%) a larger
            // range should be shown by default.
            y: {
                min: yMin > -100 ? -100 : yMin,
                max: yMax > 100 ? yMax : 100
            }
        };
    }

    protected getLegendTitle(tabId: TabId, trellises: any): string {
        switch (tabId) {
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                return trellises.getIn([0, 'trellisedBy']);
            default:
                return 'All';
        }
    }
}
