package server.Poker;

import Cards.Card;
import server.users.ConnectedUser;

public class Player {
    public ConnectedUser user;
    public String username;
    public double moneyLeft;
    public double moneyBet;
    public Card cardA = null;
    public Card cardB = null;

    public Player(ConnectedUser user){
        this.user = user;
        this.username = user.username;
        this.moneyLeft = 1000.0;
        this.moneyBet = 0;
    }
    public void GiveCards(Card a, Card b){
        this.cardA = a;
        this.cardB = b;
    }
    public String PickCards(){
        if(cardA != null && cardB != null) return "You've got: " + cardA + ", and " + cardB;
        else return "no_cards";
    }
}
