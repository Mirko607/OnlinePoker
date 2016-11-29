package OfflinePoker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import handChecker.HandChecker;
import handChecker.HandValue;
import handChecker.PokerCard;

public class Table {
	//Kartendeck anlegen
	Deck deck;
	int numbOfPlayers, playerCount, setMoney, pot = -1, pos, posDealer, whoSetMost, smallBlind = 25, setSmallBlind;
	Player[] playerOnTable, player;
	Card[] flop = new Card[3];
	Card turn, river; 
	boolean AllIn = false, allAllIn = false, setBigBlind, showFlop, showRiver, showTurn;
	InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
	
	public Table() throws IOException {
		posDealer = -1;
	//Spieler hinzufügen mit Startguthaben, Name
		System.out.println("Wie lautet dein Name? ");
		String name = br.readLine();
		System.out.println("Hallo " + name + "! \n");
		
		System.out.println("Ein neuer Tisch wird für dich vorbereitet \n");
		
		System.out.println("Für wie viele Spieler soll der Tisch eingerichtet werden?: ");
		String input = br.readLine();
		int opponents=0;
		if(isInteger(input))
			opponents = Integer.parseInt(input);
		while ((opponents < 2) || (opponents > 9)) {
			System.out.println("Eingabe konnte nicht dem Bereich von 2-9 Spielern zugeordnet werden. ");
			System.out.println("Gib bitte erneut die Anzahl von Spielern an: ");
			input = br.readLine();
			if (isInteger(input))
				opponents = Integer.parseInt(input);
		}
		System.out.println("Der Tisch wird für " + opponents + " Spieler fertig gemacht.\n"
				+ "(In dieser Version wirst du alle Spieler selbst spielen. )\n");
			
		System.out.println("Am Tisch sitzen: ");
		numbOfPlayers = opponents;
		playerOnTable = new Player[numbOfPlayers];
		for(int i=0; i<numbOfPlayers; i++){
			if(i>0)
				playerOnTable[i] = new Player("Spieler "+i);
			else
				playerOnTable[i] = new Player(name);
			System.out.println("\t" + playerOnTable[i].name);
		}
	//start Round 
		while(numbOfPlayers > 1) {
			System.out.println("\nEine neue Runde beginnt. ");
		
	//ask for joining round
			for(int i=0; i<numbOfPlayers; i++) {
				boolean correctInput=false;
				while(!correctInput) {
					System.out.println("\n" + playerOnTable[i].name + ", möchtest Du diese Runde mitspielen? [Y/N] oder den Tisch verlassen? [LT]");
					input = br.readLine();
					String s = input; 
					if ((s.equals("Y")) || (s.equals("y"))) {
						playerOnTable[i].playRound = true;
						correctInput = true;
						System.out.println("Du bist dabei. ");
					}
					else if((s.equals("N")) || (s.equals("n"))) {
						playerOnTable[i].playRound = false;
						correctInput = true;
					}
					else if(s.equals("lt") || s.equals("LT") || s.equals("Lt")) {
						System.out.println(playerOnTable[i].name + " verlässt den Tisch");
						DeletePlayer(playerOnTable[i]);
						i--;
						correctInput = true;
					}
					else
						System.out.println("falsche Eingabe.");
				} 
			}
		
			playerCount = 0;
			for(int i=0; i<numbOfPlayers; i++) {
				if(playerOnTable[i].playRound)
					playerCount++;
			}
			if(playerCount == 0 )
				playerCount++;
			
			int counter = 0;
			player = new Player[playerCount];
			for(int i=0; i<numbOfPlayers; i++) {
				if(playerOnTable[i].playRound) {
					player[counter] = playerOnTable[i];
					counter++;
				}
			}
	
	//determine Dealer randomly in first round and small and big blind | 2 Players: Dealer=small blind
			
			if(posDealer < 0) {
				Random rand = new Random();
				posDealer = rand.nextInt(playerCount); 
				player[posDealer].dealer = true;
				System.out.println(player[posDealer].name + " ist der neue Dealer");
			}
			else {
				player[posDealer].dealer = false;
				if(posDealer < playerCount-1)
					posDealer++;
				else
					posDealer = 0;
				player[posDealer].dealer = true;
				System.out.println(player[posDealer].name + " ist der neue Dealer");
			}
			
			pos = posDealer;
			
			if (playerCount > 2){
				for(int i=0; i<playerCount; i++) {
					switch (i) {
					case 0:
						player[pos].position = "Dealer";
						break;
					case 1:
						player[pos].position = "Small blind" ;
						break;
					case 2:
						player[pos].position = "Big Blind";
						break;
					default:
						player[pos].position = (i-2) + ". Position";
						break;
					}
					if(pos < (playerCount-1)) 
						pos++;
					else
						pos=0;
				}
			}
			else if (playerCount == 2){
				player[pos].position = "Dealer, Small Blind";
				if (pos == 1) 
					player[0].position = "Big Blind";
				else 
					player[1].position = "Big Blind";
			}
			
			deck = null;	
			deck = new Deck();
	//deal out cards to players after setting big blind
			//System.out.println("Karten werden verteilt");
			for(int i=0; i<2; i++) {
				for(int j=0; j<playerCount; j++) {
					player[j].card.Add(deck.card.Delete(deck.card.head).value);
				}
			}
			flop[0] = deck.card.Delete(deck.card.head).value;
			flop[1] = deck.card.Delete(deck.card.head).value;
			flop[2] = deck.card.Delete(deck.card.head).value;
			turn = deck.card.Delete(deck.card.head).value;
			river = deck.card.Delete(deck.card.head).value;
			
	//start first betting round
			for(int i=0; i<playerCount; i++) 
				player[i].setMoney = 0;
			if(playerCount > 1) { //e.g. 2 Players, one is not joining Round
				pos = GetPos();
				BettingRound(pos, posDealer);
				if(playerCount > 1) {
					showFlop = true;
					pos = GetPos();
					if(!allAllIn)
						BettingRound(pos, posDealer);
					if(playerCount > 1) {
						showTurn = true;
						pos = GetPos();
						if(!allAllIn)
							BettingRound(pos, posDealer);
						if(playerCount > 1) {
							showRiver = true;
							pos = GetPos();
							if(!allAllIn)
								BettingRound(pos, posDealer);
						}
					}
				}
				
				DistributionOfProfit(player);
			}
	//Delete players from table when Money == 0
			for (int i=0; i < playerCount; i++){
				if(player[i].money == 0) {
					System.out.println(player[i].name + " verlässt den Tisch. ");
					DeletePlayer(player[i]);
				}
			}
	//reset everything
			for(int i=0; i<numbOfPlayers; i++) {
				playerOnTable[i].playRound = false;
				playerOnTable[i].setMoney = 0;
				playerOnTable[i].allIn = false;
				playerOnTable[i].dealer = false;
				if(playerOnTable[i].card.head != null)
					playerOnTable[i].card.Delete(playerOnTable[i].card.head);
				if(playerOnTable[i].card.head != null)
					playerOnTable[i].card.Delete(playerOnTable[i].card.head);
			}
			setBigBlind = false;
			showFlop = false;
			showTurn = false;
			showRiver = false;
			pot = -1;
			AllIn = false;
		} //end of while(numbOfPlayers > 1) && end of Round
		System.out.println(playerOnTable[0].name + " ist der letzte Spieler und hat somit gewonnen. Glückwunsch! :) \n"
				+ "\n"
				+ "Das Spiel ist vorbei, der Tisch wird geschlossen.");
	} 
	
