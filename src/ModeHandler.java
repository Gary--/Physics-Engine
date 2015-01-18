import java.awt.*;
import java.util.ArrayList;
import java.io.*;
//import Ctrl.*;

class ModeHandler{
	static Controller Ctrl;
	static Vars VX;
	//static START=false;

	static int mx,my;
	static int startX,startY;
	static boolean mouseOnCanvas=false;
	static boolean mouseDown=false;
	
	static final int mouseDragBox=1;
	static int mouseMode=1;
	//0=nothing
	//1=drag box
	
	
	//== PHYSICS OBJ SIGNALS
	boolean badStretch = false;
	
	public static void  setCtrl(Controller a){
		Ctrl = a;
	}
	
	public ModeHandler(){
	
	
		if (Ctrl.curHandler!=null){
			Ctrl.curHandler.close();
		}
		Ctrl.resetAllFields();
		mouseMode = 0;
		
		Ctrl.configMyBigButton(5,"BACK");
	}
	
	public void updateMousePos(int mx,int my){
		this.mx = mx;
		this.my = my;
	}
	
	public void  mousePressed(int mx, int my){
		mouseDown = true;
		mouseOnCanvas = true;
		startX = mx;
		startY = my;
		updateMousePos(mx,my);
	}
	
	public static void doRelease(){
		mouseDown = false;
		startX = mx;
		startY = my;
	};
	
	public  void mouseDragged(int mx, int my){
	//System.out.println("Drag");
		mouseOnCanvas = true;
		//mouseDown = true;
		updateMousePos(mx,my);
		if (!mouseDown){
			doRelease();
		}
		
	}
	
	public  void mouseMoved(int mx, int my){
		//System.out.println("move");
		mouseOnCanvas = true;
		mouseDown = false;
		updateMousePos(mx,my);
		doRelease();
	}
	
    public void mouseEntered (int mx,int my) {
		mouseOnCanvas = true;
		mouseDown = false;
		updateMousePos(mx,my);
		doRelease();
	}
   
	
	public void mouseReleased(int mx,int my){
		mouseOnCanvas = true;
		mouseDown = false;
		startX = mx;
		startY = my;
		updateMousePos(mx,my);
		doRelease();
	}
	
	public void mouseExited(int mx,int my){
		mouseReleased( mx, my);
	
		mouseOnCanvas = false;
		mouseDown = false;
		doRelease();
	}
	
	public void fieldInput(int f,double val){
		System.out.println("Warning: Abstract call to fieldInput()");
	}
	
	public void drawExtra(Graphics2D g2){
		if (mouseMode==mouseDragBox && mouseDown){
			g2.setColor(Color.green);
			g2.setStroke(new BasicStroke(1));
			
			//=== Swap if start is greater than current
			int cmx=mx, cmy=my, cstartX = startX, cstartY = startY;
			if (cstartX>cmx){cmx ^= cstartX; cstartX ^= cmx;cmx ^= cstartX;}
			if (cstartY>cmy){cmy ^= cstartY; cstartY ^= cmy;cmy ^= cstartY;}
			
			if (!(cmx==cstartX && cmy==cstartY)){ g2.drawRect(cstartX,cstartY,cmx-cstartX,cmy-cstartY); }
			
			
			//System.out.println();
			//System.out.println("here " +startX + " " + startY + " " + (mx-startX) + " " + (my-startY) );
		}
	}
	
	public PhysicsObj[] getItemsInSelection(int mx,int my){
		int n=0;
		
		for (int i=0;i<Ctrl.allObj.size();++i){
			PhysicsObj pointer = Ctrl.allObj.get(i).isInRect(startX,startY,mx,my);
			if (pointer!=null){
				n ++ ;
			}
		}
		
		PhysicsObj[] items = new PhysicsObj[n];
		int j=0;
		for (int i=0;i<Ctrl.allObj.size();++i){
			PhysicsObj pointer = Ctrl.allObj.get(i).isInRect(startX,startY,mx,my);
			if (pointer!=null){
				items[j++] = pointer;
			}
		}
		
		return items;
		
	}
	
	public void buttonPressed(int i){}
	
	public void bigButtonPressed(int i){
		if (i==5){
			Ctrl.curHandler = new  MainHandler();
		}
	}
	
