import {Subject} from 'rxjs/Subject';
import {TrackName} from '../store/ITimeline';

export interface ITimelineContextMenuItem {
    title: TimelineContextMenuOptions;
    enabled: boolean;
    action?: Subject<any>;
}

export enum TimelineContextMenuOptions {
    EXPAND_MENU_ITEM = <any>'Expand ...',
    EXPAND_ALL_MENU_ITEM = <any>'Expand all ',
    COLLAPSE_MENU_ITEM = <any>'Collapse ...',
    COLLAPSE_ALL_MENU_ITEM = <any>'Collapse all '
}

export interface ITrackExpansion {
    subjectId?: string;
    track: TrackName;
    expansionLevel: number;
    expand: boolean;
}
