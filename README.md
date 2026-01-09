# C-Minus Compiler

A compiler implementation for the C-Minus programming language, built as part of a compilers course project.

## Features

- **Lexical Analysis**: Tokenizes C-Minus source code using JFlex
- **Syntax Analysis**: Parses tokens into an Abstract Syntax Tree (AST) using CUP parser generator
- **Semantic Analysis**: Type checking and symbol table management
- **Error Reporting**: Detailed error messages with line numbers

## Tech Stack

- Java
- JFlex (Lexical Analyzer Generator)
- CUP (Parser Generator)
- Make

## Project Structure

```
CMparser/
├── Lexer.flex      # Lexical analyzer specification
├── Parser.cup      # Grammar specification
├── absyn/          # Abstract syntax tree node classes
├── GivenTests/     # Provided test cases
└── MyTests/        # Custom test cases
```

## Building & Running

```bash
cd CMparser
make clean
make
java -cp /usr/share/java/cup.jar:. CM test.cm
```

## Sample Input

```c
int gcd(int u, int v) {
    if (v == 0)
        return u;
    else
        return gcd(v, u - u / v * v);
}

void main(void) {
    int x; int y;
    x = input();
    y = input();
    output(gcd(x, y));
}
```

## What I Learned

- Compiler design principles and implementation
- Lexical analysis and regular expressions
- Context-free grammars and parsing techniques
- Symbol table design and scope management
- Abstract syntax tree construction
