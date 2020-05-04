package com.example.monopoly;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

/**
 *
 * @author johni
 */
class City implements visitable{
    private int position;
    private String cityName;
    private int colorId;
    protected Bitmap bitmap;
    private Bitmap lastBitmap;
    protected int width;
    protected int height;
    protected int left;
    protected int top;
    protected int right;
    protected int bottom;
    protected float angle;
    Map<Integer,Bitmap> generatedBitmaps;
    protected List<Player> playersInCity;
    protected ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public City(int position, String cityName,int colorId) {
        this.position = position;
        this.cityName = cityName;
        this.colorId = colorId;

        this.generatedBitmaps = new HashMap<Integer,Bitmap>();
        this.playersInCity = new LinkedList<Player>();

        if(this.position % 10 == 0)
        {
            this.width = (int)(SizeDisplay.CITY_SQUARE_SIZE * SizeDisplay.getPhoneDensity());
            this.height = (int)(SizeDisplay.CITY_SQUARE_SIZE * SizeDisplay.getPhoneDensity());
            this.left = 0;
            this.top = 0;
            this.right = this.width;
            this.bottom = this.height;
        }
        else
        {
            this.width = (int)(SizeDisplay.CITY_WIDTH * SizeDisplay.getPhoneDensity());
            this.height = (int)(SizeDisplay.CITY_HEIGHT * SizeDisplay.getPhoneDensity());
            this.left = 0;
            this.top = 0;
            this.right = this.width;
            this.bottom = (int)(SizeDisplay.CITY_COLOR_SIZE * SizeDisplay.getPhoneDensity());
        }
        if(this.position == 0)
        {
            this.angle = 0;
        }
        else if(this.position <= 10)
        {
            this.angle = 90;
        }
        else if(this.position <= 20)
        {
            this.angle = 180;
        }
        else if(this.position <= 30)
        {
            this.angle = 270;
        }
        else if(this.position < 40 )
        {
            this.angle = 0;
        }
        this.createImage();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createImage() {
        Typeface monopolyBold = ResourcesCompat.getFont(PlayActivity.getInstance(),R.font.monopolybold);
        bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(monopolyBold);

        Rect rect = new Rect(this.left,this.top,this.right,this.bottom);
        paint.setStyle(Paint.Style.FILL);
        if(this.colorId == 0)
        {
            paint.setColor(Color.TRANSPARENT);
            canvas.drawRect(rect,paint);
        }
        else
        {
            paint.setColor(this.colorId);
            canvas.drawRect(rect,paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect,paint);

        }

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        rect = new Rect(0,0,this.width,this.height);
        canvas.drawRect(rect,paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(1);
        paint.setTextSize((int)(SizeDisplay.CITY_TEXT_SIZE * SizeDisplay.getPhoneDensity()));

        String[] name = this.cityName.split("\\s");
        String lastWord = name[name.length-1];
        int midHeight = this.height/2;
        for(String word : name)
        {
            if(!word.equals(lastWord))
            {
                word = word.concat("\n");
            }
            canvas.drawText(word,this.width/2,midHeight,paint);
            midHeight = midHeight+(int)(SizeDisplay.CITY_TEXT_SEPARATOR * SizeDisplay.getPhoneDensity());
        }

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    public Bitmap getBitmap() {
        return rotateBitmap(this.bitmap);
    }

    public int getPosition() {
        return position;
    }

    public String getCityName() {
        return cityName;
    }

    public int getColorId() {
        return colorId;
    }

    @Override
    public String toString() {
        return "Position=" + position + " CityName=" + cityName+", Color=" + colorId;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void visit(Player player) {
        System.out.println("here");
        this.playerInCity(player);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playerInCity(Player player) {
        playersInCity.add(player);
        Collections.sort(playersInCity, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player t1) {
                return player.getId() - t1.getId();
            }
        });
        PlayActivity.updateBoard();
    }

    public Bitmap playerInBitmap(int id,Bitmap bitmap) {
        Bitmap playerPiece = Board.getPieces().get(id);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        float pieceLeft,pieceTop;
        if(id == 1)
        {
            if(this.position % 10 == 0)
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_1_PIECE_LEFT_SQR * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_1_PIECE_TOP_SQR * SizeDisplay.getPhoneDensity());
            }
            else
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_1_PIECE_LEFT * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_1_PIECE_TOP * SizeDisplay.getPhoneDensity());
            }
        }
        else if(id == 2)
        {
            if(this.position % 10 == 0)
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_2_PIECE_LEFT_SQR * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_2_PIECE_TOP_SQR * SizeDisplay.getPhoneDensity());
            }
            else
            {
                pieceLeft = (int)( SizeDisplay.PLAYER_2_PIECE_LEFT * SizeDisplay.getPhoneDensity());;
                pieceTop = (int)(SizeDisplay.PLAYER_2_PIECE_TOP * SizeDisplay.getPhoneDensity());
            }
        }
        else if(id == 3)
        {
            if(this.position % 10 == 0)
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_3_PIECE_LEFT_SQR * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_3_PIECE_TOP_SQR * SizeDisplay.getPhoneDensity());
            }
            else {
                pieceLeft = (int)(SizeDisplay.PLAYER_3_PIECE_LEFT * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_3_PIECE_TOP * SizeDisplay.getPhoneDensity());
            }
        }
        else {
            if(this.position % 10 == 0)
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_4_PIECE_LEFT_SQR * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_4_PIECE_TOP_SQR * SizeDisplay.getPhoneDensity());
            }
            else
            {
                pieceLeft = (int)(SizeDisplay.PLAYER_4_PIECE_LEFT * SizeDisplay.getPhoneDensity());
                pieceTop = (int)(SizeDisplay.PLAYER_4_PIECE_TOP * SizeDisplay.getPhoneDensity());
            }
        }
        canvas.drawBitmap(playerPiece,pieceLeft,pieceTop,paint);
        return bitmap;
    }

    protected Bitmap rotateBitmap(Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(this.angle);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateBitmaps()
    {
        int[] keys = {0,1,2,3,4,12,13,14,23,24,34,123,124,134,234,1234};
        for(int key : keys)
        {
            int iter = key;
            Bitmap generated = Bitmap.createBitmap(this.bitmap);
            while(iter > 0)
            {
                int digit = iter %10;
                generated = playerInBitmap(digit,generated);
                iter = iter/10;

            }
            generatedBitmaps.putIfAbsent(key,generated);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Bitmap getUpdatedBitmap() {
        String string = null;
        for(Player player : this.playersInCity)
        {
            if(string == null)
                string = String.valueOf(player.getId());
            else
                string = string.concat(String.valueOf(player.getId()));
        }
        int key = string!=null ?Integer.parseInt(string) : 0;
        return this.rotateBitmap(this.generatedBitmaps.getOrDefault(key,this.bitmap));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updatePlayerList(Player player) {
        this.playersInCity.remove(player);
        PlayActivity.updateBoard();
    }

}
