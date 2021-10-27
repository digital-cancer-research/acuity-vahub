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

import {Component, Input} from '@angular/core';
import {forOwn, isEmpty, uniq} from 'lodash';
import {List} from 'immutable';
import {IPlot, ISelectionDetail, TabId} from '../store/ITrellising';
import {DataService} from '../data/DataService';
import CBioData = InMemory.CBioData;
import * as  _ from 'lodash';

@Component({
    selector: 'cBio-jump',
    templateUrl: 'CBioJumpComponent.html'
})
export class CBioJumpComponent {
    @Input() selectionDetail: ISelectionDetail;
    @Input() plots: List<IPlot>;
    @Input() tabId: TabId;
    @Input() loading: boolean;
    @Input() cBioLink: any;

    selectedData: CBioData;

    constructor(private data: DataService) {
    }

    public jump(): void {
        const eventIds = isEmpty(this.selectionDetail) ? [] : this.selectionDetail.eventIds;
        this.data.getCBioData(this.tabId, eventIds)
            .subscribe((cBioData: CBioData) => {
                this.selectedData = cBioData;
                this.submitFormWithParams();
            });
    }

    private submitFormWithParams(): void {
        const form = document.forms['cBioLink'];
        const formParams = this.createFormParams();

        forOwn(formParams, (value, name) => {
            const existingInput = document.getElementsByName(name);
            let input;
            if (existingInput.length > 0) {
                input = existingInput[0];
            } else {
                input = document.createElement('input');
                input.type = 'hidden';
                input.name = name;
                form.appendChild(input);
            }
            input.value = value;

        });
        form.submit();
    }

    public createFormParams(): any {
        // In this method we consider that there can be only one dataset,
        // as we can not check it on more than one and do not know the api for this case.
        // As soon as we have more datasets on cBio, we need to extend this method for more than one dataset.
        const cbioPortalDatasetCodes = this.selectedData.cbioPortalDatasetCodes;
        const formParams: any = {
            Action: 'Submit',
            RPPA_SCORE_THRESHOLD: '2.0', //harcdcoded value, provided by Hyve
            Z_SCORE_THRESHOLD: '2.0', //harcdcoded value, provided by Hyve
            case_set_id: 'all',
            data_priority: '0', //harcdcoded value, provided by Hyve
            gene_list: '',
            case_ids: '',
            geneset_list: ' ',
            tab_index: 'tab_visualize',
            show_samples: false, //false for patient view, true for sample view
            clinicallist: 'NUM_SAMPLES_PER_PATIENT', //shows clinical tracks on oncoprint. Value can be any comma-separated sequence
            // of NUM_SAMPLES_PER_PATIENT (default) and (MUTATION_COUNT and/or FRACTION_GENOME_ALTERED)

            oncoprint_sortby: 'case_id'
            //'case_id' - sort by identifier, 'case_list' - sort by order of appearance in the case list of the study
        };

        const studyCodes: string[] = _.uniq(this.selectedData.data
            .map(d => cbioPortalDatasetCodes[d.studyId] || d.studyId));

        formParams.cancer_study_list = studyCodes.join(',');

        formParams.cancer_study_id = (studyCodes.indexOf('all') > -1 || studyCodes.length > 1)
            ? 'all'
            : studyCodes[0];

        formParams.case_ids = _.uniq(this.selectedData.data
            .map(d => (cbioPortalDatasetCodes[d.studyId] || d.studyId) + ':' + d.sampleId))
            .join('%0D%0A');

        formParams.gene_list = _.uniq(this.selectedData.data.map(d => d.gene)).join('%20');

        Object.keys(this.selectedData.profiles).forEach(key => {
            // there could be more if there are more than one dataset, see comment above
            formParams[key] = this.selectedData.profiles[key][0];
        });

        return formParams;
    }
}
