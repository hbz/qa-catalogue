package de.gwdg.metadataqa.marc.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DataElementsStaticticsTest {

  @Test
  public void testStatistics() {
    Counter<DataElementType> statistics = DataElementsStatictics.count();
    assertEquals(   8, statistics.keys().size());
    assertEquals("There should be 6 defined control fields",   6, statistics.get(DataElementType.controlFields));
    assertEquals("There should be 212 defined control field positions",  212, statistics.get(DataElementType.controlFieldPositions));
    assertEquals("There should be 230 defined fields in MARC core",  230, statistics.get(DataElementType.coreFields));
    assertEquals("There should be 186 defined indicators in MARC core",  186, statistics.get(DataElementType.coreIndicators));
    assertEquals("There should be 2688 defined subfields in MARC core", 2688, statistics.get(DataElementType.coreSubfields));
    assertEquals("There should be 303 defined locally defined fields",  303, statistics.get(DataElementType.localFields));
    assertEquals("There should be 67 defined locally defined indicators",   67, statistics.get(DataElementType.localIndicators));
    assertEquals("There should be 2567 defined locally defined subfields", 2567 , statistics.get(DataElementType.localSubfields));
    assertEquals("There should be 6259 defined data elements", 6259, statistics.total());
  }
}