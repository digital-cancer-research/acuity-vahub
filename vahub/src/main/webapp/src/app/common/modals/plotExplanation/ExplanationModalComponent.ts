import {Component, OnDestroy, Input, HostListener, ElementRef} from '@angular/core';
import {Explanation} from '../../utils/ExplanationUtils';

@Component({
    selector: 'explanation-modal',
    templateUrl: 'ExplanationModalComponent.html',
    styleUrls: ['./ExplanationModalComponent.css']
})
export class ExplanationModalComponent implements OnDestroy {
    @Input() explanationContent: Explanation;
    @Input() isVisible: boolean;
    constructor (private _elementRef: ElementRef) {
    }

    ngOnDestroy(): void {
        this.close();
    }

    open(): void {
        this.isVisible = true;
    }

    close(): void {
        this.isVisible = false;
    }

    @HostListener('document:click', ['$event']) closeFromOutside(e) {
        const clickOutside = !this._elementRef.nativeElement.contains(e.target);
        if (clickOutside) {
            this.close();
        }
    }
}
