import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;


public class Game {
    private static final String filename = "card.txt";
    private static HashMap<String, Integer> cleavageMap = new HashMap<>();
    private static HashMap<String, Integer> ecoMap = new HashMap<>();
    private static HashMap<String, Integer> crustalMap = new HashMap<>();
    private static final String[] categories = {"Hardness", "Specific gravity", "Cleavage", "Crustal abundance", "Economic value"};

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
        int numPlayers = getNumberInput(3, 5, "Enter number of players(3-5): ");
        playGame(pack, numPlayers);
    }

    private static int getNumberInput(int min, int max, String message) {
        Scanner input = new Scanner(System.in);
        int inputNum = min - 1;
        boolean err = false;
        do {
            if (err) {
                System.out.println("Number must be between " + min + " and " + max + " inclusive.");
            }
            System.out.print(message);
            inputNum = input.nextInt();
            err = true;
        } while (inputNum < min || inputNum > max);
        return inputNum;
    }

    private static void playGame(ArrayList<Card> pack, int numPlayers) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(dealHand(pack));
        }

        int currentCategory = -1;
        int currentPlayer = 0;
        boolean firstTurn = true;
        MineralCard lastCard = null;

        while (!gameOver(players)) {
            while (!roundOver(players)) {
                if (!firstTurn)
                    currentPlayer = 0;
                if (currentPlayer == numPlayers)
                    currentPlayer--;

                boolean trumpOverride = false;

                while (currentPlayer < numPlayers) {

                    if (players[currentPlayer].passed || players[currentPlayer].hand.size() == 0) {
                        currentPlayer++;
                        continue;
                    }

                    System.out.println("Player " + (currentPlayer + 1) + "'s  turn");
                    displayHand(players[currentPlayer].hand);
                    int choice = 0;
                    if (firstTurn) {
                        choice = getNumberInput(1, players[currentPlayer].hand.size(), "Choose a card: ");
                    } else {
                        System.out.println("Current category: " + categories[currentCategory]);
                        choice = getNumberInput(0, players[currentPlayer].hand.size(), "Choose a card, or type 0 to pass: ");
                        if (choice == 0) {
                            System.out.println("Player " + (currentPlayer + 1) + " passed and drew a card.");
                            players[currentPlayer].hand.add(pack.get(0));
                            players[currentPlayer].passed = true;
                            currentPlayer++;
                            continue;
                        }
                    }

                    Card chosenCard = players[currentPlayer].hand.get(choice - 1);

                    if (chosenCard instanceof MineralCard) {
                        if (firstTurn) {
                            lastCard = (MineralCard) players[currentPlayer].hand.get(choice - 1);

                            for (int j = 0; j < categories.length; j++)
                                System.out.println((j + 1) + ". " + categories[j]);

                            currentCategory = getNumberInput(1, 5, "Choose the category (1-5): ") - 1;
                            System.out.println("Category chosen: " + categories[currentCategory]);
                            players[currentPlayer].hand.remove(lastCard);
                            System.out.println("Played card: " + lastCard.getName());
                            firstTurn = false;
                            trumpOverride = false;
                            currentPlayer++;
                        } else if (trumpOverride || isGreater(currentCategory, (MineralCard) chosenCard, lastCard)) {
                            lastCard = (MineralCard) chosenCard;
                            players[currentPlayer].hand.remove(lastCard);
                            System.out.println("Played card: " + lastCard.getName());
                            currentPlayer++;
                            trumpOverride = false;
                        } else {
                            System.out.println("The card's " + categories[currentCategory] + " must be greater than the last card played.");
                            displayLastCard(lastCard, currentCategory);
                        }
                    } else if (chosenCard instanceof SuperTrumpCard) {
                        int trumpType = ((SuperTrumpCard) chosenCard).getTrumpType();
                        firstTurn = false;
                        trumpOverride = true;
                        System.out.println("Player " + (currentPlayer + 1) + " played SuperTrump card: " + chosenCard.getName());
                        if (trumpType != 5) {
                            System.out.println("Changing category to " + categories[trumpType]);
                            currentCategory = trumpType;
                        } else {
                            for (int j = 0; j < categories.length; j++)
                                System.out.println((j + 1) + ". " + categories[j]);
                            currentCategory = getNumberInput(1, 5, "Choose the category (1-5): ") - 1;
                            System.out.println("Category chosen: " + categories[currentCategory]);
                        }
                        players[currentPlayer].hand.remove(chosenCard);

                        for (Player player : players) {
                            player.passed = false;
                        }
                    }
                }
            }

            for (Player player : players) {
                player.passed = false;
            }

            firstTurn = true;

        }

        System.out.println("Game over!");
    }

    private static void displayLastCard(MineralCard card, int category) {
        String output = "Last card played: " + card.getName() + " " + categories[category] + ": ";
        if (category == 0)
            output = output + card.getHardness();
        else if (category == 1)
            output = output + card.getGravity();
        else if (category == 2)
            output = output + card.getCleavage();
        else if (category == 3)
            output = output + card.getCrustalAbundance();
        else if (category == 4)
            output = output + card.getEcoValue();
        System.out.println(output);

    }


    private static Card getCardWithName(String name, ArrayList<Card> list) {
        for (Card card : list) {
            if (card.getName().equals(name))
                return card;
        }
        return null;
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
        return (numPassed >= players.length - 1);
    }

    private static ArrayList<Card> readCards(String filename) throws FileNotFoundException {
        Scanner file;
        file = new Scanner(new FileInputStream(filename));

        ArrayList<Card> pack = new ArrayList<>();

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
                new SuperTrumpCard("The Mineralogist", 2, "changes the trumps category to Cleavage"),
                new SuperTrumpCard("The Geologist", 5, "change to trumps category of your choice"),
                new SuperTrumpCard("The Geophysicist", 1, "changes the trumps category to Specific Gravity"),
                new SuperTrumpCard("The Petrologist", 3, "changes the trumps category to Crustal Abundance"),
                new SuperTrumpCard("The Miner", 4, "changes the trumps category to Economic Value"),
                new SuperTrumpCard("The Gemmologist", 0, "changes the trumps category to Hardness")};

        pack.addAll(Arrays.asList(superTrumpList));
        return pack;
    }

    private static boolean isValidMove(Card card, Card lastCard, int category) {
        return false;
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

        if (crustalMap.isEmpty()) {
            crustalMap.put("ultratrace", 0);
            crustalMap.put("trace", 1);
            crustalMap.put("low", 2);
            crustalMap.put("moderate", 3);
            crustalMap.put("high", 4);
            crustalMap.put("very high", 5);
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


        if (category == 0) {
            return (card1.getHardness() > card2.getHardness());
        } else if (category == 1) {
            return (card1.getGravity() > card2.getGravity());
        } else if (category == 2) {
            return (cleavageMap.get(card1.getCleavage()) > cleavageMap.get((card2.getCleavage())));
        } else if (category == 3) {
            return (crustalMap.get(card1.getCrustalAbundance()) > cleavageMap.get((card2.getCrustalAbundance())));
        } else if (category == 4) {
            return (ecoMap.get(card1.getEcoValue()) > ecoMap.get(card2.getEcoValue()));
        }
        return false;
    }

    private static void displayHand(ArrayList<Card> hand) {
        System.out.printf("Number\tName\tHardness\tSpecific Gravity\tCleavage\tCrystal Abundance\tEconomic Value\n");
        int count = 1;
        for (Card card : hand) {
            if (card instanceof SuperTrumpCard)
                System.out.println(count + ". \t" + card.getName() + "\t" + ((SuperTrumpCard) card).getInstructions());
            else {
                MineralCard cardCasted;
                cardCasted = (MineralCard) card;
                System.out.println(count + ". \t" + cardCasted.getName() + "\t" + cardCasted.getHardness() + "\t" + cardCasted.getGravity() + "\t" + cardCasted.getCleavage() + "\t" + cardCasted.getCrustalAbundance() + "\t" + cardCasted.getEcoValue());
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
