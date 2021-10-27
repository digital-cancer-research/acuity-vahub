import {Component, EventEmitter, OnInit, OnDestroy, Input, Output, ChangeDetectionStrategy} from '@angular/core';
import {ModalAnswer} from '../../trellising/store/ITrellising';

/**
 * Component for model window with clearing or saving applied filters
 *
 * @property {string} FilterName
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<ModalAnswer>} modalHasBeenSubmitted
 * @property {string} clearedFilterOption
 */
@Component({
    selector: 'clear-filter-modal',
    templateUrl: 'ClearFiltersModalComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClearFiltersModalComponent implements OnInit, OnDestroy {

    @Input() FilterName: string;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<ModalAnswer> = new EventEmitter<ModalAnswer>();

    clearedFilterOption = 'current';

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(ModalAnswer.CANCEL);
    }

    /**
     * Updates clearedFilterOption to 'current' or 'all'
     * @param {string} option - can be 'current' or 'all'
     */
    updateClearedFilterOption(option: string): void {
        this.clearedFilterOption = option;
    }

    okModal(): void {
        if (this.clearedFilterOption === 'all') {
            this.modalHasBeenSubmitted.emit(ModalAnswer.YES);

        } else {
            this.modalHasBeenSubmitted.emit(ModalAnswer.NO);

        }
    }
}
