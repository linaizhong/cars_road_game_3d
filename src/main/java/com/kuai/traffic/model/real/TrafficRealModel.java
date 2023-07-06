package com.kuai.traffic.model.real;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kuai.traffic.TrafficFrame;
import com.kuai.traffic.model.Camera;
import com.kuai.traffic.model.Car;
import com.kuai.traffic.model.Constants;
import com.kuai.traffic.model.Project;
import com.kuai.traffic.model.Screen;
import com.kuai.traffic.model.Segment;
import com.kuai.traffic.model.Sprite;
import com.kuai.traffic.model.TrafficColors;
import com.kuai.traffic.model.TrafficModel;
import com.kuai.traffic.model.TrafficRender;
import com.kuai.traffic.model.World;
import com.kuai.traffic.util.Util;

public class TrafficRealModel extends TrafficModel {
	private List<Car> cars = new ArrayList<>(); // array of cars on the road

	private int totalCars = 200; // total number of cars on the road
	private long currentLapTime = 0; // current lap time
	private long lastLapTime = 0; // last lap time

	private Map<String, String> hud = new HashMap<>();

	public TrafficRealModel() {
		super();

		hud.put("speed", null);
		hud.put("current_lap_time", null);
		hud.put("last_lap_time", null);
		hud.put("fast_lap_time", null);
	}

	public void update(double dt) {
		//System.out.println("update() : " + dt);

		Car car;
		double carW;
		Sprite sprite;
		double spriteW;

		Segment playerSegment = findSegment(getPosition() + getPlayerZ());
		double playerW = Constants.SPRITES.get("PLAYER_STRAIGHT").getWidth() * Constants.SPRITES_SCALE;
		double speedPercent = getSpeed() / getMaxSpeed();
		double dx = dt * 2 * speedPercent;
		double startPosition = getPosition();

		updateCars(dt, playerSegment, playerW);

		setPosition(Util.increase(getPosition(), dt * getSpeed(), getTrackLength()));

		if (keyLeft) {
			playerX -= dx;
		} else if (keyRight) {
			playerX += dx;
		}

		playerX -= (dx * speedPercent * playerSegment.getCurve() * centrifugal);

		if (keyFaster)
			speed = Util.accelerate(speed, accel, dt);
		else if (keySlower)
			speed = Util.accelerate(speed, breaking, dt);
		else
			speed = Util.accelerate(speed, decel, dt);

		if ((playerX < -1) || (playerX > 1)) {
			if (speed > offRoadLimit)
				speed = Util.accelerate(speed, offRoadDecel, dt);

			for (int n = 0; n < playerSegment.getSprites().size(); n++) {
				sprite = playerSegment.getSprites().get(n);
				spriteW = sprite.getSource().getWidth() * Constants.SPRITES_SCALE;
				if (Util.overlap(playerX, playerW,
						(sprite.getOffset() + spriteW / 2 * (sprite.getOffset() > 0 ? 1 : -1)), spriteW)) {
					speed = maxSpeed / 5;
					position = Util.increase(playerSegment.getP1().getWorld().getZ(), -playerZ, trackLength);
					break;
				}
			}
		}

		for (int n = 0; n < playerSegment.getCars().size(); n++) {
			car = playerSegment.getCars().get(n);
			carW = car.getSprite().getSource().getWidth() * Constants.SPRITES_SCALE;
			if (speed > car.getSpeed()) {
				if (Util.overlap(playerX, (int) playerW, car.getOffset(), carW, 0.8)) {
					speed = car.getSpeed() * (car.getSpeed() / speed);
					position = Util.increase(car.getZ(), -playerZ, trackLength);
					break;
				}
			}
		}

		playerX = Util.limit((int) playerX, -3, 3); // dont ever let it go too
													// far out
													// of bounds
		speed = Util.limit((int) speed, 0, (int) maxSpeed); // or exceed
															// maxSpeed

		skyOffset = (int) Util.increase(skyOffset,
				skySpeed * playerSegment.getCurve() * (position - startPosition) / segmentLength, 1);
		hillOffset = (int) Util.increase(hillOffset,
				hillSpeed * playerSegment.getCurve() * (position - startPosition) / segmentLength, 1);
		treeOffset = (int) Util.increase(treeOffset,
				treeSpeed * playerSegment.getCurve() * (position - startPosition) / segmentLength, 1);

		if (position > playerZ) {
			if (startPosition < playerZ) {
				lastLapTime = currentLapTime;
				currentLapTime = 0;
				updateHud("last_lap_time", formatTime((int) lastLapTime));
			} else {
				currentLapTime += dt;
			}
		}

		updateHud("speed", String.valueOf(5 * Math.round(speed / 500)));
		updateHud("current_lap_time", formatTime((int) currentLapTime));

	}

