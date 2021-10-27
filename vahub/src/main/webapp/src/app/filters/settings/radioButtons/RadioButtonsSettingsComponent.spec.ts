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
import {FormsModule} from '@angular/forms';


import {RadioButtonsSettingsComponent} from './RadioButtonsSettingsComponent';
import {CapitalizePipe, SentenceCasePipe, SettingsPipe} from '../../../common/pipes';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [RadioButtonsSettingsComponent, SentenceCasePipe, CapitalizePipe, SettingsPipe]
        });
    });

    describe('WHEN new option is selected', () => {
        it('THEN event should be emitted',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(RadioButtonsSettingsComponent);
                    component.componentInstance.currentOption = 'A';
                    component.componentInstance.selectedValue = 'B';
                    spyOn(component.componentInstance.onChange, 'emit');
                    component.componentInstance.changeHandler();
                    expect(component.componentInstance.onChange.emit).toHaveBeenCalledWith({currentOption: 'A', selectedValue: 'B'});
                });
            }));
    });

});
