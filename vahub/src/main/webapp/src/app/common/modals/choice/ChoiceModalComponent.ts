import {Component, EventEmitter, OnInit, OnDestroy, Input, Output} from '@angular/core';
import {ModalAnswer, IModalAnswer} from '../../trellising/store/ITrellising';

/**
 * Component for modal window which offers to apply currently selected filters after going to another tab
 *
 * @property {string} Title
 * @property {string} Msg
 * @property {string} ButtonAcceptText
 * @property {string} ButtonDeclineText
 * @property {boolean} ModalBeDismissed
 * @property {boolean} ModalIsVisible
 * @property {EventEmitter<IModalAnswer>} modalHasBeenSubmitted
 * @property {boolean} doNotShowAgain
 */
@Component({
    selector: 'app-choice-modal',
    templateUrl: 'ChoiceModalComponent.html'
})
export class ChoiceModalComponent implements OnInit, OnDestroy {

    @Input() Title: string;
    @Input() Msg: string;
    @Input() ButtonAcceptText: string;
    @Input() ButtonDeclineText: string;
    @Input() ModalBeDismissed: boolean;
    @Input() ModalIsVisible: boolean;
    @Output() modalHasBeenSubmitted: EventEmitter<IModalAnswer> = new EventEmitter<IModalAnswer>();
    doNotShowAgain: boolean;

    ngOnInit(): void {
        this.doNotShowAgain = false;
    }

    ngOnDestroy(): void {
        this.ModalIsVisible = false;
    }

    cancelModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.CANCEL, doNotShowAgain: this.doNotShowAgain});
    }

    acceptModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.YES, doNotShowAgain: this.doNotShowAgain});
    }

    declineModal(): void {
        this.modalHasBeenSubmitted.emit(<IModalAnswer>{answer: ModalAnswer.NO, doNotShowAgain: this.doNotShowAgain});
    }
}
