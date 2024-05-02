import java.util.*;
import java.util.stream.*;

public class RegexNFA {
    NFA a = new NFA();
    NFA b = new NFA();

    static NFA concat(NFA a, NFA b) {
        NFA result = new NFA();
        result.set_vertex(a.get_vertex_count()
            + b.get_vertex_count()); // No new vertex added in concatenation
        int i;
        Transitions new_trans;
        for (i = 0; i < a.transitions.size(); i++) {
            new_trans = a.transitions.get(i);
            result.set_transition(new_trans.vertex_from, new_trans.vertex_to,
                new_trans.symbol); // Copy old transitions
        }
        result.set_transition(a.get_final_state(), a.get_vertex_count(),
            '^'); // Creating the link; final state of a will link to initial state of b
        for (i = 0; i < b.transitions.size(); i++) {
            new_trans = b.transitions.get(i);
            result.set_transition(new_trans.vertex_from + a.get_vertex_count(),
                new_trans.vertex_to + a.get_vertex_count(),
                new_trans.symbol); // Copy old transitions wit offset as a's vertices have
                                   // already been added
        }
        result.set_final_state(a.get_vertex_count() + b.get_vertex_count()
            - 1); // Mark b's final as final in new one too
        return result;
    }
    static NFA kleene(NFA a) {
        NFA result = new NFA();
        int i;
        Transitions new_trans;
        result.set_vertex(a.get_vertex_count() + 2);
        result.set_transition(0, 1, '^'); // Epsilon transition from S0 to S1
        for (i = 0; i < a.transitions.size(); i++) {
            new_trans = a.transitions.get(i);
            result.set_transition(new_trans.vertex_from + 1, new_trans.vertex_to + 1,
                new_trans.symbol); // Copy old transitions
        }
        result.set_transition(a.get_vertex_count(), a.get_vertex_count() + 1,
            '^'); // Epsilon transition to new final state
        result.set_transition(a.get_vertex_count(), 1, '^'); // Reverese epsilon
                                                             // transition
        result.set_transition(
            0, a.get_vertex_count() + 1, '^'); // Forward total epsilon transition
        result.set_final_state(a.get_vertex_count() + 1); // Mark final state
        return result;
    }
    static NFA or_selection(ArrayList<NFA> selections, int no_of_selections) {
        NFA result = new NFA();
        int vertex_count = 2;
        int i, j;
        NFA med;
        Transitions new_trans;
        for (i = 0; i < no_of_selections; i++) {
            vertex_count +=
                selections.get(i)
                    .get_vertex_count(); // Find total vertices by summing all NFAs
        }
        result.set_vertex(vertex_count);
        int adder_track = 1;
        for (i = 0; i < no_of_selections; i++) {
            result.set_transition(0, adder_track,
                '^'); 
            med = selections.get(i);
            for (j = 0; j < med.transitions.size(); j++) {
                new_trans = med.transitions.get(j);
                result.set_transition(new_trans.vertex_from + adder_track,
                    new_trans.vertex_to + adder_track,
                    new_trans.symbol); 
            }
            adder_track += med.get_vertex_count(); // Find how amny vertices added
            result.set_transition(adder_track - 1, vertex_count - 1,
                '^'); // Add epsilon transition to final state
        }
        result.set_final_state(vertex_count - 1); // Mark final state
        return result;
    }
    static NFA re_to_nfa(String re) {
        Stack<Character> operators = new Stack<>();
        Stack<NFA> operands = new Stack<>();
        char op_sym;
        int op_count;
        char cur_sym;
        NFA new_sym;
        char x[] = re.toCharArray();
        for (int i = 0; i < x.length; i++) {
            cur_sym = x[i];
            if (cur_sym != '(' && cur_sym != ')' && cur_sym != '*' && cur_sym != '|'
                && cur_sym != '.') // Must be a character, so build simplest NFA
            {
                new_sym = new NFA();
                new_sym.set_vertex(2);
                new_sym.set_transition(0, 1, cur_sym);
                new_sym.set_final_state(1);
                operands.push(new_sym); // push it back
            } else {
                switch (cur_sym) {
                    case '*':
                        NFA star_sym = operands.pop();
                        operands.push(kleene(star_sym));
                        break;
                    case '.':
                        operators.push(cur_sym);
                        break;
                    case '|':
                        operators.push(cur_sym);
                        break;
                    case '(':
                        operators.push(cur_sym);
                        break;
                    default:
                        op_count = 0;
                        char c;
                        op_sym = operators.peek(); // See whih symbol is on top
                        if (op_sym == '(')
                            continue; // Keep searching operands
                        do {
                            operators.pop();
                            op_count++;
                        } while (operators.peek() != '('); // Collect operands
                        operators.pop();
                        NFA op1;
                        NFA op2;
                        ArrayList<NFA> selections = new ArrayList<>();
                        if (op_sym == '.') {
                            for (int ii = 0; ii < op_count; ii++) {
                                op2 = operands.pop();
                                op1 = operands.pop();
                                operands.push(
                                    concat(op1, op2)); // Concatenate and add back
                            }
                        } else if (op_sym == '|') {
                            for (int j = 0; j < op_count + 1; j++)
                                selections.add(new NFA());
                            int tracker = op_count;
                            for (int k = 0; k < op_count + 1; k++) {
                                selections.set(tracker, operands.pop());
                                tracker--;
                            }
                            operands.push(or_selection(selections, op_count + 1));
                        }
                        break;
                }
            }
        }
        return operands.peek(); // Return the single entity. operands.poll() is also fine
    }
    String join(ArrayList<Integer> v, String delim) {
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < v.size(); ++i) {
            if (i != 0)
                ss.append(delim);
            ss.append(v.get(i));
        }
        return ss.toString();
    }
    public static void main(String args[]) {
        String re;
        System.out.println("------------------");
        System.out.println("FORMAT : \n"
            + "> Explicitly mention concatenation with a '.' operator \n"
            + "-> Enclose every concatenation and or section by parantheses \n"
            + "-> Enclose the entire regular expression with parantheses like (a*.b*) but use (a.b*) singly \n\n");
        System.out.println("For example : For the regular expression (a.(b|c))");
        System.out.println("Enter the regular expression in the above mentioned format");
        re = new Scanner(System.in).next();
        System.out.println("\nThe required NFA has the transitions: ");
        NFA required_nfa;
        required_nfa = re_to_nfa(re);
        required_nfa.display();
    }
}
class Transitions {
    int vertex_from;
    int vertex_to;
    char symbol;
    Transitions(int vertex_from, int vertex_to, char symbol) {
        this.vertex_from = vertex_from;
        this.vertex_to = vertex_to;
        this.symbol = symbol;
    }
}
class NFA {
    ArrayList<Integer> vertex = new ArrayList<>();
    ArrayList<Transitions> transitions = new ArrayList<>();
    int final_state;
    int get_vertex_count() {
        return vertex.size();
    }
    void set_vertex(int total_vertex) {
        for (int i = 0; i < total_vertex; i++) {
            vertex.add(i);
        }
    }
    void set_transition(int vertex_from, int vertex_to, char symbol) {
        Transitions trans = new Transitions(vertex_from, vertex_to, symbol);
        transitions.add(trans);
    }
    void set_final_state(int fs) {
        final_state = fs;
    }
    int get_final_state() {
        return final_state;
    }
    void display() {
        for (int i = 0; i < transitions.size(); i++) {
            Transitions temp = transitions.get(i);
            System.out.println("q" + temp.vertex_from + " -> q" + temp.vertex_to
                + " : Symbol - " + temp.symbol);
        }
        System.out.println("The final state is q" + get_final_state());
    }
    ArrayList<Character> find_possible_input_symbols(ArrayList<Integer> vertex) {
        ArrayList<Character> result = new ArrayList<>();
        for (int i = 0; i < vertex.size(); i++) {
            int vertex_from = vertex.get(i);
            for (int j = 0; j < transitions.size(); j++) {
                Transitions it = transitions.get(j);
                if (it.vertex_from == vertex_from && it.symbol != '^') {
                    result.add(it.symbol);
                }
            }
        }
        return result;
    }
    ArrayList<Integer> unique(ArrayList<Integer> list) {
        return IntStream.range(0, list.size())
            .filter(i
                -> ((i < list.size() - 1 && !list.get(i).equals(list.get(i + 1)))
                    || i == list.size() - 1))
            .mapToObj(i -> list.get(i))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    ArrayList<Integer> eclosure(ArrayList<Integer> vertex) {
        ArrayList<Integer> result = new ArrayList<>();
        boolean visited[] = new boolean[get_vertex_count()];
        for (int i = 0; i < vertex.size(); i++) {
            eclosure(vertex.get(i), result, visited);
        }
        Collections.sort(result);
        return unique(result);
    }
    void eclosure(int x, ArrayList<Integer> result, boolean visited[]) // Simple DFS
    {
        result.add(x);
        for (int i = 0; i < transitions.size(); i++) {
            Transitions it = transitions.get(i);
            if (it.vertex_from == x && it.symbol == '^') {
                int y = it.vertex_to;
                if (!visited[y]) {
                    visited[y] = true;
                    eclosure(y, result, visited);
                }
            }
        }
    }
    ArrayList<Integer> move(ArrayList<Integer> T, char symbol) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int j = 0; j < T.size(); j++) {
            int t = T.get(j);
            for (int i = 0; i < transitions.size(); i++) {
                Transitions it = transitions.get(i);
                if (it.vertex_from == t && it.symbol == symbol) {
                    result.add(it.vertex_to);
                }
            }
        }
        Collections.sort(result);
        boolean debug = true;
        if (debug) {
            int l1 = result.size();
            unique(result);
            int l2 = result.size();
            if (l2 < l1) {
                System.out.println("move(T, a) returns non-unique ArrayList");
                System.exit(1);
            }
        }
        return result;
    }
}