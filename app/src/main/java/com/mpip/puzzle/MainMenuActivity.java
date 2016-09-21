package com.mpip.puzzle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainMenuActivity extends Activity {
	
	public final static String EXTRA_GAMESIZE = "com.mpip.puzzle.GAMESIZE";
	public final static String EXTRA_IMGURI = "com.mpip.puzzle.IMGURI";
	public final static String EXTRA_BOARD_ORIENTATION = "com.mpip.puzzle.BOARD_ORIENTATION";
	
	public final static int PHOTO_FROM_MEMORY_REQUESTED = 10;
	public final static int PHOTO_FROM_CAMERA_REQUESTED = 20;
	public static Intent svc;
	public final static String MAIN_FOLDER = "/com.mpip.squaredpuzzle/";
	
	private Uri tempPictureUri;
	public static boolean playPlease=false;
	private ViewSwitcher menuViewSwitcher;
	private boolean shouldSwitch = true;
	
	private Spinner gameSizeSpinner;
	private Button playButton;
	
	private Bitmap selectedImage;
	private Uri imageUri;

	private Button musicButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main_menu);
        menuViewSwitcher = (ViewSwitcher) findViewById(R.id.mainMenuViewSwitcher);
        gameSizeSpinner = (Spinner) findViewById(R.id.newGameMenuGameSizeSpinner);

        playButton = (Button) findViewById(R.id.playButton);
		playButton.setAlpha(.5f);
        playButton.setEnabled(false);

		musicButton = (Button) findViewById(R.id.musicButton);
		if(!SoundStatus.shouldBePlaying) {
			musicButton.setBackgroundResource(R.drawable.music1);
		}

		svc=new Intent(this, BackgroundSoundService.class);
		startService(svc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    public void newGameButtonOnClick(View view) throws IOException {
    	menuViewSwitcher.showNext();

		Typeface face=Typeface.createFromAsset(getAssets(),"fonts/ebrima.ttf");
		if(SoundStatus.shouldBePlaying) {
			startService(svc);
		}
		TextView tx = (TextView)findViewById(R.id.textView1);
		tx.setTypeface(face);
    	
    }

    public void pickImageOnClick(View view) {
		shouldSwitch = false;
    	Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(i, PHOTO_FROM_MEMORY_REQUESTED);
    }
    
    
    public void TakePhotoOnClick(View view){
    	shouldSwitch = false;
    	Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    	File photo = null;
        try
        {
            photo = this.createGameFile("puzzle", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
        	
            Log.v("DUPA", "Can't create file to take picture!");
            Log.d("DUPA", e.getMessage());
            return;
        }
        
    	tempPictureUri = Uri.fromFile(photo);

    	i.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureUri);
    	i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);

    	startActivityForResult(i, PHOTO_FROM_CAMERA_REQUESTED); 
    }
    

    private File createGameFile(String part, String ext) throws Exception
    {
        File mainDir= Environment.getExternalStorageDirectory();
        mainDir=new File(mainDir.getAbsolutePath()+MAIN_FOLDER);

        if(!mainDir.exists()) mainDir.mkdir();       
        
        return new File(mainDir.getAbsolutePath().toString(), part+ext);
    }
    
    
    public void playOnClick(View View){
    	Intent intent = new Intent(this, PuzzleActivity.class);
    	String[] gameSizes = getResources().getStringArray(R.array.gamesizes);
       	intent.putExtra(EXTRA_GAMESIZE, gameSizes[gameSizeSpinner.getSelectedItemPosition()]);
    	intent.putExtra(EXTRA_IMGURI, imageUri);
    	
    	int orientation;
    	orientation = (selectedImage.getWidth()>selectedImage.getHeight()) ? 
    			GameBoard.ORIENTATION_HORIZONTAL : GameBoard.ORIENTATION_PORTRAIT;
    	intent.putExtra(EXTRA_BOARD_ORIENTATION, orientation);
    	
    	shouldSwitch = true;
		playPlease = true;
    	startActivity(intent);
    	
    }

	public void musicOnClick(View View){
		if (!checkServiceRunning()){
			startService(svc);
			SoundStatus.shouldBePlaying = true;
			musicButton.setBackgroundResource(R.drawable.music);
		}
		else{
			stopService(svc);
			SoundStatus.shouldBePlaying = false;
			musicButton.setBackgroundResource(R.drawable.music1);
		}
	}

    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == PHOTO_FROM_MEMORY_REQUESTED && resultCode == RESULT_OK){
			updateSelectedPicture(data.getData());
			playButton.setAlpha(1.0f);
			playButton.setEnabled(true);
		}
		
		if(requestCode == PHOTO_FROM_CAMERA_REQUESTED && resultCode == RESULT_OK){
			updateSelectedPicture(tempPictureUri);
			playButton.setAlpha(1.0f);
			playButton.setEnabled(true);
		}
		
		
	}

	private void updateSelectedPicture(Uri uri){
		try{
			imageUri = uri;

			InputStream imageStream = getContentResolver().openInputStream(imageUri);
			selectedImage = BitmapFactory.decodeStream(imageStream);

			ImageView iv = (ImageView) findViewById(R.id.selectedImageView);
			iv.setImageDrawable(new BitmapDrawable(selectedImage));
		
		}catch(FileNotFoundException ex){
			Log.e("File not found", "Cannot find a file under received URI");
		}	
	}
	
	public void scaleImage(Uri imgUri, int maxSize){


	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgUri.toString(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    int scaleFactor = Math.min(photoW/maxSize, photoH/maxSize);

	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(imgUri.toString(), bmOptions);

	    try {
	    	 FileOutputStream out = new FileOutputStream(new File(imgUri.toString()));
	         bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	         out.flush();
	         out.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(SoundStatus.shouldBePlaying){
			startService(svc);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(shouldSwitch) 
			if(menuViewSwitcher.getDisplayedChild()==1) 
				menuViewSwitcher.showPrevious();
		if (SoundStatus.shouldBePlaying)
			startService(svc);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if(SoundStatus.shouldBePlaying){
			startService(svc);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopService(svc);

		if (playPlease && SoundStatus.shouldBePlaying)
		{
			startService(svc);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(svc);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopService(svc);
	}

	public boolean checkServiceRunning(){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if ("com.mpip.puzzle.BackgroundSoundService"
					.equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	public void stopMusic() {
		stopService(svc);
	}
}
