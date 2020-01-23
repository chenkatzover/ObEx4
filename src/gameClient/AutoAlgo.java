package gameClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DEdge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

/**
 * This class represent different algorithms that helps the robots at the auto game 
 * moves as effective as possible -Get more points in as few steps as possible.
 * @author CHEN KATZOVER
 *
 */
public class AutoAlgo  {

	game_service game;
	List <Fruit> List_Fruits;
	List <Robot> List_Robots;
	graph gr;
	Graph_Algo ga;
	double width;
	double height;
	double max_x;
	double min_x;
	double max_y;
	double min_y;
	
//**constructor***
	public AutoAlgo (game_service game,graph gr,List <Fruit> List_Fruits,List <Robot> List_Robots) {
		this.List_Fruits=List_Fruits;
		this.List_Robots=List_Robots;
		this.gr=gr;
		ga = new Graph_Algo ();
		ga.init(gr);
		this.game=game;
		init_Robots_auto();

	}

/**
 * This func locate the first robots when the game starts.
 */
	public void init_Robots_auto () {

		try {
			JSONObject robot_json1 = new JSONObject(game.toString());
			JSONObject ttt = robot_json1.getJSONObject("GameServer");
			int num_robot= ttt.getInt("robots");
			for (int i=0; i<num_robot; i++) {
				int pos = List_Fruits.get(i).getEdge().getSrc();
				game.addRobot(pos);
			}
			List <String> game_move= game.getRobots();
			for (String robot : game_move) {
				JSONObject robot_json = new JSONObject(robot);
				JSONObject details = robot_json.getJSONObject("Robot");
				int id = details.getInt("id");
				int src = details.getInt("src");
				int dest = details.getInt("dest");
				double value = details.getDouble("value");
				double speed = details.getDouble("speed");
				Point3D pos= new Point3D (details.getString("pos"));
				List_Robots.add(new Robot (id, value,src,dest,speed,pos));
			}
		}
		catch (Exception e) {e.printStackTrace();};
	}

/**
 * This func send every time the robots to move.
 */
	public synchronized void play_game ()  {
		try {
			moveAutomaticallyRobots();
		} catch (Exception e) {e.printStackTrace();}

	}

/**
 * This func moves the robots while the game is running.
 */
	
	 public void moveAutomaticallyRobots() {
		if (List_Fruits!=null) {
			Fruit temp=null ;
			//find the shortest path between each robot to a fruit on the game
			for(Robot rob:List_Robots) {
				if (rob.getDest() == -1) {
					double dist = Double.MAX_VALUE;
					for (Fruit fr:List_Fruits) {
						if(dist>(ga.shortestPathDist(rob.getSrc(), fr.getEdge().getSrc())+fr.getEdge().getWeight())&&(!fr.getVisit())) {
							dist =ga.shortestPathDist(rob.getSrc(), fr.getEdge().getSrc())+fr.getEdge().getWeight();
							temp=fr;
						}
					}
					rob.setPath(ga.shortestPath(rob.getSrc(), temp.getEdge().getSrc()));
					if(rob.getPath().size()>1)	rob.getPath().remove(0);
					rob.getPath().add(gr.getNode(temp.getEdge().getDest()));
					for (Fruit fr:List_Fruits) {
						if(fr.getPos()==temp.getPos()) {
							fr.setVisit(true);

						}
					}
				}

				//move the robots by the path we find for him

			}
			for (Robot r : List_Robots) {						
				while(!r.getPath().isEmpty()) {
					game.chooseNextEdge(r.getId(), r.getPath().get(0).getKey());
					r.getPath().remove(0);
					
				}
			}	
		}

	}



}