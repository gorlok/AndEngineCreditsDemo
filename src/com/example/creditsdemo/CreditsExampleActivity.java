package com.example.creditsdemo;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Scrolling text for a credits scene.
 * Updated for AndEngine GLES2AC "Anchor Center"
 * 
 * @see <a href="http://www.andengine.org/forums/tutorials/credits-scene-scrolling-text-t6610.html">original source</a>
 * 
 * @author stovenator (original work)
 * @author gorlok (GLES2AC adaptation)
 */
public class CreditsExampleActivity extends SimpleBaseGameActivity {

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	
	private Camera mCamera;
	protected Scene mScene;
	private static Sprite backButton;
	private static String creditsString;
	public static BitmapTextureAtlas mLargeFontTexture;
	public static BitmapTextureAtlas mSmallFontTexture;
	public static BitmapTextureAtlas mMenuTexture;
	public static TextureRegion mMenuCreditsTextureRegion;
	public static TextureRegion mMenuBackTextureRegion;
	public static Font mLargeFont;
	public static Font mSmallFont;
	private static Text creditsText;
	private static Text titleText;

	private static ScrollDetector mScrollDetector;
	private static boolean manualScrolling;
	private static boolean creditsFinished;
	private static boolean backButtonDisplayed;

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() throws IOException {
		Engine engine = this.getEngine();
		Context context = this.getBaseContext();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		mLargeFontTexture = new BitmapTextureAtlas(getTextureManager(), 512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLargeFont = FontFactory.createStroke(mEngine.getFontManager(), mLargeFontTexture, 
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, 
				Color.WHITE_ABGR_PACKED_INT, 3, Color.BLACK_ABGR_PACKED_INT);

		mSmallFontTexture = new BitmapTextureAtlas(getTextureManager(), 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mSmallFont = new Font(getFontManager(), mSmallFontTexture, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
				32, true, Color.WHITE);

		mMenuTexture = new BitmapTextureAtlas(getTextureManager(), 256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMenuBackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuTexture, context,
				"menu_back.png", 0, 250);

		engine.getTextureManager().loadTexture(mLargeFontTexture);
		engine.getTextureManager().loadTexture(mSmallFontTexture);
		engine.getTextureManager().loadTexture(mMenuTexture);
		engine.getFontManager().loadFonts(mLargeFont, mSmallFont);
	}

	@Override
	protected Scene onCreateScene() {
		final CreditsExampleActivity core = this;
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		backButtonDisplayed = false;
		manualScrolling = false;
		creditsFinished = false;

		titleText = new Text(0, 0, mLargeFont, "Credits", new TextOptions(HorizontalAlign.CENTER),
				getVertexBufferObjectManager());
		titleText.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT - titleText.getHeight());

		backButton = new Sprite(CAMERA_WIDTH / 2, 50, mMenuBackTextureRegion, getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
					final float pTouchAreaLocalY) {
				core.finish();
				return true;
			}
		};
		creditsString = "Developed By:\nDeveloper Name\n\nGraphics By:\nGraphics Creator\n\nResearch Done By:\nCrack Research Team\n\nEngine:\nAndEngine\n\nLegal Work:\nLegal Team\n\nProduced By:\nExample Studios\n\n"
				+ "Inspired By:\nA True story\n\nCredits Scene:\nDeveloped By stovenator\n\nLorem Ipsum:\nDolor Sit amet\n\nconsectetur adipiscing:\nelit.\n\nSed a ipsum:\ngravida sem rhoncus\nfringilla:\n"
				+ "duis nec orci\n\n quis nisi aliquet:\nconsequat.\n\n";

		creditsText = new Text(0, 0, mSmallFont, creditsString, new TextOptions(HorizontalAlign.CENTER),
				getVertexBufferObjectManager());
		creditsText.setAnchorCenter(0.5f, 1f); // center on x-axis, bottom for y-axis
		creditsText.setPosition(CAMERA_WIDTH / 2, 0);

		this.mScene.attachChild(creditsText);
		this.mScene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (manualScrolling == false) {
					if (creditsText.getY() > CAMERA_HEIGHT + creditsText.getHeight()) {
						creditsFinished = true;
					}
					if (creditsText.getY() > CAMERA_HEIGHT + creditsText.getHeight()) {
						creditsText.setPosition(creditsText.getX(), 0);
					} else {
						creditsText.setPosition(creditsText.getX(),
								creditsText.getY() + (float) getDropDistance(4, pSecondsElapsed));
					}
					if (creditsFinished == true && backButtonDisplayed == false) {
						backButtonDisplayed = true;
						mScene.attachChild(backButton);
						mScene.registerTouchArea(backButton);
					}
				}
			}

			@Override
			public void reset() {
			}
		});

		this.mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.isActionUp()) {
					manualScrolling = false;
					return true;
				} else {
					manualScrolling = true;
					mScrollDetector.onTouchEvent(pSceneTouchEvent);
					return true;
				}
			}

		});

		this.mScrollDetector = new SurfaceScrollDetector(new IScrollDetectorListener() {

			@Override
			public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX,
					float pDistanceY) {
			}

			@Override
			public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
				if (creditsText.getY() > CAMERA_HEIGHT + creditsText.getHeight()) {
					creditsFinished = true;
				}
				if (creditsText.getY() > CAMERA_HEIGHT + creditsText.getHeight()) {
					creditsText.setPosition(creditsText.getX(), 0);
				} else {
					creditsText.setPosition(creditsText.getX(), creditsText.getY() - pDistanceY);
				}
			}

			@Override
			public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX,
					float pDistanceY) {
			}

		});
		this.mScrollDetector.setEnabled(true);

		this.mScene.attachChild(titleText);

		this.mScene.setOnSceneTouchListener(this.mScene.getOnSceneTouchListener());
		this.mScene.setOnSceneTouchListenerBindingOnActionDownEnabled(true);

		return this.mScene;
	}

	public static double getDropDistance(double dropRate, float mSecondsElapsed) {
		// dropRate is how fast in seconds it should travel the screen
		return (CAMERA_HEIGHT * mSecondsElapsed) / dropRate;
	}

}
