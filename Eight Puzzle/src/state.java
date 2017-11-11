import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class State{
	int manhattan = 0;
	int cost = 0;
	int f = 0;
	int[][] board = new int[3][3];
	State parent = null;
	Random ran = new Random();


	public State(JButton[][] gameButtons,int x){					//constructor for new game
		LinkedList<Integer> init = new LinkedList<Integer>();		//initial state
		for(int i = 0 ; i < 9 ; i++){
			int temp = ran.nextInt(9);
			if(!init.contains(temp)) init.add(temp);
			else i--;
		}		
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				int a = init.remove();
				gameButtons[i][j].setText(Integer.toString(a));
				board[i][j] = a;	
			} 
		}
		this.manhattan = getManhattanDistance(this);
		this.f = cost + manhattan;
	}

	public State(JButton[][] gameButtons){							//constructor for end game 
		int counter = 1;
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				if(counter != 9) board[i][j] = counter;
				else board[i][j] = 0;
				counter++;
			}
		}
		manhattan = getManhattanDistance(this);
		f = cost + manhattan;
	}

	public State(){													//constructor for initial state
		LinkedList<Integer> init = new LinkedList<Integer>();
		for(int i = 0 ; i < 9 ; i++){
			int temp = ran.nextInt(9);
			if(!init.contains(temp)) init.add(temp);
			else i--;
		}
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++) board[i][j] = init.remove();
		}
		this.manhattan = getManhattanDistance(this);
		this.f = cost + manhattan;
	}
	
	public State(State s, int i, int j, int x, int y){				//constructor overload for next state
		int temp = s.board[i][j];
		for(int g = 0 ; g < 3 ; g++){
			for(int h = 0 ; h < 3 ; h++) this.board[g][h] = s.board[g][h];
		}		
		this.board[x][y] = temp;
		this.board[i][j] = s.board[x][y];	
		this.parent = s;
		this.cost = s.cost+1;		
		this.manhattan = getManhattanDistance(this);
		this.f = cost+manhattan;
	}
	
	public LinkedList<String> getActions(){							//returns linkedlist of states of all possible next states
		LinkedList<String> possibleActions = new LinkedList<String>();
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				if(board[i][j] == 0 ){		//check adjacent sides
						if(i-1>=0) possibleActions.add(Integer.toString(i-1)+""+Integer.toString(j));		//up 
						if(i+1<=2) possibleActions.add(Integer.toString(i+1)+""+Integer.toString(j));		//down
						if(j-1>=0) possibleActions.add(Integer.toString(i)+""+Integer.toString(j-1));		//left
						if(j+1<=2) possibleActions.add(Integer.toString(i)+""+Integer.toString(j+1));		//right
				}
			}
		}//close outer for loop
		return possibleActions;
	}
	
	public State getResult(char x, char y){					//moves to next state given an action
		int i,j=0;
		for(i=0;i<3;i++){									//gets coordinates of zero
			for(j=0;j<3;j++){
				if(board[i][j]==0) break;
			}
			if(j<3) break;
		}
		State s2 = new State(this,i,j,x-'0',y-'0');			//creates new state with swapped zero and action button
		return s2;											//returns new state
	}

	public void printBoard(){								//print board at terminal for checking purposes
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++) System.out.print(" " + board[i][j]);
			System.out.println();
		}
	}

	public int getManhattanDistance(State s){				//computes for manhattan distance
		int manhattanDistance = 0;
		int[][] goal = new int[][]{{1,2,3},{4,5,6},{7,8,0}};
		
		for(int i = 0 ; i < 3 ; i++){						//gets the summation of all cell's distance from their proper position
			for(int j = 0 ; j < 3 ; j++){
				switch(s.board[i][j]){
					case 1: manhattanDistance = manhattanDistance + (Math.abs(0-i) + Math.abs(0-j)); break;
					case 2: manhattanDistance = manhattanDistance + (Math.abs(0-i) + Math.abs(1-j)); break;
					case 3: manhattanDistance = manhattanDistance + (Math.abs(0-i) + Math.abs(2-j)); break;
					case 4: manhattanDistance = manhattanDistance + (Math.abs(1-i) + Math.abs(0-j)); break;
					case 5: manhattanDistance = manhattanDistance + (Math.abs(1-i) + Math.abs(1-j)); break;
					case 6: manhattanDistance = manhattanDistance + (Math.abs(1-i) + Math.abs(2-j)); break;
					case 7: manhattanDistance = manhattanDistance + (Math.abs(2-i) + Math.abs(0-j)); break;
					case 8: manhattanDistance = manhattanDistance + (Math.abs(2-i) + Math.abs(1-j)); break;
					case 0: manhattanDistance = manhattanDistance + (Math.abs(2-i) + Math.abs(2-j)); break;
				}//close switch
			}//close inner for
		}//close outer for	
		return manhattanDistance;
	}

	public void setParent(State s){							//redirects parent pointer :D
		this.parent = s;
	}

	public void generateNewGame(JButton[][] gameButtons){	//generates a new set of random board
		LinkedList<Integer> init = new LinkedList<Integer>();

		for(int i = 0 ; i < 9 ; i++){						//generates new 0-8 in a random order
			int temp = ran.nextInt(9);
			if(!init.contains(temp)) init.add(temp);
			else i--;
		}//close for random generate

		for(int i = 0 ; i < 3 ; i++){						//assign 9 numbers to buttons and board
			for(int j = 0 ; j < 3 ; j++){
				board[i][j] = init.remove();
				gameButtons[i][j].setText(Integer.toString(board[i][j]));
			}
		}//close for assign
		this.manhattan = getManhattanDistance(this);		//recompute manhattan
		this.cost = 0;										
		this.f = cost + manhattan;							//reocmputes f
		printBoard();										//print board for verification purposes
	}

}
