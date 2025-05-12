package ru.s21.rogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Player implements Actor {
    static int[] XPTable = new int[]{
            0,
            10,
            20,
            40,
            80,
            160,
            320,
            640,
            1300,
            2600,
            5200,
            13000,
            26000,
            50000,
            100000,
            200000,
            400000,
            800000,
            2000000,
            4000000,
            8000000,
    };
    int X, Y;
    char Symbol;
    int moves;
    int depth;
    int HP;
    int maxHP;
    int Str;
    int maxStr;
    int Level;
    int XP;
    int AC;
    Dice Melee;
    int Gold;
    int healCount;
    int foodCount;
    ArrayList<Item> inventory = new ArrayList<>();
    HashMap<String, Equipable> equiped = new HashMap<>();
    HashMap<String, Integer> timer = new HashMap<>();
    String killedBy;

    // -----------------------------------------------------------------------
    void init() {
        Str = 16;
        maxStr = 16;
        HP = 12;
        maxHP = 12;
        AC = 10;
        Level = 1;
        foodCount = 1300;
        timer = new HashMap<>();
        equiped = new HashMap<>() {{
            put("weapon", null);
            put("armor", null);
            put("left", null);
            put("right", null);
        }};
        resetHealCount();
    }

// -----------------------------------------------------------------------
// implement the Actor interface
    //record Coord (int X, int Y){};

    public Coord pos() {
        return new Coord(X, Y);
    }

    public void setPos(Coord newPos) {
        X = newPos.X;
        Y = newPos.Y;
    }

    public char rune() {
        return Symbol;
    }

    public void adjustHP(int amt) {
        HP += amt;
        if (HP > maxHP) {
            HP = maxHP;
        }
    }

    public void attack(Actor m, MessageLog msg) {

        String label;
        if (isBlind()) {
            label = "something";
        } else {
            label = String.format("the %s", m);
        }

        if (attackHits(toHit(), armorClass())) {
            int dmg = rollDamage();
            m.adjustHP(-dmg);
            healCount++; // this shouldn't decrement when fighting
            msg.add("You hit %s for %d damage.", label, dmg);
        } else {
            msg.add("You miss %s.", label);
        }
    }

    Random rand = new Random();

    Boolean attackHits(int toHit, int targetAC) {
        int roll = rand.nextInt(20) + 1;
        int target = toHit - targetAC;
        Boolean isHit = (roll >= target);
        //debug.Add("hit? roll=%d target=%d (%d-%d)  -> %v", roll, target, toHit, targetAC, isHit)
        return isHit;
    }

    public int armorClass() {
        return AC;
    }

    public boolean isConfused() {
        if (timer.get("confused") == null) {
            return false;
        }
        return timer.get("confused") > 0;
    }

    public boolean isBlind() {
        if (timer.get("blind") == null) {
            return false;
        }
        return timer.get("blind") > 0;
    }

// -----------------------------------------------------------------------

    boolean isParalyzed() {
        if (timer.get("paralyzed") == null) {
            return false;
        }
        return timer.get("paralyzed") > 0;
    }

    boolean isHasted() {
        if (timer.get("haste") == null) {
            return false;
        }
        return timer.get("haste") > 0;
    }

// -----------------------------------------------------------------------

    int strAttackBonus() {
        if (Str <= 6) return Str - 7;
        if (Str <= 16) return 0;
        if (Str <= 19) return 1;
        if (Str <= 20) return 2;
        if (Str >= 22) return 3;

        return 0;
    }

    int strDamageBonus() {

        if (Str <= 6)
            return Str - 7;
        if (Str <= 15)
            return 0;
        if (Str <= 17)
            return 1;
        if (Str == 18)
            return 2;
        if (Str == 19)
            return 3;
        if (Str == 20)
            return 4;
        if (Str == 21)
            return 5;
        if (Str >= 22) return 6;

        return 0;

    }

    int toHit() {
        return 21 - Level - strAttackBonus();
    }

    int rollDamage() {
        return damageDice().roll();
    }

    Dice damageDice() {
        return Melee.add(strDamageBonus());
    }

// -----------------------------------------------------------------------

    boolean pickup(Item item) {
        if (item instanceof Gold) {
            Gold += ((ru.s21.rogue.Gold) item).qty;
            //Gold += 10;

            return true;
        }

        inventory.add(item);
        return true;

    }

    void RemoveItem(int idx) {
        inventory.remove(idx); //???

    }

// -----------------------------------------------------------------------

    void addXP(int amt) {
        XP += amt;
    }

    String checkLevel() {
        String msg = "";
        int level = 0;
        for (int xp : XPTable) {
            if (XP < xp) {
                break;
            }
            level++;
        }

        if (Level < level) {
            // Level Up!
            int hp = rand.nextInt(12) + 1;
            HP += hp;
            maxHP += hp;
            msg = String.format("Welcome to level %d! [%+d HP]", level, hp);
        }
        Level = level;
        return msg;
    }

    // -----------------------------------------------------------------------
    void resetHealCount() {
        if (Level < 8) {
            healCount = 21 - Level * 2;
        } else {
            healCount = 3;
        }
    }

    void adjustFoodCount(int amt) {
        foodCount += amt;
        if (foodCount > 1300) {
            foodCount = 1300;
        }
    }

    // -----------------------------------------------------------------------
    void update(MessageLog msg) {

        // Decrement and timers that are set
        for (String k : timer.keySet()) {
            timer.put(k, timer.get(k) - 1);
            if (timer.get(k) < 0) {
                timer.remove(k);
            }
        }

        // At 300 start being hungry, at 150 weak
        // At 0, every turn 20% chance you faint which paralyzes for 4-11 turns
        int f1 = foodCount;
        foodCount--;
        if (f1 > 300 && foodCount <= 300) {
            msg.add("You are starting to get hungry.");
        }
        if (f1 > 150 && foodCount <= 150) {
            msg.add("You are starting to feel weak.");
            //TODO: handle feinting from hunger
        }

        // Levels 1-7, heal one point every [21-LVL*2] turns without fighting.
        // Levels 8+, heal between 1 and [LVL-7] points every three turns without fighting.
        // Note: Also see Attack()
        healCount--;
        if (healCount == 0) {
            if (Level < 8) {
                adjustHP(1);
            } else {
                int amt = rand.nextInt(Level - 7);
                adjustHP(amt);
            }
            resetHealCount();
        }

        moves++;
    }

// -----------------------------------------------------------------------

    int timer(String name) {
        if (timer.get(name) == null) {
            return 0;
        }
        return timer.get(name);
    }

    void setTimer(String name, int val) {
        if (val == 0) {
            timer.remove(name);
        } else {
            timer.put(name, val);
        }
    }

    // -----------------------------------------------------------------------
    String infoString() {
        String condition = "";
        if (isParalyzed()) condition = "Paralyzed";
        else if (foodCount <= 300) condition = "Hungry";
        else if (isConfused()) condition = "Confused";
        else if (isBlind()) condition = "Blind";
        else if (isHasted()) condition = "Haste";


        return String.format(
                "Gold:%-5d  Hp:%2d(%2d)  Str:%-2d  Hit:%-2d  Arm:%-2d  Lvl:%d/21 %s",

                Gold,
                HP,
                maxHP,
                Str,
                toHit(),
                armorClass(),
                depth,
                condition
        );
    }

    // -----------------------------------------------------------------------
    String[] StatsStrings() {


        int savePoison = (7 + Level / 2);
        int saveMagic = (4 + Level / 2);
        Dice dice = damageDice();

        return new String[]{

                String.format("Level:  %d", Level),
                "",
                String.format("Hit Points: %d / %d", HP, maxHP),
                "",
                String.format("Strength:   %d / %d", Str, maxStr),
                //fmt.Sprintf("(%+d hit, %d dmg)", p.StrAttackBonus(), p.StrDamageBonus()),
                String.format(" %+d hit", strAttackBonus()),
                String.format(" %+d dmg", strDamageBonus()),
                "",
                String.format("THAC0:  %d    (%+d)", toHit(), strAttackBonus()),
                String.format("Damage: %d-%-2d  (%+d)", dice.min(), dice.max(), strDamageBonus()),
                String.format("Armor:  %d", AC),
                "",
                String.format("Poison: %d", savePoison),
                String.format("Magic:  %d", saveMagic),
                "",
                String.format("XP:     %d", XP),
                String.format("Next:   %d", XPTable[Level]),
        };
    }

    int score() {
        int sum = Gold;
        for (Item item : inventory) {
            sum += item.worth();
        }
        return sum;
    }

}
