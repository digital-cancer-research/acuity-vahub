import {
    AfterViewInit,
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    Output,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import * as _ from 'lodash';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {ApplicationState} from '../common/store/models/ApplicationState';
import {fromEvent} from 'rxjs/observable/fromEvent';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';

@Component({
    selector: 'study-selection-controls',
    templateUrl: 'StudyListControlsComponent.html',
    styleUrls: ['./StudyListControlsComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StudySelectionControlsComponent implements OnChanges, OnDestroy, AfterViewInit {
    @ViewChild('query') query;

    @Input() searchString: string;
    @Input() numberOfSubjects: number;
    @Input() submitButtonText: string;

    @Output() handleUpdateQuery: EventEmitter<string> = new EventEmitter<string>();
    @Output() handleSubmit: EventEmitter<void> = new EventEmitter<void>();

    changesStream: Observable<any>;

    constructor(private _store: Store<ApplicationState>) {

    }

    ngOnChanges(changes: SimpleChanges): void {
        if (!_.isEqual(changes['numberOfSubjects'].previousValue, changes['numberOfSubjects'].currentValue)) {
            this.spinCounterTo(changes['numberOfSubjects'].currentValue);
        }
    }

    ngOnDestroy(): void {
    }

    ngAfterViewInit(): void {
        this.changesStream = fromEvent(this.query.nativeElement, 'input')
            .pipe(
                map((e: any) => e.target.value),
                debounceTime(300),
                distinctUntilChanged()
            );
        this.changesStream.subscribe(value => this.handleUpdateQuery.emit(value));    }

    private spinCounterTo(number): void {
        $('.subject-counter .counter-span').animate({
            counter: number
        }, {
            duration: 800,
            easing: 'swing',
            step: function (now): void {
                $(this).text(Math.ceil(now));
            }
        });
    }
}
