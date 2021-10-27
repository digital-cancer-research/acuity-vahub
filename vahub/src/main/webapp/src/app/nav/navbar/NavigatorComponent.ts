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

import {Component, ViewChild, ElementRef, OnInit, OnDestroy, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Subject} from 'rxjs/Subject';
import {Subscription} from 'rxjs/Subscription';
import {isEmpty, isNil} from 'lodash';

import {SessionEventService} from '../../session/module';
import {StudyService} from '../../common/module';
import {HelpService} from '../help/HelpService';
import {TimelineHelpService} from '../help/TimelineHelpService';
import {AdminService} from '../../common/AdminService';
import {ConfigurationService} from '../../configuration/ConfigurationService';
import {SessionHttpService} from '../../session/http/SessionHttpService';

interface SecurityDropDownItem {
    name: string;
    target: string;
    url: string;
}

const COLLAPSED_HEIGHT = 51;

@Component({
    selector: 'navigator',
    templateUrl: 'NavigatorComponent.html',
    styleUrls: ['./NavigatorComponent.css']
})
export class NavigatorComponent implements OnInit, OnDestroy {

    @ViewChild('navBarStudiesContainer') navBarStudiesContainer: ElementRef;
    navBarStudiesCollapsed: boolean;
    navBarStudiesExpandCollapseEnabled: boolean;
    studySelectionSubscription: Subscription;
    whoAmISubscription: Subscription;

    public loading = new Subject<boolean>();
    private hasStudySelected = false;
    public hideOnScreenHelp: boolean;
    public hideOnHelpManual: boolean;
    public hideHelpDropdown = true;
    public currentDatasets: string[];
    public showAllButtonVisible: boolean;
    public headerExpanded: boolean;
    public headerHeight: number;
    public disableShowAllButton: boolean;
    userName: string;
    // modal dialog
    public modalDialogTitle: string;
    public modalDialogText: string;
    public modalDialogCanCancel: boolean;
    public modalDialogSubmitText = 'OK';
    public modalDialogVisible = false;

    private subKey = 'NavigatorComponent';

    public support: { [index: string]: string };
    public pathToImages: string;

    constructor(public sessionEventService: SessionEventService,
                private studyService: StudyService,
                private router: Router,
                private helpService: HelpService,
                private timelineHelpService: TimelineHelpService,
                private adminService: AdminService,
                public configurationService: ConfigurationService,
                private sessionHttpService: SessionHttpService) {
        this.headerHeight = COLLAPSED_HEIGHT;
        this.headerExpanded = false;
        this.disableShowAllButton = true;
        this.support = configurationService.brandingProperties.support;
        this.pathToImages = configurationService.pathToStatic;
    }

    ngOnDestroy(): void {
        if (this.studySelectionSubscription != null) {
            this.studySelectionSubscription.unsubscribe();
        }
        if (this.whoAmISubscription != null) {
            this.whoAmISubscription.unsubscribe();
        }
    }

    ngOnInit(): void {
        this.subscribeToPageChangeEvents();

        // check if there is an already selected study, if so enable to nav bar
        if (this.sessionEventService.currentSelectedDatasets !== null) {
            this.hasStudySelected = true;
        }
        //don't hide selected study if the user refreshes
        // that.checkStudyIsStillSelected();
        // listen for study selection events changes
        this.studySelectionSubscription = this.sessionEventService.currentDatasets
            .subscribe((datasetsChanged: any) => {
                const datasets = datasetsChanged.toJS();
                this.hasStudySelected = !isEmpty(datasets);
                this.currentDatasets = this.studyService.createDisplayNames(datasets);
                this.navBarStudiesExpandCollapseEnabled = this.currentDatasets.length > 1;
                this.showAllButtonVisible = this.getHeaderHeight() > 55;
                this.maybeUpdateWidgetState();
            });
        this.whoAmISubscription = this.sessionHttpService.getUserDetailsObservable().subscribe(userDetails => {
            this.userName = userDetails.fullName;
        });
        this.sessionEventService.addSubscription(this.subKey + 'studySelection', this.studySelectionSubscription);
    }

    /**
     * Checks if the current user has development team permission
     * TODO: PROPER CHECK USING VAPermissionEvalutator?
     */
    hasDevelopmentTeamPermission(): boolean {
        if (this.sessionEventService.userDetails && this.sessionEventService.userDetails.authoritiesAsString) {
            return this.sessionEventService.userDetails.authoritiesAsString.some((authority) => {
                return authority === 'DEVELOPMENT_TEAM';
            });
        }
        return false;
    }

    /**
     * Checks if reference for a particular multisponsor to best practices exists
     */
    existsReferenceToBestPractice(): boolean {
        return this.support && !isNil(this.support.bestPractice) && this.support.bestPractice.length > 0;
    }

