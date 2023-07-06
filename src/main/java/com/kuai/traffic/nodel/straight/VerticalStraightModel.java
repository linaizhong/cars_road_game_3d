package com.kuai.traffic.nodel.straight;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.kuai.traffic.TrafficFrame;
import com.kuai.traffic.model.Camera;
import com.kuai.traffic.model.Constants;
import com.kuai.traffic.model.Project;
import com.kuai.traffic.model.Screen;
import com.kuai.traffic.model.Segment;
import com.kuai.traffic.model.TrafficColors;
import com.kuai.traffic.model.TrafficModel;
import com.kuai.traffic.model.TrafficRender;
import com.kuai.traffic.model.World;
import com.kuai.traffic.util.Util;

public class VerticalStraightModel extends TrafficModel {
	private Logger log = Logger.getLogger(VerticalStraightModel.class);
	
	public VerticalStraightModel() {
		super();
	}

	public void update(double dt) {
		position = Util.increase(position, dt * speed, trackLength);

		double dx = dt * 2 * (speed / maxSpeed); // at top speed, should be able
													// to cross from left to
													// right (-1 to 1) in 1
													// second

		if (keyLeft)
			playerX = playerX - dx;
		else if (keyRight)
			playerX = playerX + dx;

		if (keyFaster)
			speed = Util.accelerate(speed, accel, dt);
		else if (keySlower)
			speed = Util.accelerate(speed, breaking, dt);
		else
			speed = Util.accelerate(speed, decel, dt);

		if (((playerX < -1) || (playerX > 1)) && (speed > offRoadLimit))
			speed = Util.accelerate(speed, offRoadDecel, dt);

		playerX = Util.limit((int) playerX, -2, 2);         // dont ever let player go too far out of bounds
		speed = Util.limit((int) speed, 0, (int) maxSpeed); // or exceed maxSpeed
	}

	public void render(TrafficRender render) {
		log.info("render");
		
		Segment baseSegment = findSegment(position);
		log.info("render::baseSegment::" + baseSegment.toString());
		
		double maxy = height;

		BufferedImage tbg = TrafficFrame.getInstance().background;
		render.background(tbg, width, height, Constants.BACKGROUND.get("SKY"));
		render.background(tbg, width, height, Constants.BACKGROUND.get("HILLS"));
		render.background(tbg, width, height, Constants.BACKGROUND.get("TREES"));

		Segment segment;
		for (int n = 0; n < drawDistance; n++) {
			segment = segments.get((baseSegment.getIndex() + n) % segments.size());
			segment.setLooped(segment.getIndex() < baseSegment.getIndex());
			segment.setFog((int) Util.exponentialFog(n / drawDistance, fogDensity));

			Util.project(segment.getP1(), 
			    (playerX * roadWidth), 
			    cameraHeight,
					position - (segment.isLooped() ? trackLength : 0), 
					cameraDepth, 
					width, 
					height, 
					roadWidth);
			Util.project(segment.getP2(), 
			    (playerX * roadWidth), 
			    cameraHeight,
					position - (segment.isLooped() ? trackLength : 0), 
					cameraDepth, 
					width, 
					height, 
					roadWidth);

			log.info("render::segment: " + segment.toString());
			
			if ((segment.getP1().getCamera().getZ() <= cameraDepth) || (segment.getP2().getScreen().getY() >= maxy))
				continue;

			render.segment(width, lanes, 
			    segment.getP1().getScreen().getX(), 
			    segment.getP1().getScreen().getY(),
					segment.getP1().getScreen().getW(), 
					segment.getP2().getScreen().getX(),
					segment.getP2().getScreen().getY(), 
					segment.getP2().getScreen().getW(), 
					segment.getFog(),
					segment.getColors());

			maxy = segment.getP2().getScreen().getY();
		}

		render.player(width, height, resolution, roadWidth, sprites, speed / maxSpeed, cameraDepth / playerZ, width / 2,
				height, speed * (keyLeft ? -1 : keyRight ? 1 : 0), 0);

		render.repaint();
	}

	public void resetRoad() {
		segments = new ArrayList<>();
		for (int n = 0; n < 500; n++) {
			Segment segment = new Segment();
			segment.setIndex(n);
			segment.setP1(new Project(new World(n * segmentLength), new Camera(), new Screen()));
			segment.setP2(new Project(new World((n + 1) * segmentLength), new Camera(), new Screen()));
			segment.setColors((Math.floor(n / rumbleLength) % 2 != 0) ? (TrafficColors) Constants.COLORS.get("DARK")
					: (TrafficColors) Constants.COLORS.get("LIGHT"));
			segments.add(segment);
		}

		segments.get(findSegment(playerZ).getIndex() + 2).setColors((TrafficColors) Constants.COLORS.get("START"));
		segments.get(findSegment(playerZ).getIndex() + 3).setColors((TrafficColors) Constants.COLORS.get("START"));

		for (int n = 0; n < rumbleLength; n++) {
			segments.get(segments.size() - 1 - n).setColors((TrafficColors) Constants.COLORS.get("FINISH"));
		}

		trackLength = segments.size() * segmentLength;
		
//		for(int i=0; i<segments.size(); i++) {
//			log.info("resetRoad::segment: " + segments.get(i).toString());
//		}
	}
}
