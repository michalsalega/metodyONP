package com.michalek;

import java.util.Scanner;

public class Main {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int numOfEquations = scanner.nextInt();
        scanner.nextLine();

        for(int i = 0; i < numOfEquations; i++) {
            String currLine = scanner.nextLine();
            String result = "";

            if(currLine.startsWith("ONP: ")) {
                result = convertToINF(currLine.substring(5));
            }else if(currLine.startsWith("INF: ")) {
                result = convertToONP(currLine.substring(5));
            } else if(currLine.startsWith("ONP:")) {
                result = convertToINF(currLine.substring(4));
            } else if(currLine.startsWith("INF:")) {
                result = convertToONP(currLine.substring(4));
            }

            System.out.println(result);
        }
    }

    public static String convertToONP(String equation) {

        equation = cleanString(equation);

        if(checkINF(equation)) {

            int i = 0;
            Stack stack = new Stack(equation.length() * 2);
            Stack output = new Stack(equation.length() * 2);

            do{
                char currChar = equation.charAt(i);

                if(isAnOperand(currChar)) {
                    output.push(currChar);
                } else if(isARightOperator(currChar)) {

                    int currPrior = calculatePrior(currChar);

                    while( !stack.isEmpty() && (calculatePrior(stack.peek()) > currPrior) ) {
                        output.push(stack.pop());
                    }

                    stack.push(currChar);
                } else if(isALeftOperator(currChar)) {

                    int currPrior = calculatePrior(currChar);

                    while( !stack.isEmpty() && (calculatePrior(stack.peek()) >= currPrior) ) {
                        output.push(stack.pop());
                    }

                    stack.push(currChar);

                } else if(currChar == '(') {
                    stack.push(currChar);
                } else if(currChar == ')') {

                    while(stack.peek() != '(' ) {
                        output.push(stack.pop());
                    }

                    stack.pop();
                }

                i++;
            } while(i < equation.length());

            while(!stack.isEmpty()) {
                output.push(stack.pop());
            }

            char[] resultChar = new char[output.size()];

            int j = output.size() - 1;

            while(!output.isEmpty()) {
                resultChar[j--] = output.pop();
            }

            String resultString = "";

            int k = 0;
            while(k < resultChar.length) {
                resultString += resultChar[k++];
            }

            if(checkONP(resultString)) {
                return "ONP: " + resultString;
            }

        }
        return "ONP: error";

    }

    public static String convertToINF(String equation) {

        equation = cleanString(equation);
        equation = cleanParenthesis(equation);

        if(checkONP(equation)) {

            StringStack output = new StringStack(equation.length() * 4);

            for(int i = 0; i < equation.length(); i++) {

                char currChar = equation.charAt(i);

                if(isAnOperand(currChar)) {
                    output.push(new StringOperator(currChar + "", ' '));
                } else if(isATwoArgOperator(currChar)) {

                    StringOperator so1 = output.pop();
                    StringOperator so2 = output.pop();
                    String s1;
                    String s2;

                    int currPrior = calculatePrior(currChar);

                    if (currPrior >= calculatePrior(so1.getOperator()) && currChar != '=' && !((currChar == '^') && so1.getOperator() == '^')) {
                        s1 = "(" + so1.getEquation() + ")";
                    } else {
                        s1 = so1.getEquation();
                    }

                    if (currPrior > calculatePrior(so2.getOperator()) && currChar != '=') {
                        s2 = "(" + so2.getEquation() + ")";
                    } else {
                        s2 = so2.getEquation();
                    }

                    output.push(new StringOperator(s2 + currChar + s1, currChar));
                } else {

                    StringOperator so1 = output.pop();

                    if(calculatePrior(currChar) > calculatePrior(so1.getOperator())) {
                        output.push(new StringOperator(currChar + "(" + so1.getEquation() + ")", currChar));

                    } else {
                        output.push(new StringOperator(currChar + so1.getEquation(), currChar));

                    }

                }
            }

            if(output.size() == 1) {
                return "INF: " + output.pop().getEquation();
            } else {
                return "INF: error";
            }

        } else {
            return "INF: error";
        }
    }

    public static boolean checkINF(String equation) {

        int stan = 0;
        int numOfOperators = 0;
        int numOfOperands = 0;
        int numOfLeftBracket = 0;
        int numOfRightBracket = 0;

        for(int i = 0; i < equation.length(); i++) {

            char currChar = equation.charAt(i);

            if(numOfRightBracket > numOfLeftBracket || numOfOperators > numOfOperands) {
                return false;
            }

            if(stan == 0) {
                if(currChar == '(') {
                    numOfLeftBracket++;
                } else if(currChar == '~') {
                    stan = 2;
                } else if(isAnOperand(currChar)) {
                    stan = 1;
                    numOfOperands++;
                } else {
                    return false;
                }
            } else if(stan == 1) {
                if(currChar == ')') {
                    numOfRightBracket++;
                } else if(isATwoArgOperator(currChar)) {
                    stan = 0;
                    numOfOperators++;
                } else {
                    return false;
                }
            } else {
                if(currChar == '~') {

                } else if(isAnOperand(currChar)) {
                    stan = 1;
                    numOfOperands++;
                } else if(currChar == '(') {
                    stan = 0;
                    numOfLeftBracket++;
                } else {
                    return false;
                }
            }
        }

        return stan == 1 && (numOfOperands - numOfOperators == 1) && (numOfLeftBracket == numOfRightBracket);

    }

    public static boolean checkONP(String equation) {

        int numOfOperators = 0;
        int numOfOperands = 0;
        int numOfLeftBracket = 0;
        int numOfRightBracket = 0;

        for(int i = 0; i < equation.length(); i++) {

            char currChar = equation.charAt(i);

            if((numOfRightBracket > numOfLeftBracket)) {
                return false;
            }

            if(isAnOperand(currChar)) {
                numOfOperands++;
            } else if(isATwoArgOperator(currChar)) {
                numOfOperators++;
                if(i < 2) {
                    return false;
                }
            } else if(currChar == '(') {
                numOfLeftBracket++;
            } else if(currChar == ')') {
                numOfRightBracket++;
            }
        }

        return (numOfOperands - numOfOperators == 1) && (numOfLeftBracket == numOfRightBracket);
    }

    public static int calculatePrior(char c) {

        int prior = -1;

        if(c == '=') {
            prior = 0;
        } else if(c == '>' || c == '<') {
            prior = 1;
        } else if(c == '+' || c == '-') {
            prior = 2;
        } else if(c == '*' || c == '/' || c == '%') {
            prior = 3;
        } else if(c == '^') {
            prior = 4;
        } else if(c == '~') {
            prior = 5;
        } else if(c == ' ') {
            prior = 6;
        }

        return prior;
    }

    public static boolean isATwoArgOperator(char c) {

        return c == '^' || c == '*' || c == '/' || c == '%' || c == '+' || c == '-' || c == '<' || c == '>' || c == '=';
    }

    public static boolean isARightOperator(char c) {

        return c == '~' || c == '^' || c == '=';
    }

    public static boolean isALeftOperator(char c) {

        return c == '*' || c == '/' || c == '%' || c == '+' || c == '-' || c == '>' || c == '<';
    }

    public static boolean isAnOperand(char c) {
        return c >= 97 && c <= 122;
    }

    public static String cleanString(String equation) {

        equation = equation.replaceAll("\\s", "");
        equation = equation.replaceAll("\\.", "");
        equation = equation.replaceAll(",", "");
        equation = equation.replaceAll("#", "");
        equation = equation.replaceAll("\\?", "");
        equation = equation.replaceAll(";", "");
        equation = equation.replaceAll(":", "");
        equation = equation.replaceAll("@", "");
        equation = equation.replaceAll("\\$", "");
        equation = equation.replaceAll("!", "");
        equation = equation.replaceAll("_", "");
        equation = equation.replaceAll("\\{", "");
        equation = equation.replaceAll("}", "");
        equation = equation.replaceAll("\\[", "");
        equation = equation.replaceAll("]", "");
        equation = equation.replaceAll("\\|", "");
        equation = equation.replaceAll("\\d", "");
        equation = equation.replaceAll("\\\\", "");
        equation = equation.replaceAll("`", "");
        equation = equation.replaceAll("[A-Z]", "");

        return equation;
    }

    public static String cleanParenthesis(String equation) {
        equation = equation.replaceAll("\\(", "");
        equation = equation.replaceAll("\\)", "");

        return equation;
    }

    static class Stack
    {
        private char[] arr;
        private int top;
        private int capacity;

        Stack(int size)
        {
            arr = new char[size];
            capacity = size;
            top = -1;
        }

        public void push(char x)
        {
            if (isFull())
            {
                char[] newArr = new char[2*capacity];

                if (capacity >= 0) System.arraycopy(arr, 0, newArr, 0, capacity);

                this.arr = newArr;
            }

            arr[++top] = x;
        }

        public char pop()
        {
            return arr[top--];
        }

        public char peek()
        {
            return arr[top];
        }

        public int size()
        {
            return top + 1;
        }

        public Boolean isEmpty()
        {
            return top == -1;
        }

        public Boolean isFull()
        {
            return top == capacity - 1;
        }
    }

    static class StringStack
    {
        private StringOperator arr[];
        private int top;
        private int capacity;

        StringStack(int size)
        {
            arr = new StringOperator[size];
            capacity = size;
            top = -1;
        }

        public void push(StringOperator x)
        {
            if (isFull())
            {
                StringOperator[] newArr = new StringOperator[2*capacity];

                for(int i = 0; i < capacity; i++) {
                    newArr[i] = arr[i];
                }

                this.arr = newArr;
            }

            arr[++top] = x;
        }

        public StringOperator pop()
        {
            return arr[top--];
        }

        public StringOperator peek()
        {
            return arr[top];
        }

        public int size()
        {
            return top + 1;
        }

        public Boolean isEmpty()
        {
            return top == -1;
        }

        public Boolean isFull()
        {
            return top == capacity - 1;
        }
    }

    static class StringOperator {

        public String equation;
        public char operator;

        public StringOperator(String equation, char operator) {
            this.equation = equation;
            this.operator = operator;
        }

        public String getEquation() {
            return equation;
        }

        public char getOperator() {
            return operator;
        }
    }
}
