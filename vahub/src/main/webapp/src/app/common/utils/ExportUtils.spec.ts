import {ExportUtils} from './ExportUtils';

describe('GIVEN Export utils tests', () => {
    describe('WHEN Convert headers to csv tests', () => {
        it('THEN it should get empty text', () => {
            expect(ExportUtils.convertTreeToCsv([])).toEqual('');
        });

        it('THEN it should get formatted csv', () => {
            expect(ExportUtils.convertTreeToCsv([
                {
                    headerName: 'a',
                    children: [
                        {headerName: 'e'},
                        {headerName: 'h'}
                    ]
                }, {headerName: 'b'}])).toContain('a++++++');
        });
    });
});
