import {DetailsOnDemandSummaryService} from './DetailsOnDemandSummaryService';
import {ISelectionDetail} from '../../store/ITrellising';

describe('GIVEN DetailsOnDemandSummaryService', () => {

    let service: DetailsOnDemandSummaryService;

    beforeEach(() => {
        service = new DetailsOnDemandSummaryService();
    });

    describe('WHEN all subjects are selected', () => {
        it('THEN the correct summary is returned', () => {

            const selection: ISelectionDetail = {
                eventIds: [],
                subjectIds: ['subj-1', 'subj-2', 'subj-3', 'subj-4', 'subj-5'],
                totalSubjects: 5,
                totalEvents: 0
            };

            expect(service.getSubjectSummary(selection)).toBe('(All) 5 of 5');
        });
    });

    describe('WHEN some subjects are selected', () => {
        it('THEN the correct summary is returned', () => {

            const selection: ISelectionDetail = {
                eventIds: [],
                subjectIds: ['subj-1'],
                totalSubjects: 5,
                totalEvents: 0
            };

            expect(service.getSubjectSummary(selection)).toBe('1 of 5');
        });
    });

    describe('WHEN all events are selected', () => {
        it('THEN the correct summary is returned', () => {

            const selection: ISelectionDetail = {
                eventIds: ['ev-1', 'ev-2', 'ev-3', 'ev-4', 'ev-5'],
                subjectIds: [],
                totalSubjects: 0,
                totalEvents: 5
            };

            expect(service.getEventSummary(selection)).toBe('(All) 5 of 5');
        });
    });

    describe('WHEN some events are selected', () => {
        it('THEN the correct summary is returned', () => {

            const selection: ISelectionDetail = {
                eventIds: ['ev-1'],
                subjectIds: [],
                totalSubjects: 0,
                totalEvents: 5
            };

            expect(service.getEventSummary(selection)).toBe('1 of 5');
        });
    });
});
