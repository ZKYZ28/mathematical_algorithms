package tree;

public class TrieNode {
		private TrieNode[] links;
		private boolean isEnd;
	
		/*
		 * CONSTRUCTEUR
		 */
		public TrieNode() {
		   
		}
		
		/**
		 * Méthode qui return un noeud qui correspond à la lettre donnée, 
		 * si la lettre ne fait pas partie des enfants du noeud alors on renvoie null
		 * @param c La lettre dont on souhaite le noeud
		 * @return Le noeud qui correspond à la lettre, sinon trouvé alors null
		 */
		public TrieNode get(char c) {
			try {
				 return links[getIndex(c)];
			}catch(Exception e) {
				return null;
			}
			
		}
		
		public TrieNode[] getLinks() {
			return this.links;
		}
	
		/**
		 * Méthode qui permet d'ajouter une lettre aux enfants du noeud. 
		 * Si l'index de la lettre est supérieur à la taille du tableau alors on recrée un tableau plus grand
		 * de manière à pouvoir l'inclure.
		 * @param c La lettre que l'on souhaite afficher
		 * @param node Le noeud qui représente la lettre
		 */
		public void put(char c, TrieNode node) {
			if(this.links == null) {
				 links = new TrieNode[2];
			}
			
		    if(getIndex(c) >= links.length) {
		        TrieNode[] newLinks = new TrieNode[getIndex(c) + 1];
		        System.arraycopy(links, 0, newLinks, 0, links.length);
		        links = newLinks;      	        
		    } 		
			
			links[getIndex(c)] = node;
		}
	
		public void setEnd() {
		    isEnd = true;
		}
	
		public boolean isEnd() {
		    return isEnd;
		}
		
	    /**
	     * Méthode qui permet de récupérer l'index d'une lettre
	     * @param c La lettre dont on souhaite l'index
	     * @return L'index correspondant à la lettre
	     */
		private int getIndex(char c) {
		    if (c == '-') {
		        return 26;
		    } else if (c == '\'') {
		        return 27;
		    } else {
		        return c - 'a';
		    }
		}
}
