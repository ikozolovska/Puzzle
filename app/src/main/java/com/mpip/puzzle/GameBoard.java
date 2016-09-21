package com.mpip.puzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ViewSwitcher;


public class GameBoard implements OnClickListener{

	public final static int ORIENTATION_PORTRAIT = 0;
	public final static int ORIENTATION_HORIZONTAL = 1;
	
	private RelativeLayout screen;
	private Context context;
	private Dimension screenResolution;
	private Dimension gameSize = null;
	private int tileSize;
	private volatile int counter = 0;
	private int orientation;
	public ViewSwitcher ingameViewSwitcher;
	private GameTile[][] tiles;
	private GameTile currentTile;
	Button back;
	
	public GameBoard(Dimension gameSize, RelativeLayout scr, int orientation, Context con, ViewSwitcher vs, Button bt){
		back=bt;
		ingameViewSwitcher=vs;
		this.gameSize = gameSize;
		this.screen = scr;
		this.context = con;
		this.orientation = orientation;

		if(orientation==ORIENTATION_HORIZONTAL){
			this.gameSize = new Dimension(gameSize.y, gameSize.x);
		}
		
		tiles = new GameTile[this.gameSize.x][this.gameSize.y];
		calculateTileSize();
		
	}
	
	public void loadTiles(PuzzleTile[][] res){
		for(int y=0; y<gameSize.y; y++){
			for(int x=0; x<gameSize.x; x++){
				if(res[x][y]==null)
					tiles[x][y] = null; 
				else{
				tiles[x][y] = new GameTile(this.context, res[x][y].getDrawable(), res[x][y].getNumber());
				tiles[x][y].setClickable(true);
				tiles[x][y].setOnClickListener(this);
				tiles[x][y].pos = new Dimension(x,y);
				}
			}
		}
	}	
	
	public void calculateTileSize(){

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screenResolution = new Dimension(display.getWidth(), display.getHeight());

		int tSize = (int) (screenResolution.x/gameSize.x);
		if(tSize*gameSize.y>screenResolution.y){
			tSize = (int) (screenResolution.y/gameSize.y);
		}

		tileSize = tSize;
		
		
	}
	
	public void drawBoard(){
		for(int x=0; x<gameSize.x; x++){
			for(int y=0; y<gameSize.y; y++){
				if(tiles[x][y]==null) continue;
				tiles[x][y].params = new LayoutParams(tileSize, tileSize);
				Dimension onScreen = getOnScreenCord(new Dimension(x,y));
				tiles[x][y].params.setMargins(onScreen.x, onScreen.y, 0, 0);
				screen.addView(tiles[x][y], tiles[x][y].params);
				
			}
		}
	}
	
	public void reDrawBoard(){
		screen.removeAllViews();
		for(int x=0; x<gameSize.x; x++){
			for(int y=0; y<gameSize.y; y++){
				if(tiles[x][y]==null) continue;
				screen.addView(tiles[x][y], tiles[x][y].params);
				
			}
		}
	}
	
	public GameTile getTile(Dimension at){
		return tiles[at.x][at.y];
	}
	
	public Dimension getTilePos(GameTile t){
		for(int x=0; x<gameSize.x; x++){
			for(int y=0; y<gameSize.y; y++){
				if(tiles[x][y]==null) continue;
				if(t.equals(tiles[x][y])) return new Dimension(x,y);
			}
		}
		return null;
	}
	
	/**
	 * Calculates tile position on screen, given its position on the game board.
	 * @param input
	 * @return
	 */
	public Dimension getOnScreenCord(Dimension input){
		return new Dimension(tileSize*input.x, tileSize*input.y);
	}
	
	private boolean canBeMoved(GameTile tile){
		Dimension empty = getEmptyPosition();
		if(tile.pos.x==empty.x){
			if(empty.y==tile.pos.y-1 || empty.y==tile.pos.y+1) return true;
		}
		if(tile.pos.y==empty.y){
			if(empty.x==tile.pos.x-1 || empty.x==tile.pos.x+1) return true;
		}
		return false;
	}
	
