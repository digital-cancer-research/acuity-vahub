import {Directive, Input, HostListener} from '@angular/core';
import {TimelineContextMenuModel} from './TimelineContextMenuModel';
import {ITimelineContextMenuItem} from './ITimelineContextMenuItem';

@Directive({
    selector: '[contextMenu]'
})
export class TimelineContextMenuDirective {
    @Input() contextMenu: ITimelineContextMenuItem[];

    constructor(private contextMenuModel: TimelineContextMenuModel) {
    }

    @HostListener('contextmenu', ['$event'])
    rightClicked(event: MouseEvent): void {
        this.contextMenuModel.show.next({ event: event, menuItems: this.contextMenu });
        event.preventDefault();
    }
}
