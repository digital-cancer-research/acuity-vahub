import {MarkingUtils} from './MarkingUtils';
import {ISelectionDetail} from '../ITrellising';

describe('GIVEN MarkingUtils class', () => {
    describe('WHEN merging 1 study detail', () => {
        let details: ISelectionDetail[];
        beforeEach(() => {
            details = [{
                eventIds: ['1', '2'],
                subjectIds: ['DummyData-1', 'DummyData-2'],
                totalSubjects: 197,
                totalEvents: 10000
            }];
        });
        it('THEN returns the 1 study detail', () => {
            expect(MarkingUtils.merge(details)).toEqual(details[0]);
        });
    });

    describe('WHEN merging 2 study details', () => {
        let details: ISelectionDetail[];
        beforeEach(() => {
            details = [{
                eventIds: ['1', '2'],
                subjectIds: ['DummyData-1', 'DummyData-2'],
                totalSubjects: 197,
                totalEvents: 10000
            },
                {
                    eventIds: ['1', '3'],
                    subjectIds: ['DummyData-3', 'DummyData-1'],
                    totalSubjects: 197,
                    totalEvents: 10000
                }];
        });
        it('THEN returns the 1 study detail', () => {
            const expected: ISelectionDetail = {
                eventIds: ['1', '2', '3'],
                subjectIds: ['DummyData-1', 'DummyData-2', 'DummyData-3'],
                totalSubjects: 197,
                totalEvents: 10000
            };
            expect(MarkingUtils.merge(details)).toEqual(expected);
        });
    });
});
