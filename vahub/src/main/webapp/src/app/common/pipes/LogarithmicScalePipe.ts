import {Pipe, PipeTransform} from '@angular/core';
import {lowerFirst} from 'lodash';
import {ScaleTypes} from '../trellising/store';
import {knownAcronyms} from '../utils/Utils';

@Pipe({name: 'logarithmic'})
export class LogarithmicScalePipe implements PipeTransform {

    transform(value: string, scaleType: ScaleTypes, preserveTitle?: boolean): string {
        if (!value || !scaleType) {
            return value;
        }
        // lowercase first letter unless first word is akronym
        const formattedValue = knownAcronyms.indexOf(value.split(' ')[0]) > -1 || preserveTitle ? value : lowerFirst(value);
        switch (scaleType) {
            case ScaleTypes.LOGARITHMIC_SCALE:
                return `Log scale ${formattedValue}`;
            case ScaleTypes.LINEAR_SCALE:
                return `Linear scale ${formattedValue}`;
            default:
                return value;
        }
    }
}

