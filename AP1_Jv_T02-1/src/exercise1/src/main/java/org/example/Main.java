package org.example;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int n = sc.nextInt();
        List<Animal> pets = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String type = sc.next();
            if (type.equalsIgnoreCase("Cat")) {
                String name = "";
                int age = 0;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }
                pets.add(new Cat(name, age));
            } else if (type.equalsIgnoreCase("Dog")) {
                String name = "";
                int age = 0;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }
                pets.add(new Dog(name, age));
            } else {
                System.out.println("Incorrect input. Unsupported pet type");
            }
        }
        for (Animal pet : pets) {
            System.out.println(pet);
        }
    }
}