package nl.br.map.floors;

import java.util.ArrayList;

import en.lib.io.IO;
import en.lib.math.MathUtils;
import nl.br.main.BossRush;
import nl.br.map.floorfilling.Room;
import nl.br.panels.DrawPanel;
import nl.br.panels.LoadingScreen;

public class FloorConfig {
	private String config;
	
	private ArrayList<String> SpawnConfigs = new ArrayList<String>();
	
	private ArrayList<String> CrossingConfigs = new ArrayList<String>();
	
	private ArrayList<String> TopTConfigs = new ArrayList<String>();
	private ArrayList<String> RightTConfigs = new ArrayList<String>();
	private ArrayList<String> BottomTConfigs = new ArrayList<String>();
	private ArrayList<String> LeftTConfigs = new ArrayList<String>();
	
	private ArrayList<String> VerticalCorridorConfigs = new ArrayList<String>();
	private ArrayList<String> HorizontalCorridorConfigs = new ArrayList<String>();
	
	private ArrayList<String> TRCornerConfigs = new ArrayList<String>();
	private ArrayList<String> BRCornerConfigs = new ArrayList<String>();
	private ArrayList<String> BLCornerConfigs = new ArrayList<String>();
	private ArrayList<String> TLCornerConfigs = new ArrayList<String>();
	
	private ArrayList<String> TopEndConfigs = new ArrayList<String>();
	private ArrayList<String> RightEndConfigs = new ArrayList<String>();
	private ArrayList<String> BottomEndConfigs = new ArrayList<String>();
	private ArrayList<String> LeftEndConfigs = new ArrayList<String>();
	
	private ArrayList<String> anyTopOpening = new ArrayList<String>();
	private ArrayList<String> anyRightOpening = new ArrayList<String>();
	private ArrayList<String> anyBottomOpening = new ArrayList<String>();
	private ArrayList<String> anyLeftOpening = new ArrayList<String>();
	
