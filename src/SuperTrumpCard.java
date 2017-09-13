public class SuperTrumpCard extends Card {
    private int trumpType;
    String instructions;


    public SuperTrumpCard(String name, int type, String instructions) {
        super(name);
        this.trumpType = type;
        this.instructions = instructions;

    }

    public int getTrumpType() {
        return trumpType;
    }


    public String getInstructions() {
        return instructions;
    }
}
