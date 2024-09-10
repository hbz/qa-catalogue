package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für GBV
 */
public class Tag951 extends DataFieldDefinition {

  private static Tag951 uniqueInstance;

  private Tag951() {
    initialize();
    postCreation();
  }

  public static Tag951 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag951();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "951";
    label = "reserviert für GBV";
    mqTag = "reserviert für GBV";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=698777686";

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "GBV-Feld", "R"
    );


  }
}
