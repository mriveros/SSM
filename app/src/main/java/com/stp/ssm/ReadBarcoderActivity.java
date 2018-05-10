package com.stp.ssm;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.stp.ssm.Util.CameraSource;
import com.stp.ssm.Util.CellUtils;
import java.io.IOException;
import de.greenrobot.event.EventBus;

import static android.content.Intent.ACTION_DEVICE_STORAGE_LOW;
import static android.content.Intent.CATEGORY_DEFAULT;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.util.Log.e;
import static android.util.Log.i;
import static android.view.ScaleGestureDetector.OnScaleGestureListener;
import static android.view.SurfaceHolder.Callback;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static com.google.android.gms.common.GoogleApiAvailability.getInstance;
import static com.google.android.gms.vision.barcode.Barcode.ALL_FORMATS;
import static com.google.android.gms.vision.barcode.BarcodeDetector.Builder;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.surfaceView;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_read_barcoder;
import static com.stp.ssm.Util.CameraSource.CAMERA_FACING_BACK;
import static com.stp.ssm.Util.CellUtils.playAlert;
import static de.greenrobot.event.EventBus.getDefault;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class ReadBarcoderActivity extends BaseActivity {

    private static final int RC_HANDLE_GMS = 9001;
    private SurfaceView cameraView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private ScaleGestureDetector scaleGestureDetector;
    private LinearLayout layout_barcoder;
    private EventBus eventBus;

    public static final String ACTION_READ_CODE = "com.stp.ssm.READCODES";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_read_barcoder);

        getSupportActionBar().hide();
        eventBus = getDefault();
        layout_barcoder = (LinearLayout) findViewById(id.layout_barcoder);
        cameraView = (SurfaceView) findViewById(surfaceView);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        inicializarBarcodeDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarCamara();
    }


    private void inicializarBarcodeDetector() {
        barcodeDetector = new Builder(this).setBarcodeFormats(ALL_FORMATS).build();
        if (!barcodeDetector.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
            if (hasLowStorage) {
                makeText(this, "Dependencias no instaladas", LENGTH_LONG).show();
            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        cameraSource = builder.build();
    }

    private void inicializarCamara() {
        int code = getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != SUCCESS) {
            Dialog dlg = getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            cameraView.getHolder().addCallback(new Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        cameraSource.start(cameraView.getHolder());

                        Size size = cameraSource.getPreviewSize();
                        int min = min(size.getWidth(), size.getHeight());
                        int max = max(size.getWidth(), size.getHeight());

                    } catch (IOException ie) {
                        e("CAMERA SOURCE", ie.getMessage());
                    } catch (SecurityException ex) {
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });


            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {
                        i("Barcode", barcodes.valueAt(0).displayValue);

                        playAlert(getApplicationContext());
                        Intent intent = getIntent();
                        intent.putExtra("barcoder", barcodes.valueAt(0).displayValue);
                        setResult(RESULT_OK, intent);

                        Bundle bundle = new Bundle();
                        bundle.putString("readcode", barcodes.valueAt(0).displayValue);
                        enviarBroadcast(bundle);
                        //eventBus.post(new ReadCodigoEvt(barcodes.valueAt(0).displayValue));

                        finish();
                    }
                }
            });
        }
    }

    private void enviarBroadcast(Bundle bundle) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_READ_CODE);
        intentResponse.addCategory(CATEGORY_DEFAULT);
        intentResponse.putExtras(bundle);
        sendBroadcast(intentResponse);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private class ScaleListener implements OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            cameraSource.doZoom(detector.getScaleFactor());
        }
    }
}