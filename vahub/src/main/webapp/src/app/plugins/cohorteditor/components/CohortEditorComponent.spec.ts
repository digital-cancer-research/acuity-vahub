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

import {Observable} from 'rxjs/Observable';
import * as  _ from 'lodash';

import {CohortEditorComponent} from './CohortEditorComponent';
import {AvailableCohortsComponent} from './AvailableCohortsComponent';
import {FilterListComponent} from './FilterListComponent';
import {FilterReloadService} from '../services/FilterReloadService';
import {MockDatasetViews, MockFilterEventService, MockFilterHttpService} from '../../../common/MockClasses';
import {
    AesFiltersModel,
    CheckListFilterItemModel,
    CohortFiltersModel,
    ListFilterItemModel,
    PopulationFiltersModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel,
    StudySpecificFilter,
    StudySpecificFilterModel
} from '../../../filters/module';
import {SaveCohortDto} from '../dto/SaveCohortDto';
import SavedFilterVO = Request.SavedFilterVO;

let component: CohortEditorComponent;
let availableCohortsComponent: AvailableCohortsComponent;
let filterListComponent: FilterListComponent;
const mockFilterEventService = new MockFilterEventService();
const filterReloadService = new FilterReloadService(<any> mockFilterEventService);
const mockPopulationFiltersModel = new PopulationFiltersModel(
    <any> new MockFilterHttpService(), <any> mockFilterEventService, <any> {}, <any> {});
const mockAesFiltersModel = new AesFiltersModel(mockPopulationFiltersModel, <any> new MockFilterHttpService(),
    <any> mockFilterEventService, <any> {});
const mockCohortFiltersModel = new CohortFiltersModel(<any> {}, <any> {});
const mockService = {
    getCohorts(): Observable<{}> {
        return Observable.of([
            {savedFilter: {name: 'a saved cohort'}},
            {savedFilter: {name: 'an other saved cohort'}}
        ]);
    },
    updateNumberOfSubjectsInSelectedFilters(): void {
    },
    saveCohort(): Observable<{}> {
        return Observable.of(Observable.of([1, 2, 3]));
    },
    deleteCohort(): Observable<{}> {
        return Observable.of(Observable.of([1, 2, 3]));
    },
    applyCohort(id: number, name: string): Observable<{}> {
        return Observable.of({});
    },
    renameCohortIfApplied(): void {
    }
};

