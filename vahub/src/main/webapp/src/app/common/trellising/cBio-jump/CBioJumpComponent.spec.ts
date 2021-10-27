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

import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DataService} from '../data/DataService';
import {CBioJumpComponent} from './CBioJumpComponent';
import {MockDataService} from '../../MockClasses';


describe('CBioJumpComponent', () => {

    let component: CBioJumpComponent;
    let fixture: ComponentFixture<CBioJumpComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [CBioJumpComponent],
            providers: [
                {provide: DataService, useClass: MockDataService}
            ]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CBioJumpComponent);
        component = fixture.componentInstance;
    });

    it('Should construct formParams properly for 2 events with the same study ids', () => {
        const selectedData = [
            {
                studyId: 'study_1',
                gene: 'a',
                sampleId: '10'
            },
            {
                studyId: 'study_1',
                gene: 'b',
                sampleId: '11'
            }];
        const cBioData: any = {
            data: selectedData,
            cbioPortalDatasetCodes: {study_1: 'cbio_name'},
            profiles: {genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: ['cbio_name_mutations_1'],
                genetic_profile_ids_PROFILE_MUTATION_EXTENDED_2: ['cbio_name_mutations_2']}
        };


        const expectedFormParams: any = {
            Action: 'Submit',
            RPPA_SCORE_THRESHOLD: '2.0',
            Z_SCORE_THRESHOLD: '2.0',
            case_set_id: 'all',
            data_priority: '0',
            gene_list: 'a%20b',
            geneset_list: ' ',
            case_ids: 'cbio_name:10%0D%0Acbio_name:11',
            tab_index: 'tab_visualize',
            show_samples: false,
            clinicallist: 'NUM_SAMPLES_PER_PATIENT',
            oncoprint_sortby: 'case_id',
            cancer_study_id: 'cbio_name',
            cancer_study_list: 'cbio_name',
            genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: 'cbio_name_mutations_1',
            genetic_profile_ids_PROFILE_MUTATION_EXTENDED_2: 'cbio_name_mutations_2'
        };

        component.selectedData = cBioData;
        expect(component.createFormParams()).toEqual(expectedFormParams);
    });

    it('Form params gene_list and case_ids shouldn\'t include the same elements', () => {
        const selectedData: any[] = [
            {
                studyId: 'study_1',
                gene: 'a',
                sampleId: '10'
            },
            {
                studyId: 'study_1',
                gene: 'a',
                sampleId: '10'
            }];

        const cBioData: any = {
            data: selectedData,
            cbioPortalDatasetCodes: {study_1: 'cbio_name'},
            profiles: {genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: ['cbio_name_mutations_1']}
        };

        const expectedFormParams: any = {
            Action: 'Submit',
            RPPA_SCORE_THRESHOLD: '2.0',
            Z_SCORE_THRESHOLD: '2.0',
            case_set_id: 'all',
            case_ids: 'cbio_name:10',
            data_priority: '0',
            gene_list: 'a',
            geneset_list: ' ',
            tab_index: 'tab_visualize',
            show_samples: false,
            clinicallist: 'NUM_SAMPLES_PER_PATIENT',
            oncoprint_sortby: 'case_id',
            cancer_study_id: 'cbio_name',
            cancer_study_list: 'cbio_name',
            genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: 'cbio_name_mutations_1'
        };

        component.selectedData = cBioData;
        expect(component.createFormParams()).toEqual(expectedFormParams);
    });

    it('Should construct form params properly for 2 events with different study ids', () => {
        const selectedData: any[] = [
            {
                studyId: 'study_1',
                gene: 'a',
                sampleId: '10'
            },
            {
                studyId: 'study_2',
                gene: 'b',
                sampleId: '11'
            }];

        const cBioData: any = {
            data: selectedData,
            cbioPortalDatasetCodes: {study_1: 'study_1', study_2: 'cbio_name'},
            profiles: {genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: ['study_1_mutations_1', 'cbio_name_mutations_1'],
                genetic_profile_ids_PROFILE_MUTATION_EXTENDED_2: ['study_1_mutations_2']}
        };
        const expectedFormParams: any = {
            Action: 'Submit',
            RPPA_SCORE_THRESHOLD: '2.0',
            Z_SCORE_THRESHOLD: '2.0',
            case_set_id: 'all',
            data_priority: '0',
            gene_list: 'a%20b',
            geneset_list: ' ',
            case_ids: 'study_1:10%0D%0Acbio_name:11',
            tab_index: 'tab_visualize',
            show_samples: false,
            clinicallist: 'NUM_SAMPLES_PER_PATIENT',
            oncoprint_sortby: 'case_id',
            cancer_study_id: 'all',
            // for now until we do not know how it is handled
            genetic_profile_ids_PROFILE_MUTATION_EXTENDED_1: 'study_1_mutations_1',
            genetic_profile_ids_PROFILE_MUTATION_EXTENDED_2: 'study_1_mutations_2',
            cancer_study_list: 'study_1,cbio_name'
        };

        component.selectedData = cBioData;
        expect(component.createFormParams()).toEqual(expectedFormParams);
    });
});
