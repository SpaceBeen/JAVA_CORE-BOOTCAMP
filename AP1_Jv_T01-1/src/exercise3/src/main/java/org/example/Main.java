package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int n;
        n = getInput();
        try {
            int fibbonachi =fibonacci(n);
            System.out.println(fibbonachi);;
        }catch (StackOverflowError e){
            System.err.println("Too Long n");
        }

    }

    private static int getInput() {
        int number;
        while (true) {
            try {
                number = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.err.println("Couldn't parse a number. Please, try again");
                scanner.next();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return number;
    }

    private static int fibonacci(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);

    }

}