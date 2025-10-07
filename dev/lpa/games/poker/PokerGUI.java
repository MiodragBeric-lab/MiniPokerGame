package dev.lpa.games.poker;

import dev.lpa.Card;
import dev.lpa.games.Ranking;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PokerGUI extends JFrame {
    private PokerGame game;
    private JTextPane deckArea;
    private JTextPane handsArea;
    private JTextPane remainingCardsArea;
    private JButton startButton;
    private JButton dealButton;
    private JButton shuffleButton;
    private JButton[] discardButtons;

    public PokerGUI() {
        // Inicijalizacija igre (8 igrača, 5 karata po ruci)
        game = new PokerGame(8, 5);

        // Postavke prozora
        setTitle("Poker Game");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 220)); // Svetla pozadina

        // Panel za dugmad
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(200, 200, 200));
        startButton = new JButton("Start Game");
        dealButton = new JButton("Deal Cards");
        shuffleButton = new JButton("Shuffle Deck");

        // Stilizacija dugmadi
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        startButton.setFont(buttonFont);
        dealButton.setFont(buttonFont);
        shuffleButton.setFont(buttonFont);
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        dealButton.setBackground(new Color(50, 50, 150));
        dealButton.setForeground(Color.WHITE);
        shuffleButton.setBackground(new Color(150, 50, 50));
        shuffleButton.setForeground(Color.WHITE);

        buttonPanel.add(startButton);
        buttonPanel.add(dealButton);
        buttonPanel.add(shuffleButton);

        // Tekstualna područja sa stilizovanim tekstom
        deckArea = new JTextPane();
        handsArea = new JTextPane();
        remainingCardsArea = new JTextPane();
        deckArea.setEditable(false);
        handsArea.setEditable(false);
        remainingCardsArea.setEditable(false);
        deckArea.setBorder(BorderFactory.createTitledBorder("Current Deck"));
        handsArea.setBorder(BorderFactory.createTitledBorder("Players' Hands"));
        remainingCardsArea.setBorder(BorderFactory.createTitledBorder("Remaining Cards"));

        // Postavljanje fonta i pozadine
        Font font = new Font("Monospaced", Font.PLAIN, 16);
        deckArea.setFont(font);
        handsArea.setFont(font);
        remainingCardsArea.setFont(font);
        deckArea.setBackground(Color.WHITE);
        handsArea.setBackground(Color.WHITE);
        remainingCardsArea.setBackground(Color.WHITE);

        // Panel za prikaz špila i ruku
        JPanel displayPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayPanel.add(new JScrollPane(deckArea));
        displayPanel.add(new JScrollPane(handsArea));
        displayPanel.add(new JScrollPane(remainingCardsArea));

        // Panel za odbacivanje karata
        JPanel discardPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        discardPanel.setBorder(BorderFactory.createTitledBorder("Discard Cards"));
        discardButtons = new JButton[8];
        for (int i = 0; i < 8; i++) {
            discardButtons[i] = new JButton("Discard for Player " + (i + 1));
            discardButtons[i].setFont(new Font("Arial", Font.PLAIN, 12));
            discardButtons[i].setBackground(new Color(200, 100, 100));
            discardButtons[i].setForeground(Color.WHITE);
            int playerIndex = i;
            discardButtons[i].addActionListener(e -> discardCards(playerIndex));
            discardPanel.add(discardButtons[i]);
        }

        // Dodavanje komponenti u prozor
        add(buttonPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        add(new JScrollPane(discardPanel), BorderLayout.EAST);

        // Akcije dugmadi
        startButton.addActionListener(e -> startGame());
        dealButton.addActionListener(e -> dealCards());
        shuffleButton.addActionListener(e -> shuffleDeck());

        // Inicijalni prikaz špila
        updateDeckDisplay();
    }

    private void startGame() {
        game.startPlay();
        updateDeckDisplay();
        updateHandsDisplay();
        updateRemainingCardsDisplay();
    }

    private void dealCards() {
        game = new PokerGame(8, 5); // Resetuj igru
        game.startPlay();
        updateHandsDisplay();
        updateRemainingCardsDisplay();
    }

    private void shuffleDeck() {
        Collections.shuffle(game.getDeck());
        updateDeckDisplay();
    }

    private void discardCards(int playerIndex) {
        PokerHand hand = game.getPokerHands().get(playerIndex);
        hand.evalHand(); // Evaluira ruku i popunjava keepers/discard
        List<Card> discards = hand.getDiscard();
        if (!discards.isEmpty()) {
            // Ukloni odbačene karte iz ruke
            hand.getHand().removeAll(discards);
            // Dodeli nove karte iz preostalog špila
            List<Card> remaining = game.getRemainingCards();
            for (int i = 0; i < discards.size() && !remaining.isEmpty(); i++) {
                hand.getHand().add(remaining.remove(0));
            }
            hand.evalHand(); // Ponovo evaluiraj ruku
        }
        updateHandsDisplay();
        updateRemainingCardsDisplay();
    }

    private void updateDeckDisplay() {
        deckArea.setText("");
        appendStyledText(deckArea, "Current Deck:\n", Color.BLACK, true);
        appendStyledDeck(deckArea, game.getDeck(), 4);
    }

    private void updateHandsDisplay() {
        handsArea.setText("");
        appendStyledText(handsArea, "Players' Hands:\n", Color.BLACK, true);
        for (PokerHand hand : game.getPokerHands()) {
            appendStyledText(handsArea, hand.toString() + "\n", Color.BLACK, false);
        }
    }

    private void updateRemainingCardsDisplay() {
        remainingCardsArea.setText("");
        appendStyledText(remainingCardsArea, "Remaining Cards:\n", Color.BLACK, true);
        appendStyledDeck(remainingCardsArea, game.getRemainingCards(), 2);
    }

    private void appendStyledText(JTextPane textPane, String text, Color color, boolean bold) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendStyledDeck(JTextPane textPane, List<Card> deck, int rows) {
        int cardsInRow = deck.size() / rows;
        for (int i = 0; i < rows; i++) {
            int startIndex = i * cardsInRow;
            int endIndex = Math.min(startIndex + cardsInRow, deck.size());
            for (Card card : deck.subList(startIndex, endIndex)) {
                Color color = (card.suit() == Card.Suit.HEART || card.suit() == Card.Suit.DIAMOND) ? Color.RED : Color.BLACK;
                appendStyledText(textPane, String.format("%-10s", card), color, false);
            }
            appendStyledText(textPane, "\n", Color.BLACK, false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PokerGUI gui = new PokerGUI();
            gui.setVisible(true);
        });
    }

    // Getteri za pristup privatnim poljima PokerGame
    public List<Card> getDeck() {
        return game.getDeck();
    }

    public List<PokerHand> getPokerHands() {
        return game.getPokerHands();
    }

    public List<Card> getRemainingCards() {
        return game.getRemainingCards();
    }
}