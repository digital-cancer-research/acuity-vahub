import {formatDayHourString} from './TimelineUtils';

describe('GIVEN TimelineUtils', () => {

    describe('WHEN formatting day hour string', () => {

        it('THEN day hour at mid night will be one second less ', () => {
            expect(formatDayHourString('12d 00:00', 12, false)).toEqual('11d 23:59');
            expect(formatDayHourString('12d 00:00', 12.01, false)).toEqual('12d 00:00');
            expect(formatDayHourString('-12d 00:00', 12, false)).toEqual('-13d 23:59');
            expect(formatDayHourString('-12d 00:00', 12.01, false)).toEqual('-12d 00:00');
            expect(formatDayHourString('-12d 12:34', 12.55, false)).toEqual('-12d 12:34');
            expect(formatDayHourString(null, null, false)).toBe(null);
        });
    });
});
