import {inject, TestBed} from '@angular/core/testing';
import {ScatterPlotService} from './ScatterPlotService';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

describe('GIVEN ScatterPlotService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                ScatterPlotService
            ]
        });
    });

    describe('WHEN transforming server data', () => {

        let data: OutputScatterPlotEntry[];
        beforeEach(() => {
            data = [{
                color: 'red',
                name: '123',
                x: 0,
                y: 1,
                measurementValue: null
            },
                {
                    color: 'red',
                    name: '124',
                    x: 1,
                    y: 1,
                    measurementValue: null
                },
                {
                    color: 'green',
                    name: '128',
                    x: 2,
                    y: 3,
                    measurementValue: null
                }];
        });


        it('THEN flattens data to separate arrays',
            inject([ScatterPlotService], (s: ScatterPlotService) => {
                const result = s.reformatServerData(data);
                expect(result.length).toBe(2);
                expect(result[0].color).toBe('red');
                expect(result[0].data).toEqual([[0, 1], [1, 1]]);
                expect(result[1].data).toEqual([[2, 3]]);
            })
        );
    });

});
