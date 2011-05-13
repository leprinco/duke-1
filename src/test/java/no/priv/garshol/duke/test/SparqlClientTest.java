
package no.priv.garshol.duke.test;

import org.junit.Test;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import junit.framework.AssertionFailedError;

import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.InputSource;

import no.priv.garshol.duke.SparqlClient;

public class SparqlClientTest {

  @Test
  public void testEmpty() throws IOException {
    List<String[]> results = load("sparql-empty.xml");
    assertEquals(0, results.size());
  }

  @Test
  public void testOneRow() throws IOException {
    List<String[]> results = load("sparql-onerow.xml");
    assertEquals(1, results.size());
    String[] row = results.get(0);
    assertEquals(1, row.length);
    assertEquals("1", row[0]);
  }

  @Test
  public void testOneRow2Col() throws IOException {
    List<String[]> results = load("sparql-onerow2col.xml");
    assertEquals(1, results.size());
    String[] row = results.get(0);
    assertEquals(2, row.length);
    assertEquals("1", row[0]);
    assertEquals("http://example.org", row[1]);
  }

  @Test
  public void testTwoRow2Col() throws IOException {
    List<String[]> results = load("sparql-tworow2col.xml");
    assertEquals(2, results.size());
    String[] row = results.get(0);
    assertEquals(2, row.length);
    assertEquals("1", row[0]);
    assertEquals("http://example.org", row[1]);
    row = results.get(1);
    assertEquals(2, row.length);
    assertEquals("2", row[0]);
    assertEquals("http://example.com", row[1]);    
  }

  @Test
  public void testBnode() throws IOException {
    List<String[]> results = load("sparql-bnode.xml");
    assertEquals(1, results.size());
    String[] row = results.get(0);
    assertEquals(1, row.length);
    assertEquals("r2", row[0]);
  }
  
  private List<String[]> load(String file) throws IOException {
    return SparqlClient.loadResultSet(getStream(file));
  }
  
  private InputSource getStream(String file) throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return new InputSource(cl.getResourceAsStream(file));
  }
}