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

import {fromJS, List} from 'immutable';
import * as  _ from 'lodash';

import {ITrellises, IPlot, PlotRecord, TrellisCategory} from '../ITrellising';
import {AbstractPaginationService} from './AbstractPaginationService';

export class EmptyPlotsService extends AbstractPaginationService {

    public static generateEmptyPlots(limit: number, offset: number, trellises: List<ITrellises>): List<IPlot> {
        const trellisesJS: any[] = trellises.toJS();
        const series: any = _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES});
        const trellisOptions = this.getNonSeriesTrellisOptionValues(trellisesJS);

        if (trellisOptions.length > 0) {

            const allTrellisCombinations = this.calculateTrellisCombinationForEachChart(trellisOptions);

            const trellisCombinationsOnThisPage = allTrellisCombinations.slice(offset - 1, offset - 1 + limit);
            const trellisesNonSeries: ITrellises[] = this.getNonSeriesTrellisOptions(trellisesJS);

            return fromJS(trellisCombinationsOnThisPage.map((page: string[]) => {
                return new PlotRecord({
                    data: null,
                    plotType: null,
                    series: series,
                    trellising: page.map((option: string, index: number) => {
                        return {
                            category: trellisesNonSeries[index].category,
                            trellisedBy: trellisesNonSeries[index].trellisedBy,
                            trellisOption: option
                        };
                    })
                });
            }));
        } else {
            return fromJS([
                new PlotRecord({
                    data: null,
                    plotType: null,
                    trellising: [],
                    series: series
                })
            ]);
        }
    }
}
