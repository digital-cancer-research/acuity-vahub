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

import {SimpleChange, SimpleChanges} from '@angular/core';
import {fromJS, Map} from 'immutable';

import {DetailsOnDemandComponent} from './DetailsOnDemandComponent';
import {TabId} from '../store/ITrellising';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/finally';
import 'rxjs/add/observable/of';

class MockEventTableService {
    saveToStore(): void {
    }

    setGridOptions(): void {
    }

    hasOnlyGroups(): boolean {
        return false;
    }

    getTableData(tabId: TabId, ids: string[] | Map<string, string[]>): any {
    }

    formatMappedColumns(rowsThisPage: any): void {
    }

    setColumnDefs(expandedGroupsIds: string[]): void {
    }
}

class MockSubjectTableService {
    saveToStore(): void {
    }

    setGridOptions(): void {
    }

    getTableData(tabId: TabId, ids: string[] | Map<string, string[]>): any {
    }

    formatMappedColumns(rowsThisPage: any): void {
    }

    setColumnDefs(expandedGroupsIds: string[]): void {
    }
}

class MockChangeDetectorRef {
    detectChanges(): void {
    }
}

class MockDetailsOnDemandHeightService {
}

class MockDetailsOnDemandSummaryService {
    getSubjectSummary(): string {
        return '';
    }

    getEventSummary(): string {
        return '';
    }
}

class MockDataService {
    downloadAllDetailsOnDemandData(): void {
    }

    downloadDetailsOnDemandData(): void {
    }
}

class MockRouter {
}

