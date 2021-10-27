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

import {TestBed, async} from '@angular/core/testing';
import {TrellisingJumpComponent} from './TrellisingJumpComponent';
import {TrellisingJumpService} from './TrellisingJumpService';
import {CommonModule, Location} from '@angular/common';
import {MockRouter} from '../../../common/MockClasses';
import {Router} from '@angular/router';
import {SpyLocation} from '@angular/common/testing';
import {FormsModule} from '@angular/forms';
import {TrellisingDispatcher} from '../store/dispatcher/TrellisingDispatcher';

describe('GIVEN TrellisingJumpComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: Router, useClass: MockRouter},
                {provide: Location, useClass: SpyLocation},
                {provide: TrellisingJumpService},
                {provide: TrellisingDispatcher},
            ],
            declarations: [TrellisingJumpComponent],
            imports: [
                CommonModule,
                FormsModule
            ]
        });
    });

    describe('WHEN initially constructed', () => {
        it('THEN should have nothing showing', async(() => {
            TestBed.compileComponents().then(() => {
                const rootTC = TestBed.createComponent(TrellisingJumpComponent);
                rootTC.detectChanges();

                expect(rootTC.componentInstance.currentTabLinks.length).toBe(0);

            });
        }));
    });
});
