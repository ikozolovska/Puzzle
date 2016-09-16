package com.mpip.puzzle;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;


public class TileAnimationListener implements AnimationListener {
	GameTile currentTile;
	GameBoard board;

	public TileAnimationListener(GameTile currentTile, GameBoard board){
		this.currentTile = currentTile;
		this.board = board;
		
	}

	public void onAnimationEnd(Animation animation) {
		board.moveTileToEmpty(currentTile);
		board.reDrawBoard();

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
