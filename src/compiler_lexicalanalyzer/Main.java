package compiler_lexicalanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static class Token {

        String label;
        String token;
        String regularExpr;

        Token(String l, String t, String r) {
            this.label = l;
            this.token = t;
            this.regularExpr = r;
        }
    }

    static class LexicalAnalysis {

        String group;
        int start;
        int end;
        Token token;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<Token> symbolTable = new ArrayList<Token>();
        ArrayList<LexicalAnalysis> lexical = new ArrayList<LexicalAnalysis>();
        symbolTable = CreateSymbolTable();//create symbol table
        //read input from file
        File file = new File("Code.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String targetCode = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            targetCode += line + "\n";
        }
        br.close();
        System.out.println("input code: ");
        System.out.print(targetCode);
        System.out.println();
        targetCode = generateLexical(lexical, symbolTable, targetCode);//matching target with symbol table and find the unmatched symbols
        //if there is result of matched input 
        if (lexical.size() > 0) {
            sortByFirst(lexical);
            printLexical(lexical);
        }
    }
//create symbol table 

    static ArrayList<Token> CreateSymbolTable() {
        ArrayList<Token> symbolTable = new ArrayList();
        symbolTable.add(new Token("MULTI_COMMENT", "/* blabla */", "(^\\/\\*(\\\\w*\\s*)*?(\\n)*?\\*\\/$)"));
        symbolTable.add(new Token("SINGLE_COMMENT", "//", "([\\/\\/].*)"));
        symbolTable.add(new Token("STRING_LITERAL", "String", "(\"\\w+\")"));
        symbolTable.add(new Token("CHAR_LITERAL", "character", "(\'\\w\')"));
        symbolTable.add(new Token("AUTO", "auto", "(\\bauto\\b)"));
        symbolTable.add(new Token("TRUE", "true", "(\\btrue\\b)"));
        symbolTable.add(new Token("BOOL", "bool", "(\\bbool\\b)"));
        symbolTable.add(new Token("CONST", "const", "(\\bconst\\b)"));
        symbolTable.add(new Token("DO", "do", "(\\bdo\\b)"));
        symbolTable.add(new Token("ENUM", "enum", "(\\benum\\b)"));
        symbolTable.add(new Token("INT", "int", "(\\bint\\b)"));
        symbolTable.add(new Token("RETURN", "return", "(\\breturn\\b)"));
        symbolTable.add(new Token("SIZEOF", "sizeof", "(\\bsizeof\\b)"));
        symbolTable.add(new Token("SWITCH", "switch", "(\\bswitch\\b)"));
        symbolTable.add(new Token("UNSIGNED", "unsigned", "(\\bunsigned\\b)"));
        symbolTable.add(new Token("WHILE", "while", "(\\bwhile\\b)"));
        symbolTable.add(new Token("NEW", "new", "(\\bnew\\b)"));
        symbolTable.add(new Token("FALSE", "false", "(\\bfalse\\b)"));
        symbolTable.add(new Token("CASE", "case", "(\\bcase\\b)"));
        symbolTable.add(new Token("CONTINUE", "continue", "(\\bcontinue\\b)"));
        symbolTable.add(new Token("DOUBLE", "double", "(\\bdouble\\b)"));
        symbolTable.add(new Token("EXTERN", "extern", "(\\bextern\\b)"));
        symbolTable.add(new Token("GOTO", "goto", "(\\bgoto\\b)"));
        symbolTable.add(new Token("LONG", "long", "(\\blong\\b)"));
        symbolTable.add(new Token("SHORT", "short", "(\\bshort\\b)"));
        symbolTable.add(new Token("STATIC", "static", "(\\bstatic\\b)"));
        symbolTable.add(new Token("TYPEDEF", "typedef", "(\\btypedef\\b)"));
        symbolTable.add(new Token("VOID", "void", "(\\bvoid\\b)"));
        symbolTable.add(new Token("IF", "if", "(\\bif\\b)"));
        symbolTable.add(new Token("ELSE", "else", "(\\belse\\b)"));
        symbolTable.add(new Token("BREAK", "break", "(\\bbreak\\b)"));
        symbolTable.add(new Token("REGISTER", "register", "(\\bregister\\b)"));
        symbolTable.add(new Token("UNION", "union", "(\\bunion\\b)"));
        symbolTable.add(new Token("CHAR", "char", "(\\bchar\\b)"));
        symbolTable.add(new Token("SIGNED", "signed", "(\\bsigned\\b)"));
        symbolTable.add(new Token("STRUCT", "struct", "(\\bstruct\\b)"));
        symbolTable.add(new Token("VOLATILE", "volatile", "(\\bvolatile\\b)"));
        symbolTable.add(new Token("DEFAULT", "default", "(\\bdefault\\b)"));
        symbolTable.add(new Token("FOR", "for", "(\\bfor\\b)"));
        symbolTable.add(new Token("FLOAT", "float", "(\\bfloat\\b)"));
        symbolTable.add(new Token("FLOAT_LITERAL", "float number", "(\\b\\d+\\.\\d+\\b)"));
        symbolTable.add(new Token("INTEGER_LITERAL", "int number", "(\\b\\d+\\b)"));
        symbolTable.add(new Token("LEFT_CURLY_B", "{", "(\\{)"));
        symbolTable.add(new Token("RIGH_CURLY_B", "}", "(\\})"));
        symbolTable.add(new Token("LEFT_SQUARE_B", "[", "(\\[)"));
        symbolTable.add(new Token("RIGH_SQUARE_B", "]", "(\\])"));
        symbolTable.add(new Token("LEFT_ROUND_B", "(", "(\\()"));
        symbolTable.add(new Token("RIGH_ROUND_B", ")", "(\\))"));
        symbolTable.add(new Token("LEFT_SHIFT", ">>", "(\\>>)"));
        symbolTable.add(new Token("RIGHT_SHIFT", "<<", "(\\<<)"));
        symbolTable.add(new Token("EQUAL", "==", "(\\==)"));
        symbolTable.add(new Token("NOT_EQ", "!=", "(\\!=)"));
        symbolTable.add(new Token("GREAT_EQ", "=<", "(\\=<)"));
        symbolTable.add(new Token("LESS_EQ", "=>", "(\\=>)"));
        symbolTable.add(new Token("LESSTHAN", ">", "(\\>)"));
        symbolTable.add(new Token("GREATERTHAN", "<", "(\\<)"));
        symbolTable.add(new Token("AND", "&&", "(\\&&)"));
        symbolTable.add(new Token("OR", "||", "(\\|\\|)"));
        symbolTable.add(new Token("BITWISE_AND", "&", "(\\&)"));
        symbolTable.add(new Token("BITWISE_OR", "|", "(\\|)"));
        symbolTable.add(new Token("ASSIGN_OPERATOR", "=", "(\\=)"));
        symbolTable.add(new Token("MOD", "%", "(\\%)"));
        symbolTable.add(new Token("NOT", "!", "(\\!)"));
        symbolTable.add(new Token("PLUS", "+", "(\\+)"));
        symbolTable.add(new Token("MINUS", "-", "(\\-)"));
        symbolTable.add(new Token("DIVIDE", "/", "(\\/)"));
        symbolTable.add(new Token("SEMICOLON", ";", "(\\;)"));
        symbolTable.add(new Token("COMMA", ",", "(\\,)"));
        symbolTable.add(new Token("ASTERICK", "*", "(\\*)"));
        symbolTable.add(new Token("BACKWARD_SLASH", "\\", "(\\\\)"));
        symbolTable.add(new Token("PREPROCESSOR", "#", "(\\#)"));
        symbolTable.add(new Token("BITWISE_XOR", "^", "(\\^)"));
        symbolTable.add(new Token("BITWISE_NOT", "~", "(\\~)"));
        symbolTable.add(new Token("DOT", ".", "(\\.)"));
        symbolTable.add(new Token("EOF", "0", "(0)"));
        symbolTable.add(new Token("ID", "", "(\\b[a-zA-Z_][a-zA-Z0-9_]*)"));
        return symbolTable;
    }
