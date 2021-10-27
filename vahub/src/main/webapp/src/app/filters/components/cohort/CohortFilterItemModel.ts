import * as  _ from 'lodash';

import {FILTER_TYPE, Values, ValuesWithEmpty} from '../dtos';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class CohortFilterItemModel extends BaseFilterItemModel {

    selectedValues: any[];

    constructor(key: string, displayName: string, sizeDynamic?: boolean, maxSize?: number) {
        super(FILTER_TYPE.COHORT_EDITOR, key, displayName);
        this.reset();
    }

    reset(): void {
        this.selectedValues = [];
    }

    clear(): void {
        this.selectedValues = [];
    }

    clearNotAppliedSelectedValues(): void {
        // Do nothing
    }

    toServerObject(manuallyApplied = false): ValuesWithEmpty {
        if (_.isEmpty(this.selectedValues)) {
            return null;
        } else {
            return {
                values: this.selectedValues
            };
        }
    }

    fromServerObject(returnedServerObject: Values): void {
        // Does not go to the server
    }

    updateNumberOfSelectedFilters(): void {
        // Do nothing
    }

    setSelectedValues(values: any): void {
        // Do nothing
    }
}
