package nl.br.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import en.lib.drawing.DrawUtils;
import en.lib.drawing.Drawable;
import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import en.lib.math.Vector;
import en.lib.objects.Tickable;
import en.lib.pooling.PoolMaster;
import en.lib.pooling.Poolable;
import en.lib.setup.Panel;
import nl.br.effects.Effect;
import nl.br.entities.enemies.Enemy;
import nl.br.entities.enemies.EnemyUtils;
import nl.br.entities.players.Ben;
import nl.br.main.BossRush;
import nl.br.map.MapUtils;
import nl.br.map.floorfilling.Area;
import nl.br.map.floorfilling.Block;
import nl.br.map.floorfilling.Room;
import nl.br.map.floorfilling.props.Prop;
import nl.br.map.floors.Floor;
import nl.br.map.pathFinding.PathFindUtils;
import nl.br.weapons.Gun;
import nl.br.weapons.Wand;
import nl.br.weapons.projectiles.Arrow;
import nl.br.weapons.projectiles.Bullet;
import nl.br.weapons.projectiles.MagicDamageOrb;

public class DrawPanel extends Panel implements MouseMotionListener {
	public static int WIDTH = BossRush.WIDTH, HEIGHT = BossRush.HEIGHT;
	public static int SCALE = 20;
	
	private static double translationX = 0, translationY = 0;

	public static boolean DEBUG = false, DEBUG2 = false, stressTest = false;
	private static Color bgColor = Color.BLACK;
	
	public static Floor currentFloor;
	
	public static Point mousePoint = new Point(), relMousePoint = new Point();
	
	private static ArrayList<Drawable> drawables = new ArrayList<Drawable>(), currentlyDrawn = new ArrayList<Drawable>();
	private static ArrayList<Tickable> tickables = new ArrayList<Tickable>();
	public static Ben ben;
	
	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	private static double renderBoxFactor = 1+(0.2*((double)SCALE/MapUtils.blockResolution));
	private static Rectangle renderBox = new Rectangle((int)((-WIDTH/2)*(renderBoxFactor-1)), (int)((-HEIGHT/2)*(renderBoxFactor-1)), (int)(WIDTH*renderBoxFactor), (int)(HEIGHT*renderBoxFactor));
	private static double propRenderBoxFactor = 2;
	public static Rectangle extraLargeRenderBox = new Rectangle((int)((-WIDTH/2)*(propRenderBoxFactor-1)), (int)((-HEIGHT/2)*(propRenderBoxFactor-1)), (int)(WIDTH*propRenderBoxFactor), (int)(HEIGHT*propRenderBoxFactor));
	
	public static Font tinyFont = new Font("Arial", 0, 8);
	public static Font bigFont = new Font("Arial", 0, 20);
	
	private static int maxEnemyArrows = 350;
	private static int maxFriendlyArrows = 100;
	
	private static int maxEnemyBullets = 350;
	private static int maxFriendlyBullets = 100;
	
	private static int maxEnemyMagic = 350;
	private static int maxFriendlyMagic = 200;
	
	private static int cameraPanStartRadius = 100, cameraPanMaxRadius = 500;
	
	public static double deltaMultiplier = 1, slowMotionTimer;
	
	private static boolean screenShaking;
	private static double shakeScale, shakeScaleDegradation;
	private static int screenShakeX, screenShakeY;
	
	public static int alphaOverlay = 0;
	
	public static Point aimPoint = new Point();
	
	public static final int maxStars = 10;
	public static ArrayList<Point2D.Double> starPoints = new ArrayList<Point2D.Double>();
	public static ArrayList<Vector> starVectors = new ArrayList<Vector>();
	