	public FloorConfig(String pathConfig) {
		config = IO.readFile(pathConfig);
		//TODO buffer in the files so they dont need to be read over and over
		for (String s:IO.readStringArray(IO.getPropertyValue("Spawn", config)))	{
			SpawnConfigs.add(IO.readFile(s));
		}
		
		for (String s:IO.readStringArray(IO.getPropertyValue("Crossing", config)))	{
			CrossingConfigs.add(IO.readFile(s));
		}
		
		for (String s:IO.readStringArray(IO.getPropertyValue("TopT", config)))	{
			TopTConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("RightT", config)))	{
			RightTConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("BottomT", config)))	{
			BottomTConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("LeftT", config)))	{
			LeftTConfigs.add(IO.readFile(s));
		}
		
		for (String s:IO.readStringArray(IO.getPropertyValue("VerticalCorridor", config)))	{
			VerticalCorridorConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("HorizontalCorridor", config)))	{
			HorizontalCorridorConfigs.add(IO.readFile(s));
		}
		
		for (String s:IO.readStringArray(IO.getPropertyValue("TRCorner", config)))	{
			TRCornerConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("BRCorner", config)))	{
			BRCornerConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("BLCorner", config)))	{
			BLCornerConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("TLCorner", config)))	{
			TLCornerConfigs.add(IO.readFile(s));
		}
		
		for (String s:IO.readStringArray(IO.getPropertyValue("TopEnd", config)))	{
			TopEndConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("RightEnd", config)))	{
			RightEndConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("BottomEnd", config)))	{
			BottomEndConfigs.add(IO.readFile(s));
		}
		for (String s:IO.readStringArray(IO.getPropertyValue("LeftEnd", config)))	{
			LeftEndConfigs.add(IO.readFile(s));
		}
		
		anyTopOpening.addAll(SpawnConfigs);
		anyTopOpening.addAll(CrossingConfigs);
		anyTopOpening.addAll(TopTConfigs);
		anyTopOpening.addAll(RightTConfigs);
		anyTopOpening.addAll(LeftTConfigs);
		anyTopOpening.addAll(VerticalCorridorConfigs);
		anyTopOpening.addAll(TRCornerConfigs);
		anyTopOpening.addAll(TLCornerConfigs);
		anyTopOpening.addAll(TopEndConfigs);
		
		anyRightOpening.addAll(SpawnConfigs);
		anyRightOpening.addAll(CrossingConfigs);
		anyRightOpening.addAll(TopTConfigs);
		anyRightOpening.addAll(RightTConfigs);
		anyRightOpening.addAll(BottomTConfigs);
		anyRightOpening.addAll(HorizontalCorridorConfigs);
		anyRightOpening.addAll(TRCornerConfigs);
		anyRightOpening.addAll(BRCornerConfigs);
		anyRightOpening.addAll(RightEndConfigs);
		
		anyBottomOpening.addAll(SpawnConfigs);
		anyBottomOpening.addAll(CrossingConfigs);
		anyBottomOpening.addAll(BottomTConfigs);
		anyBottomOpening.addAll(RightTConfigs);
		anyBottomOpening.addAll(LeftTConfigs);
		anyBottomOpening.addAll(VerticalCorridorConfigs);
		anyBottomOpening.addAll(BRCornerConfigs);
		anyBottomOpening.addAll(BLCornerConfigs);
		anyBottomOpening.addAll(BottomEndConfigs);
		
		anyLeftOpening.addAll(SpawnConfigs);
		anyLeftOpening.addAll(CrossingConfigs);
		anyLeftOpening.addAll(TopTConfigs);
		anyLeftOpening.addAll(LeftTConfigs);
		anyLeftOpening.addAll(BottomTConfigs);
		anyLeftOpening.addAll(HorizontalCorridorConfigs);
		anyLeftOpening.addAll(TLCornerConfigs);
		anyLeftOpening.addAll(BLCornerConfigs);
		anyLeftOpening.addAll(LeftEndConfigs);
	}
	
	public String[][] generateFullFloor() {
		String[][] result = new String[Floor.floorSize][Floor.floorSize];
		result[Floor.spawnX][Floor.spawnY] = SpawnConfigs.get(0);
		LoadingScreen.loadingLabel = "Loading roomTypes..."; 
		for (int y = 0; y < Floor.floorSize; y++) {
			for (int x = 0; x < Floor.floorSize; x++) {
				//System.out.println("Loading roomTypes: " + MathUtils.roundToDecimals((y*Floor.floorSize+x)/(double)(Floor.floorSize*Floor.floorSize), 2)*100 + "%");
				LoadingScreen.loadPercent = (y*Floor.floorSize+x)/(double)(Floor.floorSize*Floor.floorSize);
				BossRush.loadingScreen.paintImmediately(0, 0, BossRush.WIDTH, BossRush.HEIGHT);
				result[y][x] = getRandom(x, y, result);
			}
		}
		return result;
	}
	
	public String[][] generateNiceFloor() {
		String[][] result = new String[Floor.floorSize][Floor.floorSize];
		result[Floor.spawnY][Floor.spawnX] = SpawnConfigs.get(0);
		LoadingScreen.loadingLabel = "Loading roomTypes..."; 
		boolean finished = false;
		while(!finished) {
			finished = true;
			for (int y = 0; y < Floor.floorSize; y++) {
				for (int x = 0; x < Floor.floorSize; x++) {
					if (result[y][x] == null && y != 0 && hasBottomOpening(result[y-1][x]) == 1) {
						if (getOpenNeighBours(x, y, result) > 1) {
							result[y][x] = getRandom(x, y, result);
						} else {
							int corridorLength = MathUtils.randInt(1, 3);
							boolean corridorEnded = false;
							for (int i = 0; i < corridorLength; i++) {
								if (result[y+i][x] == null) {
									if (y+i+1 == Floor.floorSize || hasTopOpening(result[y+i+1][x]) == -1) {
										result[y+i][x] = pickRandom(TopEndConfigs);
										corridorEnded = true;
										break;
									} else {
										result[y+i][x] = pickRandom(VerticalCorridorConfigs);
									}
								}
							}
							if (!corridorEnded && y+corridorLength < Floor.floorSize && result[y+corridorLength][x] == null) {
								result[y+corridorLength][x] = getRandom(x, y+corridorLength, result);
							}
						}
						finished = false;
					}
					if (result[y][x] == null && x != Floor.floorSize-1 && hasLeftOpening(result[y][x+1]) == 1) {
						if (getOpenNeighBours(x, y, result) > 1) {
							result[y][x] = getRandom(x, y, result);
						} else {
							int corridorLength = MathUtils.randInt(1, 3);
							boolean corridorEnded = false;
							for (int i = 0; i < corridorLength; i++) {
								if (result[y][x-i] == null) {
									if (x-i-1 == -1 || hasRightOpening(result[y][x-i-1]) == -1) {
										result[y][x-i] = pickRandom(RightEndConfigs);
										corridorEnded = true;
										break;
									} else {
										result[y][x-i] = pickRandom(HorizontalCorridorConfigs);
									}
								}
							}
							if (!corridorEnded && x-corridorLength > 0 && result[y][x-corridorLength] == null) {
								result[y][x-corridorLength] = getRandom(x-corridorLength, y, result);
							}
						}
						finished = false;
					}
					if (result[y][x] == null && y != Floor.floorSize-1 && hasTopOpening(result[y+1][x]) == 1) {
						if (getOpenNeighBours(x, y, result) > 1) {
							result[y][x] = getRandom(x, y, result);
						} else {
							int corridorLength = MathUtils.randInt(1, 3);
							boolean corridorEnded = false;
							for (int i = 0; i < corridorLength; i++) {
								if (result[y-i][x] == null) {
									if (y-i-1 == -1 || hasBottomOpening(result[y-i-1][x]) == -1) {
										result[y-i][x] = pickRandom(BottomEndConfigs);
										corridorEnded = true;
										break;
									} else {
										result[y-i][x] = pickRandom(VerticalCorridorConfigs);
									}
								}
							}
							if (!corridorEnded && y-corridorLength > 0 && result[y-corridorLength][x] == null) {
								result[y-corridorLength][x] = getRandom(x, y-corridorLength, result);
							}
						}
						finished = false;
					}
					if (result[y][x] == null && x != 0 && hasRightOpening(result[y][x-1]) == 1) {
						if (getOpenNeighBours(x, y, result) > 1) {
							result[y][x] = getRandom(x, y, result);
						} else {
							int corridorLength = MathUtils.randInt(1, 3);
							boolean corridorEnded = false;
							for (int i = 0; i < corridorLength; i++) {
								if (result[y][x+i] == null) {
									if (x+i+1 == Floor.floorSize || hasLeftOpening(result[y][x+i+1]) == -1) {
										result[y][x+i] = pickRandom(LeftEndConfigs);
										corridorEnded = true;
										break;
									} else {
										result[y][x+i] = pickRandom(HorizontalCorridorConfigs);
									}
								}
							}
							if (!corridorEnded && x+corridorLength < Floor.floorSize && result[y][x+corridorLength] == null) {
								result[y][x+corridorLength] = getRandom(x+corridorLength, y, result);
							}
						}
						finished = false;
					}
					
				}
			}
		}
		return result;
	}
	
	private int getOpenNeighBours(int x, int y, String[][] grid) {
		int result = 0;
		if (y != 0 && hasBottomOpening(grid[y-1][x]) == 1) {
			result++;
		}
		if (x != 0 && hasRightOpening(grid[y][x-1]) == 1) {
			result++;
		}
		if (y != grid.length-1 && hasTopOpening(grid[y+1][x]) == 1) {
			result++;
		}
		if (x != grid[y].length-1 && hasLeftOpening(grid[y][x+1]) == 1) {
			result++;
		}
		return result;
	}
	
	private int hasTopOpening(String s) {
		if (s == null) {
			return 0;
		}
		if (anyTopOpening.contains(s)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	private int hasRightOpening(String s) {
		if (s == null) {
			return 0;
		}
		if (anyRightOpening.contains(s)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	private int hasBottomOpening(String s) {
		if (s == null) {
			return 0;
		}
		if (anyBottomOpening.contains(s)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	private int hasLeftOpening(String s) {
		if (s == null) {
			return 0;
		}
		if (anyLeftOpening.contains(s)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	private ArrayList<String> getAll(boolean includeSpawn) {
		ArrayList<String> all = new ArrayList<String>();
		if (includeSpawn) {
			all.addAll(SpawnConfigs);
		}
		all.addAll(CrossingConfigs);
		
		all.addAll(TopTConfigs);
		all.addAll(RightTConfigs);
		all.addAll(BottomTConfigs);
		all.addAll(LeftTConfigs);

		all.addAll(VerticalCorridorConfigs);
		all.addAll(HorizontalCorridorConfigs);

		all.addAll(TRCornerConfigs);
		all.addAll(BRCornerConfigs);
		all.addAll(BLCornerConfigs);
		all.addAll(TLCornerConfigs);

		all.addAll(TopEndConfigs);
		all.addAll(RightEndConfigs);
		all.addAll(BottomEndConfigs);
		all.addAll(LeftEndConfigs);
		return all;
	}
	
	private String pickRandom(ArrayList<String> possibilities) {
		int randomResult = MathUtils.randInt(0, possibilities.size()-1);
		String result = possibilities.get(randomResult);
		return result;
	}
	
	private String getRandom(int topOpen, int rightOpen, int bottomOpen, int leftOpen) {
		ArrayList<String> possibleResults = new ArrayList<String>();
		if (topOpen != 1 && rightOpen != 1 && bottomOpen != 1 && leftOpen != 1) {
			possibleResults.addAll(getAll(false));
		} else {
			if (topOpen == 1) {
				possibleResults.addAll(anyTopOpening);
			}
			if (rightOpen == 1) {
				if (possibleResults.isEmpty()) {
					possibleResults.addAll(anyRightOpening);
				} else {
					ArrayList<String> toRemove = new ArrayList<String>();
					for (int i = 0; i < possibleResults.size(); i++) {
						if (!anyRightOpening.contains(possibleResults.get(i))) {
							toRemove.add(possibleResults.get(i));
						}
					}
					possibleResults.removeAll(toRemove);
				}
			}
			if (bottomOpen == 1) {
				if (possibleResults.isEmpty()) { 
					possibleResults.addAll(anyBottomOpening);
				} else {
					ArrayList<String> toRemove = new ArrayList<String>();
					for (int i = 0; i < possibleResults.size(); i++) {
						if (!anyBottomOpening.contains(possibleResults.get(i))) {
							toRemove.add(possibleResults.get(i));
						}
					}
					possibleResults.removeAll(toRemove);
				}
			}
			if (leftOpen == 1) {
				if (possibleResults.isEmpty()) {
					possibleResults.addAll(anyLeftOpening);
				} else {
					ArrayList<String> toRemove = new ArrayList<String>();
					for (int i = 0; i < possibleResults.size(); i++) {
						if (!anyLeftOpening.contains(possibleResults.get(i))) {
							toRemove.add(possibleResults.get(i));
						}
					}
					possibleResults.removeAll(toRemove);
				}
			}
		}
		
		if (topOpen == -1) {
			possibleResults.removeAll(anyTopOpening);
		}
		if (rightOpen == -1) {
			possibleResults.removeAll(anyRightOpening);
		}
		if (bottomOpen == -1) {
			possibleResults.removeAll(anyBottomOpening);
		}
		if (leftOpen == -1) {
			possibleResults.removeAll(anyLeftOpening);
		}
		
		possibleResults.removeAll(SpawnConfigs);
		
		//System.out.println("Top:"+topOpen+",Right:"+rightOpen+",Bottom:"+bottomOpen+",Left:"+leftOpen);
		
		if (possibleResults.size() > 0) {
			return pickRandom(possibleResults);
		} else {
			return null;
		}
	}

	private String getRandom(int x, int y, String[][] grid) {
		if (grid[y][x] == null) {
			int mustHaveTopOpen, mustHaveRightOpen, mustHaveBottomOpen, mustHaveLeftOpen;
			if (y != 0) {
				mustHaveTopOpen = hasBottomOpening(grid[y-1][x]);
			} else {
				mustHaveTopOpen = -1;
			}
			
			if (x != grid[y].length-1) {
				mustHaveRightOpen = hasLeftOpening(grid[y][x+1]);
			} else {
				mustHaveRightOpen = -1;
			}
			
			if (y != grid.length-1) {
				mustHaveBottomOpen = hasTopOpening(grid[y+1][x]);
			} else {
				mustHaveBottomOpen = -1;
			}
			
			if (x != 0) {
				mustHaveLeftOpen = hasRightOpening(grid[y][x-1]);
			} else {
				mustHaveLeftOpen = -1;
			}
			
			return getRandom(mustHaveTopOpen, mustHaveRightOpen, mustHaveBottomOpen, mustHaveLeftOpen);
		} else {
			return grid[y][x];
		}
		
	}
}
