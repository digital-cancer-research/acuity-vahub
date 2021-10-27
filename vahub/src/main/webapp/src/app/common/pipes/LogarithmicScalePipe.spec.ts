import {LogarithmicScalePipe} from './LogarithmicScalePipe';
import {ScaleTypes} from '../trellising/store';

describe('Given LogarithmicScalePipe', () => {
    let pipe: LogarithmicScalePipe;
    beforeEach(() => {
        pipe = new LogarithmicScalePipe();
    });

    it('should not transform with undefined or null scaleType', () => {
        expect(pipe.transform('value', undefined)).toEqual('value');
        expect(pipe.transform('value', null)).toEqual('value');
    });

    it('should not transform with not logarithmic scaleType', () => {
        expect(pipe.transform('value', ScaleTypes.LINEAR_SCALE)).toEqual('Linear scale value');
    });

    it('should transform with logarithmic scaleType', () => {
        expect(pipe.transform('value', ScaleTypes.LOGARITHMIC_SCALE)).toEqual('Log scale value');
    });
});
