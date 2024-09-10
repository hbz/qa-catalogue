package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für NB Schweiz und Schweiz
 */
public class Tag976 extends DataFieldDefinition {

  private static Tag976 uniqueInstance;

  private Tag976() {
    initialize();
    postCreation();
  }

  public static Tag976 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag976();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "976";
    label = "reserviert für NB Schweiz und Schweiz";
    mqTag = "reserviert für NB Schweiz und Schweiz";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=698777686";

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "NB Schweiz und Schweiz-Feld", "R"
    );


  }
}
