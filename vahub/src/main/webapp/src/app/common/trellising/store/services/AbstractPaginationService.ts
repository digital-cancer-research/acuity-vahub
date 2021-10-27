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