	public DrawPanel() {
		setBounds(0, 0, WIDTH, HEIGHT);
		
		//currentFloor = new Floor("resources/floors/TestFloorSmall");
		currentFloor = new Floor();
		for (Room r:currentFloor.allRooms) {
			for (int y = 0; y < r.blocks.length; y++) {
				for (int x = 0; x < r.blocks[0].length; x++) {
					drawables.add(r.blocks[y][x]);
				}
			}
		}
		for (int y = 0; y < Floor.floorSize; y++) {
			for (int x = 0; x < Floor.floorSize; x++) {
				if (currentFloor.rooms[y][x] != null) {
					currentFloor.rooms[y][x].updateCorners();
					addDrawable(currentFloor.rooms[y][x]);
				}
			}
		}
		
		Point playerStart = currentFloor.getStart();
		ben = new Ben(playerStart.x, playerStart.y, this);
		addDrawable(ben);
		addTickable(ben);
		addMouseListener(ben);
		addMouseMotionListener(this);
		
		for (Room r:currentFloor.allRooms) {
			for (Prop p:r.props) {
				addDrawable(p);
				addTickable(p);
			}
		}
		
		initProjectiles();
		
		for (int i = 0; i < 10; i++) {
			Enemy newEnemy = new Enemy(300, 300, EnemyUtils.getEnemyByType(1));
			enemies.add(newEnemy);
			drawables.add(newEnemy);
			tickables.add(newEnemy);
		}
		
		PoolMaster.addPool("smokePoofs", new Effect(new SpriteMap(10, 10, 0, new File("resources/textures/effects/SmokePoof.png")), 0, 0, 4, 0, 5, false), 3);
		for (Poolable p:PoolMaster.getPool("smokePoofs")) {
			Effect e = (Effect)p;
			drawables.add(e);
			tickables.add(e);
		}
		
		for (int i = 0; i < maxStars; i++) {
			Point2D.Double starPoint = new Point2D.Double(MathUtils.randInt(0, WIDTH), MathUtils.randInt(0, HEIGHT));
			Vector starVector = new Vector(MathUtils.randInt(0, 360), MathUtils.randInt(1, 3));
			starPoints.add(starPoint);
			starVectors.add(starVector);
		}
		
		BossRush.frame.removePanel(BossRush.loadingScreen);
	}
	
	public void initProjectiles() {
		PoolMaster.addPool("enemyArrows", new Arrow(false), maxEnemyArrows);
		for (Poolable p:PoolMaster.getPool("enemyArrows")) {
			addDrawable((Arrow)p);
			addTickable((Arrow)p);
		}
		
		PoolMaster.addPool("friendlyArrows", new Arrow(true), maxFriendlyArrows);
		for (Poolable p:PoolMaster.getPool("friendlyArrows")) {
			addDrawable((Arrow)p);
			addTickable((Arrow)p);
		}
		
		
		PoolMaster.addPool("enemyBullets", new Bullet(false), maxEnemyBullets);
		for (Poolable p:PoolMaster.getPool("enemyBullets")) {
			addDrawable((Bullet)p);
			addTickable((Bullet)p);
		}
		
		PoolMaster.addPool("friendlyBullets", new Bullet(true), maxFriendlyBullets);
		for (Poolable p:PoolMaster.getPool("friendlyBullets")) {
			addDrawable((Bullet)p);
			addTickable((Bullet)p);
		}
		
		PoolMaster.addPool("enemyMagic", new MagicDamageOrb(false), maxEnemyMagic);
		for (Poolable p:PoolMaster.getPool("enemyMagic")) {
			addDrawable((MagicDamageOrb)p);
			addTickable((MagicDamageOrb)p);
		}
		
		PoolMaster.addPool("friendlyMagic", new MagicDamageOrb(true), maxFriendlyMagic);
		for (Poolable p:PoolMaster.getPool("friendlyMagic")) {
			addDrawable((MagicDamageOrb)p);
			addTickable((MagicDamageOrb)p);
		}
	}
	
