package gameClient;

import java.util.List;

import dataStructure.node_data;
import utils.Point3D;
/**
 * The class represent a robot object that have 7 fildes:

private int id;//the robot id
private double value;//the robot score
private int src;//the node it's on now
private int dest;//the node it's about to move to
private double speed;//the robot speed
private Point3D pos;//his location
private List<node_data> path;//the path to the closest fruit
 * @author CHEN KATZOVER
 *
 */

public class Robot {
	private int id;
	private double value;
	private int src;
	private int dest;
	private double speed;
	private Point3D pos;
	private List<node_data> path;
	
	//******constructor**********
	public Robot (int id, double value, int src, int dest, double speed, Point3D pos) {
		this.id=id;
		this.value=value;
		this.src=src;
		this.dest=dest;
		this.speed=speed;
		this.pos=pos;
	}
	//******Setters&Getters*****
	public String toString () {
		return "id "+id+" src "+src+" dest "+dest+" pos "+pos;
	}
//This func return the robot id
	public int getId() {
		return id;
	}
	//This func sets the robot id
	public void setId(int id) {
		this.id = id;
	}
	//This func return the robot value
	public double getValue() {
		return value;
	}
	//This func sets the robot value
	public void setValue(double value) {
		this.value = value;
	}
	//This func return the robot src
	public int getSrc() {
		return src;
	}
	//This func sets the robot src

	public void setSrc(int src) {
		this.src = src;
	}
	//This func return the robot dest

	public int getDest() {
		return dest;
	}
	//This func sets the robot dest

	public void setDest(int dest) {
		this.dest = dest;
	}
	//This func return the robot speed

	public double getSpeed() {
		return speed;
	}
	//This func sets the robot speed

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	//This func return the robot position

	public Point3D getPos() {
		return pos;
	}
	//This func sets the robot position

	public void setPos(Point3D pos) {
		this.pos = pos;
	}
	//This func return the robot path

	public List<node_data> getPath(){
		return this.path;
	}
	//This func sets the robot path

	public void setPath(List<node_data> way) {
		this.path= way;
		
	}
	
	
}