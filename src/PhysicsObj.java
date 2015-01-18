//Physics Engine


import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.ArrayList;
import java.io.Serializable;



class PhysicsObj implements Serializable{//base class for all objects in the simulation
	static Controller Ctrl;
	static Vars VX;//variable storage
	
	public static void setCtrl(Controller a){
		Ctrl = a;
	}
	

	
	public static final int 
		typeMass=1,
		typeStiffRod=2,
		typeSpring=3,
		typeRotMass=4,
	
	asdfasdfasdfasd=12313
	;
	
	

	
	

	// THIS IS HOW IT WORKS::::::: USER GIVEN * Var  ==  USED
	
	//  N = kg*m/s^2
	//public static double uPerNewton, mPerSec2, mPerSec, NperM;


	public static double eps=0.0001,
	INF = 9999999;
	
	//=== Graphics 
	public static int boxSize=3;
	
	
	//== OPtions
	public static final int TYPE_CONNECT = 1;
	public static final int TYPE_MASS = 2;
	
	
	public int classTYPE=0;
	
	//=== Obj Variables
	int arrayListPos;
	
	boolean active=true;
	boolean visible=true;
	
	double cx,cy,vx,vy;
	int ID;
	int TYPE;
	int ORD;//order to resolve updates in 
	int fixed=0;//cannot move
	double m;
	double charge=0.0;
	
	//double realM;
	
	public Color color;
	
	//==== LOAD SAVE
	public void load2(ArrayList<PhysicsObj> objs){load2();}//Called after object is loaded
	public void load2(){}
	
	public void preSave(ArrayList<PhysicsObj> objs){preSave();}//Called before object is written to file
	public void preSave(){}
	//======
	
	
	public void update(ArrayList<PhysicsObj> objs){update();}
	public void update(){}

	public void updateEnviro(ArrayList<PhysicsObj> objs){updateEnviro();}
	public void updateEnviro(){}
	
	
	//public void updateEnviro(){}//friction, grav
	
	
	
	public void draw(Graphics2D g2){
		System.out.println("WARNING: Abstract all to draw.");
	}
	
	public void endCycle(ArrayList<PhysicsObj> objs){
		endCycle();
	}
	
	public void endCycle(){}
	
	public void drawSelected(Graphics2D g2){
		System.out.println("WARNING: Abstract all to drawSelected.");
	}
	
	public void applyImpulse(double Px,double Py){}
	
	//public void applyFriction(){}
	
	public double distTo(PhysicsObj other){
		return pointDist(cx,cy,other.cx,other.cy);
	}
	public double distTo(double x,double y){
		return pointDist(cx,cy,x,y);
	}
	
	public PhysicsObj isInRect(int x1,int y1,int x2,int y2){
		//System.out.println("Abstract call to isInRect");
		return null;
	}
	
	
	// == = == = = = public functions
	//point and rect boundaries
	public static boolean isInRect(double px, double py,int x1,int y1,int x2,int y2){
		if (x1>x2){x1^=x2;x2^=x1;x1^=x2;}
		if (y1>y2){y1^=y2;y2^=y1;y1^=y2;}
		
		return (px<=x2 && x1<=px && py<=y2 && y1<=py);
	}
	
	
	public static double reduceComp(
			double dist,//actual len of vector
			double abs,//correct len of vector
			double x){//actual len of comp
		return x*abs/dist;
	}
	
	public static int sgn(double x){
		return (x<0) ? (-1) : 1;
	}
	
	public static double pointDist(double x1,double y1,double x2,double y2){
		return Math.sqrt((x1-x2)*(x1-x2) +  (y1-y2)*(y1-y2));
	}
	
	public static double myAtan2(double x,double y){
		return -Math.atan2(x,y) + Math.toRadians(90.0);
	}
	
	public static double triArea(double x1,double y1, double x2,double y2,double x3,double y3){
		return (x1*y2 + x2*y3 + x3*y1 - y1*x2 - y2*x3 - y3*x1)/2.0;
	}
	
	public static boolean isAcuteTri(double x1,double y1,double x2,double y2,double x3,double y3){
		//a,b,c
		double a = pointDist(x1,y1,x2,y2),
		b = pointDist(x3,y3,x2,y2),
		c = pointDist(x3,y3,x1,y1);
		
		double lim = Math.PI/2.0;//90 degs
		
		return cosLawAng(a,b,c)<lim && cosLawAng(b,c,a)<lim && cosLawAng(c,a,b)<lim;
		
	}
	
	
	public static double cosLawAng(double a,double b,double c){//angle opposite of c
		return Math.acos( (a*a + b*b - c*c ) / 2.0 / a / b );
	}
	
	public static void drawLine(Graphics2D g2,PhysicsObj a,
	PhysicsObj b,int thickness,Color color){
		g2.setColor(color);
		g2.setStroke(new BasicStroke(thickness));
		
		g2.drawLine((int)Math.round(a.cx),(int)Math.round(a.cy),(int)Math.round(b.cx),(int)Math.round(b.cy));
		
		
	}
}


