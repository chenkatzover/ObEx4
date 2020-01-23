package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import dataStructure.DEdge;
import dataStructure.DGraph;
import dataStructure.DNode;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
/**
 * This class represent a game and the functions allows you to open a new game,play the game manual
 * or auto and get information about the game. 
 * 
 * @author CHEN KATZOVER
 *
 */

public class MyGameGUI extends JFrame implements MouseListener,ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	game_service game; // you have [0,10] games.
	List <Fruit> List_Fruits;
	List <Robot> List_Robots;
	graph gr;
	graph originalDrapg;
	boolean isRun;
	boolean isPic;
	int type;
	Image bananaImg;
	Image appleImg;
	Image robotImg;
	Thread t;
	Thread r;
	private static KML_Logger kml;
	private static int graph_num;
	private static int ID;

/**
 * constructor that opens a new game 
 */
	public MyGameGUI () {
		initWindow ();

	}
/**
 * open a new game window 
 */
	private void initWindow() {
		openWindowGraph();
		openWindowId();

	}
/**
 * open a window that ask the user ID
 */
	private void openWindowId () {
		JFrame window=new JFrame(); 

		//submit button
		JButton buttont=new JButton("Ok");    
		buttont.setBounds(100,110,140, 40);   

		//enter name label
		JLabel labelt = new JLabel();		
		labelt.setText("Enter ID:");
		labelt.setBounds(10, 10, 500, 100);

		//empty label which will show event after button clicked
		JLabel labelt1 = new JLabel();
		labelt1.setBounds(10, 110, 200, 100);

		//textfield to enter name
		JTextField textfieldt= new JTextField();
		textfieldt.setBounds(110, 70, 130, 30);

		//add to frame
		window.add(labelt1);
		window.add(textfieldt);
		window.add(labelt);
		window.add(buttont);    
		window.setSize(500,300);    
		window.setLayout(null);    
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//action listener
		buttont.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = textfieldt.getText();
				int id= Integer.parseInt(text);
				ID=id;
				window.dispose();
				Game_Server.login(ID);
			}


		});

	}
	/**
	 * set a menu bar in the jframe.
	 */
	private void setMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu play = new JMenu("play");
		JMenu info = new JMenu("information");
		JMenuItem NOG = new JMenuItem ("Your num of games");
		NOG.addActionListener(this);
		JMenuItem YCL = new JMenuItem ("Your current level");
		YCL.addActionListener(this);
		JMenuItem YBS = new JMenuItem ("Your best score");
		YBS.addActionListener(this);
		JMenuItem playa = new JMenuItem ("play game auto");
		playa.addActionListener(this);
		JMenuItem playm = new JMenuItem ("play game manual");
		playm.addActionListener(this);
		JMenuItem p = new JMenuItem ("Your position");
		p.addActionListener(this);

		info.add(NOG);
		info.add(YCL);
		info.add(YBS);
		info.add(p);
		play.add(playa);
		play.add(playm);
		menuBar.add(play);
		this.setJMenuBar(menuBar);
		menuBar.add(info);
		this.setJMenuBar(menuBar);
	}

	private void initGUI (int sNumber) throws JSONException {

		isRun=false;
		this.setSize(1000, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		List_Fruits=null;
		List_Robots=new LinkedList <Robot> ();
		game = Game_Server.getServer(sNumber);
		setMenuBar();
		init_Graph();
		init_Fruits();		
		setImage();

		this.setVisible(true);
	}
/**
 * set the robots and fruits images at the game
 */
	private void setImage () {
		try {
			bananaImg = ImageIO.read(new File ("data/banana1.png"));
			appleImg = ImageIO.read(new File ("data/apple1.png"));
			robotImg = ImageIO.read(new File ("data/robot1.png"));
			isPic=true;

		}
		catch (Exception e) {
			e.printStackTrace();
			isPic= false;
		}
	}
	/**
	 *scaled the locations to the jframe window size.
	 */
	public double find_max_x () {

		double ans=Double.MIN_VALUE;
		for (node_data node: gr.getV()) {
			if (node.getLocation().x()>ans) {
				ans=node.getLocation().x();
			}
		}
		return ans;
	}

	public double find_min_x () {
		double ans=Double.MAX_VALUE;
		for (node_data node: gr.getV() ) {
			if (node.getLocation().x()<ans) {
				ans=node.getLocation().x();
			}
		}
		return ans;
	}

	public double find_max_y () {
		double ans=Double.MIN_VALUE;
		for (node_data node: gr.getV()) {
			if (node.getLocation().y()>ans) {
				ans=node.getLocation().y();
			}
		}
		return ans;
	}

	public double find_min_y () {
		double ans=Double.MAX_VALUE;
		for (node_data node: gr.getV() ) {
			if (node.getLocation().y()<ans) {
				ans=node.getLocation().y();
			}
		}
		return ans;
	}

	/**
	 * 
	 * @param x-pos
	 * @param y-pos
	 * @return the scale of this points by the Jframe
	 */
	public Point3D setLocation (double x , double y) {
		double frame_max_x=getWidth()*0.9;
		double farme_min_x=getWidth()*0.01;
		double frame_max_y=getHeight()*0.8;
		double farme_min_y=getHeight()*0.1;
		double max_x=find_max_x();
		double min_x=find_min_x();
		double max_y=find_max_y();
		double min_y=find_min_y();

		double resx = ((x - min_x) / (max_x-min_x)) * (frame_max_x - farme_min_x) + farme_min_x;
		double resy = ((y - min_y) / (max_y-min_y)) * (frame_max_y - farme_min_y) + farme_min_y;
		return new Point3D(resx, resy);
	}
	
	/**
	 * This func call to all paint functions
	 */
	@Override
	
	public void paint(Graphics g) {
		BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		super.paint(g2d);
		paint_graph (g2d);
		paint_fruits(g2d);
		paint_robots(g2d);
		try {
			paint_time(g2d);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Graphics2D g2dComponent = (Graphics2D) g;
		g2dComponent.drawImage(bufferedImage, null, 0, 0); 
	}
/**
 * paint on the jframe window -time to end,moves and score
 * 
 */
	private void paint_time (Graphics g) throws JSONException {
		if (!isRun) {			
			g.setColor(Color.red);
			g.setFont(new Font("Courier", Font.PLAIN, 20));
			g.drawString("to start the game please press play , and choose the type", (int)( getWidth()*0.1),(int)(getHeight()*0.09));
		}
		if (game.isRunning()) {
			g.setColor(Color.black);
			g.setFont(new Font("Courier", Font.PLAIN, 20));
			JSONObject fruit_json = new JSONObject(game.toString());
			JSONObject details = fruit_json.getJSONObject("GameServer");
			double grade = details.getDouble("grade");
			int moves = details.getInt("moves");
			String time = "time to end:"+ String.valueOf((game.timeToEnd()/1000)+"   score: "+grade+"    moves: "+moves);
			g.drawString(time,(int)( getWidth()*0.1),(int)(getHeight()*0.09));
		}
		if (isRun && !game.isRunning()){ 
			g.setColor(Color.red);
			g.setFont(new Font("Courier", Font.PLAIN, 20));
			JSONObject fruit_json = new JSONObject(game.toString());
			JSONObject details = fruit_json.getJSONObject("GameServer");
			double grade = details.getDouble("grade");
			long moves = details.getLong("moves");
			String results = "Game Over ! your grade : "+ grade+" , you did "+moves+" moves!";
			g.drawString(results, (int)( getWidth()*0.1),(int)(getHeight()*0.09));
		}

	}
	/**
	 *paint the graph in the jframe.
	 */
	
	private void paint_graph(Graphics g) {
		for (node_data n : gr.getV()) {
			n.setTag(0);
		}

		for (node_data n : gr.getV()) {

			Point3D loc = setLocation(n.getLocation().x(), n.getLocation().y()); 

			for (edge_data edge : gr.getE(n.getKey())) {

				Point3D loc1 =setLocation(gr.getNode(edge.getDest()).getLocation().x(), gr.getNode(edge.getDest()).getLocation().y());

				//print edge between n and dest
				g.setColor(Color.PINK);
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(2));
				g.drawLine(loc.ix(), loc.iy(), loc1.ix(), loc1.iy()); 

				//edge weight
				g.setColor(Color.black);
				Graphics2D g3 = (Graphics2D) g;
				g3.setStroke(new BasicStroke(2));
				g3.setFont(new Font("Courier", Font.PLAIN, 10));
				int x = (int)((loc.ix()*0.7)+(0.3*loc1.ix()));
				int y = 1+(int)((loc.iy()*0.7)+(0.3*loc1.iy()));
				g.drawString(String.format("%.2f", edge.getWeight()), x,y); 

				//mark src	
				g.setColor(Color.PINK);
				g.fillOval((int)((loc.ix()*0.7)+(0.3*loc1.ix())), 1+(int)((loc.iy()*0.7)+(0.3*loc1.iy())), 8, 8); 

				//print neighbor
				if (gr.getNode(edge.getDest()).getTag() == 0) {
					//neighbor point
					g.setColor(Color.BLUE);
					g.fillOval(loc1.ix(), loc1.iy(), 8, 8); 

					g.setColor(Color.black);
					g.setFont(new Font("Courier", Font.PLAIN, 20));
					//neighber key
					g.setColor(Color.black);
					g.drawString(Integer.toString(( gr.getNode(edge.getDest())).getKey()), loc1.ix(),loc1.iy());


					//mark the neighbor
					gr.getNode(edge.getDest()).setTag(1); 
				}
			}
			if (n.getTag() == 0) {
				g.setColor(Color.BLUE);
				//n point
				g.fillOval(loc.ix(), loc.iy(), 10, 10); 
				g.setColor(Color.black);
				g.setFont(new Font("Courier", Font.PLAIN, 20));
				// n key
				g.drawString(Integer.toString(n.getKey()), loc.ix(), loc.iy()); 
			}

			//mark n
			n.setTag(1);
		}
	}
	/**
	 *paint the robots on the graph.
	 */
	private void paint_robots(Graphics g) {
		if (List_Robots!=null) {
			for (Robot r: List_Robots) {

				kml.Place_Mark("data/robot.jpg",r.getPos().toString());
				Point3D pos = setLocation(r.getPos().x(), r.getPos().y());
				if (isPic) {
					g.drawImage(robotImg, pos.ix(),pos.iy(), 30, 30, null);
				}
				else {
					g.setColor(Color.green);						
					g.fillOval(pos.ix(),pos.iy(), 15, 15); 
				}
			}
		}
	}
	/**
	 * paint the fruits on the graph.
	 */
	private void paint_fruits(Graphics g) {


		for (Fruit f: List_Fruits) {
			Point3D pos= setLocation(f.getPos().x(), f.getPos().y());
			if (isPic) {
				if (f.getType()==1) {
					kml.Place_Mark("fruit_1",f.getPos().toString());
					g.drawImage(appleImg, pos.ix(), pos.iy(), 30, 30, null);
				}
				if (f.getType()==-1) {
					kml.Place_Mark("fruit_-1",f.getPos().toString());
					g.drawImage(bananaImg, pos.ix(), pos.iy(), 30, 30, null);
				}
			}


			else {
				if (f.getType()==1) {
					kml.Place_Mark("fruit_1",f.getPos().toString());
					g.setColor(Color.RED);
				}
				if (f.getType()==-1) {
					kml.Place_Mark("fruit_-1",f.getPos().toString());
					g.setColor(Color.YELLOW);
				}
				g.fillOval(pos.ix(), pos.iy(),10, 20); 
			}
		}
	}
/**
 * open a window that ask a Graph number from the user
 */
	private void openWindowGraph () {
		JFrame window=new JFrame(); 

		//submit button
		JButton buttont=new JButton("Ok");    
		buttont.setBounds(100,110,140, 40);   

		//enter name label
		JLabel labelt = new JLabel();		
		labelt.setText("Enter graph number (0-10)");
		labelt.setBounds(10, 10, 500, 100);

		//empty label which will show event after button clicked
		JLabel labelt1 = new JLabel();
		labelt1.setBounds(10, 110, 200, 100);

		//textfield to enter name
		JTextField textfieldt= new JTextField();
		textfieldt.setBounds(110, 70, 130, 30);

		//add to frame
		window.add(labelt1);
		window.add(textfieldt);
		window.add(labelt);
		window.add(buttont);    
		window.setSize(500,300);    
		window.setLayout(null);    
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//action listener
		buttont.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = textfieldt.getText();
				int sNumber= Integer.parseInt(text);
				if (sNumber<0 || sNumber>23) {
					labelt1.setText("choose number between 0 to 10");	
				}
				else {
					try {
						initGUI(sNumber);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					graph_num=sNumber;
					kml= new KML_Logger(graph_num,game);
					kml.UploadKMLfile(game);
					window.dispose();
				}

			}          
		});

	}
	/**
	 *  update the graph from the game server.
	 */
	private void init_Graph() throws JSONException{

		gr=new DGraph ();
		JSONObject graph = new JSONObject(game.getGraph());
		JSONArray  node =graph.getJSONArray("Nodes");
		for (int i = 0; i < node.length(); i++)
		{
			Point3D pos = new Point3D (node.getJSONObject(i).getString("pos"));
			int id = node.getJSONObject(i).getInt("id");
			gr.addNode(new DNode(id,pos));

		}
		JSONArray  edge =graph.getJSONArray("Edges");
		for (int i = 0; i < edge.length(); i++)
		{
			int src = edge.getJSONObject(i).getInt("src");
			double w = edge.getJSONObject(i).getDouble("w");
			int dest = edge.getJSONObject(i).getInt("dest");

			gr.connect(src, dest, w);
		}

	}
	/**
	 *  update the fruits from the game server
	 */
	private void init_Fruits ()  throws JSONException {

		List <String> String_Fruits= game.getFruits();
		if(List_Fruits == null)
			List_Fruits= new LinkedList <Fruit>();

		for (String fruit: String_Fruits) {
			JSONObject fruit_json = new JSONObject(fruit);
			JSONObject details = fruit_json.getJSONObject("Fruit");
			double value = details.getDouble("value");
			int type = details.getInt("type");
			Point3D pos= new Point3D (details.getString("pos"));
			Fruit f= new Fruit (value,type,pos);
			List_Fruits.add(f);

		}

		for (Fruit f: List_Fruits) {
			Point3D pos_f= setLocation(f.getPos().x(), f.getPos().y());
			double EPS=0.01;
			double edge_dis; //dis bet n,dest
			double f_dis_src; // dis bet f, n
			double f_dis_dest; //dis bet f, dest
			for (node_data n: gr.getV()) {
				Point3D pos_n= setLocation(n.getLocation().x(), n.getLocation().y());
				for (edge_data e: gr.getE(n.getKey())) {
					if (f.getEdge()==null) {
						Point3D pos_e= setLocation(gr.getNode(e.getDest()).getLocation().x(), gr.getNode(e.getDest()).getLocation().y());

						f_dis_src=pos_n.distance2D(pos_f);
						f_dis_dest=pos_e.distance2D(pos_f);
						edge_dis=pos_e.distance2D(pos_n);

						double hefresh=(f_dis_src+f_dis_dest)-edge_dis;
						if (hefresh<EPS && hefresh>(-1*EPS)) {
							if ( (f.getType()<0) && (e.getSrc()>e.getDest()) || (f.getType()>0) && (e.getSrc()<e.getDest()) )
								f.setEdge((DEdge)e);
						}
					}
				}

			}
		}

	}
	/**
	 *  update the robots from the game server
	 */
	private void init_Robots () throws JSONException {
		List <String> String_Robots= game.getRobots();
		List_Robots= new LinkedList <Robot>();
		for (String robot: String_Robots) {
			JSONObject robot_json1 = new JSONObject(robot);
			JSONObject details = robot_json1.getJSONObject("Robot");
			int id = details.getInt("id");

			if (!isContainsR(id)) {
				double value = details.getDouble("value");
				int src = details.getInt("src");
				int dest = details.getInt("dest");
				double speed = details.getDouble("speed");
				Point3D pos= new Point3D (details.getString("pos"));
				Robot r= new Robot (id,value,src,dest,speed,pos);
				List_Robots.add(r);
			}
		}		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {}
	/**
	 *   locate the robots at the moment the user press the mouse. and also move the robots to different nodes when the user plays manual game.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {

		if (isRun==false) {
			//find the closest node to the click
			Point3D point= new Point3D (arg0.getX(),arg0.getY());
			double min_dis=Double.MAX_VALUE;
			node_data ans = null;
			for (node_data node: gr.getV()) {
				Point3D pos_n= setLocation(node.getLocation().x(), node.getLocation().y());
				double dis=point.distance2D(pos_n);
				if (dis<min_dis) {
					min_dis=dis;
					ans=node;
				}
			}

			game.addRobot(ans.getKey());

			try {
				init_Robots();

			} catch (JSONException e) {e.printStackTrace();}
		}
		else {

			int x = arg0.getX();

			int y = arg0.getY();

			Point3D point= new Point3D (x,y);

			//find the closest node to the click
			double min_dis=Double.MAX_VALUE;
			node_data ans = null;
			for (node_data node: gr.getV()) {
				Point3D pos_n= setLocation(node.getLocation().x(), node.getLocation().y());
				double dis=point.distance2D(pos_n);
				if (dis<min_dis) {
					min_dis=dis;
					ans=node;
				}
			}

			for (Robot r: List_Robots) {
				if (r.getDest() != -1) {
					continue;
				}

				if(gr.getEdge(r.getSrc(), ans.getKey()) != null) {
					game.chooseNextEdge(r.getId(), ans.getKey());
					break;
				}

			}
		}

	}
	/**
	 * starts the game the moment the user finished locate all the robots.
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {

		JSONObject robot_json;
		try {
			robot_json = new JSONObject(game.toString());
			JSONObject ttt = robot_json.getJSONObject("GameServer");
			int num_robot= ttt.getInt("robots");
			if (List_Robots.size() == num_robot && isRun==false) {
				runGameManual();
			}

		} catch (JSONException e) {e.printStackTrace();}

	}

	public synchronized void ThreadMove()
	{
		r = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				while(game.isRunning())
				{
					if(game.isRunning())
					{
						game.move();
					}
					try {
						Thread.sleep(timeSleep());
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				r.interrupt();
			}
		});
		r.start();
	}
	/**
	 * running the automatic game.
	 */
	private void runGameAuto () throws JSONException {

		AutoAlgo Agame= new AutoAlgo (game, gr, List_Fruits,List_Robots);

		game.startGame();
		ThreadMove();
		isRun=true;//
		//List<node_data> list=onlyFor0();

		Runnable moveA = new Runnable() {

			@Override
			public synchronized void run() {
				try {
					while (game.isRunning()) {
						Agame.play_game();
						//move0(list);
						updateRobots(game.getRobots());
						updateFruit();
						repaint();
						Thread.sleep(20);//4
					}
					repaint();
					kml.KML_Stop();
				}
				catch (Exception e) {e.getStackTrace();}
			}

		};
		Thread thread = new Thread(moveA);
		thread.start();
	}
/**
 * this fucn sets for the threads how much to sleep
 * 
 */
	private synchronized int timeSleep () {
		int sleep = 40; //1
		for (Robot rob: List_Robots) {
			if (List_Fruits !=null) {
				for (Fruit fruit : List_Fruits) {
					if (fruit.getEdge() != null) {
						if (rob.getSrc() == fruit.getEdge().getSrc() && rob.getDest() == fruit.getEdge().getDest()) {
							sleep = 1; //2
						}
						else
							sleep =50;//3	
					}

				}
			}
		}
		return sleep;
	}

	//	private void move0 (List<node_data> list) {
	//			if (!list.isEmpty() && List_Robots.get(0).getDest()== -1) {
	//				game.chooseNextEdge(List_Robots.get(0).getId(), list.get(0).getKey());
	//				list.remove(0);
	//			}
	//		}
	//	private List<node_data> onlyFor0() {
	//
	//		List<node_data> temp= new ArrayList<node_data>();
	//
	//		//temp.add(gr.getNode(r.getSrc()));
	//		temp.add(gr.getNode(8));
	//		temp.add(gr.getNode(7));
	//		temp.add(gr.getNode(6));
	//		temp.add(gr.getNode(5));
	//		temp.add(gr.getNode(4));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(4));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(0));
	//		temp.add(gr.getNode(10));
	//		temp.add(gr.getNode(9));
	//		temp.add(gr.getNode(8));
	//		temp.add(gr.getNode(7));
	//		temp.add(gr.getNode(6));
	//		temp.add(gr.getNode(5));
	//		temp.add(gr.getNode(4));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(0));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(0));
	//		temp.add(gr.getNode(10));
	//		temp.add(gr.getNode(0));
	//		temp.add(gr.getNode(1));
	//		temp.add(gr.getNode(2));
	//		temp.add(gr.getNode(3));
	//		temp.add(gr.getNode(2));
	//
	//		return temp;
	//	}


	//	}

	/**
	 * running the manual game..
	 */
	private void runGameManual ()  {

		game.startGame();
		isRun=true;
		Runnable move = new Runnable() {

			@Override
			public void run() {
				while(game.isRunning()) {
					moveRobot();

					try {
						Thread.sleep(85);
					} catch (Exception e) {}

				}
				repaint ();

			}
		};
		Thread thread = new Thread(move);
		thread.start();


	}
	/**
	 * update the robots changes in the manual game.
	 */
	public void moveRobot() {

		try {
			List <String> game_move = game.move();
			if(game_move != null) {
				updateRobots(game_move);
				updateFruit();
				repaint();
			}
		} catch (JSONException e) {e.printStackTrace();}
	}
	/**
	 * update the robot list.
	 */
	private void updateRobots (List <String> game_move) throws JSONException { 
		for (String robot : game_move) {
			JSONObject robot_json = new JSONObject(robot);
			JSONObject details = robot_json.getJSONObject("Robot");
			int id = details.getInt("id");
			int src = details.getInt("src");
			int dest = details.getInt("dest");
			Point3D pos= new Point3D (details.getString("pos"));
			List_Robots.get(id).setDest(dest);
			List_Robots.get(id).setSrc(src);
			List_Robots.get(id).setPos(pos);
		}
	}
	/**
	 * update the fruits list.
	 */

	private void updateFruit () throws JSONException {
		List_Fruits.clear();
		init_Fruits();

	}

	private boolean isContainsR (int id) {
		for (Robot r: List_Robots) {
			if (r.getId()==id) return true;
		}
		return false;
	}

/**
 * identify the user mouse clicked and action by it
 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand()=="play game auto") {
			this.type=2;
			try {
				runGameAuto();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		if (e.getActionCommand()=="play game manual") {
			this.type=1;
			addMouseListener(this);
		}

		if (e.getActionCommand()=="Your num of games") {
			JFrame num=new JFrame(); 
			JLabel labels = new JLabel();	
			int answer = DBinfo.numOfGames(ID);
			if(answer==-1) {
				labels.setText("You did not play yet" );
				labels.setBounds(10, 10, 500, 100);
				num.add(labels);
				num.setSize(300,300);    

				num.setVisible(true);  
			}
			else {
				labels.setText("The number of games that you played is:"+answer );
				labels.setBounds(10, 10, 500, 100);
				num.add(labels);
				num.setSize(300,300);    
				num.setVisible(true); 
			}


		}
		if (e.getActionCommand()=="Your current level") {
			int level=DBinfo.currentLevel(ID);
			JFrame lev=new JFrame(); 
			JLabel labels = new JLabel();		
			labels.setText("Your current level is:"+level );
			labels.setBounds(10, 10, 500, 100);
			lev.add(labels);
			lev.setSize(300,300);    
			lev.setVisible(true);   

		}
		if (e.getActionCommand()=="Your best score") {

			JFrame window=new JFrame(); 

			//submit button
			JButton buttont=new JButton("Ok");    
			buttont.setBounds(100,110,140, 40);   

			//enter name label
			JLabel labelt = new JLabel();		
			labelt.setText("choose Graph number");
			labelt.setBounds(10, 10, 500, 100);

			//empty label which will show event after button clicked
			JLabel labelt1 = new JLabel();
			labelt1.setBounds(10, 110, 200, 100);

			//textfield to enter name
			JTextField textfieldt= new JTextField();
			textfieldt.setBounds(110, 70, 130, 30);

			//add to frame
			window.add(labelt1);
			window.add(textfieldt);
			window.add(labelt);
			window.add(buttont);    
			window.setSize(500,300);    
			window.setLayout(null);    
			window.setVisible(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//action listener
			buttont.addActionListener(new ActionListener() {


				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = textfieldt.getText();
					int Graphn= Integer.parseInt(text);
					int score=DBinfo.bestScore(ID,Graphn);
					if (Graphn<0 || Graphn>11) {
						labelt1.setText("choose number between 0 to 11");	
					}
					else {
						labelt1.setText("Your best score in this level is:"+score);	
					}
				}          
			});

		}
		if (e.getActionCommand()=="Your position") {
			JFrame window=new JFrame(); 

			//submit button
			JButton buttont=new JButton("Ok");    
			buttont.setBounds(100,110,140, 40);   

			//enter name label
			JLabel labelt = new JLabel();		
			labelt.setText("choose Graph number");
			labelt.setBounds(10, 10, 500, 100);

			//empty label which will show event after button clicked
			JLabel labelt1 = new JLabel();
			labelt1.setBounds(10, 110, 200, 100);

			//textfield to enter name
			JTextField textfieldt= new JTextField();
			textfieldt.setBounds(110, 70, 130, 30);

			//add to frame
			window.add(labelt1);
			window.add(textfieldt);
			window.add(labelt);
			window.add(buttont);    
			window.setSize(500,300);    
			window.setLayout(null);    
			window.setVisible(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//action listener
			buttont.addActionListener(new ActionListener() {


				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = textfieldt.getText();
					int sNumber= Integer.parseInt(text);
					int p=DBinfo.Position(ID,sNumber);
					if (sNumber<0 || sNumber>11) {
						labelt1.setText("choose number between 0 to 11");	
					}
					else {
						labelt1.setText("Your position in the game is:"+p);	

					}

				}          
			});

		}	
	}

}