import {async, TestBed} from '@angular/core/testing';
import {Map} from 'immutable';
import {SentenceCasePipe} from '../../../common/pipes';
import {ErrorBarsType} from '../../../common/trellising/store';

import {ListSelectionComponent} from './ListSelectionComponent';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [ListSelectionComponent, SentenceCasePipe]
        });
    });

    describe('WHEN new option is selected in the list', () => {
        it('THEN event should be emitted',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(ListSelectionComponent);
                    component.componentInstance.elements = Map(Object.keys(ErrorBarsType).map(e => ([e, false])));
                    component.componentInstance.elements['STANDARD_DEVIATION'] = true;
                    spyOn(component.componentInstance.onSelect, 'emit');
                    component.componentInstance.selectElement('STANDARD_DEVIATION');
                    expect(component.componentInstance.onSelect.emit).toHaveBeenCalledWith({
                        name: 'STANDARD_DEVIATION',
                        selected: true
                    });
                });
            }));
    });
});
