public class MineralCard extends Card {
    private double hardness;
    private double gravity;
    private String cleavage;
    private String ecoValue;
    private String crustalAbundance;


    public MineralCard(String name, double hardness, double gravity, String cleavage, String crustalAbundance, String ecoValue) {
        super(name);
        this.hardness = hardness;
        this.gravity = gravity;
        this.cleavage = cleavage;
        this.crustalAbundance = crustalAbundance;
        this.ecoValue = ecoValue;
    }

    public String getCrustalAbundance() {
        return crustalAbundance;
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
