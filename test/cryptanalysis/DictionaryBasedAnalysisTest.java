package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;


public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;

	
	
	@BeforeAll
	private static void initTestDictionary() {
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	//TEST MOI
	@Test
	void construcroWithInvalidCryptogram() {
		  assertThrows(IllegalArgumentException.class, () -> new DictionaryBasedAnalysis("", dictionary));
	}
	
	//guessApproximatedAlphabet
	
	@Test
	void guessApproximatedAlphabetTest_AllLetters() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Toutes les lettres n'ont pas été trouvées [" + score + "]");
	}
	
	
	@Test
	void guessApproximatedAlphabetTest_ShortWord() {
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("BPB ", dictionary);
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 1, "Score inférieur à 1 :  [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetTest_AllGoodLetters() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		
		assertNotNull(dba);
		
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score == 26, "Score inférieur à 26 :  [" + score + "]");
	}
	
	@Test
    void guessApproximatedAlphabetTest_AllGoodLetters_SmallCryptogram() {
        DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("OMCCYRM ", dictionary);

        assertNotNull(dba);

        String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);

        int score = 0;
        for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
            if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
        }
        assertTrue(score == 26, "Score inférieur à 15 :  [" + score + "]");
    }
	
	@Test
    void guessApproximatedAlphabetTest_OneWord() {
		LexicographicTree dictionary = new LexicographicTree();
		dictionary.insertWord("CONSCIENCIEUSEMENT".toLowerCase());
		
        DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("SJBCSFMBSFMLCMOMBD\n", dictionary);
        
        assertNotNull(dba);
        String alphabet = dba.guessApproximatedAlphabet(LETTERS);
        
        int score = 0;
        for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
            if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
        }
       
        assertEquals("ANSTJIGHFOKUEBMPQRCDLVWXYZ", alphabet);
        assertTrue(score == 10, "Score inférieur à 10 :  [" + score + "]");
    }
	
	@Test
	void guessApproximatedAlphabet_MatchOneWord() {
		LexicographicTree dict = new LexicographicTree();
		dict.insertWord("concordance");
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis("XPSXPTVWSXF\n", dict);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		
		assertEquals('C', alphabet.charAt('X'-'A'));
		assertEquals('O', alphabet.charAt('P'-'A'));
		assertEquals('N', alphabet.charAt('S'-'A'));
		assertEquals('R', alphabet.charAt('T'-'A'));
		assertEquals('D', alphabet.charAt('V'-'A'));
		assertEquals('A', alphabet.charAt('W'-'A'));
		assertEquals('E', alphabet.charAt('F'-'A'));
	}
	
	//applySubstitution
	@Test
	void applySubstitutionEmptyAlphabet() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("ZAZA", ""));
	}
	
	@Test
	void applySubstitutionEmptyMessage() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("", ENCODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionEmptyMessageAndAlphabet() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("", ""));
	}
	
	
	@Test
	void applySubstitutionOneLetter() {
		String message = "M";
		String encoded = "O";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionAllSameLetters() {
		String message = "EEEEEEE";
		String encoded = "MMMMMMM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionTest_spaces() {
		String message = "MESSAGE MESSAGE MESSAGE MESSAGE MESSAGE";
		String encoded = "OMCCYRM OMCCYRM OMCCYRM OMCCYRM OMCCYRM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionTest_points() {
		String message = "MESSAGE.MESSAGE..MESSAGE...MESSAGE";
		String encoded = "OMCCYRM.OMCCYRM..OMCCYRM...OMCCYRM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionTest_accents_notDecoded() {
	    String message = "É";
	    String encoded = "É";
	    assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
	    assertEquals(message.toUpperCase(), DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET).toUpperCase());
	}
	
	@Test
	void applySubstitutionTest_invalidCharacter_notDecoded() {
	    String message = "@";
	    String encoded = "@";
	    assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
	    assertEquals(message.toUpperCase(), DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET).toUpperCase());
	}
	
	@Test
	void applySubstitutionTest_GoodCase() {
	    String message = "GOOD MESSAGE";
	    String encoded = "RJJU OMCCYRM";
	    assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
	    assertEquals(message.toUpperCase(), DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET).toUpperCase());
	}
}
