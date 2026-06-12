package platformer.code.gamelogic.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import platformer.code.gameengine.input.MouseInputManager;
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

     // slower
     private ArrayList<Water> waterList = new ArrayList<>();
	 // slower
     
    

     // damage
    private ArrayList<Gas> gasList = new ArrayList<>();
	 // damage
	
	 private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();
    
	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 67;
    // damage
    private long gasDamageTimer = 0;
	private long gasDamageInterval = 7; 
	// damage
    private boolean teleport = false;
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
        gasDamageTimer = 0;
		gasList.clear();
		waterList.clear();
		flowers.clear();
		enemiesList.clear();
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
			   else if (values[x][y] == 22)
				    tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Thomp"), this);
			  else if (values[x][y] == 23) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("teleportFlower"), this, 3);
					flowers.add((Flower) tiles[x][y]);
			}
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
             if (MouseInputManager.isButtonDown(1) && player.canTeleport()) {
    // teleport
    teleportPlayer((int) MouseInputManager.getMouseX(),(int) MouseInputManager.getMouseY()); 
     // teleport
}
		
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
                Flower currentFlower = flowers.get(i);
                if (currentFlower.getHitbox().isIntersecting(player.getHitbox())) {
                    
                    if (currentFlower.getType() == 1) {
                        water(currentFlower.getCol(), currentFlower.getRow(), map, 3);
                    } else if (currentFlower.getType() == 2) {
                        addGas(currentFlower.getCol(), currentFlower.getRow(), map, 20, new ArrayList<Gas>());
                    // teleport
					} else if (currentFlower.getType() == 3) {
                        player.enableTeleport(); 
                    }
                    // teleport
					
					flowers.remove(i);
                    i--;
                }
            }
			 ///////  slower
			 
			 boolean inWater = false;
			 for(Water w: waterList){
				if(player.getHitbox().isIntersecting(w.getHitbox())) {
					inWater = true;
				}
				}
                if(inWater){
                 player.walkSpeed = 100;
				 player.jumpPower = 1350;
				} else {
					player.walkSpeed = 400;
				    player.jumpPower = 1350;
				}

			 
             ////// slower
		
		
			
	 ////// damage
			   boolean isInGas = false;
	            for(int d = 0; d < gasList.size(); d++) {
				if(gasList.get(d).getHitbox().isIntersecting(player.getHitbox()) ) {
					isInGas = true;
				  if(gasDamageTimer == 0){
					gasDamageTimer = System.currentTimeMillis();
				  }
				  else {
                     if((System.currentTimeMillis() - gasDamageTimer) / 1000 >= gasDamageInterval) {
						
				     onPlayerDeath();
					 System.out.println("died to gas");
					 gasDamageTimer = 0;
					}
				}
			  }
			}
			  if(!isInGas){
               gasDamageTimer = 0;
				//im out of gas
			  }
			 
			



			 // Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
			System.out.println("died due to enemy");
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

  ////// slower
    waterList.add(w);
  ////// slower
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

    if (numSquaresToFill <= 0) {
        return;
    }

    Gas initialGas = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
    map.addTile(col, row, initialGas);
    placedThisRound.add(initialGas);
    numSquaresToFill--;

   
    int[][] directions = {
        {0, -1}, //  Straight Up
        {1, -1}, //  Up-Right
        {-1, -1}, //  Up-Left
        {1, 0}, //  Straight Right
        {-1, 0}, //  Straight Left
        {0, 1}, //  Straight Down
        {1, 1}, //  Down-Right
        {-1, 1} //  Down-Left
    };

    int head = 0;

    while (head < placedThisRound.size() && numSquaresToFill > 0) {
        Gas currentGas = placedThisRound.get(head);
        gasList.add(currentGas); 
		 
        int currentCol = (int) currentGas.getCol();
        int currentRow = (int) currentGas.getRow();

        for (int i = 0; i < directions.length; i++) {
            if (numSquaresToFill <= 0) {
                return;
            }

            int targetCol = currentCol + directions[i][0];
            int targetRow = currentRow + directions[i][1];

            if (targetCol >= 0 && targetCol < map.getTiles().length) {
                if (targetRow >= 0 && targetRow < map.getTiles()[targetCol].length) {
                    
                    Tile targetTile = map.getTiles()[targetCol][targetRow];

                    if ((targetTile == null || !targetTile.isSolid()) && !(targetTile instanceof Gas)) {
                        
                        Gas newGas = new Gas(targetCol, targetRow, tileSize, tileset.getImage("GasOne"), this, 0);
                        map.addTile(targetCol, targetRow, newGas);
                        gasList.add(newGas);
                        placedThisRound.add(newGas);
                        numSquaresToFill--;
                    }
                }
           
			}
        // damage
			
		// damage
	}
        
        head++;
    }
         
                 
}

// end of gas
//
// ###############################################################################################################
// ###############################################################################################################
//
// start of teleport

public void teleportPlayer(int mouseX, int mouseY){

if(!player.canTeleport()){
	return;
}

float worldX = mouseX + camera.getX();
float worldY = mouseY + camera.getY();

int col = (int)(worldX / tileSize);
int row = (int)(worldY / tileSize);

if( col < 0 || col>= map.getWidth() || row < 0 || row >= map.getHeight()){
	return;
}

Tile tile = map.getTiles()[col][row];


if(tile == null || !tile.isSolid()){
	player.teleport(col * tileSize, row * tileSize);
}

System.out.println(player.canTeleport());
player.disableTeleport();
System.out.println(player.canTeleport());


}

// end of teleport
//
// ################################################################################################
// ################################################################################################
//
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
         // teleport
         if (player.canTeleport()) {
          g.setColor(Color.GREEN); 
            g.setFont(new Font("Arial", Font.BOLD, 20));
    
    
            int textX = (int) player.getX();
              int textY = (int) player.getY() - 10; 
    
           g.drawString("Can Teleport", textX, textY);
}
         // teleport




		 // damage
         g.setColor(Color.RED);
		 g.setFont(new Font("Arial", Font.BOLD, 30));
		if (gasDamageTimer != 0) {
		g.drawString((System.currentTimeMillis() - gasDamageTimer)/1000 + "", (int) player.getX(), (int) player.getY() +10);
		}
	   	// damage
		
        


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