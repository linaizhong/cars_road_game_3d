package com.kuai.traffic.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.kuai.traffic.TrafficFrame;

public class RoadConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public RoadConfigurationDialog(JFrame parent, String title, String message) {
		super(parent, title);

		JPanel slidersPanel = new JPanel(new GridLayout(12, 1));

		JLabel jLabel = new JLabel("    Lanes:");
		JSlider lanes = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
		lanes.setMinorTickSpacing(1);
		lanes.setMajorTickSpacing(1);
		lanes.setPaintTicks(true);
		lanes.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(lanes, BorderLayout.CENTER);
		lanes.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setLanes(lanes.getValue());
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		jLabel = new JLabel("    Road Width:");
		JSlider roadWidth = new JSlider(JSlider.HORIZONTAL, 0, 100, 60);
		roadWidth.setMinorTickSpacing(2);
		roadWidth.setMajorTickSpacing(10);
		roadWidth.setPaintTicks(true);
		roadWidth.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(roadWidth, BorderLayout.CENTER);
		roadWidth.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setRoadWidth(500 + roadWidth.getValue() * 25);
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		jLabel = new JLabel("    Camera Height:");
		JSlider cameraHeight = new JSlider(JSlider.HORIZONTAL, 0, 100, 22);
		cameraHeight.setMinorTickSpacing(2);
		cameraHeight.setMajorTickSpacing(10);
		cameraHeight.setPaintTicks(true);
		cameraHeight.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(cameraHeight, BorderLayout.CENTER);
		cameraHeight.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setCameraHeight(500 + cameraHeight.getValue() * 45);
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		jLabel = new JLabel("    View Depth:");
		JSlider viewDepth = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		viewDepth.setMinorTickSpacing(2);
		viewDepth.setMajorTickSpacing(10);
		viewDepth.setPaintTicks(true);
		viewDepth.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(viewDepth, BorderLayout.CENTER);
		viewDepth.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setCameraDepth(100 + viewDepth.getValue() * 4);
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		jLabel = new JLabel("    View Width:");
		JSlider viewWidth = new JSlider(JSlider.HORIZONTAL, 0, 100, 33);
		viewWidth.setMinorTickSpacing(2);
		viewWidth.setMajorTickSpacing(10);
		viewWidth.setPaintTicks(true);
		viewWidth.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(viewWidth, BorderLayout.CENTER);
		viewWidth.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setFieldOfView(80 + (int) (viewWidth.getValue() * 0.6));
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		jLabel = new JLabel("    Fog Density:");
		JSlider fogDensity = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
		fogDensity.setMinorTickSpacing(2);
		fogDensity.setMajorTickSpacing(10);
		fogDensity.setPaintTicks(true);
		fogDensity.setPaintLabels(true);
		slidersPanel.add(jLabel, BorderLayout.WEST);
		slidersPanel.add(fogDensity, BorderLayout.CENTER);
		fogDensity.addChangeListener(e -> {
			TrafficFrame.getInstance().getGame().setExit(true);
			try {
				Thread.sleep(200);
				TrafficFrame.getInstance().getTrafficModel().setFogDensity((int) (fogDensity.getValue() * 0.5));
				TrafficFrame.getInstance().getGame().refresh();
			} catch(Exception ex) {}
		});

		TrafficFrame.getInstance().getTrafficModel().setLanes(3);
		TrafficFrame.getInstance().getTrafficModel().setRoadWidth(2000);
		TrafficFrame.getInstance().getTrafficModel().setCameraHeight(1000);
		TrafficFrame.getInstance().getTrafficModel().setCameraDepth(300);
		TrafficFrame.getInstance().getTrafficModel().setFieldOfView(100);
		TrafficFrame.getInstance().getTrafficModel().setFogDensity(5);
		
		getContentPane().add(slidersPanel);
		
		setSize(480, 680);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		//pack();
		setVisible(true);
	}
}
