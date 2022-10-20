package Cards;

import java.util.ArrayList;
import java.util.Collections;

public class CardsPackage { // Package of 52 cards as should be in "Texas Hold'em" (No Jokers)
    ArrayList<Card> cards;

    public CardsPackage(){
        cards = new ArrayList<>(52);
        for(int i = 0; i < 13; i++){
            cards.add(new Card(CardSign.CLUBS, i + 1));
            cards.add(new Card(CardSign.DIAMONDS, i + 1));
            cards.add(new Card(CardSign.HEARTS, i + 1));
            cards.add(new Card(CardSign.SPADES, i + 1));
        }
        Collections.shuffle(cards);
        System.out.println(cards);
    }

    public Card getCard(){
        Card c = cards.get(0);
        cards.remove(0);
        return c;
    }
}
