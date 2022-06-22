package de.gwdg.metadataqa.marc.cli.utils.ignorablerecords;

import de.gwdg.metadataqa.marc.dao.MarcRecord;

import java.io.Serializable;

public class RecordFilterMarc21 extends Marc21Filter implements RecordFilter, Serializable {
  private static final long serialVersionUID = 2925309909344217190L;

  public RecordFilterMarc21(String allowableRecordsInput) {
    parseInput(allowableRecordsInput);
  }

  @Override
  public boolean isAllowable(MarcRecord marcRecord) {
    if (isEmpty())
      return true;

    return met(marcRecord);
  }

  @Override
  public String toString() {
    return isEmpty() ? "" : conditions.toString();
  }
}
