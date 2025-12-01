public class Destroyer extends SpaceShip {

    public Destroyer(String name) {
        super("Destroyer " + name, 150, 80, 50, 48, 52);
        this.dodgeChance = 0.05;
        this.specialEnergyCost = 30;
    }

    @Override
    public void specialAbility(SpaceShip target) {
        if (specialCooldown > 0) { System.out.println("System niegotowy! (" + specialCooldown + ")"); return; }
        if (energy < specialEnergyCost) { System.out.println("Brak mocy (" + specialEnergyCost + ")!"); return; }

        System.out.println(name + " odpala TORPEDĘ ZAPALAJACA!");
        energy -= specialEnergyCost;
        specialCooldown = 4;

        target.takeDamage(40 + attackBonus + specialDmgBonus);
        target.applyBurn(3);
    }

    @Override
    public void defensiveManeuver() {
        System.out.println(name + " wzmacnia PANCERZ!");
        this.isDefending = true;
        this.shields = Math.min(maxShields, shields + 15);
    }
}