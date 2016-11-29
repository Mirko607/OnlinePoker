package OfflinePoker;

public class Player  {
	boolean dealer=false, playRound = false, allIn = false;
	int money = 1500, setMoney = 0;
	String position, name;
	GenList<Card> card = new GenList<Card>();

	protected Player(String name) {
		this.name = name;
	}
	
	public boolean wantToSet(int value) {
		if(value < (money + setMoney))
			return true;
		else
			return false;
	}
	
	public void set(int value) {
		money -= (value - setMoney); 
		setMoney = value; 
		
		if(money == 0) {
			allIn = true;
			System.out.println(name + " geht All In");
		}
		System.out.println(name + " setzt " + setMoney);
	}
	
	public void printCards() {
		card.head.value.print();
		card.head.next.value.print();
	}
	
}
