package OfflinePoker;

import handChecker.PokerCard;

public class Card implements PokerCard{
	Color color; 
	Value value; 
	
	public Card(Color c, Value v) {
		this.color=c;
		this.value=v;
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Value getValue() {
		return value;
	}
	
	public void print() {
		System.out.println("\t" + color + " " + value);
	}
}
