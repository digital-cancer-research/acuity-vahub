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
