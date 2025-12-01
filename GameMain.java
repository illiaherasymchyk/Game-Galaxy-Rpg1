import java.util.*;

public class GameMain {
    private static Scanner scanner = new Scanner(System.in);

    private static boolean farmAvailable = true;
    private static int lastFarmTier = 0;

    public static void main(String[] args) {
        System.out.println("=== SPACE TACTICS: ULTIMATE BALANCE ===");

        SpaceShip player = createCharacter();
        int sector = 1;
        int galaxy = 1;

        while (player.isAlive()) {
            int currentTier = (sector - 1) / 3;
            if (currentTier > lastFarmTier) {
                farmAvailable = true;
                lastFarmTier = currentTier;
            }

            System.out.println("\n########################################");
            System.out.println("GALAKTYKA " + galaxy + " | SEKTOR " + sector + "/20");
            System.out.println("Kasa: " + player.getScrap() + "$");
            System.out.println("########################################");

            if (sector % 10 == 0) {
                runBossBattle(player, sector, galaxy);
                if (player.isAlive()) {
                    if (sector == 20) break;
                    galaxy++; sector++; player.startTurn();
                }
                continue;
            }
            if (sector % 3 == 0 && sector % 10 != 0 && sector != 9 && sector != 19) {
                System.out.println("\n! ELITARNY WRÓG BLOKUJE DROGĘ !");
                AlienShip miniBoss = new AlienShip(sector, galaxy);
                battle(player, miniBoss);
                if (player.isAlive()) { sector++; player.startTurn(); }
                continue;
            }

            System.out.println("Wybierz akcję:");
            System.out.println("1. Patrol (Walka - dalej)");
            System.out.println("2. Sklep (Ulepszenia)");

            if (farmAvailable) System.out.println("3. Mirna Strefa (Dostępne!)");
            else System.out.println("3. Mirna Strefa (Niedostępne - czekaj do nast. strefy)");

            int choice = -1;
            if (scanner.hasNextInt()) choice = scanner.nextInt(); else scanner.next();

            if (choice == 1) {
                AlienShip enemy = new AlienShip(sector, galaxy);
                battle(player, enemy);
                if (player.isAlive()) {
                    sector++;
                    player.startTurn();
                }
            } else if (choice == 2) {
                visitShop(player, galaxy);
            } else if (choice == 3) {
                if (farmAvailable) {
                    runFarm(player);
                    farmAvailable = false;
                } else {
                    System.out.println(">>> Już tu byłeś! Leć dalej (Patrol).");
                }
            } else {
                System.out.println("Nieznana komenda.");
            }
        }

        if (!player.isAlive()) System.out.println("\nGAME OVER.");
        else System.out.println("\nGRATULACJE! PRZESZEDŁEŚ GRĘ!");
    }

    private static void runFarm(SpaceShip p) {
        System.out.println("\n--- MIRNA STREFA ---");
        System.out.println("Bezpieczne wydobycie surowców...");
        int gold = 0;
        for(int i=0; i<5; i++) {
            gold += 3 + new Random().nextInt(3);
        }
        System.out.println("Zarobiłeś: " + gold + "$ (Brak XP)");
        p.addLoot(gold);
    }

    private static void runBossBattle(SpaceShip player, int sector, int galaxy) {
        System.out.println("\n*********************************");
        System.out.println("!!! TERAZ WALKA Z BOSSEM !!!");
        System.out.println("*********************************");
        String bossName = (sector == 20) ? "IMPERATOR" : "STRAŻNIK";
        System.out.println("CEL: " + bossName);

        AlienShip boss = new AlienShip(sector, galaxy);
        battle(player, boss);
    }

    private static void battle(SpaceShip player, AlienShip enemy) {
        while (player.isAlive() && enemy.isAlive()) {
            player.startTurn();
            enemy.startTurn();

            System.out.println("\n--------------------------------");
            System.out.println("[TY]   " + player);
            System.out.println("[WROG] " + enemy);
            System.out.println("--------------------------------");

            System.out.println("!!! WROG PLANUJE: " + enemy.getNextMoveIntent() + " !!!");

            System.out.println("1. Atak");
            System.out.println("2. Specjal Atak");
            System.out.println("3. OBRONA (100% Blok)");
            System.out.println("4. Apteczka (Masz: " + player.getRepairKits() + ")");

            System.out.print("> ");
            int act = -1;
            if (scanner.hasNextInt()) act = scanner.nextInt(); else scanner.next();

            if (act == 1) player.attack(enemy);
            else if (act == 2) player.specialAbility(enemy);
            else if (act == 3) player.defensiveManeuver();
            else if (act == 4) player.useRepairKit();
            else System.out.println("Zly klawisz!");

            if (!enemy.isAlive()) {
                if (enemy.isFinalBoss()) {
                    System.out.println("BOSS POKONANY! +300$ +200XP");
                    player.addLoot(300); player.gainExperience(200);
                } else if (enemy.isMiniBoss()) {
                    System.out.println("Elita pokonana. +80$ +100XP");
                    player.addLoot(80); player.gainExperience(100);
                } else {
                    System.out.println("Zwyciestwo. +30$ +40XP");
                    player.addLoot(30); player.gainExperience(40);
                }
                return;
            }

            System.out.println("\n--- Tura Wroga ---");
            enemy.executeTurn(player);
        }
    }

    private static SpaceShip createCharacter() {
        System.out.println("1. Interceptor (Szybki, 25 DMG, 30% Unik)");
        System.out.println("2. Destroyer (Ciezki, 50 DMG, 5% Unik)");
        int t = 0;
        if(scanner.hasNextInt()) t = scanner.nextInt(); else scanner.next();
        scanner.nextLine();
        System.out.print("Nazwa: ");
        String name = scanner.nextLine();
        return (t==1) ? new Interceptor(name) : new Destroyer(name);
    }

    private static void visitShop(SpaceShip p, int galaxy) {
        System.out.println("\n--- SKLEP (" + p.getScrap() + "$) ---");
        System.out.println("UWAGA: Kazdy kolejny zakup tego samego typu jest o 25$ drozszy!");

        while (true) {
            System.out.println("1. Ulepsz Dziala (+5 DMG)              - " + p.getUpgradePrice(1) + "$");
            System.out.println("2. Wzmocnij Kadlub (HP)                - " + p.getUpgradePrice(2) + "$");
            System.out.println("3. Ulepsz Specjal (+3 DMG)   - " + p.getUpgradePrice(3) + "$");
            System.out.println("4. Ulepsz Silniki (+2% Unik)           - " + p.getUpgradePrice(4) + "$");
            System.out.println("5. Kup Apteczke                        - 30$");
            System.out.println("0. Wyjdz");

            System.out.print("Kupujesz (0-5): ");
            int k = -1;
            if(scanner.hasNextInt()) k = scanner.nextInt(); else scanner.next();

            if (k == 0) break;

            if (k >= 1 && k <= 4) p.upgrade(k, galaxy);
            else if (k == 5) p.buyKit();
            else System.out.println("Nie ma takiego towaru.");

            System.out.println("Zostalo kasy: " + p.getScrap() + "$");
        }
    }
}