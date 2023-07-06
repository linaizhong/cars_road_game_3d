package com.kuai.traffic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Logger;

import com.kuai.traffic.common.MessageConsole;
import com.kuai.traffic.dialogs.RoadConfigurationDialog;
import com.kuai.traffic.dialogs.TrafficConfigurationDialog;
import com.kuai.traffic.model.Game;
import com.kuai.traffic.model.TrafficModel;
import com.kuai.traffic.model.TrafficRender;
import com.kuai.traffic.model.curve.TrafficCurveModel;
import com.kuai.traffic.model.hill.TrafficHillModel;
import com.kuai.traffic.model.real.TrafficRealModel;
import com.kuai.traffic.model.road.HorizontalRender;
import com.kuai.traffic.nodel.straight.HorizontalStraightModel;

public class TrafficFrame implements Runnable {
	private Logger log = Logger.getLogger(TrafficFrame.class);
	
	public final static int WIDTH = 1048;
	public final static int HEIGHT = 864;

	private JFrame frame;

	private JPanel jpCanvasPanel;

	private int scale = 3;

	private JTree commandTree;
	static private JTextPane agentOutput;
	private JScrollPane scrollPane;

	private static TrafficFrame trafficApp = null;
	private JPanel modelPanel;
	private TrafficRender trafficRender = null;
	private TrafficModel trafficModel;

	private static JEditorPane outputPane;

	private JSplitPane vRightSplitPane;
	private JPanel contentPanel, commandTreePanel, deviceManagerTreePanel, transportLogPanel, systemLogPanel;

	private Game game;

	// private ImageIcon ICON_FILE;
	private ImageIcon ICON_CLEAR;
	private ImageIcon ICON_TOGGLE;
	private ImageIcon ICON_FILTER;
	private ImageIcon ICON_SAVE;
	private ImageIcon ICON_SAVE_AS;
	private ImageIcon ICON_EXIT;
	private ImageIcon ICON_PAUSE;
	private ImageIcon ICON_RESUME;

	public BufferedImage hills;
	public BufferedImage sky;
	public BufferedImage trees;
	public BufferedImage billboard01;
	public BufferedImage billboard02;
	public BufferedImage billboard03;
	public BufferedImage billboard04;
	public BufferedImage billboard05;
	public BufferedImage billboard06;
	public BufferedImage billboard07;
	public BufferedImage billboard08;
	public BufferedImage billboard09;
	public BufferedImage boulder1;
	public BufferedImage boulder2;
	public BufferedImage boulder3;
	public BufferedImage bush1;
	public BufferedImage bush2;
	public BufferedImage cactus;
	public BufferedImage car01;
	public BufferedImage car02;
	public BufferedImage car03;
	public BufferedImage car04;
	public BufferedImage column;
	public BufferedImage dead_tree1;
	public BufferedImage dead_tree2;
	public BufferedImage palm_tree;
	public BufferedImage player_left;
	public BufferedImage player_right;
	public BufferedImage player_straight;
	public BufferedImage player_uphill_left;
	public BufferedImage player_uphill_right;
	public BufferedImage player_uphill_straight;
	public BufferedImage semi;
	public BufferedImage stump;
	public BufferedImage tree1;
	public BufferedImage tree2;
	public BufferedImage truck;
	public BufferedImage background;
	public BufferedImage sprites;
	public BufferedImage mute;

	private Font font = new Font("Courier", Font.PLAIN, 14);

	private JPanel statusPanel = null;

	private MessageConsole mConsole;
	private JButton jbToggleText;
	private JButton jbToggleFilter;

	private JMenuItem jmiDeviceManagerStart;
	private JMenuItem jmiDeviceManagerStop;

	private JButton jbStartDeviceManager;
	private JButton jbStopDeviceManager;
	
	private JButton jbRoadConfiguration;
	private JButton jbTrafficConfiguration;

	private static Properties props = new Properties();

	private static boolean pause = false;

	private static StringBuilder tranportBuffer = new StringBuilder();

