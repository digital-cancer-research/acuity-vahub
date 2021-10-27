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

import {Component, EventEmitter, OnInit, OnDestroy, Input, Output} from '@angular/core';

/**
 * Component for modal window for saving selected filters
 *
 * @property {string} Title
 * @property {string} ButtonText
 * @property {boolean} ModalBeDismissed
 * @property {any} FiltersList
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<boolean>} modalHasBeenSubmitted
 */
@Component({
    selector: 'app-modal-filter-save',
    templateUrl: 'ModalFilterSaveComponent.html'
})
export class ModalFilterSaveComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() ButtonText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() FiltersList: any;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<boolean> = new EventEmitter<boolean>();

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(false);
    }

    okModal(): void {
        this.modalHasBeenSubmitted.emit(true);
    }
}
