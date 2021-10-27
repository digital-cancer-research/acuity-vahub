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

import {async, TestBed, inject} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';

import {ProgressComponent} from './ProgressComponent';
import {TabId} from '../../trellising/store/ITrellising';
import {ProgressService} from './ProgressService';

describe('GIVEN ProgressComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [ProgressComponent],
            providers: [ProgressService]
        });
    });

    function initFixture(fixture, height: number, loading: boolean, tabId: TabId): any {
        fixture.componentInstance.height = height;
        fixture.componentInstance.limit = 1;
        fixture.componentInstance.loading = loading;
        fixture.componentInstance.tabId = tabId;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the loader is not visible by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(ProgressComponent);
                    rootTC.detectChanges();
                    expect(rootTC.componentInstance.loading).toBeFalsy();
                });
            }));

        describe('WHEN backDropHeight() is called', () => {
            it('THEN the height of background is returned',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ProgressComponent);
                        initFixture(rootTC, 400, true, TabId.AES_COUNTS_BARCHART);
                        rootTC.componentInstance.getBounds();
                        expect(rootTC.componentInstance.backDropHeight).toContain('400px');
                        expect(rootTC.componentInstance.topOfMessage).toContain('100px');
                    });
                }));
        });

        // describe('WHEN loading is changed to TRUE', () => {
        //     it('THEN the messages are generated and generateMessage() is called',
        //         async(inject([ProgressService], (progressService: ProgressService) => {
        //             TestBed.compileComponents().then(() => {
        //                 const rootTC = TestBed.createComponent(ProgressComponent);
        //                 initFixture(rootTC, 400, true, TabId.AES_COUNTS_BARCHART);
        //                 spyOn(progressService, 'generateMessage').and.returnValue('Generated loading message');
        //                 rootTC.detectChanges();
        //                 expect(progressService.generateMessage).toHaveBeenCalled();
        //                 expect(rootTC.componentInstance.messages).toEqual(['Generated loading message']);
        //             });
        //         }))
        //     );
        // });
    });
});
