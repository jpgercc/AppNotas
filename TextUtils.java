import java.util.*;

public class TextUtils {
    
    // Análise de texto em O(n)
    // Conta caracteres, palavras, linhas, frases, caracteres únicos e o mais comum
    public static TextStats analyzeText(String text) {
        if (text.isEmpty()) {
            return new TextStats();
        }
        
        TextStats stats = new TextStats();
        Map<Character, Integer> charCount = new HashMap<>();
        boolean inWord = false;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            stats.characters++;
            
            charCount.put(c, charCount.getOrDefault(c, 0) + 1); 
            
            if (c == '\n') {
                stats.lines++; 
                inWord = false;
            } else if (Character.isWhitespace(c)) {
                inWord = false;
            } else if (!inWord) {
                stats.words++;
                inWord = true;
            }
            
            if (c == '.' || c == '!' || c == '?') {
                stats.sentences++;
            }
        }
        
        // Caractere mais comum
        char mostCommon = ' ';
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            if (entry.getValue() > maxCount && !Character.isWhitespace(entry.getKey())) {
                maxCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }
        
        stats.mostCommonChar = mostCommon;
        stats.mostCommonCount = maxCount;
        stats.uniqueChars = charCount.size();
        
        return stats;
    }

    public static List<Integer> searchText(String text, String searchTerm) {
        List<Integer> results = new ArrayList<>();
        if (text.isEmpty() || searchTerm.isEmpty()) {
            return results;
        }
        
        String lowerText = text.toLowerCase();
        String lowerTerm = searchTerm.toLowerCase();
        
        int index = 0;
        while ((index = lowerText.indexOf(lowerTerm, index)) != -1) {
            results.add(index);
            index++;
        }
        
        return results;
    }


    public static List<String> findDuplicateLines(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] lines = text.split("\n");
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                if (seen.contains(trimmed)) {
                    duplicates.add(trimmed);
                } else {
                    seen.add(trimmed);
                }
            }
        }
        
        return new ArrayList<>(duplicates);
    }
    

    public static class TextStats {
        public int characters = 0;
        public int words = 0;
        public int lines = 1;
        public int sentences = 0;
        public int uniqueChars = 0;
        public char mostCommonChar = ' ';
        public int mostCommonCount = 0;
        
        @Override
        public String toString() {
            return String.format(
                "Estatísticas do Texto:\n\n" +
                "Caracteres: %d\n" +
                "Palavras: %d\n" +
                "Linhas: %d\n" +
                "Frases: %d\n" +
                "Caracteres únicos: %d\n" +
                "Caractere mais comum: '%c' (%d vezes)",
                characters, words, lines, sentences, uniqueChars, 
                mostCommonChar, mostCommonCount
            );
        }
    }
}