import {clone, cloneDeep, isUndefined} from 'lodash';
import {ListFilterItemModel} from '../list/ListFilterItemModel';
import {FILTER_TYPE, ValuesWithEmpty, Values} from '../dtos';
import {DatasetViews} from '../../../security/DatasetViews';

export class UnselectedCheckListFilterItemModel extends ListFilterItemModel {

    public initialValues: any[];
    public isDisabled = false;
    datasetViews: DatasetViews;

    constructor(key: string, displayName: string, datasetViews?: DatasetViews) {
        super(key, displayName);
        this.type = FILTER_TYPE.UNSELECTED_CHECK_LIST;
        this.reset();
        this.datasetViews = datasetViews;
    }

    reset(): void {
        super.reset();
        this.initialValues = [];
        this.appliedSelectedValues = [];
        this.isDisabled = false;
        this.selectedValues = [];
    }

    clear(): void {
        this.selectedValues = [];
        this.appliedSelectedValues = [];
    }

    clearNotAppliedSelectedValues(): void {
        if (this.appliedSelectedValues.length > 0) {
            this.selectedValues = clone(this.appliedSelectedValues);
        } else {
            this.selectedValues = [];
        }
    }

    showNumberOfSelectedFilters(): boolean {
        return this.numberOfSelectedFilters !== 0 && this.numberOfSelectedFilters < this.initialValues.length;
    }

    toServerObject(manuallyApplied = false): ValuesWithEmpty {

        if (manuallyApplied) {
            this.appliedSelectedValues = clone(this.selectedValues);
        }
        if (this.initialValues && 0 < this.appliedSelectedValues.length) {
            const serverObject: ValuesWithEmpty = {};

            //  check if has null or empty, if so add includeEmptyValues = true to the object
            if (this.hasEmpty()) {
                serverObject['includeEmptyValues'] = true;
            }

            // filter out the null/emptys
            serverObject['values'] = this.removeEmpty(this.appliedSelectedValues);

            return serverObject;
        } else {
            return null;
        }
    }

    /**
     * returnedServerObject: { values ['ABDOMINAL PAIN'] }
     */
    fromServerObject(returnedServerObject: Values): void {
        if (returnedServerObject) {
            this.availableValues = returnedServerObject.values;

            if (this.initialValues.length < returnedServerObject.values.length) {

                this.initialValues = returnedServerObject.values;
                this.selectedValues = [];
            } else if (!this.initialValues.length && this.datasetViews) {
                const tab = window.location.hash.slice(10).split('/')[1];
                const disabledFilterName = this.datasetViews.getFilterIfDisabled(tab);
                this.isDisabled = disabledFilterName !== null;
                this.initialValues = [disabledFilterName];
            }
        } else {
            this.availableValues = [];
        }
    }
}
