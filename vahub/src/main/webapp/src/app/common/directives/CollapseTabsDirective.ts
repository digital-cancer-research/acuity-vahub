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

import {Directive, ElementRef} from '@angular/core';
import {PluginsService} from '../../plugins/PluginsService';

@Directive({
    selector: '[collapseTabs]'
})

/**
 * Directive for wrapping tabs that do not fit in one line to the dropdown.
 */
export class CollapseTabsDirective {

    tabsWidthsSum: number[];

    constructor(public element: ElementRef,
                public pluginsService: PluginsService) {
    }

    /**
     * Method that hides tabs from navbar that overflow the width of container and shows these tabs in dropdown
     * @param {boolean} isResize - if change was triggered by window resize or not
     */
    public hideOverflowedTabs(isResize: boolean = false): void {
        const tabs = this.element.nativeElement.querySelectorAll('ul.nav-tabs > li');
        const dropdownWidth = this.element.nativeElement.querySelector('.nav-tabs__dropdown').offsetWidth;
        const dropdownTabs = this.element.nativeElement.querySelectorAll('ul.dropdown-menu > li');
        const dropdown = this.element.nativeElement.querySelector('li.dropdown');
        const containerWidth = this.element.nativeElement.offsetWidth;
        let dropdownNeeded = false;
        let currentWidth = 0;
        const selectedTab = this.pluginsService.selectedTab.getValue();

        if (!isResize) {
            this.tabsWidthsSum = [];
            for (let i = 1; i < tabs.length - 1; i++) {
                this.tabsWidthsSum.push(tabs[i].offsetWidth);
            }
        }

        this.tabsWidthsSum.forEach((tabWidth, index) => {
            currentWidth += tabWidth;
            const currentTabName = tabs[index + 1].querySelector('a').text;
            if (dropdownNeeded || currentWidth + dropdownWidth + 50 >= containerWidth) {
                dropdownNeeded = true;
                dropdownTabs[index].classList.remove('overflowed-tab');
                tabs[index + 1].classList.add('overflowed-tab');
            } else {
                if (tabs[index + 1].classList.contains('overflowed-tab')) {
                    tabs[index + 1].classList.remove('overflowed-tab');
                }
                if (currentTabName === selectedTab) {
                    this.pluginsService.selectedTab.next(null);
                    return;
                }
                dropdownTabs[index].classList.add('overflowed-tab');
            }
        });

        if (!dropdownNeeded) {
            dropdown.classList.add('overflowed-tab');
        } else if (dropdownNeeded && dropdown.classList.contains('overflowed-tab')) {
            dropdown.classList.remove('overflowed-tab');
        }
    }
}
