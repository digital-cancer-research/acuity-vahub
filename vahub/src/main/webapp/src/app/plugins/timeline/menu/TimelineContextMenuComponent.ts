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

import { Component , HostListener, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {TimelineContextMenuModel} from './TimelineContextMenuModel';
import {ITimelineContextMenuItem} from './ITimelineContextMenuItem';

@Component({
    selector: 'timline-context-menu-holder',
    templateUrl: 'TimelineContextMenuComponent.html',
    styleUrls: ['./TimelineContextMenuComponent.css']
})
export class TimelineContextMenuComponent implements OnDestroy {

    menuItems: ITimelineContextMenuItem[] = [];
    showing = false;
    private mouseLocation: { left: number, top: number } = { left: 0, top: 0 };
    private modelSubscription: Subscription;

    constructor(private contextMenuModel: TimelineContextMenuModel) {
        this.modelSubscription = contextMenuModel.show.subscribe(e => this.showMenu(e.event, e.menuItems));
    }

    get locationCss(): any {
        return {
            'position': 'fixed',
            'display': this.showing ? 'block' : 'none',
            'left': this.mouseLocation.left + 'px',
            'top': this.mouseLocation.top + 'px',
            'z-index': 1000
        };
    }

    @HostListener('document:click' , ['$event.target'])
    clickedOutside(event: any): void {
        this.showing = false;
    }

    clickMenuItem(menuItem: ITimelineContextMenuItem): void {
        if (menuItem) {
            this.showing = false;
            setTimeout(() => menuItem.action.next(menuItem), 0);
        }
    }

    showMenu(event: MouseEvent, menuItems): void {
        this.showing = true;
        this.menuItems = menuItems;
        this.mouseLocation = {
            left: event.clientX,
            top: event.clientY
        };
    }

    ngOnDestroy(): void {
        if (this.modelSubscription) {
            this.modelSubscription.unsubscribe();
        }
    }
}
