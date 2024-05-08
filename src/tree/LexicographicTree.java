package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LexicographicTree {
	private int size = 0;
	private TrieNode root;
	
	//Pour la crypto
    private Map<Integer, List<String>> wordsOfLength;
    
    /*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		size = 0;
		root = new TrieNode();
		wordsOfLength = new HashMap<Integer, List<String>>();
	}
	
	/**
	 * Constructor : creates a lexicographic tree populated with words 
	 * @param filename A text file containing the words to be inserted in the tree 
	 */
	public LexicographicTree(String filename) {
		this();
		
        try(BufferedReader lecteur = new BufferedReader(new FileReader(filename))) { 
            String mot;
            
            while ((mot = lecteur.readLine()) != null) {              	
                insertWord(mot);                                                                      
            }
                       
        } catch (IOException e) {
        	
        }
	}
	
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Returns the number of words present in the lexicographic tree.
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return size; 
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * @param word A word
	 */
	public void insertWord(String word) {
	
		word = clearWord(word);
		
	    int wordLength = word.length();
	    TrieNode current = root;
	    
	    for (int i = 0; i < wordLength; ++i) {
	        char c = word.charAt(i);
	        TrieNode node = current.get(c);

	        if (node == null) {
	            node = new TrieNode();
	            current.put(c, node);
	        }
	        current = node;
	    }

	    if (!current.isEnd()) {
	        current.setEnd();
	        size++;
	    }
	}
	
	
	/**
	 * Determines if a word is present in the lexicographic tree.
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		// TODO		
		TrieNode node = searchPrefix(word);
	    return node != null ? node.isEnd() : false;
	}
	
    /**
     * Returns an alphabetic list of all words starting with the supplied prefix.
     * If 'prefix' is an empty string, all words are returned.
     * @param prefix Expected prefix
     * @return The list of words starting with the supplied prefix
     */
    public List<String> getWords(String prefix) {
    	 List<String> result = new ArrayList<>();
    	 TrieNode node = searchPrefix(prefix);
    	 
    	 StringBuilder sb = new StringBuilder(prefix);
    	 
	     if (node != null) {   
	        dfs(node, sb, result);
	     }
    	    
    	 return result;
    }


    /**
     * Returns an alphabetic list of all words of a given length.
     * If 'length' is lower than or equal to zero, an empty list is returned.
     * @param length Expected word length
     * @return The list of words with the given length
     */
    public List<String> getWordsOfLength(int length) {
    	if(length < 1) {
    		return new ArrayList<>();
    	}
    	
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        dfs(root, sb, 0, length, result);
        
        return result;
    }
    
	/*
	 * PRIVATE METHODS
	 */
	
	// TODO
    
    /**
     * Return le noeud qui correspond à la dernière lettre du prefix donné, si le préfix n'est pas trouvé alors null
     * @param prefix Séquence de lettres qui représente le prefix que l'on recherche
     * @return Le noeud qui représente la dernière lettre du prefix, si préfix non trouvé alors null
     */
    private TrieNode searchPrefix(String prefix) {
        TrieNode current = root;       
        int i = 0;
        
        while (i < prefix.length()) {
            TrieNode node = current.get(prefix.charAt(i));
            if (node == null) {
                return null;
            }
            
            current = node;
            i++;
        }
        
        return current;
    }
    
    /**
     * Méthode qui permet d'ajouter les différents mots à la List result à partir d'un noeud courant
     * @param node Le noeud courant de la recherche
     * @param sb La chaîne de lettres courantes
     * @param result La List de tous les mots trouvés
     */
    private void dfs(TrieNode node, StringBuilder sb, List<String> result) {   	
    	 if (node.isEnd()) {
    	        result.add(sb.toString());
    	    }
    	    
    	    TrieNode[] links = node.getLinks();
    	    if (links == null) {
    	        return;
    	    }

    	    int cpt = 0;
    	    for (TrieNode link : links) {
    	        if (link != null) {
    	            sb.append((char)('a' + cpt));
    	            dfs(link, sb, result);
    	            sb.setLength(sb.length() - 1);
    	        }
    	        cpt++;
    	    }
    }
    

    /**
     * Méthode qui permet d'ajouter les différents mots d'une certaine taille à la List result à partir d'un noeud courant
     * @param node Le noeud courant de la recherche
     * @param sb La chaîne de lettres courantes
     * @param currentLength La taille courante 
     * @param wordsLength La taille des mots souhaités
     * @param result La List de tous les mots trouvés d'une certaine taille
     */
    private void dfs(TrieNode node, StringBuilder sb, int currentLength, int wordsLength, List<String> result) {
    	if (node.isEnd() && currentLength == wordsLength) {
            result.add(sb.toString());
            return;
        }
    	
    	TrieNode[] links = node.getLinks();
        if (currentLength >= wordsLength || links == null) {
            return;
        }
        
        int cpt = 0;
	    for (TrieNode link : links) {
	        if (link != null) {
            	sb.append((char)('a' + cpt));
                dfs(link, sb, currentLength + 1, wordsLength, result);
                sb.setLength(sb.length() - 1);
            }
	        cpt++;
        }       
    }
    
    //Pour la crypto
    
    /**
     * Méthode qui permet de converitr une String en nombre
     * la lettre est remplacée par l'index de la première occurence trouvée pour cette lettre
     * @param word Le mot que l'on souhaite convertir
     * @return Le mot converti sous forme de nombre
     */
    private String convertStringToNumber(String word) {
	    StringBuilder stringBuilder = new StringBuilder();
	    Map<Character, Integer> charIndices = new HashMap<>();

	    for (int i = 0; i < word.length(); i++) {
	        char character = word.charAt(i);

	        if (!charIndices.containsKey(character)) {
	            charIndices.put(character, i + 1);
	        }

	        int index = charIndices.get(character);        
	        stringBuilder.append(index);
	    }

	    return stringBuilder.toString();
	}
    
         
    /**
     * Méthode qui permet de supprimer les caractères non autorisés d'un mot
     * @param word Le mot dont on souhaite retirer les caractères non autorisés
     * @return Le mot avec les caractères non autorisés retirés
     */
	private String clearWord(String word) {
		int wordLenght = word.length();
		StringBuilder cleanedStrBuilder = new StringBuilder(wordLenght);

		for (int i = 0; i < wordLenght; i++) {
		    char c = word.charAt(i);
		    if (Character.isLetter(c) || c == '-' || c == '\'') {
		        cleanedStrBuilder.append(c);
		    }
		}

		String cleanedStr = cleanedStrBuilder.toString();
		
		return cleanedStr;
	}
	
	/**
	 * Méthode qui permet d'ajouter tous les mots d'une certaine taille à une Map
	 * @param wordLength La taille des mots que l'on veut ajouter à la Map
	 */
	private void addWordsOfLength(int wordLength) {
		if (this.wordsOfLength.get(wordLength) == null) {
	        List<String> words = this.getWordsOfLength(wordLength);
	        words.replaceAll(String::toUpperCase);	    
	        wordsOfLength.put(wordLength, words);
	    }
	}
    
    /*
	 * PUBLIC METHODS
	 */ 
    
    //Pour le Boggle
    /**
     * Méthode qui permet de savoir s'il existe des mots qui commencent par le préfix donné
     * @param prefix Le préfix 
     * @return True s'il existe des mots avec ce préfix sinon false
     */
    public boolean existPrefix(String prefix) {
        TrieNode current = root;       
        int i = 0;
        
        while (i < prefix.length()) {
            TrieNode node = current.get(prefix.charAt(i));
            if (node == null) {
                return false;
            }
            
            current = node;
            i++;
        }
        
        return true;
    }
    
    //Pour la crypto
    /**
     * Méthode qui permet de retourner le premier mot compatible avec un autre.
     * Pour que deux mots soient compatibles, il faut qu'ils aient la même répétition de lettres et la même taille. 
     * Si aucun mot n'est trouvé alors on renvoie une chaîne vide.
     * @param cryptedWord Le mot sur base duquel on souhaite obtenir un mot comptable
     * @return Un mot compatabile avec le mot donné, si aucun mot compatible alors null
     */
    public String giveCompatibleWord(String cryptedWord){
    	int wordLenght = cryptedWord.length();
    	addWordsOfLength(wordLenght);
    	  	
    	List<String> compatibleWords = wordsOfLength.get(wordLenght);
    	int lenghtCompatbileWords = compatibleWords.size();
    	
    	for (int i = 0; i < lenghtCompatbileWords; i++) {
    		String deryptedWord = compatibleWords.get(i);
    	    if (convertStringToNumber(deryptedWord).equals(convertStringToNumber(cryptedWord))) {
    	    	compatibleWords.remove(i);
    	        return deryptedWord;
    	    }
    	}
    	
    	return null;
    }
   
    
	/*
	 * TEST FUNCTIONS
	 */
		
	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char)('a' + (int)(number % radix)) + word;
			number = number / radix;
		} while(number != 0);
		return word;
	}
	
	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;
		
		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();
		
		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine();
				    boolean found = dico.containsWord(word);
				    if (!found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine() + "xx";
				    boolean found = dico.containsWord(word);
				    if (found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory()/MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory()/MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory()/MB);
			}
		}
	}
	
	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_sans_accents.txt");
		
		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}
}
