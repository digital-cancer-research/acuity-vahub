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

// import {inject, TestBed} from '@angular/core/testing';
// import {BaseRequestOptions} from '@angular/http';
// import {HttpClientTestingModule} from '@angular/common/http/testing';
// import {MockBackend} from '@angular/http/testing';
// import * as _ from 'lodash';
//
// import {
//     PopulationFiltersModel,
//     AesFiltersModel
// } from '../filters/module';
// import {} from '../filters/dataTypes/aes/AesFiltersModel';
// import {FiltersExportService} from './FiltersExportService';
// import {FiltersUtils} from './utils/FiltersUtils';
// import {MockFilterModel, MockFiltersUtils} from '../common/MockClasses';
// import {DatasetViews} from '../security/DatasetViews';
// import {StudyService} from '../common/StudyService';
// import {BaseFilterItemModel} from './components/module';
//
// xdescribe('GIVEN a FiltersExportService class', () => {
//     let service;
//     let filtersUtils;
//     let aesFiltersModel;
//     let populationFiltersModel;
//
//     beforeEach(() => {
//         TestBed.configureTestingModule({
//             imports: [HttpClientTestingModule],
//             providers: [
//                 FiltersExportService,
//                 DatasetViews,
//                 BaseRequestOptions,
//                 MockBackend,
//                 StudyService,
//                 {provide: FiltersUtils, useClass: MockFiltersUtils},
//                 {provide: PopulationFiltersModel, useClass: MockFilterModel},
//                 {provide: AesFiltersModel, useClass: MockFilterModel}
//             ]
//         });
//     });
//
//     beforeEach(inject([FiltersExportService, AesFiltersModel, FiltersUtils, PopulationFiltersModel], (_service: FiltersExportService, _aesFiltersModel: AesFiltersModel, _filtersUtils: FiltersUtils, _populationFiltersModel: PopulationFiltersModel) => {
//         service = _service;
//         filtersUtils = _filtersUtils;
//         aesFiltersModel = _aesFiltersModel;
//         populationFiltersModel = _populationFiltersModel;
//     }));
//
//     describe('WHEN filters are transformed to text', () => {
//         let filterName;
//
//         beforeEach(() => {
//             filterName = 'Population';
//         });
//
//         it('THEN matched items count is displayed correctly', () => {
//             service.populationFiltersModel.matchedItemsCount = 199;
//
//             const filtersText = service.transformFiltersToText();
//             const pattern = 'Matched subjects count:';
//             const matchedItemsCountIndex = filtersText.indexOf(pattern);
//             const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(filterName) + filterName.length + 1);
//             expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('199');
//         });
//
//         describe('AND range filter is transformed', () => {
//
//             it('THEN appropriate method is called', () => {
//                 const rangeFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'durationOnStudy'});
//                 rangeFilterItemModel.selectedValues = {from: 1, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.appliedSelectedValues = {from: 1, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
//                 rangeFilterItemModel.haveMadeChange = true;
//
//                 spyOn(service, 'transformRangeFilter').and.callThrough();
//
//                 service.transformFiltersToText();
//
//                 expect(service.transformRangeFilter).toHaveBeenCalledWith(rangeFilterItemModel);
//             });
//
//             it('THEN output is represented correctly if both values are present', () => {
//                 const rangeFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'durationOnStudy'});
//                 rangeFilterItemModel.selectedValues = {from: 1, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.appliedSelectedValues = {from: 1, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
//                 rangeFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Duration on Study: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from 1, to 10, include empty values: true');
//             });
//
//             it('THEN output is represented correctly if first value is present', () => {
//                 const rangeFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'durationOnStudy'});
//                 rangeFilterItemModel.selectedValues = {from: 1, to: 99, includeEmptyValues: true};
//                 rangeFilterItemModel.appliedSelectedValues = {from: 1, to: 99, includeEmptyValues: true};
//                 rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
//                 rangeFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Duration on Study: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from 1, to <no filter set>, include empty values: true');
//             });
//
//             it('THEN output is represented correctly if second value is present', () => {
//                 const rangeFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'durationOnStudy'});
//                 rangeFilterItemModel.selectedValues = {from: -99, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.appliedSelectedValues = {from: -99, to: 10, includeEmptyValues: true};
//                 rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
//                 rangeFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Duration on Study: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from <no filter set>, to 10, include empty values: true');
//             });
//         });
//
//         describe('AND range date filter is transformed', () => {
//             it('THEN appropriate method is called', () => {
//                 const rangeDateFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'randomisationDate'});
//                 rangeDateFilterItemModel.selectedValues = {
//                     from: '01-Feb-2000',
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.appliedSelectedValues = {
//                     from: '01-Feb-2000',
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.haveMadeChange = true;
//
//                 spyOn(service, 'transformRangeDateFilter').and.callThrough();
//                 // spyOn(filtersUtils, 'formatDateToDDMMMYYYY').and.returnValue();
//
//                 service.transformFiltersToText();
//
//                 expect(service.transformRangeDateFilter).toHaveBeenCalledWith(rangeDateFilterItemModel);
//             });
//
//             it('THEN output is represented correctly', () => {
//
//                 const rangeDateFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'randomisationDate'});
//                 rangeDateFilterItemModel.selectedValues = {
//                     from: '01-Feb-2000',
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.appliedSelectedValues = {
//                     from: '01-Feb-2000',
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Date of Randomisation: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from 01-Feb-2000, to 01-Feb-2001, include empty values: true');
//             });
//
//             it('THEN output is represented correctly when from value is not set', () => {
//
//                 const rangeDateFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'randomisationDate'});
//                 rangeDateFilterItemModel.selectedValues = {
//                     from: null,
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.appliedSelectedValues = {
//                     from: null,
//                     to: '01-Feb-2001',
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText(rangeDateFilterItemModel);
//                 const pattern = 'Date of Randomisation: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from <no filter set>, to 01-Feb-2001, include empty values: true');
//             });
//
//             it('THEN output is represented correctly when to value is not set', () => {
//
//                 const rangeDateFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'randomisationDate'});
//                 rangeDateFilterItemModel.selectedValues = {
//                     from: '01-Feb-2000',
//                     to: null,
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.appliedSelectedValues = {
//                     from: '01-Feb-2000',
//                     to: null,
//                     includeEmptyValues: true
//                 };
//                 rangeDateFilterItemModel.haveMadeChange = true;
//
//                 const filtersText = service.transformFiltersToText(rangeDateFilterItemModel);
//                 const pattern = 'Date of Randomisation: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('from 01-Feb-2000, to <no filter set>, include empty values: true');
//             });
//         });
//
//         describe('AND list filter is transformed', () => {
//             it('THEN appropriate method is called', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
//                 listFilterItemModel.selectedValues = ['A', 'B'];
//                 listFilterItemModel.appliedSelectedValues = ['A', 'B'];
//
//                 spyOn(service, 'transformListFilter').and.callThrough();
//
//                 service.transformFiltersToText();
//
//                 expect(service.transformListFilter).toHaveBeenCalledWith(listFilterItemModel);
//             });
//
//             it('THEN output is represented correctly for filled filter', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
//                 listFilterItemModel.selectedValues = ['A', 'B'];
//                 listFilterItemModel.appliedSelectedValues = ['A', 'B'];
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Subject ID: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('A, B');
//             });
//
//             it('THEN output is represented correctly for empty filter', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
//                 listFilterItemModel.selectedValues = [];
//                 listFilterItemModel.appliedSelectedValues = [];
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Subject ID: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('<no filter set>');
//             });
//         });
//
//         describe('AND check list filter is transformed', () => {
//             it('THEN appropriate method is called', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'studyIdentifier'});
//                 listFilterItemModel.selectedValues = ['A', 'B'];
//                 listFilterItemModel.appliedSelectedValues = ['A', 'B'];
//
//                 spyOn(service, 'transformListFilter').and.callThrough();
//
//                 service.transformFiltersToText();
//
//                 expect(service.transformListFilter).toHaveBeenCalledWith(listFilterItemModel);
//             });
//
//             it('THEN output is represented correctly', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'studyIdentifier'});
//                 listFilterItemModel.selectedValues = ['A', 'B'];
//                 listFilterItemModel.appliedSelectedValues = ['A', 'B'];
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Study ID: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('A, B');
//             });
//
//             it('THEN output is represented correctly', () => {
//
//                 const listFilterItemModel: any = _.find(service.populationFiltersModel.itemsModels, {key: 'studyIdentifier'});
//                 listFilterItemModel.selectedValues = [];
//                 listFilterItemModel.appliedSelectedValues = [];
//
//                 const filtersText = service.transformFiltersToText();
//                 const pattern = 'Study ID: ';
//                 const matchedItemsCountIndex = filtersText.indexOf(pattern);
//                 const endOfTheLine = filtersText.indexOf('\r\n', filtersText.indexOf(pattern) + filterName.length + 1);
//
//                 expect(filtersText.slice(matchedItemsCountIndex + pattern.length, endOfTheLine)).toBe('<no filter set>');
//             });
//         });
//     });
// });
