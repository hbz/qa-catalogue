package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für HeBIS
 */
public class Tag956 extends DataFieldDefinition {

  private static Tag956 uniqueInstance;

  private Tag956() {
    initialize();
    postCreation();
  }

  public static Tag956 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag956();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "956";
    label = "reserviert für HeBIS";
    mqTag = "reserviert für HeBIS";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=698777686";

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "HeBIS-Feld", "R"
    );


  }
}
