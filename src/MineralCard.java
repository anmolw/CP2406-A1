public class MineralCard extends Card {
    private double hardness;
    private double gravity;
    private String cleavage;
    private String ecoValue;
    private String crystalAbundance;


    public MineralCard(String name, double hardness, double gravity, String cleavage, String crystalAbundance, String ecoValue) {
        super(name);
        this.hardness = hardness;
        this.gravity = gravity;
        this.cleavage = cleavage;
        this.crystalAbundance = crystalAbundance;
        this.ecoValue = ecoValue;
    }

    public String getCrystalAbundance() {
        return crystalAbundance;
    }

    public double getHardness() {
        return hardness;
    }

    public double getGravity() {
        return gravity;
    }

    public String getCleavage() {
        return cleavage;
    }

    public String getEcoValue() {
        return ecoValue;
    }
}
