package com.kuai.traffic.model;

public class World {
	private double x;
	private double y;
	private double z;

	public World(double y, double z) {
		this.y = y;
		this.z = z;
	}

	public World(double z) {
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "World [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
