import {AEsSummaryMstCmnDataService} from './AEsSummaryMstCmnDataService';

describe('GIVEN AEsSummaryMstCmnDataService', () => {
    let aesDataService: AEsSummaryMstCmnDataService;

    beforeEach(() => {
        aesDataService = new AEsSummaryMstCmnDataService();
    });
    describe('Data validation', () => {
        it('should be invalid when undefined', () => {
            expect(aesDataService.isValidData(undefined)).toBeFalsy();
        });
        it('should be invalid when is empty array', () => {
            expect(aesDataService.isValidData([])).toBeFalsy();
        });
        it('should be valid for non-empty array', () => {
            expect(aesDataService.isValidData([{
                cohortCounts: [],
                rows: [],
                datasetName: '',
                countDosedSubject: 1
            }])).toBeTruthy();
        });
    });


    describe('Value calculation for the non-category cell', () => {
        it('should correctly calculate name for non-empty object',
            () => {
                expect(aesDataService.getPercentValue({
                    cohortCount: {cohort: 'abc', grouping: 'cd', studyPart: 'a', count: 1, groupingType: 'NONE'},
                    value: 2,
                    percentage: 1
                }))
                    .toBe('1.0');
            });

        it('should place 0 instead of null',
            () => {
                expect(aesDataService.getPercentValue({
                    cohortCount: {cohort: 'abc', grouping: 'cd', studyPart: 'a', count: 1, groupingType: 'NONE'},
                    value: null,
                    percentage: 1
                }))
                    .toBe('1.0');
            });
        it('should place - instead of empty string', () => {
            expect(aesDataService.getPercentValue({
                cohortCount: {cohort: 'abc', grouping: 'cd', studyPart: 'a', count: 1, groupingType: 'NONE'},
                value: NaN,
                percentage: 1
            }))
                .toBe('1.0');
        });
    });

    describe('WHEN keys generate for columns', () => {
        describe('AND key consists dots', () => {
            it('SHOULD replace dots with dashes', () => {
                const key = 'A-()B.1.ACQ-Cohort and Module';
                const expectedKey = 'A-()B-1-ACQ-Cohort and Module';

                expect((<any>aesDataService).replaceDotsWithDashes(key)).toBe(expectedKey);
            });
        });

        describe('AND key does not consist dots', () => {
            it('SHOULD return the same key', () => {
                const key = 'A-()B1ACQ-Cohort and Module';

                expect((<any>aesDataService).replaceDotsWithDashes(key)).toBe(key);
            });
        });
    });
});
