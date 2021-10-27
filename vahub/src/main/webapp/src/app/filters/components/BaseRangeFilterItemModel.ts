import {BaseFilterItemModel} from './BaseFilterItemModel';

export abstract class BaseRangeFilterItemModel extends BaseFilterItemModel {

    public disabled: boolean;
    public haveMadeChange: boolean;

    reset(): void {
        this.haveMadeChange = false;
        this.disabled = false;
    }

    clear(): void {
        this.haveMadeChange = false;
    }
}