	public void BettingRound(int pos, int posDealer) throws IOException{
		boolean allSetSame = false, correctInput = false; 
		String input;
		
		while(!allSetSame) {
			int i = 0; 
			while((i < playerCount) && (playerCount > 1)) {
				int value;
				correctInput = false;
				while(!correctInput) {
					if(setBigBlind) {
						if(!player[pos].allIn) {
							pot = 0;
							for(int j = 0; j < numbOfPlayers; j++)
								pot += playerOnTable[j].setMoney;
							
							System.out.println("\n" + player[pos].name + ", du bist an der Reihe! \n" + 
									"\tim Pot: " + pot + "\n" + 
									"\tdu hast noch: " + player[pos].money + "\n" +
									"\taktuell gesetzt: " + player[pos].setMoney + "\n" + 
									"\tmindestens zu setzen: " + setMoney + "\n" + 
									"Deine Karten: ");
							player[pos].printCards();
							if(showFlop) {
								System.out.println("\nFlop: ");
								flop[0].print();
								flop[1].print();
								flop[2].print();
								if(showTurn) {
									System.out.println("Turn: ");
									turn.print();
									if(showRiver) {
										System.out.println("River: ");
										river.print();
										System.out.println("");
									}
								}
							}
							System.out.println("Was möchtest du tun? (check, raise/bet, fold, call, allin)");
							input = br.readLine();
							if(input.equals("check")) {
								if(player[pos].setMoney == setMoney) {
									System.out.println(player[pos].name + " checks");
									correctInput = true;
								}
								else
									System.out.println("Fehler: Du hast noch nicht den zu setzenden Betrag gesetzt. ");
							}
							else if(input.equals("call")) {
								if(player[pos].setMoney < setMoney) {
									if (player[pos].wantToSet(setMoney)){
										player[pos].set(setMoney);
										correctInput = true;
									}
									else 
										System.out.println("Du hast nicht mehr genug Geld zum callen. Du musst All In gehen! ");
								}
								else
									System.out.println("Du hast bereits den zu setzenden Betrag gesetzt. ");
							}
							else if(input.equals("raise") || input.equals("bet")) {
								if((player[pos].money+player[pos].setMoney) > setMoney) {
									while(!correctInput) {
										System.out.println("Auf wie viel möchtest du erhöhen? ");
										String input2 = br.readLine();
										if(isInteger(input2)) {
											value = Integer.parseInt(input2);
											if(value >= (setMoney+setSmallBlind)) {
												if(player[pos].wantToSet(value)) {
													player[pos].set(value);
													setMoney = player[pos].setMoney;
													whoSetMost = pos;
													i = 0;
													correctInput = true;
												}
												else
													System.out.println("Du hast nicht mehr genug Geld um auf diesen Betrag zu erhöhen. ");
											}
											else {
												System.out.println("Dein Betrag ist zu gering. Du musst mindestens " + (setMoney+setSmallBlind) + " setzen. ");
											}
										}
										else
											System.out.println("Deine Eingabe ist keine ganze Zahl");
									}
								}
								else if((player[pos].money+player[pos].setMoney) == setMoney) {
									System.out.println("Du hast nicht mehr genug Geld für diese Aktion. Du kannst aber noch callen");
								}
								else
									System.out.println("Du hast nicht mehr genug Geld für raise, bet oder call. Du musst All In gehen! ");
							}
							else if(input.equals("fold")){
								System.out.println(player[pos].name + " steigt aus. ");
								int counter = 0;
								Player[] p = new Player[playerCount-1];
								for(int j=0; j<playerCount; j++) {
									if(player[j] != player[pos]) {
										p[counter] = player[j];
										counter++;
									}
								}
								playerCount--;
								i--;
								pos--;
								player = p;
								correctInput = true;
							} 
							else if(input.equals("allin")){
								player[pos].set((player[pos].money+player[pos].setMoney));
								if(player[pos].setMoney > setMoney) {
									setMoney = player[pos].setMoney;
									whoSetMost = pos;
									i=0; 
								}
								AllIn = true; 
								System.out.println(player[pos].name  + " geht All In");
								
								correctInput = true;
							}
							else {
								System.out.println("ungütige Eingabe " + input);
							}
						}
						else {
							System.out.println(player[pos].name  + " ging AllIn \n"
											+ "der nächste Spieler ist an der Reihe");
							correctInput = true; 
						}
					}
					else {
						if(pot < 0) {
							System.out.println("\n" + player[pos].name + " ist dran und setzt den small Blind \n"
									+ "Mindesteinsatz: " + smallBlind);
							if(player[pos].wantToSet(smallBlind)) {
								System.out.println("Wie viel möchtest du setzen? ");
								while(!correctInput){
									input = br.readLine();
									if(isInteger(input)) {
										setSmallBlind = Integer.parseInt(input);
										if(setSmallBlind >= smallBlind) {
											if(player[pos].wantToSet(setSmallBlind)){
												player[pos].set(setSmallBlind);
												System.out.println(player[pos].name + " hat den Small Blind mit " + setSmallBlind + " bezahlt");
												correctInput = true;
											}
											else
												System.out.println("Diesen Betrag kannst du nicht setzen. \n"
														+ "Wie viel möchtest du setzen? ");
											}
										else 
											System.out.println("Das ist leider zu wenig. \n"
													+ "Mindesteinsatz: " + smallBlind);
									}
									else
										System.out.println("Fehler: Deine Eingabe ist keine ganze Zahl");
								}
							}
							else {
								System.out.println(player[pos].name + " kann den Small Blind nicht zahlen und geht All In");
								player[pos].set(player[pos].money);
								AllIn = true; 
								correctInput = true; 
							}
							pot = 0;
						}
						else {
							System.out.println("\n" + player[pos].name + " ist dran und muss den Big Blind setzen \n"
									+ "Einsatz: " + (2*setSmallBlind));
							if(player[pos].wantToSet(2*setSmallBlind)) {
								player[pos].set(2*setSmallBlind);
								setMoney = 2*setSmallBlind;
								System.out.println(player[pos].name  + " hat den Big Blind bezahlt");
							}
							else {
								System.out.println(player[pos].name + " kann den Big Blind nicht zahlen und geht All In");
								player[pos].set(player[pos].money);
								AllIn = true; 
							}
							i = -1;						
							setBigBlind = true; 
							correctInput = true; 
						}
					}
				}
				
				if(pos < (playerCount-1)) 
					pos++;
				else
					pos=0;
				
				i++;
			}
			allSetSame = CheckAllSetSame();
		}
		allAllIn = true;
		int counter = 0; 
		while(counter < playerCount) {
			if(!player[counter].allIn) {
				allAllIn = false;
				counter = playerCount; 
			}
			counter++;
		}
	}
	
