package com.kuai.traffic.model;

import com.kuai.traffic.TrafficFrame;
import com.kuai.traffic.util.Util;

public class Game implements Runnable {
	private double step;
	private long now, last;
	private double dt = 0;
	private double gdt = 0;
	private Stats stats;
	private TrafficRender render;

	private boolean exit = false;

	public Game() {
		refresh();
	}

	public void refresh() {
		render = TrafficFrame.getInstance().getTrafficRender();

		last = Util.timestamp();
		stats = TrafficFrame.getInstance().getTrafficModel().getStats();

		ready();

		step = TrafficFrame.getInstance().getTrafficModel().getStep();
		TrafficFrame.getInstance().getTrafficModel().update(step);
		
		forwardOneStep();
	}

	@Override
	public void run() {
		TrafficFrame.getInstance().getTrafficModel().setKeyFaster(true);

		while (!exit) {
			forwardOneStep();
			
			try {
				Thread.sleep((int) (1000 / TrafficFrame.getInstance().getTrafficModel().getFps()));
			} catch (Exception e) {
			}
		}

		TrafficFrame.getInstance().getTrafficModel().setKeyFaster(false);
	}

	public void forwardOneStep() {
		now = Util.timestamp();
		dt = Math.min(1, (now - last) / (double)1000);
		gdt = gdt + dt;

		//System.out.println("(dt: " + dt + ", gdt: " + gdt + ", last: " + last + ", now: " + now + ", step: " + step + ")");
		while (gdt > step) {
			gdt = gdt - step;

			TrafficFrame.getInstance().getTrafficModel().update(step);
		}

		TrafficFrame.getInstance().getTrafficModel().render(render);
		stats.update();
		last = now;
	}
	
	public void simulate() {
		exit = false;
		Thread t = new Thread(this);
		t.start();
	}
	
	public void loadImages() {

	}

	public void setKeyListener() {

	}

	public Stats stats() {
		return null;
	}

	public void playMusic() {

	}

	private void ready() {
		// System.out.println("ready");

		TrafficFrame.getInstance().getTrafficModel().reset();
	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}
}