	public void sliderMoved(int i,int val){
		//System.out.println("Sider val: " + val);
	}
	
	public void fpsTick(){}
	public void updateTick(){}
	public void checkBoxChanged(int i,boolean state){}
	public void close(){
		System.out.println("Default close!");
	}
	
}

class MainHandler extends ModeHandler{
	
	
	public MainHandler(){
		super();
		Ctrl.configMyBigButton(0,"Set Globals");
		Ctrl.configMyBigButton(1,"Add Masses");
		Ctrl.configMyBigButton(2,"Add Connectors");
		
		
		
		
		//Ctrl.configMyBigButton(4,"Add bars");
		
		Ctrl.configMyBigButton(4,"Interact");
		Ctrl.configMyBigButton(5,"RESET");
	}
	
	
	public void bigButtonPressed(int i){
		if (i==0){
			Ctrl.curHandler = new ModeSetGlobals();
		} else if (i==1){
			Ctrl.curHandler = new ModeAddFreeMasses();
		} else if (i==2){// INTERACT
			Ctrl.curHandler = new SelectConnector();
		} else if (i==4){
			Ctrl.curHandler = new ModeInteractive();
		} else if (i==5){
			Ctrl.allObj.clear();
		}
		
		super.bigButtonPressed(i);
	}
	
	
	///public void fpsTick(){
		
		
	//}
	
	//public void drawExtra(Graphics2D g){
		//double ang = Wall.reduce(VX.myAtan2(mx-500,my-500));
		//g.drawLine(500,500,(int)(500+Math.cos(ang)*100),(int)(500+Math.sin(ang)*100));
		//System.out.println("Ang: " + Math.toDegrees(ang ));
	//}
}



class ModeSetGlobals extends ModeHandler{
	final static double stressSliderBase=1.05;
	public ModeSetGlobals(){
		super();

		
		//Ctrl.configMyField(1,"FPS","Max FPS (will be slower from computing time)",10.0,100.0,""+Ctrl.updateDelay);
		Ctrl.configMyField(3,"x-Gravity","Horizontal gravity (m/s^2)",-200.0,200.0,""+VX.xGrav/VX.mPerSec2);
		Ctrl.configMyField(4,"y-Gravity","Vertical gravity (m/s^2)",-200.0 ,200.0,""+VX.yGrav/VX.mPerSec2);
		Ctrl.configMyField(5,"Friction","Constant of friction (N/kg)",0,200.0,""+VX.kF/VX.mPerSec2 );
		
		
		Ctrl.configMySlider(0, "FPS","Max FPS (will be lower from computing time)",4,300,(int)Math.round(1000.0/Ctrl.updateDelay));
		Ctrl.configMySlider(1, "Stress Const","For color indicators",-20,200,(int)Math.round(Math.log(ConnectingBar.stressConst)/Math.log(stressSliderBase)));
		
		Ctrl.configMyButton(1,"Size++","Increase the size of the masses.");
		Ctrl.configMyButton(0,"Size--","Decrease the size of the masses.");
		
		Ctrl.configMyBigButton(3,"Save");
		Ctrl.configMyBigButton(4,"Load");
	}
	
	public void buttonPressed(int i){
		if (i==0){
			PhysicsObj.boxSize = Math.max(PhysicsObj.boxSize-2,3);
			
		} else if (i==1){
			PhysicsObj.boxSize = Math.min(PhysicsObj.boxSize+2,15);
		} else {
			System.out.println("WTF");
		}
		super.buttonPressed(i);
	}
	
	public void bigButtonPressed(int i){
		super.bigButtonPressed(i);
		if (i==3){
			try{
				Ctrl.chooseSave();
			}catch (Exception e){
				System.out.println("Save error: " + e);
			}
		} else if (i==4){
			try{
				Ctrl.chooseLoad();
				//Ctrl.loadState("adsf");
			}catch (Exception e){
				System.out.println("Load error: " + e);
			}
			
			Ctrl.curHandler = new ModeSetGlobals();
		}
	}
	
