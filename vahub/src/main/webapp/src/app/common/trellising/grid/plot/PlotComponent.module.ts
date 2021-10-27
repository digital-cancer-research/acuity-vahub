import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { PlotComponent } from './PlotComponent';
import {BoxPlotComponent} from './plottypes/boxplot/BoxPlotComponent';
import {StackedBarPlotComponent} from './plottypes/stackedbarplot/StackedBarPlotComponent';
import {RangePlotComponent} from './plottypes/rangeplot/RangePlotComponent';
import {ScatterPlotComponent} from './plottypes/scatterplot/ScatterPlotComponent';
import {ErrorPlotComponent} from './plottypes/errorplot/ErrorPlotComponent';
import {MarkingComponent} from './markingdialogue/index';
import {AxisLabelService} from './services/AxisLabelService';
import {TitleService} from './services/TitleService';
import { BarLinePlotComponent } from './plottypes/barlineplot/BarLinePlotComponent';
import {LinePlotComponent} from './plottypes/lineplot/LinePlotComponent';
import { GroupedBarPlotComponent } from './plottypes/groupedbarplot/GroupedBarPlotComponent';
import {JoinedRangePlotComponent} from './plottypes/joinedrangeplot/JoinedRangePlotComponent';
import {SimpleLinePlotComponent} from './plottypes/simple-line-plot/SimpleLinePlotComponent';
import {HeatMapPlotComponent} from './plottypes/heatmap/HeatMapPlotComponent';
import {CommonPipesModule} from '../../../pipes/CommonPipes.module';
import {WaterfallPlotComponent} from './plottypes/waterfallplot/WaterfallPlotComponent';
import {ColumnRangePlotComponent} from './plottypes/columnrange-plot/ColumnRangePlotComponent';
import {PlotService} from './PlotService';
import {ModalMessageComponentModule} from '../../../modals/modalMessage/ModalMessageComponent.module';
import {DisplayLimitationMessageComponentModule} from '../../../modals/displayLimitationMessage/DisplayLimitationMessageComponent.module';
import {ChordDiagramComponent} from './plottypes/chord-diagram/ChordDiagramComponent';

@NgModule({
    imports: [CommonModule, FormsModule, CommonPipesModule, ModalMessageComponentModule, DisplayLimitationMessageComponentModule],
    exports: [PlotComponent],
    declarations: [
        PlotComponent,
        BoxPlotComponent,
        StackedBarPlotComponent,
        ErrorPlotComponent,
        RangePlotComponent,
        ScatterPlotComponent,
        MarkingComponent,
        BarLinePlotComponent,
        LinePlotComponent,
        GroupedBarPlotComponent,
        JoinedRangePlotComponent,
        SimpleLinePlotComponent,
        HeatMapPlotComponent,
        WaterfallPlotComponent,
        ColumnRangePlotComponent,
        ChordDiagramComponent
    ],
   providers: [AxisLabelService, TitleService, CommonPipesModule, PlotService]
})
export class PlotComponentModule { }
