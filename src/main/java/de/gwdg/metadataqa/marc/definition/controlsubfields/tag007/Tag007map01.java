package de.gwdg.metadataqa.marc.definition.controlsubfields.tag007;

import de.gwdg.metadataqa.marc.Utils;
import de.gwdg.metadataqa.marc.definition.ControlSubfield;

/**
 * Specific material designation
 * https://www.loc.gov/marc/bibliographic/bd007a.html
 */
public class Tag007map01 extends ControlSubfield {
	private static Tag007map01 uniqueInstance;

	private Tag007map01() {
		initialize();
		extractValidCodes();
	}

	public static Tag007map01 getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new Tag007map01();
		return uniqueInstance;
	}

	private void initialize() {
		label = "Specific material designation";
		id = "tag007map01";
		mqTag = "specificMaterialDesignation";
		positionStart = 1;
		positionEnd = 2;
		descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd007a.html";
		codes = Utils.generateCodes(
			"d", "Atlas",
			"g", "Diagram",
			"j", "Map",
			"k", "Profile",
			"q", "Model",
			"r", "Remote-sensing image",
			"s", "Section",
			"u", "Unspecified",
			"y", "View",
			"z", "Other",
			"|", "No attempt to code"
		);
	}
}