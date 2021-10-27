import {FILTER_TYPE} from './dtos';

export abstract class BaseFilterItemModel {

    numberOfSelectedFilters = 0;
    filterIsVisible = true;

    constructor(public type: FILTER_TYPE,
                public key: string,
                public displayName?: string) {
    }

    abstract toServerObject(manuallyApplied?: boolean, isCohortFilter?: boolean): any;

    abstract fromServerObject(fromServerObject: any): void;

    abstract reset(resetAndDestroy?: boolean): any;

    abstract clear(name?: string): void;

    abstract clearNotAppliedSelectedValues(): void;

    abstract updateNumberOfSelectedFilters(): void;

    abstract setSelectedValues(values: any): void;

    showNumberOfSelectedFilters(): boolean {
        return this.numberOfSelectedFilters !== 0;
    }

    resetNumberOfSelectedFilters(): void {
        this.numberOfSelectedFilters = 0;
    }
}
