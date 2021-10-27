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

import {HelpService} from './HelpService';
import {TestBed, inject} from '@angular/core/testing';
import {SpyLocation} from '@angular/common/testing';
import {some} from 'lodash';

describe('GIVEN helpService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [],
            providers: [
                HelpService,
                {provide: Location, useClass: SpyLocation}
            ]
        });
    });

    describe('WHEN starts intro for homepage tab', () => {
        it('THEN should assign right options', () => {
            inject([HelpService],
                (s: HelpService) => {
                    spyOn(location, 'path').and.returnValue('summary-table');

                    s.startIntroHomepage();

                    expect(s.introB.getSteps).toBeDefined();
                });
        });
    });

    describe('WHEN starts intro for tab with table', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('summary-table');

                    s.startIntroAJs();

                    expect(s.introA.getSteps()).toBeDefined();
                    expect(s.introA.getSteps()[4].popover.position).toMatch('top');
                });
        });
    });

    describe('WHEN starts intro for spotfire modules', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('plugins/exposure/spotfire');

                    s.startIntroAJs();

                    expect(s.introA.getSteps()).toBeDefined();
                    expect(s.introA.getSteps().length).toBeLessThanOrEqual(2);
                });
        });
    });

    describe('WHEN starts intro for any page other than population or single subject', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('plugins/ae');

                    s.startIntroAJs();

                    expect(s.introA.getSteps).toBeDefined();
                    expect(some(s.introA.getSteps(), s.filters.events)).toBeTruthy();
                });
        });
    });

});