	private static Vector translationPan = new Vector(0, 0);
	public void tick(double delta) {
		double modifiedDelta = delta*deltaMultiplier;
		
		if (MathUtils.distPoints(getCameraLocation(), ben.getSpriteCenter()) > cameraPanStartRadius) {
			translationPan.setDirection(Vector.getVectorDirection(getCameraLocation(), ben.getSpriteCenter()));
			translationPan.setSize(((MathUtils.distPoints(getCameraLocation(), ben.getSpriteCenter())-cameraPanStartRadius)/(cameraPanMaxRadius-cameraPanStartRadius))*ben.movement.getSize()*modifiedDelta);
			translationX -= translationPan.getXComp();
			translationY -= translationPan.getYComp();
		}
		
		for (int i = 0; i < starPoints.size(); i++) {
			starPoints.get(i).x += starVectors.get(i).getXComp();
			starPoints.get(i).y += starVectors.get(i).getYComp();
		}
		
		relMousePoint = makeRelative(mousePoint);
		
		tickAll(modifiedDelta);
		
		currentlyDrawn.clear();
		for (int i = 0; i < drawables.size(); i++) {
			Point objectCenter = drawables.get(i).getCenter();
			if (!(drawables.get(i) instanceof Prop) && !(drawables.get(i) instanceof Room)) {
				if (renderBox.contains(objectCenter)) {
					currentlyDrawn.add(drawables.get(i));
				}
			} else if (extraLargeRenderBox.contains(objectCenter)) {
				currentlyDrawn.add(drawables.get(i));
			}
		}
		
		if (slowMotionTimer > 0) {
			slowMotionTimer-=delta;
			if (slowMotionTimer <= 0) {
				resetSlowMotion();
			}
		}
		
		renderBox.setLocation((int)(-translationX+(-WIDTH/2)*(renderBoxFactor-1)), (int)(-translationY+(-HEIGHT/2)*(renderBoxFactor-1)));
		extraLargeRenderBox.setLocation((int)(-translationX+(-WIDTH/2)*(propRenderBoxFactor-1)), (int)(-translationY+(-HEIGHT/2)*(propRenderBoxFactor-1)));
	}
	private void tickAll(double delta) {
		for (Tickable t:tickables) {
			t.tick(delta);
		}
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(bgColor);
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2.setFont(DrawPanel.tinyFont);
		
		g2.setColor(Color.WHITE);
		for (Point2D.Double star:starPoints) {
			DrawUtils.drawPoint(Math.floorMod((int)star.x, WIDTH), Math.floorMod((int)star.y, HEIGHT), 4, g2);
		}
		
		if (screenShaking) {
			if (shakeScale > 0) {
				shakeScale-=shakeScaleDegradation;
			} else {
				stopScreenShake();
			}
			screenShakeX = MathUtils.randInt(-((int)shakeScale), (int)shakeScale);
			screenShakeY = MathUtils.randInt(-((int)shakeScale), (int)shakeScale);
		}
		
		g2.translate(translationX+screenShakeX, translationY+screenShakeY);
		drawObjects(g2);
		g2.translate(-translationX-screenShakeX, -translationY-screenShakeY);
		
		g2.setColor(new Color(0,0,0,alphaOverlay));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		drawUI(g2);
	
	}
	