class FreeMass extends PhysicsObj{//moving mass
	int classTYPE = TYPE_MASS;

	public FreeMass(){
		this(1,0,0,0,0);
	}
	
	public FreeMass(double m,double cx,double cy){
		this(m,cx,cy,0,0);
	}
	
	public FreeMass(double m,double cx,double cy,double vx,double vy){
		TYPE = 1;
		ORD = 7;
		this.m = m;
		this.cx = cx;
		this.cy = cy;
		this.vx = vx;
		this.vy = vy;
	}
	
	
	
	public void draw(Graphics2D g2){
		if (fixed==0){
			g2.setColor(Color.blue);
		} else {
			g2.setColor(Color.red);
		}
		g2.fillRect((int)Math.round(cx-boxSize),(int)Math.round(cy-boxSize),Math.round(boxSize*2),Math.round(boxSize*2));
		
	}
	
	public void drawSelected(Graphics2D g2){
		g2.setColor(Color.green);
		g2.setStroke(new BasicStroke(2));
		g2.drawRect((int)(cx-boxSize),(int)(cy-boxSize),(int)boxSize*2,(int)boxSize*2);
		g2.setStroke(new BasicStroke(1));
	}
	
	public void applyImpulse(double Px,double Py){
		vx += Px/m;
		vy += Py/m;
		if (fixed>0){
			vx = vy = 0;
		}
		
	}
	
	public PhysicsObj isInRect(int x1,int y1,int x2,int y2){
		return isInRect(cx,cy, x1, y1, x2, y2) ? this : null;
	}
	
	public void applyFriction(){
		double Px=vx*m;
		if (Math.abs(Px)<VX.kF*m){
			vx = 0;
		} else {
			Px += -(VX.kF*m)*sgn(Px);
			vx = Px/m;
		}
		
		double Py=vy*m;
		if (Math.abs(Py)<VX.kF*m){
			vy = 0;
		} else {
			Py += -(VX.kF*m)*sgn(Py);
			vy = Py/m;
		}
		
		if (fixed>0){
			vx = vy = 0;
		}
	}
	
	public void applyGrav(){
		//if (!fixed){//is movable
		vx += VX.xGrav;
		vy += VX.yGrav;
		if (fixed>0){
			vx = vy = 0;
		}
		//}
	}
	
	public void applyFrictionOLDWRONG(){
		applyFriction();
		if (true ) return;
		double P = VX.kF*m;
		double V = pointDist(0.0,0.0,vx,vy);
		
		
		
		if (V<eps){
			vx = vy = 0;
		} else {
			P = Math.min(P,V*m);
			
			
			double Px = - vx*P/V;
			double Py = - vy*P/V;
			
			vx += Px/m;
			vy += Py/m;
		}
		
		if (fixed>0){
			vx = vy = 0;
		}


		
	}
	public void updateEnviro(){
		applyGrav();
		applyFriction();
		if (fixed>0){
			vx = vy = 0;
		}
	}
	
	public void updateEnviro(ArrayList<PhysicsObj> objs){
		updateEnviro();
		updateElectroStatic(objs);

	}
	
	public void applyImpulseToward(double F,double x,double y){
		double dist=this.distTo(x,y);
		double dx=(x - cx)/dist, dy =(y - cy)/dist;//unit vectors
		this.applyImpulse(dx*F,dy*F);
		
	}
	
	public void applyImpulseToward(double F,PhysicsObj other){
		this.applyImpulseToward(F,other.cx,other.cy);
	}
	
	public void updateElectroStatic(ArrayList<PhysicsObj> objs){
	//System.out.println("HERE " + kE + " " + charge );
		if ( VX.kE<eps || Math.abs(charge)<eps){return;}//not enabled, or no charge
		
		for (int i=0;i<objs.size();++i){
			if (objs.get(i).TYPE==1 && objs.get(i)!=this){
				
				double dist=this.distTo(objs.get(i));
				if (dist<eps){continue;}
				
				
				
				double F=-charge*objs.get(i).charge*VX.kE / dist/dist;
				this.applyImpulseToward(F,objs.get(i));
				((FreeMass)objs.get(i)).applyImpulseToward(F,this);
				//System.out.println("Force: " + F);
			}
		}
	}
	
	public void update(){
		//System.out.println("Mass update!");
		cx += vx;
		cy += vy;
	}
	//public FreeMass
}



class LimiterTrack extends PhysicsObj{
	public static double far=999999.0;

	PhysicsObj targ;//mass on the track
	double x1,y1,x2,y2,farX,farY,len;//coordinates of start and end points
	ConnectingBar longBar,bar1,bar2;
	FreeMass endMass1,endMass2,farMass;
	
	public LimiterTrack(PhysicsObj targ,double x1,double y1,double x2,double y2 ){
		this.targ = targ;
		this.x1 = x1;
		this.y1 = y1;
		this.y2 = y2;
		this.x2 = x2;
		this.len = pointDist(x1,y1,x2,y2);
		ORD = 2;
		setup();
	}
	
