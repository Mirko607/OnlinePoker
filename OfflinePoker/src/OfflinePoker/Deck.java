package OfflinePoker;

import java.util.Random;

import handChecker.PokerCard.Color;
import handChecker.PokerCard.Value;

public class Deck { 
	public GenList<Card> card = new GenList<Card>();
	
	public Deck() {
		//create new sorted deck
		Card[] deck = new Card[52];
		int counter=0;
		for(int i=0; i<4; i++) {
			for(int j=0; j<13; j++) {
				Color c = Color.values()[i];
				Value v = Value.values()[j];
				deck[counter] = new Card(c, v);
				counter++;
			}
		}
		for(int i=0; i<52; i++) 
			card.Add(deck[i]);
		mix();
		//System.out.println("neues Kartendeck angelegt und gemischt");
	}
	
	public void mix() {
		GenList<Card> deck = new GenList<Card>(); 
		Random r = new Random();
		int pos;
		while(card.length > 0) {
			pos = r.nextInt(card.length);
			deck.Add(card.Delete(card.getItem(pos)).value);
		}
		card = deck;
	}
	
	public void Print() {
		Node<Card> curNode = card.head;
		while(curNode != null) {
			curNode.value.print();
			curNode = curNode.next;
		}
	}
}
