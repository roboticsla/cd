#include <iostream>
#include <vector>
#include <map>
#include <set>

using namespace std;

struct ProductionRule {
    string left;
    string right;
};

int main() {
    int ruleCount;
    vector<ProductionRule> rules;

    cout << "Enter the number of production rules: ";
    cin >> ruleCount;
    rules.resize(ruleCount);

    cout << "Enter the production rules (in the form 'left->right'): " << endl;
    for (int i = 0; i < ruleCount; i++) {
        string temp;
        cin >> temp;
        int pos = temp.find("->");
        string token1 = temp.substr(0, pos);
        string token2 = temp.substr(pos + 2);
        rules[i].left = token1;
        rules[i].right = token2;
    }

    map<string, set<char>> leading;
    map<string, set<char>> trailing;

    for (const ProductionRule& rule : rules) {
        leading[rule.left] = set<char>();
        trailing[rule.left] = set<char>();
    }

    for (const ProductionRule& rule : rules) {
        set<char>& leadingSet = leading[rule.left];
        set<char>& trailingSet = trailing[rule.left];

        // Calculate Leading
        if (isupper(rule.right[0])) {
            leadingSet.insert(rule.right[0]);
        } else {
            for (int i = 0; i < rule.right.length() - 1; i++) {
                if (isupper(rule.right[i]) && !isupper(rule.right[i + 1])) {
                    leadingSet.insert(rule.right[i + 1]);
                }
            }
        }

        // Calculate Trailing
        if (isupper(rule.right.back())) {
            trailingSet.insert(rule.right.back());
        } else {
            for (int i = rule.right.length() - 1; i > 0; i--) {
                if (isupper(rule.right[i]) && !isupper(rule.right[i - 1])) {
                    trailingSet.insert(rule.right[i - 1]);
                }
            }
        }
    }

    cout << "\nLeading and Trailing for the given grammar production:" << endl;
    for (const auto& pair : leading) {
        cout << "Non-Terminal: " << pair.first << endl;
        cout << "Leading: ";
        for (char c : pair.second) {
            cout << c << " ";
        }
        cout << endl;
        cout << "Trailing: ";
        for (char c : trailing[pair.first]) {
            cout << c << " ";
        }
        cout << endl << endl;
    }

    return 0;
}
