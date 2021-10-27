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

import {Component, OnInit} from '@angular/core';
import * as toastr from 'toastr/build/toastr.min.js';
import {TimeoutService} from '../session/timeout/TimeoutService';
import {SessionEventService} from '../session/event/SessionEventService';
import {hexToRgba} from '../common/CommonChartUtils';
import {ConfigurationService} from '../configuration/module';
import BrandingProperties = Request.BrandingProperties;

@Component({
    selector: 'base-component',
    templateUrl: 'BaseComponent.html',
})
export class BaseComponent implements OnInit {
    RefreshTitle = 'Session timed out!';
    RefreshMsg = 'Sorry, your session has timed out. The page needs to be refreshed and you will need to log in!';
    RefreshButtonText = 'Refresh';
    RefreshModalBeDismissed = false;
    RefreshModalIsVisible = false;
    properties: any;

    constructor(timeoutService: TimeoutService,
                public sessionEventService: SessionEventService,
                configurationService: ConfigurationService) {
        this.configToastr(timeoutService);
        this.applyBrandingToDocument(configurationService.brandingProperties);
    }

    private applyBrandingToDocument(brandingProperties: BrandingProperties) {
        const brandingColors = brandingProperties.brandingColors;
        const transparency = brandingColors.transparency || '0.15';
        if (brandingColors && brandingColors.brandingColor) {
            document.body.style.setProperty('--branding-color', brandingColors.brandingColor);
            document.body.style.setProperty('--header-text-color', brandingColors.headerTextColor);
            document.body.style.setProperty('--bright-branding-color', brandingColors.brightBrandingColor);
            document.body.style.setProperty('--active-panel-color', brandingColors.activePanelColor);
            document.body.style.setProperty('--widget-color', brandingColors.widgetColor);
            document.body.style.setProperty('--widget-bright-color', brandingColors.widgetBrightColor);
            document.body.style.setProperty('--branding-color-transparent', hexToRgba(brandingColors.brandingColor, transparency));
            document.body.style.setProperty('--header-datasets-color', brandingColors.headerDatasetsColor);
        }
    }

    private configToastr(timeoutService: TimeoutService): void {
        toastr.options = {
            'closeButton': true,
            'debug': false,
            'newestOnTop': false,
            'progressBar': false,
            'positionClass': 'toast-top-right',
            'preventDuplicates': false,
            'showDuration': '300',
            'hideDuration': '1000',
            'timeOut': '10000',
            'extendedTimeOut': '1000',
            'showEasing': 'swing',
            'hideEasing': 'linear',
            'showMethod': 'fadeIn',
            'hideMethod': 'fadeOut'
        };

        timeoutService.currentState.subscribe(
            (res) => {
                if (res === 419) {
                    this.RefreshModalIsVisible = true;
                }
            });
    }

    ngOnInit(): void {
        // Reset each time the app is opened, otherwise the users will never be able to undo it, or use the actual functionality
        localStorage.setItem('doNotShowAgain', 'false');
    }

    okRefresh(event: boolean): void {
        if (event === true) {
            this.RefreshModalIsVisible = false;
            window.location.reload();
        } else {
            this.RefreshModalIsVisible = true;
        }
    }
}
