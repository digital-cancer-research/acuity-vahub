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
import {isEmpty, keys, union} from 'lodash';
import {Map} from 'immutable';

import {StudyService} from '../common/StudyService';

/**
 * Class holds all the information regarding if the tabs have data in the db
 */
@Injectable()
export class DatasetViews {

    constructor(private studyService: StudyService) {
    }

    private static transformTabNameToAppropriateState(tab: string) {

        switch (tab) {
            case 'respiratory':
            case 'lungFunction':
                return 'lungFunction-java';
            case 'cvot':
                return 'cvotEndpoints';
            case 'tumour-response':
                return 'tumour';
            case 'dose-proportionality':
                return 'pkResult';
            case 'pk-overall-response':
                return 'pkResultWithResponse';
            default:
                return tab;
        }
    }

    hasAesData(): boolean {
        return this.checkHasData('aes');
    }

    hasCerebrovascularData(): boolean {
        return this.checkHasData('cerebrovascular');
    }

    hasCvotData(): boolean {
        return this.checkHasData('cvotEndpoints');
    }

    hasConmedsData(): boolean {
        return this.checkHasData('conmeds');
    }

    hasLabsData(): boolean {
        return this.checkHasData('labs');
    }

    hasVitalsData(): boolean {
        return this.checkHasData('vitals-java');
    }

    hasPatientData(): boolean {
        return this.checkHasData('patientData');
    }

    hasCardiacData(): boolean {
        return this.checkHasData('cardiac');
    }

    hasRenalData(): boolean {
        return this.checkHasData('renal-java');
    }

    hasLiverData(): boolean {
        return this.checkHasData('liver');
    }

    hasRespiratryData(): boolean {
        return this.checkHasData('lungFunction-java');
    }

    hasExacerbationsData(): boolean {
        return this.checkHasData('exacerbation');
    }

    hasExposureData(): boolean {
        return this.checkHasData('exposure');
    }

    hasTumourResponseData(): boolean {
        return this.checkHasData('tumour');
    }

    hasTumourTherapyData(): boolean {
        return this.checkBoolean('tumour-therapy', 'hasPriorTherapy');
    }

    hasTumourLesionData(): boolean {
        return this.checkHasData('tumour-lesion');
    }

    hasAnyTumourData(): boolean {
        return this.hasTumourResponseData() || this.hasTumourTherapyData() || this.hasTumourLesionData();
    }

    hasDoseData(): boolean {
        return this.checkHasData('dose');
    }

    hasDeathData(): boolean {
        return this.checkHasData('death');
    }

    hasDoseDiscontinuationData(): boolean {
        return this.checkHasData('doseDisc');
    }

    hasNicotineData(): boolean {
        return this.checkHasData('nicotine');
    }

    hasLiverDiagnosticInvestigationData(): boolean {
        return this.checkHasData('liverDiag');
    }

    hasLiverRiskFactorsData(): boolean {
        return this.checkHasData('liverRisk');
    }

    hasAlcoholData(): boolean {
        return this.checkHasData('alcohol');
    }

    hasCIEventsData(): boolean {
        return this.checkHasData('cievents');
    }

    hasOncologySpotfireModules(): boolean {
        return this.checkHasSpotfireModules('oncology');
    }

    hasExposureSpotfireModules(): boolean {
        return this.checkHasSpotfireModules('exposure');
    }

    hasPkResultData(): boolean {
        return this.checkHasData('pkResult');
    }

    hasPkResultWithResponseData(): boolean {
        return this.checkHasData('pkResultWithResponse');
    }

    hasRespiratorySpotfireModules(): boolean {
        return this.checkHasSpotfireModules('respiratory');
    }

    hasDetectRespiratorySpotfireModules(): boolean {
        return this.checkHasSpotfireModules('detectrespiratory');
    }

    hasAesummariesSpotfireModules(): boolean {
        return this.checkHasSpotfireModules('aesummaries');
    }

    hasTolerabilitySpotfireModules(): boolean {
        return this.checkHasSpotfireModules('tolerability');
    }

