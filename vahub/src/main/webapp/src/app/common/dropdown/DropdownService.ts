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
