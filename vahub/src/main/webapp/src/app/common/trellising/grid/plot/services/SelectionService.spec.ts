import {SelectionService} from './SelectionService';

describe('GIVEN SelectionService class', () => {
    describe('WHEN altering x selection for categorical overlapping 1 bin', () => {
        it('THEN expands to fill the overlapped bin', () => {
            const result = SelectionService.alterCategoricalSelectionX(-0.3, 0.3);
            expect(result).toEqual({xMin: -0.49, xMax: 0.49});
        });
    });
    describe('WHEN altering x selection for categorical overlapping 2 bins', () => {
        it('THEN expands to fill the overlapped bins (case 1)', () => {
            const result = SelectionService.alterCategoricalSelectionX(-0.3, 0.6);
            expect(result).toEqual({xMin: -0.49, xMax: 1.49});
        });

        it('THEN expands to fill the overlapped bins (case 2)', () => {
            const result = SelectionService.alterCategoricalSelectionX(0.4, 0.6);
            expect(result).toEqual({xMin: -0.49, xMax: 1.49});
        });

        it('THEN expands to fill the overlapped bins (case 3)', () => {
            const result = SelectionService.alterCategoricalSelectionX(-0.3, 1.1);
            expect(result).toEqual({xMin: -0.49, xMax: 1.49});
        });
    });
    describe('WHEN getting all selection from series', () => {
        let series: any[];
        beforeEach(() => {
            series = [
                {
                    dataMin: 100,
                    dataMax: 120,
                    data: [
                        {x: 0},
                        {x: 2}
                    ]
                },
                {
                    dataMin: 90,
                    dataMax: 110,
                    data: [
                        {x: 3},
                        {x: 4}
                    ]
                }
            ];
        });

        it('THEN gets y range from dataMin and dataMax +-5%', () => {
            const result = SelectionService.getAllSelection(series).range;
            expect(result.yMin).toEqual(89.85);
            expect(result.yMax).toEqual(120.15);
        });

        it('THEN gets x range from datas +-5%', () => {
            const result = SelectionService.getAllSelection(series).range;
            expect(result.xMin).toEqual(-0.02);
            expect(result.xMax).toEqual(4.02);
        });
    });

});
