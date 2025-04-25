package ru.s21.rogue;

import java.util.HashMap;
import java.util.Random;


public class Armor implements Equipable, Item {

    String Name;
    int AC;
    int ench;
    boolean cursed;
    int worth;
    static HashMap<String, ArmorTemplate> ArmorLib = new HashMap<String, ArmorTemplate>() {{
        put("leather armor", new ArmorTemplate(8, 5));
        put("ring mail", new ArmorTemplate(7, 30));
        put("scale mail", new ArmorTemplate(6, 3));
        put("chain mail", new ArmorTemplate(5, 75));
        put("banded mail", new ArmorTemplate(4, 90));
        put("plate mail", new ArmorTemplate(3, 440));
    }};

    record ArmorTemplate(int AC, int worth) {
    }

    ;

    Armor(String name, int AC, int worth, int ench, boolean cursed) {
        this.Name = name;
        this.AC = AC;
        this.worth = worth;
        this.ench = ench;
        this.cursed = cursed;
    }

    // -----------------------------------------------------------------------
    static Armor newArmor(String name) {
        ArmorTemplate t = ArmorLib.get(name);

        return new Armor(name, t.AC, t.worth, 0, false);
    }

    // -----------------------------------------------------------------------
    static Armor randArmor() {
        // Pick an armor from the list at random
        Random ran = new Random();
        int i = ran.nextInt(ArmorLib.size());
        Armor a = null;
        for (String name : ArmorLib.keySet()) {
            if (i == 0) {
                a = newArmor(name);
            }
            i--;
        }
        assert a != null;
        a.ench = randEnchant(8, 20);
        boolean cursed = false;
        if (a.ench < 0) {
            cursed = true;
            a.cursed = cursed;
        }
        return a;
    }

    static int randEnchant(int enchantProb, int cursedProb) {
        // 10% chance of a cursed weapon with -1 to -3 penalty, and a 5% chance
        // of an enchanted weapon with a +1 to +3 bonus.
        Random ran2 = new Random();
        int ench = 0;
        if (ran2.nextInt(100) < enchantProb) { // enchanted
            ench = ran2.nextInt(2) + 1;
        } else if (ran2.nextInt(100) < cursedProb) { // cursed
            ench = -1 * (ran2.nextInt(2) + 1);
        }
        return ench;
    }

    // -----------------------------------------------------------------------
    public boolean equip(Player p, MessageLog msg) {
        if (p.equiped.get("armor") == this) {
            unequip(p, msg);
            return false;
        }
        if (p.equiped.get("armor") != null) {
            msg.add("You need to take off the %s first.", p.equiped.get("armor"));
            return false;
        }
        p.equiped.put("armor", this);
        p.AC = this.AC - this.ench;
        msg.add("You are now wearing the %s.", this);
        return true;
    }

    // -----------------------------------------------------------------------
    public boolean unequip(Player p, MessageLog msg) {
        if (p.equiped.get("armor") == null) { //???
            msg.add("You aren't wearing the %s.", this);
            return false;
        }
        if (cursed) {
            msg.add("You cannot take off the %s, it's cursed!", this);
            return false;
        }
        p.equiped.put("armor", null);
        p.AC = 10;
        msg.add("You take off the %s.", this);
        return true;
    }

    // -----------------------------------------------------------------------
    public char rune() {
        return ']';
    }

    public String gndString() {
        return String.format("some %s", this);
    }

    public String invString() {
        String cursed = "";
        if (this.cursed) {
            cursed = " {cursed}";
        }
        return String.format("%+d %s [%d]%s", this.ench, this.Name, this.AC - this.ench, cursed);
    }

    public int worth() {
        if (this.ench < 0) {
            return 0;
        } else {
            return (1 + (10 * this.ench)) * this.worth;
        }
    }

    String String() {
        return this.Name;
    }

    // -----------------------------------------------------------------------


}
