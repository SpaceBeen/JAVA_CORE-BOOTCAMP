package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int n;
        int[] perfectArray;
        try {
            n = scanner.nextInt();
            if (n <= 0) {
                System.err.println("Input error. Size <= 0");
                System.exit(0);
            }
            int[] array = new int[n];
            try {
                int count = 0;
                for (int i = 0; i < n; i++) {
                    array[i] = scanner.nextInt();
                    if (isPerfect(array[i])) {
                        count++;
                    }
                }
                if (count > 0) {
                    perfectArray = new int[count];
                    int j = 0;
                    for (int i = 0; i < n; i++) {
                        if (isPerfect(array[i])) {
                            perfectArray[j] = array[i];
                            j++;
                            System.out.printf("%d ", perfectArray[j]);
                        }
                    }
                } else {
                    System.out.println("There are no such elements");
                }
            } catch (InputMismatchException e) {
                System.err.println("Couldn't parse a number. Please, try again");
                scanner.next();
            } finally {
                scanner.close();
            }
        } catch (InputMismatchException e) {
            System.err.println("Couldn't parse a number. Please, try again");
            scanner.next();
        } finally {
            scanner.close();
        }
    }

    private static boolean isPerfect(int number) {
        int first = number % 10, last = number % 10;
        int c = (number - number % 10) / 10;
        while (c != 0) {
            first = c % 10;
            c = (c - c % 10) / 10;
        }
        return first == last;
    }
}