	public void setup(){
		//System.out.println(" " + x1 + " " + y1 + " "+ x2 + " " + y2);
	
		ORD = 2;
		//len = pointDist(x1,y1,x2,y2);
		
		double midX = (x2+x1)/2, midY = (y2+y1)/2;
		double ang = -Math.atan2(x2-x1,y2-y1);
		// ang = Math.atan2(x2-x1,y2-y1);
		farX = midX + Math.cos(ang)*far;
		farY = midY + Math.sin(ang)*far;
		
		endMass1 = new FreeMass(1.0,x1,y1);
		endMass2 = new FreeMass(1.0,x2,y2);
		farMass = new FreeMass(1.0,farX,farY);
		endMass1.fixed = endMass2.fixed = farMass.fixed = 1;
		
		bar1 = new ConnectingBar(targ, endMass1,2,len);
		bar2 = new ConnectingBar(targ, endMass2,2,len);
		longBar = new ConnectingBar(targ, farMass,1,far);
		
	}
	
	public  LimiterTrack(PhysicsObj targ,double x1,double y1,double len){//extends len dist toward x2,y2
		double dx = targ.cx-x1,
		dy = targ.cy-y1;
		
		double curDist = pointDist(dx,dy,0.0,0.0);
		
		dx *= len/curDist;
		dy *= len/curDist;
		
		this.len = len;
		this.targ = targ;
		this.x1 = x1;
		this.y1 = y1;
		
		this.y2 = y1+dy;
		this.x2 = x1+dx;
		
		setup();
	}
	
	public void draw(Graphics2D g2){
		g2.setColor(Color.blue);
		g2.setStroke(new BasicStroke(3));
		
		g2.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
		g2.setStroke(new BasicStroke(1));
		
		//bar1.draw(g2);
		//bar2.draw(g2);
		//longBar.draw(g2);
		
	}
	
	public void update(){
		bar1.update();
		bar2.update();
		longBar.update();
	}
}

class Connector extends PhysicsObj{
	int classTYPE=TYPE_CONNECT;

	public static Color colors[] = {Color.red,Color.white,Color.blue};
	PhysicsObj a,b;
	int indA,indB;
	int ConnectorType;
	
	public void fixedToA(){//swap the fixed object to first
		if (b.fixed>0){
			PhysicsObj temp = this.b;
			this.b = this.a;
			this.a = temp;
		}
	}
	
	public void preSave(){
		indA = a.arrayListPos;
		indB = b.arrayListPos;
	}
	
	public Connector(){
		classTYPE=TYPE_CONNECT;
	}
	
	public static void deactivateConnectors(PhysicsObj a,PhysicsObj b, ArrayList<PhysicsObj> objs){
		for (int i=0;i<objs.size();++i){
			//System.out.println("here " + objs.get(i).TYPE);
			
			if (objs.get(i).TYPE>=2 && objs.get(i).TYPE<=4){
				
				Connector cur = (Connector)objs.get(i);
				if ((cur.a==a && cur.b==b)||(cur.a==b && cur.b==a)){
					cur.active = false;
				}
			}
		}
	}
	
	public void draw(Graphics2D g2){
		//if (doApplyForce()){
			//if (stretch()>0){
				//g2.setColor(Color.red);
			//} else {
				//g2.setColor(Color.green);
			//}
			 
		//} else {
			//g2.setColor(Color.yellow);
		//}
		
		
		//g2.setColor(Color.red);
		

		int thick=0;
		if (ConnectorType==0 || ConnectorType==1){//Elastic or bar
			thick = 2;
			//g2.setStroke(new BasicStroke(2));
			
		} else {
			//g2.setStroke(new BasicStroke(1));
			thick = 1;
		}

		drawLine(g2,a,b,thick,colors[ConnectorType]);
		//g2.drawLine((int)Math.round(a.cx+dx),(int)Math.round(a.cy+dy),(int)Math.round(b.cx+dx),(int)Math.round(b.cy+dy));
		//System.out.println("draw " + len + " " +a.cx + " " + a.cy + " "  + b.cx + " " + b.cy);
	}
}





class TwoWaySpring extends  Connector{
	public static  Color color = Color.yellow;
	
	double len, 
	k;//spring constant N/m
	
	int type;//1 is two way, 2 is pull on stretch, 3 is push on compress
	

	
	public TwoWaySpring(){}
	public TwoWaySpring(double len,double k,PhysicsObj a,PhysicsObj b,int type){
		super();
		ConnectorType = 0;
		
		TYPE = 3;
		ORD = 1;
		this.len = len;
		assert k>=0;
		this.k = k;
		this.a = a;
		
		this.b = b;
		this.type = type;
		//this.a.cy = 200;
		//System.out.println("start " +a.cx + " " + a.cy + " "  + b.cx + " " + b.cy);
		
	}
	

	
	public double stretch(){//+ is stretch, - is compression
		return pointDist(a.cx, a.cy, b.cx, b.cy)-len;
	}
	
