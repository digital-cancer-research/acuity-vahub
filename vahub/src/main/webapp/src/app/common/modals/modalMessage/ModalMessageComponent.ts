import {Component, EventEmitter, OnInit, OnDestroy, Input, Output} from '@angular/core';

/**
 * Component for modal window with some message
 *
 * @property {string} Title - modal title
 * @property {string} Msg - modal content message
 * @property {string} ButtonText - accept button text
 * @property {boolean} ModalBeDismissed - if modal can be canceled
 * @property {boolean} ModalIsVisible - flag to make modal visible
 * @property {boolean} ModalWithOverlay - shows if a background overlay should be added
 * @property {EventEmitter<boolean>} modalHasBeenSubmitted - callback when modal is submitted
 */
@Component({
    selector: 'app-modal-message',
    templateUrl: 'ModalMessageComponent.html',
    styleUrls: ['./ModalMessageComponent.css']
})
export class ModalMessageComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() Msg: string;
    @Input() ButtonText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() ModalWithOverlay: boolean;
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
