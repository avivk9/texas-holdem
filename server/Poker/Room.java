package server.Poker;

import Cards.CardsPackage;

import java.util.ArrayList;

public class Room {
    public ArrayList<Player> players;
    boolean isAvailable = true;
    CardsPackage cp;
    double currentBet;
    double moneyInPot;
    public Room(){
        players = new ArrayList<>();
        cp = new CardsPackage();
        currentBet = 0;
        moneyInPot = 0;
    }
    public void Join(Player p){
        if(isAvailable)
            players.add(p);
    }
    public void Start(){
        if(players.size() > 2 && isAvailable){
            isAvailable = false;
        }
    }
}
