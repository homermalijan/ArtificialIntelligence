import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.table.*;	
import java.awt.event.*;	

class bagOfWords{
	//class attributes
	private static float hamDic = 0;
	private static float spamDic = 0;
	private static float hamTot = 0;
	private static float spamTot = 0;
	private static float dicTotal = 0;
	private static float spamLines = 0;
	private static float hamLines = 0;
	
	public static void main(String args[]){
		//frame and countainer
		final JFrame bagFrame = new JFrame("Bag of Words");
		Container bagContainer = bagFrame.getContentPane();
		bagContainer.setLayout(new BorderLayout());

		//input text area
		final JTextArea textArea = new JTextArea(5, 20); 
		final JTextArea textArea2 = new JTextArea(1, 20); 
		
		//table models spam, ham, output
		final DefaultTableModel model = new DefaultTableModel();
		final DefaultTableModel model2 = new DefaultTableModel();
		final DefaultTableModel model3 = new DefaultTableModel();

		//table columns
		model.addColumn("Words");
		model.addColumn("Frequency");
		model2.addColumn("Word");
		model2.addColumn("Frequency");
		model3.addColumn("Message");
		model3.addColumn("Classification");

		//create tables
		final JTable bagTable = new JTable(model);
		bagTable.setEnabled(false);
		final JTable bagTable2 = new JTable(model2);
		bagTable2.setEnabled(false);
		final JTable output = new JTable(model3);
		output.setEnabled(false);
		
		//Hashmap for spam and ham
		final HashMap<String,Integer> spamMap = new HashMap<String,Integer>();
		final HashMap<String,Integer> hamMap = new HashMap<String,Integer>();
		
		//Buttons
		JButton open = new JButton("Browse Spam");
		JButton open2 = new JButton("Browse Ham");
		JButton filter = new JButton("Filter");

		//fram settings
		bagFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bagFrame.setPreferredSize(new Dimension(800,500));
		
		//label desciptions
		final JLabel hamSize = new JLabel("Total Words in Ham: ");
		final JLabel spamSize = new JLabel("Total Words in Spam: ");
		final JLabel dicSize = new JLabel("	Dictionary Size: ");
		final JLabel totalWords = new JLabel("Total Words: ");

		//numeric labels
		final JLabel dicSizeNumber = new JLabel("0");
		final JLabel totalWordsNumber = new JLabel("0");
		final JLabel totalHam = new JLabel("0");										//labels
		final JLabel totalSpam = new JLabel("0");										//labels
		
		//open spam.txt
		open.addActionListener(new ActionListener(){									//opens a file when open a file button is clicked
			public void actionPerformed(ActionEvent e){		
				openFileNow(spamMap,bagFrame,dicSizeNumber,totalWordsNumber,0,totalSpam);			//opens file and parse it
				if (model.getRowCount() > 0) {											//clear table rows
					for (int i = model.getRowCount() - 1; i > -1; i--) {
						model.removeRow(i);
					}
				}//close if
				
				Iterator it = spamMap.entrySet().iterator();
   				while (it.hasNext()) {													//place every entry of the hashmap to the rows of the model
					Map.Entry pair = (Map.Entry)it.next();
					model.addRow(new Object[]{pair.getKey(),pair.getValue()});
				}				
				dicSizeNumber.setText(Integer.toString((int)countUnique(hamMap, spamMap)));						//update text on display
			}
		});

		//open ham.txt
		open2.addActionListener(new ActionListener(){									//opens a file when open a file button is clicked
			public void actionPerformed(ActionEvent e){		
				openFileNow(hamMap,bagFrame,dicSizeNumber,totalWordsNumber,1,totalHam);	//opens file and parse it
				if (model2.getRowCount() > 0) {											//clear table rows
					for (int i = model2.getRowCount() - 1; i > -1; i--) {
						model2.removeRow(i);
					}
				}//close if
				
				Iterator it = hamMap.entrySet().iterator();
   				while (it.hasNext()) {													//place every entry of the hashmap to the rows of the model
					Map.Entry pair = (Map.Entry)it.next();
					model2.addRow(new Object[]{pair.getKey(),pair.getValue()});
				}

				dicSizeNumber.setText(Integer.toString((int)countUnique(hamMap, spamMap)));						//update text on display				
			}
		});//close open2

		filter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				float k=0;
				if(textArea.getText().trim().length() == 0 ){
					 JOptionPane.showMessageDialog(null, "Empty Input");
				}else if(textArea2.getText().trim().length() == 0 ){
					JOptionPane.showMessageDialog(null, "No Value for /K/");
				}else{
					String inputText = textArea.getText();
					
					try{
						k = Float.parseFloat(textArea2.getText());
					}catch(Exception exp){
						exp.printStackTrace();	
					} 

					String[] inputList = inputText.split("\n");
					
					if (model3.getRowCount() > 0) {											//clear table rows
						for (int i = model3.getRowCount() - 1; i > -1; i--) {
							model3.removeRow(i);
						}
					}//close if

					for(String s : inputList){												//add 1 by one per line in text area
						if(s.trim().isEmpty()) continue;									//skip empty lines
						model3.addRow(new Object[]{s, processLine(s, spamMap, hamMap, k)});	//add line and corresponding classification
					}
				}
			}
		});//close filter 

		//button headers and label
		FlowLayout center = new FlowLayout(FlowLayout.CENTER);

		//browse buttons
		JPanel buttonCon = new JPanel(new FlowLayout(20));
		buttonCon.setLayout(center);
		buttonCon.add(open);
		buttonCon.add(open2);
		
		//dicitonary size and total word count
		JPanel descCon = new JPanel(new FlowLayout(20));
		descCon.setLayout(center);
		descCon.add(dicSize);
		descCon.add(dicSizeNumber);
		descCon.add(totalWords);
		descCon.add(totalWordsNumber);

		//main header panel
		JPanel top = new JPanel(new GridLayout(2,1));
		top.add(buttonCon);
		top.add(descCon);

		//tables, description and input/output (including buttons)
		//ham description
		JPanel desc1 = new JPanel(new FlowLayout());
		desc1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		desc1.add(hamSize);
		desc1.add(totalHam);

		//spam description
		JPanel desc2 = new JPanel(new FlowLayout());
		desc2.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		desc2.add(spamSize);
		desc2.add(totalSpam);

		//spam table + description
		JPanel table1 = new JPanel(new BorderLayout());
		table1.add(new JScrollPane(bagTable), BorderLayout.CENTER);
		table1.add(desc2, BorderLayout.SOUTH);

		//ham table + description
		JPanel table2 = new JPanel(new BorderLayout());
		table2.add(new JScrollPane(bagTable2), BorderLayout.CENTER);
		table2.add(desc1, BorderLayout.SOUTH);

		//input output
		JPanel table3 = new JPanel(new GridLayout(2,1));
		JPanel table31 = new JPanel(new BorderLayout());
		
		//text area
		table31.add(new JScrollPane(textArea2), BorderLayout.NORTH);
		table31.add(new JScrollPane(textArea), BorderLayout.CENTER);
		table31.add(filter, BorderLayout.SOUTH);
		table3.add(table31);
		//table for output
		table3.add(new JScrollPane(output));

		//compile 3 tables
		JPanel tableCon = new JPanel(new GridLayout(1,2));
		tableCon.add(table1);
		tableCon.add(table2);
		tableCon.add(table3);

		//add header and center piece to container
		bagContainer.add(top,BorderLayout.NORTH);		
		bagContainer.add(tableCon, BorderLayout.CENTER);
		
		//necessary stuff
		bagFrame.pack();
		bagFrame.setVisible(true);
	}	
	
	public static void openFileNow(HashMap<String,Integer> spamMap, JFrame bagFrame,JLabel dicSizeNumber, JLabel totalWordsNumber, int classification, JLabel changeThis){//JButton[][] buttons, JFrame gameFrame){
		int totalWords = 0;
		int dictionarySize = 0;
		int counter = 0;
		spamMap.clear();																//reset word map
		JFileChooser chooser = new JFileChooser();										//open file
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = chooser.showOpenDialog(bagFrame);
		if(result == JFileChooser.APPROVE_OPTION){										//after click okay
			File selectFile = chooser.getSelectedFile();	
			BufferedReader br = null;
			try{
			br = new BufferedReader(new FileReader(selectFile.getAbsolutePath()));	//create a new buffered reader for
			String currentLine;														//the selected file via get absolute path of the file
			int row = 0;
				while((currentLine = br.readLine()) != null){
					counter++;
					String[] tempLine = currentLine.split(" ");							//split line via space	
					for(String s : tempLine){
						s = s.replaceAll("[^A-Za-z0-9]", "");							//replace non alpha numeric characters
						s = s.toLowerCase().trim();
						if(s.equals("")) continue;										//if all is non alphanumeric, skip
						if(spamMap.containsKey(s)) spamMap.put(s, spamMap.get(s) + 1);
						else {
							spamMap.put(s,1);
							dictionarySize++;											//update size of dictionary
						}
						totalWords++;													//update no of words every time
					}
				}
			} catch(IOException e){
				e.printStackTrace();
			} finally{
				try{
					if(br != null) br.close();											//just in case
				}catch(IOException e){
					e.printStackTrace();
				}
			}//finally
		}//close if approve option
		if(classification == 1){														//if input ham
			hamDic = dictionarySize;													//update ham
			hamLines = counter;
			hamTot = totalWords;
		}else{																			//else
			spamDic = dictionarySize;													//update spam
			spamLines = counter;
			spamTot = totalWords;
		}

		//update labels
		
		totalWordsNumber.setText(Float.toString(hamTot+spamTot));		
		changeThis.setText(Integer.toString(totalWords));
	}//close openfilenow

	public static String processLine(String line, HashMap<String,Integer> spamMap, HashMap<String,Integer> hamMap, float k){
		float tempA = 1, tempB = 1, x;

		line = line.toLowerCase();										//lowercase
		String[] tempLine = line.split(" ");							//split line via space	

		//spam/ham probability
		float pSpam = (spamLines+k)/(spamLines + hamLines + (2*k));
		float pHam = (hamLines+k)/(spamLines + hamLines + (2*k));

		//new words
		float newSpam = countNew(spamMap,tempLine);
		float newHam = countNew(hamMap,tempLine);

		//dictionary
		float unique = countUnique(hamMap, spamMap);

		//compute for ham side
		for(String s : tempLine){
			s = s.replaceAll("A-Za-z0-9","");
			if(!hamMap.containsKey(s)) x = (k / (float)(hamTot + (k * (unique + newHam))));
			else x = (((float)hamMap.get(s) + k) / (float)(hamTot + (k * (unique + newHam))));
			System.out.println(x);											//update 
			tempA = tempA*x;			//update
		}
		tempA = tempA*pHam;

		//compute for spam side
		for(String s : tempLine){
			s = s.replaceAll("A-Za-z0-9","");
			if(!spamMap.containsKey(s)) x = (k / (float)(spamTot + (k * (unique + newSpam))));
			else x = (( (float)spamMap.get(s) + k) / (float)(spamTot + (k * (unique + newSpam))));
			tempB = tempB*x;
			//System.out.println(x);											//update 
		}
		tempB = tempB*pSpam;

		//0.5 threshold
		System.out.println((tempB/(tempB+tempA)));
		if((tempB/(tempB+tempA)) >= 0.5) return "SPAM";
		else return "HAM";
	}
	
	//count new words
	public static float countNew(HashMap<String,Integer> wordMap, String[] line){
		float counter=0;
		for(String s : line){
			s = s.replaceAll("A-Za-z0-9","");
			if(!wordMap.containsKey(s.trim())) counter++;
		}
		return counter;
	}//close count new

	//counte unique words for total dicitonary size
	public static float countUnique(HashMap<String,Integer> hamMap, HashMap<String,Integer> spamMap){
		float counter = hamMap.size();
		Iterator it = spamMap.entrySet().iterator();
		
		for(String s : spamMap.keySet()){
			if(hamMap.containsKey(s)) continue;
			counter++;
		}

		return counter;
	}//close count new
}//close class
