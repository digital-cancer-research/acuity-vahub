import {Component, EventEmitter, OnInit, OnDestroy, Input, Output} from '@angular/core';

/**
 * Component for modal window for saving selected filters
 *
 * @property {string} Title
 * @property {string} ButtonText
 * @property {boolean} ModalBeDismissed
 * @property {any} FiltersList
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<boolean>} modalHasBeenSubmitted
 */
@Component({
    selector: 'app-modal-filter-save',
    templateUrl: 'ModalFilterSaveComponent.html'
})
export class ModalFilterSaveComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() ButtonText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() FiltersList: any;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<boolean> = new EventEmitter<boolean>();

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(false);
    }

    okModal(): void {
        this.modalHasBeenSubmitted.emit(true);
    }
}
