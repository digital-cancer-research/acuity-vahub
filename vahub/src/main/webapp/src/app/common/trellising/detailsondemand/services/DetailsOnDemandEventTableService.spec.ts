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
import {GridApi, ColumnApi} from 'ag-grid/main';
import {List} from 'immutable';

import {DetailsOnDemandEventTableService} from './DetailsOnDemandEventTableService';
import {IDetailsOnDemand} from '../../store/ITrellising';
import {
    MockDataService,
    MockDatasetViews,
    MockDetailsOnDemandHeightService
} from '../../../MockClasses';
import {AdverseEventsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/adverse-events/AdverseEventsColumnModel';
import {ConmedsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/conmeds/ConmedsColumnModel';
import {ExacerbationsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/exacerbations/ExacerbationsColumnModel';
import {LabsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/labs/LabsColumnModel';
import {VitalsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/vitals/VitalsColumnModel';
import {CardiacColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cardiac/CardiacColumnModel';
import {RenalColumnsModel} from '../../../../plugins/refactored-singlesubject/datatypes/renal/RenalColumnsModel';
// tslint:disable-next-line
import {CerebrovascularColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cerebrovascular/CerebrovascularColumnModel';
import {CIEventsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cievents/CIEventsColumnModel';
import {CvotColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cvot/CvotColumnModel';
import * as BiomarkersTableColumns from './model/BiomarkersTableColumns';
import {QTProlongationColumnModel} from './model/QTProlongationColumnModel';

describe('GIVEN DetailsOnDemandEventTableService', () => {

    let service: DetailsOnDemandEventTableService;
    let mockDataService: MockDataService;
    let mockDatasetView: MockDatasetViews;
    let heightService: MockDetailsOnDemandHeightService;
    let mockGridApi: GridApi;
    let mockColumnApi: ColumnApi;
    let mockEventEmitter: EventEmitter<IDetailsOnDemand>;
    // let params: IGetRowsParams;
    let gridOptions: any;
    const aesColumnModel = new AdverseEventsColumnModel(),
        conmedsColumnModel = new ConmedsColumnModel(),
        exacerbationsColumnModel = new ExacerbationsColumnModel(),
        labsColumnModel = new LabsColumnModel(),
        renalColumnModel = new RenalColumnsModel(),
        vitalsColumnModel = new VitalsColumnModel(),
        cardiacColumnModel = new CardiacColumnModel(),
        cerebrovascularColumnModel = new CerebrovascularColumnModel(),
        cieventsColumnModel = new CIEventsColumnModel(),
        cvotColumnModel = new CvotColumnModel(),
        qtProlongationModel = new QTProlongationColumnModel();

    beforeEach(() => {
        mockDataService = new MockDataService();
        mockDatasetView = new MockDatasetViews();
        heightService = new MockDetailsOnDemandHeightService();
        mockGridApi = <any> {
            setColumnDefs: function (): void {
            }
        };
        mockColumnApi = <any> {
            getAllColumns: function (): void {
            },
            getAllDisplayedColumnGroups: function (): any {
                return null;
            },
            setColumnGroupOpened: function (): void {

            },
            getColumnState: function (): any {
                return [{width: 1}];
            },
            setColumnState: function(): void {
            }
        };
        mockEventEmitter = new EventEmitter<IDetailsOnDemand>();
        service = new DetailsOnDemandEventTableService(
            <any> mockDataService,
            <any> mockDatasetView,
            <any>heightService,
            aesColumnModel,
            conmedsColumnModel,
            exacerbationsColumnModel,
            labsColumnModel,
            renalColumnModel,
            vitalsColumnModel,
            cardiacColumnModel,
            cieventsColumnModel,
            cerebrovascularColumnModel,
            cvotColumnModel,
            qtProlongationModel);
        // params = {
        //     startRow: 0,
        //     endRow: 5,
        //     successCallback: function (): void {
        //     },
        //     failCallback: function (): void {
        //     },
        //     sortModel: null,
        //     filterModel: null,
        //     context: {
        //         tabId: TabId.VITALS_BOXPLOT,
        //         ids: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
        //         modelChanged: mockEventEmitter,
        //         allData: []
        //     }
        // };
        gridOptions = {
            api: mockGridApi,
            columnApi: mockColumnApi
        };
        service.setGridOptions(gridOptions);
    });

    describe('WHEN groups are checked', () => {
        it('THEN groups are available for biomarker page', () => {
            spyOn(service, 'getMetadataKey').and.returnValue('biomarker');
            expect(service.hasOnlyGroups()).toBeTruthy();
        });
        it('THEN groups are not available for any other page', () => {
            spyOn(service, 'getMetadataKey').and.returnValue('any value');
            expect(service.hasOnlyGroups()).toBeFalsy();
        });
    });

    describe('WHEN setColumnDefs is called', () => {
        const columns = [{'studyId': 'studyId'}, {'studyPart': 'studyPart'}, {'subjectId': 'subjectId'},
            {'sampleType': 'sampleType'}, {'sampleId': 'sampleId'}, {'variantCount': 'variantCount'}];
        beforeEach(() => {
            spyOn(mockDatasetView, 'getDetailsOnDemandColumns').and.returnValue(columns);
        });
        describe('AND page has groups', () => {
            beforeEach(() => {
                spyOn(service, 'getMetadataKey').and.returnValue('biomarker');
                spyOn(service, 'hasOnlyGroups').and.returnValue('true');
                spyOn(service, 'getColumnsByPage').and.returnValue(List());
                spyOn(BiomarkersTableColumns, 'getColumnGroupDefs').and.returnValue(List([
                    {
                        headerName: 'Sample status',
                        children: [
                            {headerName: 'Sample type', field: 'sampleType', columnGroupShow: 'open'},
                            {headerName: 'Sample id', field: 'sampleId', columnGroupShow: 'open'},
                            {headerName: 'Variant count', field: 'variantCount', columnGroupShow: 'open'}
                        ]
                    }
                ]));
                spyOn(mockGridApi, 'setColumnDefs');
                spyOn(mockColumnApi, 'setColumnGroupOpened');

            });
            it('THEN groups are not available for any other page', () => {
                service.setColumnDefs([]);
                expect(BiomarkersTableColumns.getColumnGroupDefs).toHaveBeenCalledWith(columns);
                expect(mockColumnApi.setColumnGroupOpened).not.toHaveBeenCalled();
                expect(mockGridApi.setColumnDefs).toHaveBeenCalledWith([Object({
                    headerName: 'Sample status',
                    children: [{
                        headerName: 'Sample type',
                        field: 'sampleType',
                        columnGroupShow: null,
                        headerTooltip: 'Sample type',
                        tooltipField: 'sampleType'
                    }, {
                        headerName: 'Sample id',
                        field: 'sampleId',
                        columnGroupShow: 'open',
                        headerTooltip: 'Sample id',
                        tooltipField: 'sampleId'
                    }, {
                        headerName: 'Variant count',
                        field: 'variantCount',
                        columnGroupShow: 'open',
                        headerTooltip: 'Variant count',
                        tooltipField: 'variantCount'
                    }]
                })]);
            });
        });
    });
});
