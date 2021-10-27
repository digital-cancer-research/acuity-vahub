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

import {Injectable} from '@angular/core';
import * as Driver from 'driver.js';


@Injectable()
export class TimelineHelpService {
    introTimeline: Driver;
    introTimelineSteps: Array<Driver.Step> = [
        {
            element: '#intro1',
            popover: {
                title: ' ',
                description: 'The name of the study(s) and dataset that you have selected to analyse are listed here',
                position: 'bottom'
            }

        },
        {
            element: '#intro2',
            popover: {
                title: ' ',
                description: 'Use these icons to choose a view and type of data that you will see on the plots',
                position: 'right',
            }

        },
        {
            element: '#intro3',
            popover: {
                title: ' ',
                description: 'The Population Activity Area shows you which filters you have applied (Widgets) and a summary ' +
                'of how many subjects from the total in the study are shown. You can remove each widget individually, ' +
                'clear them all and save them for using later',
                position: 'bottom'
            }

        },
        {
            element: '#intro4',
            popover: {
                title: ' ',
                description: 'The Event Activity Area shows you which filters you have applied (Widgets) and a summary of ' +
                'how many events from the total in the study are shown. You can remove each widget individually, ' +
                'clear them all and save them for using later',
                position: 'bottom',
            }

        },
        {
            element: '#intro7',
            popover: {
                title: ' ',
                description: ' Open/Close the filters dialog <br> <br> Population Filters <br> <br> ' +
                'Event Filters Specific to information shown <br> <br> Track Filters for timeline view <br> <br>',
                position: 'left'
            }
        },
        {
            element: '#intro6',
            popover: {
                title: ' ',
                description: 'Use the "Turn On/Off Tracks" tab to add and remove tracks in the timeline ' +
                '<br> <br> Different filters become available as tracks are added and removed. ' +
                'Use the filters to select the data you want to view',
                position: 'left',
            }

        },
        {
            element: '#intro5',
            popover: {
                title: ' ',
                description: 'Selected tracks will be shown here. Some tracks can be expanded (multiple times) to show ' +
                'more detail - to do this, right click on a track and select the "Expand" option. To revert, ' +
                'select "Collapse" <br>  <br> Hover over data points or bars in a track to see further information about the data ' +
                '<br> <br> Scroll down the plot to see all tracks on the current page',
                position: 'right'
            }
        },
        {
            element: '#intro8',
            popover: {
                title: ' ',
                description: 'Use this setting to change the scale used on the time axis',
                position: 'right'
            }
        }];
    introInProgress: boolean;

    constructor() {
        this.introInProgress = false;
        this.introTimeline = new Driver({
            padding: 3,
            onHighlightStarted: (element: any) => {
                switch (element.node.classList[0]) {
                    case 'filter-tabs':
                        $('#intro6').addClass('filtersOnFocus');
                        break;
                    default:
                        $('#intro6').removeClass('filtersOnFocus');
                        break;
                }
            },
            onReset: (element: any) => {
                $('#intro6').removeClass('filtersOnFocus');
                this.introInProgress = false;
            }
        });
    }

    public startIntro(): void {
        if (!this.introInProgress) {
            this.introTimeline.defineSteps(this.introTimelineSteps);
            this.introTimeline.start();
            this.introInProgress = true;
        }
    }
}
