package org.example;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public static void main(String[] args) {
        int n;
        float[] array;
        try {
            n = scanner.nextInt();
            if (n <= 0) {
                System.err.println("Input error. Size <= 0");
                System.exit(0);
            }
            array = new float[n];
            for (int i = 0; i < n; i++) {
                array[i] = scanner.nextFloat();
            }
            insertionSort(array);
            for (int i = 0; i < n; i++) {
                System.out.print(array[i] + " ");
            }
        } catch (InputMismatchException e) {
            System.err.println("Couldn't parse a number. Please, try again");
            scanner.next();
        }
    }

    private static void insertionSort(float[] array) {
        for (int left = 0; left < array.length; left++) {
            float value = array[left];
            int i = left - 1;
            for (; i >= 0; i--) {
                if (array[i] > value) {
                    array[i + 1] = array[i];
                } else {
                    break;
                }
            }
            array[i + 1] = value;
        }
    }
}