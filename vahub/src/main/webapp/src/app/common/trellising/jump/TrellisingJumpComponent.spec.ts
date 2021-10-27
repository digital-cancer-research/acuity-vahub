import {TestBed, async} from '@angular/core/testing';
import {TrellisingJumpComponent} from './TrellisingJumpComponent';
import {TrellisingJumpService} from './TrellisingJumpService';
import {CommonModule, Location} from '@angular/common';
import {MockRouter} from '../../../common/MockClasses';
import {Router} from '@angular/router';
import {SpyLocation} from '@angular/common/testing';
import {FormsModule} from '@angular/forms';
import {TrellisingDispatcher} from '../store/dispatcher/TrellisingDispatcher';

describe('GIVEN TrellisingJumpComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: Router, useClass: MockRouter},
                {provide: Location, useClass: SpyLocation},
                {provide: TrellisingJumpService},
                {provide: TrellisingDispatcher},
            ],
            declarations: [TrellisingJumpComponent],
            imports: [
                CommonModule,
                FormsModule
            ]
        });
    });

    describe('WHEN initially constructed', () => {
        it('THEN should have nothing showing', async(() => {
            TestBed.compileComponents().then(() => {
                const rootTC = TestBed.createComponent(TrellisingJumpComponent);
                rootTC.detectChanges();

                expect(rootTC.componentInstance.currentTabLinks.length).toBe(0);

            });
        }));
    });
});
