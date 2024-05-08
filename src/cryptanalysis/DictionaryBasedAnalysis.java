package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_sans_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock

	private final LexicographicTree dict;
	private Set<String> motsChiffres;	
	private LinkedList<String> wordsNotFound;
	private char alphabet[];
	
	/*
	 * CONSTRUCTOR
	 */
	/**
	 * Constucteur de DictionaryBasedAnalysis 
	 * @param cryptogram Un cryptogram 
	 * @param dict Le dictionnaire dans lequel on doit chercher les mots du cryptogram
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		// TODO
		if(cryptogram.isEmpty()) {
			throw new IllegalArgumentException("Le cryptogram ne peut pas être vide");
		}
		
		this.dict = dict;
		this.alphabet = new char[26];	
		this.alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		this.wordsNotFound = new LinkedList<String>();			
			
		giveAllWords(cryptogram);
		wordsNotFound.addAll(motsChiffres); 
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an approximated decoding alphabet.
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		// TODO					
		this.alphabet = alphabet.toCharArray();
		int repeat = 0;		
	    String previousWord = wordsNotFound.getFirst();    
	    int nbrWordsFoundBefore = countNumberWordFound();
	    int nbrWordsFoundAfter = 0;
	    char[] previousAlphabet = new char[26];
	    
	    
	    while (!wordsNotFound.isEmpty()) {
		    previousWord = wordsNotFound.getFirst();
	    	
	        // MISE A JOUR DE L'ALPHABET
	        if (updateAlphabet(previousWord, previousAlphabet)) {

	            // VERIFICATION DE L'AMELIORATION
	            nbrWordsFoundAfter = countNumberWordFound();
	            
	            if (nbrWordsFoundBefore > nbrWordsFoundAfter) {
	                System.arraycopy(previousAlphabet, 0, this.alphabet, 0, 26);		               
	            } else {
	                nbrWordsFoundBefore = nbrWordsFoundAfter;
	            }
	            
	        }
	       
	        if(repeat == 3) {
	        	wordsNotFound.removeFirst();
	        	repeat = 0;	        	        
	        }
	       	
			if(wordsNotFound.isEmpty()) {
				return new String(this.alphabet);
			}
	        
	        if(wordsNotFound.getFirst().equals(previousWord)) {
	        	repeat++;
	        }
	    }


        
	    return new String(this.alphabet);	
	}	         
	
	/**
	 * Applies an alphabet-specified substitution to a text.
	 * @param text A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		// TODO	
		 if (alphabet.isEmpty() || text.isEmpty()) {
		        throw new IllegalArgumentException("L'alphabet ou/et le texte ne peuvent pas être vides");
		 }

	    char[] alphabetTab = alphabet.toCharArray();

	    StringBuilder sb = new StringBuilder(text.length());
	    for (char c : text.toCharArray()) {
	        if (c == ' ') {
	            sb.append(c);
	            continue;
	        }

	        int index = c - 'A';
	        if (index >= 0 && index < 26) {
	            char substitution = Character.isLowerCase(c) ? Character.toLowerCase(alphabetTab[index]) : alphabetTab[index];
	            sb.append(substitution);
	        } else {
	            sb.append(c);
	        }
	    }

	    return sb.toString();
	}
	
	
	/*
	 * PRIVATE METHODS
	 */
	/**
	 * Compares two substitution alphabets.
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	/**
	 * Load the text file pointed to by pathname into a String.
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Méthode qui permet de placer tous les mots du texte chiffré dans 
	 * motsChiffres par ordre de taille. On n'ajoute que les mots qui font 3 caractères
	 * ou plus et qui ont des lettres dupliquées.
	 * @param cryptogram Le texte chiffré
	 */
	private void giveAllWords(String cryptogram) {
	    String[] mots = cryptogram.split("[\\s\\n]+");

	    Comparator<String> stringLengthComparator = (s1, s2) -> {
	        int lengthComparison = Integer.compare(s2.length(), s1.length());
	        return lengthComparison != 0 ? lengthComparison : s1.compareTo(s2);
	    };

	    this.motsChiffres = new TreeSet<>(stringLengthComparator);
	    for (String mot : mots) {
	        if (mot.length() >= 3 && containsDuplicateLetters(mot)) {
	        	mot = mot.replaceAll("[-']"," ");	 
	        	motsChiffres.add(mot);		        		    	               
	        }
	    }
	    
	}
	
	/**
	 * Méthode qui permet de mettre à jour l'alphabet de décryptage
	 * @param word le mot crypté que l'on souhaite décrypter
	 * @param previousAlphabet L'alphabet utilisé avant de le mettre à jour
	 * @return True si l'alphabet a été mis à jour sinon false
	 */
	private boolean updateAlphabet(String word, char[] previousAlphabet) {
	    String compatibleWord = dict.giveCompatibleWord(word);
	    
	    //Si pas de mot comptabile alors on ne fait pas le traitement
	    if (compatibleWord == null) {
	        wordsNotFound.remove(word);
	        return false;
	    }

	    //Ici pour ne pas le faire dans tous les cas
	    System.arraycopy(this.alphabet, 0, previousAlphabet, 0, 26);

	    // Remplacement de l'aphabet
	    char[] wordTab = word.toCharArray();
	    char[]  compatibleWordTab = compatibleWord.toCharArray();
	    String alphabetString = new String(this.alphabet);
	    char temp = '%';
	    
	    for (int i = 0; i < compatibleWord.length(); i++) {
	        char currentLetter = wordTab[i];

	        char oldValue = this.alphabet[currentLetter - 'A'];
	        char newValue = compatibleWordTab[i];

	        if (oldValue != newValue) {
	        	this.alphabet[currentLetter - 'A'] = newValue;
	        	     
	        	//Permutation des lettres
	        	alphabetString = alphabetString.replace(oldValue, temp)
	        	.replace(newValue, oldValue)
	        	.replace(temp, newValue);
	        	this.alphabet = alphabetString.toCharArray();	        		     
	        }	        	        
	    }
	    
	    //Vérification de si l'alphabet à changé
	    if (Arrays.equals(previousAlphabet, this.alphabet)) {
	        wordsNotFound.remove(word);
	        return false;
	    }
	    
	    return true;
	}
	
	/**
	 * Méthode qui permet de trouver le nombre de mots décrypté. 
	 * @return Le nombre de mots déchiffré avec l'alphabet courant
	 */
	private int countNumberWordFound() {
	    int nbMotsReconnus = 0;
	    
	    for (String motChiffre : this.motsChiffres) {
	    	String motDechiffre = applySubstitution(motChiffre);
	    	
 	        if (this.dict.containsWord(motDechiffre)) {
 	        	wordsNotFound.remove(motChiffre);      	
 	        	nbMotsReconnus++;
 	        }	        
	    }    
	    return nbMotsReconnus;
	}
	
	/**
	 * Méthode qui permet de déchiffrer un mot sur base d'un alphabet
	 * @param mot Le mot que l'on souhaite déchiffrer
	 * @return Le mot déchiffré
	 */
	private String applySubstitution(String mot) {
	    char[] decryptedWord = new char[mot.length()];
	    char[] cryptedWordTab = mot.toCharArray();
	    
	    for (int i = 0; i < mot.length(); i++) {
	        decryptedWord[i] = alphabet[cryptedWordTab[i] - 'A'];
	    }
	    
	    return new String(decryptedWord).toLowerCase();
	}

	/**
	 * Méthode qui permet de savoir si un mot contient des lettres dupliquées
	 * @param word String le mot dont on souhaite savoir s'il contient des lettres dupliquées
	 * @return True si le mot contient des lettres dupliquées sinon false
	 */
	private boolean containsDuplicateLetters(String word) {
		boolean[] dejaVue = new boolean[26];
	    
	    for (int i = 0; i < word.length(); i++) {
	        char c = word.charAt(i);
	        int index = c - 'A';
	        
	        if (dejaVue[index]) {
	            return true;
	        }
	       
	        dejaVue[index] = true;
	    }
	    
	    return false;
	}
	
    /*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();
		
		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
		System.out.println();

		/*
		 *  Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		String startAlphabet = LETTERS;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		long avantAphabet = System.currentTimeMillis();
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		long solveTime = System.currentTimeMillis();
		
		System.out.println("Duration : " + (solveTime - avantAphabet)/1000.0);
		
		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();
		
		// Display decoded text
		//System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}
