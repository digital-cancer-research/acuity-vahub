import {Injectable} from '@angular/core';
import {List, fromJS} from 'immutable';
import {DynamicAxis} from '../../../common/trellising/store/ITrellising';
import {AxisLabelService} from '../../../common/trellising/axislabel/AxisLabelService';
import {XAxisLabelService} from '../../../common/trellising/axislabel/xaxis/XAxisLabelService';
import * as _ from 'lodash';

@Injectable()
export class TimelineAxisLabelService {
    generateAdditionalSelection(option: DynamicAxis, options: List<DynamicAxis>): string[] {
        return options.toArray()
            .filter((x: DynamicAxis) => x.get('value') === option.get('value'))
            .reduce((acc: string[], x: DynamicAxis) => {
                const selectableValue = x.get('stringarg');

                if (!_.isNull(selectableValue)) {
                    acc.push(selectableValue);
                }
                return acc;
            }, []);
    }

    /**
     * Get list of options, remove dupes and convert it to iterable array for angular
     * @param options
     * @returns string[]
     */
    generateAvailableValueStrings(options: List<DynamicAxis>): string[] {
        return _.uniqWith(
            options.toArray()
            .map((x: DynamicAxis) => {
                return x.get('value');
            }),
            (current, next) => current === next
        );
    }

    /**
     * Get a single option that matches value selection
     * @param event {Event}
     * @param options {List<DynamicAxis>}
     * @returns {undefined|DynamicAxis}
     */
    optionFromValueSelection(event: any, options: List<DynamicAxis>): DynamicAxis {
        const selectedValue = event.target.value;
        return options.toArray()
            .find((x: DynamicAxis) => x.get('value') === selectedValue);
    }

    optionFromAdditionalSelection(event: any, localOption: DynamicAxis, options: List<DynamicAxis>): DynamicAxis {
        const selectedValue: any = event.target.value;
        return options.toArray()
            .find((x: DynamicAxis) => x.get('value') === localOption.get('value')
                && x.get('stringarg') === selectedValue);
    }
}
