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

import {EventEmitter, Input, OnChanges, Output, SimpleChange} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Subscription} from 'rxjs/Subscription';

import {ITimelineContextMenuItem, TimelineContextMenuOptions} from '../menu/ITimelineContextMenuItem';
import {IHighlightedPlotArea, ITrack, IZoom} from '../store/ITimeline';
import {ITrackPlotDetail} from './ITrackPlotDetail';
import {TrackUtils} from './TrackUtils';
import {TimelineUtils} from '../chart/TimelineUtils';
import {List} from 'immutable';
import {includes, remove} from 'lodash';

export abstract class AbstractTrackComponent implements OnChanges {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() zoom: IZoom;
    @Input() subjectHighlighted: boolean;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);

    trackPlotDetails: ITrackPlotDetail[] = [];
    highlightedTrackPlotDetailIndex: number[] = [];
    contextMenuItems: ITimelineContextMenuItem[] = [];
    maxHeight: number = TimelineUtils.BAR_CHART_HEIGHT;
    barPlotHeight: number = TimelineUtils.BAR_CHART_HEIGHT;
    steppedLinePlotHeight: number = TimelineUtils.STEPPED_LINE_CHART_HEIGHT;
    linePlotHeight: number = TimelineUtils.LINE_CHART_HEIGHT;
    private contextMenuSubscriptions: Subscription[] = [];

    hasTrackData(): boolean {
        return false;
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return false;
    }

    canExpandAll(): boolean {
        return false;
    }

    canCollapseAll(): boolean {
        return false;
    }

    mouseEventOnTrackName(isOver: boolean): void {
        this.highlightedTrackPlotDetailIndex = [];
        if (isOver) {
            this.trackPlotDetails.forEach((trackPlotDetail, index) => {
                this.highlightedTrackPlotDetailIndex.push(index);
            });
        }
    }

    mouseEventOnSubTrackName(trackPlotDetailIndex: number, highlighed: boolean): void {
        if (highlighed) {
            this.highlightedTrackPlotDetailIndex.push(trackPlotDetailIndex);
        } else {
            remove(this.highlightedTrackPlotDetailIndex, index => {
                return index === trackPlotDetailIndex;
            });
        }
    }

    isPlotHighlighted(trackPlotDetailIndex: number): boolean {
        return includes(this.highlightedTrackPlotDetailIndex, trackPlotDetailIndex);
    }

    getGapSizeBetweenSubTrack(subTrackIndex: number): number {
        return subTrackIndex < this.trackPlotDetails.length - 1 ? TrackUtils.GAP_BETWEEN_SUB_TRACKS : 0;
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['track']) {
            this.updateComponent(changes);
        }

        if (changes['subjectHighlighted']) {
            this.mouseEventOnTrackName(this.subjectHighlighted);
        }
    }

    protected updateComponent(changes): void {
        if (!changes.track || (changes.track
            && (!changes.track.previousValue
                || changes.track.previousValue.expansionLevel !== changes.track.currentValue.expansionLevel
                || !changes.track.previousValue.data || !changes.track.currentValue.data
                || changes.track.previousValue.data && changes.track.currentValue.data
                && changes.track.previousValue.data.length !== changes.track.currentValue.data.length))) {

            this.updatePlotData();
        }
        this.updateContextMenu();
        this.updateMaxHeight();
    }

    protected abstract updatePlotData(): void;

    get trackName(): string {
        if (this.track) {
            return this.track.name + '';
        }
    }

    get subTrackNames(): string[] {
        const subTrackNames: string[] = [];

        this.trackPlotDetails.forEach((trackPlotDetail: ITrackPlotDetail) => {
            if (trackPlotDetail.subTrackName) {
                subTrackNames.push(trackPlotDetail.subTrackName);
            }
        });

        return subTrackNames;
    }

    protected updateMaxHeight(): void {
        this.maxHeight = TrackUtils.getHeightOfTrack(this.track);
    }

    protected updateContextMenu(): void {
        this.unsubscribeToContextMenuItems();
        this.updateContextMenuItems();
    }

    protected updateContextMenuItems(): void {
        const canExpand = this.canExpand();
        const canCollapse = this.canCollapse();
        const canExpandAll = this.canExpandAll();
        const canCollapseAll = this.canCollapseAll();

        const items: ITimelineContextMenuItem[] = [];

        const contextMenuItemSubject = this.createContextMenuItemsSubject();

        items.push({
            title: TimelineContextMenuOptions.EXPAND_MENU_ITEM,
            enabled: canExpand,
            action: contextMenuItemSubject
        });

        items.push({
            title: TimelineContextMenuOptions.EXPAND_ALL_MENU_ITEM + (<any>(this.trackName + ' tracks')),
            enabled: canExpandAll,
            action: contextMenuItemSubject
        });

        items.push({
            title: TimelineContextMenuOptions.COLLAPSE_MENU_ITEM,
            enabled: canCollapse,
            action: contextMenuItemSubject
        });

        items.push({
            title: TimelineContextMenuOptions.COLLAPSE_ALL_MENU_ITEM + (<any>(this.trackName + ' tracks')),
            enabled: canCollapseAll,
            action: contextMenuItemSubject
        });

        this.contextMenuItems = items;
    }

    protected unsubscribeToContextMenuItems(): void {
        this.contextMenuSubscriptions.forEach((subscription: Subscription) => {
            subscription.unsubscribe();
        });
        this.contextMenuSubscriptions = [];
    }

    protected createContextMenuItemsSubject(): Subject<ITimelineContextMenuItem> {
        const contextMenuSubject = new Subject<ITimelineContextMenuItem>();
        const subscription = contextMenuSubject.subscribe((menuItem: ITimelineContextMenuItem) => {
            if (menuItem.enabled) {
                switch (menuItem.title) {
                    case TimelineContextMenuOptions.EXPAND_MENU_ITEM:
                        this.expandOrCollapseTrack.emit({
                            subjectId: this.subjectId,
                            track: this.track.name,
                            expansionLevel: this.track.expansionLevel,
                            expand: true
                        });
                        return;
                    case TimelineContextMenuOptions.EXPAND_ALL_MENU_ITEM + (<any>(this.trackName + ' tracks')):
                        this.expandOrCollapseTrack.emit({
                            track: this.track.name,
                            expansionLevel: this.track.expansionLevel,
                            expand: true
                        });
                        return;
                    case TimelineContextMenuOptions.COLLAPSE_MENU_ITEM:
                        this.expandOrCollapseTrack.emit({
                            subjectId: this.subjectId,
                            track: this.track.name,
                            expansionLevel: this.track.expansionLevel,
                            expand: false
                        });
                        return;
                    case TimelineContextMenuOptions.COLLAPSE_ALL_MENU_ITEM + (<any>(this.trackName + ' tracks')):
                        this.expandOrCollapseTrack.emit({
                            track: this.track.name,
                            expansionLevel: this.track.expansionLevel,
                            expand: false
                        });
                        return;
                    default:
                        return;
                }
            }
        });

        this.contextMenuSubscriptions.push(subscription);
        return contextMenuSubject;
    }

    trackBySubTrackName(index, item) {
        return item && item.subTrackName ? item.subTrackName :
            item && item.name ? item.name : undefined;
    }
}