	private boolean keepExecuting = false;
	private int runInterval = 5000;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(192, 192, 192)));
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		trafficApp = new TrafficFrame();
		SwingUtilities.invokeLater(trafficApp);
	}

	public static synchronized TrafficFrame getInstance() {
		if (trafficApp == null) {
			trafficApp = new TrafficFrame();
		}

		return trafficApp;
	}

	public TrafficFrame() {
		loadProperties();

		readIcons();
		readImages();
	}

	@Override
	public void run() {
		// Create and set up the window.
		frame = new JFrame("Cars running on 3d road");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
			}
		});

		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(-1, -1));

		JToolBar jtp = createToolbar();
		contentPanel.add(jtp, BorderLayout.NORTH);

		JTabbedPane leftTabbedPane = new JTabbedPane();
		commandTreePanel = new JPanel(new BorderLayout());
		deviceManagerTreePanel = new JPanel(new BorderLayout());
		leftTabbedPane.addTab("Road Configuration", deviceManagerTreePanel);
		leftTabbedPane.addTab("Traffic Configuration", commandTreePanel);

		modelPanel = new JPanel(new BorderLayout());
		modelPanel.setPreferredSize(new Dimension(-1, -1));

		trafficModel = new HorizontalStraightModel();
		trafficRender = new HorizontalRender();
		modelPanel.add(trafficRender, BorderLayout.CENTER);

		systemLogPanel = createSystemLogPane();
		transportLogPanel = createTransportLogPane();
		JTabbedPane logTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		logTabbedPane.setPreferredSize(new Dimension(-1, -1));
		logTabbedPane.add("System Log", systemLogPanel);
		logTabbedPane.add("Transport Log", transportLogPanel);

		vRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, modelPanel, logTabbedPane);
		vRightSplitPane.setOneTouchExpandable(true);
		vRightSplitPane.setDividerLocation(800);

		contentPanel.add(vRightSplitPane, BorderLayout.CENTER);

		statusPanel = new JPanel(new BorderLayout());
		statusPanel.setPreferredSize(new Dimension(-1, 24));
		frame.add(statusPanel, BorderLayout.SOUTH);

		frame.add(contentPanel);
		frame.setJMenuBar(createMenuBar());

		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		mConsole = new MessageConsole(agentOutput);
		mConsole.redirectOut(Color.BLUE, System.out);
		mConsole.redirectErr(Color.RED, System.err);
		if (props.getProperty("log.mask.text").trim().length() > 0) {
			mConsole.setToggleText(props.getProperty("log.mask.text"));
			mConsole.setShowToggleText(false);
			jbToggleText.setText("Show " + props.getProperty("log.mask.text").trim());
		}
		if (props.getProperty("log.filter.text").trim().length() > 0) {
			mConsole.setToggleFilter(props.getProperty("log.filter.text"));
			mConsole.setShowToggleFilter(true);
			jbToggleFilter.setText("UnFilter " + props.getProperty("log.filter.text").trim());
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				game = new Game();
			}
		});
	}

	private void loadProperties() {
		InputStream input = null;

		try {
			input = new FileInputStream(System.getProperty("user.dir") + File.separator + "resources" + File.separator
					+ "config.properties");
			props.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readImages() {
		hills = getImage("background/hills.png");
		sky = getImage("background/sky.png");
		trees = getImage("background/trees.png");
		billboard01 = getImage("sprites/billboard01.png");
		billboard02 = getImage("sprites/billboard02.png");
		billboard03 = getImage("sprites/billboard03.png");
		billboard04 = getImage("sprites/billboard04.png");
		billboard05 = getImage("sprites/billboard05.png");
		billboard06 = getImage("sprites/billboard06.png");
		billboard07 = getImage("sprites/billboard07.png");
		billboard08 = getImage("sprites/billboard08.png");
		billboard09 = getImage("sprites/billboard09.png");
		boulder1 = getImage("sprites/boulder1.png");
		boulder2 = getImage("sprites/boulder2.png");
		boulder3 = getImage("sprites/boulder3.png");
		bush1 = getImage("sprites/bush1.png");
		bush2 = getImage("sprites/bush2.png");
		cactus = getImage("sprites/cactus.png");
		car01 = getImage("sprites/car01.png");
		car02 = getImage("sprites/car02.png");
		car03 = getImage("sprites/car03.png");
		car04 = getImage("sprites/car04.png");
		column = getImage("sprites/column.png");
		dead_tree1 = getImage("sprites/dead_tree1.png");
		dead_tree2 = getImage("sprites/dead_tree2.png");
		palm_tree = getImage("sprites/palm_tree.png");
		player_left = getImage("sprites/player_left.png");
		player_right = getImage("sprites/player_right.png");
		player_straight = getImage("sprites/player_straight.png");
		player_uphill_left = getImage("sprites/player_uphill_left.png");
		player_uphill_right = getImage("sprites/player_uphill_right.png");
		player_uphill_straight = getImage("sprites/player_uphill_straight.png");
		semi = getImage("sprites/semi.png");
		stump = getImage("sprites/stump.png");
		tree1 = getImage("sprites/tree1.png");
		tree2 = getImage("sprites/tree2.png");
		truck = getImage("sprites/truck.png");
		background = getImage("background.png");
		sprites = getImage("sprites.png");
		mute = getImage("mute.png");
	}

	private void readIcons() {
		ICON_CLEAR = getImageIcon("clear.gif");
		ICON_TOGGLE = getImageIcon("split.png");
		ICON_FILTER = getImageIcon("filter.gif");
		ICON_SAVE = getImageIcon("save.png");
		ICON_SAVE_AS = getImageIcon("saveas.png");
		ICON_EXIT = getImageIcon("exit-16.png");
		ICON_PAUSE = getImageIcon("pause-16.png");
		ICON_RESUME = getImageIcon("resume-16.png");
	}

	public synchronized BufferedImage getImage(String imageName) {
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(System.getProperty("user.dir") + File.separator
					+ "resources" + File.separator + "images" + File.separator + imageName));
			return image;
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return null;
	}

	public synchronized ImageIcon getImageIcon(String iconName) {
		try {
			ImageIcon icon = new ImageIcon(ImageIO.read(new FileInputStream(System.getProperty("user.dir")
					+ File.separator + "resources" + File.separator + "icons" + File.separator + iconName)));
			return icon;
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return null;
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		JMenuItem jmiFileExit = new JMenuItem("Exit");
		jmiFileExit.setIcon(ICON_EXIT);
		jmiFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				frame.setVisible(false);
				System.exit(0);
			}
		});
		menu.add(jmiFileExit);
		menuBar.add(menu);

		menu = new JMenu("    Model");
		JMenuItem jmiRtaTmu = new JMenuItem("Straight");
		jmiRtaTmu.addActionListener(e -> {
			game.setExit(true);
			try {
				Thread.sleep(200);
				modelPanel.removeAll();
				modelPanel.revalidate();
				trafficModel = new HorizontalStraightModel();
				trafficRender = new HorizontalRender();
				modelPanel.add(trafficRender, BorderLayout.CENTER);
				trafficModel.setRoadWidth(2000);
				trafficModel.setCameraHeight(5000);
				trafficModel.setCameraDepth(300);
				trafficModel.setFieldOfView(100);
				trafficModel.setFogDensity(5);
				game.refresh();
			} catch(Exception ex) {}
		});
		menu.add(jmiRtaTmu);
		menu.addSeparator();
		JMenuItem jmiRtaVms = new JMenuItem("Curve");
		jmiRtaVms.addActionListener(e -> {
			game.setExit(true);
			try {
				Thread.sleep(200);
				modelPanel.removeAll();
				modelPanel.revalidate();
				trafficModel = new TrafficCurveModel();
				trafficRender = new TrafficRender();
				modelPanel.add(trafficRender, BorderLayout.CENTER);
				trafficModel.setRoadWidth(2000);
				trafficModel.setCameraHeight(1000);
				trafficModel.setCameraDepth(300);
				trafficModel.setFieldOfView(100);
				trafficModel.setFogDensity(5);
				game.refresh();
			} catch(Exception ex) {}
		});
		menu.add(jmiRtaVms);
		menu.addSeparator();
		JMenuItem jmiRtaWs = new JMenuItem("Hill");
		jmiRtaWs.addActionListener(e -> {
			game.setExit(true);
			try {
				Thread.sleep(200);
				modelPanel.removeAll();
				modelPanel.revalidate();
				trafficModel = new TrafficHillModel();
				trafficRender = new TrafficRender();
				modelPanel.add(trafficRender, BorderLayout.CENTER);
				trafficModel.setRoadWidth(2000);
				trafficModel.setCameraHeight(1000);
				trafficModel.setCameraDepth(300);
				trafficModel.setFieldOfView(100);
				trafficModel.setFogDensity(5);
				game.refresh();
			} catch(Exception ex) {}
		});
		menu.add(jmiRtaWs);
		menu.addSeparator();
		JMenuItem jmiNtcipDms = new JMenuItem("Real");
		jmiNtcipDms.addActionListener(e -> {
			game.setExit(true);
			try {
				Thread.sleep(200);
				modelPanel.removeAll();
				modelPanel.revalidate();
				trafficModel = new TrafficRealModel();
				trafficRender = new TrafficRender();
				modelPanel.add(trafficRender, BorderLayout.CENTER);
				trafficModel.setRoadWidth(2000);
				trafficModel.setCameraHeight(1000);
				trafficModel.setCameraDepth(300);
				trafficModel.setFieldOfView(100);
				trafficModel.setFogDensity(5);
				game.refresh();
			} catch(Exception ex) {}
		});
		menu.add(jmiNtcipDms);
		menuBar.add(menu);

		menu = new JMenu("    Simulate");
		menu.setMnemonic(KeyEvent.VK_F);
		jmiDeviceManagerStart = new JMenuItem("Start");
		jmiDeviceManagerStart.setIcon(getImageIcon("start-16.png"));
		jmiDeviceManagerStart.setEnabled(true);
		menu.add(jmiDeviceManagerStart);
		jmiDeviceManagerStop = new JMenuItem("Stop");
		jmiDeviceManagerStop.setIcon(getImageIcon("stop-16.png"));
		jmiDeviceManagerStop.setEnabled(false);
		menu.add(jmiDeviceManagerStop);
		menuBar.add(menu);

		jmiDeviceManagerStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					game.simulate();
					jbStartDeviceManager.setEnabled(false);
					jbStopDeviceManager.setEnabled(true);
					jmiDeviceManagerStart.setEnabled(false);
					jmiDeviceManagerStop.setEnabled(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		jmiDeviceManagerStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					game.setExit(true);
					jbStartDeviceManager.setEnabled(true);
					jbStopDeviceManager.setEnabled(false);
					jmiDeviceManagerStart.setEnabled(true);
					jmiDeviceManagerStop.setEnabled(false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		menu = new JMenu("    Setting");
		menu.setMnemonic(KeyEvent.VK_C);
		JMenuItem jmiRoadConfiguration = new JMenuItem("Road Configuration");
		jmiRoadConfiguration.setIcon(getImageIcon("road.png"));
		jmiRoadConfiguration.setEnabled(true);
		menu.add(jmiRoadConfiguration);
		menu.addSeparator();
		JMenuItem jmiTrafficConfiguration = new JMenuItem("Traffic Configuration");
		jmiTrafficConfiguration.setIcon(getImageIcon("traffic.png"));
		jmiTrafficConfiguration.setEnabled(true);
		menu.add(jmiTrafficConfiguration);
		menuBar.add(menu);
		jmiRoadConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new RoadConfigurationDialog(TrafficFrame.getInstance().getJFrame(), "Road Configuration", null);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		jmiTrafficConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		menu = new JMenu("    Help");
		menu.setMnemonic(KeyEvent.VK_H);
		JMenuItem jmiAbout = new JMenuItem("About");
		jmiAbout.setIcon(getImageIcon("about-16.png"));
		menu.add(jmiAbout);
		jmiAbout.setEnabled(true);
		jmiAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuBar.add(menu);

		return menuBar;
	}

	public JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setPreferredSize(new Dimension(-1, 20));

		jbStartDeviceManager = new JButton();
		jbStartDeviceManager.setToolTipText("Start simulation");
		jbStartDeviceManager.setIcon(getImageIcon("start-16.png"));
		jbStartDeviceManager.setEnabled(true);
		toolbar.add(jbStartDeviceManager);
		jbStopDeviceManager = new JButton();
		jbStopDeviceManager.setToolTipText("Stop simulation");
		jbStopDeviceManager.setIcon(getImageIcon("stop-16.png"));
		jbStopDeviceManager.setEnabled(false);
		toolbar.add(jbStopDeviceManager);
		toolbar.addSeparator();
		jbStartDeviceManager.addActionListener(e -> {
			game.simulate();
			jbStartDeviceManager.setEnabled(false);
			jbStopDeviceManager.setEnabled(true);
			jmiDeviceManagerStart.setEnabled(false);
			jmiDeviceManagerStop.setEnabled(true);
		});
		jbStopDeviceManager.addActionListener(e -> {
			game.setExit(true);
			jbStartDeviceManager.setEnabled(true);
			jbStopDeviceManager.setEnabled(false);
			jmiDeviceManagerStart.setEnabled(true);
			jmiDeviceManagerStop.setEnabled(false);
		});
		
		jbRoadConfiguration = new JButton();
		jbRoadConfiguration.setToolTipText("Road configuration");
		jbRoadConfiguration.setIcon(getImageIcon("road.png"));
		jbRoadConfiguration.setEnabled(true);
		toolbar.add(jbRoadConfiguration);
		jbTrafficConfiguration = new JButton();
		jbTrafficConfiguration.setToolTipText("Traffic configuration");
		jbTrafficConfiguration.setIcon(getImageIcon("traffic.png"));
		jbTrafficConfiguration.setEnabled(true);
		toolbar.add(jbTrafficConfiguration);
		jbRoadConfiguration.addActionListener(e -> {
			new RoadConfigurationDialog(TrafficFrame.getInstance().getJFrame(), "Road Configuration", null);
		});
		jbTrafficConfiguration.addActionListener(e -> {
			new TrafficConfigurationDialog();
		});
		toolbar.addSeparator();
		
		JButton jbAbout = new JButton();
		jbAbout.setToolTipText("About");
		jbAbout.setIcon(getImageIcon("about-16.png"));
		jbAbout.addActionListener(e -> {
		});
		toolbar.add(jbAbout);
		
		return toolbar;
	}

	public JFrame getJFrame() {
		return frame;
	}

	public JPanel createSystemLogPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		JToolBar agentToolBar = new JToolBar();
		agentToolBar.setPreferredSize(new Dimension(-1, 24));

		JButton jbClear = new JButton("Clear");
		jbClear.setIcon(ICON_CLEAR);
		jbClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				agentOutput.setText("");
			}
		});
		agentToolBar.add(jbClear);
		agentToolBar.addSeparator();
		JButton jbSave = new JButton("Save");
		jbSave.setIcon(ICON_SAVE);
		jbSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String contents = agentOutput.getText();
				contents = contents.replaceAll("\r\r\n", "\n");

				File path = new File(System.getProperty("user.dir") + File.separator + "logs");
				if (!path.exists()) {
					path.mkdir();
				}

				File selectedFile = new File(System.getProperty("user.dir") + File.separator + "logs" + File.separator
						+ "log_" + getTimeInMilliSeconds((new Date()).getTime()) + ".log");

				BufferedWriter bw = null;
				FileWriter fw = null;
				try {
					fw = new FileWriter(selectedFile.getAbsolutePath());
					bw = new BufferedWriter(fw);
					bw.write(contents);

					JOptionPane.showMessageDialog(null, "Log contents are saved to: " + selectedFile.getAbsolutePath());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				} finally {
					try {
						if (bw != null)
							bw.close();

						if (fw != null)
							fw.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		agentToolBar.add(jbSave);
		JButton jbSaveAs = new JButton("Save As");
		jbSaveAs.setIcon(ICON_SAVE_AS);
		jbSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String contents = agentOutput.getText();
				contents = contents.replaceAll("\r\r\n", "\n");

				JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

				chooser.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(".log");
					}

					public String getDescription() {
						return "Log Files (*.log)";
					}
				});

				try {
					switch (chooser.showSaveDialog(TrafficFrame.getInstance().getJFrame())) {
					case JFileChooser.APPROVE_OPTION:

						try {
							Files.write(Paths.get(chooser.getSelectedFile().getAbsolutePath()), contents.getBytes());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getMessage());
						}

						break;
					default:
						// do nothing
					}
				} catch (Exception ex) {
					agentOutput.setText(ex.toString());
				}
			}
		});
		agentToolBar.add(jbSaveAs);
		agentToolBar.addSeparator();
		jbToggleText = new JButton("Mask Text");
		jbToggleText.setIcon(ICON_TOGGLE);
		jbToggleText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (mConsole.isShowToggleText()) {
					String ret = JOptionPane.showInputDialog("Please input the text you are going to mask");

					if (ret != null && ret.trim().length() > 0) {
						mConsole.setToggleText(ret.trim());
						mConsole.setShowToggleText(false);
						jbToggleText.setText("Show " + ret.trim());
					}
				} else {
					mConsole.setShowToggleText(true);
					jbToggleText.setText("Mask Text");
				}
			}
		});
		agentToolBar.add(jbToggleText);
		agentToolBar.addSeparator();
		jbToggleFilter = new JButton("Filter Text");
		jbToggleFilter.setIcon(ICON_FILTER);
		jbToggleFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!mConsole.isShowToggleFilter()) {
					String ret = JOptionPane.showInputDialog("Please input the text you are going to filter");

					if (ret != null && ret.trim().length() > 0) {
						mConsole.setToggleFilter(ret.trim());
						mConsole.setShowToggleFilter(true);
						jbToggleFilter.setText("UnFilter " + ret.trim());
					}
				} else {
					mConsole.setShowToggleFilter(false);
					jbToggleFilter.setText("Filter Text");
				}
			}
		});
		agentToolBar.add(jbToggleFilter);
		contentPane.add(agentToolBar, BorderLayout.NORTH);

		agentOutput = new JTextPane();
		agentOutput.setFont(font);
		agentOutput.setEditable(true);
		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.add(agentOutput);
		scrollPane = new JScrollPane(noWrapPanel);

		contentPane.add(scrollPane, BorderLayout.CENTER);

		return contentPane;
	}

	public JPanel createTransportLogPane() {
		JPanel contentPane = new JPanel(new BorderLayout());

		JToolBar outputToolBar = new JToolBar();
		outputToolBar.setPreferredSize(new Dimension(-1, 24));
		JButton jbClear = new JButton("Clear");
		jbClear.setIcon(ICON_CLEAR);
		jbClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				outputPane.setText("");
			}
		});
		outputToolBar.add(jbClear);
		outputToolBar.addSeparator();
		JButton jbSave = new JButton("Save");
		jbSave.setIcon(ICON_SAVE);
		jbSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String contents = outputPane.getText();
				contents = contents.replaceAll("\r\r\n", "\n");

				File path = new File(System.getProperty("user.dir") + File.separator + "logs");
				if (!path.exists()) {
					path.mkdir();
				}

				File selectedFile = new File(System.getProperty("user.dir") + File.separator + "logs" + File.separator
						+ "log_" + getTimeInMilliSeconds((new Date()).getTime()) + ".log");

				BufferedWriter bw = null;
				FileWriter fw = null;
				try {
					fw = new FileWriter(selectedFile.getAbsolutePath());
					bw = new BufferedWriter(fw);
					bw.write(contents);

					JOptionPane.showMessageDialog(null, "Log contents are saved to: " + selectedFile.getAbsolutePath());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				} finally {
					try {
						if (bw != null)
							bw.close();

						if (fw != null)
							fw.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		outputToolBar.add(jbSave);
		outputToolBar.addSeparator();
		JButton jbPause = new JButton("Pause");
		jbPause.setIcon(ICON_PAUSE);
		outputToolBar.add(jbPause);
		JButton jbResume = new JButton("Resume");
		jbResume.setIcon(ICON_RESUME);
		jbResume.setEnabled(false);
		outputToolBar.add(jbResume);
		jbPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pause = true;
				tranportBuffer = new StringBuilder();

				jbPause.setEnabled(false);
				jbResume.setEnabled(true);
			}
		});
		jbResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pause = false;
				appendText(tranportBuffer.toString());

				jbPause.setEnabled(true);
				jbResume.setEnabled(false);
			}
		});
		contentPane.add(outputToolBar, BorderLayout.NORTH);

		outputPane = new JEditorPane();
		outputPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		contentPane.add(new JScrollPane(outputPane), BorderLayout.CENTER);

		return contentPane;
	}

	public JTree getMeridianAgentTree() {
		return commandTree;
	}

	public void setSimulatorPanelSize(int width, int height) {
		jpCanvasPanel.setPreferredSize(new Dimension(width, height));
		jpCanvasPanel.setMaximumSize(new Dimension(width, height));
		jpCanvasPanel.setMinimumSize(new Dimension(width, height));
	}

	public void setMessage(String mess) {
		// dmsSignCanvas.setMessage(mess);
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public static void setText(String text) {
		outputPane.setText(text);
	}

	public static void appendText(String text) {
		if (!pause) {
			if (text != null && outputPane != null && outputPane.getText() != null) {
				if (outputPane.getText().trim().length() < 1) {
					outputPane.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + text);
				} else {
					outputPane.setText(outputPane.getText() + "\n"
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + text);
				}

				outputPane.setCaretPosition(outputPane.getDocument().getLength());
			}
		} else {
			tranportBuffer.append("\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + text);
		}
	}

	public static void appendLogText(String text) {
		if (text != null && agentOutput != null && agentOutput.getText() != null) {
			if (agentOutput.getText().trim().length() < 1) {
				agentOutput.setText(text);
			} else {
				agentOutput.setText(outputPane.getText() + text);
			}
		}
	}

	public static Properties getProps() {
		return props;
	}

	public static void setProps(Properties props) {
		TrafficFrame.props = props;
	}

	public String getTimeInMilliSeconds(long timeInMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int millisecond = cal.get(Calendar.MILLISECOND);
		return String.format("%04d%02d%02d%02d%02d%02d%03d", year, month, day, hour, minute, second, millisecond);
	}

	public String getTimeStringInMilliSeconds(long timeInMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int millisecond = cal.get(Calendar.MILLISECOND);
		return String.format("%04d-%02d-%02d %02d:%02d:%02d %03d", year, month, day, hour, minute, second, millisecond);
	}

//	public String getDeviceConfigFile() {
//		return deviceConfigFile;
//	}
//
//	public void setDeviceConfigFile(String deviceConfigFile) {
//		this.deviceConfigFile = deviceConfigFile;
//	}
//
	public boolean isKeepExecuting() {
		return keepExecuting;
	}

	public void setKeepExecuting(boolean keepExecuting) {
		this.keepExecuting = keepExecuting;
	}

	public int getRunInterval() {
		return runInterval;
	}

	public void setRunInterval(int runInterval) {
		this.runInterval = runInterval;
	}

	// public TrafficModel getTrafficModel() {
	// return trafficModel;
	// }

	public TrafficModel getTrafficModel() {
		return trafficModel;
	}

	public TrafficRender getTrafficRender() {
		return trafficRender;
	}

	public Game getGame() {
		return game;
	}
}
