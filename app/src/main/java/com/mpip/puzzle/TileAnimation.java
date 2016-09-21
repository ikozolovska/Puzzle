/**
 * 
 */
package com.mpip.puzzle;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;


public class TileAnimation implements Runnable, AnimationListener {

	private GameTile tileToMove;
	Dimension destination;
	GameBoard gameBoard;
	
	public TileAnimation(GameTile toMove, Dimension dest, GameBoard board){
		tileToMove = toMove;
		destination = dest;
		gameBoard = board;
	}

	public void run() {
		
		Dimension empty = gameBoard.getEmptyPosition();
		Dimension emptyOnScreen = gameBoard.getOnScreenCord(empty);

		int changeX = 0;
		int changeY = 0;
		if(tileToMove.pos.x==empty.x && empty.y==tileToMove.pos.y-1){
			//empty to the north
			changeY = -gameBoard.getTileSize();
		}else if(tileToMove.pos.x==empty.x && empty.y==tileToMove.pos.y+1){
			//empty to the south
			changeY = gameBoard.getTileSize();
		}else if(tileToMove.pos.y==empty.y && empty.x==tileToMove.pos.x-1){
			//empty to the left
			changeX = -gameBoard.getTileSize();
		}else changeX = gameBoard.getTileSize(); // empty to the right
		
		//setting the animation
		TranslateAnimation anim = new TranslateAnimation(0, changeX, 0, changeY);
        anim.setDuration(1000);
        anim.setAnimationListener(this);

       
        tileToMove.startAnimation(anim);

	}

	public void onAnimationEnd(Animation animation) {
		gameBoard.moveTileToEmpty(tileToMove);
		
	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}
