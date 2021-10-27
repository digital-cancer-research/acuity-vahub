import {MonthsOrderByPipe} from './MonthsOrderByPipe';
import {ILegendEntry, LegendSymbol} from '../trellising/store/ITrellising';

describe('GIVEN MonthsOrderByPipe', () => {
    let pipe: MonthsOrderByPipe;
    beforeEach(() => {
        pipe = new MonthsOrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN sorted list of months is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
            ];

            expect(pipe.transform(listOfLegendEntries, 'FIRST_TREATMENT_DATE')).toEqual(sortedListOfLegendEntries);
        });
    });

    describe('WHEN elements are sorted', () => {

        it('THEN list of items is sorted by the given property', () => {
            const listOfLegendEntries: any[] = [
                {
                    title: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: any[] = [
                {
                    title: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
            ];

            expect(pipe.transform(listOfLegendEntries, 'FIRST_TREATMENT_DATE', 'title')).toEqual(sortedListOfLegendEntries);
        });
    });
});
