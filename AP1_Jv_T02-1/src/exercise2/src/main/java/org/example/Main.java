package org.example;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

public class Main {
    static Scanner sc = new Scanner(System.in).useLocale(Locale.US);

    public static void main(String[] args) {
        int n = sc.nextInt();
        List<Animal> pets = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String type = sc.next();
            if (type.equalsIgnoreCase("Cat")) {
                String name;
                int age;
                double weight;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    weight = sc.nextDouble();
                    if (weight <= 0.0) {
                        System.out.println("Incorrect input. Mass <= 0");
                        continue;
                    }
                    pets.add(new Cat(name, age, weight));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }

            } else if (type.equalsIgnoreCase("Dog")) {
                String name;
                int age;
                double weight;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    weight = sc.nextDouble();
                    if (weight <= 0.0) {
                        System.out.println("Incorrect input. Mass <= 0");
                        continue;
                    }
                    pets.add(new Dog(name, age, weight));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }

            } else {
                System.out.println("Incorrect input. Unsupported pet type");
            }
        }
        for (Animal pet : pets) {
            System.out.println(pet);
        }
    }
}