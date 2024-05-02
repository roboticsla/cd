import java.util.*;


public class LeadingTrailing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int ruleCount;
        ProductionRule[] rules;

        System.out.print("Enter the number of production rules: ");
        ruleCount = scanner.nextInt();
        rules = new ProductionRule[ruleCount];

        System.out.println("Enter the production rules (in the form 'left->right'): ");
        for (int i = 0; i < ruleCount; i++) {
            String temp = scanner.next();
            int pos = temp.indexOf("->");
            String token1 = temp.substring(0, pos);
            String token2 = temp.substring(pos + 2);
            rules[i] = new ProductionRule();
            rules[i].left = token1;
            rules[i].right = token2;
        }

        Map<String, Set<Character>> leading = new HashMap<>();
        Map<String, Set<Character>> trailing = new HashMap<>();

        for (ProductionRule rule : rules) {
            leading.put(rule.left, new HashSet<>());
            trailing.put(rule.left, new HashSet<>());
        }

        for (ProductionRule rule : rules) {
            Set<Character> leadingSet = leading.get(rule.left);
            Set<Character> trailingSet = trailing.get(rule.left);

            // Calculate Leading
            if (Character.isUpperCase(rule.right.charAt(0))) {
                leadingSet.add(rule.right.charAt(0));
            } else {
                for (int i = 0; i < rule.right.length() - 1; i++) {
                    if (Character.isUpperCase(rule.right.charAt(i)) && !Character.isUpperCase(rule.right.charAt(i + 1))) {
                        leadingSet.add(rule.right.charAt(i + 1));
                    }
                }
            }

            // Calculate Trailing
            if (Character.isUpperCase(rule.right.charAt(rule.right.length() - 1))) {
                trailingSet.add(rule.right.charAt(rule.right.length() - 1));
            } else {
                for (int i = rule.right.length() - 1; i > 0; i--) {
                    if (Character.isUpperCase(rule.right.charAt(i)) && !Character.isUpperCase(rule.right.charAt(i - 1))) {
                        trailingSet.add(rule.right.charAt(i - 1));
                    }
                }
            }
        }

        System.out.println("\nLeading and Trailing for the given grammar production:");
        for (String nonTerminal : leading.keySet()) {
            System.out.println("Non-Terminal: " + nonTerminal);
            System.out.println("Leading: " + leading.get(nonTerminal));
            System.out.println("Trailing: " + trailing.get(nonTerminal));
            System.out.println();
        }
    }
}

class ProductionRule {
    String left;
    String right;
}