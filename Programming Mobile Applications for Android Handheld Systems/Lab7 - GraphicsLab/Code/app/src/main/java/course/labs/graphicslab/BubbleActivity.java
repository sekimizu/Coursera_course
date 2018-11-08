package course.labs.graphicslab;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class BubbleActivity extends Activity {

	// These variables are for testing purposes, do not modify
	private final static int RANDOM = 0;
	private final static int SINGLE = 1;
	private final static int STILL = 2;
	private static int speedMode = RANDOM;

	private static final String TAG = "Lab-Graphics";

	// The Main view
	private RelativeLayout mFrame;

	// Bubble image's bitmap
	private Bitmap mBitmap;

	// Display dimensions
	private int mDisplayWidth, mDisplayHeight;

	// Sound variables

	// AudioManager
	private AudioManager mAudioManager;
	// SoundPool
	private SoundPool mSoundPool;
	// ID for the bubble popping sound
	private int mSoundID;
	// Audio volume
	private float mStreamVolume;

	// Gesture Detector
	private GestureDetector mGestureDetector;

	//Display Metrics
	private DisplayMetrics mDisplayMetrics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Set up user interface
		mFrame = (RelativeLayout) findViewById(R.id.frame);

		// Load basic bubble Bitmap
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b64);

		mDisplayMetrics = new DisplayMetrics();
		BubbleActivity.this.getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		mDisplayWidth = mDisplayMetrics.widthPixels;
		mDisplayHeight = mDisplayMetrics.heightPixels;

		Log.i("Kenny", " mDisplayWidth=" + mDisplayWidth + " ,mDisplayHeight=" + mDisplayHeight);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Manage bubble popping sound
		// Use AudioManager.STREAM_MUSIC as stream type

		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mStreamVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// TODO - make a new SoundPool, allowing up to 10 streams 
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// TODO - set a SoundPool OnLoadCompletedListener that calls setupGestureDetector()
		mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
									   int status) {
				if (0 == status) {
					setupGestureDetector();
				} else {
					Log.i("Kenny", " can't load sound");
					finish();
				}
			}
		});

		// TODO - load the sound from res/raw/bubble_pop.wav
		mSoundID = mSoundPool.load(this, R.raw.bubble_pop, 1);
		mAudioManager.setSpeakerphoneOn(true);
		mAudioManager.loadSoundEffects();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			// Get the size of the display so this View knows where borders are
			//mDisplayWidth = mFrame.getWidth();
			//mDisplayHeight = mFrame.getHeight();

		}
	}

	// Set up GestureDetector
	private void setupGestureDetector() {

		mGestureDetector = new GestureDetector(this,

		new GestureDetector.SimpleOnGestureListener() {

			// If a fling gesture starts on a BubbleView then change the
			// BubbleView's velocity

			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2,
					float velocityX, float velocityY) {

				// TODO - Implement onFling actions.
				// You can get all Views in mFrame using the
				// ViewGroup.getChildCount() method
				for(int i = 0 ; i < mFrame.getChildCount(); i++){
					BubbleView view = (BubbleView) mFrame.getChildAt(i);
					if(view.intersects(event1.getX(), event1.getY())){
						Log.i("Kenny", " fling the bubble!!!");
						view.deflect(velocityX,velocityY);
					}
				}
				return false;
			}

			// If a single tap intersects a BubbleView, then pop the BubbleView
			// Otherwise, create a new BubbleView at the tap's location and add
			// it to mFrame. You can get all views from mFrame with ViewGroup.getChildAt()

			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {

				// TODO - Implement onSingleTapConfirmed actions.
				// You can get all Views in mFrame using the
				// ViewGroup.getChildCount() method
				Log.i("Kenny", " Single Tap...");
				boolean createBubble = true;
				float x = event.getX();
				float y = event.getY();
				for(int i = 0 ; i < mFrame.getChildCount(); i++){
					BubbleView view = (BubbleView) mFrame.getChildAt(i);
					if(view.intersects(x, y)){
						view.stop(true);
						createBubble = false;
					}
				}
				if(createBubble){
					final BubbleView bv = new BubbleView(getApplicationContext(), x, y);
					mFrame.addView(bv);
				}
				return false;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO - Delegate the touch to the gestureDetector
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		// TODO - Release all SoundPool resources
		if (null != mSoundPool) {
			mSoundPool.unload(mSoundID);
			mSoundPool.release();
			mSoundPool = null;
		}

		mAudioManager.setSpeakerphoneOn(false);
		mAudioManager.unloadSoundEffects();

		super.onPause();
	}

	// BubbleView is a View that displays a bubble.
	// This class handles animating, drawing, and popping amongst other actions.
	// A new BubbleView is created for each bubble on the display

	public class BubbleView extends View {

		private static final int BITMAP_SIZE = 64;
		private static final int REFRESH_RATE = 40;
		private final Paint mPainter = new Paint();
		private ScheduledFuture<?> mMoverFuture;
		private int mScaledBitmapWidth;
		private Bitmap mScaledBitmap;

		// location, speed and direction of the bubble
		private float mXPos, mYPos, mDx, mDy, mRadius, mRadiusSquared;
		private long mRotate, mDRotate;
		private BubbleView mView;

		BubbleView(Context context, float x, float y) {
			super(context);

			// Create a new random number generator to
			// randomize size, rotation, speed and direction
			Random r = new Random();

			// Creates the bubble bitmap for this BubbleView
			createScaledBitmap(r);

			// Radius of the Bitmap
			mRadius = mScaledBitmapWidth / 2;
			mRadiusSquared = mRadius * mRadius;

			// Adjust position to center the bubble under user's finger
			mXPos = x - mRadius;
			mYPos = y - mRadius;

			// Set the BubbleView's speed and direction
			setSpeedAndDirection(r);
			
			// Set the BubbleView's rotation
			setRotation(r);

			mPainter.setAntiAlias(true);
			mView = this;
			/*
			this.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					Log.i("Kenny", " touch on Bubble...kill it, view = " + view.getId());
					return false;
				}
			});
			*/
			/*
			mView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.i("Kenny", " touch on Bubble...kill it, view = " + view.getId());
				}
			});
			*/
		}

		private void setRotation(Random r) {
			if (speedMode == RANDOM) {
				// TODO - set rotation in range [1..3]
				mDRotate = r.nextInt(4);
			} else {
				mDRotate = 0;
			}
			Log.i("Kenny", " setRotation, mDRotate = " + mDRotate);
		}

		private void setSpeedAndDirection(Random r) {
			// Used by test cases
			switch (speedMode) {
			case SINGLE:
				mDx = 20;
				mDy = 20;
				break;
			case STILL:
				// No speed
				mDx = 0;
				mDy = 0;
				break;
			default:
				// TODO - Set movement direction and speed
				// Limit movement speed in the x and y
				// direction to [-3..3] pixels per movement.
				/*
				mDx = r.nextInt(7) - 3;
				mDy = r.nextInt(7) - 3;
				*/
				// (0,0) will cause freeze...???, avoid 0
				mDx = r.nextInt(6) - 3;
				mDy = r.nextInt(6) - 3;
				if(mDx >= 0) {
					mDx++;
				}
				if(mDy >= 0) {
					mDy++;
				}
			}
			Log.i("Kenny", " setSpeedAndDirection, mDx = " + mDx + " , mDy = " + mDy);
		}

		private void createScaledBitmap(Random r) {
			if (speedMode != RANDOM) {
				mScaledBitmapWidth = BITMAP_SIZE * 3;
			} else {
				//TODO - set scaled bitmap size in range [1..3] * BITMAP_SIZE
				mScaledBitmapWidth = BITMAP_SIZE * (r.nextInt(3) + 1);
			}
			Log.i("Kenny", " createScaledBitmap, mScaledBitmapWidth = " + mScaledBitmapWidth);
			// TODO - create the scaled bitmap using size set above
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, mScaledBitmapWidth, mScaledBitmapWidth, false);

			start();
		}

		// Start moving the BubbleView & updating the display
		private void start() {

			// Creates a WorkerThread
			ScheduledExecutorService executor = Executors
					.newScheduledThreadPool(1);

			// Execute the run() in Worker Thread every REFRESH_RATE
			// milliseconds
			// Save reference to this job in mMoverFuture
			mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					//Log.i("Kenny", " start run");
					// TODO - implement movement logic.
					// Each time this method is run the BubbleView should
					// move one step. If the BubbleView exits the display, 
					// stop the BubbleView's Worker Thread. 
					// Otherwise, request that the BubbleView be redrawn. 
					if(!isOutOfView()) {
						//Log.i("Kenny", " move...");
						moveWhileOnScreen();
					} else {
						stop(false);
					}
				}
			}, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
		}

		// Returns true if the BubbleView intersects position (x,y)
		private synchronized boolean intersects(float x, float y) {
			// TODO - Return true if the BubbleView intersects position (x,y)
			if ((x > mXPos && x < (mXPos+mScaledBitmapWidth)) && (y > mYPos && y < (mYPos+mScaledBitmapWidth))) {
				return true;
			}
		    return false;
		}

		// Cancel the Bubble's movement
		// Remove Bubble from mFrame
		// Play pop sound if the BubbleView was popped

		private void stop(final boolean wasPopped) {
			if(wasPopped) {
				Log.i("Kenny", " Remove bubble because user click it");
			} else {
				Log.i("Kenny", " Remove bubble because out of screen");
			}
			if (null != mMoverFuture && !mMoverFuture.isDone()) {
				mMoverFuture.cancel(true);
			}

			// This work will be performed on the UI Thread
			mFrame.post(new Runnable() {
				@Override
				public void run() {

					// TODO - Remove the BubbleView from mFrame
					mFrame.removeView(mView);
					
					// TODO - If the bubble was popped by user,
					// play the popping sound
					if (wasPopped) {
						mSoundPool.play(mSoundID, 1, 1, 1, 0, 1);
					}
				}
			});
		}

		// Change the Bubble's speed and direction
		private synchronized void deflect(float velocityX, float velocityY) {
			//TODO - set mDx and mDy to be the new velocities divided by the REFRESH_RATE
			mDx = velocityX / REFRESH_RATE;
			mDy = velocityY / REFRESH_RATE;
		}

		// Draw the Bubble at its current location
		@Override
		protected synchronized void onDraw(Canvas canvas) {

			// TODO - save the canvas
			canvas.save();

			// TODO - increase the rotation of the original image by mDRotate
			mRotate = mRotate + mDRotate;

			// TODO Rotate the canvas by current rotation
			// Hint - Rotate around the bubble's center, not its position
			canvas.rotate(mRotate, mXPos + mScaledBitmapWidth/2, mYPos + mScaledBitmapWidth/2);

			// TODO - draw the bitmap at its new location
			canvas.drawBitmap(mScaledBitmap, mXPos, mYPos,mPainter);

			// TODO - restore the canvas
			canvas.restore();
		}

		// Returns true if the BubbleView is still on the screen after the move
		// operation
		private synchronized boolean moveWhileOnScreen() {
			// TODO - Move the BubbleView
			mXPos = mDx + mXPos;
			mYPos = mDy + mYPos;
			postInvalidate();
			return isOutOfView();
		}

		// Return true if the BubbleView is off the screen after the move
		// operation
		private boolean isOutOfView() {

			// TODO - Return true if the BubbleView is off the screen after
			// the move operation
			if(mXPos + mScaledBitmapWidth/2 >= mDisplayWidth - mScaledBitmapWidth/2 || mXPos  <0
					||mYPos + mScaledBitmapWidth/2 >= mDisplayHeight - mScaledBitmapWidth/2 || mYPos  <0){
				return true;
			}

			return false;
		}
	}

	// Do not modify below here

	@Override
	public void onBackPressed() {
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_still_mode:
			speedMode = STILL;
			return true;
		case R.id.menu_single_speed:
			speedMode = SINGLE;
			return true;
		case R.id.menu_random_mode:
			speedMode = RANDOM;
			return true;
		case R.id.quit:
			exitRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void exitRequested() {
		super.onBackPressed();
	}
}