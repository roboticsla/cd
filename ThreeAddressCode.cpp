#include <iostream>
#include <stack>
#include <string>
#include <map>

using namespace std;

bool isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/';
}

bool isOperand(char c) {
    return isalpha(c);
}

int precedence(char op) {
    if (op == '+' || op == '-') return 1;
    if (op == '*' || op == '/') return 2;
    return 0;
}

string toThreeAddressCode(const string& expression) {
    stack<char> operators;
    stack<string> operands;
    map<string, string> threeAddressCode;
    int tempVarCounter = 1;

    for (char c : expression) {
        if (c == ' ') continue;

        if (isOperand(c)) {
            string operand(1, c);
            operands.push(operand);
        } else if (isOperator(c)) {
            while (!operators.empty() && precedence(operators.top()) >= precedence(c)) {
                string op2 = operands.top();
                operands.pop();
                string op1 = operands.top();
                operands.pop();
                string tempVar = "t" + to_string(tempVarCounter++);
                operands.push(tempVar);
                threeAddressCode[tempVar] = op1 + " " + operators.top() + " " + op2;
                operators.pop();
            }
            operators.push(c);
        }
    }

    while (!operators.empty()) {
        string op2 = operands.top();
        operands.pop();
        string op1 = operands.top();
        operands.pop();
        string tempVar = "t" + to_string(tempVarCounter++);
        operands.push(tempVar);
        threeAddressCode[tempVar] = op1 + " " + operators.top() + " " + op2;
        operators.pop();
    }

    string result;
    map<string, string>::iterator it;
    for (it = threeAddressCode.begin(); it != threeAddressCode.end(); ++it) {
        result += it->first + " = " + it->second + "\n";
    }

    return result;
}

int main() {
    string input;
    cout << "Enter the arithmetic expression: ";
    getline(cin, input);

    string threeAddress = toThreeAddressCode(input);

    cout << "Three Address Code:" << endl;
    cout << threeAddress << endl;

    return 0;
}
