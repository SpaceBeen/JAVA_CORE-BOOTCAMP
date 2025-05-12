package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int n;
        try {
            n = scanner.nextInt();
            if (n <= 0) {
                System.err.println("Input error. Size <= 0");
                System.exit(0);
            }
            int[] array = new int[n];
            try {
                for (int i = 0; i < n; i++) {
                    array[i] = scanner.nextInt();
                }
                int count = 0, sum = 0;
                for (int i = 0; i < n; i++) {
                    if (array[i] < 0) {
                        count += 1;
                        sum += array[i];
                    }
                }
                if (count == 0) {
                    System.err.println("«There are no negative elements");
                } else {
                    System.out.println(sum / count);
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
}