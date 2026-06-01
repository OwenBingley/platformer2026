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
					 else
					addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new
					 ArrayList<Gas>());
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

private void water(int col, int row, Map map, int fullness) {
    
	// Check if the position is out of bounds
	if (col < 0 || col >= map.getTiles().length || row < 0 || row >= map.getTiles()[col].length) {
        return;
    }
    // Check if the tile is solid and cannot be replaced by water
    Tile currentTile = map.getTiles()[col][row];

    // If the tile is solid and not a flower water cant be placed
    if (currentTile != null && currentTile.isSolid() && !(currentTile instanceof Flower)) {
        return;
		
    }
   
    // If the tile is already water, check if we need to change its fullness
    if (currentTile instanceof Water) {
        Water existingWater = (Water) currentTile;
        if (existingWater.getFullness() >= fullness) {
            return; 
        }
    }
   
   // Determine the image based on fullness
    String imageName;
    if (fullness == 3) {
        imageName = "Full_water";
    } else if (fullness == 2) {
        imageName = "Half_water";
    } else if (fullness == 1) {
        imageName = "Quarter_water";
    } else {
        imageName = "Falling_water"; 
    }
    
    
	// Places the water tile
    Water w = new Water(col, row, tileSize, tileset.getImage(imageName), this, fullness);
    map.addTile(col, row, w);

  // Trys yo flow downwards first
    int nextRow = row + 1;
    boolean canFlowDown = false;

  // Checks if the tile below is solid or not  
    if (nextRow < map.getTiles()[col].length) {
        Tile tileBelow = map.getTiles()[col][nextRow];
        if (!tileBelow.isSolid()) {
            canFlowDown = true;
        }
    }else{ // if previous statment returns false because of out of bounds, return so we can still flow down
		return;
		
	}
    // if it can flow down, it does it and returns so it doesnt flow left and right
    if (canFlowDown) {
        
        int floorRow = nextRow + 1;
        int nextFullness = 0; 
        
        
       
	    if (floorRow < map.getTiles()[col].length) {
            Tile floorTile = map.getTiles()[col][floorRow];
            if (floorTile != null && floorTile.isSolid()) {
                nextFullness = 3; 
            }
        }

       
        water(col, nextRow, map, nextFullness);
        
        
        return; 
    }
    
    int hFullness;
    
	if (fullness == 3) {
       hFullness = 2; 
    } else if (fullness == 2) {
       hFullness = 1; 
    } else {
       hFullness = 1; 
    }

    int rightCol = col + 1;
    if (rightCol < map.getTiles().length) {
        water(rightCol, row, map, hFullness);
    }

    int leftCol = col - 1;
    if (leftCol >= 0) {
        water(leftCol, row, map, hFullness);
    }
}
// end of water 
//
// ##############################################################################################################
// ##############################################################################################################
//
// start of gas
	
//Adds gas tiles until the requisite number of squares are filled or there is no more room 
private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
     
   
    
    // Preconditions: col and row must be valid map coordinates where a flower was triggered.
    // numSquaresToFill is the total target gas block budget (e.g., 20).
    // placedThisRound is an empty list supplied to track the iterative expansion queue.
    // Postconditions: The map is populated with up to numSquaresToFill gas tiles working outward 
    // iteratively based on strict positional directions.

    // Base validation check: exit immediately if there's no budget to fill
    if (numSquaresToFill <= 0) {
        return;
    }

    // Place the original gas tile where the flower used to be
    Gas initialGas = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
    map.addTile(col, row, initialGas);
    placedThisRound.add(initialGas);
    numSquaresToFill--;

    // Strict expansion array mapped exactly to the 1-8 handwritten priorities in 1000004029.jpg:
    // 1: Straight Up {0, -1} | 2: Up-Right {1, -1} | 3: Up-Left {-1, -1}
    // 4: Straight Right {1, 0} | 5: Straight Left {-1, 0}
    // 6: Straight Down {0, 1} | 7: Down-Right {1, 1} | 8: Down-Left {-1, 1}
    int[][] directions = {
        {0, -1}, // 1. Straight Up
        {1, -1}, // 2. Up-Right
        {-1, -1}, // 3. Up-Left
        {1, 0}, // 4. Straight Right
        {-1, 0}, // 5. Straight Left
        {0, 1}, // 6. Straight Down
        {1, 1}, // 7. Down-Right
        {-1, 1} // 8. Down-Left
    };

    // Index pointer tracking our position inside our iterative "queue" (placedThisRound)
    int head = 0;

    // Iteratively loop through tiles placed this round and use them as new center expansion origins
    while (head < placedThisRound.size() && numSquaresToFill > 0) {
        Gas currentGas = placedThisRound.get(head);
        
        // Convert the gas tile's pixel coordinate values back into grid array indices
        int currentCol = (int) currentGas.getX();
        int currentRow = (int) currentGas.getY();

        // Check all 8 neighboring directions around the current tile in exact priority order
        for (int i = 0; i < directions.length; i++) {
            // Early exit check if budget fills up mid-step
            if (numSquaresToFill <= 0) {
                return;
            }

            int targetCol = currentCol + directions[i][0];
            int targetRow = currentRow + directions[i][1];

            // 1. Array Bound Validation: prevent IndexOutOfBoundsExceptions on column-major array
            if (targetCol >= 0 && targetCol < map.getTiles().length) {
                if (targetRow >= 0 && targetRow < map.getTiles()[targetCol].length) {
                    
                    Tile targetTile = map.getTiles()[targetCol][targetRow];

                    // 2. Tile Validation: Only expand if space is empty (null or non-solid passing tile)
                    // and make sure we aren't overwriting an existing Gas tile
                    if ((targetTile == null || !targetTile.isSolid()) && !(targetTile instanceof Gas)) {
                        
                        // Construct the new Gas block
                        Gas newGas = new Gas(targetCol, targetRow, tileSize, tileset.getImage("GasOne"), this, 0);
                        map.addTile(targetCol, targetRow, newGas);
                        
                        // Append to list so this tile can eventually act as an expansion origin too
                        placedThisRound.add(newGas);
                        numSquaresToFill--;
                    }
                }
            }
        }
        
        // Step forward in our array list queue to evaluate the next gas tile placed
        head++;
    }
           
                 
}

// end of gas
//
// ###############################################################################################################
// ###############################################################################################################
//
// start of draw 
public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
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