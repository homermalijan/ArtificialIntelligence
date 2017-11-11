import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class state{
	int[][] cur = new int[5][5];
	int[][] explored = new int[5][5];

	public state(JButton buttons[][]){										//constructor for initial state
		for(int i = 0 ; i < 5 ; i++){										//initialize cur and explored
			for(int j = 0 ; j < 5 ; j++){
				if(buttons[i][j].getBackground() ==  Color.BLACK){
					cur[i][j] = 0;
				}else{
					cur[i][j] = 1;
				}
				explored[i][j] = 0;
			}
		}
	}//close initial constructor

	public state(int[][] initThis,int[][] exploredNew,int a, int b){		//constructoroverload 
		for(int i = 0 ; i < 5 ; i++){										//for creating a new state with a given toggled button a,b
			for(int j = 0 ; j < 5 ; j++){
				this.cur[i][j] = initThis[i][j];
				this.explored[i][j] = exploredNew[i][j];
			}
		}
		this.explored[a][b] = 1;
	}//close contructor overload 
								
	public boolean goalTest(){												//checks if current state is goal state
		for (int i = 0 ; i < 5 ; i++){
			for (int j = 0 ; j < 5 ; j++){
				if(this.cur[i][j] == 1) return false;
			}
		}
		return true;
	}//close goal state

	public void toggle(int a, int b){										//toggles a button
		try{											//toggle self
			if(cur[a][b] == 0) cur[a][b] = 1;
			else cur[a][b] = 0;
		}catch(Exception ex){}
		try{											//toggle top
			if(cur[a+1][b] == 0) cur[a+1][b] = 1;
			else cur[a+1][b] = 0;
		}catch(Exception ex){}
		try{											//toggle bottom
			if(cur[a-1][b] == 0) cur[a-1][b] = 1;
			else cur[a-1][b] = 0;
		}catch(Exception ex){}
		try{											//toggle right
			if(cur[a][b+1] == 0) cur[a][b+1] = 1;
			else cur[a][b+1] = 0;
		}catch(Exception ex){}
		try{											//toggle left
			if(cur[a][b-1] == 0) cur[a][b-1] = 1;
			else cur[a][b-1] = 0;
		}catch(Exception ex){}

	}//close toggle

	public state toggleButton(char x, char y){
		int a = x-'0';									//convert to ascii int equivalent
		int b = y-'0';									//convert to ascii int equivalent

		state snew = new state(this.cur,this.explored,a,b);			//creates a new state
		snew.toggle(a,b);											//toggles the button in the new state created
		return snew;
	}//close toggle button

	public LinkedList<String> getActions(){										//scan explored 2d array for possible actions
		LinkedList<String> possibleActions = new LinkedList<String>();
		for(int i = 0 ; i < 5 ; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(this.explored[i][j] == 0) {
					possibleActions.addLast(Integer.toString(i)+""+Integer.toString(j)); 
				}
			}
		}
		return possibleActions;
	}//close getAction
}//close class