	public void DistributionOfProfit(Player[] p){
		playerCount = p.length;
		
		if(showFlop) {
			System.out.println("Flop: ");
			flop[0].print();
			flop[1].print();
			flop[2].print();
			if(showTurn) {
				System.out.println("Turn: ");
				turn.print();
				if(showRiver) {
					System.out.println("River: ");
					river.print();
					System.out.println("");
				}
			}
		}
		
		LinkedList<Player> pl = new LinkedList<Player>();
		LinkedList<Player> plSorted = new LinkedList<Player>();
		for(int i=0; i<playerCount; i++) {
			pl.add(p[i]);
		}
		for(int i=0; i<playerCount; i++) {
			int min = 0; 
			for(int j=0; j<pl.size(); j++) {
				if(pl.get(j).setMoney < pl.get(min).setMoney)
					min = j;
			}
			plSorted.addLast(pl.remove(min));
		}
		for(int i=0; i<playerCount; i++) {
			p[i] = plSorted.get(i);
		}
		pl.clear();
		
		boolean sevWinAllIn = false;
		List<PokerCard> halfList = new LinkedList<PokerCard>();
		List<PokerCard> fullList = new LinkedList<PokerCard>();
		halfList.add(flop[0]);
		halfList.add(flop[1]);
		halfList.add(flop[2]);
		halfList.add(turn);
		halfList.add(river);
		
		HandChecker hc = new HandChecker();
		HandValue[] hv = new HandValue[playerCount];
		for(int i=0; i<playerCount; i++) {
			fullList.clear();
			fullList.addAll(halfList);
			fullList.add(p[i].card.head.value);
			fullList.add(p[i].card.head.next.value);
			hv[i] = hc.check(fullList);
		}
		
		int best = 0; 
		for(int i=0; i<playerCount; i++){
			if(hv[best].compareTo(hv[i]) < 0 ){
				best = i;
				pl.clear();
				pl.add(p[i]);
			}
			else if(hv[best].compareTo(hv[i]) == 0) {
				pl.add(p[i]);
			}
		}
		System.out.println("Zu den Gewinnern der Runde gehören: ");
		for(int i=0; i<pl.size(); i++) {	
			System.out.println("\t" + pl.get(i).name);
		}
		pot = 0;
		int[] setMoney = new int[numbOfPlayers];
		for(int i=0; i<numbOfPlayers; i++) {
			setMoney[i] = playerOnTable[i].setMoney; 
			pot += playerOnTable[i].setMoney; 
		}
		
		if(pl.size() == 1) {
			if(!AllIn || !p[best].allIn)  {
				p[best].money += pot;
				System.out.println(p[best].name + " gewinnt den Pot! ");
			}
			else {
				System.out.println(p[best].name + " ist AllIn gegangen und hat gewonnen!");
				for(int i = 0; i < numbOfPlayers; i++){
					if(playerOnTable[i].playRound) {
						if(setMoney[i] > p[best].setMoney) {
							p[best].money += p[best].setMoney;
							pot -= p[best].setMoney;
							setMoney[i] -= p[best].setMoney;
							//playerOnTable[i].setMoney -= p[best].setMoney;
						}
						else {
							p[best].money += setMoney[i]; 
							pot -= setMoney[i]; 
							setMoney[i] = 0; 
							//playerOnTable[i].setMoney = 0;
						}
					}
				}
				//p[best].setMoney = 0;
				int winner=0;
				for(int i=0; i<numbOfPlayers; i++) {
					if(setMoney[i] > 0) 
						winner++;
				}
				if(winner > 0) {
					int restPlayer = 0;
					Player[] rest = new Player[winner];
					for(int j=0; j<numbOfPlayers; j++) {
						if(playerOnTable[j].playRound && (setMoney[j]>0)) {
							rest[restPlayer] = p[j];
							restPlayer++;
						}
					}
					DistributionOfProfit(rest);
				}
			}
		}
		else {
			if(pot % pl.size() != 0){
				pot -= (pot % pl.size());
				int i = 0;
				while(!playerOnTable[i].dealer) {
					i++;
				}
				if(i < playerCount-1) 
					i++;
				else
					i=0;
				
				while(!playerOnTable[i].playRound) {
					if(i < playerCount-1) 
						i++;
					else
						i=0;
				}
				playerOnTable[i].money += (pot % pl.size());
			}
			int i = 0;
			while(!sevWinAllIn && i<pl.size()) {
				if(pl.get(i).allIn == true)
					sevWinAllIn = true;
				i++;
			}
			if(!AllIn || !sevWinAllIn)  {
				for(i = 0; i < pl.size() ;i++) {
						pl.get(i).money += pot/pl.size();
				}
			}
			else {
				for(int k = 0; k < pl.size(); k++){
					for(i = 0; i < numbOfPlayers; i++){
						if(playerOnTable[i].playRound) {
							if(setMoney[i] > pl.get(k).setMoney) {
								pl.get(k).money += pl.get(k).setMoney/pl.size();
								pot -= pl.get(k).setMoney/pl.size();
								setMoney[i] -= pl.get(k).setMoney/pl.size();
								//playerOnTable[i].setMoney -= pl.get(k).setMoney/pl.size();
							}
							else {
								pl.get(k).money += setMoney[i]/pl.size(); 
								pot -= setMoney[i]; 
								setMoney[i] = 0; 
								//playerOnTable[i].setMoney = 0;
							}
						}
					}
					int winner=0;
					for(i=0; i<numbOfPlayers; i++) {
						if(setMoney[i] > 0) 
							winner++;
					}
					if(winner > 0) {
						int restPlayer = 0;
						Player[] rest = new Player[winner];
						for(int j=0; j<numbOfPlayers; j++) {
							if(playerOnTable[j].playRound && (setMoney[j]>0)) {
								rest[restPlayer] = p[j];
								restPlayer++;
							}
						}
						DistributionOfProfit(rest);
					}
				}
			}
		} // end of else, (pl.size() > 1);
	}

