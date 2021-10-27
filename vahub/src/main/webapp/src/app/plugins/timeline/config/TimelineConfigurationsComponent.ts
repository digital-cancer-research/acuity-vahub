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

import {Component} from '@angular/core';
@Component({
    selector: 'timeline-configurations',
    templateUrl: 'TimelineConfigurationsComponent.html',
    styleUrls: ['../../../filters/filters.css']
})
export class TimelineConfigurationsComponent {
    openElement: string;

    openContent($event, elementName): void {
        const filterTitleParent = $($event.target).closest('.configuration-item');
        const isOpen = filterTitleParent.hasClass('active');
        $('.configuration-item').removeClass('active');
        this.openElement = elementName === this.openElement ? null : elementName;
        if (isOpen) {
            filterTitleParent.removeClass('active');
        } else {
            filterTitleParent.addClass('active');
        }
    }
}