    hasSeriousAesData(): boolean {
        return this.checkHasData('seriousAe');
    }

    hasSafetyAsNoInPopulation(): boolean {
        return this.checkBoolean('population', 'hasSafetyAsNoInPopulation');
    }

    hasMedicalHistoryData(): boolean {
        return this.checkHasData('medicalHistory');
    }

    hasSurgicalHistoryData(): boolean {
        return this.checkHasData('surgicalHistory');
    }

    hasOncoBiomarkers(): boolean {
        return this.checkHasData('biomarker');
    }

    getTimepointType(): string {
        if (!this.studyService.metadataInfo
            || !this.studyService.metadataInfo['pkResult']) {
            return '';
        }
        return (this.studyService.metadataInfo['pkResult'])['timepointType'];
    }

    hasCtDnaData(): boolean {
        return this.checkHasData('ctdna');
    }

    hasQTProlongationData(): boolean {
        return this.checkHasData('qt-prolongation');
    }

    getItemsCount(pluginUrl: string, tab?: string): number {
        switch (pluginUrl) {
            case 'vitals':
            case 'renal':
                return this.extractItemsCount(pluginUrl + '-java');
            case 'population':
            case 'labs':
            case 'aes':
            case 'liver':
            case 'conmeds':
            case 'cardiac':
            case 'cievents':
            case 'cerebrovascular':
            case 'exposure':
            case 'biomarker':
            case 'ctdna':
            case 'tumour-therapy':
                return this.extractItemsCount(pluginUrl);
            case 'cvot':
            case 'tumour-response':
            case 'pk-overall-response':
            case 'dose-proportionality':
                return this.extractItemsCount(DatasetViews.transformTabNameToAppropriateState(pluginUrl));
            case 'respiratory':
                return this.extractItemsCount('lungFunction-java');
            case 'singlesubject':
                switch (tab) {
                    case 'lungfunction-tab':
                        return this.extractItemsCount('lungFunction-java');
                    default:
                        return 0;
                }
            case 'machine-insights':
                return this.extractItemsCount('qt-prolongation');
            case 'exacerbations':
                return this.extractItemsCount('exacerbation');
            default:
                return 0;
        }
    }

    getEmptyFilters(pluginUrl: string): string[] {
        const result = this.extractEmptyFilters(pluginUrl, 'inMemoryEmptyFilters');
        if (result) {
            return result;
        }
        throw new Error('Unknown empty filters key: ' + pluginUrl);
    }

    getDetailsOnDemandColumns(tab: string): any {
        if (!isEmpty(this.studyService.metadataInfo)) {
            const tabName = DatasetViews.transformTabNameToAppropriateState(tab);
            switch (tabName) {
                case 'lungFunction-java':
                case 'sae':
                case 'conmeds':
                case 'liver':
                case 'death':
                case 'dose':
                case 'doseDisc':
                case 'cardiac':
                case 'nicotine':
                case 'liverDiag':
                case 'liverRisk':
                case 'alcohol':
                    if (!isEmpty(this.studyService.metadataInfo[tabName])) {
                        return this.studyService.metadataInfo[tabName]['detailsOnDemandColumns'];
                    }
                    return [];
                // TODO refactor it please. "exacerbation" should be replaced with "exacerbations" everywhere.
                case 'exacerbations':
                    if (!isEmpty(this.studyService.metadataInfo['exacerbation'])) {
                        return this.studyService.metadataInfo['exacerbation']['detailsOnDemandColumns'];
                    }
                    return [];
                case 'ae-chord':
                case 'qt-prolongation':
                case 'aes':
                case 'cievents':
                case 'cerebrovascular':
                case 'cvotEndpoints':
                case 'labs':
                case 'tumour':
                case 'tumour-lesion':
                case 'exposure':
                case 'pkResult':
                    return keys(this.studyService.metadataInfo[tabName]['detailsOnDemandTitledColumns']);
                case 'population':
                    return this.studyService.metadataInfo[tabName]['detailsOnDemandTitledColumns'];
                case 'pkResultWithResponse':
                    return Map({
                        pkResultWithResponse: keys(this.studyService.metadataInfo[tabName]['detailsOnDemandTitledColumns']),
                        'recist-pk': keys(this.studyService.metadataInfo['recist-pk']['detailsOnDemandTitledColumns']),
                    });
                case 'renal':
                    return keys(this.studyService.metadataInfo['renal-java']['detailsOnDemandTitledColumns']);
                case 'biomarker':
                    return this.getDetailsOnDemandColumnsTitles(tabName);
                case 'ctdna':
                    return Map({
                        ctdna: keys(this.studyService.metadataInfo[tabName]['detailsOnDemandTitledColumns']),
                        biomarker: this.getDetailsOnDemandColumnsTitles('biomarker')
                    });
                case 'vitals':
                    return keys(this.studyService.metadataInfo['vitals-java']['detailsOnDemandTitledColumns']);
                default:
                    return [];
            }
        }
        return [];
    }

