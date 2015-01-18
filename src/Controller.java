//Gary Zeng
//2012-03-19
//Physics Simulation, Test version 2
//Implements basic kinimatics, torque, springs

//Ms S ICS4U1






import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Calendar.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;

//import java.awt.image.BufferedImage;
//import java.lang.NumberFormatException;
//import java.util.Hashtable;
//import java.awt.geom.*;



//////////////////////////////////////////////////////////// Controller
class Controller extends JFrame 
implements ActionListener,CaretListener,ChangeListener{
    
    public static void main(String[] args) {
        Controller window = new Controller();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
		//window.show();
		
    }//end main
	
	
	
	SimWindow canvas ;//= new SimWindow();
	//canvas.addActionListener(this);
	static Vars VX;
	
	
	public static boolean START = false;
	public static Timer updateTimer, fpsTimer;
	public static final int
		modeSetGlobals=1
		
	;
	
	//=== IMPORTANT STUFF
	public static int MODE=0;
	public PhysicsObj selectedObj;
	static ModeHandler curHandler;
	
	public static int updateDelay=15;
	
	
	
	
	//== LAYOUT PANELS
	public static JPanel ctrlPanel;
	public static JTextField textFl1;
	public static Container mainWindow, titleArea,saveLoadArea;
	public static JTextArea textOutput;
	public static JButton startStopButton;
	
	
	//======Data input Fields
	public static int nTextField=7;
	public static JTextField inputFields[] = new JTextField[nTextField];
	//public static String inputLabels[] = new String[6];
	public static JLabel inputLabels[] = new JLabel[nTextField];
	public static double minVal[] = new double[nTextField], maxVal[] = new double[nTextField];
	public static String defaultFieldVals[] = new String[nTextField];
	
	public static int nL=0;
	
	
	public static int editHandles=0;
	
	
	//====== Check Boxes
	public static int nCheckBox = 2;
	public static JCheckBox checkBoxes[] = new JCheckBox[nCheckBox];
	public static JLabel checkLabels[] = new JLabel[nCheckBox];
	
	//public static JLabel titleText="Untitled Project";
	
	//==== SMALL BUTTONS
	public static int nButton = 4;
	public static JButton inputButtons[] = new JButton[nButton];
	
	
	
	//==== MAIN OBJECTS
	public static ArrayList<PhysicsObj> allObj = new ArrayList<PhysicsObj>();//array of objects
	//public static int n=0;//number of objects
	public static boolean running=false;
	

	//==== BIG BUTTONS
	public static  int nBigButton=6;
	public static JButton[] bigButtons = new JButton[nBigButton];
	
	//==== SLIDERS
	public static int nSlider=2;
	public static JSlider[] sliders = new JSlider[nSlider];
	
	
	public static double mx=0,my=0;
	
	//==== SAVE FILE SELECTOR
	static JFileChooser chooser;
	static File mainFolder;
	public static final String myFileExtension = "GARY_PHYSICS";
	
    public Controller() {//Setup main window
		VX = new Vars();
		VX.setUnits(80.0); 
		
		ModeHandler.setCtrl(this);
		ModeHandler.VX = VX;
		
		PhysicsObj.setCtrl(this);
		PhysicsObj.VX = VX;
		
		
		//=== File Chooser
		
		try{
			mainFolder = new File((new File(".")).getCanonicalPath() + "/Saved" );
		} catch (Exception e){
			System.out.println("Getting Saved folder error: " + e);
		}
		
		chooser = new JFileChooser(mainFolder);
		
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension(myFileExtension);
		filter.setDescription("Saved Gary Physics Simulations");
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		//PhysicsObj.setUnits(50.0);
        //--- create the buttons
		
		
        
        //--- layout the buttons
        mainWindow = this.getContentPane();
        mainWindow.setLayout(new BorderLayout());
		this.setResizable(false);
		
		ctrlPanel = new JPanel();
		ctrlPanel.setLayout(new GridLayout(15,1));
		mainWindow.add(ctrlPanel,BorderLayout.EAST);
		
		//textFl1 = new JTextField("Testing",12);
		
		
		//ctrlPanel.add(new JTextField("Testing",12), BorderLayout.SOUTH);
		
		
		//titleArea = new JLabel("TITLE AREA");
		//ctrlPanel.add(titleArea);
		
		
		//===TEXT OUTPUT
		//addCaretListener
		textOutput = new JTextArea();
		textOutput.setLineWrap(true); 
		textOutput.setWrapStyleWord(true); 
		textOutput.setEditable(false);
		setOutputText("Initialized.");
		ctrlPanel.add(textOutput);
		
		
		
		
		//=== START/STOP
		startStopButton = new JButton("START");
		startStopButton.addActionListener(this);
		ctrlPanel.add(startStopButton);
		
		
		
		
		titleArea = new JLabel("blank");
		
		ctrlPanel.add(new JLabel(""));
		
		
		//=== MainInputBox
		Container mainInputCont = new Container();
		inputLabels[nL] = new JLabel("xy");
		inputFields[nL] = new JTextField("nothing");
		
		
		
		mainInputCont.setLayout(new GridLayout(3,1));
		mainInputCont.add(inputLabels[nL]);
		mainInputCont.add(inputFields[nL]);
		nL++;
		

		ctrlPanel.add(mainInputCont);
		
		//ctrlPanel.add(new JLabel(""));
		
		canvas = new SimWindow();
		mainWindow.add(canvas, BorderLayout.CENTER);
		
		
		//ctrlPanel.set
        
        //=== Multi input stuff
		Container inputQuad = new Container();

		for (int i=1;i<nTextField;++i){
			if (i%3==1){
				inputQuad = new Container();
				inputQuad.setLayout(new GridLayout(3,2));
				ctrlPanel.add(inputQuad);
			}
			if (i%2==1){
				//System.out.println("here");
				for (int j=0;j<2;++j){
					inputLabels[i + j] = new JLabel("nothing");
					inputQuad.add(inputLabels[i + j]);
					
				}
			} else {
				for (int j=0;j<2;++j){
					inputFields[i + j-1] = new JTextField();
					inputFields[i + j-1].addActionListener(this);
					inputFields[i + j-1].addCaretListener(this);
					inputQuad.add(inputFields[i + j -1]);
				}
			}
		}
		
		//=== SMALL BUTTONS
		for (int i=0;i<1;++i){
			inputQuad = new Container();
			inputQuad.setLayout(new GridLayout(3,2));
			ctrlPanel.add(inputQuad);
			
			for (int j=0;j<2;++j){
				inputQuad.add(new JLabel(""));
			}
			
			for (int j=0;j<nButton;++j){
				inputButtons[j] = new JButton("nothing here");
				inputButtons[j].setEnabled(false);
				inputButtons[j].addActionListener(this);
				inputQuad.add(inputButtons[i*2+j]);
				
			}
		}
		
		
		
		//==== Check Boxes
		inputQuad = new Container();
		inputQuad.setLayout(new GridLayout(3,2));

		for (int j=0;j<nCheckBox;++j){//Check box labels
			checkLabels[j] = new JLabel("Check");
			inputQuad.add(checkLabels[j]);
		}
		
		for (int j=0;j<nCheckBox;++j){// actual check boxes
			checkBoxes[j] = new JCheckBox();
			inputQuad.add(checkBoxes[j]);
			checkBoxes[j].addActionListener(this);
		}
		
		ctrlPanel.add(inputQuad);
		
		//======== SLIDERS
		//Container doubleSlider = new Container();
		//doubleSlider.setLayout(new GridLayout(2,1));
		
		for (int i=0;i<nSlider;++i){
			sliders[i] = new JSlider(JSlider.HORIZONTAL,0,30,15);

//Turn on labels at major tick marks.
		//sliders[i].setMajorTickSpacing(10);
		//sliders[i].setMinorTickSpacing(1);
		//sliders[i].setPaintTicks(true);
		//sliders[i].setPaintLabels(true);
		//sliders[i].setMaximum(20);
			
			//sliders[i].setPaintLabels(true);
			
			sliders[i].setSnapToTicks(false); 
			sliders[i].addChangeListener(this);
			ctrlPanel.add(sliders[i]);
		}
		
		
		//ctrlPanel.add(doubleSlider);
		//sliders[0].setBorder(BorderFactory.createTitledBorder("asdfasdf"));
		//ctrlPanel.add(new JLabel(""));
		
		//=== Big Buttons
		for (int i=0;i<3;++i){
			Container doubleButtons = new Container();
			doubleButtons.setLayout(new GridLayout(2,1));
			for (int j=0;j<2;++j){
				bigButtons[i*2+j] = new JButton("");
				bigButtons[i*2+j].addActionListener(this);
				doubleButtons.add(bigButtons[i*2+j]);
			}
			ctrlPanel.add(doubleButtons);
		}
		
		

		
		
		//=== Initialize timer
		updateTimer = new Timer(updateDelay,this); 
		updateTimer.start();
		//fpsTimer = new Timer(15,this); 
		//fpsTimer.start();
		
		mx=my=-1;
		
		
		curHandler = new MainHandler();
		START = true;
		//== ==Intialize something
		//ropeThing();
		//rotatingThingsConnectedByBars();
		//running = true;
		
		//ropeThing();
		//allObj[n++]

        this.setTitle("Physics Simulation [Gary Zeng]");
        this.pack();
    }//end constructor
	
	public void chooseSave() throws Exception{
		if (chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
			saveState(new File(chooser.getSelectedFile().getAbsolutePath() + "." + myFileExtension));
		}
	}
	
	public static void saveState(File file) throws Exception{
		//if (running){toggleRunning();}
		VX.nObj = allObj.size();

		//System.out.println(parent.getCanonicalPath() + "/Saved/" + name + "/main.dat");
		
		Calendar cur =  Calendar.getInstance();
		
		mainFolder.mkdirs();//Make the "Saved" folder if it doesn't exist
		
		
		ObjectOutputStream out = new ObjectOutputStream(new  FileOutputStream(file));
		
		
		/*(new File(mainFolder.getCanonicalPath() + "/["
+  
			cur.get(Calendar.YEAR) + "-" + (cur.get(Calendar.MONTH)+1) + "-" + cur.get(Calendar.DAY_OF_MONTH)
			+ " " + cur.get(Calendar.HOUR_OF_DAY) + "." + cur.get(Calendar.MINUTE) + "." + cur.get(Calendar.SECOND)  + "] "
			+ name + ".GARY_PHYSICS"))*/
			
		out.writeObject(VX);
		
		for (int i=0;i<allObj.size();++i){//label indice positions
			allObj.get(i).arrayListPos = i;
		}
		
		for (int i=0;i<allObj.size();++i){
			allObj.get(i).preSave(allObj);
		}
		
		for (int i=0;i<allObj.size();++i){
			out.writeObject(allObj.get(i));
		}
		
		out.close();
	}
	
	public void chooseLoad() throws Exception {
		if (running){toggleRunning();}
		
		//int val=chooser.showOpenDialog(this);
		if (chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			System.out.println("File to Load: " + chooser.getSelectedFile().getName());
			loadState(chooser.getSelectedFile());
		}
	}
	
	public static void loadState(File file) throws Exception{

		//name = "C:/Users/PropagandaPanda/Desktop/Zeng Gary Physics Sim 2012-06-10 Make It Save/Saved/[2012-6-10 12.29.47] gary.GARY_PHYSICS";

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)); 
		
		
		allObj.clear();
		
		VX = null;
		VX = PhysicsObj.VX = ModeHandler.VX =  (Vars)in.readObject();
		
		
		for (int i=0;i<VX.nObj;++i){
			addObj((PhysicsObj)in.readObject());
		}
	}
	
	
	//==========
	public static void addObj(PhysicsObj toAdd){
		allObj.add(toAdd);
	}

    //====================================================== constructor
	public static void setOutputText(String text){
		textOutput.selectAll();
		textOutput.replaceSelection(text);

	}
	
	//public static void set
	
	public static void resetAllFields(){//Clear all fields and buttons and checkboxes, disable them
		for (int i=0;i<nTextField;++i){//Text fields
			inputLabels[i].setText("");
			inputLabels[i].setToolTipText("");
			defaultFieldVals[i] = "";
			minVal[i] = maxVal[i] = 0.0;
			setToDefault(i);
			inputFields[i].setEditable(false);
			
		}
		
		for (int i=0;i<nButton;++i){
			inputButtons[i].setText("");
			inputButtons[i].setToolTipText("");
			inputButtons[i].setEnabled(false);
		}
		
		for (int i=0;i<nBigButton;++i){
			bigButtons[i].setText("");
			bigButtons[i].setToolTipText("");
			bigButtons[i].setEnabled(false);
		}
		
		for (int i=0;i<nCheckBox;++i){//Check boxes
			checkLabels[i].setText("");
			checkLabels[i].setToolTipText("");
			checkBoxes[i].setEnabled(false);
			checkBoxes[i].setSelected(false);
		}
		
		for (int i=0;i<nSlider;++i){
			sliders[i].setBorder(BorderFactory.createTitledBorder(""));
			//sliders[i].setMinimum(0);
			//sliders[i].setMaximum(1);
			//sliders[i].setMajorTickSpacing(1);
			//sliders[i].
			sliders[i].setPaintLabels(false);
			sliders[i].setEnabled(false);
		}
	}
	
	public static void setToDefault(int i){
		editHandles ++;
		inputFields[i].selectAll();
		inputFields[i].replaceSelection(defaultFieldVals[i]);
		editHandles --;
	}
	
	public static void configMyField(int i,String label,String toolTipText,double min,double max,String def){//i is field number
		inputLabels[i].setText(label);
		inputLabels[i].setToolTipText(toolTipText);
		inputFields[i].setToolTipText(toolTipText);
		inputFields[i].setEditable(true);
		
		minVal[i] = min;
		maxVal[i] = max;
		defaultFieldVals[i] = def;
		setToDefault(i);
		
	}
	
	public static void configMyButton(int i,String label,String toolTipText){
		inputButtons[i].setText(label);
		//inputButtons[i].setToolTipText(toolTipText);
		inputButtons[i].setEnabled(true);
	}
	
	public static void configMyBigButton(int i,String label,String toolTipText){
		bigButtons[i].setText(label);
		bigButtons[i].setEnabled(true);
	}
	
	public static void configMyBigButton(int i,String label){
		configMyBigButton(i,label,"");
	}
	
	public static void configMyCheckBox(int i,String label,String toolTipText,boolean state){
		checkLabels[i].setText(label);
		checkLabels[i].setToolTipText(toolTipText);
		checkBoxes[i].setEnabled(true);
		checkBoxes[i].setSelected(state);
		checkBoxes[i].setToolTipText(toolTipText);
	
	}
	
	public static void configMySlider(int i,String label,String toolTipText,int min,int max,int cur){
		
		
		if (min>max){
			min ^= max;  max ^= min; min ^= max;
		}
		//sliders[i] = new JSlider(JSlider.HORIZONTAL,min, max, cur);

		sliders[i].setBorder(BorderFactory.createTitledBorder(label));
		
		sliders[i].setPaintLabels(false);
		sliders[i].setMajorTickSpacing(max-min);
		sliders[i].setMinorTickSpacing(0);
		sliders[i].setPaintLabels(true);

		sliders[i].setMinimum(min);
		sliders[i].setMaximum(max);
		sliders[i].setValue(cur);
		
		//sliders[i].setMinorTickSpacing(0);
		sliders[i].setToolTipText(toolTipText);
		
		sliders[i].setEnabled(true);
		
		//sliders[i].paint();
	}
	
	//public void activateSetGlobalsMode(){
	//	MODE = modeSetGlobals;
	//}
	
	public static void toggleRunning(){
		running = !running;
		startStopButton.setText((running) ?"STOP" : "START");
		if (running){
			setOutputText("Simulation started.");
		} else {
			setOutputText("Simulation paused.");
		}
	}
	
	public static void setUpdateDelay(int val){
		updateDelay = val;
		updateTimer.setDelay(val);
		
	}
	

	public void stateChanged(ChangeEvent e) {
		if (!START){return;}
		for (int i=0;i<nSlider;++i){
			if (sliders[i]==e.getSource()){
				//if (!sliders[i].getValueIsAdjusting()) {
					int val = sliders[i].getValue();
					curHandler.sliderMoved(i,val);
				//}
				return;
			}
		}
	
	}
	
	public void actionPerformed(ActionEvent evt){
		if (!START){return;}
		
		if(evt.getSource()==updateTimer ){// Clock Tick UPDATES
			//System.out.println(PhysicsObj.time);
			if (running){
				VX.time += 1/VX.uPerSecond;
				

				for (int i=0;i< allObj.size() ;++i){//grav/frict
					allObj.get(i).updateEnviro(allObj);

				}
				
				curHandler.updateTick();
				
				//regular stuff
				for (int t=0;t<=4;++t){
					if (t==2){continue;}
					for (int i=0;i<allObj.size();++i){
						if (allObj.get(i).ORD==t){
							allObj.get(i).update(allObj);
						}
					}
				}
				
				
				for (int k=0;k<3;++k){
				//Sticks
					for (int t=0;t<3;++t){
						for (int i=0;i<allObj.size();++i){
							if (allObj.get(i).TYPE==2 && allObj.get(i).ORD==2){
								allObj.get(i).update(allObj);
							}
						}
					}
					
					//walls
					for (int t=0;;t++){
						Wall.didHit=0;
						for (int i=0;i<allObj.size();++i){
							if (allObj.get(i).ORD==5){
								allObj.get(i).update(allObj);
							}
						}
						
						if (t==3){
							System.out.println("Something fell through a wall!");
							break;
						}
						
						if (Wall.didHit==0){
							break;
						}
						

					}
				}
				//Actual updates
				for (int t=6;t<=10;++t){
					for (int i=0;i<allObj.size();++i){
						if (allObj.get(i).ORD==t){
							allObj.get(i).update(allObj);
						}
					}
				}
				
				for (int i=0;i<allObj.size();++i){
					allObj.get(i).endCycle(allObj);
				}
			}
			
			//====DRAWING IS NOW HERE!
			curHandler.fpsTick();
			canvas.repaint();
			
			return;
		}
		
		
		//if(evt.getSource()==fpsTimer){//Engine timer

			//return;
		//}
		if(evt.getSource()==startStopButton){
			toggleRunning();
			return;
		} 
		
		for (int i=0;i<nBigButton;++i){
			if (evt.getSource()==bigButtons[i]){
				curHandler.bigButtonPressed(i);
				return;
			}
		}
		
		//else if (evt.getSource()==bigButtons[2]){
		//	curHandler = new ModeAddFreeMasses();
		//} else if (evt.getSource()==bigButtons[3]){// INTERACT
		//	curHandler = new ModeInteractive();
		//} else if (evt.getSource()==bigButtons[4]){
		//	curHandler = new ModeAddBars();
		//} else if (evt.getSource()==bigButtons[5]){
		//	n = 0;
		//} 


		
		
		//==== Check field ENTER key pressed
		boolean isTextField=false;

		int id=identFieldInput(evt);//indice of the field or -1 if not a text input field
		if (id>=0){
			handleTextFieldEvent(id);
			
		}
		
		//=== Buttons
		for (int i=0;i<nButton;++i){
			if (evt.getSource()==inputButtons[i]){
				curHandler.buttonPressed(i);
				return;
				//break;
			}
		}
		
		//=== CheckBoxes
		for (int i=0;i<nCheckBox;++i){
			if (evt.getSource()==checkBoxes[i]){
				curHandler.checkBoxChanged(i,checkBoxes[i].isSelected());
				return;
			}
		}
		
		
	}
	

	
	public static void handleTextFieldEvent(int id){
		if (id==0){//the text input box
			//nothing here atm
		} else {
			double val = getFieldVal(id);
			if (!Double.isNaN(val)){
				System.out.println("Valid input!" + val);
				setOutputText("[" + inputLabels[id].getText() + "] set to: " + val);
				curHandler.fieldInput(id,val);
				
				
			} else {
				setOutputText("Bad input!\n Value [" + minVal[id] + " <= x <= " + maxVal[id] + "]");
				System.out.println("Bad input!");
				
			}
		}
	}
	
	public static double getFieldVal(int i){
		String text = inputFields[i].getText().trim();
		if (text.length()>0){
			double val;
			
			try {
				val = Double.valueOf(text);
			} catch (NumberFormatException exp){
				return Double.NaN;
			}
			
			if (minVal[i]<=val && val<=maxVal[i]){
				return val;
			}
		}
		
		
		//setToDefault(i);
		return Double.NaN;
	}
	
	public int identFieldInput(ActionEvent evt){//gets which field was inputed into
		for (int i=0;i<nTextField;++i){
			if (evt.getSource()==inputFields[i]){
				for (int j=0;j<nTextField;++j){//reset other fields
					if (i!=j){
						setToDefault(j);
					}
				}
				
				return i;
			}
		}
		return -1;
	}
	
	
	
	public void caretUpdate(CaretEvent e){//NOTHING ATM
		if (editHandles>0){//program edited
			return;
		}
		//System.out.println("keyed");
		
		//DISABLED
		for (int i=0;i<0;++i){//stop retarded multiediting of fields
			if (e.getSource()==inputFields[i]){
				for (int j=0;j<nTextField;++j){
					if (j!=i && inputFields[i].isEditable()){
						//setToDefault(j);
					}
				}
			}
		}
		
	}
	
	public static void mousePressed(int mx,int my){
		Controller.mx = mx;
		Controller.my = my;
	}
	
	public static void mouseReleased(int mx,int my){
		Controller.mx = mx;
		Controller.my = my;
	}
	
	public static void mouseDragged(int mx,int my){
		Controller.mx = mx;
		Controller.my = my;
	}
	
	public static void mouseExited(int mx,int my){
		Controller.mx = mx;
		Controller.my = my;
	}
	


}//endclass Controller