	public Dimension getEmptyPosition(){
		
		for(int x=0; x<gameSize.x; x++){
			for(int y=0; y<gameSize.y; y++){
				if(tiles[x][y]==null) {
					return new Dimension(x,y);
				}
			}
		}
		
		throw new RuntimeException("No empty space! Not a single cell is null! WTF?");
	
	}
	
	synchronized private void animateTile(GameTile clickedTile, int number ){
		Dimension emptyOnScreen = getOnScreenCord(getEmptyPosition());
		Dimension empty = getEmptyPosition();
		//calculating the translation for animation
		int changeX = 0;
		int changeY = 0;
		if(clickedTile.pos.x==empty.x && empty.y==clickedTile.pos.y-1){
			//empty to the north
			changeY = -tileSize;
		}else if(clickedTile.pos.x==empty.x && empty.y==clickedTile.pos.y+1){
			//empty to the south
			changeY = tileSize;
		}else if(clickedTile.pos.y==empty.y && empty.x==clickedTile.pos.x-1){
			//empty to the left
			changeX = -tileSize;
		}else changeX = tileSize; // empty to the right
		
		//setting the animation
		TranslateAnimation anim = new TranslateAnimation(0, changeX, 0, changeY);
        anim.setDuration(1000);
        anim.setFillAfter(true);        
        //after animation need to really change a position of tile
        //and change position of the empty space
        
        
        //and the animation starts!
        currentTile = clickedTile;
        Log.d("Puzzle", "Animation starts! ("+ counter +")");
        anim.setAnimationListener(new TileAnimationListener(clickedTile, this));
        //reDrawBoard();
        clickedTile.startAnimation(anim);
	}
	
	
	//when a tile is clicked
	synchronized public void onClick(View v) {
		counter++;

		
		GameTile clickedTile = (GameTile) v;
		if(canBeMoved(clickedTile)==false){
			return;
		}

		moveTileToEmpty(clickedTile);

		if(isSolved()){
			ingameViewSwitcher.showNext();
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("You solved the puzzle! Congratulations!")
			       .setCancelable(false)
			       .setPositiveButton("Thanks.", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int id) {
						   Intent intent = new Intent(context, MainMenuActivity.class);
						   context.startActivity(intent);
						   //dialog.dismiss();
					   }
				   })
					.setNegativeButton("View Picture", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							back.setOnClickListener(new OnClickListener() {
								public void onClick(View v)
								{
									Intent intent = new Intent(context, MainMenuActivity.class);
									context.startActivity(intent);
								}
							});
							dialog.dismiss();

						}
					});

						AlertDialog alert = builder.create();
						alert.show();

					}

		}
	
	synchronized public void moveTileToEmpty(GameTile toMove){
		Dimension empty = getEmptyPosition(); // getting pos of the empty spot before change
		Dimension destOnScreen = getOnScreenCord(getEmptyPosition());
		tiles[toMove.pos.x][toMove.pos.y]=null; // old place
		tiles[empty.x][empty.y] = toMove; //new place
		
		
		toMove.pos = empty;
		
		//screen.removeView(toMove);
		toMove.params.setMargins(destOnScreen.x, destOnScreen.y, 0, 0);
		toMove.setClickable(true);
		toMove.setOnClickListener(this);
		//screen.addView(toMove, toMove.params);
		reDrawBoard();

	}

	boolean isSolved(){
		int n = 0;
		for(int y=0; y<gameSize.y; y++){
			for(int x=0; x<gameSize.x; x++){
				if(tiles[x][y]==null){
					if(x==gameSize.x-1 && y==gameSize.y-1)
						return true;
					else return false;
				}
				if(tiles[x][y].getCheckNumber()!=n) return false; 
				n++;
				
			}
		}
		return true;
	}
	

	public Dimension getGameSize() {
		return gameSize;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public RelativeLayout getScreen() {
		return screen;
	}

	public void setScreen(RelativeLayout screen) {
		this.screen = screen;
	}

	public Dimension getScreenResolution() {
		return screenResolution;
	}

	public void setScreenResolution(Dimension screenResolution) {
		this.screenResolution = screenResolution;
	}

	public void setGameSize(Dimension gameSize) {
		this.gameSize = gameSize;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	
}
