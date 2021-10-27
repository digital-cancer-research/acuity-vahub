import {AfterViewInit, Directive, ElementRef, HostListener, Renderer} from '@angular/core';

@Directive({
    selector: '[beta-tooltip]'
})

export class BetaTooltipDirective implements AfterViewInit {
    tooltipConfig: any = {
        elementSelector: 'span',
        className: 'beta-tooltip',
        topClassName: 'beta-tooltip--top',
        bottomClassName: 'beta-tooltip--bottom',
        activeClassName: 'beta-tooltip--visible',
        notificationText: 'Please note this is work in progress and more functionality is coming in a future release'
    };

    tooltip: HTMLElement;

    constructor(private element: ElementRef,
                private renderer: Renderer) {

    }

    ngAfterViewInit(): void {
        this.tooltip = this.renderer.createElement(this.element.nativeElement, this.tooltipConfig.elementSelector);

        this.renderer.setElementClass(this.tooltip, this.tooltipConfig.bottomClassName, true);
        this.renderer.setElementClass(this.tooltip, this.tooltipConfig.className, true);
        this.renderer.createText(this.tooltip, this.tooltipConfig.notificationText);
    }

    @HostListener('mouseenter')
    onMouseEnter(): void {
        this.renderer.setElementClass(this.tooltip, this.tooltipConfig.activeClassName, true);
    }

    @HostListener('mouseleave')
    onMouseLeave(): void {
        this.renderer.setElementClass(this.tooltip, this.tooltipConfig.activeClassName, false);
    }

    // private get tooltipPosition(): string {
    //     let elementTop = this.element.nativeElement.getBoundingClientRect().top;
    //     let headerTop = document.querySelector('#main').getBoundingClientRect().top;
    //
    //     return elementTop - headerTop > 50 ? 'top' : 'bottom';
    // }

}
