# Term Project: Turkish-Inspired Programming Language

## Group Members & Student IDs
* **[Rozerin Yaşar]** - [230315082]
* **[Cavit Can Key]** - [230316030]
* **[Azize Görür]** - [220316018]
* **[Yiğit Kızıldağ]** - [230316021]

---

## Project Description
This project implements a custom, Turkish-inspired programming language lexical and syntax analyzer using JAVA. It is built by modifying and extending the recursive-descent parser and lexical analysis structure provided in Chapter 4 of Robert W. Sebesta's *Concepts of Programming Languages*.

* **Implementation Language:** JAVA
* **Target Framework/Compiler:** javac

---

## How to Compile the Project
To compile the project, open your terminal or command prompt, navigate to the directory containing the source code, and run the following command based on your operating system:

* **Windows:**
    ```bash
   javac PLProject.java
    ```
* **Mac / Linux:**
    ```bash 
    javac PLProject.java
    ```

---

## How to Run the Project
After a successful compilation, you can execute the program using the following commands. By default, the program is configured to read the source code from a file named `front.in` in the same directory.

* **Windows:**
    ```bash
   chcp 65001
   java PLProject
    ```
* **Mac / Linux:**
    ```bash
    java PLProject
    ```

> **Note:** To test different files, copy the content of the desired test file into `front.in` or modify the `fopen` function inside `PLProject.java` with the respective filename before compiling.

---
## Test Files Reference

### Valid Test Files
The following files contain syntactically and lexically correct source code written in our custom language:
1. **`Variable declarations and assignments.txt`**
2. **`Arithmetic expressions.txt`**
3. **`Conditional statements.txt`**
4. **`Loop structures.txt`**

### Invalid Test File
The following file contains an intentional syntax error to demonstrate the compiler's error reporting and detection capabilities:
* **`Invalid.txt`**: Contains a missing right parenthesis in the condition block (`eger (a == b {`), which successfully triggers the internal error mechanism (`eger kosulunda ')' eksik!`).