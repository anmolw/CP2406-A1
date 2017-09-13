public class SuperTrumpCard extends Card {
    private int trumpType;

    public SuperTrumpCard(String name, int type) {
        super(name);
        this.trumpType = type;
    }

    public int getTrumpType() {
        return trumpType;
    }
}
