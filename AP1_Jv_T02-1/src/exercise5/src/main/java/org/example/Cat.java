package org.example;

import java.util.concurrent.TimeUnit;

public class Cat extends Animal{
    public Cat(String name, int age) {
        super(name, age);
    }

    @Override
    double goToWalk() {
        double walkTime = getAge() * 0.25;
        try {
            TimeUnit.SECONDS.sleep((long) walkTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return walkTime;
    }

    @Override
    public String toString() {
        return "Cat name = " + getName() + ", age = " + getAge();
    }
}
