%{
#include <stdio.h>
#include <string.h>

int keyword_count = 0;
int identifier_count = 0;
int literal_count = 0;
int digit_count = 0;
int special_char_count = 0;
%}

%%
main|int|char|float|double|void|if|else|for|while|do|switch|case|break|continue|return|sizeof|typedef|struct|union|enum|printf|scanf   { 
    keyword_count++; 
}
[a-zA-Z_][a-zA-Z0-9_]*        { 
    identifier_count++; 
}
\"[^\"\n]*\"                  { 
    literal_count++; 
}
[0-9]+                        { 
    digit_count++; 
}
[\+\-\*\/\(\)\{\}\[\];,]      { 
    special_char_count++; 
}

%%

int main() {
    yylex();
    printf("Number of keywords: %d\n", keyword_count);
    printf("Number of identifiers: %d\n", identifier_count);
    printf("Number of literals: %d\n", literal_count);
    printf("Number of digits: %d\n", digit_count);
    printf("Number of special characters: %d\n", special_char_count);
    return 0;
}

int yywrap() {
    return 1;
}
