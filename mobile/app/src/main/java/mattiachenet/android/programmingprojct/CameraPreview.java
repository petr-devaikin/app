package mattiachenet.android.programmingprojct;

/**
 * Created by mattiachenet on 27/10/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/** A basic Camera preview class */
public class CameraPreview extends Activity implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    ImageView palette;
    ImageView image;
    LinearLayout circleList;
    Integer selectPalette;

    Boolean setCircleVisible = false;


    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Point> pointsList = new ArrayList<Point>();
    Canvas c;


    private static final String TAG = "CameraPreview";
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String Server = "http://192.168.2.9:5000";

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceHolder.addCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            File f = new File(getApplicationContext().getCacheDir(), "background.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(byteArray);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            Ion.with(getApplicationContext())
                    .load(Server + "/background")
                    .setLogging("MyLogs", Log.DEBUG)
                    .setMultipartFile("img", f)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            Toast.makeText(getApplicationContext(), "Image send to projector!",
                                    Toast.LENGTH_LONG).show();

                        }
                    });

            mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            mSurfaceHolder.addCallback(this);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_surface);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        palette = (ImageView) findViewById(R.id.imagePalette);
        image = (ImageView) findViewById(R.id.imagePicture);
        circleList = (LinearLayout) findViewById(R.id.cirlclesList);

        circleList.setVisibility(View.VISIBLE);
        circleList.setAlpha(0.0f);

        circleList.animate()
                .alpha(1.0f);

        circleList.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        circleList.setVisibility(View.GONE);
                        palette.animate().translationY(0);

                    }
                });
        setCircleVisible = true;

        final int[] rainbow = getResources().getIntArray(R.array.rainbow);

        Integer countChild = circleList.getChildCount();
        Log.d("Circlelist element", countChild.toString());

        for(int i = 0; i<circleList.getChildCount(); i++){
            View circle = circleList.getChildAt(i);

            circle.setId(i);


            if(i == 0){
                String hexColor = String.format("#%06X", (0xFFFFFF & rainbow[i]));
                ((ProjApp) getApplication()).setSelectedColor(hexColor);
                selectPalette=i;

            }else{
                circle.animate().translationY(40);
            }
            Drawable background = circle.getBackground();

            if (background instanceof ShapeDrawable) {
                // cast to 'ShapeDrawable'
                ShapeDrawable shapeDrawable = (ShapeDrawable)background;
                shapeDrawable.getPaint().setColor(rainbow[i]);
            } else if (background instanceof GradientDrawable) {
                // cast to 'GradientDrawable'
                GradientDrawable gradientDrawable = (GradientDrawable)background;
                gradientDrawable.setColor(rainbow[i]);
            }


            circle.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(final View view) {
                  Integer colorSelcet = rainbow[view.getId()];

                  View exView = circleList.getChildAt(selectPalette);

                  exView.animate().translationY(40);
                  String hexColor = String.format("#%06X", (0xFFFFFF & colorSelcet));

                  view.animate().translationY(0);
                  selectPalette = view.getId();

                  ((ProjApp) getApplication()).setSelectedColor(hexColor);
              }
          });
        }



        palette.animate().translationY(40);
        palette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(setCircleVisible){


                    circleList.setVisibility(View.VISIBLE);
                    palette.animate().translationY(40);
                    circleList.animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);



                                }
                            });
                    setCircleVisible = false;
                    Log.i(TAG+"true","entrato");

                }else {
                    circleList.animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    circleList.setVisibility(View.GONE);
                                    palette.animate().translationY(0);

                                }
                            });
                    setCircleVisible = true;
                    Log.i(TAG+"false","entrato");
                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        mSurfaceHolder.addCallback(this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //tryDrawing();
        /*if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mSurfaceHolder.getSurface().isValid()) {

                // Add current touch position to the list of points
                pointsList.add(new Point((int)event.getX(), (int)event.getY()));

                // Get canvas from surface
                Canvas canvas = mSurfaceHolder.lockCanvas();

                // Clear screen
                canvas.drawColor(Color.BLACK);

                // Iterate on the list
                for(int i=0; i<pointsList.size(); i++) {
                    Point current = pointsList.get(i);

                    // Draw points
                    canvas.drawPoint(current.x, current.y, paint);

                }

                // Release canvas
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }*/

         String colorSel =((ProjApp) getApplication()).getSelectedColor();


        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            Float x = event.getX();
            Float y = event.getY();

            JsonObject jsonGesture = new JsonObject();
            jsonGesture.addProperty("type", "move");
            jsonGesture.addProperty("color", colorSel);

            JsonObject json = new JsonObject();
            json.addProperty("x", x.toString());
            json.addProperty("y", y.toString());
            json.addProperty("action", jsonGesture.toString());

            Ion.with(getApplicationContext())
                    .load(Server+"/gesture")
                    .setLogging("MyLogs", Log.DEBUG)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                        }
                    });

        } else if(event.getAction() == MotionEvent.ACTION_DOWN){

            Float x = event.getX();
            Float y = event.getY();

            JsonObject jsonGesture = new JsonObject();
            jsonGesture.addProperty("type", "first");
            jsonGesture.addProperty("color", colorSel);

            JsonObject json = new JsonObject();
            json.addProperty("x", x.toString());
            json.addProperty("y", y.toString());
            json.addProperty("action", jsonGesture.toString());

            Ion.with(getApplicationContext())
                    .load(Server+"/gesture")
                    .setLogging("MyLogs", Log.DEBUG)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                        }
                    });

        }else if(event.getAction() == MotionEvent.ACTION_UP){

            Float x = event.getX();
            Float y = event.getY();

            JsonObject jsonGesture = new JsonObject();
            jsonGesture.addProperty("type", "last");
            jsonGesture.addProperty("color", colorSel);

            JsonObject json = new JsonObject();
            json.addProperty("x", x.toString());
            json.addProperty("y", y.toString());
            json.addProperty("action", jsonGesture.toString());

            Ion.with(getApplicationContext())
                    .load(Server+"/gesture")
                    .setLogging("MyLogs",Log.DEBUG)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                        }
                    });
        }

        return false;
    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            mCamera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = mCamera.getParameters();

        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            param.setPictureFormat(PixelFormat.JPEG);
            param.set("orientation", "landscape");
            mCamera.setParameters(param);

            //tryDrawing(holder);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }


    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mSurfaceView.getHolder().removeCallback(this);
        holder.removeCallback(this);
        mSurfaceHolder.removeCallback(this);
        mCamera.release();
        mCamera = null;

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(holder);
    }



    private void drawMyStuff(final Canvas canvas) {
        //Random random = new Random();
        Log.i(TAG, "Drawing...");
        canvas.drawRGB(255, 128, 128);
    }

    private void tryDrawing() {
        Log.i(TAG, "Trying to draw...");

       if( mSurfaceHolder.getSurface().isValid()) {

           Canvas canvas = mSurfaceHolder.lockCanvas();
           if (canvas == null) {
               Log.e(TAG, "Cannot draw onto the canvas as it's null");
           } else {
               drawMyStuff(canvas);
               mSurfaceHolder.unlockCanvasAndPost(canvas);
           }
       }
    }

    public void refreshCamera(SurfaceHolder holder) {
        if (mSurfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {


            Camera.Parameters param = mCamera.getParameters();
            param.getSupportedPreviewSizes();
            mCamera.setParameters(param);
            //tryDrawing(holder);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {


                    Camera.Size previewSize = camera.getParameters().getPreviewSize();
                    YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21,previewSize.width,previewSize.height, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0,0,previewSize.width,previewSize.height),80,baos);


                    byte[] jdata = baos.toByteArray();
                    File f = new File(getApplicationContext().getCacheDir(), "temp.jpg");
                    try {
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(jdata);
                        fos.flush();
                        fos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Ion.with(getApplicationContext())
                            .load(Server+"/image")
                                    //.setLogging("MyLogs", Log.DEBUG)
                            .setMultipartFile("img",f)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                }
                            });
                }
            });

        } catch (Exception e) {

        }
    }


}
