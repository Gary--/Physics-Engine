import java.io.Serializable;

class Vars implements Serializable{// Holds all the number variables for saving
	public Vars(){}

	//== PHYSICS OBJ
	public double uPerNewton, mPerSec2, mPerSec, NperM;
	
	public double xGrav=0,yGrav=0.00;// Gravity m/s^2
	public  double kF=0.00;//friction
	public  double kE=1000.0;//Electro static
	public  int doElectroStatic=0;
	public double time;
	
	// fake/real

	public double uPerSecond=80;
	
	
	public static double 
	uPerKilogram=1.0,
	// s^-1

	uPerMeter=100;//100 pixels is 1 m
	
	public void setUnits(double second){//units per second
		uPerSecond = second;
		
		uPerNewton = uPerKilogram*uPerMeter/uPerSecond/uPerSecond;
		mPerSec2 = uPerMeter/uPerSecond/uPerSecond;
		mPerSec = uPerMeter/uPerSecond;
		NperM = uPerNewton/uPerMeter;
	}
	
	
	//====
	int nObj;//number of objects alive
	
	public String message="";
}
