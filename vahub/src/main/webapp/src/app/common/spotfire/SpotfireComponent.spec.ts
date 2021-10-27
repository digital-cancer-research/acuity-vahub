import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SpotfireComponent} from './SpotfireComponent';
import {MockStudyService} from '../MockClasses';
import {StudyService} from '../module';

describe('GIVEN SpotfireComponent', () => {
    beforeEach(() => {

        TestBed.configureTestingModule({
            imports: [FormsModule, CommonModule],
            declarations: [SpotfireComponent],
            providers: [
                {provide: StudyService, useValue: new MockStudyService()}
            ]
        });
    });

    function initFixture(fixture, moduletype: string): any {
        fixture.componentInstance.moduletype = moduletype;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the correct number of oncology modules should be shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SpotfireComponent);
                    const element = rootTC.nativeElement;
                    initFixture(rootTC, 'Oncology');

                    rootTC.detectChanges();
                    expect(element.querySelectorAll('.vis').length).toEqual(2);
                });
            }));

        it('THEN the correct number of respiratory modules should be shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SpotfireComponent);
                    const element = rootTC.nativeElement;
                    initFixture(rootTC, 'Respiratory');

                    rootTC.detectChanges();
                    expect(element.querySelectorAll('.vis').length).toEqual(1);
                });
            }));
    });
});
