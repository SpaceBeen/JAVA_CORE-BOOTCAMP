package ru.s21.rogue;

import java.util.Random;

import static ru.s21.rogue.Potion.effect.*;


public class Potion implements Item, Consumable {

    int id;

    enum effect {
        E_Nothing,
        E_Healing,
        E_ExtraHealing,
        E_Strength,
        E_Poison,
        E_Confusion,
        E_Blindness,
        E_Restore,
        E_DetMagic,
        E_DetMonsters,
        E_LevelUp,
        E_Paralyze,
        E_Haste,
        E_Truesight
    }

    ;

    record PotionTemplate(
            int pct, // probability of this potion being randomly generated
            int cumPct,// cumulative probability
            String name,
            effect eff,
            int worth,
            int color,
            boolean discovered,
            String message
    ) {
    }

    ;

    static PotionTemplate[] PotionLib = new PotionTemplate[]{
            new PotionTemplate(15, 15, "healing", E_Healing, 130, 0, false, "You begin to feel better."),
            new PotionTemplate(15, 30, "strength", E_Strength, 150, 0, false, "You feel stronger, what bulging muscles!"),
            new PotionTemplate(14, 44, "restore strength", E_Restore, 120, 0, false, "Hey, this tastes great, it make you feel warm all over."),
            new PotionTemplate(10, 54, "paralysis", E_Paralyze, 50, 0, false, "You feel your body seizing up, you can't move!"),
            new PotionTemplate(8, 62, "confusion", E_Confusion, 50, 0, false, "Wait, what's going on here. Huh? What? Who?"),
            new PotionTemplate(8, 70, "poison", E_Poison, 50, 0, false, "You feel very sick now."),
            new PotionTemplate(6, 76, "monster detection", E_DetMonsters, 120, 0, false, "You feel like you are not alone."),
            new PotionTemplate(6, 82, "detect magic", E_DetMagic, 105, 0, false, "You sense the presence of magic."),
            new PotionTemplate(5, 87, "extra healing", E_ExtraHealing, 180, 0, false, "You begin to feel much better."),
            new PotionTemplate(4, 91, "haste", E_Haste, 200, 0, false, "Tastes like coffee, everything seems to slow down."),
            new PotionTemplate(4, 95, "blindness", E_Blindness, 50, 0, false, "A cloak of darkness falls around you."),
            new PotionTemplate(2, 97, "raise level", E_LevelUp, 220, 0, false, "You feel more experienced."),
            new PotionTemplate(2, 99, "truesight", E_Truesight, 170, 0, false, "Tastes like slime-mold juice."),
            new PotionTemplate(1, 100, "thirst quenching", E_Nothing, 50, 0, false, "Meh, tastes pretty dull.")
    };

    Potion(int id) {
        this.id = id;
    }

    static Potion newPotion(String name) {
        boolean ok = false;
        int idx = 0;
        for (PotionTemplate t : PotionLib) {
            if (t.name.equals(name)) {
                break;
            }
            idx++;
        }
        return new Potion(idx);
    }

    static Potion randPotion() {
        Random rand = new Random();
        int roll = rand.nextInt(100) + 1; //1-100
        String name = "";
        for (PotionTemplate t : PotionLib) {
            //debug.Add("rand potion: (%d) chance=%d", roll, t.chance)
            if (roll <= t.cumPct) {
                name = t.name;
                break;
            }
        }
        return newPotion(name);
    }

    public char rune() {
        return '!';
    }

    public String invString() {
        return gndString();
    }

    public String gndString() {
        PotionTemplate templ = PotionLib[id];
        String color = PotionColors[templ.color];
        //if templ.discovered {
        return String.format("a potion of %s [%s]", templ.name, color);
//        } else {
//            return fmt.Sprintf("a %s potion", color)
//        }
    }

    public int worth() {
        PotionTemplate templ = PotionLib[id];
        return templ.worth;
    }

    String String() {
        return gndString();
    }

    public boolean consume(GameState gs) {
        PotionTemplate templ = PotionLib[id];
        doEffect(templ.eff, gs);
        gs.messages.add(templ.message);
        identify();
        return true;
    }

    boolean IsIdentified() {
        return true;
    }

    public void identify() {
        // PotionLib[id].discovered = true;
    }


    String[] PotionColors = new String[]{
            "black",
            "blue",
            "brown",
            "clear",
            "crimson",
            "cyan",
            "gold",
            "green",
            "grey",
            "magenta",
            "pink",
            "plaid",
            "purple",
            "red",
            "silver",
            "tan",
            "tangerine",
            "topaz",
            "turquoise",
            "vermilion",
            "violet",
            "white",
            "yellow",
    };


    void doEffect(effect eff, GameState gs) {
        Random rand = new Random();
        switch (eff) {
            case E_Nothing:
                //do nothing
                break;
            case E_Healing:
                gs.player.adjustHP(gs.player.Level * 3);
                gs.player.setTimer("blind", 0);
                gs.player.setTimer("confusion", 0);
                break;
            case E_ExtraHealing:
                gs.player.adjustHP(gs.player.Level * 5);
                gs.player.setTimer("blind", 0);
                gs.player.setTimer("confusion", 0);
                break;
            case E_Strength:
                gs.player.Str += 1;
                gs.player.maxStr += 1;
                break;
            case E_Poison:
                gs.player.Str -= rand.nextInt(3) + 1;
                break;
            case E_Restore:
                gs.player.Str = gs.player.maxStr;
                break;
            case E_Blindness:
                gs.player.setTimer("blind", 850);
                break;
            case E_Confusion:
                gs.player.setTimer("confused", 20 + rand.nextInt(8));
                break;
            case E_DetMonsters:
                gs.player.setTimer("detMonsters", 850);
                break;
            case E_DetMagic:
                gs.player.setTimer("detMagic", 850);
                break;
            case E_LevelUp:
                gs.player.XP = Player.XPTable[gs.player.Level];
                break;
            case E_Paralyze:
                gs.player.setTimer("paralyzed", 3);
                break;
            case E_Haste:
                // if already hasted, faint for 0-7 turns
                gs.player.setTimer("haste", rand.nextInt(5) + 10);
                break;
            case E_Truesight:
                gs.player.setTimer("truesight", 850);
                gs.player.setTimer("blind", 0);
                break;
        }
    }
}
