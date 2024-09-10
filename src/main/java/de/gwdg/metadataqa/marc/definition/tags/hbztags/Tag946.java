package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für EKZ
 */
public class Tag946 extends DataFieldDefinition {

  private static Tag946 uniqueInstance;

  private Tag946() {
    initialize();
    postCreation();
  }

  public static Tag946 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag946();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "946";
    label = "reserviert für EKZ";
    mqTag = "reserviert für EKZ";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=698777686";

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "EKZ-Feld", "R"
    );


  }
}
