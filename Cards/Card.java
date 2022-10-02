package Cards;

public class Card {
    public CardSign sign;
    public int number;
    public Card(CardSign sign, int number){
        this.sign = sign;
        if(number>13){
            this.number = 13;
            System.out.println("card number too big");
        }else{
            this.number = number;
        }
    }

    @Override
    public String toString() {
        if(sign == CardSign.CLUBS) return number + " CLUBS";
        if(sign == CardSign.DIAMONDS) return number + " DIAMONDS";
        if(sign == CardSign.HEARTS) return number + " HEARTS";
        return number + " SPADES";
    }
}
