package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<User> users = new ArrayList<>();

        int n = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < n; i++) {
            String name = scanner.nextLine();
            int age = -1;
            while (age == -1) {
                try {
                    age = Integer.parseInt(scanner.nextLine());
                    if (age <= 0) {
                        System.out.println("Incorrect input. Age <= 0");
                        age = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Couldn't parse a number. Please, try again.");
                }
            }
            users.add(new User(name, age));
        }
        List<String> adultUsers = users.stream()
                .filter(user -> user.getAge() >= 18)
                .map(User::getName)
                .collect(Collectors.toList());

        System.out.println(String.join(", ", adultUsers));
        scanner.close();
    }
}
