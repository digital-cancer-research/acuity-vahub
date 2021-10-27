import {Pipe, PipeTransform} from '@angular/core';
import {AggregationType} from '../trellising/store';

@Pipe({name: 'toSettingsTitle'})
export class SettingsPipe implements PipeTransform {
    transform(value: string, noneIfEmpty = false, settingString?: string): string {
        switch (value) {
            case 'ANALYTE_CONCENTRATION_(NG/ML)':
                return settingString === AggregationType.SUBJECT_CYCLE ? value : 'AMEAN_' + value;
            case 'SUBJECT_CYCLE':
                return !noneIfEmpty ? 'NOT_APPLIED' : '';
            case 'SUBJECT':
                return 'PER_SUBJECT';
            case 'ANALYTE':
                return 'PER_ANALYTE';
            case 'DOSE':
                return 'PER_NOMINAL_DOSE';
            case 'VISIT':
                return 'PER_VISIT';
            case 'DOSE_PER_VISIT':
                return 'PER_NOMINAL_DOSE_PER_VISIT';
            case 'DOSE_PER_CYCLE':
                return 'PER_NOMINAL_DOSE_PER_CYCLE';
            case 'CYCLE':
                return 'PER_CYCLE';
            default:
                return value;
        }
    }

}
