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

import {Component, EventEmitter, OnInit, OnDestroy, Input} from '@angular/core';

/**
 * Component for modal window with some message about display limitations with result kept in session storage
 *
 * @property {string} Msg - modal content message
 * @property {boolean} ModalIsVisible - flag to make modal visible
 */
@Component({
    selector: 'display-limit-modal-message',
    templateUrl: 'DisplayLimitationMessageComponent.html'
})
export class DisplayLimitationMessageComponent implements OnInit {

    modal: any;

    @Input() msg: string;
    @Input() isVisible: boolean;
    @Input() sessionStorageVarName: string;

    ngOnInit(): void {
        if (sessionStorage.getItem(this.sessionStorageVarName) === 'yes' || !this.isVisible) {
            this.isVisible = false;
        }
        this.modal = {
            title: `Display limitation warning`,
            message: this.msg,
            buttonText: `Ok`,
            isVisible: this.isVisible,
            modalHandler: () => {
                this.modal.isVisible = false;
                sessionStorage.setItem(this.sessionStorageVarName, 'yes');
            }
        };
    }

}
