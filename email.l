%{
#include <stdio.h>
#include <stdlib.h>
int condition = 0;
%}

%%

[a-z . 0-9]+@[a-z]+".com"|".in" { condition=1; }

\n {
    if (condition == 1) {
        printf(" - Valid email\n\n");
    } else {
        printf(" - Invalid email\n\n");
    }
    condition = 0; // Reset condition for the next line
}

%%

int yywrap(void) {}

int main() {
    yylex();
    return 0;
}