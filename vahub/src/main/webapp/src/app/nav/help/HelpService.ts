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
import {Location} from '@angular/common';
import {cloneDeep} from 'lodash';
import * as Driver from 'driver.js';
import {ConfigurationService} from '../../configuration/ConfigurationService';

@Injectable()
export class HelpService {

    filters: any = {
        population: '<br> Population Filters <br> ',
        events: '<br> Event Filters Specific to information shown <br>',
        timeline: '<br> Track Filters for timeline view <br>'
    };

    introA: Driver;
    introB: Driver;

    introASteps: Array<Driver.Step> = [
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
                description: 'The Population Activity Area shows you which filters you have applied (Widgets) ' +
                'and a summary of how many subjects from the total in the study are shown. You can remove each widget individually, ' +
                'clear them all and save them for using later',
                position: 'bottom',
            }
        },
        {
            element: '#intro4',
            popover: {
                title: ' ',
                description: 'The Event Activity Area shows you which filters you have applied (Widgets) ' +
                'and a summary of how many events from the total in the study are shown. You can remove each widget individually, ' +
                'clear them all and save them for using later',
                position: 'bottom',
            }
        },
        {
            element: '#intro5',
            popover: {
                title: ' ',
                description: 'You can save an image of the plot area as a JPG or PNG. You can also choose to print the plot ' +
                'by selecting the corresponding option from the Chart Context Menu',
                position: 'right',
            }
        },
        {
            element: '#intro6',
            popover: {
                title: ' ',
                description: 'Use the filters bar to select the filters you want to apply, which tracks you want to view' +
                ' and to access the filters you have saved',
                position: 'left'
            }
        },
        {
            element: '#intro7',
            popover: {
                title: ' ',
                description: ' Open/Close the filters dialog <br> <br> Population Filters <br>',
                position: 'left',
            }
        }
    ];
    introBSteps: Array<Driver.Step> = [
        {
            element: '#introB2',
            popover: {
                title: ' ',
                description: 'Use this search function to find the study that you want to work with',
                position: 'bottom',
            }
        },
        {
            element: '#introB1',
            popover: {
                title: ' ',
                description: 'Click on the study name to expand and collapse the study dataset summaries',
                position: 'top',
            }
        },
        {
            element: '#introB3',
            popover: {
                title: ' ',
                description: 'Click on the dataset name to view the data set',
                position: 'bottom',
            }

        },
        {
            element: '#introB5',
            popover: {
                title: ' ',
                description: 'Click on the book icon to access the Best Practice guidance and documentation',
                position: 'left'
            }

        },
        {
            element: '#introB4',
            popover: {
                title: ' ',
                description: 'Click the envelope icon to provide feedback about this application to '
                    + `the ACUITY Team`,
                position: 'left',
            }
        },
    ];

    introInProgress: boolean;

    constructor(private location: Location,
                private configurationService: ConfigurationService) {
        this.introInProgress = false;
        this.introA = new Driver({
            padding: 3,
            onHighlightStarted: (element: any) => {
                if (element.node.classList[0] === 'filter-tabs') {
                    $('#intro6').addClass('filtersOnFocus');
                }
            },
            onReset: (element: any) => {
                $('#intro6').removeClass('filtersOnFocus');
                this.introInProgress = false;
            }
        });
        this.introB = new Driver({
            padding: 3,
            onHighlightStarted: (element: any) => {
                if (element.options.element === '#introB3') {
                    if (document.getElementById('introB1').classList['1'] !== 'expanded') {
                        document.getElementById('introB1').click();
                    }
                }
            },
            onReset: (element: any) => {
                this.introInProgress = false;
            }
        });

    }

    /**
     * Start intro for page without plugins
     */
    public startIntroHomepage(): void {
        if (!this.introInProgress) {
            this.introB.defineSteps(this.introBSteps);
            this.introB.start();
            this.introInProgress = true;
        }
    }

    /**
     * Start intro for page with plugins
     */
    public startIntroAJs(): void {
        if (!this.introInProgress) {
            // Check all conditions and accumulate options
            const options: Array<Driver.Step> = cloneDeep(this.introASteps);

            // If no population
            if (this.location.path().indexOf('population') === -1) {
                options[6].popover.description += this.filters.events;
            } else {
                options.splice(3, 1);
            }

            // If ssv tables
            if (['summary-table', 'ae-tab', 'summary-tab', 'plugins/aes/table',
                    'conmeds-tab', 'death-tab', 'dose-tab', 'sae-tab', 'exacerbations-tab', 'dose-discontinuation-tab']
                    .some(plugin => this.location.path().indexOf(plugin) !== -1)) {
                options.splice(3, 2);
            }

            // If Spotfire, AEs Summaries or Cohort editor
            if (['spotfire', 'aes/summary', 'cohort-editor'].some(plugin => this.location.path().indexOf(plugin) !== -1)) {
                options.splice(2, 7);
            }
            if ((this.location.path().indexOf('mix-and-match') !== -1)) {
                options.splice(2, 7);
                options.push(this.introASteps[4]);
            }
            this.introA.defineSteps(options);
            this.introA.start();
            this.introInProgress = true;
        }
    }
}
