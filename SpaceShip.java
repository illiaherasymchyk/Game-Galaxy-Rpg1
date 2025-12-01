import java.util.Random;

public abstract class SpaceShip {
    protected String name;
    protected int hull;
    protected int maxHull;
    protected int shields;
    protected int maxShields;
    protected int energy;
    protected int maxEnergy;

    protected int minDmg;
    protected int maxDmg;

    protected double dodgeChance = 0.0;
    protected int specialEnergyCost = 0;
    protected int specialDmgBonus = 0;

    protected int weaponUpgCount = 0;
    protected int hullUpgCount = 0;
    protected int specialUpgCount = 0;
    protected int engineUpgCount = 0;

    protected boolean isStunned = false;
    protected int burnTurns = 0;
    protected int specialCooldown = 0;
    protected boolean isDefending = false;

    protected int scrap = 0;
    protected int repairKits = 1;
    protected int attackBonus = 0;
    protected int level = 1;
    protected int xp = 0;
    protected int xpToNextLevel = 50;

    protected static Random random = new Random();

    public SpaceShip(String name, int hull, int shields, int energy, int minDmg, int maxDmg) {
        this.name = name;
        this.maxHull = this.hull = hull;
        this.maxShields = this.shields = shields;
        this.maxEnergy = this.energy = energy;
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
    }

    public abstract void specialAbility(SpaceShip target);
    public abstract void defensiveManeuver();

    public void startTurn() {
        isDefending = false;
        if (energy < maxEnergy) energy = Math.min(maxEnergy, energy + 10);
        if (specialCooldown > 0) specialCooldown--;
        if (burnTurns > 0) {
            int burnDmg = 5;
            hull -= burnDmg;
            burnTurns--;
            System.out.println("\n🔥 " + name + " PŁONIE! (" + burnDmg + " dmg) 🔥");
        }
    }

    public void attack(SpaceShip target) {
        if (isStunned) {
            System.out.println("\n⛔ " + name + " JEST OGŁUSZONY I TRACI TURĘ! ⛔");
            isStunned = false;
            return;
        }
        int baseDamage = random.nextInt(maxDmg - minDmg + 1) + minDmg;
        int totalDamage = baseDamage + attackBonus;

        System.out.println(name + " strzela! (" + totalDamage + " dmg)");
        target.takeDamage(totalDamage);
    }

    public void takeDamage(int damage) {
        if (random.nextDouble() < dodgeChance) {
            System.out.println(name + " >> UNIK! (0 obrażeń)");
            return;
        }
        if (isDefending) {
            System.out.println(">>> PEŁNA OBRONA! Cios zablokowany (0 dmg).");
            return;
        }
        if (shields > 0) {
            int toShields = Math.min(shields, damage);
            shields -= toShields;
            damage -= toShields;
            System.out.println("   [Tarcze] pochłonęły uderzenie.");
        }
        if (damage > 0) {
            hull = Math.max(0, hull - damage);
            System.out.println("   [KADŁUB] -" + damage + " HP! (" + hull + "/" + maxHull + ")");
        }
    }

    public void applyBurn(int turns) { this.burnTurns += turns; System.out.println("\n🔥🔥🔥 " + name + " PODPALONY! 🔥🔥🔥"); }
    public void applyStun() { this.isStunned = true; System.out.println("\n⚡⚡⚡ " + name + " OGŁUSZONY! ⚡⚡⚡"); }

    public int getUpgradePrice(int type) {
        int basePrice = 50;
        int count = 0;
        if (type == 1) count = weaponUpgCount;
        else if (type == 2) count = hullUpgCount;
        else if (type == 3) count = specialUpgCount;
        else if (type == 4) count = engineUpgCount;
        return basePrice + (count * 25);
    }

    public void upgrade(int type, int currentGalaxy) {
        int cost = getUpgradePrice(type);
        if (scrap < cost) { System.out.println("Brak kasy! Potrzeba " + cost + "$"); return; }
        scrap -= cost;

        switch (type) {
            case 1: attackBonus += 5; weaponUpgCount++; System.out.println("ULEPSZONO DZIAŁA: +5 DMG"); break;
            case 2: int hpAdd = (currentGalaxy >= 2) ? 20 : 10; maxHull += hpAdd; hull += hpAdd; hullUpgCount++; System.out.println("WZMOCNIONO KADŁUB: +" + hpAdd + " HP"); break;
            case 3: specialDmgBonus += 3; if (specialEnergyCost > 10) specialEnergyCost -= 5; specialUpgCount++; System.out.println("ULEPSZONO SPECJAL: +3 DMG"); break;
            case 4: dodgeChance += 0.02; if (dodgeChance > 0.60) dodgeChance = 0.60; engineUpgCount++; System.out.printf("ULEPSZONO SILNIKI: Unik +2%% (Teraz: %.0f%%)\n", (dodgeChance * 100)); break;
        }
    }

    public void buyKit() {
        if (scrap >= 30) { scrap-=30; repairKits++; System.out.println("Kupiono apteczkę."); }
        else System.out.println("Brak kasy (30$)");
    }

    public void useRepairKit() {
        if (repairKits > 0) {
            repairKits--;
            hull = Math.min(maxHull, hull + 50);
            System.out.println(">>> APTECZKA UŻYTA (+50 HP). Zostało: " + repairKits);
        } else {
            System.out.println("!!! BRAK APTECZEK W EKWIPUNKU !!!");
        }
    }

    public void gainExperience(int amount) {
        xp += amount;
        if(xp >= xpToNextLevel) {
            level++; xp = 0; xpToNextLevel += 50;
            maxHull += 20; maxShields += 10;
            hull = maxHull; shields = maxShields;
            System.out.println("\n*** LEVEL UP! Poziom " + level + " ***");
        }
    }

    public boolean isAlive() { return hull > 0; }
    public String getName() { return name; }
    public int getScrap() { return scrap; }
    public int getRepairKits() { return repairKits; }
    public void addLoot(int s) { scrap += s; }

    @Override
    public String toString() {
        String status = "";
        if (isStunned) status += " [STUN]";
        if (burnTurns > 0) status += " [OGIEŃ]";
        int avgDmg = ((minDmg + maxDmg) / 2) + attackBonus;
        return String.format("[%s Lvl:%d] HP:%d/%d | SHLD:%d | DMG:~%d | UNIK:%.0f%% | KIT:%d%s",
                name, level, hull, maxHull, shields, avgDmg, (dodgeChance*100), repairKits, status);
    }
}