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

import {FormsModule} from '@angular/forms';
import {
    async,
    TestBed
} from '@angular/core/testing';

import {ControlComponent} from './ControlComponent';
import {SentenceCasePipe} from '../../../pipes/SentenceCasePipe';
import {LabelPipe} from '../../../pipes/LabelPipe';
import {Trellising} from '../../store/Trellising';
import {MockTrellising} from '../../../MockClasses';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            providers: [
                {provide: Trellising, useClass: MockTrellising}
            ],
            declarations: [ControlComponent, SentenceCasePipe, LabelPipe]
        });
    });

    describe('WHEN created', () => {
        it('THEN is not open', async(() => {
            const fixture = TestBed.createComponent(ControlComponent);
            fixture.detectChanges();
            const compiled = fixture.debugElement.nativeElement;

            expect(compiled.querySelector('.form-group')).toBe(null);
        }));
    });

    describe('WHEN click on button', () => {
        xit('THEN is open', async(() => {
            const fixture = TestBed.createComponent(ControlComponent);
            fixture.detectChanges();
            const compiled = fixture.debugElement.nativeElement;
            const button = compiled.querySelector('.control-button');
            button.click();
            fixture.whenStable().then(() => {
                fixture.detectChanges();
                expect(compiled.querySelector('.form-group')).not.toBe(null);
            });
        }));
    });
});
