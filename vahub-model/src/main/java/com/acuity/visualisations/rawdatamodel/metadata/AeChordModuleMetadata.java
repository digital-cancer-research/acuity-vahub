package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService.MAX_TIME_FRAME_AE_CHORDS;
import static com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService.TIME_FRAME;

@Service
public class AeChordModuleMetadata extends AbstractModuleColoringMetadata {

    private static final String AE_CHORD_DIAGRAM_KEY = "ae-chord";

    @Autowired
    private AeChordDiagramService aeChordDiagramService;

    protected String tab() {
        return AE_CHORD_DIAGRAM_KEY;
    }

    /**
     * Only details-on-demand related metadata is used from this class for the AEs Chord Diagram.
     * All other metadata is common with aes, see {@link AeModuleMetadata}
     */
    @Override
    protected MetadataItem buildAllMetadataItems(MetadataItem metadataItem, Datasets datasets) {
        // it is necessary to get metadata of the chord plot with the setting 'Number of days between AEs to constitute a link'
        // set as max(30 days) so dods could have all possible columns
        HashMap<String, String> settingsWithMaxTimeFrame = new HashMap<>();
        settingsWithMaxTimeFrame.put(TIME_FRAME, MAX_TIME_FRAME_AE_CHORDS.toString());

        final Map<String, String> doDColumns = aeChordDiagramService.getDoDColumns(datasets, settingsWithMaxTimeFrame);
        metadataItem.add("detailsOnDemandColumns", doDColumns.keySet());
        metadataItem.add("detailsOnDemandTitledColumns", doDColumns);
        //calculating of chord colors while dataset is loading
        return metadataItem;
    }

    @Override
    ColorInitializer getColorInitializer() {
        return aeChordDiagramService;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

}
