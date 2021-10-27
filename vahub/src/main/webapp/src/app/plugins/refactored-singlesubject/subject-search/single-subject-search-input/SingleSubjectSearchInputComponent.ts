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

import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {List} from 'immutable';
import {Subject} from 'rxjs/Subject';

@Component({
    selector: 'single-subject-search-input',
    templateUrl: 'SingleSubjectSearchInputComponent.html',
    styleUrls: ['SingleSubjectSearchInputComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SingleSubjectSearchInputComponent {
    @Input() selectedSubjectId: string;
    @Input() availableSubjects: List<string>;
    @Input() subjectSearchString: string;

    @Output() newSearchString: EventEmitter<string> = new EventEmitter<string>();
    @Output() clearSubjectSelection: EventEmitter<any> = new EventEmitter<any>();
    @Output() newSelectedSubject: EventEmitter<string> = new EventEmitter<string>();

    isListOpen = false;

    newSearchStringDebouncer: Subject<any> = new Subject<any>();

    constructor() {
        this.newSearchStringDebouncer
            .debounceTime(300)
            .subscribe((val) => this.newSearchString.emit(val));
    }

    onSearchInputChange($event): void {
        this.newSearchStringDebouncer.next($event);
    }

    setDropdownVisibility(isOpen: boolean): void {
        this.isListOpen = isOpen;
    }

    clearSubject(): void {
        this.clearSubjectSelection.emit();
        this.setDropdownVisibility(true);
    }

    selectSubject(subject: string): void {
        this.newSelectedSubject.emit(subject);
        this.setDropdownVisibility(false);
    }

}
