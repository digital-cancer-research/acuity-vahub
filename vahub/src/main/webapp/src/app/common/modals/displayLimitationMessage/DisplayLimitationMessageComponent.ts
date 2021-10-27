import {Component, EventEmitter, OnInit, OnDestroy, Input} from '@angular/core';

/**
 * Component for modal window with some message about display limitations with result kept in session storage
 *
 * @property {string} Msg - modal content message
 * @property {boolean} ModalIsVisible - flag to make modal visible
 */
@Component({
    selector: 'display-limit-modal-message',
    templateUrl: 'DisplayLimitationMessageComponent.html'
})
export class DisplayLimitationMessageComponent implements OnInit {

    modal: any;

    @Input() msg: string;
    @Input() isVisible: boolean;
    @Input() sessionStorageVarName: string;

    ngOnInit(): void {
        if (sessionStorage.getItem(this.sessionStorageVarName) === 'yes' || !this.isVisible) {
            this.isVisible = false;
        }
        this.modal = {
            title: `Display limitation warning`,
            message: this.msg,
            buttonText: `Ok`,
            isVisible: this.isVisible,
            modalHandler: () => {
                this.modal.isVisible = false;
                sessionStorage.setItem(this.sessionStorageVarName, 'yes');
            }
        };
    }

}
