package de.gwdg.metadataqa.marc.definition.tags.hbztags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 * reserviert für HeBIS
 */
public class Tag957 extends DataFieldDefinition {

  private static Tag957 uniqueInstance;

  private Tag957() {
    initialize();
    postCreation();
  }

  public static Tag957 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag957();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "957";
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
