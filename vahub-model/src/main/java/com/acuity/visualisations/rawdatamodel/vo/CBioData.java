package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/*
   This VO presents data required for cBioPortal to build the URL to go to correspondent Genomic Profile.
   It would be more logic to have this object per study, but it's impossible as data order matters.
 */
@Getter
@AllArgsConstructor
public class CBioData implements Serializable {

    private Map<String, String> cbioPortalDatasetCodes;
    /**
     * The {@code profiles} is set of pairs where key is a profile name with prefix specific to cBio and value is a list of profiles ids.
     * For instance, {genetic_profile_ids_PROFILE_MUTATION_EXTENDED: [coadread_tcga_mutations, biscay_int_mutations]}
     * where 'genetic_profile_ids_PROFILE_' - is the prefix, 'MUTATION_EXTENDED" - is profile group name,
     * 'coadread_tcga' and 'biscay_int' are studies ids and 'mutations' is postfix specific to the 'MUTATION_EXTENDED' group
     */
    private Map<String, List<String>> profiles;
    private List<Map<String, String>> data;

}
