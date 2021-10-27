/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.ssv;

import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService.SsvTableMetadata;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


/**
 * Service responsible for document creation.
 * the `docx4j` library is used under the hood.
 */
@Service
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@Slf4j
public class PatientSummaryDocumentService {
    //Document template
    private Resource templateResource;
    //Template with styles: used for convenience and are cached while init
    private Resource styleTemplateResource;

    @Autowired
    private SingleSubjectViewSummaryService summaryService;
    @Autowired
    private PopulationService populationService;

    @Autowired
    public PatientSummaryDocumentService(
            @Value(value = "classpath:template/patient_summary_template.docx") Resource templateResource,
            @Value(value = "classpath:template/patient_summary_style.docx") Resource styleTemplateResource) {
        this.templateResource = templateResource;
        this.styleTemplateResource = styleTemplateResource;
    }

    //cached styles
    private P section;
    //cached predefined tables
    private Tbl sampleTable;
    private Tbl headerSampleTable;
    private RPr smallRPr;
    private RPr smallHeaderRPr;
    private P normal;
    private P smallHight;
    private P lineBreak;

    @PostConstruct
    void initCtx() throws JAXBException, IOException, Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(styleTemplateResource.getInputStream());
        MainDocumentPart styleDocumentPart = wordMLPackage.getMainDocumentPart();
        final List<Object> content = styleDocumentPart.getContent();
        section = (P) content.get(0);
        section.getPPr().setKeepNext(new BooleanDefaultTrue());

