import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Vertex {
    public static class Entry {
        public char key;
        public int value;

        public Entry(char key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public List<Entry> nextVertexes;
    public List<Entry> move;
    public boolean flag;
    public int patternIndex;
    public int suffixLink;
    public int goodSuffixLink;
    public int parent;
    public char symbol;

    public Vertex(int parent, char symbol) {
        this.nextVertexes = new ArrayList<>();
        this.move = new ArrayList<>();
        this.flag = false;
        this.patternIndex = -1;
        this.suffixLink = -1;
        this.goodSuffixLink = -1;
        this.parent = parent;
        this.symbol = symbol;
    }

    public int getNextVertex(char key) {
        for (Entry entry : nextVertexes) {
            if (entry.key == key) {
                return entry.value;
            }
        }
        return -1;
    }

    public void putNextVertex(char key, int value) {
        nextVertexes.add(new Entry(key, value));
    }

    public int getMove(char key) {
        for (Entry entry : move) {
            if (entry.key == key) {
                return entry.value;
            }
        }
        return -1;
    }

    public void putMove(char key, int value) {
        move.add(new Entry(key, value));
    }
}

class Bor {
    public List<Vertex> bor;
    public List<String> patterns;
    public List<Pair<Integer, Integer>> result;

    public Bor() {
        this.bor = new ArrayList<>();
        this.bor.add(new Vertex(0, '$'));
        this.patterns = new ArrayList<>();
        this.result = new ArrayList<>();
    }

    public void addToBor(String pattern) {
        int number = 0;
        for (char symbol : pattern.toCharArray()) {
            int nextVertex = bor.get(number).getNextVertex(symbol);
            if (nextVertex == -1) {
                bor.add(new Vertex(number, symbol));
                nextVertex = bor.size() - 1;
                bor.get(number).putNextVertex(symbol, nextVertex);
            }
            number = nextVertex;
        }

        bor.get(number).flag = true;
        patterns.add(pattern);
        bor.get(number).patternIndex = patterns.size() - 1;
    }

    public int getSuffixLink(int vertex) {
        if (bor.get(vertex).suffixLink == -1) {
            if (vertex == 0 || bor.get(vertex).parent == 0) {
                bor.get(vertex).suffixLink = 0;
            } else {
                bor.get(vertex).suffixLink = getMove(getSuffixLink(bor.get(vertex).parent), bor.get(vertex).symbol);
            }
        }
        return bor.get(vertex).suffixLink;
    }

    public int getGoodSuffixLink(int vertex) {
        if (bor.get(vertex).goodSuffixLink == -1) {
            int currentSuffixLink = getSuffixLink(vertex);
            if (currentSuffixLink == 0) {
                bor.get(vertex).goodSuffixLink = 0;
            } else {
                bor.get(vertex).goodSuffixLink = bor.get(currentSuffixLink).flag ? currentSuffixLink : getGoodSuffixLink(currentSuffixLink);
            }
        }
        return bor.get(vertex).goodSuffixLink;
    }

    public int getMove(int vertex, char symbol) {
        int move = bor.get(vertex).getMove(symbol);
        if (move == -1) {
            int nextVertex = bor.get(vertex).getNextVertex(symbol);
            if (nextVertex != -1) {
                move = nextVertex;
            } else {
                move = vertex == 0 ? 0 : getMove(getSuffixLink(vertex), symbol);
            }
            bor.get(vertex).putMove(symbol, move);
        }
        return move;
    }

    public void find(String text) {
        int vertexNumber = 0;
        for (int textIndex = 0; textIndex < text.length(); textIndex++) {
            char symbol = text.charAt(textIndex);
            vertexNumber = getMove(vertexNumber, symbol);
            int vertex = vertexNumber;
            while (vertex != 0) {
                if (bor.get(vertex).flag) {
                    result.add(new Pair<>(textIndex + 2 - patterns.get(bor.get(vertex).patternIndex).length(), bor.get(vertex).patternIndex + 1));
                }
                vertex = getGoodSuffixLink(vertex);
            }
        }
    }

    public List<Pair<Integer, Integer>> getAnswer(String text) {
        find(text);
        result.sort((pair1, pair2) -> {
            int cmp = pair1.getFirst().compareTo(pair2.getFirst());
            if (cmp != 0) {
                return cmp;
            }
            return pair1.getSecond().compareTo(pair2.getSecond());
        });
        return result;
    }
}

class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}

public class AhoKorasick {
    public static void main(String[] args) {
        Bor bor = new Bor();
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        int patternQuantity = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < patternQuantity; i++) {
            String pattern = scanner.nextLine();
            bor.addToBor(pattern);
        }
        List<Pair<Integer, Integer>> result = bor.getAnswer(text);
        for (Pair<Integer, Integer> pair : result) {
            System.out.println(pair.getFirst() + " " + pair.getSecond());
        }
        scanner.close();
    }
}