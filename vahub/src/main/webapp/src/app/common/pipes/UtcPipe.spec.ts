import {UtcPipe} from './UtcPipe';
import {DateUtilsService} from '../utils/DateUtilsService';

describe('Given UtcPipe', () => {
    let pipe: UtcPipe;
    beforeEach(() => {
        pipe = new UtcPipe(new DateUtilsService());
    });

    it('transforms timestamp correctly', () => {
        expect(pipe.transform('2014-04-22T00:00:00')).toEqual('22-Apr-2014 00:00:00');
    });

    it('transforms timestamp without time correctly', () => {
        expect(pipe.transform('2014-04-22T00:00:00', true)).toEqual('22-Apr-2014');
    });

    it('transforms empty timestamp correctly', () => {
        expect(pipe.transform(undefined)).toEqual('--');
    });
});
