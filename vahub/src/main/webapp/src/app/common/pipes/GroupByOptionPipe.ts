import {Pipe, PipeTransform} from '@angular/core';

import {GroupBySetting} from '../trellising/store/actions/TrellisingActionCreator';

@Pipe({name: 'toGroupByOption'})
export class GroupByOptionPipe implements PipeTransform {

    transform(yAxisOption: GroupBySetting): string {
        if (yAxisOption) {
            return yAxisOption.get('groupByOption');
        }
        return '';
    }
}
