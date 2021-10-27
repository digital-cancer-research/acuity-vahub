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

import {ScaleTypes} from './types/types';

export class Axis {
    public title = '';
    public type = 'linear';
    public categories = [];
    public filteredCategories = [];
    public onZoom: (interval: number[]) => void;

    public constructor(
        onZoom: (borders: number[]) => void = () => {},
    ) {
        this.onZoom = onZoom;
    }

    public setCategories(categories: string[]) {
        this.categories = categories;
    }

    public getFilteredCategories(limits: number[]): string[] {
        return this.categories.slice(limits[0], limits[1] + 1);
    }

    public setExtremes(zoomMin: number, zoomMax: number) {
        this.onZoom([zoomMin, zoomMax]);
    }

    public setTitle(title: {text: string}) {
        this.title = title.text;
    }

    public setType(type: string) {
        this.type = type;
    }

    public get isLinear() {
        return this.type === ScaleTypes.LINEAR_SCALE;
    }

    public get isLogarithmic() {
        return this.type === ScaleTypes.LOGARITHMIC_SCALE;
    }

    public get isCategorical() {
        return this.type === ScaleTypes.CATEGORY_SCALE;
    }

    public get isDatetime() {
        return this.type === ScaleTypes.DATETIME_SCALE;
    }
}
