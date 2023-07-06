package com.kuai.traffic.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.kuai.traffic.TrafficFrame;
import com.kuai.traffic.util.Util;

@SuppressWarnings("serial")
public class TrafficRender extends JPanel {
  public Logger log = Logger.getLogger(TrafficRender.class);

  private static boolean DEBUG = false;

  protected BufferedImage bimage;
  protected Graphics2D g2d;

  public TrafficRender() {
    setBackground(Color.white);

    BufferedImage tbg = TrafficFrame.getInstance().background;
    bimage = new BufferedImage(tbg.getWidth(), tbg.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2d = bimage.createGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
        new KeyEventDispatcher() {
          @Override
          public boolean dispatchKeyEvent(KeyEvent e) {
            switch (e.getID()) {
            case KeyEvent.KEY_PRESSED:
              int keyCode = e.getKeyCode();
              switch (keyCode) {
              case KeyEvent.VK_UP:
                // System.out.println("Key UP pressed!");
                TrafficFrame.getInstance().getTrafficModel().setKeyFaster(true);
                TrafficFrame.getInstance().getGame().forwardOneStep();
                break;
              case KeyEvent.VK_DOWN:
                // System.out.println("Key DOWN pressed!");
                TrafficFrame.getInstance().getTrafficModel().setKeySlower(true);
                break;
              case KeyEvent.VK_LEFT:
                // System.out.println("Key LEFT pressed!");
                TrafficFrame.getInstance().getTrafficModel().setKeyLeft(true);
                break;
              case KeyEvent.VK_RIGHT:
                // System.out.println("Key RIGHT pressed!");
                TrafficFrame.getInstance().getTrafficModel().setKeyRight(true);
                break;
              default:
                break;
              }
              break;

            case KeyEvent.KEY_RELEASED:
              keyCode = e.getKeyCode();

              switch (keyCode) {
              case KeyEvent.VK_UP:
                // System.out.println("Key UP released!");
                TrafficFrame.getInstance().getTrafficModel().setKeyFaster(false);
                break;
              case KeyEvent.VK_DOWN:
                // System.out.println("Key DOWN released!");
                TrafficFrame.getInstance().getTrafficModel().setKeySlower(false);
                break;
              case KeyEvent.VK_LEFT:
                // System.out.println("Key LEFT released!");
                TrafficFrame.getInstance().getTrafficModel().setKeyLeft(false);
                break;
              case KeyEvent.VK_RIGHT:
                // System.out.println("Key RIGHT released!");
                TrafficFrame.getInstance().getTrafficModel().setKeyRight(false);
                break;
              default:
                break;
              }
              break;
            }

            return false;
          }
        });
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    int w = bimage.getWidth();
    int h = bimage.getHeight();
    g2.drawImage(bimage, 0, 0, w, h, null);
    g2.dispose();
  }

  // @Override
  // public Dimension getPreferredSize() {
  // return new Dimension(bimage.getWidth(), bimage.getHeight());
  // }

  public void println(String str) {
    if (DEBUG) {
      System.out.println(str);
    }
  }

  public void update() {
    repaint();
  }
  
  public void render() {
    
  }

  public void clearRect(int x, int y, int width, int height) {
    println("clearRect");

    g2d.clearRect(x, y, width, height);
  }

  public void polygon(double x1, double y1, double x2, double y2, double x3, double y3, double x4,
      double y4, Color color) {
    log.info("TrafficRender:polygon: (" + x1 + ", " + y1 + "; " + x2 + ", " + y2 + "; " + x3 + ", "
        + y3 + "; " + x4 + ", " + y4 + "; " + ")");

    g2d.setColor(color);
    int xPoly[] = { (int) x1, (int) x2, (int) x3, (int) x4 };
    int yPoly[] = { (int) y1, (int) y2, (int) y3, (int) y4 };
    Polygon p = new Polygon(xPoly, yPoly, xPoly.length);
    g2d.fillPolygon(p);
  }

  public void segment(int width, int lanes, double x1, double y1, double w1, double x2, double y2,
      double w2, double fog, TrafficColors colors) {
    log.info("TrafficRender:segment: (" + width + ", " + lanes + ", " + x1 + ", " + y1 + ", " + w1
        + ", " + x2 + ", " + y2 + ", " + w2 + ", " + fog + ")");

    double r1 = rumbleWidth(w1, lanes), r2 = rumbleWidth(w2, lanes), l1 = laneMarkerWidth(w1,
        lanes), l2 = laneMarkerWidth(w2, lanes), lanew1, lanew2, lanex1, lanex2, lane;

    g2d.setColor(colors.getGrass());
    g2d.fillRect(0, (int) y2, width, (int) (y1 - y2));

    polygon(x1 - w1 - r1, y1, x1 - w1, y1, x2 - w2, y2, x2 - w2 - r2, y2, colors.getRumble());
    polygon(x1 + w1 + r1, y1, x1 + w1, y1, x2 + w2, y2, x2 + w2 + r2, y2, colors.getRumble());
    polygon(x1 - w1, y1, x1 + w1, y1, x2 + w2, y2, x2 - w2, y2, colors.getRoad());

    if (colors.getLane() != null) {
      lanew1 = w1 * 2 / lanes;
      lanew2 = w2 * 2 / lanes;
      lanex1 = x1 - w1 + lanew1;
      lanex2 = x2 - w2 + lanew2;
      for (lane = 1; lane < lanes; lanex1 += lanew1, lanex2 += lanew2, lane++)
        polygon((lanex1 - l1 / 2), y1, (lanex1 + l1 / 2), y1, (lanex2 + l2 / 2), y2, (lanex2 - l2
            / 2), y2, colors.getLane());
    }

    fog(0, (int) y1, (int) width, (int) (y2 - y1), fog);
  }

