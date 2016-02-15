/**
 * Copyright 2016 Leadscope, Inc. All rights reserved.
 * LEADSCOPE PROPRIETARY and CONFIDENTIAL. Use is subject to license terms.
 */
package com.leadscope.commanda;

import com.leadscope.commanda.lambda.DefaultLambdaImports;
import com.leadscope.stucco.TypedValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Tests various commanda usages from the command-line
 */
public class CommandaIntegrationTest {
  private static String testDir = "src/test/resources/com/leadscope/commanda/";

  @Test
  public void testToxmlFile() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should have ids", "123-45-5", "103-90-2");

    Commanda cmda = new Commanda(
            "-tox", testDir + "test.toxml",
            "-ne", "cr -> cr.getIds().get(0).getValue()");
    cmda.run(null, expectedOutput, System.out);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testToxmlToCsv() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should have ids", "123-45-5\t123-45-5", "103-90-2\t103-90-2");

    Commanda cmda = new Commanda(
            "-tox", testDir + "test.toxml",
            "-ne", "cr -> Arrays.asList(cr.getIds().get(0).getValue(), cr.getIds().get(0).getValue())",
            "-tablines");
    cmda.run(null, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testCsvFile() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should have expected first column value", "id", "1", "2", "3");

    Commanda cmda = new Commanda(
            "-csv", testDir + "test-csv.txt",
            "-ne", "r -> r.get(0)");
    cmda.run(null, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testBadToxml() throws Throwable {
    try {
      Commanda cmda = new Commanda(
              "-tox", testDir + "test-bad.toxml");
      cmda.run(null, null);
      Assert.fail("Should have caught invalid toxml exception");
    }
    catch (RuntimeException re) {
      Assert.assertTrue("Should mention bad element Blah", re.getMessage().contains("Blah"));
    }
  }

  @Test
  public void testEmpty() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should have expected first column value", "foo", "4");

    Commanda cmda = new Commanda(
            "-csv", testDir + "test2.txt",
            "-e", "in -> in.filter(r -> !empty(r.get(1))).map(r -> r.get(0))");
    cmda.run(null, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testDefault() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should include provided text",
            "This is some text.", "Some more words.", "");

    String inputString = "This is some text.\nSome more words.\n\n";
    InputStream input = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

    Commanda cmda = new Commanda();
    cmda.run(input, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testLetterCount() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should return letter counts of lines",
            "18", "16", "0");

    String inputString = "This is some text.\nSome more words.\n\n";
    InputStream input = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

    Commanda cmda = new Commanda(
            "-e", "in -> in.mapToInt(s -> s.length()).mapToObj(l -> String.valueOf(l))"
    );
    cmda.run(input, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testLineCount() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should return the number of lines", "3");

    String inputString = "This is some text.\nSome more words.\n\n";
    InputStream input = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

    Commanda cmda = new Commanda(
            "-e", "in -> of(in.reduce(0, (count, v) -> count + 1, (count, otherCount) -> count + otherCount).toString())"
    );
    cmda.run(input, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testMultipleCsvFiles() throws Throwable {
    ExpectedOutput expectedOutput = new ExpectedOutput("Should have expected first column value", "id", "1", "2", "3", "foo", "1", "4");

    Commanda cmda = new Commanda(
            "-csv", testDir + "test-csv.txt", testDir + "test3.txt",
            "-ne", "r -> r.get(0)");
    cmda.run(null, expectedOutput);
    expectedOutput.assertAllFound();
  }

  @Test
  public void testToxmlSink() throws Throwable {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Commanda cmda = new Commanda(
            "-tox", testDir + "test.toxml",
            "-ne", "cr -> cr",
            "-toxOut");
    cmda.run(null, null, baos);

    byte[] inputBytes = Files.readAllBytes(new File(testDir+"test.toxml").toPath());
    String inputString = new String(inputBytes, StandardCharsets.UTF_8).trim();
    byte[] outputBytes = baos.toByteArray();
    String outputString = new String(outputBytes, StandardCharsets.UTF_8).trim();
    Assert.assertEquals("Output file should match input file", inputString, outputString);
  }

  @Test
  public void testToxmlModifier() throws Throwable {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Commanda cmda = new Commanda(
            DefaultLambdaImports.addImports(TypedValue.class),
            DefaultLambdaImports.staticImports,
            "-tox", testDir + "test.toxml",
            "-me", "cr -> { TypedValue id = cr.getIds().addNew(); id.setType(\"reg\"); id.setValue(\"12345\"); }",
            "-toxOut");
    cmda.run(null, null, baos);

    byte[] outputBytes = baos.toByteArray();
    String outputString = new String(outputBytes, StandardCharsets.UTF_8).trim();
    Assert.assertTrue("Should have new id in output", outputString.contains("<Id type=\"reg\">12345</Id>"));
  }

  @Test
  public void testSinkTypeCheck() throws Throwable {
    try {
      new Commanda(
              "-csv", testDir + "test3.txt",
              "-toxOut");
      Assert.fail("Should have thrown exception with mismatched type");
    }
    catch (RuntimeException re) {
      Assert.assertTrue("Should have mentioned type issue in error message", re.getMessage().contains("does not match output type"));
    }
  }

  @Test
  public void testMapTypeCheck() throws Throwable {
    try {
      new Commanda(
              "-tox", testDir + "test.toxml",
              "-csvlines",
              "-toxOut");
      Assert.fail("Should have thrown exception with mismatched type");
    }
    catch (RuntimeException re) {
      Assert.assertTrue("Should have mentioned type issue in error message", re.getMessage().contains("does not match output type"));
    }
  }

  @Test
  public void testLambdaTypeCheck() throws Throwable {
    try {
      new Commanda(
              "-csv", testDir + "test3.txt",
              "-ne", "r -> r",
              "-toxOut");
      Assert.fail("Should have thrown exception with mismatched type");
    }
    catch (RuntimeException re) {
      Assert.assertTrue("Should have mentioned type issue in error message", re.getMessage().contains("Type mismatch: cannot convert"));
    }
  }
}
