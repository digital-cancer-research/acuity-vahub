import {CohortFilterItemModel} from './CohortFilterItemModel';

describe('GIVEN a CohortFilterItemModel class', () => {
    let model: CohortFilterItemModel;

    beforeEach(() => {
        model = new CohortFilterItemModel('cohortKey', 'cohortName');
    });

    describe('WHEN transforming to server object', () => {
        describe('AND there are no subjects', () => {
            it('THEN returns null', () => {
                model.selectedValues = [];
                expect(model.toServerObject()).toBeNull();
            });
        });

        describe('AND there are subjects', () => {
            it('THEN returns the subjects', () => {
                model.selectedValues = ['subj-1'];
                expect(model.toServerObject()).toEqual({values: ['subj-1']});
            });
        });
    });
});