	private void updateCars(double dt, Segment playerSegment, double playerW) {
		println("updateCars() : " + dt);

		Segment oldSegment, newSegment;
		for (int n = 0; n < cars.size(); n++) {
			Car car = cars.get(n);
			oldSegment = findSegment(car.getZ());
			car.setOffset(car.getOffset() + updateCarOffset(car, oldSegment, playerSegment, playerW));
			car.setZ(Util.increase(car.getZ(), dt * car.getSpeed(), getTrackLength()));
			car.setPercent(Util.percentRemaining((int) car.getZ(), getSegmentLength())); // useful
																							// for
																							// interpolation
																							// during
																							// rendering
																							// phase
			newSegment = findSegment(car.getZ());
			if (oldSegment != newSegment) {
				int index = oldSegment.getCars().indexOf(car);
				if(index > 0) {
					oldSegment.getCars().remove(index);
					newSegment.getCars().add(car);
				}
			}
		}
	}

	private double updateCarOffset(Car car, Segment carSegment, Segment playerSegment, double playerW) {
		println("updateCarOffset()");

		int dir;
		Segment segment;
		Car otherCar;
		double otherCarW;
		int lookahead = 20;
		double carW = car.getSprite().getSource().getWidth() * Constants.SPRITES_SCALE;

		// optimization, dont bother steering around other cars when 'out of
		// sight' of the player
		if ((carSegment.getIndex() - playerSegment.getIndex()) > getDrawDistance())
			return 0;

		for (int i = 1; i < lookahead; i++) {
			segment = getSegments().get((carSegment.getIndex() + i) % getSegments().size());

			if ((segment == playerSegment) && (car.getSpeed() > getSpeed())
					&& (Util.overlap(getPlayerX(), playerW, car.getOffset(), carW, 1.2))) {
				if (getPlayerX() > 0.5)
					dir = -1;
				else if (getPlayerX() < -0.5)
					dir = 1;
				else
					dir = (car.getOffset() > getPlayerX()) ? 1 : -1;
				return dir * 1 / i * (car.getSpeed() - getSpeed()) / getMaxSpeed();
			}

			for (int j = 0; j < segment.getCars().size(); j++) {
				otherCar = segment.getCars().get(j);
				otherCarW = otherCar.getSprite().getSource().getWidth() * Constants.SPRITES_SCALE;
				if ((car.getSpeed() > otherCar.getSpeed()) && Util.overlap(car.getOffset(), carW,
						otherCar.getOffset(), otherCarW, 1.2)) {
					if (otherCar.getOffset() > 0.5)
						dir = -1;
					else if (otherCar.getOffset() < -0.5)
						dir = 1;
					else
						dir = (car.getOffset() > otherCar.getOffset()) ? 1 : -1;
					return dir * 1 / i * (car.getSpeed() - otherCar.getSpeed()) / getMaxSpeed();
				}
			}
		}

		// if no cars ahead, but I have somehow ended up off road, then steer
		// back on
		if (car.getOffset() < -0.9)
			return 0.1;
		else if (car.getOffset() > 0.9)
			return -0.1;
		else
			return 0;
	}

	private double lastY() {
		if (segments == null || segments.size() == 0 || segments.get(segments.size() - 1) == null
				|| segments.get(segments.size() - 1).getP2() == null
				|| segments.get(segments.size() - 1).getP2().getWorld() == null) {
			println("lastY(): 0");

			return 0;
		}

		double y = segments.get(segments.size() - 1).getP2().getWorld().getY();
		println("lastY(): " + y);

		return y;
	}

