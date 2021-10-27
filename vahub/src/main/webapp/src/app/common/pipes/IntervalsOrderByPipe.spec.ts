import {IntervalsOrderByPipe} from './IntervalsOrderByPipe';
import {ILegendEntry, LegendSymbol} from '../trellising/store/ITrellising';

describe('GIVEN IntervalsOrderByPipe', () => {
    let pipe: IntervalsOrderByPipe;
    beforeEach(() => {
        pipe = new IntervalsOrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN sorted list of weights is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            expect(pipe.transform(listOfLegendEntries, 'WEIGHT')).toEqual(sortedListOfLegendEntries);
        });

        it('THEN sorted list of duration intervals is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            expect(pipe.transform(listOfLegendEntries, 'DURATION_ON_STUDY')).toEqual(sortedListOfLegendEntries);
        });
    });

    describe('WHEN elements are sorted', () => {

        it('THEN list of items is sorted by the given property', () => {
            const listOfLegendEntries: any[] = [
                {
                    title: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: any[] = [
                {
                    title: '1-5',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: '11-12',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: '109-123',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            expect(pipe.transform(listOfLegendEntries, 'WEIGHT', 'title')).toEqual(sortedListOfLegendEntries);
        });
    });

    describe('WHEN elements are numeric range', () => {

        it('THEN the list is sorted', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {label: '.2-40', color: '#CC6677', symbol: 'CIRCLE'},
                {label: '100-100', color: '#332288', symbol: 'CIRCLE'},
                {label: '150-150', color: '#999933', symbol: 'CIRCLE'},
                {label: '200-200', color: '#882255', symbol: 'CIRCLE'},
                {label: '50-75', color: '#44AA99', symbol: 'CIRCLE'}
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {label: '.2-40', color: '#CC6677', symbol: 'CIRCLE'},
                {label: '50-75', color: '#44AA99', symbol: 'CIRCLE'},
                {label: '100-100', color: '#332288', symbol: 'CIRCLE'},
                {label: '150-150', color: '#999933', symbol: 'CIRCLE'},
                {label: '200-200', color: '#882255', symbol: 'CIRCLE'}
            ];

            expect(pipe.transform(listOfLegendEntries, 'DOSE')).toEqual(sortedListOfLegendEntries);
        });
    });
});
