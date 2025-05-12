package org.example;

import java.lang.Math;

public class Cat extends Animal{
    public Cat(String name, int age, double weight) {
        super(name, age,weight);
    }

    @Override
    public String toString() {
        return "Cat name=" + getName() + ", age = " + getAge() + " ,mass=" +getWeight()+",feed = " + getFeedInfoKg();
    }

    @Override
    public double getFeedInfoKg() {
        return Math.ceil((this.getWeight()*0.1)*100)/100;
    }
}
