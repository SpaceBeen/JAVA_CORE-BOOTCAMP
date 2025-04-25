package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int seconds;
        seconds = getInput();
        if (seconds < 0) {
            System.err.println("Incorrect time");
            System.exit(1);
        }
        System.out.printf("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);

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
}