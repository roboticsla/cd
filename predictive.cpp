#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <string>
#include <cctype>
#include <algorithm>


using namespace std;

unordered_map<char, unordered_set<char>> first, follow;
unordered_map<char, vector<string>> productions;
unordered_map<char, unordered_map<char, string>> parsingTable;

void computeFirst(char nonTerminal) {
    if (first.find(nonTerminal) != first.end())
        return;

    for (string production : productions[nonTerminal]) {
        char symbol = production[0];
        if (isupper(symbol)) {
            computeFirst(symbol);
            for (char ch : first[symbol]) {
                first[nonTerminal].insert(ch);
            }
        } else {
            first[nonTerminal].insert(symbol);
        }
    }
}

void computeFollow(char nonTerminal) {
    if (follow.find(nonTerminal) == follow.end())
        follow[nonTerminal] = unordered_set<char>();

    for (auto &it : productions) {
        char left = it.first;
        for (auto &prod : it.second) {
            for (int i = 0; i < prod.size(); i++) {
                if (prod[i] == nonTerminal) {
                    if (i == prod.size() - 1) {
                        if (left != nonTerminal) {
                            computeFollow(left);
                            for (char ch : follow[left]) {
                                follow[nonTerminal].insert(ch);
                            }
                        }
                    } else {
                        char next = prod[i + 1];
                        if (isupper(next)) {
                            for (char ch : first[next]) {
                                if (ch != '#') {
                                    follow[nonTerminal].insert(ch);
                                }
                            }
                            if (first[next].find('#') != first[next].end()) {
                                for (int j = i + 1; j < prod.size(); j++) {
                                    if (isupper(prod[j])) {
                                        for (char ch : first[prod[j]]) {
                                            if (ch != '#') {
                                                follow[nonTerminal].insert(ch);
                                            }
                                        }
                                        if (first[prod[j]].find('#') == first[prod[j]].end()) {
                                            break;
                                        }
                                    } else {
                                        follow[nonTerminal].insert(prod[j]);
                                        break;
                                    }
                                }
                            }
                        } else {
                            follow[nonTerminal].insert(next);
                        }
                    }
                }
            }
        }
    }

    if (nonTerminal == 'S') {
        follow[nonTerminal].insert('$');
    }
}

void constructParsingTable() {
    for (auto &it : productions) {
        char left = it.first;
        for (string prod : it.second) {
            for (char ch : first[left]) {
                if (ch != '#') {
                    parsingTable[left][ch] = prod;
                }
            }
            if (first[left].find('#') != first[left].end()) {
                for (char ch : follow[left]) {
                    parsingTable[left][ch] = prod;
                }
            }
        }
    }
}

int main() {
    productions['S'] = {"aBDh"};
    productions['B'] = {"cC"};
    productions['C'] = {"bC", "#"};
    productions['D'] = {"EF"};
    productions['E'] = {"g", "#"};
    productions['F'] = {"f", "#"};

    computeFirst('S');
    computeFirst('B');
    computeFirst('C');
    computeFirst('D');
    computeFirst('E');
    computeFirst('F');

    computeFollow('S');
    computeFollow('B');
    computeFollow('C');
    computeFollow('D');
    computeFollow('E');
    computeFollow('F');

    constructParsingTable();

    cout << "Predictive Parsing Table:" << endl;
    for (auto &row : parsingTable) {
        cout << row.first << " : ";
        for (auto &entry : row.second) {
            cout << "(" << entry.first << ", " << entry.second << ") ";
        }
        cout << endl;
    }

    return 0;
}
