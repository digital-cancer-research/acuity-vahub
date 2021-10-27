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
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MockTrellising, MockTrellisingObservables} from '../../MockClasses';
import {TrellisingObservables} from '../store/observable/TrellisingObservables';
import {TrellisingLegendComponent} from './TrellisingLegendComponent';
import {LegendSymbol} from '../store';
import {
    LabelPipe,
    IntervalsOrderByPipe,
    MonthsOrderByPipe,
    RemoveParenthesesPipe,
    SentenceCasePipe
} from '../../pipes';
import {ControlComponent} from './control/ControlComponent';
import {Trellising} from '../store/Trellising';
import {AlphabeticOrderByPipe} from '../../pipes/AlphabeticOrderByPipe';

describe('GIVEN TrellisingLegendComponent', () => {
    let component: TrellisingLegendComponent;
    let fixture: ComponentFixture<TrellisingLegendComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            providers: [
                IntervalsOrderByPipe, MonthsOrderByPipe, AlphabeticOrderByPipe,
                {provide: TrellisingObservables, useClass: MockTrellisingObservables},
                {provide: Trellising, useClass: MockTrellising}
            ],
            declarations: [TrellisingLegendComponent, ControlComponent,
                SentenceCasePipe, LabelPipe, RemoveParenthesesPipe, AlphabeticOrderByPipe],
            imports: [
                CommonModule,
                FormsModule
            ]
        });
    }));
    beforeEach(() => {
        fixture = TestBed.createComponent(TrellisingLegendComponent);
        component = fixture.componentInstance;
    });
    describe('WHEN legend is sorted', () => {
        describe('AND sorting is required', () => {
            beforeEach(() => {
                component.legendSortingRequired = true;
            });
            it('THEN legend is sorted correctly for both number and string elements', () => {
                const legendEntries = [
                    {label: '31', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: '2', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '10', symbol: LegendSymbol.CIRCLE, color: 'red'},
                    {label: 'Some label', symbol: LegendSymbol.CIRCLE, color: 'pink'},
                    {label: '(label)', symbol: LegendSymbol.CIRCLE, color: 'yellow'}];
                const sortedEntries = component.sortEntries(legendEntries, 'Some title');
                const expectedEntries = [
                    {label: '(label)', symbol: LegendSymbol.CIRCLE, color: 'yellow'},
                    {label: '10', symbol: LegendSymbol.CIRCLE, color: 'red'},
                    {label: '2', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '31', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: 'Some label', symbol: LegendSymbol.CIRCLE, color: 'pink'}];
                expect(sortedEntries).toEqual(expectedEntries);
            });
            it('THEN legend is sorted correctly for only number elements', () => {
                const legendEntries = [{label: '30', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: '22', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '7', symbol: LegendSymbol.CIRCLE, color: 'red'}];
                const sortedEntries = component.sortEntries(legendEntries, 'Some title');
                const expectedEntries = [
                    {label: '22', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '30', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: '7', symbol: LegendSymbol.CIRCLE, color: 'red'},
                ];
                expect(sortedEntries).toEqual(expectedEntries);
            });
            it('THEN legend is sorted correctly for only string elements', () => {
                const legendEntries = [{label: 'two', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: 'one', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '()', symbol: LegendSymbol.CIRCLE, color: 'red'}];
                const sortedEntries = component.sortEntries(legendEntries, 'Some title');
                const expectedEntries = [{label: '()', symbol: LegendSymbol.CIRCLE, color: 'red'},
                    {label: 'one', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: 'two', symbol: LegendSymbol.CIRCLE, color: 'black'}];
                expect(sortedEntries).toEqual(expectedEntries);
            });
        });
        describe('AND sorting is not required', () => {
            beforeEach(() => {
                component.legendSortingRequired = false;
            });
            it('THEN legend is not sorted and stays the same', () => {
                const legendEntries = [{label: '3', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: '2', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '1', symbol: LegendSymbol.CIRCLE, color: 'red'},
                    {label: 'Some label', symbol: LegendSymbol.CIRCLE, color: 'pink'},
                    {label: '(label)', symbol: LegendSymbol.CIRCLE, color: 'yellow'}];
                const sortedEntries = component.sortEntries(legendEntries, 'Some title');
                const expectedEntries = [{label: '3', symbol: LegendSymbol.CIRCLE, color: 'black'},
                    {label: '2', symbol: LegendSymbol.CIRCLE, color: 'blue'},
                    {label: '1', symbol: LegendSymbol.CIRCLE, color: 'red'},
                    {label: 'Some label', symbol: LegendSymbol.CIRCLE, color: 'pink'},
                    {label: '(label)', symbol: LegendSymbol.CIRCLE, color: 'yellow'}];
                expect(sortedEntries).toEqual(expectedEntries);
            });
        });
    });
});
