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

import {ITrellises, TrellisCategory} from '../ITrellising';

export abstract class AbstractPaginationService {

    protected static calculateTrellisCombinationForEachChart(trellisOptions: string[][]): string[][] {
        return _.unzip(<string[][]>trellisOptions.map((value: string[], index: number) => {

            let thisSubLength = 1;
            let thisUpperLength = 1;
            trellisOptions.forEach((subValue: string[], subindex: number) => {
                if (subindex > index) {
                    thisSubLength *= subValue.length;
                }
                if (subindex < index) {
                    thisUpperLength *= subValue.length;
                }
            });

            const higherSet: string[][][] = [];
            for (let j = 0; j < thisUpperLength; j++) {
                higherSet.push(value.map((option: string) => {
                    const optionSet: string[] = [];
                    for (let i = 0; i < thisSubLength; i++) {
                        optionSet.push(option);
                    }
                    return optionSet;
                }));
            }

            return <string[]>_.flatten(_.flatten(higherSet));
        }));
    }

    protected static getNonSeriesTrellisOptions(trellises: ITrellises[]): ITrellises[]  {
        return _.reject(trellises, {'category': TrellisCategory.NON_MANDATORY_SERIES});
    }

    protected static getNonSeriesTrellisOptionValues(trellises: ITrellises[]): any  {
        return <any> _.reject(trellises, {'category': TrellisCategory.NON_MANDATORY_SERIES}).map((value: ITrellises) => {
            return value.trellisOptions;
        });
    }
}
