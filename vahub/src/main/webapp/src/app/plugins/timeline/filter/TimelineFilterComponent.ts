import {Component, Output, EventEmitter, OnDestroy, OnInit} from '@angular/core';
import {TrackName, ITrack} from '../store/ITimeline';
import {TimelineObservables} from '../store/observable/TimelineObservables';
import {Subscription} from 'rxjs/Subscription';

@Component({
    selector: 'timeline-filter',
    templateUrl: 'TimelineFilterComponent.html',
    styleUrls: ['./TimelineFilterComponent.css']
})
export class TimelineFilterComponent implements OnInit, OnDestroy {

    @Output() clearAll = new EventEmitter<Object>();
    @Output() exportFilters = new EventEmitter<Object>();

    trackNames = TrackName;
    visibilities = new Map([]);
    trackSubscription: Subscription;

    constructor(private timelineObservables: TimelineObservables) {
    }

    ngOnInit(): void {
        this.subscribeToVisibleTracks();
    }

    ngOnDestroy(): void {
        if (this.trackSubscription) {
            this.trackSubscription.unsubscribe();
        }
    }

    openContent($event): void {
        const filterItem = $($event.target).closest('.timeline-filter-item');
        const filterListTitle = $($event.target).closest('.timeline-filter-list-title');
        const isOpen = filterItem.hasClass('active');
        $('.timeline-filter-item').siblings().removeClass('active');
        $('.timeline-filter-list-title').siblings().hide();

        if (isOpen) {
            filterItem.removeClass('active');
            filterListTitle.siblings().hide();
            $('.export-clear-buttons').hide();
        } else {
            filterItem.addClass('active');
            filterListTitle.siblings().show();
            $('.export-clear-buttons').show();
            $('.filter-list').animate({scrollTop: 0}, 'fast');
        }
    }

    onClearAll(): void {
        this.clearAll.emit({});
    }

    onExportFilters(event: MouseEvent): void {
        this.exportFilters.emit(event);
    }

    private subscribeToVisibleTracks(): void {
        this.trackSubscription = this.timelineObservables.tracks.subscribe((tracks) => {
            if (tracks) {
                tracks.forEach((track: ITrack) => {
                    this.visibilities[track.name] = track.selected;
                });
            }
        });
    }
}