    hideStudyName(): boolean {
        return isEmpty(this.currentDatasets) || window.location.hash.indexOf('plugins') === -1;
    }

    isOnStudySelectionPage(): boolean {
        return window.location.hash === '#/' || window.location.hash === '#' || window.location.hash === '';
    }

    isOnPluginsPage(): boolean {
        return window.location.hash.indexOf('plugins') !== -1;
    }

    isActive(location: string): boolean {
        return window.location.hash.indexOf(location) !== -1;
    }

    public startIntroJs(e: any): void {
        e.stopPropagation();
        if (window.location.hash.indexOf('timeline') !== -1) {
            this.timelineHelpService.startIntro();
        } else if (this.isOnPluginsPage()) {
            this.helpService.startIntroAJs();
        } else {
            this.helpService.startIntroHomepage();
        }
        this.hideHelpDropdown = true;
    }

    public navigate(url: string): void {
        this.router.navigateByUrl(url);
    }

    public toggleNavBarStudies(): void {
        if (this.navBarStudiesExpandCollapseEnabled) {
            if (this.navBarStudiesCollapsed) {
                this.headerHeight = this.getHeight(this.navBarStudiesContainer, COLLAPSED_HEIGHT);
            } else {
                this.headerHeight = COLLAPSED_HEIGHT;
            }
            this.navBarStudiesCollapsed = !this.navBarStudiesCollapsed;
        }
        this.maybeUpdateWidgetState();
    }

    public clearCache(): void {
        const that = this;
        const title = 'Clear All Caches';
        that.showModal(<any>{
            title: title,
            text: `
            <p>Please confirm that you want to clear all caches.</p>
            <p>Note that this may take quite a long time.</p>`,
            cancel: true,
            callback: (event) => {
                if (event === true) {
                    that.adminService.clearCacheAll()
                        .subscribe((res: any) => {
                            that.showModal(<any>{
                                title: title,
                                text: 'Cleared ' + res.clearedCacheNames.length + ' caches'
                            });
                        });
                }
            }
        });
    }

    public modalDialogSubmitted(event: boolean): void {
        this.modalDialogAction(event);
    }

    public toggleHelpDropdown(): void {
        this.hideHelpDropdown = !this.hideHelpDropdown;
    }

    // called when the modal dialog closes
    private modalDialogAction = (event: boolean) => {
    }

    private showModal({
                          title = 'Alert', text = 'Message', button = 'OK', cancel = false, callback = (e) => {
        }
                      }): void {
        this.modalDialogTitle = title;
        this.modalDialogText = text;
        this.modalDialogCanCancel = cancel;
        this.modalDialogSubmitText = button;
        this.modalDialogAction = (event) => {
            this.modalDialogVisible = false;
            callback(event);
        };
        this.modalDialogVisible = true;
    }

    private subscribeToPageChangeEvents(): void {
        this.router.events.subscribe(() => {
            const hash = window.location.hash;
            if (hash.indexOf('plugins') === -1) {
                this.headerExpanded = false;
                this.headerHeight = COLLAPSED_HEIGHT;
            }
            const withoutScreenHelp = ['about', 'support', 'home', 'manual'];
            this.hideOnScreenHelp = withoutScreenHelp.some(page => hash.includes(page));
            this.hideOnHelpManual = hash.includes('manual');
            setTimeout(() => {
                this.disableShowAllButton = !this.hideStudyName() && this.getHeaderHeight() <= COLLAPSED_HEIGHT;
            });
        });
    }

    private getHeaderHeight(): number {
        return document.getElementById('intro1').getElementsByClassName('navbar-studies')[0].clientHeight;
    }

    private maybeUpdateWidgetState(): void {
        setTimeout(() => {
            let navBarStudiesCollapsed = this.navBarStudiesCollapsed;
            let headerHeight = this.headerHeight;

            if (this.navBarStudiesContainer) {
                const actualHeight = this.getHeight(this.navBarStudiesContainer, COLLAPSED_HEIGHT);
                if (COLLAPSED_HEIGHT >= actualHeight) {
                    navBarStudiesCollapsed = true;
                    headerHeight = COLLAPSED_HEIGHT;
                } else {
                    navBarStudiesCollapsed = this.headerHeight <= COLLAPSED_HEIGHT;
                    if (!navBarStudiesCollapsed) {
                        headerHeight = actualHeight;
                    }
                }
            }

            if (navBarStudiesCollapsed !== this.navBarStudiesCollapsed || headerHeight !== this.headerHeight) {
                this.navBarStudiesCollapsed = navBarStudiesCollapsed;
                this.headerHeight = headerHeight;
            }
        });
    }

    private getHeight(elementRef: ElementRef, fallback: number): number {
        try {
            return elementRef.nativeElement.clientHeight;
        } catch (e) {
            return fallback;
        }
    }
}
