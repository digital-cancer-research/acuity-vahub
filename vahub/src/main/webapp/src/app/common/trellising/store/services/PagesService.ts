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

import {List} from 'immutable';
import * as  _ from 'lodash';

import {ITrellises} from '../ITrellising';
import {AbstractPaginationService} from './AbstractPaginationService';

export class PagesService extends AbstractPaginationService {

    public static pages(trellises: List<ITrellises>, limit: number): number[] {
        if (!trellises) {
            return null;
        } else {

            const trellisesJS = trellises.toJS();
            const trellisOptions = this.getNonSeriesTrellisOptionValues(trellisesJS);

            if (trellisOptions.length > 0) {
                const allTrellisCombinations = this.calculateTrellisCombinationForEachChart(trellisOptions);
                const numberPages = Math.ceil(allTrellisCombinations.length / limit);
                return _.range(1, numberPages + 1, 1);
            } else {
                return [1];
            }
        }
    }

    public static page(limit: number, offset: number): number {
        if (!_.isNumber(limit) || !_.isNumber(offset) || limit === 0) {
            return null;
        } else {
            return Math.ceil(offset / limit);
        }
    }

    public static actualColumns(columnLimit: number, plotsLength: number, page: number, limit: number): number {
        if (!columnLimit || !page || !limit) {
            return 1;
        } else {
            if (page > 1 || plotsLength === limit) {
                return columnLimit;
            } else {
                switch (plotsLength) {
                    case 1:
                        return 1;
                    case 2:
                    case 3:
                    case 4:
                        return 2;
                    case 5:
                    case 6:
                        return 3;
                    default:
                        return 1;
                }
            }
        }
    }

    public static actualLimit(columnLimit: number, plotsLength: number, page: number, limit: number): number {
        if (!columnLimit || !page || !limit) {
            return 1;
        } else {
            if (page > 1 || plotsLength === limit) {
                return limit;
            } else {
                switch (plotsLength) {
                    case 2:
                        return 2;
                    case 3:
                    case 4:
                        return 4;
                    case 5:
                    case 6:
                        return 6;
                    default:
                        return 1;
                }
            }
        }
    }
}
