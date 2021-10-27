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

import {SpirometryTrackUtils} from './SpirometryTrackUtils';

describe('GIVEN SpirometryTrackUtils', () => {
    describe('WHEN summary track symbol is requested', () => {
        describe('AND percent change is higher than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {maxValuePercentChange: 100};
                const expectedSymbol = {
                    fillColor: SpirometryTrackUtils.MAXIMAL_COLOUR,
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignSummaryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is lower than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {maxValuePercentChange: -100};
                const expectedSymbol = {
                    fillColor: SpirometryTrackUtils.MINIMAL_COLOUR,
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignSummaryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is higher than 0 and lower than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {maxValuePercentChange: 50};
                const expectedSymbol = {
                    fillColor: 'rgb(208, 139, 84)',
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignSummaryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is lower than 0 and higher than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {maxValuePercentChange: -50};
                const expectedSymbol = {
                    fillColor: 'rgb(84, 155, 185)',
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignSummaryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
    });
    describe('WHEN category track symbol is requested', () => {
        describe('AND percent change is higher than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {valuePercentChangeFromBaseline: 100};
                const expectedSymbol = {
                    fillColor: SpirometryTrackUtils.MAXIMAL_COLOUR,
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignCategoryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is lower than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {valuePercentChangeFromBaseline: -100};
                const expectedSymbol = {
                    fillColor: SpirometryTrackUtils.MINIMAL_COLOUR,
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignCategoryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is higher than 0 and lower than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {valuePercentChangeFromBaseline: 50};
                const expectedSymbol = {
                    fillColor: 'rgb(208, 139, 84)',
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignCategoryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND percent change is lower than 0 and higher than 100', () => {
            it('THEN correct symbol and color returned returned according to percent change ', () => {
                const metadata = {valuePercentChangeFromBaseline: -50};
                const expectedSymbol = {
                    fillColor: 'rgb(84, 155, 185)',
                    markerSymbol: SpirometryTrackUtils.SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignCategoryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
        describe('AND measurement has a baseline flag', () => {
            it('THEN baseline symbol is returned', () => {
                const metadata = {valuePercentChangeFromBaseline: -50, baselineFlag: true};
                const expectedSymbol = {
                    fillColor: SpirometryTrackUtils.BASELINE_COLOUR,
                    markerSymbol: SpirometryTrackUtils.BASELINE_SPIROMETRY_SYMBOL
                };
                expect(SpirometryTrackUtils.assignCategoryTrackSymbol(metadata)).toEqual(expectedSymbol);
            });
        });
    });
});