	public void fieldInput(int id,double val){
		boolean worked=true;
		//if (id==1){
			//Ctrl.updateTimer.setDelay((int)(Math.round(1000/val)));
		//	Ctrl.updateTimer.setDelay((int)(Math.round(val)));
		//	Ctrl.updateDelay = (int)(Math.round(val));
		//	//Ctrl.updateTimer;
		//} else 
		if (id==3){
			VX.xGrav = val*VX.mPerSec2;
		} else if (id==4){
			VX.yGrav = val*VX.mPerSec2;
		} else if (id==5){
			VX.kF = val*VX.mPerSec2 ;
		} else {
			worked = false;
		}
		
		
		if (worked){
			Ctrl.curHandler = new  ModeSetGlobals();
		}
		
	}
	
	public void sliderMoved(int i,int val){
		if (i==0){
			//System.out.println("Slider: " + val);
			Ctrl.setUpdateDelay((int)Math.round(1000.0/val));
		} else if (i==1){
			ConnectingBar.stressConst = Math.round(Math.pow(stressSliderBase,(double)val));
		}
	}
	
	public void close(){
		System.out.println("Globals close");
		//Ctrl.readAllTextFields();
	}
}


class ModeEditObjects extends ModeHandler{
	PhysicsObj targ;

	public ModeEditObjects(){
		super();
		ModeHandler.mouseMode = mouseDragBox;
		//Ctrl.resetAllFields();
		Ctrl.setOutputText("Edit mode initiated. \nSelect Object to edit");
	}
	
	public void close(){
		System.out.println("Edit close");
	}
	
}

class ModeAddObjects extends ModeHandler{
	//int curState=1;
	final static int massMode=1;
	
	public ModeAddObjects(){
		super();
	}
}



class ModeAddFreeMasses extends ModeHandler{
	static double vx,vy,m=1;
	static boolean fixed=false;
	static double charge=0.0;
	static int roundTo=10;
	static boolean roundCoord=false;
	
	static FreeMass tMass = new FreeMass();
	
	
	public ModeAddFreeMasses(){
		super();
		//Ctrl.resetAllFields();
		
		Ctrl.configMyField(1,"Mass","Mass of object to add (kg)",0.1,20,""+m);
		Ctrl.configMyField(2,"Charge","columb",-10,10,""+charge);
		Ctrl.configMyField(3,"x-Velocity","Horizontal velocity (m/s)",-100.0,100.0,""+vx/VX.mPerSec);
		Ctrl.configMyField(4,"y-Velocity","Vertical velocity (m/s)",-100.0 ,100.0,""+vy/VX.mPerSec);
		Ctrl.configMyField(5,"RoundTo","Coordinate rounding to nearest N",2.0 ,100.0,""+roundTo);
		
		Ctrl.configMyCheckBox(0,"Fixed","Set object as immovable",fixed);
		Ctrl.configMyCheckBox(1,"Round Coord","Set object as immovable",roundCoord);
		
		Ctrl.configMyBigButton(0,"Add Boxes");
	}
	
	static int round(int x,int k){
		return x - x%k + k*(((x%k)<(k/2)) ? 0 : 1);
	}
	
	public void checkBoxChanged(int i,boolean state){
		if (i==0){
			fixed = state;
			tMass.fixed = (fixed ? 1 : 0);
		} else if (i==1){
			roundCoord = state;
			
		
		} else {
			System.out.println("WTF!");
		}
	}
	
	public void bigButtonPressed(int i){
		if (i==0){
			Ctrl.curHandler = new  ModeAddBox();
		} else {
			super.bigButtonPressed(i);
		}
		
		
	}
	
	public void fieldInput(int id,double val){
		boolean worked=true;
		if (id==1){
			m = val;
		} else if (id==3){
			vx = val*VX.mPerSec;
		} else if (id==4){
			vy = val*VX.mPerSec;
			
		} else if (id==2){
			charge = val;
		} else if (id==5){
			roundTo = (int)val;
		
		} else {
			worked = false;
		}
		
		if (worked){
			Ctrl.curHandler = new  ModeAddFreeMasses();
		}
		
	}
	
	public static void addMass(int mx,int my){
		PhysicsObj newMass = new FreeMass(m,mx,my,vx,vy);

		newMass.fixed = (fixed ? 1 : 0);
		newMass.charge = charge;
		Ctrl.addObj(newMass);
		
	}
	
