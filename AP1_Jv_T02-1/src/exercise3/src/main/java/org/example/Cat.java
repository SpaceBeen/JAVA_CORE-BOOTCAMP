package org.example;

public class Cat extends Animal implements Omnivore{
    public Cat(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return "Cat name=" + getName() + ", age = " + getAge() + '.' + hunt();
    }

    @Override
    public String hunt() {
        return "I can hunt for mice";
    }
}