	private void addSegment(double curve, double y) {
		println("addSegment() : " + curve + ", " + y);

		int index = segments.size();

		Segment segment = new Segment();
		segment.setIndex(index);
		segment.setP1(new Project(new World(lastY(), index * segmentLength), new Camera(), new Screen()));
		segment.setP2(new Project(new World(y, (index + 1) * segmentLength), new Camera(), new Screen()));
		segment.setCurve(curve);
		segment.setSprites(new ArrayList<Sprite>());
		segment.setCars(new ArrayList<Car>());
		segment.setColors((Math.floor((double) index / rumbleLength) % 2 != 0)
				? (TrafficColors) Constants.COLORS.get("DARK") : (TrafficColors) Constants.COLORS.get("LIGHT"));
		segments.add(segment);
	}

	private void addSprite(int index, Rectangle sprite, double offset) {
		println("addSprite() : " + index + ", " + sprite + ", " + offset);

		segments.get(index).getSprites().add(new Sprite(index, sprite, offset));
	}

	private void addRoad(int enter, int hold, double leave, double curve, double y) {
		println("addRoad : " + enter + ", " + hold + ", " + leave + ", " + curve + ", " + y);

		double startY = lastY();
		double endY = startY + (Util.toInt((int) y, 0) * segmentLength);
		double total = enter + hold + leave;

		for (int i = 0; i < enter; i++) {
			addSegment(Util.easeIn(0, curve, i / (double) enter), Util.easeInOut(startY, endY, i / total));
		}

		for (int i = 0; i < hold; i++) {
			addSegment(curve, Util.easeInOut(startY, endY, (enter + i) / total));
		}

		for (int i = 0; i < leave; i++) {
			addSegment(Util.easeInOut(curve, 0, i / leave), Util.easeInOut(startY, endY, (enter + hold + i) / total));
		}
	}

	private void addStraight() {
		int num = Constants.ROAD.get("LENGTH").get("MEDIUM");
		addStraight(num);
	}

	private void addStraight(int num) {
		println("addStraight: " + num);

		addRoad(num, num, num, 0, 0);
	}

	private void addHill(int num, double height) {
		println("addHill: " + num + ", " + height);

		addRoad(num, num, num, 0, height);
	}

	private void addCurve(int num, int curve, double height) {
		println("addCurve: " + num + ", " + curve + ", " + height);

		addRoad(num, num, num, curve, height);
	}

	private void addLowRollingHills() {
		int num = Constants.ROAD.get("LENGTH").get("SHORT");
		int height = Constants.ROAD.get("HILL").get("LOW");
		addLowRollingHills(num, height);
	}

	private void addLowRollingHills(int num, double height) {
		println("addLowRollingHills: " + num + ", " + height);

		addRoad(num, num, num, 0, height / 2);
		addRoad(num, num, num, 0, -height);
		addRoad(num, num, num, Constants.ROAD.get("CURVE").get("EASY"), height);
		addRoad(num, num, num, 0, 0);
		addRoad(num, num, num, -Constants.ROAD.get("CURVE").get("EASY"), height / 2);
		addRoad(num, num, num, 0, 0);
	}

	private void addSCurves() {
		println("addCurves");

		addRoad(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("LENGTH").get("MEDIUM"),
				Constants.ROAD.get("LENGTH").get("MEDIUM"), -Constants.ROAD.get("CURVE").get("EASY"),
				Constants.ROAD.get("HILL").get("NONE"));
		addRoad(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("LENGTH").get("MEDIUM"),
				Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("MEDIUM"));
		addRoad(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("LENGTH").get("MEDIUM"),
				Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("CURVE").get("EASY"),
				-Constants.ROAD.get("HILL").get("LOW"));
		addRoad(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("LENGTH").get("MEDIUM"),
				Constants.ROAD.get("LENGTH").get("MEDIUM"), -Constants.ROAD.get("CURVE").get("EASY"),
				Constants.ROAD.get("HILL").get("MEDIUM"));
		addRoad(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("LENGTH").get("MEDIUM"),
				Constants.ROAD.get("LENGTH").get("MEDIUM"), -Constants.ROAD.get("CURVE").get("MEDIUM"),
				-Constants.ROAD.get("HILL").get("MEDIUM"));
	}

