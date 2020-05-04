package com.example.monopoly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ImageView playButton,logo,animationView;
    public final static int MODE_ONE = 1;
    public final static int MODE_TWO = 2;
    TextView name;
    Dialog playersDialog;
    private int noPlayers;

    @Override
    protected void onStart() {
        super.onStart();
        //Display width and height acquiring
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SizeDisplay.setPhoneDensity(size.x,size.y);
        Log.e("Default",size.x+" : "+size.y);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        name = findViewById(R.id.monopoly);
        animationView = findViewById(R.id.animationview);
        playButton = findViewById(R.id.play);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playersDialog = new Dialog(MainActivity.this);
                playersDialog.setContentView(R.layout.gamemode);
                playersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                playersDialog.getWindow().setGravity(Gravity.CENTER);
                Button btn1 = playersDialog.findViewById(R.id.btn1);
                Button btn2 = playersDialog.findViewById(R.id.btn2);

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                        intent.putExtra("Mode",MODE_ONE);
                        playersDialog.dismiss();
                        startActivity(intent);
                        logo.setVisibility(View.GONE);
                        name.setVisibility(View.GONE);
                        playButton.setVisibility(View.GONE);
                        animationView.setVisibility(View.VISIBLE);
                        ((AnimationDrawable) animationView.getBackground()).start();
                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playersDialog.dismiss();
                        getNumPlayers();
                    }
                });

                playersDialog.show();

            }
        });
    }


    public void getNumPlayers()
    {
        playersDialog = new Dialog(MainActivity.this);
        playersDialog.setContentView(R.layout.playersdialog);
        playersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        playersDialog.getWindow().setGravity(Gravity.CENTER);
        Button two = playersDialog.findViewById(R.id.two);
        Button three = playersDialog.findViewById(R.id.three);
        Button four = playersDialog.findViewById(R.id.four);

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noPlayers = 2;
                showDialogBox(2);
                playersDialog.dismiss();
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noPlayers = 3;
                showDialogBox(3);
                playersDialog.dismiss();
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noPlayers = 4;
                showDialogBox(4);
                playersDialog.dismiss();
            }
        });

        playersDialog.show();

    }

    public void showDialogBox(final int number) {
        final HashMap<Integer,String> names = new HashMap<Integer, String>();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.playernamesdialog);
        ArrayList<EditTextModel> list = new ArrayList<EditTextModel>();
        for(int i = 1; i<=number ;i++)
        {
            EditTextModel editTextModel = new EditTextModel();
            list.add(editTextModel);
        }
        ListView listView = dialog.findViewById(R.id.nameslist);
        Button submit = dialog.findViewById(R.id.submit);
        NamesAdapter namesAdapter = new NamesAdapter(MainActivity.this,list);
        listView.setAdapter(namesAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                int iter = 1;
                for(EditTextModel editTextModel : NamesAdapter.editTextModelArrayList)
                {
                    if(editTextModel.getEditTextValue().equals(""))
                    {
                        Toast.makeText(MainActivity.this,"Enter Player Name "+iter,Toast.LENGTH_LONG).show();
                        return;
                    }
                    names.put(iter++,editTextModel.getEditTextValue());
                }
                Log.e("PlayerNames",String.valueOf(names));

                final Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra("Mode",MODE_TWO);
                intent.putExtra("playerNames",names);
                Toast.makeText(MainActivity.this,number+" players Created",Toast.LENGTH_LONG).show();
                dialog.dismiss();
                startActivity(intent);
                logo.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                playButton.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
                ((AnimationDrawable) animationView.getBackground()).start();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        logo.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.GONE);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        animationView.setVisibility(View.INVISIBLE);
    }
}