	public boolean doApplyForce(){
		double P=stretch();
		return (type==1 || (P>0 && type==2) || (P<0 &&type==3));
	}
	
	public void update(){

		//double dist=stretch();
		double P = stretch()*k;

		double Px = (b.cx-a.cx)*P/pointDist(a.cx, a.cy, b.cx, b.cy),
			Py = (b.cy-a.cy)*P/pointDist(a.cx, a.cy, b.cx, b.cy);
		//System.out.println("Mom: " + Px + " " + Py);
		if (doApplyForce()){
		
			b.applyImpulse(-Px,-Py);
			a.applyImpulse(Px,Py);
		}
		//double 
		
	}
} 


class ConnectingBar extends TwoWaySpring{
	public static Color color =  Color.white;
	public static double err=0.01;
	public static double stressConst = 1000;
	
	static double totalM;
	double curStress=0.0, lastStress=0.0;
	static int stressMode=1;
	
	
	static final double maxDeadDrawDist=100.0;
	
	
	boolean canBreak=false;
	double strength=9999999.0;
	
	
	
	public ConnectingBar(){};
	
	public ConnectingBar(PhysicsObj a,PhysicsObj b){
		this(a,b,1,0.0);//default is actual bar
	}

	
	public ConnectingBar(PhysicsObj a,PhysicsObj b,int type,double len){
		super();
		ConnectorType = 1;
	
		//======== DO NOT SWAP IN CONSTRUCTOR! Wall depends on it!
		TYPE = 2;
		ORD = 2;
		this.type = type;
		
		this.a = a;
		this.b = b;
		this.len = len;
		curStress = lastStress = 0.0;

		
		if (len==0){
			this.len = a.distTo(b);
		}
		
		this.totalM = a.m + b.m;
	}
	
	public static int limit(double val){
		return Math.min(Math.max((int)val,0),255);
	}
	
	
	
	public void draw(Graphics2D g2){
	
	
		if (active){
		
			if (stressMode==0){//Regular display
				super.draw(g2);
			} else {
				//System.out.println(lastStress + " " + stressConst);
				drawLine(g2,a,b,2,getColor((lastStress*stressConst)));
				//Color orig = colors[ConnectorType];
				//colors[ConnectorType] = ;
				//super.draw(g2);
				//colors[ConnectorType] = orig;
				
				//Start green
				//Add red until yellow
				//Add more red for orange
				//Decrease green for red
			}
		} else {
			if (a.distTo(b)<maxDeadDrawDist+len){
				super.draw(g2);
				
			}
		}
		
		lastStress = 0.0;
	}
	
	public static Color getColor(double val2){//input value [0<= x <=382]
		//R G B
		int val = Math.min(Math.max((int)val2,0),382);
		if (val<=128){
			return new Color(val,255-val,0);
		} else if (val<255){
			return new Color(val,128,0);
		} else {
			//val = Math.min(val,382);
			return new Color(255,382-val,0);
		}
	}
	
