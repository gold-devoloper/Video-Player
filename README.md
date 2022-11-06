# Video-Player

Step 1. Add the JitPack repository to your build file

gradle

Add it in your settings.gradle at the end of repositories:


			maven { url 'https://jitpack.io' }
      
Step 2. Add the dependency

	
	        implementation 'com.github.gold-devoloper:Video-Player:1.0.0'
	
Xml code
       <gold.android.iplayer.widget.VideoPlayer
         android:id="@+id/video_player"
         android:layout_width="match_parent"
         android:layout_height="200dp"/>
         
Player ready and start playing

             mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
             mVideoPlayer.getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;//Fix the player height, or set the height to: match_parent
             //Use the SDK's own controller + each UI interaction component
             VideoController controller = new VideoController(mVideoPlayer.getContext());//create a default controller
             mVideoPlayer.setController(controller);//Bind the player to the controller
             WidgetFactory.bindDefaultControls(controller);//One-click use the default UI interaction component to bind to the controller (need to be integrated implementation 'com.github.gold-devoloper:Video-Player:1.0.0')
            //Set the video title (only visible in landscape state)
             controller.setTitle("Title here");
             //set playback source
             mVideoPlayer.setDataSource("https://upload.dongfeng-nissan.com.cn/nissan/video/202204/4cfde6f0-bf80-11ec-95c3-214c38efbbc8.mp4");
             //Asynchronous start ready to play
             mVideoPlayer.prepareAsync();

Life cycle processing

             @Override
             protected void onResume() {
               super.onResume();
               mVideoPlayer.onResume();
             }

             @Override
             protected void onPause() {
               super.onPause();
               mVideoPlayer.onPause();
             }

             @Override
             public void onBackPressed() {
               if(mVideoPlayer.isBackPressed()){
               super.onBackPressed();
                }
              }

             @Override
             protected void onDestroy() {
               super.onDestroy();
               mVideoPlayer.onDestroy();
             }
Done

Other codes read sample app








