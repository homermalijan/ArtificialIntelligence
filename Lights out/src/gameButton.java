import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class gameButton extends JButton{
	private boolean init = getRandomBoolean();

		public gameButton(){										//constructor
			super();
			this.setPreferredSize(new Dimension(99,100));
			this.setBackground(Color.BLACK);
			//if(init) this.setBackground(Color.BLACK);
			//else this.setBackground(Color.YELLOW);
		}

		public boolean getRandomBoolean() {							//for randomization purposes
	    	Random random = new Random();
	    	return random.nextBoolean();
		}

		public void toggle(){										//inverts the button's background
			if(this.getBackground() == Color.BLACK){
				this.setBackground(Color.YELLOW);
			}else{
				this.setBackground(Color.BLACK);
			} 
		} 
}
