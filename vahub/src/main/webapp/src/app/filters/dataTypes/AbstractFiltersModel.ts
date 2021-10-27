/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {BaseFilterItemModel} from '../components/BaseFilterItemModel';
import {isEmpty, isUndefined} from 'lodash';
import {Subject} from 'rxjs/Subject';

/**
 * Abstract class for all the FilterModels.
 */
export abstract class AbstractFiltersModel {
    public resetEventFilterEvent: Subject<any> = new Subject<any>();
    matchedItemsCount: number;
    itemsModels: BaseFilterItemModel[] = [];
    loading = false;
    firstTimeLoaded = false;
    firstEventEmitted = false;
    isPopulationFilter = false;
    protected vasecurityidTag = '{vasecurityid}';

    /**
     * Timeouts for filters
     */
    protected pendingRequest: any;
    protected pendingTimeout: any;

    /**
     * Resets all the models to orginal state
     */
    reset(): void {
        this.itemsModels.forEach((itemsModel: BaseFilterItemModel) => {
            itemsModel.reset();
            itemsModel.resetNumberOfSelectedFilters();
        });
    }

    /**
     * Transform filter data local model to server
     *
     * Note:  here bacially if the selectedValues are equal to inital values we dont send anything.
     * For checklist, everything is selected by default, so it checks if everything is selected
     * then send nothing
     */
    transformFiltersToServer(manuallyApplied = false, reseted: boolean = false): any {

        const serverFiltersModel: any = {};

        this.itemsModels.forEach((model: BaseFilterItemModel) => {
            const serverObject = model.toServerObject(manuallyApplied);
            if (!isEmpty(serverObject)) {
                serverFiltersModel[model.key] = serverObject;
            }
        });
        if (reseted) {
            serverFiltersModel.reseted = true;
        }
        return serverFiltersModel;
    }

    /**
     * Transform filter data from server to local model
     *
     * @param {List<Object>} filters - server filter object
     */
    transformFiltersFromServer(filters: any): void {
        this.itemsModels.forEach((itemModel: BaseFilterItemModel) => {
            const key = itemModel.key;
            itemModel.fromServerObject(filters[key]);
        });
    }

    /**
     * Hide filters that are not available for the current study
     *
     * @param {List<Object>} emptyFilters - list of empty filter keys
     */
    hideEmptyFilters(emptyFilters: string[]): void {
        this.itemsModels.forEach((itemModel: BaseFilterItemModel) => {
            itemModel.filterIsVisible = emptyFilters.indexOf(itemModel.key) === -1;
        });
    }

    /**
     * Clear filters that were selected but have not been applied after leaving the page
     *
     */
    clearNotAppliedSelectedValues(): void {
        this.itemsModels.forEach((itemModel: BaseFilterItemModel) => {
            itemModel.clearNotAppliedSelectedValues();
        });
    }

    /**
     * Couldnt do it in angular 2 Observable way.  I think a debounce option will soom appear on clickable option.
     * debounce doesnt work on Observable here
     */
    getFilters(instant?: boolean, manuallyApplied = false, reseted = false, triggeredByPopulation = false): any {
        const delayTime = instant ? 0 : 1000;

        if (this.pendingTimeout) {
            clearTimeout(this.pendingTimeout);
            this.pendingTimeout = null;
        }

        this.pendingTimeout = setTimeout(() => {
            this._getFiltersImpl(manuallyApplied, reseted, triggeredByPopulation);
        }, delayTime);
    }

    getModelByKey(key: string): BaseFilterItemModel {
        return this.itemsModels.find((item: BaseFilterItemModel) => item.key === key);
    }

    getSelectedValues(key: string): Array<any> {
        const model: any = this.getModelByKey(key);

        if (!isUndefined(model.selectedValues)) {
            return model.selectedValues;
        } else {
            throw new ReferenceError('selectedValues no available for ' + this);
        }
    }

    isTimeline(): boolean {
        return window.location.href.indexOf('timeline') !== -1;
    }

    /**
     * This is the abstract method that classes need to implement
     */
    protected abstract _getFiltersImpl(manuallyApplied: boolean, reseted: boolean, triggeredByPopulation: boolean): void;

    /**
     * Emits a event to signal the model has changed
     */
    abstract emitEvent(serverModel: any): void;

    /**
     * name of the filter model, ie aes, labs
     */
    abstract getName(): string;

    /**
     * displayed name of the filter model, ie Adverse Events, Labs
     */
    abstract getDisplayName(): string;

    /**
     * Start of the path to connect to the rest endpoint i.e. timeline/conmeds or aes, labs
     */
    abstract getModulePath(): string;

    /**
     * checks whether filter is visible in the view
     */
    abstract isVisible(): boolean;
}