	private static int crossHairSize = 5;
	private static Rectangle TPSBox = new Rectangle(WIDTH/2, 0, WIDTH/2, 30);
	private static Rectangle FPSBox = new Rectangle(WIDTH/2, 30, WIDTH/2, 30);
	private static Rectangle maxMemoryBox = new Rectangle(0, 0, WIDTH, 30);
	private static Rectangle allocatedMemoryBox = new Rectangle(0, 30, WIDTH, 30);
	private static Rectangle usedMemoryBox = new Rectangle(0, 60, WIDTH, 30);
	private static Rectangle freeMemoryBox = new Rectangle(0, 90, WIDTH, 30);
	private long lastMPSRecord = System.currentTimeMillis(), lastUsedMemory = 0;;
	private int MPS = 0, memoryThisTime = 0;
	private static Rectangle MPSBox = new Rectangle(0, 150, WIDTH, 30);
	public void drawUI(Graphics2D g2) {
		if (DEBUG) {
			g2.setColor(Color.RED);
			g2.fillOval(WIDTH/2-crossHairSize/2, HEIGHT/2-crossHairSize/2, crossHairSize, crossHairSize);
			g2.setColor(Color.GREEN);
			g2.drawOval(WIDTH/2-cameraPanStartRadius, HEIGHT/2-cameraPanStartRadius, cameraPanStartRadius*2, cameraPanStartRadius*2);
			g2.setColor(Color.RED);
			g2.drawOval(WIDTH/2-cameraPanMaxRadius, HEIGHT/2-cameraPanMaxRadius, cameraPanMaxRadius*2, cameraPanMaxRadius*2);
		}
		
		if (DEBUG2) {
			g2.setFont(bigFont);
			
			if (TPS < 60) {
				g2.setColor(Color.RED);
			} else {
				g2.setColor(Color.BLACK);
			}
			DrawUtils.drawStringInBox("Ticks executed this second(TPS): " + TPS, TPSBox, g2);
			
			if (FPS < 60) {
				g2.setColor(Color.RED);
			} else {
				g2.setColor(Color.BLACK);
			}
			DrawUtils.drawStringInBox("Screen-refreshes this second(FPS): " + FPS, FPSBox, g2);
			
			
			g2.setColor(Color.BLACK);
			DrawUtils.drawStringInBox("Max memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().maxMemory()) + "MB", maxMemoryBox, g2);
			DrawUtils.drawStringInBox("Allocated memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().totalMemory()) + "MB", allocatedMemoryBox, g2);
			g2.setColor(Color.RED);
			long usedMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			DrawUtils.drawStringInBox("Used memory: " + MathUtils.toMegaBytes(usedMemory) + "MB", usedMemoryBox, g2);
			g2.setColor(Color.GREEN);
			DrawUtils.drawStringInBox("Free memory: " + MathUtils.toMegaBytes(Runtime.getRuntime().freeMemory()) + "MB", freeMemoryBox, g2);
			
			if (usedMemory-lastUsedMemory > 0) {
				memoryThisTime += (usedMemory-lastUsedMemory);
			}
			lastUsedMemory = usedMemory;
			if (System.currentTimeMillis() - lastMPSRecord > 5000) {
				MPS = memoryThisTime/5;
				memoryThisTime = 0;
				lastMPSRecord = System.currentTimeMillis();
			}
			
			g2.setColor(Color.RED);
			DrawUtils.drawStringInBox("Memory per second: " + MathUtils.toMegaBytes(MPS) + "MB", MPSBox, g2);
		}
	}
	
	public void drawObjects(Graphics2D g2) {
		currentFloor.drawFloor(g2);
		try { 
			sortObjects();
		} catch(Exception e) {
			sortObjects();
		}
		
		/*
		for (int i = 0; i < drawables.size(); i++) {
			Point objectCenter = drawables.get(i).getCenter();
			if (!(drawables.get(i) instanceof Prop) && !(drawables.get(i) instanceof Room)) {
				if (renderBox.contains(objectCenter) | renderBox.contains(objectCenter)) {
					drawables.get(i).draw(g2);
				}
			} else if (extraLargeRenderBox.contains(objectCenter) | extraLargeRenderBox.contains(objectCenter)) {
				drawables.get(i).draw(g2);
			}
		}
		*/
		for (int i = 0; i < currentlyDrawn.size(); i++) {
			currentlyDrawn.get(i).draw(g2);
		}
		
		
		if (ben.weapon instanceof Gun) {
			g2.setColor(Color.BLACK);
			g2.drawOval(aimPoint.x-10, aimPoint.y-10, 20, 20);
			
			g2.setColor(Color.RED);
			g2.drawOval(aimPoint.x-3, aimPoint.y-3, 6, 6);
		}
		
		if (ben.weapon instanceof Wand && ((Wand)ben.weapon).orb.isActive()) {
			g2.setColor(Color.CYAN);
			g2.fillOval(((Wand)ben.weapon).orb.goal.x-5, ((Wand)ben.weapon).orb.goal.y-5, 10, 10);
			
			g2.setColor(Color.CYAN.darker());
			g2.fillOval(((Wand)ben.weapon).orb.goal.x-3, ((Wand)ben.weapon).orb.goal.y-3, 6, 6);
		}
		
		
		
		if (DEBUG) {
			for (Area a:ben.currentRoom.getCollisionAreas()) {
				if (a.solid) {
					g2.setColor(Color.RED);
				} else if (a.pit) {
					g2.setColor(Color.BLUE);
				} else {
					g2.setColor(Color.GREEN);
				}
				g2.draw(a);
				//g2.setColor(Color.BLACK);
				//g2.draw(a); 
			}
			
			g2.setColor(Color.CYAN);
			for (Room r:currentFloor.getRooms(new Line2D.Double(ben.getCenter(), relMousePoint))) {
				for (Point p:r.corners) {
					g2.fillOval(p.x-5, p.y-5, 10, 10);
				}
			}
		}	
		
		if (DEBUG2) {
			
			g2.setColor(Color.CYAN);
			ArrayList<Point> playerToMouse = PathFindUtils.getPath(ben.getCenter(), relMousePoint, 3, 3);
			if (playerToMouse != null) {
				g2.drawLine(ben.getCenter().x, ben.getCenter().y, playerToMouse.get(0).x, playerToMouse.get(0).y);
			}
			for (int i = 0; i < playerToMouse.size()-1; i++) {
				g2.drawLine(playerToMouse.get(i).x, playerToMouse.get(i).y, playerToMouse.get(i+1).x, playerToMouse.get(i+1).y);
			}
			
		}
	}
	
	public static Block getBlock(Room r, int bix, int biy) {
		return currentFloor.getBlock(r, bix, biy);
	}
	
	private static Comparator<Drawable> sortByZ = new Comparator<Drawable>() {
		public int compare(Drawable o1, Drawable o2) {
			return o1.getDrawHeight()-o2.getDrawHeight();
		}
	};
	private void sortObjects() {
		synchronized(drawables) {
			Collections.sort(drawables, sortByZ);
		}
	}
	
	public static void addDrawable(Drawable object) {
		synchronized(drawables) {
			drawables.add(object);
		}
	}
	
	public static void removeDrawable(Drawable object) {
		synchronized(drawables) {
			drawables.remove(object);
		}
	}
	
	public static void addTickable(Tickable object) {
		synchronized(tickables) {
			tickables.add(object);
		}
	}
	
	public static void removeTickable(Tickable object) {
		synchronized(tickables) {
			tickables.remove(object);
		}
	}
	public static void setCameraLocation(int x, int y) {
		translationX = -x+WIDTH/2;
		translationY = -y+HEIGHT/2;
	}
	public static Point getCameraLocation() {
		return new Point(-(int)translationX+WIDTH/2, -(int)translationY+HEIGHT/2);
	}
	
	public static void setSlowMotion(double slowMotionFactor) {
		deltaMultiplier = slowMotionFactor;
	}	
	public static void setSlowMotionTimer(double slowMotionFactor, double slowMotionTimer) {
		deltaMultiplier = slowMotionFactor;
		DrawPanel.slowMotionTimer = slowMotionTimer;
	}
	public static void resetSlowMotion() {
		deltaMultiplier = 1;
		slowMotionTimer = 0;
	}
	
	public static void shakeScreen(int scale, double scaleDegradation) {
		screenShaking = true;
		shakeScale = scale;
		shakeScaleDegradation = scaleDegradation;
	}
	public static void stopScreenShake() {
		screenShaking = false;
		screenShakeX = 0;
		screenShakeY = 0;
	}
	
	public static boolean lineIntersectsArea(Line2D line) {
		for (Area a:getAllCollisionAreas()) {
			if (a.intersectsLine(line)) {
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<Area> getAllCollisionAreas() {
		ArrayList<Area> result = new ArrayList<Area>();
		for (Room r:currentFloor.allRooms) {
			result.addAll(r.getCollisionAreas());
		}
		return result;
	}
		
	public static Point makeRelative(Point p) {
		return new Point((int)(p.x+Math.round(-translationX)), (int)(p.y+Math.round(-translationY)));
	}
	
	public void mouseDragged(MouseEvent m) {
		mousePoint = m.getPoint();
	}

	public void mouseMoved(MouseEvent m) {
		mousePoint = m.getPoint();
	}
}