	public void mousePressed(int mx,int my){
		if (roundCoord){
			addMass((int)tMass.cx, (int)tMass.cy);
		} else {
			addMass(mx, my);
		}
	
		
		super.mousePressed(mx,my);
	}
	
	public void mousePressed2(int mx,int my){
		super.mousePressed(mx,my);
	}
	
	public void mouseMoved(int x, int y){
		super.mouseMoved(x,y);
		
		if (roundCoord){
			tMass.cx = round(x,roundTo);
			tMass.cy = round(y,roundTo);
		} else {
			tMass.cx = x;
			tMass.cy = y;
		}
		
	}
	
	public void drawExtra(Graphics2D g2){
		drawExtra2(g2);
		//System.out.println(mouseDown);
		if (mouseOnCanvas){
			//System.out.println("Draw extra!");
			tMass.draw(g2);
		}
		
	}
	
	public void drawExtra2(Graphics2D g2){
		super.drawExtra(g2);
		if (roundCoord){
			drawRoundedDots( g2);
		}
		
	}
	
	public void drawRoundedDots(Graphics2D g2){
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(1));
		for (int x=0;x<1000;x+=roundTo){
			for (int y=0;y<1000;y+=roundTo){
				g2.drawLine(x,y,x,y);
			}
		}
	}
	
	public void close(){
		System.out.println("Free masses close");
		//Ctrl.readAllTextFields();
	}
}


class ModeAddBox extends ModeAddFreeMasses{
	public ModeAddBox(){
		super();
		ModeHandler.mouseMode = mouseDragBox;
	}
	
	public void mousePressed(int mx,int my){
		
		super.mousePressed2(round(mx,roundTo),round(my,roundTo));//skip modeaddfree
	}
	
	/*public void bigButtonPressed(int i){
		if (i==5){
			Ctrl.curHandler = new ModeAddFreeMasses();
		} else {
			super.bigButtonPressed( i);
		}
		
	}*/
	public void mouseReleased(int mx,int my){

		if (mouseDown && mouseOnCanvas && mx!=startX && my!=startY){
			if (roundCoord){
				mx = round(mx,roundTo);
				my = round(my,roundTo);
			}
		
			if (startX>mx){mx^=startX;startX^=mx;mx^=startX;} 
			if (startY>my){my^=startY;startY^=my;my^=startY;}
			

			
			addMass(startX,startY);
			addMass(mx,startY);
			addMass(mx,my);
			addMass(startX,my);
			int n = Ctrl.allObj.size();
			for (int i=0;i<3;++i){
				Ctrl.addObj(new ConnectingBar(Ctrl.allObj.get(n-4+i),Ctrl.allObj.get(n-4+i+1)));
				if (i<2){
					Ctrl.addObj(new ConnectingBar(Ctrl.allObj.get(n-4+i),Ctrl.allObj.get(n-4+i+2)));
				}
				
				Ctrl.addObj(new Wall(Ctrl.allObj.get(n-4+i),Ctrl.allObj.get(n-4+i+1)));
				
			}
			
			Ctrl.addObj(new ConnectingBar(Ctrl.allObj.get(n-4),Ctrl.allObj.get(n-1)));
			Ctrl.addObj(new Wall(Ctrl.allObj.get(n-4),Ctrl.allObj.get(n-1)));
			
			
		}
		super.mouseReleased(mx,my);
	}
	
}


class SelectConnector extends ModeHandler{
	public SelectConnector(){
		super();
		
		Ctrl.configMyBigButton(0,"Add Sticks");
		Ctrl.configMyBigButton(1,"Add Elastics");
		Ctrl.configMyBigButton(2,"Add Walls");
		
	}
	
	public void bigButtonPressed(int i){
		System.out.println("Button number: " + i);
	
		if (i==0){
			Ctrl.curHandler = new ModeAddBars();
			//System.out.println("Initalize add bar!");
		} else if (i==1){
			Ctrl.curHandler = new ModeAddElastics();
			//System.out.println("Initalize add elastic!");
		} else if (i==2){
			Ctrl.curHandler = new ModeAddWalls();
			//System.out.println("Initalize add wall!");
		}
		super.bigButtonPressed(i);
	
	}
}



class AbstractAddConnectorsMode extends ModeHandler{
	public static PhysicsObj targA,targB;
	
