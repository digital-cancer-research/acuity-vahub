import {Pipe, PipeTransform} from '@angular/core';
import * as  _ from 'lodash';
import {MAX_NUMBER} from '../trellising/store/ITrellising';

@Pipe({name: 'intervalsSort'})
export class IntervalsOrderByPipe implements PipeTransform {
    transform(array: Array<any>, groupName, sortProperty = 'label'): Array<any> {
        if (['WEIGHT', 'HEIGHT', 'TOTAL_DURATION_ON_STUDY', 'DOSE', 'DURATION_ON_STUDY'].indexOf(groupName) === -1 || _.isEmpty(array)) {
            return array;
        } else {
            const compareFunc: Function = (interval: string) => {
                switch (interval.toLowerCase()) {
                    case '(empty)':
                        return MAX_NUMBER - 1;
                    case 'total':
                        return MAX_NUMBER;
                    default:
                        const matchedString = interval.split('-');
                        return parseFloat(matchedString[0]);
                }
            };

            return array.sort((a: any, b: any) => {
                const intervalA = compareFunc(a[sortProperty]);
                const intervalB = compareFunc(b[sortProperty]);
                if (intervalA < intervalB) {
                    return -1;
                } else if (intervalA > intervalB) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }
}
