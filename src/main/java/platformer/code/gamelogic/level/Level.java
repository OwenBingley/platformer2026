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


private void water(int col, int row, Map map, int fullness) {

   
    if(col < 0 || row < 0 ||
       col >= map.getTiles().length ||
       row >= map.getTiles()[0].length) {

        return;
    }



   // stops if  solid block
    if(map.getTiles()[col][row] != null &&
       map.getTiles()[col][row].isSolid()) {

        return;
    }



    // turns water into full water
    if(map.getTiles()[col][row] instanceof Water) {

        Water fullWater = new Water(
            col,
            row,
            tileSize,
            tileset.getImage("Full_water"),
            this,
            3
        );

        map.addTile(col, row, fullWater);

        return;
    }



    // PICK IMAGE
    String image = "";

    if(fullness == 3) {
        image = "Full_water";
    }

    else if(fullness == 2) {
        image = "Half_water";
    }

    else if(fullness == 1) {
        image = "Quarter_water";
    }

    else {
        image = "Falling_water";
    }



   
    // place water
    
    Water w = new Water(col,row,tileSize,tileset.getImage(image),this,fullness);

    map.addTile(col , row , w);



    // checks below
    boolean canGoDown = false;

    if(row + 1 < map.getTiles()[0].length) {

        Tile below = map.getTiles()[col][row + 1];

        if(below == null || !below.isSolid()) {
            canGoDown = true;
        }
    }



   // water falls down
    if(canGoDown) {

        // check if this is only a 1 block drop
        boolean oneBlockDrop = false;

        if(row + 2 < map.getTiles()[0].length) {

            Tile twoDown = map.getTiles()[col][row + 2];

            if(twoDown != null && twoDown.isSolid()) {
                oneBlockDrop = true;
            }
        }



        // if drop is small their is no point for falling water
        if(oneBlockDrop) {

            int nextFullness = fullness;

            if(fullness == 3) {
                nextFullness = 2;
            }

            else if(fullness == 2) {
                nextFullness = 1;
            }

            else if(fullness == 1) {
                nextFullness = 1;
            }
            
            water(col, row + 1, map, nextFullness);

            return;
        }



        // fall
        water(col, row + 1, map, 0);



        //water goes above falling water

        if(fullness > 0 && row - 1 >= 0) {

            int nextAbove = fullness;

            if(fullness == 3) {
                nextAbove = 2;
            }

            else if(fullness == 2) {
                nextAbove = 1;
            }

            else if(fullness == 1) {
                nextAbove = 1;
            }

            Tile above = map.getTiles()[col][row - 1];

            if(!(above instanceof Water)) {

                water(col, row - 1, map, nextAbove);

            }
        }

        return;
    }


    // works
    // falling water hits ground, it turns into full water
    if(fullness == 0) {

        Water full = new Water(col,row,tileSize,tileset.getImage("Full_water"),this,3);

        map.addTile(col, row, full);

       fullness = 3;
   }



 
    int nextFullness = fullness;

    if(fullness == 3) {
        nextFullness = 2;
    }

    else if(fullness == 2) {
        nextFullness = 1;
    }

    else if(fullness == 1) {
        nextFullness = 1;
    }



    
    // FLOW RIGHT

    if(col + 1 < map.getTiles().length) {

        Tile right = map.getTiles()[col + 1][row];

        if(right == null || !right.isSolid()) {

            boolean rightCanFall = false;

            if(row + 1 < map.getTiles()[0].length) {

                Tile belowRight = map.getTiles()[col + 1][row + 1];

                if(belowRight == null || !belowRight.isSolid()) {
                    rightCanFall = true;
                }
            }



            if(rightCanFall) {

               

                boolean bigDrop = false;

                if(row + 2 < map.getTiles()[0].length) {

                    Tile twoDownRight =
                        map.getTiles()[col + 1][row + 2];

                    if(twoDownRight == null ||
                       !twoDownRight.isSolid()) {

                        bigDrop = true;
                    }
                }

                if(bigDrop) {
                    water(col + 1, row, map, 0);
                }

                else {
                    water(col + 1, row + 1, map, nextFullness);
                }
            }

            else {
                water(col + 1, row, map, nextFullness);
            }
        }
    }



   
    // FLOW LEFT
    
    if(col - 1 >= 0) {

        Tile left = map.getTiles()[col - 1][row];

        if(left == null || !left.isSolid()) {

            boolean leftCanFall = false;

            if(row + 1 < map.getTiles()[0].length) {

                Tile belowLeft = map.getTiles()[col - 1][row + 1];

                if(belowLeft == null || !belowLeft.isSolid()) {
                    leftCanFall = true;
                }
            }



            // edge -> falling water
            if(leftCanFall) {

                // only use falling water
                // if more than 1 block down

                boolean bigDrop = false;

                if(row + 2 < map.getTiles()[0].length) {

                    Tile twoDownLeft =
                        map.getTiles()[col - 1][row + 2];

                    if(twoDownLeft == null ||
                       !twoDownLeft.isSolid()) {

                        bigDrop = true;
                    }
                }

                if(bigDrop) {
                    water(col - 1, row, map, 0);
                }

                else {
                    water(col - 1, row + 1, map, nextFullness);
                }
            }

            else {
                water(col - 1, row, map, nextFullness);
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