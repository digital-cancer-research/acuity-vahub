import * as  _ from 'lodash';
import {List} from 'immutable';
import {DynamicAxis} from '../../store/ITrellising';
import {Injectable} from '@angular/core';

@Injectable()
export class XAxisLabelService {
    public generateAdditionalSelection(localOption: DynamicAxis, options: List<DynamicAxis>): any[] {
        return options.filter((x: DynamicAxis) => x.get('stringarg') === localOption.get('stringarg')
        && x.get('intarg') !== null
        && x.get('value') === localOption.get('value'))
            .map(x => x.get('intarg')).toArray();
    }

    public generateAvailableValueStrings(options: List<DynamicAxis>): any[] {
        const availableValueStrings = options.toArray()
            .map((x: DynamicAxis) => {
                const value = x.get('value');
                const stringarg = x.get('stringarg');
                return {
                    value: value,
                    stringarg: stringarg,
                    optionValue: JSON.stringify({value, stringarg}),
                    valueStringArg: !stringarg ? value : `${value} ${stringarg}`
                };
            });
        return _.sortBy(_.uniqWith(availableValueStrings, _.isEqual), item => {
            return item.value.toLowerCase() === 'none' ? -1 : 0; // Move All up to top
        });
    }

    public optionFromValueSelection(event: any, options: List<DynamicAxis>): DynamicAxis {
        const selection = <{ value: string, stringarg: string }>JSON.parse(event.target.value);
        return options
            .find((x: DynamicAxis) => {
                return x.get('stringarg', null) === selection.stringarg
                    && x.get('value') === selection.value
                    && (x.get('intarg') === 1 || x.get('intarg') === null);
            });
    }

    public optionFromAdditionalSelection(event: any, localOption: DynamicAxis, options: List<DynamicAxis>): DynamicAxis {
        const selection = <string>event.target.value;
        return options
            .find((x: DynamicAxis) => {
                return x.get('stringarg') === localOption.get('stringarg')
                    && x.get('value') === localOption.get('value')
                    && (x.get('intarg') === parseInt(selection, 10) || (parseInt(selection, 10) === 1 && x.get('intarg') === null));
            });
    }

}
