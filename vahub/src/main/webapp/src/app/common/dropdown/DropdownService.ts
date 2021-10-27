import {DropdownModel} from './DropdownModel';
import {EventEmitter, Injectable} from '@angular/core';

/**
 * Service to keep exclusivity of drop downs being open
 * @property {DropdownModel[]} models              - array of dropdown models
 * @property {EventEmitter<DropdownModel[]>} event - event emitter for dropdown models
 */
@Injectable()
export class DropdownService {
    private models: DropdownModel[];
    public event: EventEmitter<DropdownModel[]>;

    /**
     * @constructor initialises class params
     */
    constructor() {
        this.event = new EventEmitter<DropdownModel[]>();
        this.models = [];
    }

    /**
     * Adds dropdown to models
     * @param {DropdownModel} model - dropdown
     */
    add(model: DropdownModel): void {
        if (this.models.map((x) => {
                return x.index;
            }).indexOf(model.index) === -1) {
            this.models.push(model);
        }
    }

    /**
     * Emittes event for dropdown models when dropdown is opened.
     * @param index - name of a dropdown
     */
    open(index: string): void {
        this.models.forEach((model: DropdownModel) => {
            model.isOpen = model.index === index;
        });
        this.event.emit(this.models);
    }

    /**
     * Closes all dropdowns
     */
    closeAll(): void {
        this.models.forEach((model: DropdownModel) => {
            model.isOpen = false;
        });
        this.event.emit(this.models);
    }

}
