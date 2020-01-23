package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;

class JsonTest {

	@Test
	void test() throws JSONException {
		game_service game = Game_Server.getServer(0);
		JSONObject graph = new JSONObject(game.getGraph());
		JSONArray  node =graph.getJSONArray("Nodes");
		assertEquals(node.length(), 11);

		List <String> String_Fruits= game.getFruits();
		assertEquals(String_Fruits.size(), 1);
		game.addRobot(0);
		List <String> String_Robots= game.getRobots();
		assertEquals(String_Robots.size(), 1);

	}

}