//find and match the input with symbol table and print the unmatched if found

    static String generateLexical(ArrayList<LexicalAnalysis> lexical, ArrayList<Token> symbolTable, String target) {
        Pattern pattern;
        Matcher matcher;
        String not_found = "";
        for (int i = 0; i < symbolTable.size(); i++) {
            pattern = Pattern.compile(symbolTable.get(i).regularExpr);
            matcher = pattern.matcher(target);

            while (matcher.find()) {
                LexicalAnalysis obj = new LexicalAnalysis();
                obj.group = matcher.group();
                obj.start = matcher.start();
                obj.end = matcher.end();
                obj.token = symbolTable.get(i);
                lexical.add(obj);

                String firstPart = target.substring(0, matcher.start());
                String targetPart = target.substring(matcher.start(), matcher.end());
                String lastPart = target.substring(matcher.end());

                targetPart = addSpaces(matcher.end() - matcher.start());
                target = firstPart + targetPart + lastPart;
            }
            if (!matcher.find()) {
                not_found = target;
            }
        }
        //printing unmatched strings
        String not_matched[] = not_found.trim().split(" ");
        if (not_matched.length > 0) {
            for (int i = 0; i < not_matched.length; i++) {
                if (not_matched[i].trim().isEmpty()) {
                    continue;
                }
                System.out.println("not matched--> " + not_matched[i]);
            }
        }

        return target;
    }
//to help with splitting input to match

    static String addSpaces(int len) {
        String spaces = "";
        for (int i = 0; i < len; i++) {
            spaces += " ";
        }
        return spaces;
    }
//sort the output from matching result

    static void sortByFirst(ArrayList<LexicalAnalysis> lexical) {
        LexicalAnalysis temp = new LexicalAnalysis();
        temp = lexical.get(0);
        for (int i = 0; i < lexical.size(); i++) {
            for (int j = 0; j < lexical.size(); j++) {
                if (lexical.get(i).start < lexical.get(j).start) {
                    temp = lexical.get(i);
                    lexical.set(i, lexical.get(j));
                    lexical.set(j, temp);
                }
            }
        }
    }

    //print and write to file
    static void printLexical(ArrayList<LexicalAnalysis> lexical) throws IOException {
        FileWriter myWriter = new FileWriter("output.txt");
        BufferedWriter buffer = new BufferedWriter(myWriter);
        for (int i = 0; i < lexical.size(); i++) {
            System.out.println("<" + lexical.get(i).token.label + "> : " + lexical.get(i).group);
            buffer.write("<" + lexical.get(i).token.label + "> : " + lexical.get(i).group);
            buffer.newLine();

        }
        buffer.close();
        myWriter.close();
        System.out.println("Successfully wrote to the file.");

    }
}
