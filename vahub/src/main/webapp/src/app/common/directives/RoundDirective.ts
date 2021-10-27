import {Directive, HostListener, Input} from '@angular/core';

@Directive({
    selector: '[roundInput]'
})

export class RoundDirective {
    @Input('roundInput') defaultValue: number;

    @HostListener('keyup', ['$event.target'])
    onKeyUp(event: any): void {
        event.value = !event.validity.badInput ? Math.floor(event.valueAsNumber) : this.defaultValue;
    }
}
