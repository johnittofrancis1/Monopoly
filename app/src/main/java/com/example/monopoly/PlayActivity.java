package com.example.monopoly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Animation;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayActivity extends AppCompatActivity implements OnClickListener, View.OnLongClickListener {
    int dice1,dice2;
    private long mLastClickTime = 0;
    private static Map<Integer, ImageView> imageViewMap;
    private static Map<Integer, Bitmap> updatedBitmaps;
    Intent intent;
    Board board;
    public static int MODE;
    ImageView imageView;
    ImageView player1;
    ImageView player2;
    ImageView player3;
    ImageView player4;
    TextView money1, money2, money3, money4;
    Bitmap dice1Bitmap = null, dice2Bitmap = null;
    private static Map<Integer, City> cities;
    private static Map<Integer, Player> players;
    private static PlayActivity instance;
    private static Player.notifier moneyNotifier;
    public int choiceSelected;
    public Property selectedProperty = null;
    int noPlayers;
    Boolean nextChance = false;
    public static Player.OnCompleteListener onCompleteListener;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;
    private boolean computerNextChance;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        setContentView(R.layout.activity_play);
        instance = this;
        moneyNotifier = new Player.notifier() {
            @Override
            public void notified(Object obj) {
                PlayActivity.this.updateMoneyDisplay();
            }

            @Override
            public void notifierCancelled() {

            }
        };

        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
        player3 = findViewById(R.id.player3);
        player4 = findViewById(R.id.player4);

        money1 = findViewById(R.id.money1);
        money2 = findViewById(R.id.money2);
        money3 = findViewById(R.id.money3);
        money4 = findViewById(R.id.money4);

        //instantiating Board
        board = new Board(PlayActivity.this);

        //determining Mode of Game and Adding Players
        intent = getIntent();
        MODE = intent.getIntExtra("Mode", 1);
        if (MODE == MainActivity.MODE_ONE) {
            noPlayers = 2;
            board.computerMode();
        } else if (MODE == MainActivity.MODE_TWO) {
            HashMap<Integer, String> names = (HashMap<Integer, String>) intent.getSerializableExtra("playerNames");
            noPlayers = names.size();
            board.addPlayers(names);
        }


        RelativeLayout relativeLayout = findViewById(R.id.relativelayout);


        if (noPlayers == 2) {
            relativeLayout.removeView(findViewById(R.id.coin3));
            relativeLayout.removeView(findViewById(R.id.coin4));
            relativeLayout.removeView(money3);
            relativeLayout.removeView(money4);
            relativeLayout.removeView(player3);
            relativeLayout.removeView(player4);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) player2.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

            ImageView coin2 = findViewById(R.id.coin2);
            params = (RelativeLayout.LayoutParams) coin2.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.money2);
            params.addRule(RelativeLayout.LEFT_OF, R.id.player2);
            params.removeRule(RelativeLayout.RIGHT_OF);

            params = (RelativeLayout.LayoutParams) money2.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.LEFT_OF, R.id.player2);

            if(MODE == MainActivity.MODE_ONE)
            {
                coin2.setRotation(0);
                money2.setRotation(0);
                player2.setRotation(0);
            }
            else
            {
                coin2.setRotation(180);
                player2.setRotation(180);
                money2.setRotation(180);
            }

        } else if (noPlayers == 3) {
            relativeLayout.removeView(findViewById(R.id.coin4));
            relativeLayout.removeView(money4);
            relativeLayout.removeView(player4);
        }

        cities = Board.getCities();
        players = Board.getPlayers();

        imageViewMap = new HashMap<Integer, ImageView>();
        updatedBitmaps = new HashMap<Integer, Bitmap>();

        imageView = findViewById(R.id.zero);
        imageViewMap.put(0, imageView);

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(imageView,updatedBitmaps.get(0));
            }
        });*/

        // Retrieve and cache the system'fadein default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        imageView = findViewById(R.id.one);
        imageViewMap.put(1, imageView);

        imageView = findViewById(R.id.two);
        imageViewMap.put(2, imageView);

        imageView = findViewById(R.id.three);
        imageViewMap.put(3, imageView);

        imageView = findViewById(R.id.four);
        imageViewMap.put(4, imageView);

        imageView = findViewById(R.id.five);
        imageViewMap.put(5, imageView);

        imageView = findViewById(R.id.six);
        imageViewMap.put(6, imageView);

        imageView = findViewById(R.id.seven);
        imageViewMap.put(7, imageView);

        imageView = findViewById(R.id.eight);
        imageViewMap.put(8, imageView);

        imageView = findViewById(R.id.nine);
        imageViewMap.put(9, imageView);

        imageView = findViewById(R.id.ten);
        imageViewMap.put(10, imageView);

        imageView = findViewById(R.id.eleven);
        imageViewMap.put(11, imageView);

        imageView = findViewById(R.id.twelve);
        imageViewMap.put(12, imageView);

        imageView = findViewById(R.id.thirteen);
        imageViewMap.put(13, imageView);

        imageView = findViewById(R.id.fourteen);
        imageViewMap.put(14, imageView);

        imageView = findViewById(R.id.fifteen);
        imageViewMap.put(15, imageView);

        imageView = findViewById(R.id.sixteen);
        imageViewMap.put(16, imageView);

        imageView = findViewById(R.id.seventeen);
        imageViewMap.put(17, imageView);

        imageView = findViewById(R.id.eighteen);
        imageViewMap.put(18, imageView);

        imageView = findViewById(R.id.nineteen);
        imageViewMap.put(19, imageView);

        imageView = findViewById(R.id.twenty);
        imageViewMap.put(20, imageView);

        imageView = findViewById(R.id.twentyone);
        imageViewMap.put(21, imageView);

        imageView = findViewById(R.id.twentytwo);
        imageViewMap.put(22, imageView);

        imageView = findViewById(R.id.twentythree);
        imageViewMap.put(23, imageView);

        imageView = findViewById(R.id.twentyfour);
        imageViewMap.put(24, imageView);

        imageView = findViewById(R.id.twentyfive);
        imageViewMap.put(25, imageView);

        imageView = findViewById(R.id.twentysix);
        imageViewMap.put(26, imageView);

        imageView = findViewById(R.id.twentyseven);
        imageViewMap.put(27, imageView);

        imageView = findViewById(R.id.twentyeight);
        imageViewMap.put(28, imageView);

        imageView = findViewById(R.id.twentynine);
        imageViewMap.put(29, imageView);

        imageView = findViewById(R.id.thirty);
        imageViewMap.put(30, imageView);

        imageView = findViewById(R.id.thirtyone);
        imageViewMap.put(31, imageView);

        imageView = findViewById(R.id.thirtytwo);
        imageViewMap.put(32, imageView);

        imageView = findViewById(R.id.thirtythree);
        imageViewMap.put(33, imageView);

        imageView = findViewById(R.id.thirtyfour);
        imageViewMap.put(34, imageView);

        imageView = findViewById(R.id.thirtyfive);
        imageViewMap.put(35, imageView);

        imageView = findViewById(R.id.thirtysix);
        imageViewMap.put(36, imageView);

        imageView = findViewById(R.id.thirtyseven);
        imageViewMap.put(37, imageView);

        imageView = findViewById(R.id.thirtyeight);
        imageViewMap.put(38, imageView);

        imageView = findViewById(R.id.thirtynine);
        imageViewMap.put(39, imageView);

        for (Map.Entry<Integer, ImageView> entry : imageViewMap.entrySet()) {
            City city = cities.get(entry.getKey());
            city.setImageView(entry.getValue());
        }
        board.start();
        updateMoneyDisplay();
        updateBoard();

        player1.setOnClickListener(this);
        player2.setOnClickListener(this);
        player3.setOnClickListener(this);
        player4.setOnClickListener(this);

        player1.setOnLongClickListener(this);
        player2.setOnLongClickListener(this);
        player3.setOnLongClickListener(this);
        player4.setOnLongClickListener(this);

        for (final Map.Entry<Integer, ImageView> entry : imageViewMap.entrySet()) {
            entry.getValue().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(entry.getValue(), updatedBitmaps.get(entry.getKey()));
                }
            });
        }
        adjustBoardSize();
    }

    private void adjustBoardSize() {
        /*ImageView expandedImageView = findViewById(R.id.expanded_image);
        expandedImageView.requestLayout();
        expandedImageView.getLayoutParams().width = (int)SizeDisplay.getBoardSize();
        expandedImageView.getLayoutParams().height = (int)SizeDisplay.getBoardSize();*/
        RelativeLayout boardLayout = findViewById(R.id.board);
        boardLayout.getLayoutParams().width = (int)SizeDisplay.getBoardSize();
        boardLayout.getLayoutParams().height = (int)SizeDisplay.getBoardSize();
        Log.e("Board","Size: "+boardLayout.getLayoutParams().width);
        for (Map.Entry<Integer,ImageView> entry : imageViewMap.entrySet()){
            ImageView imageView = entry.getValue();
            imageView.requestLayout();
            if(entry.getKey() % 10 == 0)
            {
                imageView.getLayoutParams().width = (int)SizeDisplay.getSqrCitySize();
                imageView.getLayoutParams().height = (int)SizeDisplay.getSqrCitySize();
            }
            else if((entry.getKey()>30 && entry.getKey()<40) || (entry.getKey()>10 && entry.getKey()<20) )
            {
                imageView.getLayoutParams().width = (int)SizeDisplay.getCityWidth();
                imageView.getLayoutParams().height = (int)SizeDisplay.getCityHeight();
            }
            else
            {
                imageView.getLayoutParams().width = (int)SizeDisplay.getCityHeight();
                imageView.getLayoutParams().height = (int)SizeDisplay.getCityWidth();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateBoard() {

        for (Map.Entry<Integer, ImageView> entry : imageViewMap.entrySet()) {
            int position = entry.getKey();
            ImageView imgView = entry.getValue();
            City city = cities.get(position);
            Bitmap bitmap = city.getUpdatedBitmap();
            updatedBitmaps.put(position, bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }


    public static PlayActivity getInstance() {
        return instance;
    }

    public static Player.notifier getMoneyNotifier() {
        return moneyNotifier;
    }

    public void showMessage(String message, final Player player, final Property property) {
        final Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.playerquestion);
        RelativeLayout relativeLayout;
        //Drawable background = getResources().getDrawable(R.drawable.whitetexture);

        switch (player.getId()) {
            case 1: {
                relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2) {
                    relativeLayout = dialog.findViewById(R.id.relativelayout);
                    relativeLayout.setRotation(180);
                } else {
                    dialog.setContentView(R.layout.playerquestion1);
                    relativeLayout = dialog.findViewById(R.id.relativelayout);
                }
                break;
            }
            case 3: {
                relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(180);
                break;
            }
            case 4: {
                dialog.setContentView(R.layout.playerquestion1);
                relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(180);
                break;
            }
        }

        //relativeLayout.setBackground(background);
        TextView textView = dialog.findViewById(R.id.message);
        Button ok = dialog.findViewById(R.id.ok);
        Button cancel = dialog.findViewById(R.id.cancel);

        /*Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/
        textView.setText(message);
        ok.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    player.buyProperty(property);
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayActivity.onCompleteListener.OnComplete(player);
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showCard(Player player, String title, String message, Dialog.OnDismissListener callBack) {

        final Dialog dialog = new Dialog(PlayActivity.this);
        if (title.equals("Chance"))
            dialog.setContentView(R.layout.chancecard);
        else if (title.equals("Community Chest"))
            dialog.setContentView(R.layout.communitycard);
        RelativeLayout cardLayout = dialog.findViewById(R.id.cardlayout);
        switch (player.getId()) {
            case 1: {
                cardLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2) {
                    cardLayout.setRotation(180);
                    if(player instanceof Computer)
                        cardLayout.setRotation(0);
                }
                else
                    cardLayout.setRotation(90);
                break;
            }
            case 3: {
                cardLayout.setRotation(270);
                break;
            }
            case 4: {
                cardLayout.setRotation(360);
                break;
            }
        }
        TextView details = dialog.findViewById(R.id.details);

        /*Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/

        details.setText(message);

        dialog.setOnDismissListener(callBack);
        dialog.show();
    }

    public void showChoices(final List<String> choicesList, final Player player, Dialog.OnDismissListener callBack) {
        final Dialog dialog = new Dialog(PlayActivity.this);

        switch (player.getId()) {
            case 1: {
                dialog.setContentView(R.layout.choices);
                RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2) {
                    dialog.setContentView(R.layout.choices);
                    RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
                    relativeLayout.setRotation(180);
                } else {
                    dialog.setContentView(R.layout.choices1);
                    RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
                }
                break;
            }
            case 3: {
                dialog.setContentView(R.layout.choices);
                RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(180);
                break;
            }
            case 4: {
                dialog.setContentView(R.layout.choices1);
                RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
                relativeLayout.setRotation(180);
                break;
            }
        }
        ListView listView = dialog.findViewById(R.id.listview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlayActivity.this, R.layout.choice, R.id.choicetext, choicesList);
        listView.setAdapter(arrayAdapter);

        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choiceSelected = i + 1;
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(callBack);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showProperties(String title, final List<Property> choicesList, final Player player, Dialog.OnDismissListener callBack) {
        final Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.propertyrecyclerview);
        RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
        switch (player.getId()) {
            case 1: {
                relativeLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2)
                    relativeLayout.setRotation(180);
                else {
                    //Drawable background = getResources().getDrawable(R.drawable.whitetexture1);
                    //relativeLayout.setBackground(background);
                    relativeLayout.setRotation(90);
                }
                break;
            }
            case 3: {
                relativeLayout.setRotation(180);
                break;
            }
            case 4: {
                //Drawable background = getResources().getDrawable(R.drawable.whitetexture1);
                //relativeLayout.setBackground(background);
                relativeLayout.setRotation(270);
                break;
            }
        }
        TextView titleView = dialog.findViewById(R.id.message);
        titleView.setText(title);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);
        PropertyAdapter propertyAdapter = new PropertyAdapter(choicesList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View V, int position) {
                selectedProperty = choicesList.get(position);
                Log.e("Selected Property", selectedProperty.getCityName());
                dialog.dismiss();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PlayActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(propertyAdapter);

        dialog.show();

        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                selectedProperty = null;
            }
        });
        if (callBack != null) {
            dialog.setOnDismissListener(callBack);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showPlayers(Player currentPlayer) {
        Bitmap player1Face = BitmapFactory.decodeResource(getResources(), R.drawable.blueface);
        Bitmap player2Face = BitmapFactory.decodeResource(getResources(), R.drawable.redface);
        Bitmap player3Face = BitmapFactory.decodeResource(getResources(), R.drawable.yellowface);
        Bitmap player4Face = BitmapFactory.decodeResource(getResources(), R.drawable.greenface);

        player1.setImageBitmap(player1Face);
        player2.setImageBitmap(player2Face);
        player3.setImageBitmap(player3Face);
        player4.setImageBitmap(player4Face);

        if (currentPlayer.getId() == 1) {
            Bitmap box = BitmapFactory.decodeResource(getResources(), R.drawable.bluebox);
            player1.setImageBitmap(box);
        } else if (currentPlayer.getId() == 2) {
            Bitmap box = BitmapFactory.decodeResource(getResources(), R.drawable.redbox);
            player2.setImageBitmap(box);
        } else if (currentPlayer.getId() == 3) {
            Bitmap box = BitmapFactory.decodeResource(getResources(), R.drawable.yellowbox);
            player3.setImageBitmap(box);
        } else {
            Bitmap box = BitmapFactory.decodeResource(getResources(), R.drawable.greenbox);
            player4.setImageBitmap(box);
        }
    }

    public void updateMoneyDisplay() {
        if (players.size() >= 2) {
            money1.setText(String.valueOf(players.get(1).getMoney()));
            money2.setText(String.valueOf(players.get(2).getMoney()));
        }
        if (players.size() >= 3)
            money3.setText(String.valueOf(players.get(3).getMoney()));
        if (players.size() == 4)
            money4.setText(String.valueOf(players.get(4).getMoney()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Player currentPlayer = null;
        ImageView currentImgView = null;
        Typeface monopolyBold = ResourcesCompat.getFont(PlayActivity.getInstance(), R.font.monopolybold);


        switch (view.getId()) {
            case R.id.player1: {
                currentImgView = player1;
                currentPlayer = players.get(1);
                break;
            }
            case R.id.player2: {
                currentImgView = player2;
                currentPlayer = players.get(2);
                break;
            }
            case R.id.player3: {
                currentImgView = player3;
                currentPlayer = players.get(3);
                break;
            }
            case R.id.player4: {
                currentImgView = player4;
                currentPlayer = players.get(4);
            }
        }
        if (currentPlayer instanceof Computer)
            return;
        if (currentPlayer.getDiceToken()) {
            if(rollDice(currentImgView))
            {
                final Player finalCurrentPlayer = currentPlayer;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalCurrentPlayer.move(dice1, dice2, new Player.OnStuckListener() {
                            @Override
                            public void OnStuck(Player player) {
                                nextChance = false;
                                Log.e("NextChance", player.getName() + " " + nextChance);
                            }

                            @Override
                            public void OnAnotherChance(Player player) {
                                nextChance = true;
                                Log.e("NextChance", player.getName() + " " + nextChance);
                            }
                        }, onCompleteListener = new Player.OnCompleteListener() {
                            @Override
                            public void OnComplete(Player player) {
                                Log.e("OnComplete", "Completed By " + player.getName());
                                if (nextChance) {
                                    player.setDiceToken(true);
                                    showPlayers(player);
                                } else {
                                    player.setDiceToken(false);
                                    player.setCount(0);
                                    Board.playerToPlay();
                                }
                            }
                        });

                    }
                }, 2000);
            }

        }
    }


    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
        // If there'fadein an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageBitmap(bitmap);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view'fadein offset as the origin for the
        // bounds, since that'fadein the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.relativelayout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        //invisible all the imageviews.*
        for (Map.Entry<Integer, ImageView> entry : imageViewMap.entrySet()) {
            entry.getValue().setAlpha(0f);
        }
        findViewById(R.id.gamename).setAlpha(0f);
        findViewById(R.id.chancedisplay).setAlpha(0f);
        findViewById(R.id.communitydisplay).setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        //after 2 seconds zoomedimage goes down to thumbnail again
        final float startScaleFinal = startScale;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        //visible all the imageview
                        for (Map.Entry<Integer, ImageView> entry : imageViewMap.entrySet()) {
                            entry.getValue().setAlpha(1f);
                        }
                        findViewById(R.id.gamename).setAlpha(1f);
                        findViewById(R.id.chancedisplay).setAlpha(1f);
                        findViewById(R.id.communitydisplay).setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }

        }, 2000);

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        /*expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    /*private void showPopup(final View thumbView, final ScrollView popupScrollView) {
        // If there'fadein an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }


        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        final Point thumbViewOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view'fadein offset as the origin for the
        // bounds, since that'fadein the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds,thumbViewOffset);
        findViewById(R.id.relativelayout).getGlobalVisibleRect(finalBounds,globalOffset);
        startBounds.offset(-thumbViewOffset.x, -thumbViewOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //invisible all the imageviews.*
        final RelativeLayout boardLayout = findViewById(R.id.board);
        boardLayout.setAlpha(0.25f);
        boardLayout.setFocusable(false);
        popupScrollView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        //popupScrollView.setPivotX(0f);
        //popupScrollView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(popupScrollView, View.X,
                        startBounds.left, startBounds.right))
                .with(ObjectAnimator.ofFloat(popupScrollView, View.Y,
                        startBounds.top, startBounds.bottom))
                .with(ObjectAnimator.ofFloat(popupScrollView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(popupScrollView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        findViewById(R.id.relativelayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(popupScrollView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(popupScrollView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(popupScrollView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(popupScrollView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        boardLayout.setAlpha(1f);
                        boardLayout.setFocusable(true);
                        popupScrollView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        popupScrollView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }*/

    @Override
    public boolean onLongClick(View view) {

        mLastClickTime = SystemClock.elapsedRealtime();
        Player currentPlayer = null;
        final ScrollView popup = findViewById(R.id.playerpopup);
        LinearLayout popupLayout = findViewById(R.id.popuplayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) popup.getLayoutParams();

        switch (view.getId()) {
            case R.id.player1: {
                currentPlayer = players.get(1);
                params.removeRule(RelativeLayout.BELOW);
                params.removeRule(RelativeLayout.LEFT_OF);
                params.addRule(RelativeLayout.ABOVE, R.id.player1);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.player1);
                popupLayout.setRotation(0);
                break;
            }
            case R.id.player2: {
                currentPlayer = players.get(2);
                if (noPlayers == 2) {
                    params.removeRule(RelativeLayout.ABOVE);
                    params.removeRule(RelativeLayout.RIGHT_OF);
                    params.addRule(RelativeLayout.BELOW, R.id.player2);
                    params.addRule(RelativeLayout.LEFT_OF, R.id.player2);
                    if(!(currentPlayer instanceof Computer))
                    {
                        popupLayout.setRotation(180);
                    }
                    else
                    {
                        popupLayout.setRotation(360);
                        Log.e("Popup","computer");
                    }
                } else {
                    params.removeRule(RelativeLayout.ABOVE);
                    params.removeRule(RelativeLayout.LEFT_OF);
                    params.addRule(RelativeLayout.BELOW, R.id.player2);
                    params.addRule(RelativeLayout.RIGHT_OF, R.id.player2);
                    popupLayout.setRotation(90);
                }

                break;
            }
            case R.id.player3: {
                currentPlayer = players.get(3);
                params.removeRule(RelativeLayout.ABOVE);
                params.removeRule(RelativeLayout.RIGHT_OF);
                params.addRule(RelativeLayout.BELOW, R.id.player3);
                params.addRule(RelativeLayout.LEFT_OF, R.id.player3);
                popupLayout.setRotation(180);
                break;
            }
            case R.id.player4: {
                currentPlayer = players.get(4);
                params.removeRule(RelativeLayout.BELOW);
                params.removeRule(RelativeLayout.RIGHT_OF);
                params.addRule(RelativeLayout.ABOVE, R.id.player4);
                params.addRule(RelativeLayout.LEFT_OF, R.id.player4);
                popupLayout.setRotation(270);
            }
        }
        if (currentPlayer.getDiceToken())
            return false;
        popup.setLayoutParams(params);
        popup.setVisibility(View.VISIBLE);
        final RelativeLayout boardLayout = findViewById(R.id.board);
        boardLayout.setClickable(false);
        findViewById(R.id.relativelayout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setVisibility(View.GONE);
                boardLayout.setClickable(true);
            }
        });

        TextView playerName = findViewById(R.id.playername);
        TextView numProperties = findViewById(R.id.propertyno);
        TextView totValue = findViewById(R.id.totValue);
        TextView mortgageNo = findViewById(R.id.mortgageno);
        TextView releaseMortgage = findViewById(R.id.releaseMortgage);

        playerName.setText(currentPlayer.getName());
        numProperties.setText("Properties: " + currentPlayer.getNumOwnedProperties());
        totValue.setText("Total Value: " + currentPlayer.getTotalValue());
        mortgageNo.setText("Mortgages: " + currentPlayer.getNumMortgagedProperties());
        releaseMortgage.setText("Release Mortgages");

        final Player finalCurrentPlayer = currentPlayer;
        numProperties.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    finalCurrentPlayer.showOwnedProperties();
                }
            }
        });

        releaseMortgage.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    finalCurrentPlayer.releaseProperties();
                }
            }
        });

        return true;
    }

    public void showNotification(Player player, String message) {
        TextView notifyView = null;
        switch (player.getId()) {
            case 1:
                notifyView = findViewById(R.id.player1notification);
                break;
            case 2: {
                if (noPlayers == 2)
                {
                    notifyView = findViewById(R.id.player3notification);
                    if(player instanceof Computer)
                        notifyView.setRotation(0);

                }
                else
                    notifyView = findViewById(R.id.player2notification);
            }
            break;
            case 3:
                notifyView = findViewById(R.id.player3notification);
                break;
            case 4:
                notifyView = findViewById(R.id.player4notification);
                break;
        }
        notifyView.setText(message);

        notifyView.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        final TextView finalNotifyView = notifyView;
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finalNotifyView.setVisibility(View.INVISIBLE);
            }

        }, 2000);
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.playerquestion);

        TextView textView = dialog.findViewById(R.id.message);
        Button ok = dialog.findViewById(R.id.ok);
        Button cancel = dialog.findViewById(R.id.cancel);

        /*Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/

        textView.setText("Are You Sure You Want To Finish The Game?");
        ok.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    board.finishGame();
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void computerPlay(final Computer computer)
    {
        if(computer.getDiceToken())
        {
            if(rollDice(player2))
            {
                final Computer finalComputer = computer;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    finalComputer.move(dice1, dice2, new Player.OnStuckListener() {
                        @Override
                        public void OnStuck(Player player) {
                            computerNextChance = false;
                            Log.e("NextChance", player.getName() + computerNextChance);
                        }

                        @Override
                        public void OnAnotherChance(Player player) {
                            computerNextChance = true;
                            Log.e("NextChance", player.getName() + computerNextChance);
                        }
                    }, onCompleteListener = new Player.OnCompleteListener() {
                        @Override
                        public void OnComplete(final Player player) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("OnComplete", "Computer completed");
                                    if(computerNextChance)
                                    {
                                        final Computer computer1 = (Computer) player;
                                        player.setDiceToken(true);
                                        showPlayers(player);
                                        Log.e("OnComplete","Calling");
                                        computerPlay(computer1);
                                        Log.e("OnComplete","Called another Time");

                                    } else {
                                        player.setDiceToken(false);
                                        player.setCount(0);
                                        Board.playerToPlay();
                                    }
                                }
                            },2000);
                        }
                    });
                }
            }

        }

    }

    public Boolean rollDice(ImageView imageView) {
        ImageView currentImgView = imageView;
        Drawable currentDrawable = currentImgView.getDrawable();
            Random rand = new Random();
            dice1 = (rand.nextInt(50) % 6) + 1;
            dice2 = (rand.nextInt(50) % 6) + 1;
            Log.e("Dice","Rolled: "+dice1+" : "+dice2);
            switch (dice1) {
                case 1:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice1);
                    break;
                case 2:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice2);
                    break;
                case 3:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice3);
                    break;
                case 4:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice4);
                    break;
                case 5:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice5);
                    break;
                case 6:
                    dice1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice6);
                    break;
            }
            switch (dice2) {
                case 1:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice1);
                    break;
                case 2:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice2);
                    break;
                case 3:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice3);
                    break;
                case 4:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice4);
                    break;
                case 5:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice5);
                    break;
                case 6:
                    dice2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice6);
                    break;
            }

            //Animation for Rolling Dice
            final Drawable finalCurrentDrawable = currentDrawable;
            final ImageView finalImageView = currentImgView;
            final Animation diceAnimation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.diceanim);
            diceAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Bitmap bitmap = ((BitmapDrawable) finalCurrentDrawable).getBitmap();
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);

                    int diceWidth = mutableBitmap.getWidth()/3;
                    int diceHeight = mutableBitmap.getHeight()/3;

                    int remainingWidth = mutableBitmap.getWidth() - (diceWidth*2);
                    int remainingHeight = mutableBitmap.getHeight() - diceHeight;
                    int tempWidth = remainingWidth /5;

                    int diceTopOffset = remainingHeight / 2;
                    int dice1LeftOffset = tempWidth*2;
                    int dice2LeftOffset = dice1LeftOffset + diceWidth + tempWidth;

                    dice1Bitmap = Bitmap.createScaledBitmap(dice1Bitmap, mutableBitmap.getWidth()/3, mutableBitmap.getHeight()/3, true);
                    dice2Bitmap = Bitmap.createScaledBitmap(dice2Bitmap, mutableBitmap.getWidth()/3, mutableBitmap.getHeight()/3, true);
                    Log.e("Dice",dice1Bitmap.getWidth()+" : "+dice1Bitmap.getHeight());

                    canvas.drawBitmap(dice1Bitmap, dice1LeftOffset, diceTopOffset, null);
                    canvas.drawBitmap(dice2Bitmap, dice2LeftOffset, diceTopOffset, null);
                    finalImageView.setImageBitmap(mutableBitmap);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            currentImgView.startAnimation(diceAnimation);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showOwnedProperties(final List<Property> choicesList, final Player player) {
        String title = "Owned Properties";
        final Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.propertyrecyclerview);
        RelativeLayout relativeLayout = dialog.findViewById(R.id.relativelayout);
        switch (player.getId()) {
            case 1: {
                relativeLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2)
                {
                    if(!(player instanceof Computer))
                        relativeLayout.setRotation(180);
                }
                else {
                    //Drawable background = getResources().getDrawable(R.drawable.whitetexture1);
                    //relativeLayout.setBackground(background);
                    relativeLayout.setRotation(90);
                }
                break;
            }
            case 3: {
                relativeLayout.setRotation(180);
                break;
            }
            case 4: {
                //Drawable background = getResources().getDrawable(R.drawable.whitetexture1);
                //relativeLayout.setBackground(background);
                relativeLayout.setRotation(270);
                break;
            }
        }
        TextView titleView = dialog.findViewById(R.id.message);
        titleView.setText(title);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);
        PropertyAdapter propertyAdapter = new PropertyAdapter(choicesList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View V, int position) {
                selectedProperty = choicesList.get(position);
                showTitleDeed(player,selectedProperty,null);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PlayActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(propertyAdapter);

        dialog.show();

        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                selectedProperty = null;
            }
        });
    }

    public void showTitleDeed(Player player, final Property property,Dialog.OnDismissListener callBack) {
        final Dialog titleDeed = new Dialog(PlayActivity.this);

        if(property instanceof Station)
        {
            titleDeed.setContentView(R.layout.station_titledeed);
            TextView name = titleDeed.findViewById(R.id.name);
            TextView rent = titleDeed.findViewById(R.id.rent);
            TextView station_2_rent = titleDeed.findViewById(R.id.station2);
            TextView station_3_rent = titleDeed.findViewById(R.id.station3);
            TextView station_4_rent = titleDeed.findViewById(R.id.station4);
            TextView mortgage = titleDeed.findViewById(R.id.mortgage);

            name.setText(property.getCityName().toUpperCase());
            rent.setText("$"+property.getPrimaryRent());
            station_2_rent.setText("$"+property.getHouse1Rent());
            station_3_rent.setText("$"+property.getHouse2Rent());
            station_4_rent.setText("$"+property.getHouse3Rent());
            mortgage.setText("Mortgage Value - $"+property.getMortgage());
        }
        else if(property instanceof Utilities)
        {
            titleDeed.setContentView(R.layout.utility_titledeed);
            ImageView logo = titleDeed.findViewById(R.id.logo);
            TextView name = titleDeed.findViewById(R.id.name);
            TextView mortgage = titleDeed.findViewById(R.id.mortgage);

            if(property.getCityName().equals("Electric Company"))
                logo.setImageResource(R.drawable.bulb);
            else if(property.getCityName().equals("Water Works"))
                logo.setImageResource(R.drawable.faucet);
            name.setText(property.getCityName().toUpperCase());
            mortgage.setText("Mortgage Value - $"+property.getMortgage());
        }
        else
        {
            titleDeed.setContentView(R.layout.titledeed);
            RelativeLayout titleCard = titleDeed.findViewById(R.id.titlecard);
            LayerDrawable layer_drawable = (LayerDrawable)titleCard.getBackground();
            final GradientDrawable shapeDrawable = (GradientDrawable) layer_drawable
                    .findDrawableByLayerId(R.id.shape);
            shapeDrawable.setColor(property.getColorId());

            TextView propertyName = titleDeed.findViewById(R.id.name);
            TextView rent = titleDeed.findViewById(R.id.rent);
            TextView house_1_rent = titleDeed.findViewById(R.id.house1);
            TextView house_2_rent = titleDeed.findViewById(R.id.house2);
            TextView house_3_rent = titleDeed.findViewById(R.id.house3);
            TextView hotel_rent = titleDeed.findViewById(R.id.hotel);
            TextView mortgage = titleDeed.findViewById(R.id.mortgage);
            TextView house_price = titleDeed.findViewById(R.id.houseprice);
            TextView hotel_price = titleDeed.findViewById(R.id.hotelprice);

            propertyName.setText(property.getCityName().toUpperCase());
            rent.setText("RENT $"+property.getPrimaryRent());
            house_1_rent.setText("$"+property.getHouse1Rent());
            house_2_rent.setText("$"+property.getHouse2Rent());
            house_3_rent.setText("$"+property.getHouse3Rent());
            hotel_rent.setText("$"+property.getHotelRent());
            mortgage.setText("Mortgage Value $"+property.getMortgage());
            house_price.setText("Houses cost $"+property.getHousePrice()+" each");
            hotel_price.setText("Hotels cost $"+property.getHousePrice()+" each\nplus 3 houses");
        }
        RelativeLayout relativeLayout = titleDeed.findViewById(R.id.titledeed);
        switch (player.getId()) {
            case 1: {
                relativeLayout.setRotation(0);
                break;
            }
            case 2: {
                if (noPlayers == 2)
                {
                    if(!(player instanceof Computer))
                        relativeLayout.setRotation(180);
                }
                else {
                    relativeLayout.setRotation(90);
                }
                break;
            }
            case 3: {
                relativeLayout.setRotation(180);
                break;
            }
            case 4: {
                //Drawable background = getResources().getDrawable(R.drawable.whitetexture1);
                //relativeLayout.setBackground(background);
                relativeLayout.setRotation(270);
                break;
            }
        }

        titleDeed.show();
        if(callBack != null)
        {
            titleDeed.setOnDismissListener(callBack);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Animation exitAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
        exitAnimation.start();
    }
}