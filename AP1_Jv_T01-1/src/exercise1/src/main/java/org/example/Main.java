package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Locale;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Main {
    static Scanner scanner;
    public static void main(String[] args) {

       while (true){
           double x1,x2,x3;
           double y1,y2,y3;
           try{
                scanner = new Scanner(System.in).useLocale(Locale.US);
                x1 = scanner.nextDouble();
                y1 = scanner.nextDouble();
                x2 = scanner.nextDouble();
                y2 = scanner.nextDouble();
                x3 = scanner.nextDouble();
                y3 = scanner.nextDouble();

               double firstLeg = sqrt(pow(x1-x2,2)+pow(y1-y2,2));
               double secondLeg =  sqrt(pow(x2-x3,2)+pow(y2-y3,2));
               double thirdLeg = sqrt(pow(x3-x1,2)+pow(y3-y1,2));
                if(firstLeg+secondLeg>thirdLeg && secondLeg+thirdLeg>firstLeg && firstLeg+thirdLeg>secondLeg){
                    System.out.printf("Perimeter: %.3f",firstLeg+secondLeg+thirdLeg);
                    scanner.close();
                    break;
                }
                else {
                    System.err.println("It isn't triangle");
                    scanner.close();
                    break;
                }
           } catch (InputMismatchException e) {
               System.out.println("Couldn't parse a number. Please, try again");
           }
       }
    }
}