	public void update(ArrayList<PhysicsObj> objs){
		if (!active){
			return;
		}
		//curStress = 0.0;
		
		
		//System.out.println("Stress: " + curStress);
		fixedToA();
		
		double nx1= a.cx + a.vx,
		ny1=a.cy+a.vy,
		nx2= b.cx + b.vx,
		ny2=b.cy+b.vy;
		
		double dx1 = nx2 - nx1 ,
		dy1 = ny2 - ny1 ;

		
		double curDist = pointDist(0.0,0.0,dx1,dy1);
		double distDiff = curDist-len;

		if (  ((distDiff>err)&&type==2) || (Math.abs(distDiff)>err && type==1) ){ //pull only, stiff stick
			if (Math.abs(distDiff)>20){
				Ctrl.curHandler.badStretch = true;
			}
			
			
			double stillDist = a.distTo(b);//without velocities
			double ex=100;
			double dcx=(b.cx-a.cx)*ex/stillDist,
			dcy=(b.cy-a.cy) *ex/stillDist ;
			
			double x2=nx2 - nx1 ,y2 = ny2 - ny1;
			double x1=x2-  dcx  , y1=y2-dcy ;
			
			//double (b.cx)
			

			double r = len;
			//x1 = x2-dcx,
			//y1 = y2-dcy;
			
			
			double D = x1*y2 - x2*y1;
			double dx = x2 - x1;
			double dy = y2 - y1;
			double dr = Math.sqrt(dx*dx+dy*dy);
			//System.out.println(r*r*dr*dr - D*D);
			double disc = r*r*dr*dr - D*D;//discriminent
			//System.out.println(" " + x1 + " " + y1 + " " + x2 + " " + y2);
			if (disc<0){
				//a.fixed = b.fixed = true;
				//a.vx = a.vy = b.vx = b.vy = 0;
				System.out.println("WARN: Bar discriminant <0! ");
				
				//System.out.println((y2-y1)/(x2-x1));
				return;
			}
			disc = Math.sqrt(disc);
			//System.out.println(disc);
			double bestDist = INF, bestx = 0, besty = 0;
			for (int i=-1;i<=1;i+=2){//i = (+/-) 1
				double x= (D * dy + i*sgn(dy)*dx*disc) / ( dr*dr );
				double y =(-D*dx + i*Math.abs(dy) *disc ) / ( dr*dr );
				
				double thisDist = pointDist(x,y,nx2-nx1,ny2-ny1);
				
				if (thisDist< bestDist){
					bestDist = thisDist;
					bestx = x;
					besty = y;
				}
			}
			
			//how much to change the velocity by (applyed to b)
			double tx = nx2-(bestx + nx1),
			ty = ny2-(besty+ny1);
			
			double toChange = pointDist(0.0,0.0,tx,ty);// m
			curStress += toChange;
			
			
			
			double P;
			if (a.fixed==0){

				//double P=toChange/(1/a.m + 1/b.m); // mass / impulse = delta v
				P=1.0/(1/a.m + 1/b.m);

			} else {
				P=1.0/(1/b.m); // mass / impulse = delta v

			}
			

			
			double Px = tx*P ;
			double Py = ty*P ;
			
			double fullP = pointDist(0.0,0.0,Px,Py);
			//System.out.println(fullP/uPerNewton + " " + strength/uPerNewton);
			
			
			if (canBreak && fullP>strength){
				System.out.println("Break!" + fullP);
				Px *= strength/fullP;
				Py *= strength/fullP;
				deactivateConnectors(a,b,objs);
				//active = false;
			}
			//System.out.println(fullP/uPerNewton);
			
			
			b.applyImpulse(-Px,-Py);
			a.applyImpulse(Px,Py);
			
		}
	}
	
	public void applyImpulse(double Px,double Py,double cx,double cy){
		if ( Math.abs( a.distTo(cx,cy) + b.distTo(cx,cy) - len ) <eps ){
			double distA = a.distTo(cx,cy);
			applyImpulse(distA,Px,Py);
			
		} else {
			System.out.println("Bad bar apply force!");
		}
	}
	
	public void applyImpulse(double distA,double Px,double Py){
		double ratio = distA/len;
		b.applyImpulse(Px*ratio, Py*ratio);
		ratio = 1 - ratio;
		a.applyImpulse(Px*ratio, Py*ratio);
	}
	
	public void endCycle(){
		//System.out.println("Last at end: " + lastStress);
		lastStress = Math.max(lastStress,curStress);
		curStress = 0.0;
	}
}


class Wall extends TwoWaySpring {
	//public Line2D line;
	static int count = 0;
	static double close=0.5;
	static int didHit=0;
	
	
	public Wall(PhysicsObj a,PhysicsObj b){
		ORD = 5;
		this.a = a;
		this.b = b;
		TYPE = 4;
		ConnectorType = 2;
	}
	
	public void draw(Graphics2D g2){
		if (active){
			super.draw(g2);
		}
		
	}
	
	public double acute(double ang){//acute angle between wall angle and vector, given actual difference
		while (ang<0.0){
			ang += Math.PI*2;
		}
		
		while (ang>Math.PI/2){
			ang -= Math.PI/2;
		}
		
		return ang;
	}	
	
	public double piIt(double ang){
		while (ang<0.0){
			ang += Math.PI*2;
		}
		while (ang>Math.PI){
			ang -= Math.PI;
		}
		return ang;
	}
	
	public double acuteDiff(double a, double b){
		a = reduce(a);
		b = reduce(b);
	
		if (a>b){//b>a always
			double t=b;
			b = a;
			a = t;
		}
		
		double diff=b-a;
		if (diff>Math.PI){
			diff -= Math.PI;
		}
		
		if (diff>Math.PI/2){
			return Math.PI - diff;
		} else {
			return diff;
		}
	}
	
	public static double reduce(double ang){
		if (ang<0.0){
			ang += Math.PI*2;
		}
		
		if (ang>Math.PI*2){
			ang -= Math.PI*2;
		}
		
		return ang;
	}
	
