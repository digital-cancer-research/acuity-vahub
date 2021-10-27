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

@Component({
    selector: 'dataset-names',
    templateUrl: 'DatasetNamesComponent.html',
    styleUrls: ['DatasetNamesComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatasetNamesComponent implements OnChanges {
    @Input() names: any[];
    @Output() toggleNavBar: EventEmitter<void> = new EventEmitter<void>();
    isCollapsed = true;

    constructor() {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.hasOwnProperty('names')) {
            this.isCollapsed = true;
        }
    }

    toggle(): void {
        this.isCollapsed = !this.isCollapsed;
        setTimeout(this.toggleNavBar.emit(), 500);
    }
}
