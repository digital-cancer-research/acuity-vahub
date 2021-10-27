/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