///////////////////////////////////////////////////////////// SimWindow

class SimWindow extends JPanel implements MouseListener, //Also does the simulation stuff
                                           MouseMotionListener 
										   /*ActionListener*/{
    //--- Public constants used to specify shape being drawn.          

    //--- Private constant for size of paint area.
    private static final int SIZE = 1000; // size of paint area

	
    public SimWindow() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setBackground(Color.white);
        //--- Add the mouse listeners.
        this.addMouseListener(this); 
        this.addMouseMotionListener(this);

		
    }//endconstructor
	
	
	//public void actionPerformed(ActionEvent evt){
		//System.out.println(count++);

	//}

    //=================================================== paintComponent
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;  // downcast to Graphics2D

		
		
        g2.setColor(Color.black);
		
        g2.fillRect(0, 0, SIZE, SIZE); // fill in background
		
		//box1.draw(g2);
		//System.out.println("LOC: " + box1.cx + " " + box1.cy);
		for (int i=0;i<Controller.allObj.size();++i){
			if (Controller.allObj.get(i).visible){
				Controller.allObj.get(i).draw(g2);
			}
		}
		
		Controller.curHandler.drawExtra(g2);
		//bar.ang+=0.01;
		g2.setColor(Color.white);
		

    }//end paintComponent

    //===================================================== mousePressed
    public void mousePressed(MouseEvent e) {
		 Controller.curHandler.mousePressed(e.getX(),e.getY());
    }

    //===================================================== mouseDragged
    public void mouseDragged(MouseEvent e) {
		Controller.curHandler.mouseDragged(e.getX(),e.getY());
		//this.repaint();            // show new shape
    }//end mouseDragged
    
    //==================================================== mouseReleased
    public void mouseReleased(MouseEvent e) {
		 Controller.curHandler.mouseReleased(e.getX(),e.getY());
        //this.repaint();
    }//end mouseReleased
    
	 public void mouseExited  (MouseEvent e) {
		Controller.curHandler.mouseExited(e.getX(),e.getY());
		
	 }
	 
	
    //========================================== ignored mouse listeners
    public void mouseMoved   (MouseEvent e) {
		Controller.curHandler.mouseMoved(e.getX(),e.getY());
	}
    public void mouseEntered (MouseEvent e) {
		Controller.curHandler.mouseEntered(e.getX(),e.getY());
	}
   
    public void mouseClicked (MouseEvent e) {}
}//endclass SimWindow 

