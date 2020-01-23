package gameClient;
import dataStructure.DEdge;
import utils.Point3D;
/**
 * 
 *The class represent a fruit object that have five fildes:

private double value;//the fruit value
private int type;//if the type value is -1=the fruit is banana if the type value is 1=the fruit is apple
private Point3D pos;//the fruit location
private DEdge edge;//the edge that the fruit on
private boolean visit;//true if there a robor on his way to eat him,false otherwise.
 *
 */
//****fields*****
public class Fruit {
	private double value;
	private int type;
	private Point3D pos;
	private DEdge edge;
	private boolean visit;
	
	
	//******constructor**********
	public Fruit (double value, int type, Point3D pos)  {
		this.value=value;
		this.type=type;
		this.pos=pos;
		this.edge=null;
	}
	
	//**functions**
	public String toString () {
		return "value "+value+" type "+type+" pos "+pos+" edge "+edge;
	}
	
	//**getters & setters**
	
	//This func sets the fruit edge
	public void setEdge (DEdge e) {
		this.edge=e;
	}
	//This func return the fruit edge
	public DEdge getEdge () {
		return this.edge;
	}
	
	//This func return the fruit value
	public double getValue() {
		return value;
	}
	//This func sets the fruit value
	public void setValue(double value) {
		this.value = value;
	}
	//This func return the fruit type
	public int getType() {
		return type;
	}
	//This func sets the fruit type
	public void setType(int type) {
		this.type = type;
	}
	//This func return the fruit position
	public Point3D getPos() {
		return pos;
	}
	//This func sets the fruit position
	public void setPos(Point3D pos) {
		this.pos = pos;
	}
	//This func return if there a robot on his way to eat this fruit
	public boolean getVisit() {
		return this.visit;
	}
	//This func sets the option if there a robot on his way to eat this fruit
	public void setVisit(boolean b) {
		this.visit= b;		
	}

}