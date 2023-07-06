package com.kuai.traffic.model;

public class Project {
	private Camera camera;
	private Screen screen;
	private World world;

	public Project(World world, Camera camera, Screen screen) {
		this.world = world;
		this.camera = camera;
		this.screen = screen;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public String toString() {
		return "Project [camera=" + camera + ", screen=" + screen + ", world=" + world + "]";
	}
}