describe('GIVEN CohortEditorComponent', () => {


    const mockSavedList = [{
        savedFilter: {
            name: 'a saved cohort',
            id: 99
        },
        sharedWith: ['sam']
    }];

    beforeEach(() => {
        component = new CohortEditorComponent(
            <any> mockService, <any> mockPopulationFiltersModel, mockCohortFiltersModel, mockAesFiltersModel,
            filterReloadService, <any> {});
        availableCohortsComponent = new AvailableCohortsComponent(<any> mockService, mockCohortFiltersModel);
        filterListComponent = new FilterListComponent(<any> new MockDatasetViews());
        component.availableCohortsComponent = availableCohortsComponent;
        component.filterListComponent = filterListComponent;
        spyOn(mockCohortFiltersModel, 'reset');
        spyOn(mockCohortFiltersModel, 'clearNotAppliedSelectedValues');

    });

    describe('WHEN initialised', () => {
        it('THEN requests a list of available cohorts', () => {
            availableCohortsComponent.ngOnInit();

            expect(availableCohortsComponent.availableCohorts).toEqual([
                {
                    savedFilter: {
                        name: 'a saved cohort'
                    }
                },
                {
                    savedFilter: {
                        name: 'an other saved cohort'
                    }
                }]);
        });
    });

    describe('WHEN destroyed', () => {
        it('THEN unsaved changes are removed', () => {
            availableCohortsComponent.availableCohorts = <any> [
                {
                    savedFilter: {
                        name: 'an unsaved cohort',
                        id: availableCohortsComponent.ID_FOR_NEW_COHORTS
                    }
                },
                {
                    savedFilter: {
                        name: 'a saved cohort',
                        id: 99
                    }
                }];

            component.ngOnDestroy();

            expect(availableCohortsComponent.availableCohorts).toEqual([
                {
                    savedFilter: {
                        name: 'a saved cohort',
                        id: 99
                    }
                }]);
        });
    });

    describe('WHEN saving a cohort', () => {

        beforeEach(() => {
            availableCohortsComponent.availableCohorts = [];
            availableCohortsComponent.newCohortName = ' a new cohort';
            spyOn(mockService, 'saveCohort').and.returnValue(Observable.of(mockSavedList));
            spyOn(mockCohortFiltersModel, 'setItems');
            spyOn(mockService, 'renameCohortIfApplied');
            spyOn(availableCohortsComponent, 'getSelectedCohort').and.returnValue(mockSavedList[0]);
            component.saveCohort();
        });

        it('THEN sends the cohort to the server', () => {
            expect(mockService.saveCohort).toHaveBeenCalled();
        });

        it('THEN adds the updated cohort list to the component', () => {
            expect(availableCohortsComponent.availableCohorts).toEqual(mockSavedList);
        });

        it('THEN adds the new cohort to the cohort filter component', () => {
            expect(mockCohortFiltersModel.setItems).toHaveBeenCalledWith(mockSavedList);
        });

        it('THEN updates who the cohort is shared with', () => {
            expect(component.sharedWith).toEqual(mockSavedList[0].sharedWith);
        });
    });

    describe('WHEN saving and applying a cohort', () => {

        let mockSavedList2;

        beforeEach(() => {
            availableCohortsComponent.availableCohorts = [];
            availableCohortsComponent.newCohortName = 'a new cohort';
            mockSavedList2 = [{
                savedFilter: {
                    name: 'a new cohort',
                    id: 99
                },
                sharedWith: ['sam']
            }];
            spyOn(mockService, 'saveCohort').and.returnValue(Observable.of(mockSavedList2));
            spyOn(mockService, 'applyCohort').and.returnValue(Observable.of({}));
            spyOn(mockCohortFiltersModel, 'setItems');
            spyOn(mockCohortFiltersModel, 'addSelectedValue');
            spyOn(mockService, 'renameCohortIfApplied');
            spyOn(availableCohortsComponent, 'getSelectedCohort').and.returnValue(mockSavedList[0]);
            component.saveAndApplyCohort();
        });

        it('THEN sends the cohort to the server', () => {
            expect(mockService.saveCohort).toHaveBeenCalled();
            expect(mockService.applyCohort).toHaveBeenCalledWith(99, 'a new cohort');
        });

        it('THEN adds the updated cohort list to the component', () => {
            expect(availableCohortsComponent.availableCohorts).toEqual(mockSavedList2);
        });

        it('THEN adds the new cohort to the cohort filter component', () => {
            expect(mockCohortFiltersModel.setItems).toHaveBeenCalledWith(mockSavedList2);
            expect(mockCohortFiltersModel.addSelectedValue).toHaveBeenCalled();
        });

        it('THEN updates who the cohort is shared with', () => {
            expect(component.sharedWith).toEqual(mockSavedList[0].sharedWith);
        });
    });

    describe('WHEN saving an existing cohort', () => {

        const selectedCohortId = 1;
        const savedPopulationCohortFilterId = 101;
        const savedAesCohortFilterId = 102;
        const cohortName = 'a new cohort';

        beforeEach(() => {
            availableCohortsComponent.selectedCohortId = selectedCohortId;
            availableCohortsComponent.newCohortName = cohortName;
            availableCohortsComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: availableCohortsComponent.selectedCohortId,
                    name: availableCohortsComponent.newCohortName,
                },
                cohortFilters: [{
                    id: savedPopulationCohortFilterId,
                    filterView: 'POPULATION'
                },
                    {
                        id: savedAesCohortFilterId,
                        filterView: 'AES'
                    }]
            }];
            spyOn(mockService, 'saveCohort').and.returnValue(Observable.of(mockSavedList));
            spyOn(mockService, 'renameCohortIfApplied');
            spyOn(availableCohortsComponent, 'getSelectedCohort').and.returnValue(mockSavedList[0]);
            spyOn(availableCohortsComponent, 'addSavedCohortsToList');
            spyOn(mockCohortFiltersModel, 'setItems');
            component.saveCohort();
        });

        it('THEN sends the cohort to the server', () => {
            expect(mockService.saveCohort).toHaveBeenCalled();
        });

        it('THEN adds the updated cohort list to the component', () => {
            expect(availableCohortsComponent.addSavedCohortsToList).toHaveBeenCalledWith(mockSavedList);
        });

        it('THEN updates who the cohort is shared with', () => {
            expect(component.sharedWith).toEqual(mockSavedList[0].sharedWith);
        });
    });

    describe('WHEN applying a cohort', () => {

        beforeEach(() => {
            availableCohortsComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: 99,
                    name: 'a cohort'
                }
            }];
            availableCohortsComponent.selectedCohortId = 99;
            spyOn(mockService, 'applyCohort').and.returnValue(Observable.of({}));
            spyOn(mockCohortFiltersModel, 'addSelectedValue');
            component.applyCohort();
        });

        it('THEN applies the selected cohort', () => {
            expect(mockService.applyCohort).toHaveBeenCalledWith(99, 'a cohort');
        });

        it('THEN adds the new cohort to the cohort filter component', () => {
            expect(mockCohortFiltersModel.addSelectedValue).toHaveBeenCalledWith('a cohort');
        });
    });

    describe('WHEN deleting a cohort', () => {
        beforeEach(() => {
            availableCohortsComponent.availableCohorts = <any>[{
                savedFilter: {
                    name: 'a saved cohort',
                    id: 99
                }
            }];
            availableCohortsComponent.selectedCohortId = 99;
            spyOn(mockService, 'deleteCohort').and.returnValue(Observable.of(mockSavedList));
            spyOn(mockCohortFiltersModel, 'setItems');
            availableCohortsComponent.deleteCohort();
        });

        it('THEN sends the request to the server', () => {
            expect(mockService.deleteCohort).toHaveBeenCalledWith(99, 'a saved cohort');
        });

        it('THEN adds the updated cohort list to the component', () => {
            expect(availableCohortsComponent.availableCohorts).toEqual(mockSavedList);
        });

        it('THEN updates the cohort filter component', () => {
            expect(mockCohortFiltersModel.setItems).toHaveBeenCalledWith(mockSavedList);
        });
    });

    describe('WHEN adding a Population filter', () => {
        it('THEN the filter gets added to the list', () => {
            filterListComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: availableCohortsComponent.selectedCohortId,
                    name: availableCohortsComponent.newCohortName,
                },
                cohortFilters: []
            }];
            filterListComponent.addPopulationFilter();

            expect(filterListComponent.availableCohorts[0].cohortFilters[0]).toEqual({
                id: -1,
                filterView: 'POPULATION',
                json: ''
            });
        });
    });

    describe('WHEN adding an Adverse Event filter', () => {
        it('THEN the filter gets added to the list', () => {
            filterListComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: availableCohortsComponent.selectedCohortId,
                    name: availableCohortsComponent.newCohortName,
                },
                cohortFilters: []
            }];
            filterListComponent.addAeFilter();

            expect(filterListComponent.availableCohorts[0].cohortFilters[0]).toEqual({
                id: -1,
                filterView: 'AES',
                json: ''
            });
        });
    });

    describe('WHEN a cohort is selected', () => {
        beforeEach(() => {
            availableCohortsComponent.selectedCohortId = 101;
            availableCohortsComponent.availableCohorts = <any> [{
                savedFilter: {
                    id: availableCohortsComponent.selectedCohortId,
                    name: availableCohortsComponent.newCohortName,
                },
                cohortFilters: [{
                    id: 1,
                    filterView: 'POPULATION',
                    json: '{subjectIds: {values: ["subj-1"]}}'
                },
                    {
                        id: 2,
                        filterView: 'AES',
                        json: '{soc: {values: ["Eye"]}}'
                    }],
                sharedWith: [{
                    id: null, prid: 'kdbg488', fullName: 'Sam Bentley'
                }]
            }];

            spyOn(filterReloadService, 'reloadSavedFilter').and.returnValue(Observable.of({}));

            component.cohortSelected(availableCohortsComponent.availableCohorts[0]);
        });

        it('THEN all its filters are loaded', () => {
            expect(filterReloadService.reloadSavedFilter).toHaveBeenCalledTimes(2);
        });

        it('THEN the sharedWith list is updated', () => {
            expect(component.sharedWith).toBe(availableCohortsComponent.availableCohorts[0].sharedWith);
        });
    });

    describe('WHEN an Adverse Event cohort filter is selected', () => {
        beforeEach(() => {
            const selectedId = 1;

            availableCohortsComponent.availableCohorts = [{
                sharedWith: [],
                savedFilter: {
                    name: 'a saved cohort',
                    id: selectedId,
                    createdDate: new Date(),
                    owner: 'kdbg488',
                    operator: 'OR',
                    filters: [],
                    datasetClass: 'Acuity',
                    datasetId: '333'
                },
                cohortFilters: [{
                    id: 99,
                    filterView: 'AES',
                    json: JSON.stringify({
                        pt: {
                            values: ['Abdominal pain']
                        }
                    })
                }]
            }];

            spyOn(mockAesFiltersModel, 'reset');
            spyOn(mockAesFiltersModel, 'getFilters');


            component.cohortSelected(availableCohortsComponent.availableCohorts[0]);
            filterListComponent.selectFilterInstance('AES');
        });

        it('THEN the filters are reset', () => {
            expect(mockAesFiltersModel.reset).toHaveBeenCalled();
            expect(mockAesFiltersModel.getFilters).toHaveBeenCalled();
        });

        it('THEN applies CheckBox filters', () => {
            mockFilterEventService.eventFilterEventCount.next(null);

            const sexFilter = <ListFilterItemModel> _.find(mockAesFiltersModel.itemsModels, {key: 'pt'});
            expect(sexFilter.selectedValues).toEqual(['Abdominal pain']);
        });
    });

    describe('WHEN a new cohort has been added AND is unsaved', () => {
        it('THEN the component knows it is unsaved', () => {
            availableCohortsComponent.availableCohorts = [];
            availableCohortsComponent.newCohortName = 'new cohort';
            availableCohortsComponent.addModalSubmitted(true);

            expect(component.hasUnsavedChanges()).toBeTruthy();
        });
    });

    describe('WHEN filters have been changed AND is unsaved', () => {
        it('THEN the component knows it is unsaved', () => {
            spyOn(filterReloadService, 'reloadSavedFilter').and.returnValue(Observable.of(true));
            availableCohortsComponent.availableCohorts = [{
                sharedWith: [],
                savedFilter: {
                    name: 'a saved cohort',
                    id: 99,
                    createdDate: new Date(),
                    owner: 'kdbg488',
                    operator: 'OR',
                    filters: [],
                    datasetClass: 'Acuity',
                    datasetId: '333'
                },
                cohortFilters: [{
                    id: 101,
                    filterView: 'POPULATION',
                    json: JSON.stringify({
                        sex: {
                            values: ['M', 'F']
                        },
                    })
                }]
            }];
            mockPopulationFiltersModel.itemsModels[0]['selectedValues'] = ['Y'];
            filterListComponent.selectedFilterInstance = 'POPULATION';
            availableCohortsComponent.selectCohort(99);
            component.updateFilters();
            component.cohortSelected(availableCohortsComponent.availableCohorts[0]);
            mockPopulationFiltersModel.itemsModels[0]['selectedValues'] = ['N'];

            expect(component.hasUnsavedChanges()).toBeTruthy();
        });
    });

    describe('WHEN range filters have been changed AND is unsaved', () => {
        it('THEN the component knows it is unsaved', () => {

            spyOn(filterReloadService, 'reloadSavedFilter').and.returnValue(Observable.of(true));
            availableCohortsComponent.availableCohorts = [{
                sharedWith: [],
                savedFilter: {
                    name: 'a saved cohort',
                    id: 99,
                    createdDate: new Date(),
                    owner: 'kdbg488',
                    operator: 'OR',
                    filters: [],
                    datasetClass: 'Acuity',
                    datasetId: '333'
                },
                cohortFilters: [{
                    id: 101,
                    filterView: 'POPULATION',
                    json: JSON.stringify({
                        age: {
                            haveMadeChange: false,
                            from: 1,
                            to: 100
                        },
                    })
                }]
            }];

            filterListComponent.selectedFilterInstance = 'POPULATION';
            availableCohortsComponent.selectCohort(99);
            component.updateFilters();
            component.cohortSelected(availableCohortsComponent.availableCohorts[0]);
            mockPopulationFiltersModel.itemsModels[0].setSelectedValues({from: 10, to: 20});

            expect(component.hasUnsavedChanges()).toBeTruthy();
        });
    });

    describe('WHEN filters have been added AND are unchnaged', () => {
        it('THEN the Save buttons are disabled', () => {
            component['localPopulationFiltersModel'] = new PopulationFiltersModel(
                <any> new MockFilterHttpService(), <any> mockFilterEventService, <any> {}, <any> {});
            component['localAeFiltersModel'] = new AesFiltersModel(mockPopulationFiltersModel,
                <any> mockFilterEventService, <any> {}, <any> {});

            expect(component.canSave()).toBeFalsy();
        });
    });

    describe('WHEN renaming a new cohort', () => {
        it('THEN the cohort is renamed', () => {
            availableCohortsComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: -1,
                    name: 'a cohort'
                }
            }];
            availableCohortsComponent.selectedCohortId = -1;
            availableCohortsComponent.renamedCohortName = 'renamed cohort';
            availableCohortsComponent.renameModalSubmitted(true);

            expect(availableCohortsComponent.newCohortName).toBe('renamed cohort');
        });
    });

    describe('WHEN renaming a saved, but unapplied cohort', () => {
        it('THEN the cohort is renamed', () => {
            availableCohortsComponent.availableCohorts = <SavedFilterVO[]> [{
                savedFilter: {
                    id: 99,
                    name: 'a cohort'
                }
            }];
            spyOn(mockService, 'saveCohort').and.returnValue(Observable.of(mockSavedList));
            spyOn(availableCohortsComponent, 'addSavedCohortsToList');
            spyOn(mockCohortFiltersModel, 'setItems');
            availableCohortsComponent.selectedCohortId = 99;
            availableCohortsComponent.renamedCohortName = 'renamed cohort';
            availableCohortsComponent.renameModalSubmitted(true);

            component.saveCohort();

            expect(mockService.saveCohort).toHaveBeenCalledWith(
                new SaveCohortDto(99, 'renamed cohort', [], null,
                    null, mockPopulationFiltersModel, mockAesFiltersModel));
        });
    });
});

