import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';

import {SimpleLoadingComponent} from './SimpleLoadingComponent';

describe('GIVEN SimpleLoadingComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [SimpleLoadingComponent]
        });
    });

    function initFixture(fixture, loading: boolean): any {
        fixture.componentInstance.loading = loading;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the loader is not visible by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    rootTC.detectChanges();
                    expect(rootTC.componentInstance.loading).toBeFalsy();
                });
            }));

    });

    describe('WHEN loading is changed to TRUE', () => {
        it('THEN the loader is shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    const element = initFixture(rootTC, true);
                    rootTC.detectChanges();
                    expect(element.getElementsByClassName('loader').length).toBeGreaterThan(0);
                });
            }));
    });

    describe('WHEN loading is changed to FALSE', () => {
        it('THEN the loader is shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    const element = initFixture(rootTC, false);
                    rootTC.detectChanges();
                    expect(element.getElementsByClassName('loader').length).toBe(0);
                });
            }));
    });
});
