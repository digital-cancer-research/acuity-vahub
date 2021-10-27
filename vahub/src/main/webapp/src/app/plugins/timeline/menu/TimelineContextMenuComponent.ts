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
