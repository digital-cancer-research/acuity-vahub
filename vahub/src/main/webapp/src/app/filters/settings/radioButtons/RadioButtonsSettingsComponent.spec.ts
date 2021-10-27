import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';


import {RadioButtonsSettingsComponent} from './RadioButtonsSettingsComponent';
import {CapitalizePipe, SentenceCasePipe, SettingsPipe} from '../../../common/pipes';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [RadioButtonsSettingsComponent, SentenceCasePipe, CapitalizePipe, SettingsPipe]
        });
    });

    describe('WHEN new option is selected', () => {
        it('THEN event should be emitted',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(RadioButtonsSettingsComponent);
                    component.componentInstance.currentOption = 'A';
                    component.componentInstance.selectedValue = 'B';
                    spyOn(component.componentInstance.onChange, 'emit');
                    component.componentInstance.changeHandler();
                    expect(component.componentInstance.onChange.emit).toHaveBeenCalledWith({currentOption: 'A', selectedValue: 'B'});
                });
            }));
    });

});
