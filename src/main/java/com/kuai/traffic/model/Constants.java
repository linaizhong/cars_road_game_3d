package com.kuai.traffic.model;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Constants {
	public static Map<String, Map<String, Integer>> ROAD = new HashMap<>();
	static {
		ROAD.put("LENGTH", new HashMap<String, Integer>() {
			{
				put("NONE", 0);
				put("SHORT", 25);
				put("MEDIUM", 50);
				put("LONG", 100);
			}
		});
		ROAD.put("HILL", new HashMap<String, Integer>() {
			{
				put("NONE", 0);
				put("LOW", 20);
				put("MEDIUM", 40);
				put("HIGH", 60);
			}
		});
		ROAD.put("CURVE", new HashMap<String, Integer>() {
			{
				put("NONE", 0);
				put("EASY", 2);
				put("MEDIUM", 4);
				put("HARD", 6);
			}
		});
	}

	public static Map<String, Integer> KEYS = new HashMap<>();
	static {
		KEYS.put("LEFT", 37);
		KEYS.put("UP", 38);
		KEYS.put("RIGHT", 39);
		KEYS.put("DOWN", 40);
		KEYS.put("A", 65);
		KEYS.put("D", 68);
		KEYS.put("S", 83);
		KEYS.put("W", 87);
	}

	public static Map<String, Object> COLORS = new HashMap<>();
	static {
		COLORS.put("SKY", new Color(0x72, 0xd7, 0xee));
		COLORS.put("TREE", new Color(0x00, 0x51, 0x08));
		COLORS.put("FOG", new Color(0x00, 0x51, 0x08));
//		 COLORS.put("LIGHT", new TrafficColors(new Color(0x6b, 0x6b, 0x6b),
//		 new Color(0x10, 0xaa, 0x10), new Color(0x55, 0x55, 0x55), new
//		 Color(0xcc, 0xcc, 0xcc)));
//		 COLORS.put("DARK", new TrafficColors(new Color(0x69, 0x69, 0x69), new
//		 Color(0x00, 0x9a, 0x00), new Color(0xbb, 0xbb, 0xbb), null));
		COLORS.put("LIGHT", new TrafficColors(new Color(0x6b, 0x6b, 0x6b), new Color(0xaa, 0xaa, 0x10),
				new Color(0x55, 0x55, 0x55), new Color(0xcc, 0xcc, 0xcc)));
		COLORS.put("DARK", new TrafficColors(new Color(0x69, 0x69, 0x69), new Color(0x9a, 0x9a, 0x00),
				new Color(0xbb, 0xbb, 0xbb), null));
		COLORS.put("START", new TrafficColors(new Color(0xff, 0xff, 0xff), new Color(0xff, 0xff, 0xff),
				new Color(0xff, 0xff, 0xff), null));
		COLORS.put("FINISH", new TrafficColors(new Color(0x00, 0x00, 0x00), new Color(0x00, 0x00, 0x00),
				new Color(0x00, 0x00, 0x00), null));
	}

	public static Map<String, Rectangle> BACKGROUND = new HashMap<>();
	static {
		BACKGROUND.put("HILLS", new Rectangle(5, 5, 1280, 480));
		BACKGROUND.put("SKY", new Rectangle(5, 495, 1280, 480));
		BACKGROUND.put("TREES", new Rectangle(5, 985, 1280, 480));
	}

	public static Map<String, Rectangle> SPRITES = new HashMap<>();
	static {
		SPRITES.put("PALM_TREE", new Rectangle(5, 5, 215, 540));
		SPRITES.put("BILLBOARD08", new Rectangle(230, 5, 385, 265));
		SPRITES.put("TREE1", new Rectangle(625, 5, 360, 360));
		SPRITES.put("DEAD_TREE1", new Rectangle(5, 555, 135, 332));
		SPRITES.put("BILLBOARD09", new Rectangle(150, 555, 328, 282));
		SPRITES.put("BOULDER3", new Rectangle(230, 280, 320, 220));
		SPRITES.put("COLUMN", new Rectangle(995, 5, 200, 315));
		SPRITES.put("BILLBOARD01", new Rectangle(625, 375, 300, 170));
		SPRITES.put("BILLBOARD06", new Rectangle(488, 555, 298, 190));
		SPRITES.put("BILLBOARD05", new Rectangle(5, 897, 298, 190));
		SPRITES.put("BILLBOARD07", new Rectangle(313, 897, 298, 190));
		SPRITES.put("BOULDER2", new Rectangle(621, 897, 298, 140));
		SPRITES.put("TREE2", new Rectangle(1205, 5, 282, 295));
		SPRITES.put("BILLBOARD04", new Rectangle(1205, 310, 268, 170));
		SPRITES.put("DEAD_TREE2", new Rectangle(1205, 490, 150, 260));
		SPRITES.put("BOULDER1", new Rectangle(1205, 760, 168, 248));
		SPRITES.put("BUSH1", new Rectangle(5, 1097, 240, 155));
		SPRITES.put("CACTUS", new Rectangle(929, 897, 235, 118));
		SPRITES.put("BUSH2", new Rectangle(255, 1097, 232, 152));
		SPRITES.put("BILLBOARD03", new Rectangle(5, 1262, 230, 220));
		SPRITES.put("BILLBOARD02", new Rectangle(245, 1262, 215, 220));
		SPRITES.put("STUMP", new Rectangle(995, 330, 195, 140));
		SPRITES.put("SEMI", new Rectangle(1365, 490, 122, 144));
		SPRITES.put("TRUCK", new Rectangle(1365, 644, 100, 78));
		SPRITES.put("CAR03", new Rectangle(1383, 760, 88, 55));
		SPRITES.put("CAR02", new Rectangle(1383, 825, 80, 59));
		SPRITES.put("CAR04", new Rectangle(1383, 894, 80, 57));
		SPRITES.put("CAR01", new Rectangle(1205, 1018, 80, 56));
		SPRITES.put("PLAYER_UPHILL_LEFT", new Rectangle(1383, 961, 80, 45));
		SPRITES.put("PLAYER_UPHILL_STRAIGHT", new Rectangle(1295, 1018, 80, 45));
		SPRITES.put("PLAYER_UPHILL_RIGHT", new Rectangle(1385, 1018, 80, 45));
		SPRITES.put("PLAYER_LEFT", new Rectangle(995, 480, 80, 41));
		SPRITES.put("PLAYER_STRAIGHT", new Rectangle(1085, 480, 80, 41));
		SPRITES.put("PLAYER_RIGHT", new Rectangle(995, 531, 80, 41));
	}

	//the reference sprite width should be 1/3rd the (half-)roadWidth
	public static double SPRITES_SCALE = 0.3 * (1 / (double)SPRITES.get("PLAYER_STRAIGHT").width); 

	public static Rectangle[] SPRITES_BILLBOARDS = { SPRITES.get("BILLBOARD01"), SPRITES.get("BILLBOARD02"),
			SPRITES.get("BILLBOARD03"), SPRITES.get("BILLBOARD04"), SPRITES.get("BILLBOARD05"),
			SPRITES.get("BILLBOARD06"), SPRITES.get("BILLBOARD07"), SPRITES.get("BILLBOARD08"),
			SPRITES.get("BILLBOARD09") };

	public static Rectangle[] SPRITES_PLANTS = { SPRITES.get("TREE1"), SPRITES.get("TREE2"), SPRITES.get("DEAD_TREE1"),
			SPRITES.get("DEAD_TREE2"), SPRITES.get("PALM_TREE"), SPRITES.get("BUSH1"), SPRITES.get("BUSH2"),
			SPRITES.get("CACTUS"), SPRITES.get("STUMP"), SPRITES.get("BOULDER1"), SPRITES.get("BOULDER2"),
			SPRITES.get("BOULDER3") };

	public static Rectangle[] SPRITES_CARS = { SPRITES.get("CAR01"), SPRITES.get("CAR02"), SPRITES.get("CAR03"),
			SPRITES.get("CAR04"), SPRITES.get("SEMI"), SPRITES.get("TRUCK") };
}
