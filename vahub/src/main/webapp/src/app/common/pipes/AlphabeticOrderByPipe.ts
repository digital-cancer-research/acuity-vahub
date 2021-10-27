import {Pipe, PipeTransform} from '@angular/core';
import * as  _ from 'lodash';

@Pipe({name: 'alphabeticSort'})
export class AlphabeticOrderByPipe implements PipeTransform {
    transform(array: Array<any>, groupName, sortProperty = 'label'): Array<any> {
        if (_.isEmpty(array)) {
            return array;
        }
        return _.sortBy(array, function (a) {
            return a.label === 'Empty' || a.label === '(Empty)' ? '~' : a.label;
        });
    }
}
