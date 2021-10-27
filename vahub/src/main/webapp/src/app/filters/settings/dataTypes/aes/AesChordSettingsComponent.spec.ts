import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {Map} from 'immutable';

import {MockTrellising, MockTrellisingDispatcher, MockTrellisingObservables} from '../../../../common/MockClasses';
import {CapitalizePipe, SentenceCasePipe, SettingsPipe} from '../../../../common/pipes';
import {TermLevelType} from '../../../../common/trellising/store';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {Trellising} from '../../../../common/trellising/store/Trellising';
import {RadioButtonsSettingsComponent} from '../../radioButtons/RadioButtonsSettingsComponent';
import {AesChordSettingsComponent} from './AesChordSettingsComponent';
import {RangeSettingsComponent} from '../../range/RangeSettingsComponent';
import {CommonDirectivesModule} from '../../../../common/directives/directives.module';
import {PlotSettings} from '../../../../common/trellising/store/ITrellising';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule, CommonDirectivesModule],
            providers: [
                {provide: Trellising, useClass: MockTrellising},
                {provide: TrellisingObservables, useClass: MockTrellisingObservables},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher}
            ],
            declarations: [AesChordSettingsComponent, RadioButtonsSettingsComponent,
                CapitalizePipe, SentenceCasePipe, SettingsPipe, RangeSettingsComponent]
        });
    });
    describe('WHEN term level selection setting is applied', () => {
        it('THEN term level is updated correctly',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(AesChordSettingsComponent);
                    spyOn(component.componentInstance.trellisingMiddleware, 'updatePlotSettings');

                    const currentPlotSettings = Map({
                        trellisedBy: 'ANY',
                        trellisOptions: Map({'timeFrame': 0})
                    }) as PlotSettings;
                    component.componentInstance.plotSettings = currentPlotSettings;
                    const appliedTermLevel = TermLevelType.HLT;

                    component.componentInstance.setTermLevel({
                        currentOption: currentPlotSettings,
                        selectedValue: appliedTermLevel
                    });
                    component.componentInstance.setDaysNumberBetweenAEs(15);
                    component.componentInstance.apply();

                    expect(component.componentInstance.trellisingMiddleware.updatePlotSettings).toHaveBeenCalled();
                    expect(component.componentInstance.plotSettings.get('trellisedBy')).toEqual(TermLevelType.HLT);
                    expect(component.componentInstance.plotSettings.getIn(['trellisOptions', 'timeFrame'])).toEqual(15);
                });
            }));
    });
    describe('WHEN visible links setting is applied', () => {
        it('THEN chord plot is updated correctly',
            async(() => {
                const component = TestBed.createComponent(AesChordSettingsComponent);
                spyOn(component.componentInstance.trellisingMiddleware, 'updatePlotSettings');

                component.componentInstance.plotSettings = Map({
                    trellisedBy: 'ANY',
                    trellisOptions: Map({'percentageOfLinks': 10})
                }) as PlotSettings;

                component.componentInstance.setNumberOfLinks(15);
                component.componentInstance.apply();

                expect(component.componentInstance.trellisingMiddleware.updatePlotSettings).toHaveBeenCalled();
                expect(component.componentInstance.plotSettings.getIn(['trellisOptions', 'percentageOfLinks'])).toEqual(15);
            }));
    });
    describe('WHEN term level selection setting is applied', () => {
        it('THEN term level is updated correctly',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(AesChordSettingsComponent);
                    spyOn(component.componentInstance.trellisingMiddleware, 'updatePlotSettings');

                    const currentPlotSettings = Map({
                        trellisedBy: 'ANY',
                        trellisOptions: Map([['timeFrame', 0], ['percentageOfLinks', 10]])
                    }) as PlotSettings;
                    component.componentInstance.plotSettings = currentPlotSettings;
                    const appliedTermLevel = TermLevelType.HLT;

                    component.componentInstance.setTermLevel({
                        currentOption: currentPlotSettings,
                        selectedValue: appliedTermLevel
                    });

                    component.componentInstance.setNumberOfLinks(20);
                    component.componentInstance.setDaysNumberBetweenAEs(15);
                    component.componentInstance.apply();

                    expect(component.componentInstance.trellisingMiddleware.updatePlotSettings).toHaveBeenCalled();
                    expect(component.componentInstance.plotSettings.get('trellisedBy')).toEqual(TermLevelType.HLT);
                    expect(component.componentInstance.plotSettings.getIn(['trellisOptions', 'timeFrame'])).toEqual(15);
                    expect(component.componentInstance.plotSettings.getIn(['trellisOptions', 'percentageOfLinks'])).toEqual(20);
                });
            }));
    });
});
