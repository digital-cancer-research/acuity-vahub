import {Component, Input, OnInit} from '@angular/core';
import {includes, remove} from 'lodash';

import {UnselectedCheckListFilterItemModel} from './UnselectedCheckListFilterItemModel';
import {BaseFilterItemModel} from '../BaseFilterItemModel';
import {DatasetViews} from '../../../security/DatasetViews';

@Component({
    selector: 'unselectedchecklistfilter',
    templateUrl: 'UnselectedCheckListFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class UnselectedCheckListFilterComponent {

    @Input() model: UnselectedCheckListFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    toggleSelectedItem(item: string): void {
        if (includes(this.model.selectedValues, item)) {
            remove(this.model.selectedValues, (selectedValue: string) => {
                return selectedValue === item;
            });
        } else {
            this.model.selectedValues.push(item);
        }
    }

    isItemPresentInAvailableList(item: any): boolean {
        return this.model.isInAvaliableList(item);
    }

    isItemPresentInSelectedList(item: any): boolean {
        return this.model.isInSelectedList(item);
    }
}
