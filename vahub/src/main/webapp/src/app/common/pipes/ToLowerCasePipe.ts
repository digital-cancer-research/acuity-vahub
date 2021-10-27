import {Pipe, PipeTransform} from '@angular/core';
import {TabId} from '../trellising';

@Pipe({name: 'toLowerCase'})
export class ToLowerCasePipe implements PipeTransform {
    transform(value: string, tabId: TabId): string {
        if (tabId === TabId.TUMOUR_RESPONSE_WATERFALL_PLOT) {
            return value.toLowerCase();
        }
        return value;
    }
}
