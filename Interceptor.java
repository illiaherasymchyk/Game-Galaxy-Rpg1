public class Interceptor extends SpaceShip {

    public Interceptor(String name) {
        super("Interceptor " + name, 80, 20, 100, 23, 27);
        this.dodgeChance = 0.30;
        this.specialEnergyCost = 40;
    }

    @Override
    public void specialAbility(SpaceShip target) {
        if (specialCooldown > 0) { System.out.println("System przegrzany! (" + specialCooldown + ")"); return; }
        if (energy < specialEnergyCost) { System.out.println("Za malo energii (" + specialEnergyCost + ")!"); return; }

        System.out.println(name + " odpala RAKIETĘ EMP!");
        energy -= specialEnergyCost;
        specialCooldown = 3;

        target.takeDamage(30 + attackBonus + specialDmgBonus);
        target.applyStun();
    }

    @Override
    public void defensiveManeuver() {
        System.out.println(name + " wykonuje SZYBKI UNIK!");
        this.energy = Math.min(maxEnergy, energy + 20);
        this.isDefending = true;
    }
}