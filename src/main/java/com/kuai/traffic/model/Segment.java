package com.kuai.traffic.model;

import java.util.ArrayList;
import java.util.List;

public class Segment {
	private int index;
	private Project p1;
	private Project p2;
	private double curve;
	private List<Sprite> sprites = new ArrayList<>();
	private List<Car> cars = new ArrayList<>();
	private boolean looped = true;
	private int fog;
	private double clip;
	private TrafficColors colors;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Project getP1() {
		return p1;
	}

	public void setP1(Project p1) {
		this.p1 = p1;
	}

	public Project getP2() {
		return p2;
	}

	public void setP2(Project p2) {
		this.p2 = p2;
	}

	public double getCurve() {
		return curve;
	}

	public void setCurve(double curve) {
		this.curve = curve;
	}

	public List<Sprite> getSprites() {
		return sprites;
	}

	public void setSprites(List<Sprite> sprites) {
		this.sprites = sprites;
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	public boolean isLooped() {
		return looped;
	}

	public void setLooped(boolean looped) {
		this.looped = looped;
	}

	public int getFog() {
		return fog;
	}

	public void setFog(int fog) {
		this.fog = fog;
	}

	public double getClip() {
		return clip;
	}

	public void setClip(double clip) {
		this.clip = clip;
	}

	public TrafficColors getColors() {
		return colors;
	}

	public void setColors(TrafficColors colors) {
		this.colors = colors;
	}

	@Override
	public String toString() {
		return "Segment [index=" + index + ", p1=" + p1 + ", p2=" + p2 + ", curve=" + curve + ", sprites=" + sprites
				+ ", cars=" + cars + ", looped=" + looped + ", fog=" + fog + ", clip=" + clip + "]";
	}
}
