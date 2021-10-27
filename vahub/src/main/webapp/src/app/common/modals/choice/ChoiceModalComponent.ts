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
import {ModalAnswer, IModalAnswer} from '../../trellising/store/ITrellising';

/**
 * Component for modal window which offers to apply currently selected filters after going to another tab
 *
 * @property {string} Title
 * @property {string} Msg
 * @property {string} ButtonAcceptText
 * @property {string} ButtonDeclineText
 * @property {boolean} ModalBeDismissed
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<IModalAnswer>} modalHasBeenSubmitted
 * @property {boolean} doNotShowAgain
 */
@Component({
    selector: 'app-choice-modal',
    templateUrl: 'ChoiceModalComponent.html'
})
export class ChoiceModalComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() Msg: string;
    @Input() ButtonAcceptText: string;
    @Input() ButtonDeclineText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<IModalAnswer> = new EventEmitter<IModalAnswer>();
    doNotShowAgain: boolean;

    ngOnInit(): void {
        this.doNotShowAgain = false;
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.CANCEL, doNotShowAgain: this.doNotShowAgain});
    }

    acceptModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.YES, doNotShowAgain: this.doNotShowAgain});
    }

    declineModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.NO, doNotShowAgain: this.doNotShowAgain});
    }
}