	public boolean willHit(PhysicsObj mass){
		//=== Add edges a bit
		double curAng = PhysicsObj.myAtan2(b.cx-a.cx,b.cy-a.cy);
		
		double nx1 = a.cx + a.vx,
		ny1 = a.cy + a.vy,
		nx2 = b.cx + b.vx,
		ny2 = b.cy + b.vy   ;
		
		int curSgn = sgn( triArea(a.cx,a.cy,b.cx,b.cy,mass.cx,mass.cy) );
		
		//double nextDistToLine = Line2D.ptLineDist(nx1,ny1,nx2,ny2,mass.cx+mass.vx,mass.cy+mass.vy);
		int nextSgn = sgn( triArea(nx1,ny1,nx2,ny2,mass.cx+mass.vx,mass.cy+mass.vy) );
		
		double da = pointDist(mass.cx, mass.cy, a.cx, a.cy);
		double db = pointDist(mass.cx, mass.cy, b.cx, b.cy);
		double dc = pointDist(b.cx, b.cy, a.cx, a.cy);
		
		boolean isAcute = cosLawAng(da,dc,db )<(Math.PI/2+0.03) && cosLawAng(dc,db,da )<(Math.PI/2+0.03);
		
		da = pointDist(mass.cx + mass.vx, mass.cy + mass.vy, nx1, ny1);
		db = pointDist(mass.cx + mass.vx, mass.cy + mass.vy, nx2, ny2);
		dc = pointDist(nx2, ny2, nx1, ny1);
		
		boolean isAcute2 = cosLawAng(da,dc,db )<(Math.PI/2+0.03) && cosLawAng(dc,db,da )<(Math.PI/2+0.03);
		
		return (curSgn!=nextSgn) && (isAcute||isAcute2);
	}
	
	public void applyHit(PhysicsObj mass,ArrayList<PhysicsObj> objs){// confirm hit and do it
		
		int curSgn = sgn( triArea(a.cx,a.cy,b.cx,b.cy,mass.cx,mass.cy) );
		
		if (this.willHit(mass)){//will cross it during this time interval
			//System.out.println("Wall pass " + (count++));
			
			didHit += 1;
			double wLen = a.distTo(b);
			//System.out.println("Cross! " + (count++));
			
			double minT = 0.0, maxT = 1.0;
			
			//condi: (Math.abs(minT-maxT)>(eps*10)
			for (int i=0; i<15;++i){//binary search to estimate the time of collision in between intervals
				double midT = (minT+maxT)/2;
				int tSgn = sgn( 
				triArea(
					a.cx + a.vx*midT ,  a.cy + a.vy*midT ,
					b.cx + b.vx*midT ,  b.cy + b.vy*midT ,
					mass.cx + mass.vx*midT ,  mass.cy + mass.vy*midT
				
				));
				
				if (tSgn==curSgn){
					minT = midT;
				} else {
					maxT = midT;
				}
			}
			

			
			
			double midT = (minT+maxT)/2;
			
			//== EXPERIMENTAL SET LOC
			//mass.cx += mass.vx*(midT-eps*100);
			//mass.cy += mass.vy*(midT-eps*100);
			
			//System.out.println((minT+maxT)/2);
			double hx = mass.cx + mass.vx*midT,
			hy = mass.cy + mass.vy*midT;//point of impact
			

			
			//Where the points of the ends of the wall are at the time of collision
			double tx1 = a.cx + a.vx*midT,
			ty1 = a.cy + a.vy*midT,
			tx2 = b.cx + b.vx*midT,
			ty2 = b.cy + b.vy*midT;
			
			double distToA = pointDist(tx1,ty1,hx,hy);//Distance of collision point to 'a' at instance of contact
			double ratio = distToA/pointDist(tx1,ty1,tx2,ty2);//how far along the wall it is
			
			//Adjust ratio for end points moving apart slightly
			
			//=== WRONG!
			double ox = a.cx + (b.cx - a.cx)*ratio,
			oy = a.cy + (b.cy - a.cy)*ratio;
			
			//Velocity of the point of collision on the wall
			double wvx = (hx-ox)/midT,
			wvy = (hy-oy)/midT;
			
			// velocity of the mass in relation to the point on the wall it hits
			double mvx = mass.vx -  wvx,
			mvy = mass.vy -  wvy;
			
			double vAng = reduce(myAtan2(mvx,mvy));
			double wAng = reduce(myAtan2(tx2-tx1,ty2-ty1));
			//System.out.println("Time: " + midT);
			//System.out.println("" + hx + " " + hy + " " + ox + " " + oy);
			double curAng = acuteDiff(wAng, vAng);
			//System.out.println(Math.toDegrees(curAng));
			double velToKill = Math.sin(curAng) * pointDist(0.0,0.0,mvx,mvy);
			//System.out.println();
			
			//angles perpendicular to the wall (direction that force can be applied in)
			double pos1 = reduce(wAng + Math.PI/2),
			pos2 = reduce(wAng - Math.PI/2);
			
			double angToApply = 999;
			if ( 
			pointDist(0.0, 0.0, mvx + velToKill*Math.cos(pos1), mvy + velToKill*Math.sin(pos1)) < 
			pointDist(0.0, 0.0, mvx + velToKill*Math.cos(pos2), mvy + velToKill*Math.sin(pos2))
			){
				angToApply = pos1;
			} else {
				angToApply = pos2;
			}
			
			//double Px = mass.m * velToKill * Math.cos(angToApply);
			//double Py = mass.m * velToKill * Math.sin(angToApply);
			
			
			//=-=== Binary search for amount of impulse to apply <--- Sketch!
			//mass.applyImpulse(Px,Py);
			double mult=1.0;
			if (b.fixed==0 && false){//at least one end is loose
				double em;
				
				if (a.fixed==0){
					em = ratio*b.m + (1-ratio)*a.m;// effective mass
				} else {
					em = b.m * (wLen/ distToA);
					//em = ratio*b.m + (1-ratio)*100.0;
				}
				mult = 1.0/(1/em + 1/mass.m);
				
				
			}
			ConnectingBar tempBar = new ConnectingBar(a,b);
			double curP = velToKill*mass.m*mult*1.00;
			curP = 1.0;
			//double Px,Py;
			
			//MAKE THIS NOT FOREVER

			for (int i=0;true;++i){//Double until won't hit
				double Px = curP*Math.cos(angToApply),
				Py = curP*Math.sin(angToApply);
				
				mass.applyImpulse(Px,Py);
				tempBar.applyImpulse(distToA,-Px,-Py);
				
				boolean works=false;
				
				if (!this.willHit(mass)){
					works = true;
				}
				
				mass.applyImpulse(-Px,-Py);
				tempBar.applyImpulse(distToA,Px,Py);
				
				
				if (works){
					break;
				}
				curP *= 1.7;
				
				if (i==20){
					System.out.println("Wall cannot apply enough force!");
					deactivateConnectors(a,b, objs);
					return;
				}
			}
			
			
			double toChange=curP/2;
			int times=20;
			for (int i=0;i<times;++i){//binary search exact force needed
				double nP = curP-toChange;
				
				double Px = nP*Math.cos(angToApply),
				Py = nP*Math.sin(angToApply);
				
				mass.applyImpulse(Px,Py);
				tempBar.applyImpulse(distToA,-Px,-Py);
				
				//boolean works=false;
				
				if (!this.willHit(mass)){
					curP -= toChange;
					//works = true;
				}
				

				mass.applyImpulse(-Px,-Py);
				tempBar.applyImpulse(distToA,Px,Py);
				
				toChange /= 2;
			}
			
			//=== FINAL APPLICATION OF FORCE
			curP += eps;
			double Px = curP*Math.cos(angToApply),
			Py = curP*Math.sin(angToApply);
			mass.applyImpulse(Px,Py);
			tempBar.applyImpulse(distToA,-Px,-Py);
			
			
			//ConnectingBar tempBar = new ConnectingBar(a,b);
			//tempBar.applyImpulse(distToA,-Px,-Py);
		}
	}
	
