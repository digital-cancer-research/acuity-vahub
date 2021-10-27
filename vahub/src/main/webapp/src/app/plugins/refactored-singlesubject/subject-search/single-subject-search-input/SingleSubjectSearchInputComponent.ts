import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {List} from 'immutable';
import {Subject} from 'rxjs/Subject';

@Component({
    selector: 'single-subject-search-input',
    templateUrl: 'SingleSubjectSearchInputComponent.html',
    styleUrls: ['SingleSubjectSearchInputComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SingleSubjectSearchInputComponent {
    @Input() selectedSubjectId: string;
    @Input() availableSubjects: List<string>;
    @Input() subjectSearchString: string;

    @Output() newSearchString: EventEmitter<string> = new EventEmitter<string>();
    @Output() clearSubjectSelection: EventEmitter<any> = new EventEmitter<any>();
    @Output() newSelectedSubject: EventEmitter<string> = new EventEmitter<string>();

    isListOpen = false;

    newSearchStringDebouncer: Subject<any> = new Subject<any>();

    constructor() {
        this.newSearchStringDebouncer
            .debounceTime(300)
            .subscribe((val) => this.newSearchString.emit(val));
    }

    onSearchInputChange($event): void {
        this.newSearchStringDebouncer.next($event);
    }

    setDropdownVisibility(isOpen: boolean): void {
        this.isListOpen = isOpen;
    }

    clearSubject(): void {
        this.clearSubjectSelection.emit();
        this.setDropdownVisibility(true);
    }

    selectSubject(subject: string): void {
        this.newSelectedSubject.emit(subject);
        this.setDropdownVisibility(false);
    }

}
