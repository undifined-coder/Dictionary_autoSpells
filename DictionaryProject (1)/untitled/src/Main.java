import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();//Data structure Use for storing words
    boolean isEndOfWord = false; // Marks if the node represents the end of a word
}

class Dictionary {
    private static final int MAX_SUGGESTIONS = 10;//To Print only upto 10 suggestions in autocomplete
    private final TrieNode root;

    public Dictionary() {
        root = new TrieNode();
    }

    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        dictionary.loadWordsFromFile("list.txt");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter a word to search (or press Enter to exit): ");
                String userInput = scanner.nextLine();

                if (userInput.isEmpty()) {
                    break;// Exit loop if input is empty

                }

                List<String> suggestions = dictionary.getSuggestions(userInput);
                if (suggestions.isEmpty() || !suggestions.contains(userInput)) {
                    System.out.println("Word not found! Did you mean: " + suggestions);
                } else {
                    System.out.println("Word found: " + userInput);
                }
            }
        } catch (Exception e) {
            System.out.println("Error while reading input");
        }
    }

    // Function to insert a word into the trie

    //Time Complexity: O(n), where n is the length of the word.
    //Space Complexity: O(n). In the worst case, we may need to create a new TrieNode for each character in the word.


    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        TrieNode currentWordNode = root;
        for (char letter : word.toCharArray()) {
            currentWordNode = currentWordNode.children.computeIfAbsent(letter, trieNode -> new TrieNode());
        }
        currentWordNode.isEndOfWord = true;
    }

    // Function to search for a word in the trie

    //Time Complexity: O(n), where n is the length of the word.
    //Space Complexity: O(1). No extra space is used except for a few variables (constant space)

    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        TrieNode currentWordNode = root;
        for (char letter : word.toCharArray()) {
            TrieNode node = currentWordNode.children.get(letter);
            if (node == null) {
                return false; // Word not found
            }
            currentWordNode = node;
        }
        return currentWordNode.isEndOfWord;
    }

    // Function to suggest words in case of spelling mistakes (auto-complete)

    //Time Complexity: O(m + k), where m is the length of the prefix,
    //and k is the number of nodes traversed in the subtree starting from the last character of the prefix.

    //Space Complexity: O(k), where k is the number of nodes traversed to find the suggestions.
    //This is used to store the suggestions list.

    public List<String> getSuggestions(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>();
        }
        TrieNode current = root;
        for (char letter : prefix.toCharArray()) {
            TrieNode node = current.children.get(letter);
            if (node == null) {
                return new ArrayList<>(); // No suggestions if prefix not found
            }
            current = node;
        }
        return autoComplete(current, prefix, new ArrayList<>());
    }

    // Helper function to auto-complete a prefix

    //Time Complexity: O(k), where k is the number of nodes traversed in the subtree starting from the given node.
    //Space Complexity: O(k).

    private List<String> autoComplete(TrieNode node, String prefix, List<String> suggestions) {
        if (suggestions.size() >= MAX_SUGGESTIONS) {
            return suggestions;
        }
        if (node.isEndOfWord) {
            suggestions.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            autoComplete(entry.getValue(), prefix + entry.getKey(), suggestions);
        }
        return suggestions;
    }

    //Helper function to read and insert words from list.txt

    //Time Complexity: O(N * m), where N is the number of words in the file, and m is the average length of the words.
    //Space Complexity: O(total characters).


    public void loadWordsFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String word;
            while ((word = br.readLine()) != null) {
                insert(word.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}