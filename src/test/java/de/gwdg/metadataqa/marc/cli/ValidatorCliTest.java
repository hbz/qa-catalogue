package de.gwdg.metadataqa.marc.cli;

import de.gwdg.metadataqa.api.util.FileUtils;
import de.gwdg.metadataqa.marc.cli.utils.RecordIterator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValidatorCliTest extends CliTestUtils {

  private String outputDir;
  private List<String> outputFiles;
  private List<String> grouppedOutputFiles;

  @Before
  public void setUp() throws Exception {
    outputDir = getPath("src/test/resources/output");
    outputFiles = Arrays.asList(
      "count.csv",
      "issue-details.csv",
      "issue-summary.csv",
      "issue-by-category.csv",
      "issue-by-type.csv",
      "issue-collector.csv",
      "issue-total.csv",
      "validation.params.json"
    );
    grouppedOutputFiles = Arrays.asList(
      "count.csv",
      "issue-details.csv",
      "issue-summary.csv",
      "issue-by-category.csv",
      "issue-by-type.csv",
      "issue-collector.csv",
      "issue-total.csv",
      "validation.params.json"
    );
  }

  @Test
  public void validate_pica_normal() throws Exception {
    clearOutput(outputDir, grouppedOutputFiles);

    ValidatorCli processor = new ValidatorCli(new String[]{
      "--schemaType", "PICA",
      "--marcFormat", "PICA_NORMALIZED",
      "--outputDir", outputDir,
      "--details",
      "--trimId",
      "--summary",
      "--format", "csv",
      "--defaultRecordType", "BOOKS",
      "--detailsFileName", "issue-details.csv",
      "--summaryFileName", "issue-summary.csv",
      getPath("src/test/resources/pica/pica-with-holdings-info.dat")
    });
    RecordIterator iterator = new RecordIterator(processor);
    iterator.start();
    assertEquals("done", iterator.getStatus());

    for (String outputFile : grouppedOutputFiles) {
      File output = new File(outputDir, outputFile);
      assertTrue(outputFile + " should exist", output.exists());
      List<String> lines = FileUtils.readLinesFromFile("src/test/resources/output/" + outputFile);
      if (outputFile.equals("issue-details.csv")) {
        assertEquals(11, lines.size());
        assertEquals("010000011,1:1;2:1;3:1;4:1;5:1;6:1;7:1;8:1;9:1;10:1;11:1;12:1;13:1;14:1;15:1;16:1;17:1;18:1;19:1;20:1;21:2;22:2;23:1;24:1", lines.get(1).trim());
        assertEquals("01000002X,1:1;2:1;4:1;21:1;5:1;22:1;6:1;23:1;7:1;24:1;8:1;25:1;26:2", lines.get(2).trim());
        assertEquals("010000038,1:1;2:1;4:1;5:1;6:1;7:1;8:1;21:2;22:2;23:1;24:1;25:1;26:1;27:1;28:2", lines.get(3).trim());
        assertEquals("010000054,32:1;33:1;1:1;34:1;2:1;35:1;36:1;37:1;38:1;39:5;40:5;21:3;22:3;29:1;30:1;31:1", lines.get(4).trim());
        assertEquals("010000070,32:1;33:1;1:1;34:1;2:1;35:1;41:1;42:1;43:1;44:1;45:1;46:1;47:1;48:1;49:1;21:1;22:1;23:1;24:1;29:1;30:1;31:1", lines.get(5).trim());
        assertEquals("010000089,1:1;50:1;2:1;51:1;52:1;53:1", lines.get(6).trim());
        assertEquals("010000127,1:1;2:1;23:1;24:1", lines.get(7).trim());
        assertEquals("010000151,1:1;2:1", lines.get(8).trim());
        assertEquals("010000178,32:1;33:1;1:1;34:1;2:1;35:1;36:1;37:1;38:1;39:6;40:6;21:2;22:2;54:2;23:1;55:2;56:1;24:1;57:1;29:1;30:1;31:1", lines.get(9).trim());
        assertEquals("010000194,1:1;2:1;6:1;7:1;8:1;9:1;10:1;11:1;15:1;16:1;17:1;18:1;19:1;21:2;22:2;23:2;24:2;25:1;58:1", lines.get(10).trim());

      } else if (outputFile.equals("issue-summary.csv")) {
        System.err.println(StringUtils.join(lines, "\n"));
        assertEquals(59, lines.size());
        assertEquals("id,MarcPath,categoryId,typeId,type,message,url,instances,records", lines.get(0).trim());
        assertTrue(lines.contains("57,041A/00-99,3,8,repetition of non-repeatable field,there are 2 instances,https://format.k10plus.de/k10plushelp.pl?cmd=kat&katalog=Standard&val=5100-5199,1,1"));
        assertTrue(lines.contains("1,001@,3,9,undefined field,001@,,10,10"));
        assertTrue(lines.contains("2,001U,3,9,undefined field,001U,,10,10"));
        assertTrue(lines.contains("3,036F/01,3,9,undefined field,036F/01,,1,1"));

      } else if (outputFile.equals("issue-by-category.csv")) {
        assertEquals(3, lines.size());
        assertEquals("3,data field,22,10", lines.get(1).trim());

      } else if (outputFile.equals("issue-by-type.csv")) {
        assertEquals(5, lines.size());
        assertEquals("8,3,data field,repetition of non-repeatable field,1,1", lines.get(1).trim());
        assertEquals("9,3,data field,undefined field,21,10", lines.get(2).trim());

      } else if (outputFile.equals("issue-collector.csv")) {
        assertEquals(59, lines.size());
        assertEquals("1,010000151;010000011;010000054;010000070;010000194;01000002X;010000127;010000038;010000178;010000089", lines.get(1).trim());
        assertEquals("2,010000151;010000011;010000054;010000070;010000194;01000002X;010000127;010000038;010000178;010000089", lines.get(2).trim());
        assertEquals("3,010000011", lines.get(3).trim());
        assertEquals("4,010000011;01000002X;010000038", lines.get(4).trim());

      } else if (outputFile.equals("issue-total.csv")) {
        assertEquals(3, lines.size());
        assertEquals("1,179,10", lines.get(1).trim());
        assertEquals("2,158,9", lines.get(2).trim());
      }

      output.delete();
      assertFalse(outputFile + " should not exist anymore", output.exists());
    }
  }

  @Test
  public void validate_pica_groupBy() throws Exception {
    clearOutput(outputDir, grouppedOutputFiles);

    // Stream.of(new File(outputDir).listFiles()).forEach(file -> System.err.println(file));

    ValidatorCli processor = new ValidatorCli(new String[]{
      "--schemaType", "PICA",
      "--groupBy", "001@$0",
      "--marcFormat", "PICA_NORMALIZED",
      "--outputDir", outputDir,
      "--details",
      "--trimId",
      "--summary",
      "--format", "csv",
      "--defaultRecordType", "BOOKS",
      "--detailsFileName", "issue-details.csv",
      "--summaryFileName", "issue-summary.csv",
      getPath("src/test/resources/pica/pica-with-holdings-info.dat")
    });
    RecordIterator iterator = new RecordIterator(processor);
    iterator.start();
    assertEquals(iterator.getStatus(), "done");

    // Stream.of(new File(outputDir).listFiles()).forEach(file -> System.err.println(file));

    for (String outputFile : grouppedOutputFiles) {
      File output = new File(outputDir, outputFile);
      assertTrue(outputFile + " should exist", output.exists());
      List<String> lines = FileUtils.readLinesFromFile("src/test/resources/output/" + outputFile);
      if (outputFile.equals("issue-details.csv")) {
        assertEquals(11, lines.size());
        assertEquals("recordId,errors", lines.get(0).trim());
        assertEquals("010000011,1:1;2:1;3:1;4:1;5:1;6:1;7:1;8:1;9:1;10:1;11:1;12:1;13:1;14:1;15:1;16:1;17:1;18:1;19:1;20:1;21:2;22:2;23:1;24:1", lines.get(1).trim());
        assertEquals("01000002X,1:1;2:1;4:1;21:1;5:1;22:1;6:1;23:1;7:1;24:1;8:1;25:1;26:2", lines.get(2).trim());
        assertEquals("010000038,1:1;2:1;4:1;5:1;6:1;7:1;8:1;21:2;22:2;23:1;24:1;25:1;26:1;27:1;28:2", lines.get(3).trim());
        assertEquals("010000054,32:1;33:1;1:1;34:1;2:1;35:1;36:1;37:1;38:1;39:5;40:5;21:3;22:3;29:1;30:1;31:1", lines.get(4).trim());
        assertEquals("010000070,32:1;33:1;1:1;34:1;2:1;35:1;41:1;42:1;43:1;44:1;45:1;46:1;47:1;48:1;49:1;21:1;22:1;23:1;24:1;29:1;30:1;31:1", lines.get(5).trim());
        assertEquals("010000089,1:1;50:1;2:1;51:1;52:1;53:1", lines.get(6).trim());
        assertEquals("010000127,1:1;2:1;23:1;24:1", lines.get(7).trim());
        assertEquals("010000151,1:1;2:1", lines.get(8).trim());
        assertEquals("010000178,32:1;33:1;1:1;34:1;2:1;35:1;36:1;37:1;38:1;39:6;40:6;21:2;22:2;54:2;23:1;55:2;56:1;24:1;57:1;29:1;30:1;31:1", lines.get(9).trim());
        assertEquals("010000194,1:1;2:1;6:1;7:1;8:1;9:1;10:1;11:1;15:1;16:1;17:1;18:1;19:1;21:2;22:2;23:2;24:2;25:1;58:1", lines.get(10).trim());

      } else if (outputFile.equals("issue-summary.csv")) {
        assertEquals(59, lines.size());
        assertEquals("id,MarcPath,categoryId,typeId,type,message,url,instances,records", lines.get(0).trim());
        assertTrue(lines.contains("57,041A/00-99,3,8,repetition of non-repeatable field,there are 2 instances,https://format.k10plus.de/k10plushelp.pl?cmd=kat&katalog=Standard&val=5100-5199,1,1"));
        assertTrue(lines.contains("1,001@,3,9,undefined field,001@,,10,10"));
        assertTrue(lines.contains("2,001U,3,9,undefined field,001U,,10,10"));
        assertTrue(lines.contains("3,036F/01,3,9,undefined field,036F/01,,1,1"));

      } else if (outputFile.equals("issue-by-category.csv")) {
        assertEquals(3, lines.size());
        assertEquals("id,category,instances,records", lines.get(0).trim());
        assertEquals("3,data field,22,10", lines.get(1).trim());

      } else if (outputFile.equals("issue-by-type.csv")) {
        assertEquals(5, lines.size());
        assertEquals("id,categoryId,category,type,instances,records", lines.get(0).trim());
        assertEquals("8,3,data field,repetition of non-repeatable field,1,1", lines.get(1).trim());
        assertEquals("9,3,data field,undefined field,21,10", lines.get(2).trim());

      } else if (outputFile.equals("issue-collector.csv")) {
        assertEquals(59, lines.size());
        assertEquals("errorId,recordIds", lines.get(0).trim());
        assertEquals("1,010000151;010000011;010000054;010000070;010000194;01000002X;010000127;010000038;010000178;010000089", lines.get(1).trim());
        assertEquals("2,010000151;010000011;010000054;010000070;010000194;01000002X;010000127;010000038;010000178;010000089", lines.get(2).trim());
        assertEquals("3,010000011", lines.get(3).trim());
        assertEquals("4,010000011;01000002X;010000038", lines.get(4).trim());

      } else if (outputFile.equals("issue-total.csv")) {
        assertEquals(3, lines.size());
        assertEquals("type,instances,records", lines.get(0).trim());
        assertEquals("1,179,10", lines.get(1).trim());
        assertEquals("2,158,9", lines.get(2).trim());

      } else if (outputFile.equals("count.csv")) {
        assertEquals(2, lines.size());
        assertEquals("total", lines.get(0).trim());
        assertEquals("10", lines.get(1).trim());

      } else if (outputFile.equals("validation.params.json")) {
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("\"args\":[\"/home/kiru/git/metadata-qa-marc/src/test/resources/pica/pica-with-holdings-info.dat\"]"));
        assertTrue(lines.get(0).contains("\"marcVersion\":\"MARC21\","));
        assertTrue(lines.get(0).contains("\"marcFormat\":\"PICA_NORMALIZED\","));
        assertTrue(lines.get(0).contains("\"dataSource\":\"FILE\","));
        assertTrue(lines.get(0).contains("\"limit\":-1,"));
        assertTrue(lines.get(0).contains("\"offset\":-1,"));
        assertTrue(lines.get(0).contains("\"id\":null,"));
        assertTrue(lines.get(0).contains("\"defaultRecordType\":\"BOOKS\","));
        assertTrue(lines.get(0).contains("\"alephseq\":false,"));
        assertTrue(lines.get(0).contains("\"marcxml\":false,"));
        assertTrue(lines.get(0).contains("\"lineSeparated\":false,"));
        assertTrue(lines.get(0).contains("\"trimId\":true,"));
        assertTrue(lines.get(0).contains("\"outputDir\":\"/home/kiru/git/metadata-qa-marc/src/test/resources/output\","));
        assertTrue(lines.get(0).contains("\"recordIgnorator\":{\"criteria\":[],\"booleanCriteria\":null,\"empty\":true},"));
        assertTrue(lines.get(0).contains("\"recordFilter\":{\"criteria\":[],\"booleanCriteria\":null,\"empty\":true},"));
        assertTrue(lines.get(0).contains("\"ignorableFields\":{\"fields\":null,\"empty\":true},"));
        assertTrue(lines.get(0).contains("\"stream\":null,"));
        assertTrue(lines.get(0).contains("\"defaultEncoding\":null,"));
        assertTrue(lines.get(0).contains("\"alephseqLineType\":null,"));
        assertTrue(lines.get(0).contains("\"picaIdField\":\"003@$0\","));
        assertTrue(lines.get(0).contains("\"picaSubfieldSeparator\":\"$\","));
        assertTrue(lines.get(0).contains("\"picaSchemaFile\":null,"));
        assertTrue(lines.get(0).contains("\"picaRecordTypeField\":\"002@$0\","));
        assertTrue(lines.get(0).contains("\"schemaType\":\"PICA\","));
        assertTrue(lines.get(0).contains("\"groupBy\":\"001@$0\","));
        assertTrue(lines.get(0).contains("\"groupListFile\":null,"));
        assertTrue(lines.get(0).contains("\"detailsFileName\":\"issue-details.csv\","));
        assertTrue(lines.get(0).contains("\"summaryFileName\":\"issue-summary.csv\","));
        assertTrue(lines.get(0).contains("\"format\":\"COMMA_SEPARATED\","));
        assertTrue(lines.get(0).contains("\"ignorableIssueTypes\":null,"));
        assertTrue(lines.get(0).contains("\"pica\":true,"));
        assertTrue(lines.get(0).contains("\"replacementInControlFields\":null,"));
        assertTrue(lines.get(0).contains("\"marc21\":false,"));
        assertTrue(lines.get(0).contains("\"mqaf.version\":\"0.9.0\","));
        assertTrue(lines.get(0).contains("\"qa-catalogue.version\":\"0.7.0-SNAPSHOT\"}"));

      } else {
        assertTrue("Unhandlet output: " + outputFile, outputFile.equals(""));
      }

      output.delete();
      assertFalse(outputFile + " should not exist anymore", output.exists());
    }
  }
}
