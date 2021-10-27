import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';

import {MockTrellisingDispatcher, MockTrellisingObservables} from '../../../../common/MockClasses';
import {RadioButtonsSettingsComponent} from '../../radioButtons/RadioButtonsSettingsComponent';
import {CapitalizePipe, SentenceCasePipe, SettingsPipe} from '../../../../common/pipes';
import {DoseProportionalitySettingsComponent} from './DoseProportionalitySettingsComponent';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {ScaleTypes} from '../../../../common/trellising/store';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            providers: [
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {provide: TrellisingObservables, useClass: MockTrellisingObservables},
            ],
            declarations: [DoseProportionalitySettingsComponent, RadioButtonsSettingsComponent, CapitalizePipe,
                SentenceCasePipe, SettingsPipe]
        });
    });

    describe('WHEN scaling selection setting is applied', () => {
        it('THEN scale type is updated correctly',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(DoseProportionalitySettingsComponent);
                    spyOn(component.componentInstance.trellisingDispatcher, 'updateScale');
                    component.componentInstance.setSelectedScale({selectedValue: ScaleTypes.LOGARITHMIC_SCALE});
                    component.componentInstance.apply();
                    expect(component.componentInstance.trellisingDispatcher.updateScale).toHaveBeenCalledWith(ScaleTypes.LOGARITHMIC_SCALE);
                });
            }));
    });

});
