package org.example;

public class Dog extends Animal {
    public Dog(String name, int age, double weight) {
        super(name, age,weight);
    }

    @Override
    public double getFeedInfoKg() {
        return Math.ceil((this.getWeight()*0.3)*100)/100;
    }


    @Override
    public String toString() {
        return "Dog name = " + getName() + ", age = " + getAge() + " ,mass=" +getWeight()+",feed = " + getFeedInfoKg();
    }
}
