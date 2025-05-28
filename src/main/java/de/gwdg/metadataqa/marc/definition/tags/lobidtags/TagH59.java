package de.gwdg.metadataqa.marc.definition.tags.lobidtags;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.structure.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.structure.Indicator;

/**
 *  normierte Bestandsangaben 	LOCAL 859 (H59) from ALMA Publishing  Holdings information (Hxx)
 * https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=949911658
 * https://www.alma-dach.org/alma-marc/holdings/859/859.html
 */
public class TagH59 extends DataFieldDefinition {

  private static TagH59 uniqueInstance;

  private TagH59() {
    initialize();
    postCreation();
  }

  public static TagH59 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new TagH59();
    return uniqueInstance;
  }

  private void initialize() {
    tag = "H59";
    label = "LOCAL 859 (H59)";
    mqTag = "LOCAL859";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=949911658";

    ind1 = new Indicator("Gruppe")
    .setCodes(
      "0", "Beginngruppe",
      "1", "Endgruppe"
    )
    .setMqTag("gruppe");


    ind2 = new Indicator("Bestand")
    .setCodes(
      "0", "abgeschlossener Bestand",
      "1", "laufender Bestand"
    )
    .setMqTag("bestand");

    setSubfieldsWithCardinality(
      "8", "Sortierhilfe", "NR",
      "a", "Bandzählung", "NR",
      "i", "Berichtsjahr", "NR",
      "8", "ALMA MMS ID linking HOL to HXX elements","R"
    );
  }
}
