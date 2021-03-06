package org.thunlp.text;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.thunlp.text.Lexicon.Word;

public class LexiconTest extends TestCase {
	public void testBasic() {
		Lexicon l = new Lexicon();
		String[] doc1 = { "this", "is", "a", "good", "day" };
		String[] doc2 = { "Bob", "is", "shit" };
		String[] doc3 = { "this", "is", "my", "day", "is", "a", "day" };

		l.addDocument(doc1);
		l.addDocument(doc2);
		l.addDocument(doc3);

		Assert.assertEquals(8, l.getSize());
		Word t = l.getWord("is");
		Assert.assertNotNull(t);
		Assert.assertEquals(4, t.getFrequency());
		Assert.assertEquals(3, t.getDocumentFrequency());
		Assert.assertEquals("is", t.getName());
		int termid = t.getId();

		Word t1 = l.getWord(termid);
		Assert.assertEquals(t1.getName(), t.getName());
	}

	public void testUnknownTerm() {
		Lexicon l = new Lexicon();
		String[] doc1 = { "this", "is", "a", "good", "day" };
		String[] doc2 = { "Bob", "is", "shit" };
		l.addDocument(doc1);
		Word[] tv1 = l.convertDocument(doc2);
		Word[] tv2 = l.convertDocument(doc2);
		Assert.assertEquals(tv1.length, tv2.length);
		for (int i = 0; i < tv1.length; i++) {
			Assert.assertEquals(tv1[i].getId(), tv2[i].getId());
		}
	}

	public void testSaveLoad() throws IOException {
		File lexiconFile = File.createTempFile("textutils", "txt");
		Lexicon l = new Lexicon();
		String[] doc1 = { "this", "is", "a", "good", "day" };
		String[] doc2 = { "Bob", "is", "shit" };
		String[] doc3 = { "this", "is", "my", "day", "is", "a", "day" };

		l.addDocument(doc1);
		l.addDocument(doc2);
		l.addDocument(doc3);

		boolean ret;

		ret = l.saveToFile(lexiconFile);
		Assert.assertTrue(ret);

		Lexicon l1 = new Lexicon();
		ret = l1.loadFromFile(lexiconFile);
		Assert.assertTrue(ret);

		Assert.assertEquals(l.getSize(), l1.getSize());
		Word t1 = l.getWord("day");
		Word t2 = l1.getWord("day");
		Assert.assertEquals(t1, t2);
	}

	public void testMap() {
		Lexicon l = new Lexicon();
		String[][] docs = { { "a", "b", "c", "d" }, { "a", "b", "d", "e" }, { "a", "d", "f" } };
		for (String[] doc : docs) {
			l.addDocument(doc);
		}

		Hashtable<Integer, Integer> trans = new Hashtable<Integer, Integer>();
		trans.put(0, 2);
		trans.put(1, 0);

		Lexicon newl = l.map(trans);

		Assert.assertEquals(2, newl.getSize());
		Assert.assertEquals(l.getNumDocs(), newl.getNumDocs());
		Assert.assertEquals(2, newl.getWord(l.getWord(0).getName()).getId());
		Assert.assertEquals(0, newl.getWord(l.getWord(1).getName()).getId());
	}

	public void testRemoveRareWords() {
		Lexicon l = new Lexicon();
		String[][] docs = { { "a", "b", "c", "d" }, { "a", "b", "d", "e" }, { "a", "d", "f" } };
		for (String[] doc : docs) {
			l.addDocument(doc);
		}

		Lexicon newl = l.removeLowCoverageWords(0.5);

		Assert.assertEquals(3, newl.getSize());
		Assert.assertNotNull(newl.getWord("a"));
		Assert.assertNotNull(newl.getWord("b"));
		Assert.assertNotNull(newl.getWord("d"));
		Assert.assertNull(newl.getWord("c"));
		Assert.assertNull(newl.getWord("e"));
		Assert.assertNull(newl.getWord("f"));
	}

	public void testRemoveStopwords() {
		Lexicon l = new Lexicon();
		String[][] docs = { { "a", "b", "c", "d" }, { "a", "b", "d", "e" }, { "a", "d", "f" } };
		for (String[] doc : docs) {
			l.addDocument(doc);
		}

		HashSet<String> stopwords = new HashSet<String>();
		stopwords.add("a");
		stopwords.add("c");
		Lexicon newl = l.removeStopwords(stopwords);

		Assert.assertEquals(4, newl.getSize());
		Assert.assertNull(newl.getWord("a"));
		Assert.assertNotNull(newl.getWord("b"));
		Assert.assertNotNull(newl.getWord("d"));
		Assert.assertNull(newl.getWord("c"));
		Assert.assertNotNull(newl.getWord("e"));
		Assert.assertNotNull(newl.getWord("f"));
	}

	public void testRemoveOov() {
		Lexicon l = new Lexicon();
		String[][] docs = { { "a", "b", "c", "d" }, { "a", "b", "d", "e" }, { "a", "d", "f" } };
		for (String[] doc : docs) {
			l.addDocument(doc);
		}

		String[] newdoc = { "a", "c", "g", "t" };
		String[] newdoc1 = l.removeOov(newdoc);
		Assert.assertEquals(2, newdoc1.length);
		Assert.assertEquals("a", newdoc1[0]);
		Assert.assertEquals("c", newdoc1[1]);
	}
}
