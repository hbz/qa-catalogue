package de.gwdg.metadataqa.marc.utils;

import de.gwdg.metadataqa.marc.MarcFactory;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlephseqMarcReader implements MarcReader {

  private BufferedReader bufferedReader = null;
  private String line = null;
  private boolean nextIsConsumed = false;
  private int lineNumber = 0;
  private List<AlephseqLine> lines = new ArrayList<>();
  private String currentId = null;

  public AlephseqMarcReader(String alephseqMarc) {
    try {
      bufferedReader = new BufferedReader(new FileReader(alephseqMarc));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean hasNext() {
    if (lineNumber == 0 || nextIsConsumed) {
      try {
        line = bufferedReader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
      lineNumber++;
      nextIsConsumed = false;
    }
    return (line != null);
  }

  @Override
  public Record next() {
    Record record = null;
    boolean finished = false;
    while (line != null && !finished) {
      AlephseqLine alephseqLine = new AlephseqLine(line, lineNumber);
      if (alephseqLine.isValidTag()) {
        if (alephseqLine.isLeader() && !lines.isEmpty()) {
          record = MarcFactory.createRecordFromAlephseq(lines);
          finished = true;
          lines = new ArrayList<>();
        }
        lines.add(alephseqLine);
      }

      try {
        line = bufferedReader.readLine();
        lineNumber++;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (line == null && !lines.isEmpty()) {
      record = MarcFactory.createRecordFromAlephseq(lines);
    }
    return record;
  }
}