    getDetailsOnDemandColumnsTitles(tab: string): any {
        const tabName = DatasetViews.transformTabNameToAppropriateState(tab);
        return this.studyService.metadataInfo[tabName]
            ? this.studyService.metadataInfo[tabName]['detailsOnDemandTitledColumns']
            : [];
    }

    getJumpFilterValues(originTab: string, filterItemKey: string): string[] {
        if (!isEmpty(this.studyService.metadataInfo) && !isEmpty(this.studyService.metadataInfo[originTab])) {
            return this.studyService.metadataInfo[originTab][filterItemKey];
        } else {
            return [];
        }
    }

    getSubjectsEcodesByIds(subjectEcodes: string[]): string[] {
        return subjectEcodes.map((ecode) => {
            return this.getSubjectEcodeById(ecode);
        });
    }

    getSubjectEcodeById(subjectId: string): string {
        if (!isEmpty(this.studyService.metadataInfo) && !isEmpty(this.studyService.metadataInfo['population'])
            && !isEmpty(this.studyService.metadataInfo['population'].patientList)) {
            return this.studyService.metadataInfo['population'].patientList.find((subject) => {
                return subject.patientId === subjectId;
            }).subjectCode;
        } else {
            return subjectId;
        }
    }

    getSubjectIdByEcode(subjectCode: string): string {
        if (!isEmpty(this.studyService.metadataInfo) && !isEmpty(this.studyService.metadataInfo['population'])
            && !isEmpty(this.studyService.metadataInfo['population'].patientList)) {
            return this.studyService.metadataInfo['population'].patientList.find((subject) => {
                return subject.subjectCode === subjectCode;
            }).patientId;
        } else {
            return subjectCode;
        }
    }

    checkHasData(tab: string): boolean {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return false;
        }
        return <boolean> this.studyService.metadataInfo[tab].hasData;
    }

    getFilterIfDisabled(tab: string): any {
        switch (tab) {
            case 'ctDNA-plot':
                return !this.studyService.metadataInfo['ctdna']['hasTrackedMutations']
                    ? this.studyService.metadataInfo['ctdna']['trackedMutationsString'] : null;
            default:
                return null;
        }
    }

    checkCBioLinkEnabled(): boolean {
        return this.checkBoolean('biomarker', 'enableCBioLink');
    }

    private checkBoolean(tab: string, attribute: string): boolean {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return false;
        }
        return <boolean>(this.studyService.metadataInfo[tab])[attribute];
    }

    private checkHasSpotfireModules(tab: string): boolean {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return false;
        }
        const spotfireModules: any[] = this.studyService.metadataInfo[tab].spotfireModules;
        return (spotfireModules && spotfireModules.length > 0);
    }

    private extractItemsCount(tab: string): number {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return 0;
        }
        return this.studyService.metadataInfo[tab].count;
    }

    private extractEmptyFilters(tab: string, emptyFiltersSection: string): string[] {
        if (!this.studyService.metadataInfo ||
            !this.studyService.metadataInfo[emptyFiltersSection] ||
            !this.studyService.metadataInfo[emptyFiltersSection][tab]) {
            return [];
        }
        return this.studyService.metadataInfo[emptyFiltersSection][tab];
    }
}
