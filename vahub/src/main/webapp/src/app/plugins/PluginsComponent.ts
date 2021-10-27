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

import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';

import {ModalAnswer, IModalAnswer} from '../common/trellising';
import {
    CanDeactivateAEs,
    CanDeactivateConmeds,
    CanDeactivateLabs,
    CanDeactivateCardiac,
    CanDeactivateRenal,
    CanDeactivateLiverFunction,
    CanDeactivateRespiratory,
    CanDeactivateExacerbations,
    CanDeactivateVitals,
    CanDeactivateCIEvents,
    CanDeactivateCerebrovascular,
    CanDeactivateCvot,
    CanDeactivateBase,
    CanDeactivateCtDna,
    CanDeactivateExposure,
    CanDeactivateBiomarkers,
    CanDeactivateDoseProportionality,
    CanDeactivatePkOverallResponse,
    CanDeactivatePriorTherapy,
    CanDeactivateRecist
} from './deactivate';
import {XAxisCoordinateService} from './timeline/chart/axis/XAxisCoordinateService';

@Component({
    templateUrl: 'PluginsComponent.html',
    styleUrls: ['./PluginsComponentStyles.css']
})
export class PluginsComponent implements  OnInit, OnDestroy {

    public modalVisible: boolean;
    public modalTitle: string;
    public modalMessage: string;
    public deactivator: CanDeactivateBase;
    public location = '';

    private deactivateAEsSubscription: Subscription;
    private deactivateConmedsSubscription: Subscription;
    private deactivateLabsSubscription: Subscription;
    private deactivateCardiacSubscription: Subscription;
    private deactivateLiverSubscription: Subscription;
    private deactivateRenalSubscription: Subscription;
    private deactivateRespiratorySubscription: Subscription;
    private deactivateVitalsSubscription: Subscription;
    private deactivateExacerbationsSubscription: Subscription;
    private deactivateCIEventsSubscription: Subscription;
    private deactivateCerebrovascularSubscription: Subscription;
    private deactivateCvotSubscription: Subscription;
    private deactivateCtDnaSubscription: Subscription;
    private deactivateBiomarkersSubscription: Subscription;
    private deactivateExposureSubscription: Subscription;
    private deactivateDoseProportionalitySubscription: Subscription;
    private deactivatePkOverallResponseSubscription: Subscription;
    private deactivatePriorTherapySubscription: Subscription;
    private deactivateRecistSubscription: Subscription;
    private routerSubscription: Subscription;

    constructor(protected  canDeactivateAEs: CanDeactivateAEs,
                protected  canDeactivateConmeds: CanDeactivateConmeds,
                protected  canDeactivateLabs: CanDeactivateLabs,
                protected  canDeactivateCardiac: CanDeactivateCardiac,
                protected  canDeactivateLiverFunction: CanDeactivateLiverFunction,
                protected  canDeactivateRespiratory: CanDeactivateRespiratory,
                protected  canDeactivateExacerbations: CanDeactivateExacerbations,
                protected  canDeactivateVitals: CanDeactivateVitals,
                protected  canDeactivateRenal: CanDeactivateRenal,
                protected  canDeactivateCIEvents: CanDeactivateCIEvents,
                protected  canDeactivateCerebrovascular: CanDeactivateCerebrovascular,
                protected  canDeactivateCvot: CanDeactivateCvot,
                protected  canDeactivateCtDna: CanDeactivateCtDna,
                protected canDeactivatePriorTherapy: CanDeactivatePriorTherapy,
                protected canDeactivateBiomarkers: CanDeactivateBiomarkers,
                protected canDeactivateExposure: CanDeactivateExposure,
                protected canDeactivateDoseProportionality: CanDeactivateDoseProportionality,
                protected canDeactivatePkOverallResponseExposure: CanDeactivatePkOverallResponse,
                protected canDeactivateRecist: CanDeactivateRecist,
                private xAxisCoordinateService: XAxisCoordinateService,
                private router: Router
            ) {
    }

