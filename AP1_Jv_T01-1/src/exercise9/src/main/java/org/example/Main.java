package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numOfStrings = scanner.nextInt();
        scanner.nextLine();
        List<String> strings = new ArrayList<>();

        for (int i = 0; i < numOfStrings; i++) {
            strings.add(scanner.nextLine());
        }

        String substring = scanner.nextLine();
        List<String> filteredStrings = filterStringsBySubstring(strings, substring);

        System.out.println(String.join(", ", filteredStrings));


        scanner.close();
    }

    public static List<String> filterStringsBySubstring(List<String> strings, String substring) {
        List<String> result = new ArrayList<>();
        for (String str : strings) {
            if (str.contains(substring)) {
                result.add(str);
            }
        }
        return result;
    }
}
