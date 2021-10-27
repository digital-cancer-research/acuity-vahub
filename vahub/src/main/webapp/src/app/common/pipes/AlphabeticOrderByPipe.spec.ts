import {AlphabeticOrderByPipe} from './AlphabeticOrderByPipe';
import {ILegendEntry, LegendSymbol} from '../trellising/store';

describe('GIVEN IntervalsOrderByPipe', () => {
    let pipe: AlphabeticOrderByPipe;
    beforeEach(() => {
        pipe = new AlphabeticOrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN sorted list of group is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'A',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'C',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'B',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'A',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'B',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'C',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            expect(pipe.transform(listOfLegendEntries, 'GROUP')).toEqual(sortedListOfLegendEntries);
        });

    });


});