	public Player DeletePlayer(Player p) {
		int i=0; 
		while(playerOnTable[i] != p) {
			i++;
		}
		int j;
		if(i < playerCount - 1)
			j = i + 1;
		else 
			j = 0;
		if(p.dealer){
			while(!playerOnTable[j].playRound){
				if(i < playerCount - 1)
					j++;
				else 
					j = 0;
			}
		}
		playerOnTable[j].dealer = true;
		posDealer = j;
		playerOnTable[i] = null; 
		numbOfPlayers--;
		
		Player[] pl = new Player[numbOfPlayers];
		j=0;
		for(i=0; i<numbOfPlayers+1; i++) {
			if(playerOnTable[i] != null) {
				pl[j] = playerOnTable[i];
				j++;
			}
		}
		playerOnTable = pl;
		System.out.println(p.name + " wurde vom Tisch entfernt \n");
		return p;
	}

	public boolean CheckAllSetSame() {
		int value = 0;
		for(int i = 0; i < playerCount; i++){
			if(player[i].setMoney > value)
				value = player[i].setMoney;
		}
		for(int i=0; i<playerCount; i++) {
			if(player[i].setMoney != value && !player[i].allIn)
				return false;
		}
		return true; 
	}
	
	public int GetPos(){
		int counter = 0; 
		for(int i=0; i<numbOfPlayers; i++) {
			if(playerOnTable[i].playRound) {
				counter++;
			}
		}
		if(counter > 2) {
			counter = 0; 
			boolean foundPlayer = false;
			int i=0;
			while(!playerOnTable[i].dealer)
				i++;
			pos = i;
			i=-100;
			while(counter < numbOfPlayers && !foundPlayer) {
				
				if(pos < (numbOfPlayers-1)) 
					pos++;
				else
					pos=0;
				
				if(playerOnTable[pos].playRound) {
					i=0; 
					while(i < playerCount && !foundPlayer) {
						if(playerOnTable[pos] == player[i]) 
							foundPlayer = true;
						else
							i++;
					}
				}
				counter++;
			}
			return i;
		}
		else 
			return posDealer;
	}
	
	public boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
}
