package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für OBV
 */
public class Tag971 extends DataFieldDefinition {

  private static Tag971 uniqueInstance;

  private Tag971() {
    initialize();
    postCreation();
  }

  public static Tag971 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag971();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "971";
    label = "reserviert für OBV";
    mqTag = "reserviert für OBV";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=698777686";

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "OBV-Feld", "R"
    );


  }
}
