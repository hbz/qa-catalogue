package de.gwdg.metadataqa.marc.cli;

import de.gwdg.metadataqa.marc.DataField;
import de.gwdg.metadataqa.marc.MarcRecord;
import de.gwdg.metadataqa.marc.MarcSubfield;
import de.gwdg.metadataqa.marc.cli.parameters.CommonParameters;
import de.gwdg.metadataqa.marc.cli.parameters.ValidatorParameters;
import de.gwdg.metadataqa.marc.cli.processor.MarcFileProcessor;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.marc4j.marc.Record;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class ClassificationAnalysis implements MarcFileProcessor, Serializable {

  private static final Logger logger = Logger.getLogger(ClassificationAnalysis.class.getCanonicalName());

  private final Options options;
  private CommonParameters parameters;
  private Map<Schema, Integer> schemaInstanceStatistics = new HashMap<>();
  private Map<Schema, Integer> schemaRecordStatistics = new HashMap<>();
  private Map<String, Map<String[], Integer>> fieldInstanceStatistics = new TreeMap<>();
  private Map<Boolean, Integer> hasClassifications = new HashMap<>();
  private Map<String[], Integer> fieldInRecordsStatistics = new HashMap<>();
  private boolean readyToProcess;

  private static final List<String> fieldsWithIndicator1AndSubfield2 = Arrays.asList(
    "052", // Geographic Classification
    "086", // Government Document Classification Number
    "852"  // Location
  );

  private static final List<String> fieldsWithIndicator2AndSubfield2 = Arrays.asList(
    "055", // Classification Numbers Assigned in Canada
    "072", // Subject Category Code
    "600", // Subject Added Entry - Personal Name
    "610", // Subject Added Entry - Corporate Name
    "611", // Subject Added Entry - Meeting Name
    "630", // Subject Added Entry - Uniform Title
    "647", // Subject Added Entry - Named Event
    "648", // Subject Added Entry - Chronological Term
    "650", // Subject Added Entry - Topical Term
    "651", // Subject Added Entry - Geographic Name
    "655"  // Index Term - Genre/Form
  );

  // 052 is bad here
  // 055 $2 -- Used only when the second indicator contains value 6 (Other call number assigned by LAC), 7 (Other class number assigned by LAC), 8 (Other call number assigned by the contributing library), or 9 (Other class number assigned by the contributing library).
  private static final List<String> fieldsWithSubfield2 = Arrays.asList(
    "084"  // Other Classificaton Number
  );

  private static final Map<String, String> fieldsWithScheme = new HashMap<>();
  static {
    fieldsWithScheme.put("080", "Universal Decimal Classification");
    fieldsWithScheme.put("082", "Dewey Decimal Classification");
    fieldsWithScheme.put("083", "Dewey Decimal Classification");
    fieldsWithScheme.put("085", "Dewey Decimal Classification");
    // fieldsWithScheme.put("086", "Government Document Classification");
  }

  public ClassificationAnalysis(String[] args) throws ParseException {
    parameters = new ValidatorParameters(args);
    options = parameters.getOptions();
    readyToProcess = true;
  }

  public static void main(String[] args) {
    MarcFileProcessor processor = null;
    try {
      processor = new ClassificationAnalysis(args);
    } catch (ParseException e) {
      System.err.println("ERROR. " + e.getLocalizedMessage());
      // processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    if (processor.getParameters().getArgs().length < 1) {
      System.err.println("Please provide a MARC file name!");
      processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    if (processor.getParameters().doHelp()) {
      processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    RecordIterator iterator = new RecordIterator(processor);
    iterator.start();
  }

  @Override
  public CommonParameters getParameters() {
    return parameters;
  }

  @Override
  public void processRecord(Record marc4jRecord, int recordNumber) throws IOException {

  }

  @Override
  public void processRecord(MarcRecord marcRecord, int recordNumber) throws IOException {
    boolean hasSchema = false;
    for (String field : fieldsWithIndicator1AndSubfield2) {
      if (!marcRecord.hasDatafield(field))
        continue;

      Map<String[], Integer> fieldStatistics = getFieldInstanceStatistics(field);
      List<Schema> schemas = new ArrayList<>();
      for (String scheme : marcRecord.extract(field, "ind1")) {
        if (scheme.equals("No information provided"))
          continue;

        if (!field.equals("852"))
          hasSchema = true;

        if (isAReferenceToSubfield2(field, scheme)) {
          List<String> altSchemes = marcRecord.extract(field, "2", true);
          if (altSchemes.isEmpty()) {
            schemas.add(new Schema(field, "$2", "undetectable"));
          } else {
            for (String altScheme : altSchemes) {
              schemas.add(new Schema(field, "$2", altScheme));
            }
          }
        } else {
          schemas.add(new Schema(field, "ind1", scheme));
        }
      }
      addSchemasToStatistics(schemaInstanceStatistics, schemas);
      addSchemasToStatistics(schemaRecordStatistics, deduplicateSchema(schemas));
    }

    for (String field : fieldsWithIndicator2AndSubfield2) {
      if (!marcRecord.hasDatafield(field))
        continue;

      hasSchema = true;
      Map<String[], Integer> fieldStatistics = getFieldInstanceStatistics(field);
      List<Schema> schemas = new ArrayList<>();
      for (String scheme : marcRecord.extract(field, "ind2")) {
        if (isAReferenceToSubfield2(field, scheme)) {
          List<String> altSchemes = marcRecord.extract(field, "2", true);
          if (altSchemes.isEmpty()) {
            schemas.add(new Schema(field, "$2", "undetectable"));
          } else {
            for (String altScheme : altSchemes) {
              schemas.add(new Schema(field, "$2", altScheme));
            }
          }
        } else {
          schemas.add(new Schema(field, "ind2", scheme));
        }
      }
      addSchemasToStatistics(schemaInstanceStatistics, schemas);
      addSchemasToStatistics(schemaRecordStatistics, deduplicateSchema(schemas));
    }

    for (String field : fieldsWithSubfield2) {
      if (!marcRecord.hasDatafield(field))
        continue;

      hasSchema = true;
      Map<String[], Integer> fieldStatistics = getFieldInstanceStatistics(field);
      List<String> schemes = marcRecord.extract(field, "2", true);
      if (schemes.isEmpty())
        schemes.add("undetectable");
      List<Schema> schemas = new ArrayList<>();
      for (String scheme : schemes) {
        schemas.add(new Schema(field, "$2", scheme));
      }
      addSchemasToStatistics(schemaInstanceStatistics, schemas);
      addSchemasToStatistics(schemaRecordStatistics, deduplicateSchema(schemas));
    }

    for (Map.Entry<String, String> entry : fieldsWithScheme.entrySet()) {
      final String field = entry.getKey();
      if (!marcRecord.hasDatafield(field))
        continue;

      hasSchema = true;
      Map<String[], Integer> fieldStatistics = getFieldInstanceStatistics(field);
      List<DataField> fields = marcRecord.getDatafield(field);
      List<Schema> schemas = new ArrayList<>();
      for (DataField dataField : fields) {
        // System.err.println(dataField.getInd1());
        String first = null;
        String alt = null;
        for (MarcSubfield subfield : dataField.getSubfields()) {
          String code = subfield.getCode();
          if (!code.equals("1") && !code.equals("2") && !code.equals("6") && !code.equals("8")) {
            first = "$" + code;
            break;
          } else {
            if (alt == null)
              alt = "$" + code;
          }
        }
        if (first != null) {
          // first = alt;
          schemas.add(new Schema(field, first, entry.getValue()));
        } else {
          System.err.println(dataField);
        }
      }
      addSchemasToStatistics(schemaInstanceStatistics, schemas);
      addSchemasToStatistics(schemaRecordStatistics, deduplicateSchema(schemas));
    }

    if (!hasClassifications.containsKey(hasSchema)) {
      hasClassifications.put(hasSchema, 0);
    }
    hasClassifications.put(hasSchema, hasClassifications.get(hasSchema) + 1);
  }

  private List<Schema> deduplicateSchema(List<Schema> schemas) {
    Set<Schema> set = new HashSet<Schema>(schemas);
    List<Schema> deduplicated = new ArrayList<Schema>();
    deduplicated.addAll(new HashSet<Schema>(schemas));
    return deduplicated;
  }

  private boolean isAReferenceToSubfield2(String field, String scheme) {
    return ((field.equals("055") && isAReferenceFrom055(scheme)) || scheme.equals("Source specified in subfield $2"));
  }

  private boolean isAReferenceFrom055(String scheme) {
    return (scheme.equals("Other call number assigned by LAC")
            || scheme.equals("Other class number assigned by LAC")
            || scheme.equals("Other call number assigned by the contributing library")
            || scheme.equals("Other class number assigned by the contributing library"));
  }

  private void addSchemasToStatistics(Map<Schema, Integer> fieldStatistics, List<Schema> schemes) {
    if (!schemes.isEmpty()) {
      for (Schema scheme : schemes) {
        if (!fieldStatistics.containsKey(scheme)) {
          fieldStatistics.put(scheme, 0);
        }
        fieldStatistics.put(scheme, fieldStatistics.get(scheme) + 1);
      }
    }
  }

  private void addSchemesToStatistics(Map<String[], Integer> fieldStatistics, List<String[]> schemes) {
    if (!schemes.isEmpty()) {
      for (String[] scheme : schemes) {
        if (!fieldStatistics.containsKey(scheme)) {
          fieldStatistics.put(scheme, 0);
          if (!fieldInRecordsStatistics.containsKey(scheme)) {
            fieldInRecordsStatistics.put(scheme, 0);
          }
          fieldInRecordsStatistics.put(scheme, fieldInRecordsStatistics.get(scheme) + 1);
        }
        fieldStatistics.put(scheme, fieldStatistics.get(scheme) + 1);
      }
    }
  }

  private Map<String[], Integer> getFieldInstanceStatistics(String field) {
    if (!fieldInstanceStatistics.containsKey(field)) {
      fieldInstanceStatistics.put(field, new HashMap<String[], Integer>());
    }
    return fieldInstanceStatistics.get(field);
  }

  @Override
  public void beforeIteration() {

  }

  @Override
  public void fileOpened(Path path) {

  }

  @Override
  public void fileProcessed() {

  }

  @Override
  public void afterIteration() {
    char separator = ',';
    Path path = Paths.get(parameters.getOutputDir(), "classifications-by-field.csv");
    /*
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(String.format("field%sscheme%scount\n", separator, separator));
      fieldInstanceStatistics
        .entrySet()
        .stream()
        .forEach(fieldEntry -> {
            // System.err.println(entry.getKey());
            fieldEntry.getValue()
              .entrySet()
              .stream()
              .sorted((e1, e2) ->
                e2.getValue().compareTo(e1.getValue()))
              .forEach(
                classificationStatistics -> {
                  try {
                    // int perRecord = fieldInRecordsStatistics.get(entry.getKey());
                    String schema = classificationStatistics.getKey()[0];
                    String location = classificationStatistics.getKey()[1];
                    int count = classificationStatistics.getValue();
                    writer.write(String.format("%s%s'%s'%s%d\n",
                      fieldEntry.getKey(), separator, location, separator, schema, separator, count
                    ));
                  } catch (IOException ex) {
                    ex.printStackTrace();
                  }
                }
              );
          }
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
    */

    path = Paths.get(parameters.getOutputDir(), "classifications-by-schema.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(String.format("%s\n", // field%slocation%sscheme%scount\n", separator, separator, separator));
        StringUtils.join(
          Arrays.asList("field", "location", "scheme", "recordcount", "count"),
          separator
      )));

              schemaInstanceStatistics
        .entrySet()
        .stream()
        .sorted((e1, e2) -> {
            int i = e1.getKey().field.compareTo(e2.getKey().field);
            if (i != 0)
              return i;
            else {
              i = e1.getKey().location.compareTo(e2.getKey().location);
              if (i != i)
                return i;
              else
                return e2.getValue().compareTo(e1.getValue());
            }
          }
        )
        .forEach(
          entry -> {
            Schema schema = entry.getKey();
            int count = entry.getValue();
            int recordCount = schemaRecordStatistics.get(schema);
            try {
              writer.write(String.format("%s\n",
                StringUtils.join(
                  Arrays.asList(schema.field, schema.location, '"' + schema.schema.replace("\"", "\\\"") + '"', recordCount, count),
                  separator
                )
              ));
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }

          /*
            schemaEntry. -> {
            // System.err.println(entry.getKey());
              schemaEntry.entrySet()
              .stream()
              .sorted((e1, e2) ->
                e2.getValue().compareTo(e1.getValue()))
              .forEach(
                classificationStatistics -> {
                  try {
                    // int perRecord = fieldInRecordsStatistics.get(entry.getKey());
                    String schema = classificationStatistics.getKey()[0];
                    String location = classificationStatistics.getKey()[1];
                    int count = classificationStatistics.getValue();
                    writer.write(String.format("%s%s'%s'%s%d\n",
                      fieldEntry.getKey(), separator, location, separator, schema, separator, count
                    ));
                  } catch (IOException ex) {
                    ex.printStackTrace();
                  }
                }
              );
          }
           */
        );
    } catch (IOException e) {
      e.printStackTrace();
    }

    path = Paths.get(parameters.getOutputDir(), "classifications-by-records.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(String.format("records-with-classification%scount\n", separator));
      hasClassifications
        .entrySet()
        .stream()
        .sorted((e1, e2) ->
          e2.getValue().compareTo(e1.getValue()))
        .forEach(
          e -> {
            try {
              writer.write(String.format("%s%s%d\n",
                e.getKey().toString(), separator, e.getValue()
              ));
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void printHelp(Options options) {

  }

  @Override
  public boolean readyToProcess() {
    return readyToProcess;
  }

  private class Schema {
    String field;
    String location;
    String schema;

    public Schema(String field, String location, String schema) {
      this.field = field;
      this.location = location;
      this.schema = schema;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;

      if (o == null || getClass() != o.getClass()) return false;

      Schema schema1 = (Schema) o;

      return new EqualsBuilder()
        .append(field, schema1.field)
        .append(location, schema1.location)
        .append(schema, schema1.schema)
        .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(17, 37)
        .append(field)
        .append(location)
        .append(schema)
        .toHashCode();
    }
  }

  private class Counter {
    int recordCount;
    int instanceCount;
  }

  // private
}
