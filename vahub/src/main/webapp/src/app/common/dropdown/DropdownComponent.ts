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

import {Component, Input, Output, OnChanges, OnInit, SimpleChanges, EventEmitter, HostListener} from '@angular/core';
import {DropdownItem} from './DropdownItem';
import {DropdownModel} from './DropdownModel';
import {DropdownService} from './DropdownService';
import * as  _ from 'lodash';

/**
 * Dropdown component class
 * @property {DropdownModel} model                        - dropdown model
 * @property {string} name                                - label before dropdown
 * @property {DropdownItem} currentSelectedItem           - currently selected dropdown element
 * @property {EventEmitter<DropdownItem>} dropdownChanged - event emitter for changing dropdown
 */
@Component({
    selector: 'dropdownselector',
    templateUrl: 'DropdownComponent.html',
    styleUrls: ['./DropdownComponent.css']
})
export class DropdownComponent implements OnChanges, OnInit {
    model: DropdownModel;

    @Input() name: string;
    @Input() currentSelectedItem: DropdownItem;
    @Input() avaliableItems: Array<DropdownItem>;
    @Output() dropdownChanged: EventEmitter<DropdownItem> = new EventEmitter<DropdownItem>();

    /**
     * @constructor
     * @param {DropdownService} dropdownService
     */
    constructor(protected dropdownService: DropdownService) {
    }

    /**
     * Initialises dropdown. Subscribes on changes of model from dropdownService
     */
    ngOnInit(): void {
        this.model = new DropdownModel();
        this.model.index = this.name;
        this.dropdownService.add(this.model);
        this.dropdownService.event.subscribe((models) => {
            models.forEach((model) => {
                if (model.index === this.name) {
                    this.model = model;
                }
            });
        });
    }

    /**
     * Is called on dropdown changes (e.g. new dropdown item was selected).
     * Either sets selected dropdown element to null when there are no options,
     * or sets it to the first avaliable option when no element is selected by user.
     * @param {SimpleChanges} changes - new and previous values
     */
    ngOnChanges(changes: SimpleChanges): void {
        if (_.isEmpty(this.avaliableItems)) {
            this.currentSelectedItem = null;
        } else if (_.isNull(this.currentSelectedItem)) {
            this.currentSelectedItem = this.avaliableItems[0];
        }
    }

    /**
     * Is called on dropdown toggle. Either opens it or closes.
     */
    toggle(): void {
        this.model.isOpen = !this.model.isOpen;
        if (this.model.isOpen) {
            this.dropdownService.open(this.name);
        } else {
            this.dropdownService.closeAll();
        }
    }

    /**
     * Fired on click on drop down component, prevent document:click
     */
    @HostListener('click', ['$event'])
    onClick($event): void {
        $event.stopPropagation();
    }

    /**
     * Fired on document:click not on drop down component, close all drop downs
     */
    @HostListener('document:click', ['$event'])
    close($event): void {
        this.dropdownService.closeAll();
    }

    /**
     * Fired when the user changes the drop down
     *
     * @param newValue The new selected dropdownItem
     */
    itemClicked(newValue: DropdownItem): void {
        this.currentSelectedItem = newValue;
        this.dropdownService.closeAll();
        this.dropdownChanged.emit(newValue);
    }
}
