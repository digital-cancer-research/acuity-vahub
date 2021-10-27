import {TestBed, ComponentFixture, inject} from '@angular/core/testing';

import {fromJS} from 'immutable';
import {FormsModule} from '@angular/forms';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

import {AxisLabelService} from '../AxisLabelService';
import {YAxisLabelComponent} from './YAxisLabelComponent';
import {CommonModule} from '@angular/common';
import {CommonPipesModule} from '../../../pipes/CommonPipes.module';


describe('GIVEN YAxisLabelComponent', () => {

    let component: YAxisLabelComponent;
    let fixture: ComponentFixture<YAxisLabelComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [YAxisLabelComponent],
            imports: [
                CommonModule,
                FormsModule,
                CommonPipesModule,
                NoopAnimationsModule
            ],
            providers: [
                AxisLabelService
            ]
        });

        fixture = TestBed.createComponent(YAxisLabelComponent);
        component = fixture.componentInstance;
        component.options = fromJS(['FIRST', 'SECOND', 'THIRD']);
        component.option = fromJS('FIRST');
    });

    describe('WHEN initialised', () => {
        it('THEN the tab name is set correctly', () => {
            component.ngOnInit();
            fixture.detectChanges();
            expect(fixture.componentInstance.tooltip).toBe('Y-Axis settings');
            expect(fixture.componentInstance.isOpen).toBeFalsy();
            expect(fixture.componentInstance.hasEmitted).toBeFalsy();
        });
    });

    describe('WHEN another axis label is opened', () => {
        beforeEach(() => {
            component.ngOnInit();
        });

        it('AND this component was an emitter ', () => {
            it('THEN the "emmited" flag is changed', () => {

                component.hasEmitted = true;
                (<any>component).axisLabelService.closeOtherAxisLabels();
                expect(component.hasEmitted).toBeFalsy();
            });
        });

        it('AND this component was not an emitter ', () => {
            it('THEN the "emmited" flag is changed', () => {
                spyOn(component, 'closeControl');
                component.hasEmitted = true;
                (<any>component).axisLabelService.closeOtherAxisLabels();
                expect(component.closeControl).toHaveBeenCalled();
            });
        });
    });
});
