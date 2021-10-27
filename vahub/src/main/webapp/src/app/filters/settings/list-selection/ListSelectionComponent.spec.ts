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

import {async, TestBed} from '@angular/core/testing';
import {Map} from 'immutable';
import {SentenceCasePipe} from '../../../common/pipes';
import {ErrorBarsType} from '../../../common/trellising/store';

import {ListSelectionComponent} from './ListSelectionComponent';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [ListSelectionComponent, SentenceCasePipe]
        });
    });

    describe('WHEN new option is selected in the list', () => {
        it('THEN event should be emitted',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(ListSelectionComponent);
                    component.componentInstance.elements = Map(Object.keys(ErrorBarsType).map(e => ([e, false])));
                    component.componentInstance.elements['STANDARD_DEVIATION'] = true;
                    spyOn(component.componentInstance.onSelect, 'emit');
                    component.componentInstance.selectElement('STANDARD_DEVIATION');
                    expect(component.componentInstance.onSelect.emit).toHaveBeenCalledWith({
                        name: 'STANDARD_DEVIATION',
                        selected: true
                    });
                });
            }));
    });
});
