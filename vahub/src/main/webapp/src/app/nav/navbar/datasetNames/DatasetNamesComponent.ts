import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

@Component({
    selector: 'dataset-names',
    templateUrl: 'DatasetNamesComponent.html',
    styleUrls: ['DatasetNamesComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatasetNamesComponent implements OnChanges {
    @Input() names: any[];
    @Output() toggleNavBar: EventEmitter<void> = new EventEmitter<void>();
    isCollapsed = true;

    constructor() {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.hasOwnProperty('names')) {
            this.isCollapsed = true;
        }
    }

    toggle(): void {
        this.isCollapsed = !this.isCollapsed;
        setTimeout(this.toggleNavBar.emit(), 500);
    }
}
