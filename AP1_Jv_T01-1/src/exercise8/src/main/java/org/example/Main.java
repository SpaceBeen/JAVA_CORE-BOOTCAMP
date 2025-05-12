package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int previousNumber = Integer.MIN_VALUE;
        int index = 0;
        boolean isOrdered = true;
        boolean hasInput = false;

        while (true) {

            if (!scanner.hasNextInt()) {
                if (hasInput) {
                    if (isOrdered) {
                        System.out.println("The sequence is ordered in ascending order");
                    }
                } else {
                    System.out.println("Input error");
                }
                break;
            }

            int currentNumber = scanner.nextInt();
            index++;
            hasInput = true;

            if (currentNumber < previousNumber) {
                System.out.println("The sequence is not ordered from the ordinal number of the number " + index);
                break;
            }
            previousNumber = currentNumber;
        }
        scanner.close();
    }
}
