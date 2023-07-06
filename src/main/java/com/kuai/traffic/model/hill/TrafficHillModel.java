package com.kuai.traffic.model.hill;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

public class TrafficHillModel extends TrafficModel {
	public TrafficHillModel() {
		super();
	}

	public void update(double dt) {
		Segment playerSegment = findSegment(position + playerZ);
		double speedPercent = speed / maxSpeed;
		double dx = dt * 2 * speedPercent; // at top speed, should be able to
											// cross from left to right (-1 to
											// 1) in 1 second

		position = Util.increase(position, dt * speed, trackLength);

		skyOffset = Util.increase(skyOffset, skySpeed * playerSegment.getCurve() * speedPercent, 1);
		hillOffset = Util.increase(hillOffset, hillSpeed * playerSegment.getCurve() * speedPercent, 1);
		treeOffset = Util.increase(treeOffset, treeSpeed * playerSegment.getCurve() * speedPercent, 1);

		if (keyLeft)
			playerX = playerX - dx;
		else if (keyRight)
			playerX = playerX + dx;

		playerX = playerX - (dx * speedPercent * playerSegment.getCurve() * centrifugal);

		if (keyFaster)
			speed = Util.accelerate(speed, accel, dt);
		else if (keySlower)
			speed = Util.accelerate(speed, breaking, dt);
		else
			speed = Util.accelerate(speed, decel, dt);

		if (((playerX < -1) || (playerX > 1)) && (speed > offRoadLimit))
			speed = Util.accelerate(speed, offRoadDecel, dt);

		playerX = Util.limit((int) playerX, -2, 2); // dont ever let it go too
													// far out of bounds
		speed = Util.limit((int) speed, 0, (int) maxSpeed); // or exceed
															// maxSpeed
	}

	public void render(TrafficRender render) {
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
		for (int n = 0; n < drawDistance; n++) {
			segment = segments.get((baseSegment.getIndex() + n) % segments.size());
			segment.setLooped(segment.getIndex() < baseSegment.getIndex());
			segment.setFog((int) Util.exponentialFog(n / drawDistance, fogDensity));

			Util.project(segment.getP1(), (playerX * roadWidth) - x, playerY + cameraHeight,
					position - (segment.isLooped() ? trackLength : 0), cameraDepth, width, height, roadWidth);
			Util.project(segment.getP2(), (playerX * roadWidth) - x - dx, playerY + cameraHeight,
					position - (segment.isLooped() ? trackLength : 0), cameraDepth, width, height, roadWidth);

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

			maxy = segment.getP2().getScreen().getY();
		}

		render.player(width, height, resolution, roadWidth, sprites, speed / maxSpeed, cameraDepth / playerZ, width / 2,
				(height / 2) - (cameraDepth / playerZ
						* Util.interpolate(playerSegment.getP1().getCamera().getY(),
								playerSegment.getP2().getCamera().getY(), playerPercent)
						* height / 2),
				speed * (keyLeft ? -1 : keyRight ? 1 : 0),
				playerSegment.getP2().getWorld().getY() - playerSegment.getP1().getWorld().getY());

		render.repaint();
	}

	private double lastY() {
		return (segments.size() == 0) ? 0 : segments.get(segments.size() - 1).getP2().getWorld().getY();
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

	private void addRoad(int enter, int hold, int leave, double curve, double y) {
		double startY = lastY();
		double endY = startY + (Util.toInt((int) y, 0) * segmentLength);
		double total = enter + hold + leave;
		for (int n = 0; n < enter; n++)
			addSegment(Util.easeIn(0, curve, n / enter), Util.easeInOut(startY, endY, n / total));
		for (int n = 0; n < hold; n++)
			addSegment(curve, Util.easeInOut(startY, endY, (enter + n) / total));
		for (int n = 0; n < leave; n++)
			addSegment(Util.easeInOut(curve, 0, n / leave), Util.easeInOut(startY, endY, (enter + hold + n) / total));
	}

	private void addStraight() {
		int num = Constants.ROAD.get("LENGTH").get("MEDIUM");
		addStraight(num);
	}

	private void addStraight(int num) {
		addRoad(num, num, num, 0, 0);
	}

	private void addHill(int num, double height) {
		addRoad(num, num, num, 0, height);
	}

	private void addCurve(int num, double curve, double height) {
		addRoad(num, num, num, curve, height);
	}

	private void addLowRollingHills() {
		int num = Constants.ROAD.get("LENGTH").get("SHORT");
		int height = Constants.ROAD.get("HILL").get("LOW");
		addLowRollingHills(num, height);
	}

	private void addLowRollingHills(int num, double height) {
		addRoad(num, num, num, 0, height / 2);
		addRoad(num, num, num, 0, -height);
		addRoad(num, num, num, 0, height);
		addRoad(num, num, num, 0, 0);
		addRoad(num, num, num, 0, height / 2);
		addRoad(num, num, num, 0, 0);
	}

	private void addDownhillToEnd() {
		int num = 200;
		addDownhillToEnd(num);
	}

	private void addDownhillToEnd(int num) {
		addRoad(num, num, num, -Constants.ROAD.get("CURVE").get("EASY"), -lastY() / segmentLength);
	}

	public void resetRoad() {
		segments = new ArrayList<>();

		addStraight(Constants.ROAD.get("LENGTH").get("SHORT") / 2);
		addHill(Constants.ROAD.get("LENGTH").get("SHORT"), Constants.ROAD.get("HILL").get("LOW"));
		addLowRollingHills();
		addCurve(Constants.ROAD.get("LENGTH").get("MEDIUM"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("LOW"));
		addLowRollingHills();
		addCurve(Constants.ROAD.get("LENGTH").get("LONG"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("MEDIUM"));
		addStraight();
		addCurve(Constants.ROAD.get("LENGTH").get("LONG"), -Constants.ROAD.get("CURVE").get("MEDIUM"),
				Constants.ROAD.get("HILL").get("MEDIUM"));
		addHill(Constants.ROAD.get("LENGTH").get("LONG"), Constants.ROAD.get("HILL").get("HIGH"));
		addCurve(Constants.ROAD.get("LENGTH").get("LONG"), Constants.ROAD.get("CURVE").get("MEDIUM"),
				-Constants.ROAD.get("HILL").get("LOW"));
		addHill(Constants.ROAD.get("LENGTH").get("LONG"), -Constants.ROAD.get("HILL").get("MEDIUM"));
		addStraight();
		addDownhillToEnd();

		segments.get(findSegment(playerZ).getIndex() + 2).setColors((TrafficColors) Constants.COLORS.get("START"));
		segments.get(findSegment(playerZ).getIndex() + 3).setColors((TrafficColors) Constants.COLORS.get("START"));
		for (int n = 0; n < rumbleLength; n++)
			segments.get(segments.size() - 1 - n).setColors((TrafficColors) Constants.COLORS.get("FINISH"));

		trackLength = segments.size() * segmentLength;
	}
}
