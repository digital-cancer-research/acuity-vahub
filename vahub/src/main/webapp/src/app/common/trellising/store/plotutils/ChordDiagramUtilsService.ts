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

import {List, Map} from 'immutable';

import {BaseChartUtilsService} from './BaseChartUtilsService';
import {ILegend, ILegendEntry, IPlot, ITrellises, LegendSymbol, TabId} from '../ITrellising';

export class ChordDiagramUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        const trellisedBySetting = trellises.getIn([0, 'trellisedBy']);
        plotsDatas.forEach((plot: IPlot) => {
            const extractedColorMap = plot.getIn(['data', 0, trellisedBySetting, 'colorBook']);
            const colorMap: Map<string, string> = extractedColorMap ? extractedColorMap.toJS() : {};
            const legendEntries = Object.keys(colorMap).map(name => {
                return {
                    label: name,
                    color: colorMap[name],
                    symbol: LegendSymbol.CIRCLE
                };
            }) as ILegendEntry[];
            legends.push({entries: legendEntries, title: trellisedBySetting});
        });
        return legends;
    }
}