xdescribe('GIVEN DetatilsOnDemandComponent', () => {

    let component: DetailsOnDemandComponent;
    const mockEventTableService = new MockEventTableService();
    const mockSubjectTableService = new MockSubjectTableService();
    const mockDetailsOnDemandHeightService = new MockDetailsOnDemandHeightService();
    const mockDetailsOnDemandSummaryService = new MockDetailsOnDemandSummaryService();
    const mockDataService = new MockDataService();
    const mockRouter = new MockRouter();
    const mockChangeDetectorRef = new MockChangeDetectorRef();
    const selection = {
        eventIds: ['1', '2'],
        subjectIds: ['a', 'b'],
        totalSubjects: 10,
        totalEvents: 100,
    };

    beforeEach(() => {
        component = new DetailsOnDemandComponent(
            <any> mockEventTableService,
            <any> mockSubjectTableService,
            <any> mockDetailsOnDemandHeightService,
            <any> mockDetailsOnDemandSummaryService,
            <any> mockDataService,
            <any> mockRouter,
            <any> mockChangeDetectorRef
        );
        component.eventGridOptions = <any> {
            context: {},
            api: {
                setRowData: function (): void {
                }
            }
        };
        component.subjectGridOptions = <any> {
            context: {},
            api: {
                setRowData: function (): void {
                }
            }
        };
        component.tabId = 'A_TAB_ID';
    });

    describe('WHEN a selection is made', () => {
        beforeEach(() => {
            const changes: SimpleChanges = {
                selectionDetail: {
                    currentValue: <any> selection
                } as SimpleChange
            };
            component.selectionDetail = selection;
            spyOn(mockDetailsOnDemandSummaryService, 'getEventSummary').and.returnValue('100 of 500');
            spyOn(mockDetailsOnDemandSummaryService, 'getSubjectSummary').and.returnValue('10 of 50');
            spyOn(mockSubjectTableService, 'getTableData').and.returnValue(Observable.of({
                summary: '10 of 50',
                ids: ['a', 'b'],
                allData: []
            }));
            spyOn(mockEventTableService, 'getTableData').and.returnValue(Observable.of({
                summary: '100 of 500',
                ids: ['1', '2'],
                allData: []
            }));
            spyOn(mockChangeDetectorRef, 'detectChanges');
            component.ngOnChanges(changes);
        });

        it('THEN the summary is updated', () => {
            expect(component.eventSummary).toBe('100 of 500');
            expect(component.subjectSummary).toBe('10 of 50');
        });

        it('THEN the tables are updated', () => {
            expect(component.subjectTableService.getTableData).toHaveBeenCalled();
            expect(component.eventTableService.getTableData).toHaveBeenCalled();
        });

        it('THEN the event table\'s ag-grid context is set correctly', () => {
            expect(component.eventGridOptions.context).toEqual({
                summary: '100 of 500',
                ids: selection.eventIds,
                allData: []
            });
        });

        it('THEN the subject table\'s ag-grid context is set correctly', () => {
            expect(component.subjectGridOptions.context).toEqual({
                summary: '10 of 50',
                ids: selection.subjectIds,
                allData: []
            });
        });
    });

    describe('WHEN the event tab is opened', () => {
        it('THEN the opened state is saved to the store', () => {
            component.subjectsTableVisible = true;
            component.openEventsTab();
            expect(component.subjectsTableVisible).toBeFalsy();
        });
    });

    describe('WHEN the subject tab is opened', () => {
        it('THEN the opened state is saved to the store', () => {
            component.subjectsTableVisible = false;
            component.openSubjectsTab();
            expect(component.subjectsTableVisible).toBeTruthy();
        });
    });

    describe('WHEN there is stored event data', () => {
        const mockIds = [1, 2];
        const mockData = [{col1: 'abc'}, {col1: 'xyz'}];
        const mockExpandedGroups = [{headerName: 'column header'}];
        const mockSummary = '1 of 10';

        beforeEach(() => {
            component.eventModel = fromJS({
                tableData: mockData,
                expandedGroups: mockExpandedGroups,
                summary: mockSummary
            });
            component.onEventTableInitialised();
        });
    });

    describe('WHEN there is stored subject data', () => {
        const mockIds = [1, 2];
        const mockData = [{col1: 'abc'}, {col1: 'xyz'}];
        const mockExpandedGroups = [{headerName: 'column header'}];
        const mockSummary = '1 of 10';

        beforeEach(() => {
            component.subjectModel = fromJS({
                tableData: mockData,
                expandedGroups: mockExpandedGroups,
                eventIds: mockIds,
                summary: mockSummary
            });

            component.onSubjectTableInitialised();
        });

        it('THEN the data is reloaded correctly', () => {
            expect(component.subjectGridOptions.context.allData).toEqual(mockData);
        });

        it('THEN the expanded column groups are restored', () => {
            expect(component.subjectGridOptions.context.expandedGroups).toEqual(mockExpandedGroups);
        });

        it('THEN the subject IDs are restored', () => {
            expect(component.subjectGridOptions.context.ids).toEqual(mockIds);
        });

        it('THEN the summary is restored', () => {
            expect(component.subjectGridOptions.context.summary).toEqual(mockSummary);
            expect(component.subjectSummary).toEqual(mockSummary);
        });
    });

    describe('WHEN a column in the event table is sorted', () => {
        it('THEN the data is requested from server', () => {
            spyOn(mockEventTableService, 'getTableData').and.returnValue(Observable.of({}));
            component.selectionDetail = selection;
            component.fetchEventTableData();
            expect(component.eventTableService.getTableData).toHaveBeenCalled();
        });
    });

    describe('WHEN a column in the subject table is sorted', () => {
        it('THEN the data is requested from server', () => {
            spyOn(mockSubjectTableService, 'getTableData').and.returnValue(Observable.of({}));
            component.selectionDetail = selection;
            component.fetchSubjectTableData();
            expect(component.subjectTableService.getTableData).toHaveBeenCalled();
        });
    });

    describe('WHEN we download all event data', () => {
        it('THEN it is requested from the server', () => {
            component.subjectsTableVisible = false;
            component.saveModalTitle = 'Download all records?';
            spyOn(mockDataService, 'downloadAllDetailsOnDemandData');

            component.saveModalSubmitted(true);

            expect(mockDataService.downloadAllDetailsOnDemandData).toHaveBeenCalledWith(component.tabId);
        });
    });

    describe('WHEN we download all subject data', () => {
        it('THEN it is requested from the server', () => {
            component.subjectsTableVisible = true;
            component.saveModalTitle = 'Download all records?';
            spyOn(mockDataService, 'downloadAllDetailsOnDemandData');

            component.saveModalSubmitted(true);

            expect(mockDataService.downloadAllDetailsOnDemandData).toHaveBeenCalledWith(TabId.POPULATION_BARCHART);
        });
    });

    describe('WHEN we download event data from the table', () => {
        it('THEN it is requested from the server', () => {
            component.subjectsTableVisible = false;
            component.eventModel = fromJS({eventIds: selection.eventIds});
            component.saveModalTitle = 'Download displayed records?';
            spyOn(mockDataService, 'downloadDetailsOnDemandData');

            component.saveModalSubmitted(true);

            expect(mockDataService.downloadDetailsOnDemandData).toHaveBeenCalled();
        });
    });

    describe('WHEN we download subject data from the table', () => {
        it('THEN it is requested from the server', () => {
            component.subjectsTableVisible = true;
            component.subjectModel = fromJS({eventIds: selection.subjectIds});
            component.saveModalTitle = 'Download displayed records?';
            spyOn(mockDataService, 'downloadDetailsOnDemandData');

            component.saveModalSubmitted(true);

            expect(mockDataService.downloadDetailsOnDemandData).toHaveBeenCalled();
        });
    });

});
