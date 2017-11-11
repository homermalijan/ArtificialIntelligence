import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;

public class eightPuzzle{
	public static void main(String args[]){
		final JFrame gameFrame = new JFrame("8 Puzzle");
		final JButton[][] gameButtons = new JButton[3][3];
		Container gameContainer = gameFrame.getContentPane();
		JPanel gamePanel = new JPanel(new GridLayout(3,3));
		gameContainer.setLayout(new BorderLayout());
		JButton solve = new JButton("Solve");
		JButton nGame = new JButton("New Game");
		JPanel extraButtons = new JPanel(new GridLayout(1,2));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setPreferredSize(new Dimension(300,350));

		final State s = new State();
		s.printBoard();	

		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				final int a = i;													//a and b are to be used
				final int b = j;													//inside the actionlistner for it requires to be final
				gameButtons[i][j] = new JButton(Integer.toString(s.board[i][j]));	//create new jbutton
				gameButtons[i][j].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						//check if possible move
						if(checkIfPossible(a,b,s)){
							String temp = gameButtons[a][b].getText(); 
							//find location of zero
							int loc = findZero(s);
							//swap text/button label
							gameButtons[a][b].setText(gameButtons[loc/10][loc%10].getText());
							gameButtons[loc/10][loc%10].setText(temp);
							int temp1 = s.board[a][b];
							//swap board value
							s.board[a][b] = s.board[loc/10][loc%10];
							s.board[loc/10][loc%10] = temp1;
							s.manhattan = s.getManhattanDistance(s);
						}else System.out.println("invalid move");
						System.out.println();
						s.printBoard();
						if(goalTest2(s)) System.out.println("YOU WIN!");
           			}//close action performed
				}); //close action listener
				gamePanel.add(gameButtons[i][j]);
			}//close for2
		}//close for1

		solve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Solving...");
				gameFrame.setEnabled(false);
				State solved = aStarSearch(s);
				solved.printBoard();
				gameFrame.setEnabled(true);
				createSolutionWindow(solved);
			}
		});//close add actionlistener to solve button;

		nGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Generating New Game...");
				s.generateNewGame(gameButtons);
			}
		});//close add actionlistener to solve button;

		extraButtons.add(solve);														//shows the panel
		extraButtons.add(nGame);														//along with the solve and open button
		gameContainer.add(extraButtons,BorderLayout.SOUTH);								//as well as the game buttons
		gameContainer.add(gamePanel,BorderLayout.CENTER);
		gameFrame.pack();
		gameFrame.setVisible(true);
	}//close main
		
	public static State aStarSearch(State s){
		LinkedList<State> openList = new LinkedList<State>();							//frontier
		LinkedList<State> closedList = new LinkedList<State>();							//explored
		openList.add(s);																//initial
		State bestNode = new State();

		while(!openList.isEmpty()){														//continuous until frontier is exhausted
			bestNode = removeMinF(openList);
			closedList.add(bestNode);
			if(goalTest(bestNode)) return bestNode;
			for(String a : bestNode.getActions()){										//expand path
				State s1 = bestNode.getResult(a.charAt(0),a.charAt(1));
				boolean inOpenList =  compareState(s1,openList);
				boolean inCloseList = compareState(s1,closedList);
				if(!inOpenList || !inCloseList){
					s1.setParent(bestNode);
					openList.add(s1);
				}else if(inOpenList || inCloseList){
					if(s1.cost < compareState2(s1, openList, closedList).cost){
						s1.setParent(bestNode);
						openList.add(s1);
					}//close if
				}//close elseif
			}//close for
		}//close while
		return bestNode;
	}//close astar search
	
	public static State compareState2(State s, LinkedList<State> searchList, LinkedList<State> searchList2){		//compares a state
		for(State s1 : searchList) if(compareBoard(s.board,s1.board)) return s1;			//in the openlist
		for(State s1 : searchList2)	if(compareBoard(s.board,s1.board)) return s1;			//or close list
		return s;																			//returns the state
	}//compare state 2

	public static boolean compareState(State s, LinkedList<State> searchList){
		for(State s1 : searchList) if(compareBoard(s.board,s1.board)) return true;			//looks for a copy in the closed/open list
		return false;
	}//compare state

	public static boolean compareBoard(int[][] arr1, int[][] arr2){							//check if board of 2 states are equal
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++) if(arr1[i][j] != arr2[i][j]) return false;
		}
		return true;
	}//compare board

	public static State removeMinF(LinkedList<State> openList){			//remove state with minimum f value from openlist
		State returnState = openList.peekFirst();
		for(State s : openList){
			if(s.f < returnState.f) returnState = s;
		}
		openList.remove(returnState);
		return returnState;
	}//close removeMinF

	public static boolean goalTest(State s){							//goal test for solve
		if(s.manhattan == 0) return true;
		return false;
	}//close goalTest

	public static boolean goalTest2(State s){							//goal test for playing
		int counter = 1;
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				if(i==2 && j == 2) break;
				if(s.board[i][j]!=counter) return false;
				counter++;
			}
		}
		return true;
	}//close goalTest2

	public static boolean checkIfPossible(int i, int j,State s){		//check if move is possible
		try{
			if(s.board[i-1][j] == 0) return true;
		}catch(Exception e){}
		try{
			if(s.board[i+1][j] == 0) return true;
		}catch(Exception e){}
		try{
			if(s.board[i][j-1] == 0) return true;
		}catch(Exception e){}
		try{
			if(s.board[i][j+1] == 0) return true;
		}catch(Exception e){}
		return false;
	}//close CheckIfPossible

	public static int findZero(State s){								//finds coordinates of zero button, returns in 2digit integer
		for(int i = 0 ; i < 3 ; i++){
			for(int j = 0 ; j < 3 ; j++){
				if(s.board[i][j] == 0) return ((i*10)+j);
			}
		}
		return 0;
	}//close find Zero

	public static void createSolutionWindow(State s){					//creates new window for solutiom
		final LinkedList<State> s1 = new LinkedList<State>();			//original linkedlist for states
		final LinkedList<State> s2 = new LinkedList<State>();			//previous step accumulator
		
		do{																//store states in s1
			s1.add(s);
			if(s.parent != null) s = s.parent;
			else break;
		}while(s!=null);

		final JFrame solutionFrame = new JFrame("8 Puzzle Solution");
		final JButton[][] solutionButtons = new JButton[3][3];
		Container solutionContainer = solutionFrame.getContentPane();
		JPanel solutionPanel = new JPanel(new GridLayout(3,3));
		JPanel extraButtons = new JPanel(new GridLayout(1,2));
		solutionContainer.setLayout(new BorderLayout());
		final JButton next = new JButton("Next Step");
		final JButton prev = new JButton("Previous Step");
		solutionFrame.setPreferredSize(new Dimension(300,350));
		State originalState = s1.removeLast();
		if(s2.isEmpty()) prev.setEnabled(false);						//if prev accumulator is empty, disable prev button
		s1.addFirst(originalState);
		if(s1.size()==1) next.setEnabled(false);						//if size of s1 is 1 or just the goal state, disable next button
		
		for(int i = 0 ; i < 3 ; i++){									//create buttons with labels of initial state
			for(int j = 0 ; j < 3 ; j++){
				solutionButtons[i][j] = new JButton(Integer.toString(originalState.board[i][j]));	//create new jbutton
				solutionButtons[i][j].setEnabled(false);
				solutionPanel.add(solutionButtons[i][j]);
			}
		}

		next.addActionListener(new ActionListener(){					//next button
			public void actionPerformed(ActionEvent e){
				State tempState = s1.removeLast();						//remove last
				s2.addFirst(tempState);									//plug it into prev accumulator
				for(int i = 0 ; i < 3 ; i++){							//update labels
					for(int j = 0 ; j < 3 ; j++){
						solutionButtons[i][j].setText(Integer.toString(tempState.board[i][j]));
					}
				}
				if(goalTest(tempState)) next.setEnabled(false);
				else next.setEnabled(true);
				if(!s2.isEmpty()) prev.setEnabled(true);
				else prev.setEnabled(false);
			}//close actio performed
		});


		prev.addActionListener(new ActionListener(){					//prev button
			public void actionPerformed(ActionEvent e){
				State tempState = s2.removeFirst();						//remove frist of prev accumulator
				s1.addLast(tempState);									//put back in s1
				for(int i = 0 ; i < 3 ; i++){							//update labels
					for(int j = 0 ; j < 3 ; j++){
						solutionButtons[i][j].setText(Integer.toString(tempState.parent.board[i][j]));// = new JButton(Integer.toString(tempState.board[i][j]));	//create new jbutton
					}
				}
				if(s2.isEmpty()) prev.setEnabled(false);
				else prev.setEnabled(true);
				if(s1.isEmpty()) next.setEnabled(false);
				else next.setEnabled(true);
			}
		});
		extraButtons.add(prev);															
		extraButtons.add(next);															
		solutionContainer.add(extraButtons,BorderLayout.SOUTH);							
		solutionContainer.add(solutionPanel,BorderLayout.CENTER);
		solutionFrame.pack();
		solutionFrame.setVisible(true);
	}
	
}//close class

