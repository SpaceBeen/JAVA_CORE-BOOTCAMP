package org.example;

import java.util.List;

public class AnimalIterator implements BaseIterator{
   private List<Animal> animals;
   private int currentIndex;

   public AnimalIterator(List<Animal> animals) {
       this.animals = animals;
   }

    @Override
    public Object next() {
        return animals.get(currentIndex++);
    }

    @Override
    public boolean hasNext() {
        return currentIndex < animals.size();
    }

    @Override
    public void reset() {
        currentIndex = 0;
    }
}
