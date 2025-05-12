package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void findMinMax(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Input error. File isn't exist");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String countStr = br.readLine().trim();
            int count;
            try {
                count = Integer.parseInt(countStr);
                if (count <= 0) {
                    System.out.println("Input error. Size <= 0");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input error. Size <= 0");
                return;
            }

            List<Double> numbers = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null && numbers.size() < count) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {
                        double num = Double.parseDouble(part);
                        numbers.add(num);
                        if (numbers.size() == count) break;
                    } catch (NumberFormatException e) {
                        // Пропуск некорректных значений
                    }
                }
            }

            if (numbers.size() < count) {
                System.out.println("Input error. Insufficient number of elements");
                return;
            }

            System.out.println(count);
            for (double number : numbers) {
                System.out.print(number + " ");
            }
            System.out.println();

            double min = numbers.get(0);
            double max = numbers.get(0);
            for (double number : numbers) {
                if (number < min) min = number;
                if (number > max) max = number;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
                writer.write(min + " " + max);
            }
            System.out.println("Saving min and max values in file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = sc.nextLine();
        findMinMax("src\\main\\java\\org\\example\\"+filePath);
    }
}
