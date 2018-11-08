package example.kenny.modernartui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private String URL = "https://www.coursera.org/learn/android-programming";

    final int[][] mColorMap1 = new int[][]{
            { 50, 224, 64 },
            { 33, 255, 225 },
            { 32, 0, 255 },
            { 209, 20, 100 },
            { 255, 255, 255 }
    };

    final int[][] mColorMap2 = new int[][]{
            { 50, 224, 64 },
            { 66, 211, 225 },
            { 32, 32, 216 },
            { 209, 0, 209 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap3 = new int[][]{
            { 200, 0, 64 },
            { 166, 211, 0 },
            { 132, 32, 216 },
            { 209, 0, 0 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap4 = new int[][]{
            { 30, 224, 64 },
            { 0, 20, 122 },
            { 132, 132, 0 },
            { 209, 0, 209 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap5 = new int[][]{
            { 50, 255, 64 },
            { 255, 123,112 },
            { 132, 312, 216 },
            { 99, 77, 88 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap6 = new int[][]{
            { 55, 88, 99 },
            { 44, 211, 225 },
            { 32, 132, 44 },
            { 77, 255, 77 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap7 = new int[][]{
            { 22, 224, 22 },
            { 11, 211, 11 },
            { 32, 32, 140 },
            { 0, 0, 255 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap8 = new int[][]{
            { 150, 224, 64 },
            { 79, 180, 60 },
            { 32, 132, 161 },
            { 17, 17, 255 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap9 = new int[][]{
            { 67, 89, 92 },
            { 66, 122, 155 },
            { 32, 132, 143 },
            { 209, 177, 166 },
            { 255, 255, 255 }
    };
    final int[][] mColorMap10 = new int[][]{
            { 188, 199, 200 },
            { 6, 7, 255 },
            { 32, 111, 32 },
            { 125, 77, 125 },
            { 255, 255, 255 }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        final ImageView[] mBlock = { (ImageView) findViewById(R.id.block1),
                (ImageView) findViewById(R.id.block2),
                (ImageView) findViewById(R.id.block3),
                (ImageView) findViewById(R.id.block4),
                (ImageView) findViewById(R.id.block5) };

        final SeekBar sk= (SeekBar) findViewById(R.id.seekbar);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                Log.i("Kenny", " progress = " + progress);
                for(int i=0; i< mBlock.length; i++) {
                    changeViewColor(mBlock[i], i, progress);
                }
            }
        });

        for(int i=0; i< mBlock.length; i++) {
            mBlock[i].setOnTouchListener(new View.OnTouchListener() {
                float dX;
                float dY;
                int lastAction;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            lastAction = MotionEvent.ACTION_DOWN;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            view.setY(event.getRawY() + dY);
                            view.setX(event.getRawX() + dX);
                            lastAction = MotionEvent.ACTION_MOVE;
                            break;

                        case MotionEvent.ACTION_UP:
                            if (lastAction == MotionEvent.ACTION_DOWN) {
                                createChangeDialog(view);
                            }
                            break;

                        default:
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("Kenny", "QQQQ");
            final View info_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.information_dialog, null);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_info_title)
                    .setView(info_dialog)
                    .setPositiveButton("Visit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent webIntent = new Intent();
                            webIntent.setAction(Intent.ACTION_VIEW);
                            webIntent.setData(Uri.parse(URL));
                            startActivity(webIntent);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeViewColor(View view, int index, int progress) {

        if(progress < 10) {
            view.setBackgroundColor(Color.rgb(mColorMap1[index][0], mColorMap1[index][1], mColorMap1[index][2]));
        } else if(progress < 20) {
            view.setBackgroundColor(Color.rgb(mColorMap2[index][0], mColorMap2[index][1], mColorMap2[index][2]));
        } else if(progress < 30) {
            view.setBackgroundColor(Color.rgb(mColorMap3[index][0], mColorMap3[index][1], mColorMap3[index][2]));
        } else if(progress < 40) {
            view.setBackgroundColor(Color.rgb(mColorMap4[index][0], mColorMap4[index][1], mColorMap4[index][2]));
        } else if(progress < 50) {
            view.setBackgroundColor(Color.rgb(mColorMap5[index][0], mColorMap5[index][1], mColorMap5[index][2]));
        } else if(progress < 60) {
            view.setBackgroundColor(Color.rgb(mColorMap6[index][0], mColorMap6[index][1], mColorMap6[index][2]));
        } else if(progress < 70) {
            view.setBackgroundColor(Color.rgb(mColorMap7[index][0], mColorMap7[index][1], mColorMap7[index][2]));
        } else if(progress < 80) {
            view.setBackgroundColor(Color.rgb(mColorMap8[index][0], mColorMap8[index][1], mColorMap8[index][2]));
        } else if(progress < 90) {
            view.setBackgroundColor(Color.rgb(mColorMap9[index][0], mColorMap9[index][1], mColorMap9[index][2]));
        } else {
            view.setBackgroundColor(Color.rgb(mColorMap10[index][0], mColorMap10[index][1], mColorMap10[index][2]));
        }
    }

    public int editTextTransToInt(String value, int original_num) {
        int num = 0;
        if(value.length() != 0) {
            return Integer.parseInt(value);
        } else {
            return original_num;
        }
    }

    public void createChangeDialog(final View view) {
        Drawable background = view.getBackground();
        final int color = ((ColorDrawable) background).getColor();
        final int red = Color.red(color);
        final int green = Color.green(color);
        final int blue = Color.blue(color);
        //final int height = view.getHeight();
        //final int width = view.getWidth();

        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog, null);

        final EditText edittext_red = (EditText) item.findViewById(R.id.input_red);
        final EditText edittext_green = (EditText) item.findViewById(R.id.input_green);
        final EditText edittext_blue = (EditText) item.findViewById(R.id.input_blue);
        //final EditText edittext_height = (EditText) item.findViewById(R.id.input_height);
        //final EditText edittext_width = (EditText) item.findViewById(R.id.input_width);
        final CheckBox bringToFront = (CheckBox) item.findViewById(R.id.checkbox_tofront);

        edittext_red.setHint(Integer.toString(red));
        edittext_green.setHint(Integer.toString(green));
        edittext_blue.setHint(Integer.toString(blue));
        //edittext_height.setHint(Integer.toString(height));
        //edittext_width.setHint(Integer.toString(width));

        Log.i("Kenny", "current red =" + red + " " + "green = " + green + " blue = " + blue);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.dialog_title)
                .setView(item)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Kenny", "get input red =" + edittext_red.getText() + " " + "green = " + edittext_green.getText() + " blue = " + edittext_blue.getText());
                        int new_red = editTextTransToInt(edittext_red.getText().toString(), red);
                        int new_green = editTextTransToInt(edittext_green.getText().toString(), green);
                        int new_blue = editTextTransToInt(edittext_blue.getText().toString(), blue);

                        /*
                        int new_height = editTextTransToInt(edittext_height.getText().toString(), height);
                        int new_width = editTextTransToInt(edittext_width.getText().toString(), width);
                        */
                        Log.i("Kenny", "modify to red =" + new_red + " " + "green = " + new_green + " blue = " + new_blue);
                        view.setBackgroundColor(Color.rgb(new_red, new_green, new_blue));
                        //view.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

                        /*
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.width = new_width;
                        layoutParams.height = new_height;
                        view.setLayoutParams(layoutParams);
                        Log.i("Kenny", "modify to width =" + new_width + " " + "height = " + new_height);
                        */
                        if(bringToFront.isChecked()) {
                            view.bringToFront();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
