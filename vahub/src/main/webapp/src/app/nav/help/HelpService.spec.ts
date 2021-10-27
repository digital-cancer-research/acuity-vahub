import {HelpService} from './HelpService';
import {TestBed, inject} from '@angular/core/testing';
import {SpyLocation} from '@angular/common/testing';
import {some} from 'lodash';

describe('GIVEN helpService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [],
            providers: [
                HelpService,
                {provide: Location, useClass: SpyLocation}
            ]
        });
    });

    describe('WHEN starts intro for homepage tab', () => {
        it('THEN should assign right options', () => {
            inject([HelpService],
                (s: HelpService) => {
                    spyOn(location, 'path').and.returnValue('summary-table');

                    s.startIntroHomepage();

                    expect(s.introB.getSteps).toBeDefined();
                });
        });
    });

    describe('WHEN starts intro for tab with table', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('summary-table');

                    s.startIntroAJs();

                    expect(s.introA.getSteps()).toBeDefined();
                    expect(s.introA.getSteps()[4].popover.position).toMatch('top');
                });
        });
    });

    describe('WHEN starts intro for spotfire modules', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('plugins/exposure/spotfire');

                    s.startIntroAJs();

                    expect(s.introA.getSteps()).toBeDefined();
                    expect(s.introA.getSteps().length).toBeLessThanOrEqual(2);
                });
        });
    });

    describe('WHEN starts intro for any page other than population or single subject', () => {
        it('THEN should assign right options', () => {
            inject([HelpService, Location],
                (s: HelpService, location: Location) => {
                    spyOn(location, 'path').and.returnValue('plugins/ae');

                    s.startIntroAJs();

                    expect(s.introA.getSteps).toBeDefined();
                    expect(some(s.introA.getSteps(), s.filters.events)).toBeTruthy();
                });
        });
    });

});