	public void update( ArrayList<PhysicsObj> objs){
		fixedToA();
		//double nx1 = a.cx + a.vx,
		//ny1 = a.cy + a.vy,
		//nx2 = b.cx + b.vx,
		//ny2 = b.cy + b.vy;

		//double wallDist = a.distTo(b);
		//double wallAng = reduce(myAtan2(nx2-nx1,ny2-ny1));
		//double pos1 = reduce(wallAng + Math.PI/2),
		//pos2 = reduce(wallAng - Math.PI/2);
		//System.out.println("Wall call");

		
		for (int i=0;i<objs.size();++i){
			if (objs.get(i).TYPE==1 && objs.get(i)!=a && objs.get(i)!=b){//is Free Mass
				applyHit(objs.get(i),objs) ;
			}
		}
		
		
	}
}


/*
//Check rotations
class RotationObj extends FreeMass{
	double ox,oy,
	ang,w,rm;
	
	double fric;
	//rm is intertia: kg*m*m
	
	//double total
	
	double totalX,totalY,totalM,totalR;//kg*m
	int nObj;//number of attached objects
	
	RotMass masses[];
	
	
	
	public RotationObj(double x,double y){
		this();
		this.cx = x;
		this.cy = y;
	}
	
	public RotationObj(){
		ORD = 7;
		ox=oy=cx=cy=vx=vy=ang=w=rm=m=totalX=totalY=fric=0;
		nObj = 0;
		masses = new RotMass[20];
	}
	
	public void addMass(RotMass obj){
		totalM += obj.m;
		totalX += obj.m * obj.ox;
		totalY += obj.m * obj.oy;
		
		//nObj += 1;
		obj.parent = this;
		obj.parentInd = nObj;
		masses[nObj++] = obj;
		
	
	}
	
	public void prepare(){//set variables for each RotMass
		rm = 0;
		m = totalM;
		fric = 0;
		for (int i=0;i<nObj;++i){
			masses[i].x = totalX/totalM - masses[i].ox;
			masses[i].y = totalY/totalM - masses[i].oy;
			
			masses[i].ang = Math.atan2(masses[i].x,masses[i].y);
			
			masses[i].dist = pointDist(0,0,masses[i].x,masses[i].y);
			totalR += masses[i].dist;
			
			masses[i].update();
			rm += masses[i].m*masses[i].dist*masses[i].dist;
			//masses[i].realM = totalM;
			
			fric += masses[i].m*masses[i].dist;
			
			//masses[i].m = totalM;//====== SET MASS OF ROTOBJ = TOTAL
		}
		ang = Math.PI/2;
		updateMasses();
		//fric *= VX.kF;
		
	}
	
	public void draw(Graphics2D g2){
		//Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect((int)(cx-boxSize),(int)(cy-boxSize),(int)boxSize*2,(int)boxSize*2);
		
		g2.setColor(Color.blue);
		
		for (int i=0;i<nObj;++i){
			double x=this.massX(i),
			y=this.massY(i);
			
			//g2.fillRect((int)(x-10),(int)(y-10),(int)20,(int)20);
			
			masses[i].draw(g2);
			
			g2.setColor(Color.blue);
			g2.drawLine((int)cx,(int)cy,(int)x,(int)y);
			//System.out.println(masses[i].dist);
		}
	}
	
	public double massX(int i){
		return cx+masses[i].dist*Math.cos(ang+masses[i].ang);
	}
	
	public double massY(int i){
		return cy+masses[i].dist*Math.sin(ang+masses[i].ang);
	}
	
	
	//improve this crap..
	public void applyGrav(){
		for (int i=0;i<nObj;++i){
			masses[i].applyImpulse( VX.xGrav*masses[i].m, VX.yGrav*masses[i].m );
		}
	}
	


	
	public void updateMasses(){
		for (int i=0;i<nObj;++i){
			masses[i].update();
		}
	}
	
	public void applyFriction2(){
		applyFriction();
		
		double P=w*totalR;//total momentum
		if (Math.abs(P)<VX.kF){
			w = 0;
		} else {
			if (P<0){
				P += VX.kF;
			} else {
				P -= VX.kF;
			}
			w = P/totalR;
		}
		updateMasses();

	}
	
	public void updateEnviro(){
		applyGrav();
		//applyFriction3(); //slower, right way i think
		applyFriction2(); //faster, prolly wrong
	}
	
	public void update(){

		ang += w;
		//ang %= Math.PI/2;
		
		cx += vx;
		cy += vy;

		updateMasses();

	}
	
	public static double turn(double x1,double y1,double x2,double y2,double x3,double y3){
		return x1*y2+x2*y3+x3*y1 - y1*x2 - y2*x3-y3*x1;
	}
	
	public void applyImpulse(double x,double y,double Px,double Py){
		//x,y where impulse applied on plane
		//System.out.println();
		vx += Px/m;
		vy += Py/m;
		if (fixed>0){
			vx = vy = 0;
		}
		
		double dx=x-cx, dy=y-cy;
		double dotProd=dx*Px + dy*Py;
		
		double perpVect=dotProd;
		
		double absP = pointDist(0,0,Px,Py),
		absD = pointDist(0,0,dx,dy);
		if (absD<eps  ){//null value
			System.out.println("Warning: Force applied to center or no force");
			return;
		} else if (absP<eps) {// zero impulse applied
			return ;
		}
		
		double angle=dotProd/absP/absD;
		//System.out.println(angle);
		//System.out.println("V: " + absD + "  " + absP);
		angle = Math.min(Math.max(angle,-1.0),1.0);//kill decimal errors
		angle = Math.acos(angle);
		

		if (turn(cx,cy,x,y,x+Px,y+Py)<0){//find which side force is applied to to find direction of rotation
			angle *= -1;
		}
		
		//System.out.println((Math.sin(angle))/rm*pointDist(0,0,Px,Py));
		
		double dw = (Math.sin(angle))/rm*absP*absD;

		w += dw;
		
	}
}


class RotMass extends FreeMass{//a part of a RotationObj
	double rm,
	ox,oy,//from rotational center
	x,y;

	//double realM;
	
	double ang,dist;//from center of mass
	RotationObj parent;
	int parentInd;
	
	public RotMass(){
		this(0,0,0);
	}
	
	public RotMass(double m,double x,double y){//x,y are distance relative to orgin of rotationObj
		this.m = m;
		this.TYPE = typeRotMass;
		ox=x;oy=y;
		ORD = 5;
	}
	
	public static RotMass RotMass(double m,double x,double y){
		RotMass temp = new RotMass(m,x,y);
		return temp;
	}
	
	//needed because each RotMass does not store the parent's center of mass location
	public double getX(){
		return parent.massX(parentInd);
	}
	public double getY(){
		return parent.massY(parentInd);
	}
	
	
	public void update(){

	
		vx = parent.vx + ( Math.cos(ang+parent.ang+parent.w)-Math.cos(ang+parent.ang) )*dist;
		vy = parent.vy + ( Math.sin(ang+parent.ang+parent.w)-Math.sin(ang+parent.ang) )*dist;

		cx = this.getX();
		cy = this.getY();
	}
	
	public void applyImpulse(double Px,double Py){

		parent.applyImpulse(this.getX(),this.getY(),Px,Py);
		
		//vx = parent.vx + Math.cos(parent.ang + this.ang) * dist;
		//vy = parent.vy + Math.sin(parent.ang + this.ang) * dist;
	
	}
	
	
}
*/
