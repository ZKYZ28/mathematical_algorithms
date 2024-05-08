package tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTest {
	private static final String[] WORDS = new String[] {"aide", "as", "au", "aux",
			"bu", "bus", "but", "et", "ete"};
	private static final LexicographicTree DICT = new LexicographicTree();

	@BeforeAll
	private static void initTestDictionary() {
		for (int i=0; i<WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}
	
	/*TEST PROF*/
	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i=0; i<WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}
	
	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] {"", "aid", "ai", "aides", "mot", "e"}) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}
	
	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());
		
		assertEquals(0, DICT.getWords("x").size());
		
		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] {"bu", "bus", "but"}, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] {"aux", "bus", "but", "ete"}, DICT.getWordsOfLength(3).toArray());
	}

	
	/*TEST MOI*/
	@Test
	void constructor_with_invalid_filename() {	
		LexicographicTree lexico = new LexicographicTree("francis");
		assertEquals(0, lexico.size());
	}
	
	@Test
	void valid_constructor() {	
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_test.txt");
		assertTrue(dict.containsWord("francis"));
		assertTrue(dict.containsWord("franchi"));
		assertTrue(dict.containsWord("francho"));
		
		assertFalse(dict.containsWord("francha"));
		assertFalse(dict.containsWord("franchy"));
		assertFalse(dict.containsWord("franchp"));
	}
	
	@Test
	void containsWord_in_another_word() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photo");
		dict.insertWord("photographe");
		
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");	
		assertTrue(dict.containsWord("photographe"), "Mot photographe non trouvé");		
	}
	
	@Test
	void containsWord_update_isWord() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photographe");
		assertFalse(dict.containsWord("photo"), "Mot photo non trouvé");	
		
		
		dict.insertWord("photo");
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");		
	}
	
	@Test
	void containsWord_similar_words() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("photo");
		dict.insertWord("photi");
		dict.insertWord("phatp");
		dict.insertWord("photm");
		dict.insertWord("phots");
		dict.insertWord("phtte");
		
		assertTrue(dict.containsWord("photo"), "Mot photo non trouvé");	
		assertTrue(dict.containsWord("photi"), "Mot photi non trouvé");
		assertTrue(dict.containsWord("phatp"), "Mot phatp non trouvé");
		assertTrue(dict.containsWord("photm"), "Mot photm non trouvé");
		assertTrue(dict.containsWord("phots"), "Mot phots non trouvé");
		assertTrue(dict.containsWord("phtte"), "Mot phtte non trouvé");
		
		assertFalse(dict.containsWord("photx"), "Mot photx inexistant trouvé");
		assertFalse(dict.containsWord("phytz"), "Mot phytz inexistant trouvé");
		assertFalse(dict.containsWord("fhotx"), "Mot fhotx inexistant trouvé");
		assertFalse(dict.containsWord("pmotx"), "Mot pmotx inexistant trouvé");
		assertFalse(dict.containsWord("photx"), "Mot photx inexistant trouvé");
	}
	
	@Test
	void containsWord_in_empty_tree() {
		LexicographicTree dict = new LexicographicTree();
		
		assertFalse(dict.containsWord("photo"), "Mot photo inexistant trouvé");	
		assertFalse(dict.containsWord(""), "Mot inexistant trouvé");	
	}
	
	
	//INSERT 	
	@Test
	void insert_with_invald_caracters() {
		LexicographicTree dict = new LexicographicTree();
			
		dict.insertWord("t@ac@o+s");
		assertEquals(1, dict.size());
		assertTrue(dict.containsWord("tacos"));
	}
	
	
	@Test
	void insert_same_word_severals_times() {
		LexicographicTree dict = new LexicographicTree();
		
		for (int i = 0; i < 10; i++) {
			dict.insertWord("francis");
		}
		
		assertEquals(1, dict.size());		
	}
	
	@Test
	void insert_empty() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("");
		assertEquals(1, dict.size());
		assertTrue(dict.containsWord(""));
	}
	
	@Test
	void insert_good_case() {
		LexicographicTree dict = new LexicographicTree();
		
		assertEquals(0, dict.size());
		assertFalse(dict.containsWord("tacos"));
		
		dict.insertWord("tacos");
		assertEquals(1, dict.size());
		assertTrue(dict.containsWord("tacos"));
	}
		
	@Test
	void insert_long_word() {
		LexicographicTree dict = new LexicographicTree();
		
		dict.insertWord("anticonstitutionnellement");
		assertEquals(1, dict.size());
		assertTrue(dict.containsWord("anticonstitutionnellement"));
	}
	
	@Test
	void getWords_sorted() {
		LexicographicTree dict = new LexicographicTree();		
		dict.insertWord("zo");
		dict.insertWord("za");
		dict.insertWord("zu");
		dict.insertWord("zi");
		
		assertArrayEquals(new String[] {"za", "zi", "zo", "zu"}, dict.getWords("z").toArray());
	}
	
	@Test
	void getWords_empty() {		
		assertArrayEquals(new String[] {"aide", "as", "au", "aux","bu", "bus", "but", "et", "ete"}, DICT.getWords("").toArray());
	}
	
	@Test
	void getWords_strange_characters() {		
		assertArrayEquals(new String[] {}, DICT.getWords("ého@").toArray());
	}
	
	//getWordsOfLength
	@Test
	void getWordsOfLength_too_many() {
		assertEquals(0, DICT.getWordsOfLength(100).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(100).toArray());	
	}
	
	@Test
	void getWordsOfLength_null() {
		assertEquals(0, DICT.getWordsOfLength(0).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(0).toArray());	
	}
	
	@Test
	void getWordsOfLength_negative() {
		assertEquals(0, DICT.getWordsOfLength(-5).size());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(0).toArray());	
	}
	
	@Test
	void getWordsOfLength_sorted() {
		assertArrayEquals(new String[] {"aux", "bus", "but", "ete"}, DICT.getWordsOfLength(3).toArray());	
	}
	
	@Test
	void getWordsOfLength_sorted_with_unsorted_insertion() {
		LexicographicTree dict = new LexicographicTree();		
		dict.insertWord("zo");
		dict.insertWord("za");
		dict.insertWord("zu");
		dict.insertWord("zi");
		assertArrayEquals(new String[] {"za", "zi", "zo", "zu"}, dict.getWordsOfLength(2).toArray());	
	}
	
	@Test
	void getWordsOfLength_one_found() {
		assertEquals(1, DICT.getWordsOfLength(4).size());
		assertArrayEquals(new String[] {"aide"}, DICT.getWordsOfLength(4).toArray());	
	}
	
	@Test
	void getWordsOfLength_normal_case() {
		assertEquals(4, DICT.getWordsOfLength(2).size());
		assertArrayEquals(new String[] {"as", "au", "bu", "et"}, DICT.getWordsOfLength(2).toArray());	
	}
	
	@Test
	void existPrefix_true() {
		LexicographicTree dict = new LexicographicTree();	
		dict.insertWord("zoo");
		
		assertTrue(dict.existPrefix("zo"));
	}
	
	@Test
	void existPrefix_false() {
		LexicographicTree dict = new LexicographicTree();	
		dict.insertWord("zaza");
		
		assertFalse(dict.existPrefix("zo"));
	}
	
	@Test
	void existPrefix_with_empty_tree() {
		LexicographicTree dict = new LexicographicTree();	
		
		assertFalse(dict.existPrefix("zo"));
	}
	
	@Test
	void existPrefix_with_multiple_results() {
		LexicographicTree dict = new LexicographicTree();	
		dict.insertWord("zaza");
		dict.insertWord("zazo");
		dict.insertWord("zazu");
		dict.insertWord("zazi");
		dict.insertWord("zazp");
		
		assertTrue(dict.existPrefix("za"));
	}
	
	@Test
	void giveCompatibleWord_good_case_simple() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_test.txt");
			
		assertEquals("PAPA", dict.giveCompatibleWord("LOLO"));
	}
	
	@Test
	void giveCompatibleWord_good_case_complex() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_test.txt");
			
		assertEquals("APAAFLGJJDDSKKS", dict.giveCompatibleWord("BSBBCQVTTFFXIIX".toLowerCase()));
	}
	
	@Test
	void giveCompatibleWord_not_found() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_test.txt");
			
		assertEquals(null, dict.giveCompatibleWord("pppp".toLowerCase()));
	}
	
	@Test
	void giveCompatibleWord_remove_findWord() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_test.txt");
			
		assertEquals("APAAFLGJJDDSKKS", dict.giveCompatibleWord("BSBBCQVTTFFXIIX".toLowerCase()));
		assertEquals(null, dict.giveCompatibleWord("BSBBCQVTTFFXIIX".toLowerCase()));
	}
}
