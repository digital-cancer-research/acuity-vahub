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

import {Component, EventEmitter, OnInit, OnDestroy, Input, Output, ChangeDetectionStrategy} from '@angular/core';
import {ModalAnswer} from '../../trellising/store/ITrellising';

/**
 * Component for model window with clearing or saving applied filters
 *
 * @property {string} FilterName
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<ModalAnswer>} modalHasBeenSubmitted
 * @property {string} clearedFilterOption
 */
@Component({
    selector: 'clear-filter-modal',
    templateUrl: 'ClearFiltersModalComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClearFiltersModalComponent implements OnInit, OnDestroy {

    @Input() FilterName: string;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<ModalAnswer> = new EventEmitter<ModalAnswer>();

    clearedFilterOption = 'current';

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(ModalAnswer.CANCEL);
    }

    /**
     * Updates clearedFilterOption to 'current' or 'all'
     * @param {string} option - can be 'current' or 'all'
     */
    updateClearedFilterOption(option: string): void {
        this.clearedFilterOption = option;
    }

    okModal(): void {
        if (this.clearedFilterOption === 'all') {
            this.modalHasBeenSubmitted.emit(ModalAnswer.YES);

        } else {
            this.modalHasBeenSubmitted.emit(ModalAnswer.NO);

        }
    }
}
