import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {TestBed, inject} from '@angular/core/testing';

import {MockSessionEventService} from '../../../../common/MockClasses';
import {SessionEventService} from '../../../../session/module';
import {PopulationFiltersModel, AesFiltersModel, FilterEventService} from '../../../../filters/module';
import {AesHttpService} from '../../../../data/aes/AesHttpService';
import {AesTableDropdownModel} from '../AesTableDropdownModel';
import {AesTableServiceEnterprise} from './AesTableServiceEnterprise';

describe('GIVEN AesTableServiceEnterprise', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                FilterEventService,
                AesTableDropdownModel,
                AesTableServiceEnterprise,
                HttpClient,
                {provide: AesHttpService, useClass: AesHttpService, deps: [HttpClient, PopulationFiltersModel, AesFiltersModel]},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]}
            ]
        });
    });

    describe('WHEN there are no subjects for a cell', () => {
        it('THEN zero is displayed', inject([AesTableServiceEnterprise], (aesTableService: AesTableServiceEnterprise) => {
            const mockAgGridParams = {
                value: null,
                node: {
                    footer: false
                },
                api: {
                    rowModel: {
                        getRow: function (): any {
                            return {
                                level: 1
                            };
                        }
                    },
                    getModel: function (): any {
                    }
                }
            };
            spyOn(mockAgGridParams.api, 'getModel').and.returnValue(mockAgGridParams.api.rowModel);

            expect(aesTableService.renderCell(mockAgGridParams)).toBe('0 (0%)');
        }));
    });

    describe('WHEN the Number of Subjects cell is rendered for a Severity row', () => {
        it('THEN the text is correct', inject([AesTableServiceEnterprise], (aesTableService: AesTableServiceEnterprise) => {
            const subjectCountPerGrade = 10;
            const subjectCountPerArm = 100;
            const mockAgGridParams = {
                value: subjectCountPerGrade,
                rowIndex: 0,
                colDef: {
                    pivotKeys: ['Placebo']
                },
                node: {
                    footer: false
                },
                api: {
                    rowModel: {
                        rowsToDisplay: [{
                            allLeafChildren: [{
                                data: {
                                    treatmentArm: 'Placebo',
                                    subjectCountPerGrade: subjectCountPerGrade,
                                    subjectCountPerArm: subjectCountPerArm
                                }
                            }],
                            level: 1
                        }],
                        getRow: function (): any {
                        }
                    },
                    getModel: function (): any {
                    }
                }
            };

            spyOn(mockAgGridParams.api, 'getModel').and.returnValue(mockAgGridParams.api.rowModel);
            spyOn(mockAgGridParams.api.rowModel, 'getRow').and.returnValue(mockAgGridParams.api.rowModel.rowsToDisplay[0]);

            const expected = subjectCountPerGrade + ' (' + subjectCountPerGrade / subjectCountPerArm * 100 + '%)';
            expect(aesTableService.renderCell(mockAgGridParams)).toBe(expected);
        }));
    });

    describe('WHEN the Number of Subjects cell is rendered for an AE Term row', () => {
        it('THEN the text is correct', inject([AesTableServiceEnterprise], (aesTableService: AesTableServiceEnterprise) => {
            const subjectCountPerTerm = 5;
            const subjectCountPerArm = 100;
            const mockAgGridParams = {
                value: subjectCountPerTerm,
                rowIndex: 0,
                colDef: {
                    pivotKeys: ['Placebo']
                },
                node: {
                    footer: false
                },
                api: {
                    rowModel: {
                        rowsToDisplay: [{
                            allLeafChildren: [{
                                data: {
                                    treatmentArm: 'Placebo',
                                    subjectCountPerTerm: subjectCountPerTerm,
                                    subjectCountPerArm: subjectCountPerArm
                                }
                            }]
                        }],
                        getRow: function (): any {
                        }
                    },
                    getModel: function (): any {
                    }
                }
            };

            spyOn(mockAgGridParams.api, 'getModel').and.returnValue(mockAgGridParams.api.rowModel);
            spyOn(mockAgGridParams.api.rowModel, 'getRow').and.returnValue(mockAgGridParams.api.rowModel.rowsToDisplay[0]);

            const expected = subjectCountPerTerm + ' (' + subjectCountPerTerm / subjectCountPerArm * 100 + '%)';
            expect(aesTableService.renderCell(mockAgGridParams)).toBe(expected);
        }));
    });

    describe('WHEN the footer row is rendered', () => {
        it('THEN the text is correct', inject([AesTableServiceEnterprise], (aesTableService: AesTableServiceEnterprise) => {
            const subjectCountPerTerm = 5;
            const subjectCountPerArm = 100;
            const mockAgGridParams = {
                value: subjectCountPerTerm,
                node: {
                    footer: true
                },
                rowIndex: 0,
                colDef: {
                    pivotKeys: ['Placebo']
                },
                api: {
                    rowModel: {
                        rowsToDisplay: [{
                            allLeafChildren: [{
                                data: {
                                    treatmentArm: 'Placebo',
                                    subjectCountPerArm: subjectCountPerArm,
                                    noIncidenceCount: 95
                                }
                            }]
                        }],
                        getRow: function (): any {
                        }
                    },
                    getModel: function (): any {
                    }
                }
            };

            spyOn(mockAgGridParams.api, 'getModel').and.returnValue(mockAgGridParams.api.rowModel);
            spyOn(mockAgGridParams.api.rowModel, 'getRow').and.returnValue(mockAgGridParams.api.rowModel.rowsToDisplay[0]);

            expect(aesTableService.renderCell(mockAgGridParams)).toBe('95 (95%)');
        }));
    });
});