	private void addBumps() {
		println("addBumps");

		addRoad(10, 10, 10, 0, 5);
		addRoad(10, 10, 10, 0, -2);
		addRoad(10, 10, 10, 0, -5);
		addRoad(10, 10, 10, 0, 8);
		addRoad(10, 10, 10, 0, 5);
		addRoad(10, 10, 10, 0, -7);
		addRoad(10, 10, 10, 0, 5);
		addRoad(10, 10, 10, 0, -2);
	}

	private void addDownhillToEnd() {
		addDownhillToEnd(200);
	}

	private void addDownhillToEnd(int num) {
		addRoad(num, num, num, -Constants.ROAD.get("CURVE").get("EASY"), -lastY() / segmentLength);
	}

	public void resetRoad() {
		println("resetRoad");

		segments = new ArrayList<>();

		addStraight(Constants.ROAD.get("LENGTH").get("SHORT"));
		addLowRollingHills();
		addSCurves();
		addCurve(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("LOW"));
		addBumps();
		addLowRollingHills();
		addCurve(Constants.ROAD.get("LENGTH").get("LONG") * 2, Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("MEDIUM"));
		addStraight();
		addHill(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("HILL").get("HIGH"));
		addSCurves();
		addCurve(Constants.ROAD.get("LENGTH").get("LONG"), -Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("NONE"));
		addHill(Constants.ROAD.get("LENGTH").get("LONG"), Constants.ROAD.get("HILL").get("HIGH"));
		addCurve(Constants.ROAD.get("LENGTH").get("LONG"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				-Constants.ROAD.get("HILL").get("LOW"));
		addBumps();
		addHill(Constants.ROAD.get("LENGTH").get("LONG"), -Constants.ROAD.get("HILL").get("MEDIUM"));
		addStraight();
		addSCurves();
		addDownhillToEnd();

		resetSprites();
		resetCars();

		segments.get(findSegment(playerZ).getIndex() + 2).setColors((TrafficColors) Constants.COLORS.get("START"));
		segments.get(findSegment(playerZ).getIndex() + 3).setColors((TrafficColors) Constants.COLORS.get("START"));
		for (int n = 0; n < rumbleLength; n++)
			segments.get(segments.size() - 1 - n).setColors((TrafficColors) Constants.COLORS.get("FINISH"));

		trackLength = segments.size() * segmentLength;
	}

	private void resetSprites() {
		println("resetSprite");

		addSprite(20, Constants.SPRITES.get("BILLBOARD07"), -1);
		addSprite(40, Constants.SPRITES.get("BILLBOARD06"), -1);
		addSprite(60, Constants.SPRITES.get("BILLBOARD08"), -1);
		addSprite(80, Constants.SPRITES.get("BILLBOARD09"), -1);
		addSprite(100, Constants.SPRITES.get("BILLBOARD01"), -1);
		addSprite(120, Constants.SPRITES.get("BILLBOARD02"), -1);
		addSprite(140, Constants.SPRITES.get("BILLBOARD03"), -1);
		addSprite(160, Constants.SPRITES.get("BILLBOARD04"), -1);
		addSprite(180, Constants.SPRITES.get("BILLBOARD05"), -1);

		addSprite(240, Constants.SPRITES.get("BILLBOARD07"), -1.2);
		addSprite(240, Constants.SPRITES.get("BILLBOARD06"), 1.2);
		addSprite(segments.size() - 25, Constants.SPRITES.get("BILLBOARD07"), -1.2);
		addSprite(segments.size() - 25, Constants.SPRITES.get("BILLBOARD06"), 1.2);

		for (int n = 10; n < 200; n += 4 + Math.floor((double) n / 100)) {
			addSprite(n, Constants.SPRITES.get("PALM_TREE"), 0.5 + Math.random() * 0.5);
			addSprite(n, Constants.SPRITES.get("PALM_TREE"), 1 + Math.random() * 2);
		}

		for (int n = 250; n < 1000; n += 5) {
			addSprite(n, Constants.SPRITES.get("COLUMN"), 1.1);
			addSprite(n + Util.randomInt(0, 5), Constants.SPRITES.get("TREE1"), -1 - (Math.random() * 2));
			addSprite(n + Util.randomInt(0, 5), Constants.SPRITES.get("TREE2"), -1 - (Math.random() * 2));
		}

		for (int n = 200; n < segments.size(); n += 3) {
			addSprite(n, Util.randomChoice(Constants.SPRITES_PLANTS),
					Util.randomChoice(new int[] { 1, -1 }) * (2 + Math.random() * 5));
		}

		double side, offset;
		Rectangle sprite;
		for (int n = 1000; n < (segments.size() - 50); n += 100) {
			side = Util.randomChoice(new int[] { 1, -1 });
			addSprite(n + Util.randomInt(0, 50), Util.randomChoice(Constants.SPRITES_BILLBOARDS), -side);
			for (int i = 0; i < 20; i++) {
				sprite = Util.randomChoice(Constants.SPRITES_PLANTS);
				offset = side * (1.5 + Math.random());
				addSprite(n + Util.randomInt(0, 50), sprite, offset);
			}
		}
	}

	private void resetCars() {
		println("resetCars");

		cars = new ArrayList<>();
		Car car;
		Segment segment;
		Sprite sprite = new Sprite();
		double z;
		double offset, speed;
		for (int n = 0; n < totalCars; n++) {
			offset = Math.random() * Util.randomChoice(new double[] { -0.8, 0.8 });
			z = (Math.floor(Math.random() * segments.size()) * segmentLength);
			sprite.setSource(Util.randomChoice(Constants.SPRITES_CARS));
			speed = maxSpeed / 4
					+ Math.random() * maxSpeed / (sprite.getSource() == Constants.SPRITES.get("SEMI") ? 4 : 2);
			car = new Car();
			car.setOffset(offset);
			car.setZ(z);
			car.setSprite(sprite);
			car.setSpeed(speed);
			segment = findSegment(car.getZ());
			segment.getCars().add(car);
			cars.add(car);
		}
	}

	private void updateHud(String key, String value) {
		// if (!hud.get(key).equals(value)) {
		// hud.put(key, value);
		// }
	}

	private String formatTime(int dt) {
		int minutes = (int) Math.floor(dt / 60);
		int seconds = (int) Math.floor(dt - (minutes * 60));
		int tenths = (int) Math.floor(10 * (dt - Math.floor(dt)));
		if (minutes > 0)
			return minutes + "." + (seconds < 10 ? "0" : "") + seconds + "." + tenths;
		else
			return seconds + "." + tenths;
	}

	public void render(TrafficRender render) {
		//System.out.println("render");

		Segment baseSegment = findSegment(position);
		double basePercent = Util.percentRemaining((int) position, segmentLength);
		Segment playerSegment = findSegment(position + playerZ);
		double playerPercent = Util.percentRemaining((int) (position + playerZ), segmentLength);
		double playerY = Util.interpolate(playerSegment.getP1().getWorld().getY(),
				playerSegment.getP2().getWorld().getY(), playerPercent);
		double maxy = height;

		double x = 0;
		double dx = -(baseSegment.getCurve() * basePercent);

		BufferedImage tbg = TrafficFrame.getInstance().background;
		render.background(tbg, width, height, Constants.BACKGROUND.get("SKY"), skyOffset,
				resolution * skySpeed * playerY);
		render.background(tbg, width, height, Constants.BACKGROUND.get("HILLS"), hillOffset,
				resolution * hillSpeed * playerY);
		render.background(tbg, width, height, Constants.BACKGROUND.get("TREES"), treeOffset,
				resolution * treeSpeed * playerY);

		Segment segment;
		Car car;
		Sprite sprite;
		double spriteScale, spriteX, spriteY;

		for (int n = 0; n < drawDistance; n++) {
			segment = segments.get((int) ((baseSegment.getIndex() + n) % segments.size()));
			segment.setLooped(segment.getIndex() < baseSegment.getIndex());
			segment.setFog((int) Util.exponentialFog(n / drawDistance, fogDensity));
			segment.setClip(maxy);

			Util.project(segment.getP1(), ((playerX * roadWidth) - x), playerY + cameraHeight,
					(position - (segment.isLooped() ? trackLength : 0)), cameraDepth, width, height, roadWidth);
			Util.project(segment.getP2(), ((playerX * roadWidth) - x - dx), playerY + cameraHeight,
					(position - (segment.isLooped() ? trackLength : 0)), cameraDepth, width, height, roadWidth);

			x = x + dx;
			dx = dx + segment.getCurve();

			if ((segment.getP1().getCamera().getZ() <= cameraDepth)
					|| (segment.getP2().getScreen().getY() >= segment.getP1().getScreen().getY())
					|| (segment.getP2().getScreen().getY() >= maxy))
				continue;

			render.segment(width, lanes, segment.getP1().getScreen().getX(), segment.getP1().getScreen().getY(),
					segment.getP1().getScreen().getW(), segment.getP2().getScreen().getX(),
					segment.getP2().getScreen().getY(), segment.getP2().getScreen().getW(), segment.getFog(),
					segment.getColors());

			maxy = segment.getP1().getScreen().getY();
		}

		for (int n = (drawDistance - 1); n > 0; n--) {
			segment = segments.get((baseSegment.getIndex() + n) % segments.size());

			for (int i = 0; i < segment.getCars().size(); i++) {
				car = segment.getCars().get(i);
				sprite = car.getSprite();
				spriteScale = Util.interpolate(segment.getP1().getScreen().getScale(),
						segment.getP2().getScreen().getScale(), car.getPercent());
				spriteX = Util.interpolate(segment.getP1().getScreen().getX(), segment.getP2().getScreen().getX(),
						car.getPercent()) + (spriteScale * car.getOffset() * roadWidth * width / 2);
				spriteY = Util.interpolate(segment.getP1().getScreen().getY(), segment.getP2().getScreen().getY(),
						car.getPercent());
				render.sprite(width, height, resolution, roadWidth, sprites, car.getSprite().getSource(), spriteScale,
						spriteX, spriteY, -0.5, -1, segment.getClip());
			}

			for (int i = 0; i < segment.getSprites().size(); i++) {
				sprite = segment.getSprites().get(i);
				spriteScale = segment.getP1().getScreen().getScale();
				spriteX = segment.getP1().getScreen().getX()
						+ (spriteScale * sprite.getOffset() * roadWidth * width / 2);
				spriteY = segment.getP1().getScreen().getY();
				render.sprite(width, height, resolution, roadWidth, sprites, sprite.getSource(), spriteScale, spriteX,
						spriteY, (sprite.getOffset() < 0 ? -1 : 0), -1, segment.getClip());
			}

			if (segment == playerSegment) {
				render.player(width, height, resolution, roadWidth, sprites, speed / maxSpeed, cameraDepth / playerZ,
						width / 2,
						(height / 2) - (cameraDepth / playerZ
								* Util.interpolate(playerSegment.getP1().getCamera().getY(),
										playerSegment.getP2().getCamera().getY(), playerPercent)
								* height / 2),
						speed * (keyLeft ? -1 : keyRight ? 1 : 0),
						(double) (playerSegment.getP2().getWorld().getY() - playerSegment.getP1().getWorld().getY()));
			}
		}

		render.repaint();
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	public int getTotalCars() {
		return totalCars;
	}

	public void setTotalCars(int totalCars) {
		this.totalCars = totalCars;
	}

	public long getCurrentLapTime() {
		return currentLapTime;
	}

	public void setCurrentLapTime(long currentLapTime) {
		this.currentLapTime = currentLapTime;
	}

	public long getLastLapTime() {
		return lastLapTime;
	}

	public void setLastLapTime(long lastLapTime) {
		this.lastLapTime = lastLapTime;
	}

	public Map<String, String> getHud() {
		return hud;
	}

	public void setHud(Map<String, String> hud) {
		this.hud = hud;
	}
}
