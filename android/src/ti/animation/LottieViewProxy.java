/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import com.airbnb.lottie.*;
import com.airbnb.lottie.LottieAnimationView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;
import org.json.JSONObject;
import android.os.Message;


@Kroll.proxy(creatableInModule=TiAnimationModule.class)
public class LottieViewProxy extends TiViewProxy
{
	// Standard Debugging variables
	private static final String LCAT = "LottieViewProxy";
	private static final boolean DBG = TiConfig.LOGD;
	private LottieAnimationView lottieView;
	private TiApplication appContext;
	KrollFunction callbackUpdate = null;
	Resources resources;
	String loadFile = "";
	String assetFolder = "";
	boolean isReady = false;
	boolean isAutoStart = false;
	boolean isLoop = false;
	long duration = 0;
	long initialDuration = 0;
	float speed = 1.0f;
	
	protected static final int MSG_STARTANIMATION = KrollProxy.MSG_LAST_ID + 101;
	
	@Kroll.constant public static final int ANIMATION_START = 1;
	@Kroll.constant public static final int ANIMATION_END = 2;
	@Kroll.constant public static final int ANIMATION_CANCEL = 3;
	@Kroll.constant public static final int ANIMATION_REPEAT = 4;
	@Kroll.constant public static final int ANIMATION_RUNNING = 5;
	
	protected class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
	{
		public void onAnimationUpdate(ValueAnimator animation)
		{
			animationEvent(animation.getAnimatedFraction(), ANIMATION_RUNNING);
		}
	}
	
	protected class AnimatorListener implements Animator.AnimatorListener
	{
		 public void onAnimationStart(Animator animation) {
			 animationEvent(getProgress(), ANIMATION_START);
		 }

		 public void onAnimationEnd(Animator animation) {
			 animationEvent(getProgress(), ANIMATION_END);
		 }

		 public void onAnimationCancel(Animator animation) {
			 animationEvent(getProgress(), ANIMATION_CANCEL);
		 }

		 public void onAnimationRepeat(Animator animation) {
			 animationEvent(getProgress(), ANIMATION_REPEAT);
		 }
	}
	
	private void animationEvent(float percentage, int status){
		if (callbackUpdate != null) {
			HashMap<String,Object> event = new HashMap<String, Object>();
			event.put("percentage", percentage);
			event.put("status", status);
			callbackUpdate.call(getKrollObject(), event);
		}
	}
	
	private class LottieView extends TiUIView
	{
		public LottieView(TiViewProxy proxy) {
			super(proxy);
			
			String packageName = proxy.getActivity().getPackageName();
			resources = proxy.getActivity().getResources();
			View videoWrapper;
			
			int resId_videoHolder = -1;
			int resId_video       = -1;
			int resId_lotti       = -1;

			resId_videoHolder = resources.getIdentifier("layout_lottie", "layout", packageName);
			resId_lotti       = resources.getIdentifier("animation_view", "id", packageName);
			
			LayoutInflater inflater     = LayoutInflater.from(getActivity());
			videoWrapper = inflater.inflate(resId_videoHolder, null);
			
			lottieView = (LottieAnimationView)videoWrapper.findViewById(resId_lotti);
			setNativeView(videoWrapper);
			appContext = TiApplication.getInstance();
			isReady = true;
			
			lottieView.addAnimatorUpdateListener(new AnimatorUpdateListener());
			lottieView.addAnimatorListener(new AnimatorListener());
			
			if (loadFile != ""){
				setFile(loadFile);
			}
		}

		@Override
		public void processProperties(KrollDict d)
		{
			super.processProperties(d);
		}
	}


	// Constructor
	public LottieViewProxy()
	{
		super();
	}
	
	private String getPathToApplicationAsset(String assetName)
	{
		// The url for an application asset can be created by resolving the specified
		// path with the proxy context. This locates a resource relative to the 
		// application resources folder
		
		String result = resolveUrl(null, assetName);
		return result;
	}

