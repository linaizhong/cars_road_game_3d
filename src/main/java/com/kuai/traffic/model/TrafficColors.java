package com.kuai.traffic.model;

import java.awt.Color;

public class TrafficColors {
	private Color grass;
	private Color rumble;
	private Color road;
	private Color lane;

	public TrafficColors(Color r, Color g, Color ru, Color l) {
		this.grass = g;
		this.rumble = ru;
		this.road = r;
		this.lane = l;
	}

	public Color getGrass() {
		return grass;
	}

	public void setGrass(Color grass) {
		this.grass = grass;
	}

	public Color getRumble() {
		return rumble;
	}

	public void setRumble(Color rumble) {
		this.rumble = rumble;
	}

	public Color getRoad() {
		return road;
	}

	public void setRoad(Color road) {
		this.road = road;
	}

	public Color getLane() {
		return lane;
	}

	public void setLane(Color lane) {
		this.lane = lane;
	}

	@Override
	public String toString() {
		return "TrafficColors [grass=" + grass + ", rumble=" + rumble + ", road=" + road + ", lane=" + lane + "]";
	}
}
