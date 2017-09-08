import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    static final String filename = "card.txt";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<Card> pack;
        try {
            pack = readCards(filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Card list file not found.");
            return;
        }
    }

    public static ArrayList<Card> readCards(String filename) throws FileNotFoundException {
        Scanner file;
        file = new Scanner(new FileInputStream(filename));
        System.out.println("File not found");

        ArrayList<Card> pack = new ArrayList<Card>();

        boolean passedHeader = false;
        while (file.hasNextLine()) {
            String line = file.nextLine();
            if (passedHeader) {
                String[] line_split = line.split(",");
                // Create a MineralCard object from each line of the card file excluding the header
                Card card = new MineralCard(line_split[0], Double.parseDouble(line_split[1]), Double.parseDouble(line_split[2]), line_split[3], line_split[4]);
                System.out.println("Created card: " + card.getName());
                pack.add(card);
            }
            passedHeader = true;
        }

        Collections.shuffle(pack);
        return pack;
    }

    public static void addSuperTrumps(ArrayList<Card> pack) {

    }

    public static ArrayList<Card> dealHand(ArrayList<Card> pack) {
        ArrayList<Card> hand = new ArrayList(pack.subList(0, 8));
        pack.subList(0, 8).clear();
        // pack.removeAll(hand);
    }
}