        lineBreak = (P) content.get(8);
        sampleTable = (Tbl) XmlUtils.unwrap(content.get(4));
        normal = (P) content.get(10);
        headerSampleTable = (Tbl) XmlUtils.unwrap(content.get(11));
        P smallHightHeader = (P) content.get(12);
        smallHeaderRPr = ((R) smallHightHeader.getContent().get(0)).getRPr();
        smallHight = (P) content.get(13);
        smallRPr = ((R) smallHight.getContent().get(0)).getRPr();
    }

    @Cacheable
    public Optional<ByteArrayOutputStream> generateDocument(Datasets datasets, String subjectId, boolean hasTumourAccess, String timeZoneOffset)
            throws JAXBException, Docx4JException, IOException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateResource.getInputStream());
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        String timestamp = formatDateTime(System.currentTimeMillis(), timeZoneOffset, "MM/dd/yyyy h:mm:ss a");
        HashMap<String, String> footerData = new LinkedHashMap<>();
        footerData.put("timestamp", timestamp);

        Optional<Subject> subject = populationService.getSubject(datasets, subjectId);
        if (!subject.isPresent()) {
            return Optional.empty();
        }

        List<Map<String, String>> headerTableData = summaryService.getHeaderDataForPrinting(subject.get());
        createHeaderPart(wordMLPackage, headerTableData);
        processFooterTemplates(wordMLPackage, footerData);

        Map<String, SsvTableMetadata> metadataByTable = summaryService.getMetadata(datasets, hasTumourAccess).stream()
                .collect(toMap(SsvTableMetadata::getName, m -> m, (o1, o2) -> o1, LinkedHashMap::new));

        Map<String, List<Map<String, String>>> tables = summaryService.getData(datasets, subjectId, hasTumourAccess);

        // transform input map of tables so that columns were sorted and column names were display names instead of camelCase names
        Map<String, List<Map<String, String>>> tablesWithSortedNamedColumns = tables.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        table -> {
                            if (table.getValue().isEmpty()) {
                                // table with one empty row
                                return Collections.singletonList(metadataByTable.get(table.getKey()).getColumns().values()
                                        .stream().collect(toMap(col -> col, col -> "", (o1, o2) -> o1, LinkedHashMap::new)));
                            } else {
                                return table.getValue().stream()
                                        .map(row -> metadataByTable.get(table.getKey()).getColumns().entrySet().stream()
                                                .collect(toMap(Map.Entry::getValue,
                                                        col -> row.get(col.getKey()) == null ? "" : row.get(col.getKey()), (o1, o2) -> o1, LinkedHashMap::new)))
                                        .collect(toList());
                            }
                        }));

        //insert tables
        metadataByTable.values().forEach(metadata -> {
                    try {
                        insertTable(documentPart, metadata.getDisplayName(), tablesWithSortedNamedColumns.get(metadata.getName()));
                    } catch (JAXBException | XPathBinderAssociationIsPartialException ignored) {
                        log.error("Ignored", ignored);
                    }
                });

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Docx4J.save(wordMLPackage, outputStream);
        return Optional.of(outputStream);
    }

    private String formatDateTime(long time, String timeZoneOffset, String dateFormat) {
        String checkedTzo = timeZoneOffset.startsWith("+") ? timeZoneOffset
                : timeZoneOffset.startsWith("-") ? timeZoneOffset
                : "+" + timeZoneOffset;
        return ZonedDateTime.of(
                LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.of(checkedTzo)),
                ZoneOffset.of(checkedTzo)
        ).format(DateTimeFormatter.ofPattern(dateFormat));
    }

    private void processFooterTemplates(WordprocessingMLPackage template, HashMap<String, String> variables) throws JAXBException, Docx4JException {
        RelationshipsPart relationshipPart = template.getMainDocumentPart().getRelationshipsPart();
        List<Relationship> relationships = relationshipPart.getJaxbElement().getRelationship();
        for (Relationship r : relationships) {
            JaxbXmlPart part = (JaxbXmlPart) relationshipPart.getPart(r);
            if (part instanceof FooterPart) {
                List<Object> texts = getAllElementFromObject(part.getContents(), Text.class);
                texts.forEach(textObject -> {
                    Text text = (Text) textObject;
                    variables.entrySet().forEach(entry -> {
                        String placeholder = "{" + entry.getKey() + "}";
                        text.setValue(text.getValue().replace(placeholder, entry.getValue()));
                    });
                });
            }
        }
    }

    private P createParagraph(Object parent, P prototype, String text) {
        P p = XmlUtils.deepCopy(prototype == null ? normal : prototype);
        p.setParent(parent);
        // replace the text elements from the copy
        List<?> texts = getAllElementFromObject(p, Text.class);
        if (!texts.isEmpty()) {
            Text textToReplace = (Text) texts.get(0);
            textToReplace.setValue(text);
        }
        return p;
    }

    private void insertTable(MainDocumentPart documentPart, String tableName, List<Map<String, String>> tableData)
            throws JAXBException, XPathBinderAssociationIsPartialException {
        documentPart.addObject(createParagraph(documentPart, section, tableName));
        documentPart.addObject(getTable(sampleTable, tableData, true));
        documentPart.addObject(lineBreak);
    }

    private List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }

        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }

        }
        return result;
    }

    private Tbl getTable(Tbl sampleTable, List<Map<String, String>> data, boolean hasHeaderRow) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        int rowCount = hasHeaderRow ? (data.size() + 1) : data.size();
        Tbl table = TblFactory.createTable(rowCount, data.get(0).size(), 300 /*inherited from sampleHeaderRow*/);
        table.setTblPr(sampleTable.getTblPr());
        table.setTblGrid(sampleTable.getTblGrid());

        final Tr sampleTableRow = (Tr) sampleTable.getContent().get(0);
        Tc tableCell = (Tc) XmlUtils.unwrap(sampleTableRow.getContent().get(0));

        int i = 0;
        if (hasHeaderRow) {
            Tr sampleHeaderRow = (Tr) sampleTable.getContent().get(1);
            Tc sampleHeaderCell = (Tc) XmlUtils.unwrap(sampleHeaderRow.getContent().get(0));
            PPr headerParagraphProperties = withAlignmentAndKeepNext(new PPr(), JcEnumeration.CENTER);
            addRow(factory, table, sampleHeaderRow, headerParagraphProperties, smallHeaderRPr, sampleHeaderCell, data.get(0).keySet(), i);
            i++;
        }

        final PPr tableParagraphProperties = withAlignmentAndKeepNext(smallHight.getPPr(), JcEnumeration.LEFT);
        for (Map<String, String> entry : data) {
            addRow(factory, table, sampleTableRow, tableParagraphProperties, smallRPr, tableCell, entry.values(), i);
            i++;
        }
        return table;
    }


    private void addRow(ObjectFactory factory, Tbl table, Tr sampleTableRow, PPr paragraphProperties, RPr runProperties,
                        Tc tableCell, Collection<String> values, int i) {

        Tr row = (Tr) table.getContent().get(i);
        row.setTrPr(sampleTableRow.getTrPr());

        int d = 0;
        for (String value : values) {
            Tc column = (Tc) row.getContent().get(d++);
            column.setTcPr(tableCell.getTcPr());
            P columnParagraph = (P) column.getContent().get(0);
            columnParagraph.setPPr(paragraphProperties);
            Text tx = factory.createText();
            R run = factory.createR();
            run.setRPr(runProperties);
            tx.setValue(value);
            run.getContent().add(tx);
            columnParagraph.getContent().add(run);
        }
    }

    private PPr withAlignmentAndKeepNext(PPr paragraphProperties, JcEnumeration hAlign) {
        Jc align = new Jc();
        align.setVal(hAlign);
        paragraphProperties.setJc(align);
        paragraphProperties.setKeepNext(new BooleanDefaultTrue());
        return paragraphProperties;
    }

    private void createHeaderPart(WordprocessingMLPackage wordMLPackage, List<Map<String, String>> headerTableData) {

        HeaderPart headerPart = (HeaderPart) wordMLPackage.getParts().getParts().values().stream().filter(p -> p instanceof HeaderPart).findFirst().get();
        // add table before line separator
        headerPart.getContent().add(0, getTable(headerSampleTable, headerTableData, false));
    }
}

