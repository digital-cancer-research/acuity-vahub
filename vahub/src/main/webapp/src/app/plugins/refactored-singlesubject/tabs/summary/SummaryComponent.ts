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

import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {isEmpty, pickBy, difference, every} from 'lodash';

import {UtcPipe} from '../../../../common/pipes/UtcPipe';
import {StudyService} from '../../../../common/StudyService';
import {SessionEventService} from '../../../../session/module';

@Component({
    templateUrl: 'SummaryComponent.html',
    styleUrls: ['./SummaryComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'summary-component'
})
export class SummaryComponent implements OnChanges {
    @Input() subjectDetail: any;
    @Input() subjectDetailMetadata: any;
    @Input() loading: boolean;
    @Input() selectedSubject: string;

    public studySpecificFilters: any;

    public studyDataKeys: string[];
    public studySpecificFiltersKeys: string[];
    public demographyKeys: string[];

    constructor(private utcPipe: UtcPipe, private sessionEventService: SessionEventService) { }

    ngOnChanges(): void {
        const studyMetadataObject = this.subjectDetailMetadata.get('study');

        if (studyMetadataObject && !isEmpty(studyMetadataObject)) {
            const studyDataObject = this.subjectDetail.get('study');

            this.studySpecificFilters = pickBy(studyDataObject, ((_, key) => key.indexOf('--studySpecificFilters') !== -1));
            const oldStudySpecificFiltersKeys = Object.keys(this.studySpecificFilters);
            this.studySpecificFilters = Object.keys(this.studySpecificFilters)
                .map(key => key.replace('--studySpecificFilters', ''))
                .sort((a, b) => a.localeCompare(b))
                .reduce((obj, key) => ({...obj, [key]: this.studySpecificFilters[`${key}--studySpecificFilters`]}), {});
            this.studySpecificFiltersKeys = Object.keys(this.studySpecificFilters);

            this.studyDataKeys = difference(Object.keys(studyMetadataObject), oldStudySpecificFiltersKeys);
        }

        const demographyMetadataObject = this.subjectDetailMetadata.get('demography');
        if (demographyMetadataObject && !isEmpty(demographyMetadataObject)) {
            this.demographyKeys = Object.keys(demographyMetadataObject);
        }
    }

    getStudyTableValue(key: string): string {
        const subjectStudyDetail = this.subjectDetail.get('study')[key];
        if (subjectStudyDetail) {
            return key.includes('Date') ? this.utcPipe.transform(subjectStudyDetail) : subjectStudyDetail;
        }
        return 'No information';
    }

    getDemographyTableValue(key: string): string {
        const subjectDemographyDetail = this.subjectDetail.get('demography')[key];
        if (subjectDemographyDetail) {
            switch (key) {
                case 'age':
                    return `${subjectDemographyDetail} Years`;
                case 'weight':
                    return `${subjectDemographyDetail} Kg`;
                case 'height':
                    return `${subjectDemographyDetail} CM`;
                default:
                    return subjectDemographyDetail;
            }
        }
        return 'No information';
    }

    getMedicalHistoryValue(): string {
        return this.subjectDetail.get('medicalHistories') ? this.subjectDetail.get('medicalHistories') : 'No information';
    }

    hasMedicalHistoryTabData(): boolean {
        return !!this.subjectDetail.get('medicalHistories');
    }
}
