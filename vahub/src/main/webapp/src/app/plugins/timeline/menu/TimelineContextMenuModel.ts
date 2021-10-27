import { Injectable } from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {ITimelineContextMenuItem} from './ITimelineContextMenuItem';

@Injectable()
export class TimelineContextMenuModel {
    show = new Subject<{ event: MouseEvent, menuItems: ITimelineContextMenuItem[] }>();
}
