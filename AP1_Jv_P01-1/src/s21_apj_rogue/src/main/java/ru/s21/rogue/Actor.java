package ru.s21.rogue;

public interface Actor {

    Coord pos();

    void setPos(Coord newPos);

    char rune();

    void adjustHP(int amt);

    void attack(Actor a, MessageLog msg);

    int armorClass();

    boolean isConfused();

    boolean isBlind();

}
