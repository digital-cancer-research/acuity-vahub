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

import {DetailsOnDemandHeightService} from './DetailsOnDemandHeightService';

describe('GIVEN DetailsOnDemandHeightService', () => {

    let service: DetailsOnDemandHeightService;

    beforeEach(() => {
        service = new DetailsOnDemandHeightService();
        (<any> window).detailsOnDemandHeight = undefined;
        spyOn(service, 'setStyleToBeOpen');
        spyOn(service, 'setStyleToBeClosed');
        spyOn(service, 'setMinHeight');
        spyOn(service, 'setMaxHeight');
        spyOn(service, 'setHeight');
        spyOn(service, 'open');
        spyOn(service, 'close');
    });

    describe('WHEN the Details on Demand table is dragged below the page', () => {
        it('THEN it is snapped back onto the page', () => {
            const draggie = {
                position: {
                    y: 1000
                }
            };

            service.onDragBar(draggie);

            expect((<any> service).setMinHeight).toHaveBeenCalled();
        });
    });

    describe('WHEN the Details on Demand table is dragged above the page', () => {
        it('THEN it is snapped back onto the page', () => {
            const draggie = {
                position: {
                    y: -1000
                }
            };

            service.onDragBar(draggie);

            expect((<any> service).setMaxHeight).toHaveBeenCalled();
        });
    });

    describe('WHEN the Details on Demand table is dragged on the page', () => {
        beforeEach(() => {
            const draggie = {
                position: {
                    y: 100
                }
            };

            service.onDragBar(draggie);
        });
        it('THEN its position is updated', () => {
            expect((<any> service).setHeight).toHaveBeenCalled();
        });
    });

    describe('WHEN the Details on Demand table is closed', () => {
        describe('AND the open button is pressed', () => {
            it('THEN it is opened', () => {
                spyOn(service, 'isClosed').and.returnValue(true);

                service.onExpandCollapseButtonPress();

                expect((<any> service).open).toHaveBeenCalled();
            });
        });
    });

    describe('WHEN the Details on Demand table is open', () => {
        describe('AND the close button is pressed', () => {
            it('THEN it is closed', () => {
                spyOn(service, 'isClosed').and.returnValue(false);

                service.onExpandCollapseButtonPress();

                expect((<any> service).close).toHaveBeenCalled();
            });
        });
    });
});