	@Override
	public TiUIView createView(Activity activity)
	{
		TiUIView view = new LottieView(this);
		view.getLayoutParams().autoFillsHeight = true;
		view.getLayoutParams().autoFillsWidth = true;
		return view;
	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict options)
	{
		super.handleCreationDict(options);
		
		if (options.containsKey("file")) {
			if (isReady){
				setFile(options.getString("file"));
			} else {
				loadFile = options.getString("file");
			}
		}
		if (options.containsKey("assetFolder")) {
			assetFolder = options.getString("assetFolder");
		}
		if (options.containsKey("loop")) {
			isLoop = options.getBoolean("loop");
		}
		if (options.containsKey("autoStart")) {
			isAutoStart = options.getBoolean("autoStart");
		}
		if (options.containsKey("update")) {
			callbackUpdate =(KrollFunction) options.get("update");
		}
	}

	public void startAnimation(){
		boolean restart = lottieView.isAnimating();
		lottieView.cancelAnimation();
		lottieView.setProgress(0f);

		
		if (duration == 0 || duration == initialDuration) {
			lottieView.playAnimation();
		} else {
			
			ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
			va.setDuration(duration);
			
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					lottieView.setProgress( (Float)animation.getAnimatedValue() );
					
					if (callbackUpdate != null) {
						HashMap<String,Object> event = new HashMap<String, Object>();
						event.put("percentage",animation.getAnimatedFraction());
						callbackUpdate.call(getKrollObject(), event);
					}
				}
			});
			va.start();
		}
	}

	@Kroll.method
	public void start() {
		if (TiApplication.isUIThread()) {
			startAnimation();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_STARTANIMATION));
		}
	}

	@Kroll.method
    public void pause() {
		lottieView.cancelAnimation();
    }
	
	@Kroll.method
    public void stop() {
		lottieView.cancelAnimation();
    }
	
	
	@Kroll.method
    public void addViewToLayer() {
		// TODO empty for now
    }
	
	@Kroll.method
    public boolean isPlaying() {
		return lottieView.isAnimating();
    }
	
	@Kroll.setProperty @Kroll.method
    public void setProgress(float val) {
        lottieView.setProgress(val);
    }
    
    @Kroll.getProperty @Kroll.method
    public float getProgress() {
        return lottieView.getProgress();
    }

	@Kroll.setProperty @Kroll.method
    public void setLoop(boolean val) {
		isLoop = val;
		lottieView.loop(isLoop);
	}

	@Kroll.getProperty @Kroll.method
	public boolean getLoop() {
		return isLoop;
	}
	
	@Kroll.setProperty @Kroll.method
	public void setSpeed(float val) {
		speed = val;
		duration = (long)(initialDuration / speed);
	}

	@Kroll.getProperty @Kroll.method
	public float getSpeed() {
		return speed;
	}
	
	@Kroll.setProperty @Kroll.method
    public void setDuration(long val) {
		duration = val;
	}

	@Kroll.getProperty @Kroll.method
	public long getDuration() {
		return duration;
	}
	
	@Kroll.method
	public void setFile(String f) {
		final String url = getPathToApplicationAsset(f);
		final TiBaseFile file = TiFileFactory.createTitaniumFile(new String[] { url }, false);      
		
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				LottieComposition.Factory.fromAssetFileName(appContext, url.replaceAll("file:///android_asset/", ""), new OnCompositionLoadedListener(){
					@Override
					public void onCompositionLoaded(LottieComposition composition) {
						lottieView.setComposition(composition);
						lottieView.setImageAssetsFolder(assetFolder);

						lottieView.addAnimatorUpdateListener(new AnimatorUpdateListener());
						lottieView.addAnimatorListener(new AnimatorListener());

						initialDuration = duration = lottieView.getDuration();
						if (isLoop){
							lottieView.loop(true);
						}
						if (isAutoStart){
							lottieView.playAnimation();
						}
					}
				});
			}
		});
		thread.start();  
    }
	
	@Kroll.method
    public void initialize() {
        
    }
	
	public boolean handleMessage(Message message) {
			switch (message.what) {
				case MSG_STARTANIMATION: {
					startAnimation();
					return true;
				}
			}
			
			return super.handleMessage(message);
	}
}
