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

import {EventEmitter} from '@angular/core';
import {GridApi, IGetRowsParams, ColumnApi} from 'ag-grid/main';
import {fromJS} from 'immutable';

import {DetailsOnDemandSubjectTableService} from './DetailsOnDemandSubjectTableService';
import {IDetailsOnDemand} from '../../store/ITrellising';
import {
    MockDataService,
    MockSubject,
    MockDatasetViews,
    MockDetailsOnDemandHeightService
} from '../../../../common/MockClasses';

// xdescribe('GIVEN DetailsOnDemandSubjectTableService', () => {
//
//     let service: DetailsOnDemandSubjectTableService;
//     let mockDataService: MockDataService;
//     let mockDatasetView: MockDatasetViews;
//     let heightService: MockDetailsOnDemandHeightService;
//     let mockGridApi: GridApi;
//     let mockColumnApi: ColumnApi;
//     let mockEventEmitter: EventEmitter<IDetailsOnDemand>;
//     let params: IGetRowsParams;
//     let gridOptions: any;
//
//     beforeEach(() => {
//         mockDataService = new MockDataService();
//         mockDatasetView = new MockDatasetViews();
//         heightService = new MockDetailsOnDemandHeightService();
//
//         mockGridApi = <any> {
//             setColumnDefs: function (): void {
//             }
//         };
//         mockColumnApi = <any> {
//             getAllColumns: function (): void {
//             },
//             getAllDisplayedColumnGroups: function (): any {
//                 return null;
//             }
//         };
//         mockEventEmitter = new EventEmitter<IDetailsOnDemand>();
//         service = new DetailsOnDemandSubjectTableService(<any> mockDataService, <any> mockDatasetView, <any>heightService);
//         params = {
//             startRow: 0,
//             endRow: 5,
//             successCallback: function (): void {
//             },
//             failCallback: function (): void {
//             },
//             sortModel: null,
//             filterModel: null,
//             context: {
//                 tabId: '',
//                 ids: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
//                 modelChanged: mockEventEmitter,
//                 allData: []
//             }
//         };
//         gridOptions = {
//             api: mockGridApi,
//             columnApi: mockColumnApi
//         };
//         service.setGridOptions(gridOptions);
//     });
//
//     describe('WHEN the metadata is set', () => {
//         it('THEN the columns are defined', () => {
//             spyOn(mockGridApi, 'setColumnDefs');
//             spyOn(mockDatasetView, 'getDetailsOnDemandColumns').and.returnValue(['subjectId', 'studySpecificFilters--Tumour Location']);
//
//             service.setGridOptions(gridOptions);
//
//             expect(mockGridApi.setColumnDefs).toHaveBeenCalledWith([
//                 {
//                     headerName: 'Studies, Parts and Subjects',
//                     children: [{
//                         headerName: 'Subject ID',
//                         field: 'subjectId',
//                         columnGroupShow: null,
//                         headerTooltip: 'Subject ID',
//                         tooltipField: 'subjectId'
//                     }
//                     ]
//                 },
//                 {
//                     headerName: 'Study Specific Filters',
//                     children: [{
//                         headerName: 'Tumour Location',
//                         field: 'studySpecificFilters--Tumour Location',
//                         columnGroupShow: null,
//                         headerTooltip: 'Tumour Location',
//                         tooltipField: 'studySpecificFilters--Tumour Location',
//                         suppressSorting: true
//                     }]
//                 }
//             ]);
//         });
//     });
//
//     describe('WHEN we get paginated table data', () => {
//
//         it('THEN the data is returned', () => {
//             let mockDetailsOnDemandService = new MockSubject();
//             // Single column for simplicity
//             let mockSingleColumnTableData = [
//                 'a', 'b', 'c', 'd', 'e'
//             ];
//             spyOn(mockDataService, 'getDetailsOnDemandData').and.returnValue(mockDetailsOnDemandService);
//             spyOn(params, 'successCallback');
//
//             service.getRows(params);
//             mockDetailsOnDemandService.next(mockSingleColumnTableData);
//
//             expect(mockDataService.getDetailsOnDemandData).toHaveBeenCalledWith(
//                 '', ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'], 0, 5, 'subjectId', 'asc');
//             expect(params.successCallback).toHaveBeenCalledWith(mockSingleColumnTableData, -1);
//         });
//
//         it('THEN the data is saved to the store', () => {
//             let mockDetailsOnDemandService = new MockSubject();
//             // Single column for simplicity
//             let mockSingleColumnTableData = [
//                 {subjectId: 'a'}, {subjectId: 'b'}, {subejctId: 'c'}, {subjectId: 'd'}, {subjectId: 'e'}
//             ];
//             spyOn(mockDataService, 'getDetailsOnDemandData').and.returnValue(mockDetailsOnDemandService);
//             spyOn(params, 'successCallback');
//             spyOn(mockEventEmitter, 'emit');
//
//             service.getRows(params);
//             mockDetailsOnDemandService.next(mockSingleColumnTableData);
//
//             expect(mockEventEmitter.emit).toHaveBeenCalledWith(fromJS({
//                 summary: undefined,
//                 eventIds: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
//                 isOpen: undefined,
//                 isExpanded: false,
//                 tableData: mockSingleColumnTableData,
//                 expandedGroups: []
//             }));
//         });
//
//         describe('AND there are no subject IDs', () => {
//             it('THEN no data is requested', () => {
//                 spyOn(mockDataService, 'getDetailsOnDemandData');
//                 params.context.ids = [];
//
//                 service.getRows(params);
//
//                 expect(mockDataService.getDetailsOnDemandData).not.toHaveBeenCalled();
//             });
//         });
//
//         describe('AND the server returns no data', () => {
//             it('THEN no data is added to the table', () => {
//                 spyOn(params, 'successCallback');
//                 let tableData = [];
//
//                 service.getRows(params);
//                 mockDataService.getDetailsOnDemandData().next(tableData);
//
//                 expect(params.successCallback).not.toHaveBeenCalled();
//             });
//         });
//
//         describe('AND a column has been sorted in asc order after all the data has been received', () => {
//             beforeEach(() => {
//                 let mockDetailsOnDemandService = new MockSubject();
//                 let mockSingleColumnTableData = [
//                     {colA: 0}, {colA: 1}, {colA: 2}, {colA: 3}
//                 ];
//                 spyOn(mockDataService, 'getDetailsOnDemandData').and.returnValue(mockDetailsOnDemandService);
//                 spyOn(params, 'successCallback');
//                 spyOn(mockEventEmitter, 'emit');
//                 params.sortModel = [
//                     {colId: 'colA', sort: 'asc'}
//                 ];
//                 params.context.allData = []; // this is reset before sorting in DetailsOnDemandComponent
//
//                 service.getRows(params);
//                 mockDetailsOnDemandService.next(mockSingleColumnTableData);
//             });
//
//             it('THEN the data is re-requested from the server (we use server sorting & pagination)', () => {
//                 expect(mockDataService.getDetailsOnDemandData).toHaveBeenCalledWith(
//                     '', ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'], 0, 5, 'colA', 'asc');
//                 expect(params.successCallback).toHaveBeenCalledWith([
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3},
//                 ], -1);
//             });
//
//             it('THEN the data is saved in the store', () => {
//                 expect(mockEventEmitter.emit).toHaveBeenCalledWith(fromJS({
//                     summary: undefined,
//                     eventIds: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
//                     isOpen: undefined,
//                     isExpanded: false,
//                     tableData: [
//                         {colA: 0},
//                         {colA: 1},
//                         {colA: 2},
//                         {colA: 3},
//                     ],
//                     expandedGroups: []
//                 }));
//             });
//         });
//
//         describe('AND a column has been sorted in asc order before new data has been received', () => {
//             it('THEN the data is new data from the server is added after the current data', () => {
//                 spyOn(params, 'successCallback');
//                 let currentData = [
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3}
//                 ];
//                 let newData = [
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3}
//                 ];
//                 params.sortModel = [
//                     {colId: 'colA', sort: 'asc'}
//                 ];
//                 params.context.allData = currentData;
//
//                 service.getRows(params);
//                 mockDataService.getDetailsOnDemandData().next(newData);
//
//                 expect(params.successCallback).toHaveBeenCalledWith([
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3},
//                 ], -1);
//                 expect(params.context.allData).toEqual([
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3},
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3}
//                 ]);
//             });
//         });
//     });
//
//     describe('WHEN there is data in the store', () => {
//         it('AND all of the data has been loaded from the server', () => {
//             it('THEN the data is reloaded', () => {
//                 params.context.needToReloadStoredData = true;
//                 spyOn(params, 'successCallback');
//                 let savedData = [
//                     {colA: 0},
//                     {colA: 1},
//                     {colA: 2},
//                     {colA: 3}
//                 ];
//                 params.context.ids = [1, 2, 3, 4];
//                 params.context.allData = savedData;
//
//                 service.getRows(params);
//
//                 expect(params.successCallback).toHaveBeenCalledWith(savedData, params.context.allData.length);
//             });
//
//             it('AND some of the data has been loaded from the server', () => {
//                 let savedData: any[];
//
//                 beforeEach(() => {
//                     params.context.needToReloadStoredData = true;
//                     spyOn(params, 'successCallback');
//                     spyOn(mockGridApi, 'setColumnDefs');
//                     savedData = [
//                         {colA: 0},
//                         {colA: 1},
//                         {colA: 2},
//                         {colA: 3}
//                     ];
//                     // if there are more subject IDs than savedData, then the server has more data to load when a user scrolls
//                     params.context.ids = [1, 2, 3, 4, 5];
//                     params.context.allData = savedData;
//
//                     service.getRows(params);
//                 });
//
//                 it('THEN ag-grid is told there is more data to load', () => {
//                     expect(params.successCallback).toHaveBeenCalledWith(savedData, -1);
//                 });
//
//                 it('THEN the columns are loaded in the correct order', () => {
//                     expect(mockGridApi.setColumnDefs).toHaveBeenCalledWith([{headerName: 'Col a', field: 'colA'}]);
//                 });
//             });
//         });
//
//         describe('AND study specific filter data is returned', () => {
//
//             it('THEN it is formatted correctly', () => {
//                 let mockDetailsOnDemandService = new MockSubject();
//                 // Single column & row for simplicity
//                 let mockSingleColumnTableData = [
//                     {
//                         studySpecificFilters: [
//                             {'Smoker': 'No'},
//                             {'Tumour Location': 'Arm'}
//                         ]
//                     }
//                 ];
//                 spyOn(mockDataService, 'getDetailsOnDemandData').and.returnValue(mockDetailsOnDemandService);
//                 spyOn(params, 'successCallback');
//
//                 service.getRows(params);
//                 mockDetailsOnDemandService.next(mockSingleColumnTableData);
//
//                 expect(params.successCallback).toHaveBeenCalledWith([
//                     {
//                         studySpecificFilters: [{'Smoker': 'No'}, {'Tumour Location': 'Arm'}],
//                         'studySpecificFilters--Smoker': 'No',
//                         'studySpecificFilters--Tumour Location': 'Arm'
//                     }
//                 ], -1);
//             });
//         });
//     });
// });
