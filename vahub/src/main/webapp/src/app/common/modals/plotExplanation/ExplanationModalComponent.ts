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

import {Component, OnDestroy, Input, HostListener, ElementRef} from '@angular/core';
import {Explanation} from '../../utils/ExplanationUtils';

@Component({
    selector: 'explanation-modal',
    templateUrl: 'ExplanationModalComponent.html',
    styleUrls: ['./ExplanationModalComponent.css']
})
export class ExplanationModalComponent implements OnDestroy {
    @Input() explanationContent: Explanation;
    @Input() isVisible: boolean;
    constructor (private _elementRef: ElementRef) {
    }

    ngOnDestroy(): void {
        this.close();
    }

    open(): void {
        this.isVisible = true;
    }

    close(): void {
        this.isVisible = false;
    }

    @HostListener('document:click', ['$event']) closeFromOutside(e) {
        const clickOutside = !this._elementRef.nativeElement.contains(e.target);
        if (clickOutside) {
            this.close();
        }
    }
}
