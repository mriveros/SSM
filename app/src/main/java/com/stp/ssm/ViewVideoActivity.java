package com.stp.ssm;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

import com.stp.ssm.Model.Secciones;

import java.io.File;
import java.io.IOException;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.MediaPlayer.OnBufferingUpdateListener;
import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnPreparedListener;
import static android.media.MediaPlayer.OnVideoSizeChangedListener;
import static android.os.Environment.getExternalStorageDirectory;
import static android.util.Log.d;
import static android.util.Log.e;
import static android.util.Log.v;
import static android.view.SurfaceHolder.Callback;
import static android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.MediaController.MediaPlayerControl;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_media_player;

public class ViewVideoActivity extends BaseActivity implements OnBufferingUpdateListener,
        OnCompletionListener,
        OnPreparedListener,
        OnVideoSizeChangedListener,
        MediaPlayerControl,
        Callback {
    private static final String TAG = "ViewVideoActivity";
    private MediaPlayer mediaPlayer;
    private int mVideoWidth;
    private int mVideoHeight;
    private SurfaceHolder holder;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private MediaController mcontroller;
    private Handler handler = new Handler();
    private SurfaceView view_video;
    private Button btn_continuar;
    private Secciones seccion;
    private boolean hasActiveHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_media_player);

        seccion = (Secciones) getIntent().getExtras().getSerializable("seccion");
        view_video = (SurfaceView) findViewById(id.view_video);
        btn_continuar = (Button) findViewById(id.btn_continuar);

        asignarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        holder = view_video.getHolder();
        holder.addCallback(this);
        holder.setType(SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void asignarEventos() {
        btn_continuar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_continuar.setVisibility(GONE);
                Intent intent = new Intent(getApplicationContext(), FormularioSimpleActivity.class);
                intent.putExtra("seccion", seccion);
                intent.putExtra("idvisita", getIntent().getExtras().getLong("idvisita"));
                intent.putExtra("beneficiario", getIntent().getExtras().getString("beneficiario"));
                startActivityForResult(intent, 100);
            }
        });
    }

    private void playVideo() {
        doCleanUp();
        try {
            mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/Download/ES_707_01_01_00.mp4");

            mediaPlayer.setDataSource(getExternalStorageDirectory() + "/DCIM/videos/" + getIntent().getExtras().getString("video"));
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepare();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setAudioStreamType(STREAM_MUSIC);
            mcontroller = new MediaController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mcontroller.show();
        return false;
    }


    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        d(TAG, "onBufferingUpdate percent:" + percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        d(TAG, "onCompletion called");
        //Toast.makeText(getApplicationContext(),"Finalizo Video",Toast.LENGTH_LONG).show();
        btn_continuar.setVisibility(VISIBLE);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(view_video);
        handler.post(new Runnable() {
            public void run() {
                mcontroller.setEnabled(true);
                mcontroller.show();
            }
        });
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            e(TAG, "invalid video width(" + width + ") or height(" + height
                    + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        d(TAG, "surfaceCreated called");
        playVideo();
        synchronized (this) {
            hasActiveHolder = true;
            this.notifyAll();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        d(TAG, "surfaceChanged called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        d(TAG, "surfaceDestroyed called");
        synchronized (this) {
            hasActiveHolder = false;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    private void startVideoPlayback() {
        v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        onResume();
                    }
                }, 100);
            }
        }
    }
}