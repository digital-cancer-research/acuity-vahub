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

export class PaginatedTrellisService extends AbstractPaginationService {
    public static paginatedTrellis(limit: number, offset: number, trellises: List<ITrellises>): List<ITrellises> {

        const trellisesJS = trellises.toJS();
        _.remove(trellisesJS, (trellis: ITrellises) => {
            return trellis.trellisedBy === 'All';
        });
        const trellisOptions = this.getNonSeriesTrellisOptionValues(trellisesJS);

        if (trellisOptions.length > 0) {

            const allTrellisCombinations = this.calculateTrellisCombinationForEachChart(trellisOptions);
            const trellisCombinationsOnThisPage = allTrellisCombinations.slice(offset - 1, offset - 1 + limit);

            const trellisesNonSeries = this.getNonSeriesTrellisOptions(trellisesJS);
            const newTrellisJS = _.unzip(trellisCombinationsOnThisPage).map((newOptions: string[], index: number) => {
                return {
                    category: trellisesNonSeries[index].category,
                    trellisedBy: trellisesNonSeries[index].trellisedBy,
                    trellisOptions: _.uniq(newOptions)
                };
            });
            _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES}).forEach((trellisSeries: any) => {
                newTrellisJS.push({
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: trellisSeries.trellisedBy,
                    trellisOptions: trellisSeries.trellisOptions
                });
            });

            return fromJS(newTrellisJS);
        } else {
            const newTrellisJS = [];
            _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES}).forEach((trellisSeries: any) => {
                newTrellisJS.push({
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: trellisSeries.trellisedBy,
                    trellisOptions: trellisSeries.trellisOptions
                });
            });
            return fromJS(newTrellisJS);
        }
    }
}
