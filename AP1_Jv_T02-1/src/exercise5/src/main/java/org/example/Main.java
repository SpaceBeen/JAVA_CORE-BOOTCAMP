package org.example;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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
        long startTime = System.nanoTime();

        List<CompletableFuture<Void>> futures = pets.stream()
                .map(pet -> CompletableFuture.runAsync(() -> {
                    long walkStartTime = System.nanoTime();
                    double walkTime = pet.goToWalk();
                    long walkEndTime = System.nanoTime();

                    double startRelativeTime = (walkStartTime - startTime) / 1_000_000_000.0;
                    double endRelativeTime = (walkEndTime - startTime) / 1_000_000_000.0;

                    System.out.printf("%s, start time = %.2f, end time = %.2f%n",
                            pet.toString(), startRelativeTime, endRelativeTime);
                }))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}