package org.blackjack.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private static final int DEALER_STOP = 17;
    private static final int TOTAL_CARDS = 51;
    private static final int BLACK_JACK = 21;

    public HBox dealerCardsHBox;
    public HBox playerCardsHBox;

    public Label dealerScoreLabel;
    public Label playerScoreLabel;

    public Button hitMeButton;
    public Button stayButton;
    public Button resetButton;

    // card's file name and its worth
    private Map<Integer, Integer> cardWorth = new HashMap<Integer, Integer>() {{
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
        put(5, 5);
        put(6, 6);
        put(7, 7);
        put(8, 8);
        put(9, 9);
        put(10, 10);
        put(11, 10);
        put(12, 10);
        put(13, 10);
        put(14, 1);
        put(15, 2);
        put(16, 3);
        put(17, 4);
        put(18, 5);
        put(19, 6);
        put(20, 7);
        put(21, 8);
        put(22, 9);
        put(23, 10);
        put(24, 10);
        put(25, 10);
        put(26, 10);
        put(27, 1);
        put(28, 2);
        put(29, 3);
        put(30, 4);
        put(31, 5);
        put(32, 6);
        put(33, 7);
        put(34, 8);
        put(35, 9);
        put(36, 10);
        put(37, 10);
        put(38, 10);
        put(39, 10);
        put(40, 1);
        put(41, 2);
        put(42, 3);
        put(43, 4);
        put(44, 5);
        put(45, 6);
        put(46, 7);
        put(47, 8);
        put(48, 9);
        put(49, 10);
        put(50, 10);
        put(51, 10);
        put(52, 10);
    }};

    private ObservableList<Card> dealerCards = FXCollections.observableArrayList();
    private ObservableList<Card> playerCards = FXCollections.observableArrayList();

    private Random random = new Random();
    private boolean hittingDealer = true;
    private int playerScore;
    private boolean playerStay = false;
    private boolean gameOver = false;

    private String dealerScoreText = "Dealer score: ";
    private String playerScoreText = "Player score: ";

    public void initialize(URL location, ResourceBundle resources) {
        // create change listener for observable list of playerCards. when we add a item to list it fires
        playerCards.addListener((ListChangeListener<Card>) observable -> {
            if (observable.next()) {
                if (observable.wasAdded()) {
                    checkPlayer(observable.getList());
                }
            }
        });

        dealerCards.addListener((ListChangeListener<Card>) observable -> {
            if (observable.next()) {
                if (observable.wasAdded()) {
                    checkDealer(observable.getList());
                }
            }
        });

        hitToPlayer();
        hitToDealer();
    }

    /**
     * check if player won
     *
     * @param cards player's cards
     */
    private void checkPlayer(ObservableList<? extends Card> cards) {
        System.out.println("Player:" + cards);
        int sum = calcScore(cards);
        // check if player's score over blackjack
        if (sum > BLACK_JACK) {
            playerBustedLabels();
            hitMeButton.setDisable(true);
            stayButton.setDisable(true);
            resetButton.requestFocus();
            return;
        }
        playerScoreLabel.setText(playerScoreText + String.valueOf(sum));
    }

    private void playerBustedLabels() {
        playerScoreLabel.setText("BUSTED");
        dealerScoreLabel.setText("Dealer wins!");
    }

    /**
     * action of fxml. happens when user presses Hit Me button
     *
     * @param event
     */
    public void hitMeAction(ActionEvent event) {
        hitPlayer();
    }

    private void hitPlayer() {
        // generate rundom card from 0 to TOTAL_CARDS and add one because we count cards from 1
        int cardNum = random.nextInt(TOTAL_CARDS) + 1;

        // place card's image in box
        ImageView cardImg = new ImageView(new Image("/images/" + cardNum + ".png"));
        playerCardsHBox.getChildren().add(cardImg);

        // create placed card and add it in player's cards list
        Card card = new Card(cardNum, cardWorth.get(cardNum), true);
        playerCards.add(card);
    }

    /**
     * action of fxml. happens when user pushes Stay button
     *
     * @param event
     */
    public void stayAction(ActionEvent event) {
        hitMeButton.setDisable(true);
        stayButton.setDisable(true);
        playerScore = calcScore(playerCards);
        playerStay = true;
        flipDealerCard();
        // while dealer can hit cards do hitting for dealer
        while (hittingDealer) {
            hitDealer();
        }
        // we need to check if game over because it can be finished at start but not when hits end for dealer
        if (!gameOver) {
            checkDealer();
        }
    }

    /**
     * calculate score of cards
     *
     * @param cards
     * @return score
     */
    private int calcScore(ObservableList<? extends Card> cards) {
        return cards.stream().mapToInt(Card::getWorth).sum();
    }

    private void flipDealerCard() {
        // remove dealer's cards from box
        dealerCardsHBox.getChildren().clear();
        // add dealer's cards according with it's cards list
        dealerCards.stream().forEach(card -> {
            dealerCardsHBox.getChildren().add(new ImageView(new Image("/images/" + card.getId() + ".png")));
        });
    }

    private void hitDealer() {
        hitDealer(true);
    }

    /**
     * action of fxml. happens when user clicks Reset button
     *
     * @param event
     */
    public void resetAction(ActionEvent event) {
        resetPlay();
    }

    private void resetPlay() {
        // reset player's fields
        playerCardsHBox.getChildren().clear();
        playerCards.clear();
        playerScoreLabel.setText(playerScoreText + "0");

        // reset dealer's fields
        dealerCardsHBox.getChildren().clear();
        dealerCards.clear();
        dealerScoreLabel.setText(dealerScoreText + "0");

        // turn on buttons
        hitMeButton.setDisable(false);
        stayButton.setDisable(false);
        hitMeButton.requestFocus();

        // reset util variables
        hittingDealer = true;
        playerStay = false;
        gameOver = false;

        // hit two cards to player
        hitToPlayer();
        // hit two cards for dealer
        hitToDealer();
    }

    private void hitToPlayer() {
        hitPlayer();
        hitPlayer();
    }

    private void hitToDealer() {
        hitDealer();
        hitDealer(false);
    }

    private void hitDealer(boolean faceup) {
        // generate random card's number
        int cardNum = random.nextInt(TOTAL_CARDS) + 1;
        // create card
        Card card = new Card(cardNum, cardWorth.get(cardNum), faceup);

        // if we passed card faceup = true - show face of card else show back of card
        ImageView cardImg;
        if (faceup) {
            cardImg = new ImageView(new Image("/images/" + cardNum + ".png"));
        } else {
            cardImg = new ImageView(new Image("/images/card_back.png"));
        }
        // add card's image to box
        dealerCardsHBox.getChildren().add(cardImg);
        // add card to dealer list
        dealerCards.add(card);
    }

    /**
     * function for shortcut
     */
    private void checkDealer() {
        checkDealer(dealerCards);
    }

    /**
     * check dealers state using it's cards
     *
     * @param cards
     */
    private void checkDealer(ObservableList<? extends Card> cards) {
        System.out.println("Dealer:" + cards);

        // if player clicked stay calculate all score else calculate only face up cards
        int sum = 0;
        if (playerStay) {
            sum = calcScore(cards);
        } else {
            sum = cards.stream().filter(Card::getFlip).mapToInt(Card::getWorth).sum();
        }
        dealerScoreLabel.setText(dealerScoreText + String.valueOf(sum));

        // stop hitting if dealer's score greater then DEALER_STOP (17)
        if (sum > DEALER_STOP) {
            hittingDealer = false;
        }

        // stop if dealer's score over blackjack. dealer busted
        if (sum > BLACK_JACK) {
            gameOver = true;
            hittingDealer = false;
            dealerBustedLables();
            resetButton.requestFocus();
            return;
        }

        // if stop hitting to dealer and player pushed Stay button end game
        if (!hittingDealer && playerStay) {
            gameOver = true;
            // check who won. sum = dealer's score
            if (sum < playerScore) { // dealer busted
                dealerBustedLables();
            } else if (sum > playerScore) { // player busted
                playerBustedLabels();
            } else { // draw
                drawLabels();
            }
        }
        resetButton.requestFocus();
    }

    private void drawLabels() {
        dealerScoreLabel.setText("DRAW");
        playerScoreLabel.setText("DRAW");
    }

    private void dealerBustedLables() {
        dealerScoreLabel.setText("BUSTED");
        playerScoreLabel.setText("Player wins!");
    }

    class Card {
        // id of card equals image's file name
        private Integer id;
        private Integer worth;
        private Boolean flip;

        public Card(Integer id, Integer worth, Boolean flip) {
            this.id = id;
            this.worth = worth;
            this.flip = flip;
        }

        public Integer getId() {
            return id;
        }

        public Integer getWorth() {
            return worth;
        }

        public Boolean getFlip() {
            return flip;
        }

        @Override
        public String toString() {
            return "Card{" + "id=" + id + ", worth=" + worth + ", flip=" + flip + '}';
        }
    }
}
