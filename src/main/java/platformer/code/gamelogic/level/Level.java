package platformer.code.gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 67;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData() {
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition * tileSize, yPosition * tileSize, this)); // TODO: objects vs
																									// tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if (flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					// else
					// addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new
					// ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}

	// #############################################################################################################
	// Your code goes here!
	// Please make sure you read the rubric/directions carefully and implement the
	// solution recursively!
	// #############################################################################################################


/**
 * Recursively propagates water through the game map based on fluid dynamics rules.
 * * Preconditions: 
 * - col and row must be within the bounds of the map's 2D tile array.
 * - map must be a valid, non-null Map object.
 * - fullness must be an integer between 0 and 3 inclusive:
 * 3: Full_water, 2: Half_water, 1: Quarter_water, 0: Falling_water.
 * * Postconditions:
 * - Updates the map tile at (col, row) with the appropriate Water object.
 * - Recursively triggers water propagation downwards if open air/non-solid exists below.
 * - Alternately, triggers lateral (left/right) propagation if downward path is blocked by a solid.
 */
private void water(int col, int row, Map map, int fullness) {
    // 1. Boundary Protection: Ensure we don't look or write out of the map borders
    if (col < 0 || col >= map.getTiles().length || row < 0 || row >= map.getTiles()[col].length) {
        return;
    }

    // 2. Solid Collision: Water cannot replace or flow through solid ground
    if (map.getTiles()[col][row] != null && map.getTiles()[col][row].isSolid()) {
        return;
    }

    // 3. Image Selection: Map fullness value to the correct string asset name
    String imageName;
    if (fullness == 3) {
        imageName = "Full_water";
    } else if (fullness == 2) {
        imageName = "Half_water";
    } else if (fullness == 1) {
        imageName = "Quarter_water";
    } else {
        imageName = "Falling_water"; // fullness == 0
    }

    // 4. Create and commit the Water tile instance to the 2D column-major array
    Water w = new Water(col, row, tileSize, tileset.getImage(imageName), this, fullness);
    map.addTile(col, row, w);

    // 5. Downward Flow Logic
    int nextRow = row + 1;
    boolean canFlowDown = false;

    // Check if the tile directly beneath exists and is not solid
    if (nextRow < map.getTiles()[col].length) {
        var tileBelow = map.getTiles()[col][nextRow];
        if (tileBelow == null || !tileBelow.isSolid()) {
            canFlowDown = true;
        }
    }

    if (canFlowDown) {
        // Rule 2 & 5: Reaches flat platform -> becomes Full_water (3), otherwise turns into Falling_water (0)
        // Check if the tile *below* the falling destination is solid to determine if it's hitting a platform.
        int floorRow = nextRow + 1;
        int nextFullness = 0; // Default to falling water

        if (floorRow < map.getTiles()[col].length) {
            var floorTile = map.getTiles()[col][floorRow];
            if (floorTile != null && floorTile.isSolid()) {
                nextFullness = 3; // Becomes a full block upon impact with a flat platform
            }
        }

        // Only recurse downwards if that position isn't already filled with identical water to prevent endless loops
        if (!(map.getTiles()[col][nextRow] instanceof Water && ((Water) map.getTiles()[col][nextRow]).getFullness() == nextFullness)) {
            water(col, nextRow, map, nextFullness);
        }
        
        // Rule 5 Note: If water falls down as Falling_water, it stops flowing sideways completely.
        return; 
    }

    // 6. Lateral (Horizontal) Flow Logic
    // This section executes only if down flow was blocked (water is sitting on a flat platform / solid ground)
    
    // Determine the fullness level for adjacent blocks based on current state (Rule 4)
    int lateralFullness;
    if (fullness == 3) {
        lateralFullness = 2; // Full out flows to Half
    } else if (fullness == 2) {
        lateralFullness = 1; // Half out flows to Quarter
    } else {
        lateralFullness = 1; // Quarter out flows continue to produce Quarter blocks
    }

    // Propagate Flow to the Right
    int rightCol = col + 1;
    if (rightCol < map.getTiles().length) {
        var rightTile = map.getTiles()[rightCol][row];
        // Ensure destination isn't solid ground and hasn't already been processed by equal or greater water
        if (rightTile == null || !rightTile.isSolid()) {
            if (!(rightTile instanceof Water && ((Water) rightTile).getFullness() >= lateralFullness)) {
                water(rightCol, row, map, lateralFullness);
            }
        }
    }

    // Propagate Flow to the Left
    int leftCol = col - 1;
    if (leftCol >= 0) {
        var leftTile = map.getTiles()[leftCol][row];
        // Ensure destination isn't solid ground and hasn't already been processed by equal or greater water
        if (leftTile == null || !leftTile.isSolid()) {
            if (!(leftTile instanceof Water && ((Water) leftTile).getFullness() >= lateralFullness)) {
                water(leftCol, row, map, lateralFullness);
            }
        }
    }
}

	
	
	
	
	
	
	// end of water 
	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());

		// Draw the map
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;
				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		// Draw the enemies
		for (int i = 0; i < enemies.length; i++) {
			enemies[i].draw(g);
		}

		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);

		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}