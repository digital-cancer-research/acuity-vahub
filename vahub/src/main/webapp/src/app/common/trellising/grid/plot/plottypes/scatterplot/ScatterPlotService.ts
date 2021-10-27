import { Injectable } from '@angular/core';
import * as  _ from 'lodash';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

export interface ScatterData {
    color: string;
    data: Array<number>;
}

@Injectable()
export class ScatterPlotService {
    static getCategoricalBin(x: number): { min: number, max: number } {
        const nearest = Math.round(x);
        return { min: nearest - 0.49, max: nearest + 0.49 };
    }

    public reformatServerData(data: OutputScatterPlotEntry[]): ScatterData[] {
        const reducedData = data.map(x => ({ color: x.color, data: [x.x, x.y] }));
        const groupedData = _.groupBy(reducedData, x => x.color);
        return _.map(groupedData, (value, key) => {
            return {
                color: key,
                data: value.reduce((a, b) => { a.push(b.data); return a; }, [])
            };
        });
    }
}
