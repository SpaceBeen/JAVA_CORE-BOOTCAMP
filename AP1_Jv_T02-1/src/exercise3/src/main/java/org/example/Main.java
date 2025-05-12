package org.example;

import java.util.*;

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
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    pets.add(new Cat(name, age));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }

            } else if (type.equalsIgnoreCase("Dog")) {
                String name;
                int age;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    pets.add(new Dog(name, age));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }

            } else if (type.equalsIgnoreCase("Hamster")) {
                String name;
                int age;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    pets.add(new Hamster(name, age));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }
            } else if (type.equalsIgnoreCase("guinea")) {
                String name;
                int age;
                try {
                    name = sc.next();
                    age = sc.nextInt();
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        continue;
                    }
                    pets.add(new GuineaPig(name, age));
                } catch (InputMismatchException e) {
                    System.err.println("Couldn't parse a number");
                }
            } else {
                System.out.println("Incorrect input. Unsupported pet type");
            }
        }
        for (Animal pet : pets) {
            if (pet instanceof Herbivore) {
                System.out.println(pet);
            }
        }
        for (Animal pet : pets) {
            if (pet instanceof Omnivore) {
                System.out.println(pet);
            }
        }
    }
}