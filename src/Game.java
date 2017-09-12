import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;


public class Game {
    private static final String filename = "card.txt";
    private static HashMap<String, Integer> cleavageMap = new HashMap<String, Integer>();
    private static HashMap<String, Integer> ecoMap = new HashMap<String, Integer>();
    private static HashMap<String, Integer> crystalMap = new HashMap<String, Integer>();


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<Card> pack;
        try {
            pack = readCards(filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Card list file not found.");
            return;
        }

        pack = addSuperTrumps(pack);
        Collections.shuffle(pack);
        System.out.println(String.format("Pack has %d cards", pack.size()));
        int inputNum = 0;
        boolean err = false;
        do {
            if (err) {
                System.out.println("Number must be between 3 and 5 inclusive.");
            }
            System.out.print("Enter number of players(3-5): ");
            inputNum = input.nextInt();
            err = true;
        } while (inputNum < 3 || inputNum > 5);

        playGame(pack, inputNum);

        ArrayList<Card> hand = dealHand(pack);
        displayHand(hand);
    }

    private static void playGame(ArrayList<Card> pack, int numPlayers) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(dealHand(pack));
        }

        Scanner input = new Scanner(System.in);
        int currentCategory = -1;
        int currentPlayer = 0;
        boolean firstTurn = true;
        while (!gameOver(players)) {
            while (!roundOver(players)) {
                for (int i = 0; i < numPlayers; i++) {
                    if (players[i].passed)
                        continue;
                    System.out.println("Player " + (i + 1) + "'s  hand");
                    displayHand(players[i].hand);
                    System.out.print("Choose a card to play: ");
                    int choice = input.nextInt();
                    if (firstTurn) {
                        System.out.print("Choose the category: ");
                    }

                }
            }

            for (Player player: players) {
                player.passed = false;
            }

        }
    }

    private static boolean gameOver(Player[] players) {
        int notEmpty = 0;
        for (Player player : players) {
            if (player.hand.size() > 0)
                notEmpty++;
        }
        return (notEmpty <= 1);
    }

    private static boolean roundOver(Player[] players) {
        int numPassed = 0;
        for (Player player : players) {
            if (player.passed) {
                numPassed++;
            }
        }
        return (numPassed == players.length - 1);
    }

    private static ArrayList<Card> readCards(String filename) throws FileNotFoundException {
        Scanner file;
        file = new Scanner(new FileInputStream(filename));

        ArrayList<Card> pack = new ArrayList<Card>();

        boolean passedHeader = false;
        while (file.hasNextLine()) {
            String line = file.nextLine();
            if (passedHeader) {
                // Split the line
                String[] line_split = line.split(",");
                // Create a MineralCard object from each line of the card file excluding the header
                Card card = new MineralCard(line_split[0], Double.parseDouble(line_split[1]), Double.parseDouble(line_split[2]), line_split[3], line_split[4], line_split[5]);
                pack.add(card);
            }
            passedHeader = true;
        }

        System.out.println("Loaded cards successfully.");
        return pack;
    }

    private static ArrayList<Card> addSuperTrumps(ArrayList<Card> pack) {
        Card[] superTrumpList = {
                new SuperTrumpCard("The Mineralogist", 0),
                new SuperTrumpCard("The Geologist", 1),
                new SuperTrumpCard("The Geophysicist", 2),
                new SuperTrumpCard("The Petrologist", 3),
                new SuperTrumpCard("The Miner", 4),
                new SuperTrumpCard("The Gemmologist", 5)};

        pack.addAll(Arrays.asList(superTrumpList));
        return pack;
    }

    private static boolean isGreater(int category, MineralCard card1, MineralCard card2) {

        if (cleavageMap.isEmpty()) {
            cleavageMap.put("none", 0);
            cleavageMap.put("poor/none", 1);
            cleavageMap.put("1 poor", 2);
            cleavageMap.put("2 poor", 3);
            cleavageMap.put("1 good", 4);
            cleavageMap.put("1 good/1 poor", 5);
            cleavageMap.put("2 good", 6);
            cleavageMap.put("3 good", 7);
            cleavageMap.put("1 perfect", 8);
            cleavageMap.put("1 perfect/1 good", 9);
            cleavageMap.put("1 perfect/2 good", 10);
            cleavageMap.put("2 perfect/1 good", 11);
            cleavageMap.put("3 perfect", 12);
            cleavageMap.put("4 perfect", 13);
            cleavageMap.put("6 perfect", 14);
        }

        if (crystalMap.isEmpty()) {
            crystalMap.put("ultratrace", 0);
            crystalMap.put("trace", 1);
            crystalMap.put("low", 2);
            crystalMap.put("moderate", 3);
            crystalMap.put("high", 4);
            crystalMap.put("very high", 5);
        }

        if (ecoMap.isEmpty()) {
            ecoMap.put("trivial", 0);
            ecoMap.put("low", 1);
            ecoMap.put("moderate", 2);
            ecoMap.put("high", 3);
            ecoMap.put("high", 4);
            ecoMap.put("very high", 5);
            ecoMap.put("I'm rich!", 6);
        }


        if (category == 1) {
            return (card1.getHardness() > card2.getHardness());
        } else if (category == 2) {
            return (card1.getGravity() > card2.getGravity());
        } else if (category == 3) {
            return (cleavageMap.get(card1.getCleavage()) > cleavageMap.get((card2.getCleavage())));
        } else if (category == 4) {
            return (crystalMap.get(card1.getCrystalAbundance()) > cleavageMap.get((card2.getCrystalAbundance())));
        } else if (category == 5) {
            return (ecoMap.get(card1.getEcoValue()) > ecoMap.get(card2.getEcoValue()));
        }
        return false;
    }

    private static void displayHand(ArrayList<Card> hand) {
        System.out.println("Number\tName\tHardness\tSpecific Gravity\tCleavage\tCrystal Abundance\tEconomic Value");
        int count = 1;
        for (Card card : hand) {
            if (card instanceof SuperTrumpCard)
                System.out.println(count + ". \t" + card.getName());
            else {
                MineralCard cardCasted;
                cardCasted = (MineralCard) card;
                System.out.println(count + ". \t" + cardCasted.getName() + "\t" + cardCasted.getHardness() + "\t" + cardCasted.getGravity() + "\t" + cardCasted.getCleavage() + "\t" + cardCasted.getCrystalAbundance() + "\t" + cardCasted.getEcoValue());
            }
            count++;
        }
    }

    private static ArrayList<Card> dealHand(ArrayList<Card> pack) {
        ArrayList<Card> hand = new ArrayList(pack.subList(0, 8));
        pack.subList(0, 8).clear();
        return hand;
        // pack.removeAll(hand);
    }
}
