import * as fromDetailsOnDemand from './DetailsOnDemandReducer';
import {Actions, UpdateColumnsAction} from '../actions/DetailsOnDemandActions';
import {Map, Set, is} from 'immutable';

export function main(): void {
    describe('Details On Demand reducer', () => {
        const initialState = fromDetailsOnDemand.initialState;
        const aesColumnNames = [
            'studyId',
            'studyPart',
            'subjectId',
            'preferredTerm',
            'highLevelTerm',
            'systemOrganClass',
            'maxSeverity',
            'startDate',
            'endDate',
            'daysOnStudyAtAEStart',
            'daysOnStudyAtAEEnd',
            'duration',
            'serious',
            'actionTaken',
            'causality',
            'description',
            'outcome',
            'specialInterestGroup'
        ];

        beforeEach(() => {
            // register immutable matchers
        });

        describe('WHEN called with unknown action', () => {
            it('SHOULD return initial state', () => {
                const result = fromDetailsOnDemand.reducer(initialState, {} as Actions);

                expect(is(result, initialState)).toBeTruthy();
            });
        });

        describe('WHEN updating columns', () => {
            it('SHOULD set appropriate columns', () => {
                const expectedResult = Map({
                    columns: Map({
                        aes: Set.of(...aesColumnNames)
                    })
                });

                const result = fromDetailsOnDemand.reducer(initialState, new UpdateColumnsAction({
                    columns: Map({
                        aes: Set.of(...aesColumnNames)
                    })
                }));

                expect(is(expectedResult, result)).toBeTruthy();
            });
        });
    });
}
