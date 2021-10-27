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

import {Injectable} from '@angular/core';
import {isUndefined} from 'lodash';
import * as $ from 'jquery';

interface DraggiePrototype {
    position: {
        y: number;
    };
}

@Injectable()
export class DetailsOnDemandHeightService {

    private readonly HEADER_HEIGHT = 39;
    private readonly SUMMARY_AREA_HEIGHT = 51;
    private readonly NAV_BAR_HEIGHT: number;
    private windowHeight: number;
    private isDoDClosed = true;

    constructor() {
        this.windowHeight = $(window).height(); // jquery accounts for a scrollbar, window.innerHeight does not
        this.NAV_BAR_HEIGHT = $('.hub-header').height();
    }

    onDragBar(draggie: DraggiePrototype): void {
        this.setStyleToBeOpen();
        if (this.isBelowPage(draggie)) {
            this.setMinHeight(draggie);
            this.isDoDClosed = true;
        } else if (this.isAboveNavBar(draggie)) {
            this.setMaxHeight(draggie);
            this.isDoDClosed = false;
        } else {
            this.setHeight(draggie);
            this.isDoDClosed = false;
        }
        this.rememberHeight(draggie);
    }

    onExpandCollapseButtonPress(): void {
        if (this.isClosed()) {
            this.open();
        } else {
            this.close();
        }
    }

    adjustTableHeightAfterWindowResize(): void {
        const isClosed = this.isClosed();
        this.updateWindowHeight();
        if (isClosed) {
            this.close();
        } else {
            this.open();
        }
    }

    openToPreviousHeight(): void {
        const previousHeight = (<any> window).detailsOnDemandHeight;
        if (!isUndefined(previousHeight)) {
            $('.draggable').css({top: previousHeight, height: this.windowHeight - previousHeight});
            $('.details-area__toggle').addClass('open');
        }
    }

    isClosed(): boolean {
        return this.isDoDClosed;
    }

    updateWindowHeight(): void {
        this.windowHeight = $(window).height(); // jquery accounts for a scrollbar, window.innerHeight does not
    }

    open(): void {
        this.isDoDClosed = false;
        $('.draggable').animate({top: '50%'});
        $('.draggable').height('50%');
        $('ag-grid-angular').height((0.5 * this.windowHeight) - this.HEADER_HEIGHT - this.SUMMARY_AREA_HEIGHT);
        this.rememberHeight({position: {y: 0.5 * this.windowHeight}});
        this.setStyleToBeOpen();
    }

    close(): void {
        this.isDoDClosed = true;
        const closeHeight = this.windowHeight - this.HEADER_HEIGHT;
        $('.draggable').animate({top: closeHeight});
        this.rememberHeight({position: {y: closeHeight}});
        this.setStyleToBeClosed();
    }

    private isAboveNavBar(draggie: DraggiePrototype): boolean {
        return draggie.position.y < this.NAV_BAR_HEIGHT;
    }

    private isBelowPage(draggie: DraggiePrototype): boolean {
        return draggie.position.y > this.windowHeight - this.HEADER_HEIGHT;
    }

    private setMinHeight(draggie: DraggiePrototype): void {
        draggie.position.y = this.windowHeight - this.HEADER_HEIGHT;
        this.setStyleToBeClosed();
    }

    private setMaxHeight(draggie: DraggiePrototype): void {
        draggie.position.y = this.NAV_BAR_HEIGHT;
    }

    private setHeight(draggie: DraggiePrototype): void {
        $('.draggable').height(this.windowHeight - draggie.position.y);
        $('ag-grid-angular').height(this.windowHeight - draggie.position.y - this.HEADER_HEIGHT - this.SUMMARY_AREA_HEIGHT);
    }

    private setStyleToBeOpen(): void {
        document.getElementsByClassName('details-area__toggle')[0].classList.add('open');
    }

    private setStyleToBeClosed(): void {
        document.getElementsByClassName('details-area__toggle')[0].classList.remove('open');
    }

    private rememberHeight(draggie: DraggiePrototype): void {
        (<any> window).detailsOnDemandHeight = draggie.position.y;
    }
}
