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
 * Component for modal window with some message
 *
 * @property {string} Title - modal title
 * @property {string} Msg - modal content message
 * @property {string} ButtonText - accept button text
 * @property {boolean} ModalBeDismissed - if modal can be canceled
 * @property {boolean} ModalIsVisible - flag to make modal visible
 * @property {boolean} ModalWithOverlay - shows if a background overlay should be added
 * @property {EventEmitter<boolean>} modalHasBeenSubmitted - callback when modal is submitted
 */
@Component({
    selector: 'app-modal-message',
    templateUrl: 'ModalMessageComponent.html',
    styleUrls: ['./ModalMessageComponent.css']
})
export class ModalMessageComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() Msg: string;
    @Input() ButtonText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() ModalWithOverlay: boolean;
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