	public void close(){
		targA = null;
	}
	
	public  AbstractAddConnectorsMode(){
		super();
		ModeHandler.mouseMode = mouseDragBox;
		targA = targB =  null;
	}
	
	public void drawExtra(Graphics2D g2){
		super.drawExtra(g2);
		if (targA!=null){
			targA.drawSelected(g2);
		}

	}
}

class ModeAddWalls extends AbstractAddConnectorsMode{
	public ModeAddWalls(){
		super();
		ModeHandler.mouseMode = mouseDragBox;
		System.out.println("Add walls");
	}
	
	public void mouseReleased(int mx,int my){
		//updateMousePos(mx,my);
		
		PhysicsObj[] items = getItemsInSelection(mx,my);
		if (items.length>0){
			if (targA==null){
				targA = items[0];
			} else if (items[0]!=targA) {
				Ctrl.addObj(new Wall(targA,items[0]));
				targA = null;
			}
			//ModeHandler.mouseMode = 0;
		}
		super.mouseReleased(mx,my);
	}
}


class ModeAddBars extends AbstractAddConnectorsMode{
	static double strength=100.0*VX.uPerNewton;
	static boolean canBreak=false;
	
	public ModeAddBars(){
		super();
		ModeHandler.mouseMode = mouseDragBox;
		System.out.println("Add bars!");
		
		Ctrl.configMyField(1,"Breaking Force","N [0.1-10000]",0.1,10000.0,"" + strength/VX.uPerNewton);
		
		Ctrl.inputFields[1].setEditable(canBreak);
		
		Ctrl.configMyCheckBox(0,"Can Break","",canBreak);

		Ctrl.setOutputText("Select objects to connect with bars.");
	}
	
	
	public void checkBoxChanged(int i,boolean state){
		if (i==0){
			canBreak = state;
			Ctrl.inputFields[1].setEditable(canBreak);
			//Ctrl.curHandler = new ModeAddBars();
		}
	}
	public void fieldInput(int id,double val){
		boolean worked = true;
		if (id==1){
			strength = val*VX.uPerNewton;
			//System.out.println(strength);
		} else {
			worked = false;
			return;
		}
		
		Ctrl.curHandler = new ModeAddBars();
	}
	
	public void mouseReleased(int mx,int my){
		//updateMousePos(mx,my);
		
		PhysicsObj[] items = getItemsInSelection(mx,my);
		if (items.length>0){
			System.out.println("Got some!");
			if (targA==null){
				targA = items[0];
			} else if (items[0]!=targA) {
				ConnectingBar newObj = new ConnectingBar(targA,items[0]);
				newObj.canBreak = canBreak;
				newObj.strength = strength;
				Ctrl.addObj(newObj);
				System.out.println("Added bar. Strength: " + strength);
				targA = null;
			}
			//ModeHandler.mouseMode = 0;
		}
		super.mouseReleased(mx,my);
	}
	
	
}

class ModeAddElastics extends AbstractAddConnectorsMode{
	public static double k=40.0 * VX.NperM;
	public static boolean useCurLen = true;
	public static double len=1;

	public ModeAddElastics(){
		super();
		
		Ctrl.setOutputText("Select objects to connect with elastics.");
		Ctrl.configMyField(1,"Spring Const","N/m",0.01,200,"" + k/VX.NperM);
		Ctrl.configMyCheckBox(0,"Use Dist","Set the current distance between the two objects as the default length",useCurLen);
		//System.out.println()
	}
	
	public void fieldInput(int id,double val){
		boolean worked = true;
		if (id==1){
			k = val*VX.NperM;
		} else {
			worked = false;
		}
		
		Ctrl.curHandler = new ModeAddElastics();
	}
	
	
	public void checkBoxChanged(int i,boolean state){
		if (i==0){
			useCurLen = state;
		}
	}
	
	public void mouseReleased(int mx,int my){
		//updateMousePos(mx,my);
		
		PhysicsObj[] items = getItemsInSelection(mx,my);
		if (items.length>0){
			System.out.println("Got some!");
			if (targA==null){
				targA = items[0];
			} else if (items[0]!=targA) {
				Ctrl.addObj(new TwoWaySpring(
					useCurLen ? targA.distTo(items[0]) : len,
					k,
					targA,
					items[0],
					1
				) );
				Ctrl.setOutputText("!!!");
				targA = null;
			}
		}
		
		super.mouseReleased(mx,my);
	}
}