  public void drawImage(BufferedImage image, int x, int y, int width, int height) {
    g2d.drawImage(image, x, y, null);
    // Color color = colors[currentColor];
    // g2d.setColor(color);
    // g2d.drawLine(100, 100, 300, 300);
    repaint();
  }

  public void background(BufferedImage background, int width, int height, Rectangle layer) {
    background(background, width, height, layer, 0, 0);
  }

  public void background(BufferedImage background, int width, int height, Rectangle layer,
      double rotation) {
    background(background, width, height, layer, rotation, 0);
  }

  public void background(BufferedImage background, int width, int height, Rectangle layer,
      double rotation, double offset) {
    println("background: (" + width + ", " + height + ", " + layer + ", " + rotation + ", " + offset
        + ")");

    int imageW = (int) layer.getWidth() / 2;
    int imageH = (int) layer.getHeight();

    double sourceX = layer.x + Math.floor(layer.getWidth() * rotation);
    double sourceY = layer.y;
    double sourceW = Math.min(imageW, layer.x + layer.getWidth() - sourceX);
    double sourceH = imageH;

    int destX = 0;
    int destY = (int) offset;
    double destW = Math.floor(width * (sourceW / imageW));
    double destH = height;

    BufferedImage subImage = background.getSubimage((int) sourceX, (int) sourceY, (int) sourceW,
        (int) sourceH);
    g2d.drawImage(subImage, destX, destY, (int) destW, (int) destH, null);
    if (sourceW < imageW) {
      subImage = background.getSubimage((int) layer.x, (int) sourceY, (int) (imageW - sourceW),
          (int) sourceH);
      g2d.drawImage(subImage, (int) destW - 1, destY, (int) (width - destW), (int) destH, null);
    }
  }

  public void sprite(int width, int height, double resolution, double roadWidth,
      BufferedImage sprites, Rectangle sprite, double scale, double destX, double destY) {
    sprite(width, height, resolution, roadWidth, sprites, sprite, scale, destX, destY, 0, 0, 0);
  }

  public void sprite(int width, int height, double resolution, double roadWidth,
      BufferedImage sprites, Rectangle sprite, double scale, double destX, double destY,
      double offsetX, double offsetY) {
    sprite(width, height, resolution, roadWidth, sprites, sprite, scale, destX, destY, offsetX,
        offsetY, 0);
  }

  public void sprite(int width, int height, double resolution, double roadWidth,
      BufferedImage sprites, Rectangle sprite, double scale, double destX, double destY,
      double offsetX, double offsetY, double clipY) {
    println("sprite: (" + width + ", " + height + ", " + resolution + ", " + roadWidth + ", "
        + sprite + ", " + scale + ", " + destX + ", " + destY + ", " + offsetX + ", " + offsetY
        + ", " + clipY + ")");

    double destW = (sprite.getWidth() * scale * width / 2) * (Constants.SPRITES_SCALE * roadWidth);
    double destH = (sprite.getHeight() * scale * width / 2) * (Constants.SPRITES_SCALE * roadWidth);

    destX = destX + (destW * offsetX);
    destY = destY + (destH * offsetY);

    double clipH = 0;
    if (clipY != 0) {
      clipH = Math.max(0, destY + destH - clipY);
    }

    if ((clipH < destH) && (int) (sprite.getHeight() - (sprite.getHeight() * clipH / destH)) != 0) {
      // System.out.println("(" + sprite.x + ", " + sprite.y + ", " + (int) sprite.getWidth() + ", "
      // + (int) (sprite.getHeight() - (sprite.getHeight() * clipH / destH)) + ")");
      BufferedImage subImage = sprites.getSubimage(sprite.x, sprite.y, (int) sprite.getWidth(),
          (int) (sprite.getHeight() - (sprite.getHeight() * clipH / destH)));
      g2d.drawImage(subImage, (int) destX, (int) destY, (int) destW, (int) (destH - clipH), null);
    }
  }

  public void player(int width, int height, double resolution, double roadWidth,
      BufferedImage sprites, double speedPercent, double scale, double destX, double destY,
      double steer, double updown) {
    println("player");

    double bounce = (1.5 * Math.random() * speedPercent * resolution) * Util.randomChoice(
        new int[] { -1, 1 });
    Rectangle sprite;
    if (steer < 0)
      sprite = (updown > 0) ? Constants.SPRITES.get("PLAYER_UPHILL_LEFT")
          : Constants.SPRITES.get("PLAYER_LEFT");
    else if (steer > 0)
      sprite = (updown > 0) ? Constants.SPRITES.get("PLAYER_UPHILL_RIGHT")
          : Constants.SPRITES.get("PLAYER_RIGHT");
    else
      sprite = (updown > 0) ? Constants.SPRITES.get("PLAYER_UPHILL_STRAIGHT")
          : Constants.SPRITES.get("PLAYER_STRAIGHT");

    sprite(width, height, resolution, roadWidth, sprites, sprite, scale, destX, (destY + bounce),
        -0.5, (double) -1);
  }

  public void fog(int x, int y, int width, int height, double fog) {
    if (fog < 1) {
      Color color = g2d.getColor();
      Color tc = (Color) Constants.COLORS.get("FOG");
      g2d.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), (int) (1 - fog)));
      g2d.fillRect(x, y, width, height);
      g2d.setColor(color);
    }
  }

  public double rumbleWidth(double projectedRoadWidth, int lanes) {
    return projectedRoadWidth / Math.max(6, 2 * lanes);
  }

  public double laneMarkerWidth(double projectedRoadWidth, int lanes) {
    return projectedRoadWidth / Math.max(32, 8 * lanes);
  }
}
