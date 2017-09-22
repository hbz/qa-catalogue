package de.gwdg.metadataqa.marc.definition.tags01x;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;

/**
 * Universal Decimal Classification Number
 * http://www.loc.gov/marc/bibliographic/bd080.html
 */
public class Tag080 extends DataFieldDefinition {

	private static Tag080 uniqueInstance;

	private Tag080() {
		initialize();
	}

	public static Tag080 getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new Tag080();
		return uniqueInstance;
	}

	private void initialize() {
		tag = "080";
		label = "Universal Decimal Classification Number";
		cardinality = Cardinality.Repeatable;
		ind1 = new Indicator("Type of edition").setCodes(
			" ", "No information provided",
			"0", "Full",
			"1", "Abridged"
		);
		ind2 = new Indicator();
		setSubfieldsWithCardinality(
			"a", "Universal Decimal Classification number", "NR",
			"b", "Item number", "NR",
			"x", "Common auxiliary subdivision", "R",
			"2", "Edition identifier", "NR",
			"6", "Linkage", "NR",
			"8", "Field link and sequence number", "R"
		);
	}
}