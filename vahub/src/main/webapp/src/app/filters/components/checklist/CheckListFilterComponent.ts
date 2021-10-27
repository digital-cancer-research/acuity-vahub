import {Component, Input} from '@angular/core';

import {CheckListFilterItemModel} from './CheckListFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'checklistfilter',
    templateUrl: 'CheckListFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class CheckListFilterComponent {

    @Input() model: CheckListFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    toggleSelectedItem(item: string): void {
        if (_.includes(this.model.selectedValues, item)) {
            _.remove(this.model.selectedValues, (selectedValue: string) => {
                return selectedValue === item;
            });
        } else {
            this.model.selectedValues.push(item);
        }
    }

    isItemPresentInAvailableList(item: any): boolean {
        return this.model.isInAvaliableList(item);
    }

    isLastAvailableItem(item: any): boolean {
        return this.model.selectedValues.length === 1 && this.model.isInSelectedList(item);
    }

    isItemPresentInSelectedList(item: any): boolean {
        return this.model.isInSelectedList(item);
    }
}