class ModeInteractive extends ModeHandler{
	

	static int mode=1;//0=select obj  1= drag, 2=apply force
	final static int modeDrag=1,modeForce=2;
	
	static PhysicsObj targ=null;
	static double maxDragDist=10;
	
	static boolean holding=false;

	static double force=10.0*VX.uPerNewton;
	
	
	public ModeInteractive(){
		super();
		if (targ==null){
			ModeHandler.mouseMode = mouseDragBox;
		} else {
			ModeHandler.mouseMode = 0;
		}
		
		//Ctrl.resetAllFields();
		Ctrl.setOutputText("Interactive Mode initiated. \nSelect Object. \n" + mode);
		Ctrl.configMyField(6,"Force","Force to apply (N)",-200,200.0,""+force/VX.uPerNewton);
		
		
		Ctrl.configMyButton(0,"ChooseObject","Select an object to interact with");
		Ctrl.configMyButton(2,"Drag","");
		Ctrl.configMyButton(3,"Apply force","");
	}
	
	public void fieldInput(int id,double val){
		if (id==6){
			force = val*VX.uPerNewton;
			Ctrl.curHandler = new ModeInteractive();
		}
	}
	
	public void buttonPressed(int i){
		if (i==0){
			targ = null;
			ModeHandler.mouseMode = mouseDragBox;
		} else if (i==2){
			//ModeHandler.mouseMode = 0;
			Ctrl.setOutputText("Interaction mode to [Drag]");
			mode = modeDrag;
		} else if (i==3){
			Ctrl.setOutputText("Interaction mode to [Apply force]");
			//ModeHandler.mouseMode = 0;
			if (holding){
				removeHold();
			}
			mode = modeForce;
		}
	}
	
	public void mouseReleased(int mx,int my){
		//updateMousePos(mx,my);
		
		if (ModeHandler.mouseMode == mouseDragBox){
			PhysicsObj[] items = getItemsInSelection(mx,my);
			if (items.length>0){
				System.out.println("Got one!");
				targ = items[0];
				ModeHandler.mouseMode = 0;
			}
		}
		
		if (mode==modeDrag){
			if (holding){
				removeHold();
			}
		}
		
		super.mouseReleased(mx,my);
	}
	
	
	public void mousePressed(int mx,int my){
		if (mode==modeDrag && targ!=null){
			addHold();
		}
		super.mousePressed(mx,my);
	}
	
	public void addHold(){
		holding = true;
		targ.fixed ++;
	}
	
	public void removeHold(){
		holding = false;
		targ.fixed --;
	}
	
	public void dragTo(PhysicsObj targ,double x,double y){
		double curDist = targ.distTo(x,y);
		if (curDist<maxDragDist){
			targ.cx = x;
			targ.cy = y;
		} else {
			double dx = x-targ.cx, dy = y-targ.cy;
			targ.cx += dx*maxDragDist/curDist;
			targ.cy += dy*maxDragDist/curDist;
		}
	}
	
	public static void userInputApplyForce(double mx,double my,PhysicsObj obj,double vect){
		double curDist=PhysicsObj.pointDist(obj.cx,obj.cy,mx,my);
		//vect=0.4;//force to apply
		double ax=(mx-obj.cx)*vect/curDist;
		double ay=(my-obj.cy)*vect/curDist;
		obj.applyImpulse(ax,ay);
		//box2.applyImpulse(ax,ay);
	}
	
	public void updateTick(){
		if (mouseDown && mouseOnCanvas && targ!=null){
			if (mode==modeDrag){
				if (badStretch==false){
					dragTo(targ,mx,my);
				} else {
					badStretch = false;
				}
			} else if (mode==modeForce){
				userInputApplyForce(mx,my,targ,force);
			}
		}
	}
	
	public void drawExtra(Graphics2D g2){
		super.drawExtra(g2);
		if (targ!=null){
			targ.drawSelected(g2);
		}

	}
	public void close(){
		System.out.println("interact close");
		if (holding){
			removeHold();
		}
		targ = null;
	}

}