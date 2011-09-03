
package no.priv.garshol.duke.test;

import org.junit.Test;
import org.junit.Before;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import junit.framework.AssertionFailedError;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;

import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.Database;
import no.priv.garshol.duke.RecordImpl;
import no.priv.garshol.duke.Deduplicator;
import no.priv.garshol.duke.AbstractMatchListener;
import no.priv.garshol.duke.comparators.ExactComparator;

public class DeduplicatorTest {
  private Deduplicator dedup;
  private Database db;
  private TestListener listener;
  
  @Before
  public void setup() {
    listener = new TestListener();
    ExactComparator comp = new ExactComparator();
    Collection<Property> props = new ArrayList();
    props.add(new Property("ID"));
    props.add(new Property("NAME", comp, 0.3, 0.8));
    props.add(new Property("EMAIL", comp, 0.3, 0.8));
    db = new Database(null, props, 0.85, 0.8, false);
    db.addMatchListener(listener);
    dedup = new Deduplicator(db);
    db.openIndexes(false);
  }
  
  @Test
  public void testEmpty() throws IOException {
    dedup.process(new ArrayList());
    assertEquals(0, listener.getMatches().size());
    assertEquals(0, listener.getRecordCount());
  }
  
  @Test
  public void testNoProperties() throws IOException {
    Collection<Record> records = new ArrayList();
    records.add(makeRecord());
    records.add(makeRecord());
    dedup.process(records);
    assertEquals(0, listener.getMatches().size());
    assertEquals(2, listener.getRecordCount());
  }
  
  @Test
  public void testDoesNotMatch() throws IOException {
    Collection<Record> records = new ArrayList();
    records.add(makeRecord("ID", "1", "NAME", "A"));
    records.add(makeRecord("ID", "2", "NAME", "B"));
    dedup.process(records);
    assertEquals(0, listener.getMatches().size());
    assertEquals(2, listener.getRecordCount());
  }
  
  @Test
  public void testDoesNotMatchEnough() throws IOException {
    Collection<Record> records = new ArrayList();
    records.add(makeRecord("ID", "1", "NAME", "A"));
    records.add(makeRecord("ID", "2", "NAME", "A"));
    dedup.process(records);
    assertEquals(0, listener.getMatches().size());
    assertEquals(2, listener.getRecordCount());
  }
  
  //@Test //FIXME: why does this fail?
  public void testMatches() throws IOException {
    Collection<Record> records = new ArrayList();
    records.add(makeRecord("ID", "1", "NAME", "AA", "EMAIL", "BB"));
    records.add(makeRecord("ID", "2", "NAME", "AA", "EMAIL", "BB"));
    dedup.process(records);

    assertEquals(2, listener.getRecordCount());
    Collection<Pair> matches = listener.getMatches();
    assertEquals(2, matches.size());
  }

  // --- Utilities

  private Record makeRecord() {
    return new RecordImpl(new HashMap());    
  }

  private Record makeRecord(String p1, String v1, String p2, String v2) {
    return makeRecord(p1, v1, p2, v2, null, null);
  }

  private Record makeRecord(String p1, String v1, String p2, String v2,
                            String p3, String v3) {
    HashMap props = new HashMap();
    props.put(p1, Collections.singleton(v1));
    props.put(p2, Collections.singleton(v2));
    if (v3 != null)
      props.put(p3, Collections.singleton(v3));
    return new RecordImpl(props);
  }
  
  static class TestListener extends AbstractMatchListener {
    private Collection<Pair> matches;
    private int records;

    public TestListener() {
      this.matches = new ArrayList();
    }
    
    public Collection<Pair> getMatches() {
      return matches;
    }

    public int getRecordCount() {
      return records;
    }

    public void startRecord(Record r) {
      records++;
    }
    
    public void matches(Record r1, Record r2, double confidence) {
      matches.add(new Pair(r1, r2));
    }
    
  }
  
  static class Pair {
    private Record r1;
    private Record r2;

    public Pair(Record r1, Record r2) {
      this.r1 = r1;
      this.r2 = r2;
    }
  }
}