    ngOnInit(): void {
        const that = this;
        this.deactivateAEsSubscription = this.canDeactivateAEs.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Adverse Event filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Adverse Event Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateAEs;
        });
        this.deactivateConmedsSubscription = this.canDeactivateConmeds.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Conmeds filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Conmeds Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateConmeds;
        });
        this.deactivateLabsSubscription = this.canDeactivateLabs.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Lab filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Lab Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateLabs;
        });
        this.deactivateCardiacSubscription = this.canDeactivateCardiac.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Cardiac filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Cardiac Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateCardiac;
        });
        this.deactivateLiverSubscription = this.canDeactivateLiverFunction.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Liver filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Liver Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateLiverFunction;
        });
        this.deactivateRenalSubscription = this.canDeactivateRenal.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Renal filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Renal Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateRenal;
        });
        this.deactivateRespiratorySubscription = this.canDeactivateRespiratory.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Respiratory filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Respiratory Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateRespiratory;
        });
        this.deactivateExacerbationsSubscription = this.canDeactivateExacerbations.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Exacerbations filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Exacerbations Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateExacerbations;
        });
        this.deactivateVitalsSubscription = this.canDeactivateVitals.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Vitals filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Vitals Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateVitals;
        });
        this.deactivateCIEventsSubscription = this.canDeactivateCIEvents.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current CI Events filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'CI Events Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateCIEvents;
        });
        this.deactivateCerebrovascularSubscription = this.canDeactivateCerebrovascular.deactivateWarning.subscribe(() => {
            that.modalMessage
                = 'Do you want your current Cerebrovascular Events filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Cerebrovascular Events Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateCerebrovascular;
        });
        this.deactivateCvotSubscription = this.canDeactivateCvot.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current CVOT Endpoint filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'CVOT Endpoint Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateCvot;
        });
        this.deactivateCtDnaSubscription = this.canDeactivateCtDna.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current ctDNA filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'CtDna Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateCtDna;
        });
        this.deactivateBiomarkersSubscription = this.canDeactivateBiomarkers.deactivateWarning.subscribe(() => {
            that.modalMessage
                = 'Do you want your current Genomic Profile filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Genomic Profile Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateBiomarkers;
        });
        this.deactivateExposureSubscription = this.canDeactivateExposure.deactivateWarning.subscribe(() => {
            that.modalMessage
                = 'Do you want your current Analyte Concentration filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Analyte Concentration Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateExposure;
        });
        this.deactivateDoseProportionalitySubscription = this.canDeactivateDoseProportionality.deactivateWarning.subscribe(() => {
            that.modalMessage
                = 'Do you want your current Dose Proportionality filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Dose Proportionality Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateDoseProportionality;
        });
        this.deactivatePkOverallResponseSubscription = this.canDeactivatePkOverallResponseExposure.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current PK-Response filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'PK-Response Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivatePkOverallResponseExposure;
        });
        this.deactivatePriorTherapySubscription = this.canDeactivatePriorTherapy.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current Previous Lines filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'Previous Lines Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivatePriorTherapy;
        });
        this.deactivateRecistSubscription = this.canDeactivateRecist.deactivateWarning.subscribe(() => {
            that.modalMessage = 'Do you want your current RECIST filter settings to limit the subjects visible in the other views?';
            that.modalTitle = 'RECIST Filter Warning';
            that.modalVisible = true;
            that.deactivator = that.canDeactivateRecist;
        });
        this.location = location.href;
        this.routerSubscription = this.router.events.subscribe((event: any) => {
            this.location = event.url;
        });
    }

    ngOnDestroy(): void {
        this.deactivateAEsSubscription.unsubscribe();
        this.deactivateConmedsSubscription.unsubscribe();
        this.deactivateLabsSubscription.unsubscribe();
        this.deactivateCardiacSubscription.unsubscribe();
        this.deactivateLiverSubscription.unsubscribe();
        this.deactivateRenalSubscription.unsubscribe();
        this.deactivateRespiratorySubscription.unsubscribe();
        this.deactivateVitalsSubscription.unsubscribe();
        this.deactivateExacerbationsSubscription.unsubscribe();
        this.deactivateCIEventsSubscription.unsubscribe();
        this.deactivateCerebrovascularSubscription.unsubscribe();
        this.deactivateCvotSubscription.unsubscribe();
        this.deactivateCtDnaSubscription.unsubscribe();
        this.deactivateBiomarkersSubscription.unsubscribe();
        this.deactivateExposureSubscription.unsubscribe();
        this.deactivateDoseProportionalitySubscription.unsubscribe();
        this.deactivatePkOverallResponseSubscription.unsubscribe();
        this.deactivatePriorTherapySubscription.unsubscribe();
        this.deactivateRecistSubscription.unsubscribe();
        this.routerSubscription.unsubscribe();
        this.xAxisCoordinateService.cursorXCoordinate.next(null);
        this.modalVisible = false;
    }

    closeModal(event: IModalAnswer): void {
        this.modalVisible = false;
        localStorage.setItem('doNotShowAgain', event.doNotShowAgain.toString());
        if (event.answer === ModalAnswer.CANCEL) {
            this.deactivator.canDeactivateObserver.next(false);
        } else {
            if (event.answer === ModalAnswer.YES) {
                this.deactivator.setAsPopulation();
            }
            this.deactivator.updateFiltersState();
            this.deactivator.canDeactivateObserver.next(true);
        }
    }

}