describe('WHEN a population cohort filter is selected', () => {
    beforeEach(() => {
        const selectedId = 1;
        component = new CohortEditorComponent(
            <any> mockService, <any> mockPopulationFiltersModel, mockCohortFiltersModel, mockAesFiltersModel,
            filterReloadService, <any> {});
        availableCohortsComponent = new AvailableCohortsComponent(<any> mockService, mockCohortFiltersModel);
        filterListComponent = new FilterListComponent(<any> new MockDatasetViews());
        component.availableCohortsComponent = availableCohortsComponent;
        component.filterListComponent = filterListComponent;

        availableCohortsComponent.availableCohorts = [{
            sharedWith: [],
            savedFilter: {
                name: 'a saved cohort',
                id: selectedId,
                createdDate: new Date(),
                owner: 'kdbg488',
                operator: 'OR',
                filters: [],
                datasetClass: 'Acuity',
                datasetId: '333'
            },
            cohortFilters: [{
                id: 101,
                filterView: 'POPULATION',
                json: JSON.stringify({
                    sex: {
                        values: ['M']
                    },
                    subjectId: {
                        values: ['subj-1', 'subj-2']
                    },
                    age: {
                        from: '50',
                        to: '94',
                        includeEmptyValues: true
                    },
                    firstTreatmentDate: {
                        from: '2014-01-01T00:00:00',
                        to: '2014-12-31T00:00:00',
                        includeEmptyValues: true
                    },
                    studySpecificFilters: {
                        values: [
                            'Smoker--N',
                            'Smoker--null',
                            'Tumour Location--Peritoneum',
                            'Tumour Location--Pleura',
                            'Tumour Location--Prostate',
                            'Tumour Location--Renal',
                            'Tumour Location--null'
                        ]
                    },
                    drugsMaxDoses: {
                        map: {
                            Placebo: {
                                values: ['100 MG/KG']
                            }
                        }
                    }
                })
            }]
        }];

        spyOn(mockPopulationFiltersModel, 'reset');
        spyOn(mockPopulationFiltersModel, 'getFilters');

        component.cohortSelected(availableCohortsComponent.availableCohorts[0]);
        filterListComponent.selectFilterInstance('POPULATION');
    });

    it('THEN the filters are reset', () => {
        expect(mockPopulationFiltersModel.reset).toHaveBeenCalled();
        expect(mockPopulationFiltersModel.getFilters).toHaveBeenCalled();
    });

    it('THEN applies CheckBox filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const sexFilter = <CheckListFilterItemModel> _.find(mockPopulationFiltersModel.itemsModels, {key: 'sex'});
        expect(sexFilter.selectedValues).toEqual(['M']);
    });

    it('THEN applies List filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const sexFilter = <ListFilterItemModel> _.find(mockPopulationFiltersModel.itemsModels, {key: 'subjectId'});
        expect(sexFilter.selectedValues).toEqual(['subj-1', 'subj-2']);
    });

    it('THEN applies Range filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const sexFilter = <RangeFilterItemModel> _.find(mockPopulationFiltersModel.itemsModels, {key: 'age'});
        expect(sexFilter.selectedValues.from).toBe('50');
        expect(sexFilter.selectedValues.to).toBe('94');
    });

    it('THEN applies DateRange filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const sexFilter = <RangeDateFilterItemModel> _.find(mockPopulationFiltersModel.itemsModels,
            {key: 'firstTreatmentDate'});
        expect(sexFilter.selectedValues.from).toBe('01-Jan-2014');
        expect(sexFilter.selectedValues.to).toBe('31-Dec-2014');
    });

    it('THEN applies StudySpecific filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const studySpecificFilter = <StudySpecificFilterModel> _.find(mockPopulationFiltersModel.itemsModels,
            {key: 'studySpecificFilters'});
        const smokerFilter = <StudySpecificFilter> _.find(studySpecificFilter.filters, {name: 'Smoker'});
        const tumourLocationFilter = <StudySpecificFilter> _.find(studySpecificFilter.filters, {name: 'Tumour Location'});

        expect(smokerFilter.selectedValues).toEqual(['N', null]);
        expect(tumourLocationFilter.selectedValues).toEqual(['Peritoneum', 'Pleura', 'Prostate', 'Renal', null]);
    });

    it('THEN applies MapList filters', () => {
        mockFilterEventService.populationFilterSubjectCount.next(null);

        const mapFilter = <StudySpecificFilterModel> _.find(mockPopulationFiltersModel.itemsModels, {key: 'drugsMaxDoses'});
        const placeboFilter = <StudySpecificFilter> _.find(mapFilter.filters, {key: 'Placebo'} as any);

        expect(placeboFilter.selectedValues).toEqual(['100 MG/KG']);
    });
});
