package com.kuai.traffic.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.kuai.traffic.TrafficFrame;

public abstract class TrafficModel {
	protected static boolean DEBUG = false;

	protected int fps = 60;
	protected double step = 1 / (float) fps;
	protected int width = 1024;
	protected int height = 768;

	protected double centrifugal = 0.3;
	protected double skySpeed = 0.001;
	protected double hillSpeed = 0.002;
	protected double treeSpeed = 0.003;
	
	protected double skyOffset = 0;
	protected double hillOffset = 0;
	protected double treeOffset = 0;

	protected List<Segment> segments = new ArrayList<>();

	protected Stats stats;

	protected BufferedImage background;
	protected BufferedImage sprites;

	protected double resolution;

	protected double roadWidth = 2000;
	protected int segmentLength = 200;
	protected int rumbleLength = 3;
	protected double trackLength = 0;

	protected int lanes = 3;
	protected int fieldOfView = 100;
	// protected int cameraHeight = 1000;
	protected int cameraHeight = 5000;
	protected double cameraDepth = 0;
	protected int drawDistance = 300;

	protected double playerX = 0;
  protected double playerY = 0;
	protected double playerZ = 0;
	protected int fogDensity = 5;
	protected double position = 0;
	protected double speed = 0;
	protected double maxSpeed = segmentLength / step;
	protected double accel = maxSpeed / 5;
	protected double breaking = -maxSpeed;
	protected double decel = -maxSpeed / 5;
	protected double offRoadDecel = -maxSpeed / 2;
	protected double offRoadLimit = maxSpeed / 4;

	protected boolean keyLeft = false;
	protected boolean keyRight = false;
	protected boolean keyFaster = false;
	protected boolean keySlower = false;

	public TrafficModel() {
		stats = new Stats();

		background = TrafficFrame.getInstance().background;
		sprites = TrafficFrame.getInstance().sprites;
	}

	protected void println(String str) {
		if (DEBUG) {
			System.out.println(str);
		}
	}

	protected Segment findSegment(double z) {
		return segments.get((int) (Math.floor(z / segmentLength) % segments.size()));
	}

	public void reset() {
		cameraDepth = 1 / Math.tan((fieldOfView / 2) * Math.PI / 180);
		playerZ = (cameraHeight * cameraDepth);
		resolution = height / 480;
		refreshTweakUI();

		if (segments.size() == 0)
			resetRoad();
	}

	private void refreshTweakUI() {
	}

	public abstract void update(double dt);

	public abstract void render(TrafficRender render);

	public abstract void resetRoad();

	public static boolean isDEBUG() {
		return DEBUG;
	}

	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getCentrifugal() {
		return centrifugal;
	}

	public void setCentrifugal(double centrifugal) {
		this.centrifugal = centrifugal;
	}

	public double getSkySpeed() {
		return skySpeed;
	}

	public void setSkySpeed(double skySpeed) {
		this.skySpeed = skySpeed;
	}

	public double getHillSpeed() {
		return hillSpeed;
	}

	public void setHillSpeed(double hillSpeed) {
		this.hillSpeed = hillSpeed;
	}

	public double getTreeSpeed() {
		return treeSpeed;
	}

	public void setTreeSpeed(double treeSpeed) {
		this.treeSpeed = treeSpeed;
	}

	public double getSkyOffset() {
		return skyOffset;
	}

	public void setSkyOffset(double skyOffset) {
		this.skyOffset = skyOffset;
	}

	public double getHillOffset() {
		return hillOffset;
	}

	public void setHillOffset(double hillOffset) {
		this.hillOffset = hillOffset;
	}

	public double getTreeOffset() {
		return treeOffset;
	}

	public void setTreeOffset(double treeOffset) {
		this.treeOffset = treeOffset;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	public BufferedImage getBackground() {
		return background;
	}

	public void setBackground(BufferedImage background) {
		this.background = background;
	}

	public BufferedImage getSprites() {
		return sprites;
	}

	public void setSprites(BufferedImage sprites) {
		this.sprites = sprites;
	}

	public double getResolution() {
		return resolution;
	}

	public void setResolution(double resolution) {
		this.resolution = resolution;
	}

	public double getRoadWidth() {
		return roadWidth;
	}

	public void setRoadWidth(double roadWidth) {
		this.roadWidth = roadWidth;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

	public int getRumbleLength() {
		return rumbleLength;
	}

	public void setRumbleLength(int rumbleLength) {
		this.rumbleLength = rumbleLength;
	}

	public double getTrackLength() {
		return trackLength;
	}

	public void setTrackLength(double trackLength) {
		this.trackLength = trackLength;
	}

	public int getLanes() {
		return lanes;
	}

	public void setLanes(int lanes) {
		this.lanes = lanes;
	}

	public int getFieldOfView() {
		return fieldOfView;
	}

	public void setFieldOfView(int fieldOfView) {
		this.fieldOfView = fieldOfView;
	}

	public int getCameraHeight() {
		return cameraHeight;
	}

	public void setCameraHeight(int cameraHeight) {
		this.cameraHeight = cameraHeight;
	}

	public double getCameraDepth() {
		return cameraDepth;
	}

	public void setCameraDepth(double cameraDepth) {
		this.cameraDepth = cameraDepth;
	}

	public int getDrawDistance() {
		return drawDistance;
	}

	public void setDrawDistance(int drawDistance) {
		this.drawDistance = drawDistance;
	}

	public double getPlayerX() {
		return playerX;
	}

	public void setPlayerX(double playerX) {
		this.playerX = playerX;
	}

	public double getPlayerZ() {
		return playerZ;
	}

	public void setPlayerZ(double playerZ) {
		this.playerZ = playerZ;
	}

	public int getFogDensity() {
		return fogDensity;
	}

	public void setFogDensity(int fogDensity) {
		this.fogDensity = fogDensity;
	}

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getAccel() {
		return accel;
	}

	public void setAccel(double accel) {
		this.accel = accel;
	}

	public double getBreaking() {
		return breaking;
	}

	public void setBreaking(double breaking) {
		this.breaking = breaking;
	}

	public double getDecel() {
		return decel;
	}

	public void setDecel(double decel) {
		this.decel = decel;
	}

	public double getOffRoadDecel() {
		return offRoadDecel;
	}

	public void setOffRoadDecel(double offRoadDecel) {
		this.offRoadDecel = offRoadDecel;
	}

	public double getOffRoadLimit() {
		return offRoadLimit;
	}

	public void setOffRoadLimit(double offRoadLimit) {
		this.offRoadLimit = offRoadLimit;
	}

	public boolean isKeyLeft() {
		return keyLeft;
	}

	public void setKeyLeft(boolean keyLeft) {
		this.keyLeft = keyLeft;
	}

	public boolean isKeyRight() {
		return keyRight;
	}

	public void setKeyRight(boolean keyRight) {
		this.keyRight = keyRight;
	}

	public boolean isKeyFaster() {
		return keyFaster;
	}

	public void setKeyFaster(boolean keyFaster) {
		this.keyFaster = keyFaster;
	}

	public boolean isKeySlower() {
		return keySlower;
	}

	public void setKeySlower(boolean keySlower) {
		this.keySlower = keySlower;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public double getStep() {
		return step;
	}

	public Stats getStats() {
		return stats;
	}

  public double getPlayerY() {
    return playerY;
  }
}
