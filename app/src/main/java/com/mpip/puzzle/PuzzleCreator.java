package com.mpip.puzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.Random;


public class PuzzleCreator {
	
	private Bitmap sourceImage;
	private ArrayList<PuzzleTile> result = new ArrayList<PuzzleTile>();
	private Dimension outputBoardSize;
	private int outputTileSize;
	private GameBoard board;
	
	private class Randomizer{
		
		PuzzleTile[][] array;
		Dimension nullPos;
		Random gen;
		
		public Randomizer(ArrayList<PuzzleTile> list){
			array = new PuzzleTile[outputBoardSize.x][outputBoardSize.y];
			int z=0;
			for(int y=0;y<outputBoardSize.y;y++){
				for(int x=0;x<outputBoardSize.x;x++){
					if(x==outputBoardSize.x-1 && y==outputBoardSize.y-1)
						break;
					else{
						array[x][y] = result.get(z);
						z++;
					}
				}
			}
			nullPos = new Dimension(outputBoardSize.x-1, outputBoardSize.y-1);
			gen = new Random();
		}
		

		private void shuffle(){						

			int n = 50;

			if(outputBoardSize.equals(6, 10)) n = 100;
			

			Dimension previous = new Dimension(); 
			Dimension current = null;
			for(int i = 0; i<n; i++){
				do{
					current = pickRandomTileToMove();
				}while(current.equals(previous));
				previous = new Dimension(nullPos.x,nullPos.y);
				switchToEmpty(current);
			}
		}


		private void switchToEmpty(Dimension pos){
			array[nullPos.x][nullPos.y] = array[pos.x][pos.y];
			array[pos.x][pos.y] = null;
			nullPos = new Dimension(pos.x, pos.y);
		}
		

		private Dimension pickRandomTileToMove(){
			if(nullPos.equals(0,0)){
				switch(gen.nextInt(2)){
					case 0:
						return getSouthern(nullPos);
					case 1:
						return getEastern(nullPos);
				}
			}
			else if(nullPos.equals(outputBoardSize.x-1, 0)){
				switch(gen.nextInt(2)){
				case 0:
					return getSouthern(nullPos);
				case 1:
					return getWestern(nullPos);
				}
				
			}else if(nullPos.equals(0, outputBoardSize.y-1)){
				switch(gen.nextInt(2)){
				case 0:
					return getNorthern(nullPos);
				case 1:
					return getEastern(nullPos);
				}
			}else if(nullPos.equals(outputBoardSize.x-1, outputBoardSize.y-1)){
				switch(gen.nextInt(2)){
				case 0:
					return getNorthern(nullPos);
				case 1:
					return getWestern(nullPos);
				}
			}else{
				if(nullPos.x==0){
					switch(gen.nextInt(3)){
					case 0:
						return getNorthern(nullPos);
					case 1:
						return getSouthern(nullPos);
					case 2:
						return getEastern(nullPos);
					}
				}
				else if(nullPos.x==outputBoardSize.x-1){
					switch(gen.nextInt(3)){
					case 0:
						return getNorthern(nullPos);
					case 1:
						return getSouthern(nullPos);
					case 2:
						return getWestern(nullPos);
					}
				}else if(nullPos.y==0){
					switch(gen.nextInt(3)){
					case 0:
						return getSouthern(nullPos);
					case 1:
						return getWestern(nullPos);
					case 2:
						return getEastern(nullPos);
					}
				}else if(nullPos.y==outputBoardSize.y-1){
					switch(gen.nextInt(3)){
					case 0:
						return getNorthern(nullPos);
					case 1:
						return getWestern(nullPos);
					case 2:
						return getEastern(nullPos);
					}
				}
			}
			//And now, finally, the situation when null is fully "inside" the board.
			switch(gen.nextInt(4)){
			case 0:
				return getNorthern(nullPos);
			case 1:
				return getWestern(nullPos);
			case 2:
				return getEastern(nullPos); 
			case 3:
				return getSouthern(nullPos);
			}
			
			return null;
		}
		
		private Dimension getNorthern(Dimension pos){
			return new Dimension(pos.x,pos.y-1);
		}
		private Dimension getSouthern(Dimension pos){
			return new Dimension(pos.x,pos.y+1);
		}
		private Dimension getEastern(Dimension pos){
			return new Dimension(pos.x+1,pos.y);
		}
		private Dimension getWestern(Dimension pos){
			return new Dimension(pos.x-1,pos.y);
		}
		
	}
	
	
	
	public PuzzleCreator(Bitmap source, GameBoard board){
		sourceImage = source;
		outputBoardSize = board.getGameSize();
		outputTileSize = board.getTileSize();
		this.board = board;
		
	}
	
	public PuzzleCreator(Bitmap source, Dimension boardSize, int tileSize){
		sourceImage = source;
		outputBoardSize = boardSize;
		outputTileSize = tileSize;
	}
	
	
	public PuzzleTile[][] createPuzzle(){
		
		if(sourceImage == null || outputBoardSize == null || outputTileSize <= 0)
			throw new RuntimeException("Missing parameter to create puzzle");


		Dimension neededRes = new Dimension(outputBoardSize.x*outputTileSize,outputBoardSize.y*outputTileSize);
		sourceImage = Bitmap.createScaledBitmap(sourceImage, neededRes.x, neededRes.y, true);
		
		
		result = new ArrayList<PuzzleTile>();
		
		int n = 0;
		for(int y = 0; y<outputBoardSize.y; y++){
			for(int x = 0; x<outputBoardSize.x; x++){

				if(!(x==outputBoardSize.x-1 && y==outputBoardSize.y-1)){
					Bitmap temp = Bitmap.createBitmap(sourceImage, x*outputTileSize, y*outputTileSize, 
							outputTileSize, outputTileSize);
					result.add(new PuzzleTile(new BitmapDrawable(temp), n));
				}
				n++;
			}
		}

		Randomizer randomizer = new Randomizer(result);
		randomizer.shuffle();
		
		return randomizer.array;
	}
	
	
	

	public Bitmap getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(Bitmap sourceImage) {
		this.sourceImage = sourceImage;
	}

	public ArrayList<PuzzleTile> getResult() {
		return result;
	}

	public Dimension getOutputBoardSize() {
		return outputBoardSize;
	}

	public void setOutputBoardSize(Dimension outputBoardSize) {
		this.outputBoardSize = outputBoardSize;
	}

	public int getOutputTileSize() {
		return outputTileSize;
	}

	public void setOutputTileSize(int outputTileSize) {
		this.outputTileSize = outputTileSize;
	}
	
	

}
