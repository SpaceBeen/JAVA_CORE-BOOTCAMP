package org.example;

import java.util.*;
import java.util.stream.Collectors;

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
            } else {
                System.out.println("Incorrect input. Unsupported pet type");
            }
        }

        pets = pets.stream()
                .map(pet -> {
                    if (pet.getAge() > 10) {
                        return pet instanceof Dog
                                ? new Dog(pet.getName(), pet.getAge() + 1)
                                : new Cat(pet.getName(), pet.getAge() + 1);
                    }
                    return pet;
                })
                .collect(Collectors.toList());

        pets.forEach(System.out::println);
    }
}