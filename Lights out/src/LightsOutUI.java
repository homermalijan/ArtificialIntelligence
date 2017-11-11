import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class LightsOutUI{
//=========================================================================================================================================
	public static void main(String[] args){
		//====================UI COMPONENT===========================//
		final JFrame gameFrame = new JFrame("Lights Out");
		final JButton[][] buttons = new JButton[5][5];
		Container gameContainer = gameFrame.getContentPane();
		JPanel gamePanel = new JPanel(new GridLayout(5,5));
		gameContainer.setLayout(new BorderLayout());
		JButton solve = new JButton("Solve");
		JButton open = new JButton("Open File");
		JPanel extraButtons = new JPanel(new GridLayout(1,2));
		//====================FRAME PROPERTIES=======================//
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setPreferredSize(new Dimension(500,550));							//wala lang para square :D
		int counter2 = 0;
		//================CREATE UI AND BUTTONS=====================//
		for(int i = 0 ; i < 5 ; i++){
			for(int j = 0 ; j < 5 ; j++){				
				buttons[i][j] = new gameButton();									//create an instance of a gameButton (custom class)
				if (buttons[i][j].getBackground() ==  Color.YELLOW) counter2++;		//update counter for an open button
																					//to check for a generated endgame
				final int a = i;													//a and b are to be used
				final int b = j;													//inside the actionlistner for it requires to be final
				
				buttons[i][j].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						gameButton gb = (gameButton)e.getSource();					//toggle button pressed
						gb.toggle();
						try{
							gameButton homer = (gameButton)buttons[a][b+1];			//right
							homer.toggle();
						}catch(Exception ex){}
						try{
							gameButton homer = (gameButton)buttons[a][b-1];			//left
							homer.toggle();
						}catch(Exception ex){}
						try{
							gameButton homer = (gameButton)buttons[a+1][b];			//top
							homer.toggle();
						}catch(Exception ex){}
						try{
							gameButton homer = (gameButton)buttons[a-1][b];			//bottom
							homer.toggle();
						}catch(Exception ex){}
           			}//close action performed
				}); //close action listener
				gamePanel.add(buttons[i][j]);
			}
		}//close for loop
		solve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){		
				gameFrame.setEnabled(false);
				JFrame solutionFrame = new JFrame("Solution");							//create new frame for solution
				JPanel solutionPanel = new JPanel();									//create new panel
				solutionPanel.setLayout(new GridLayout(5,5));
				solutionFrame.setPreferredSize(new Dimension(500,500));
				state solution = solveGame(buttons);									//solve for goal state
				for(int i = 0 ; i < 5 ; i++){											//set whether panels 
					for(int j = 0 ; j < 5 ; j++){										//are yellow or balck
						JButton solutionBut = new JButton();							//based on the explored 2d array of the 
						solutionBut.setSize(new Dimension(99,99));						//goal state
						if(solution.explored[i][j] == 1) solutionBut.setBackground(Color.YELLOW);
						else solutionBut.setBackground(Color.BLACK);
						solutionBut.setEnabled(false);
						solutionPanel.add(solutionBut);
					}
				}
				solutionFrame.add(solutionPanel);										//show frame for soltion
				solutionFrame.pack();
				solutionFrame.setVisible(true);
				gameFrame.setEnabled(true);
			}
		});//close add action listner for solve button

		open.addActionListener(new ActionListener(){									//opens a file when open a file button is clicked
			public void actionPerformed(ActionEvent e){		
				openFileNow(buttons,gameFrame);
			}
		});
		extraButtons.add(solve);														//shows the panel
		extraButtons.add(open);															//along with the solve and open button
		gameContainer.add(extraButtons,BorderLayout.SOUTH);								//as well as the game buttons
		gameContainer.add(gamePanel,BorderLayout.CENTER);
		gameFrame.pack();
		gameFrame.setVisible(true);
	}

//=========================================================================================================================================
	public static state solveGame(JButton[][] buttons){
		LinkedList<state> frontier = new LinkedList<state>();							//frontier possible next steps (list of states)
		state s = new state(buttons);
		frontier.add(s);																//creates the initial state out of the given buttons
		state currentState = new state(buttons);
		while(!frontier.isEmpty()){														//continues until frontier is empty
		 	currentState = frontier.removeFirst();										//or goal state is achieved
		 	if(currentState.goalTest())	return currentState;
	 		for(String stringer : currentState.getActions()){							//scans possible actions and executes them
	 			state tempState = currentState.toggleButton(stringer.charAt(0),stringer.charAt(1)); 
	 			if(tempState.goalTest()) return tempState;
	 			frontier.addLast(tempState);	//one by one
	 		}
	 	}
		return currentState;
	}//close solveGame

//=========================================================================================================================================
	public static void openFileNow(JButton[][] buttons, JFrame gameFrame){
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = chooser.showOpenDialog(gameFrame);
		if(result == JFileChooser.APPROVE_OPTION){
			File selectFile = chooser.getSelectedFile();
			BufferedReader br = null;
			try{
			br = new BufferedReader(new FileReader(selectFile.getAbsolutePath()));	//create a new buffered reader for
			String currentLine;														//the selected file via get absolute path of the file
			int row = 0;
				while((currentLine = br.readLine()) != null){
					for(int counter = 0 ; counter < 10; counter+=2){
						if((currentLine.charAt(counter) - '0')==0) buttons[row][counter/2].setBackground(Color.BLACK);	//if zero then black
						else buttons[row][counter/2].setBackground(Color.YELLOW);										//if 1 then yellow
					}
					row++;
				}
			} catch(IOException e){
				e.printStackTrace();
			} finally{
				try{
					if(br != null) br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}//finally
		}//close if approve option
	}//close openfilenow

}//close lightsout class

