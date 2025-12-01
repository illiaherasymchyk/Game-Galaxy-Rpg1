import java.util.Random;

public class AlienShip extends SpaceShip {

    private String nextMove;
    private boolean isBoss = false;
    private boolean isFinalBoss = false;

    public AlienShip(int sector, int galaxy) {
        super("Wróg", 0, 0, 0, 0, 0);
        int difficultyMult = galaxy;

        if (sector % 10 == 0) {
            this.isFinalBoss = true;
            this.name = "GUARDIAN OF GALAXY " + galaxy;
            initStats(300 * difficultyMult, 100 * difficultyMult, 200, 25 * difficultyMult, 35 * difficultyMult);
            this.dodgeChance = 0.0;
        }
        else if (sector % 3 == 0 && sector % 10 != 0 && sector != 9) {
            this.isBoss = true;
            this.name = "Elite Mercenary";
            initStats(160 * difficultyMult, 60 * difficultyMult, 100, 40 * difficultyMult, 50 * difficultyMult);

            this.repairKits = 1;
            this.dodgeChance = 0.20;
        }
        else {
            generateRandomEnemy(difficultyMult);
            this.dodgeChance = 0.0;
        }

        planNextMove();
    }

    private void initStats(int hp, int shld, int en, int minD, int maxD) {
        this.maxHull = this.hull = hp;
        this.maxShields = this.shields = shld;
        this.maxEnergy = this.energy = en;
        this.minDmg = minD;
        this.maxDmg = maxD;
    }

    private void generateRandomEnemy(int mult) {
        int type = random.nextInt(3);

        if (type == 0) {
            name = "Space Pirate";
            initStats(80 * mult, 20 * mult, 0, 16 * mult, 22 * mult);
        } else if (type == 1) {
            name = "Droid Fighter";
            initStats(60 * mult, 50 * mult, 20, 12 * mult, 18 * mult);
        } else {
            name = "Void Scavenger";
            initStats(70 * mult, 0, 0, 18 * mult, 24 * mult);
        }
    }

    public void planNextMove() {
        if (isStunned) { nextMove = "OGŁUSZONY"; return; }

        if (isFinalBoss) {
            int roll = random.nextInt(100);
            if (roll < 30) nextMove = "BLOKADA SYSTEMÓW (Zakaz ataku!)";
            else if (roll < 60) nextMove = "PIEKIELNY OGIEŃ (Podpalenie)";
            else nextMove = "MEGA LASER (Potężny atak)";
            return;
        }

        if (isBoss) {
            // ЛОГИКА МИНИ-БОССА:
            // 1. Если мало жизней (< 40%) и есть аптечка -> ЛЕЧИТЬСЯ
            if (hull < maxHull * 0.4 && repairKits > 0) {
                nextMove = "UŻYCIE APTECZKI";
            }
            // 2. Если мало щитов -> РЕГЕНИТЬ
            else if (shields < maxShields / 3) {
                nextMove = "REGENERACJA TARCZ";
            }
            // 3. Иначе -> СИЛЬНО БИТЬ
            else {
                nextMove = "CIĘŻKI OSTRZAŁ";
            }
            return;
        }

        int roll = random.nextInt(10);
        if (roll < 3) nextMove = "SZYBKI ATAK";
        else if (roll < 6 && shields > 0) nextMove = "OBRONA";
        else nextMove = "Zwykły Atak";
    }

    public String getNextMoveIntent() { return nextMove; }

    public void executeTurn(SpaceShip player) {
        if (isStunned) {
            System.out.println("\n⛔⛔⛔ " + name + " JEST OGŁUSZONY! ⛔⛔⛔");
            isStunned = false; planNextMove(); return;
        }

        System.out.println("\n>>> " + name + " wykonuje: " + nextMove);

        // ДЕЙСТВИЯ
        if (nextMove.contains("UŻYCIE APTECZKI")) {
            System.out.println("!!! BOSS SIĘ LECZY !!!");
            useRepairKit();
        }
        else if (nextMove.contains("BLOKADA")) {
            System.out.println("BOSS ZAKŁÓCA SYSTEMY!");
            player.applyStun(); player.takeDamage(15);
        }
        else if (nextMove.contains("PIEKIELNY")) {
            System.out.println("BOSS PODPALA STATEK!");
            player.takeDamage(20); player.applyBurn(3);
        }
        else if (nextMove.contains("MEGA LASER")) {
            player.takeDamage((int)(maxDmg * 1.5));
        }
        else if (nextMove.contains("REGENERACJA") || nextMove.contains("OBRONA")) defensiveManeuver();
        else attack(player);

        planNextMove();
        startTurn();
    }

    @Override public void specialAbility(SpaceShip target) {}

    @Override public void defensiveManeuver() {
        shields = Math.min(maxShields, shields + 30); // Босс регенит больше
        isDefending = true;
        System.out.println(name + " regeneruje tarcze (+30).");
    }

    public boolean isFinalBoss() { return isFinalBoss; }
    public boolean isMiniBoss() { return isBoss; }
}