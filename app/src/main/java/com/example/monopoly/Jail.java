package com.example.monopoly;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

/**
 *
 * @author johni
 */
class Jail extends City implements visitable{
    private static Jail instance;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Jail() {
        super(10,"Jail",0);
        instance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createImage() {
        Typeface monopolyBold = ResourcesCompat.getFont(PlayActivity.getInstance(),R.font.monopolybold);
        this.bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(monopolyBold);

        //jail background
        paint.setAlpha(20);
        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.jail);
        Bitmap jailBitmap = Bitmap.createScaledBitmap(roughBitmap,this.width,this.height,true);
        canvas.drawBitmap(jailBitmap,0,0,paint);

        //border the square
        Rect rect = new Rect(0,0,this.width,this.height);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect,paint);

        //type the cityName
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(ContextCompat.getColor(Board.getContext(),R.color.mediumblue));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(SizeDisplay.JAIL_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        canvas.drawText("Jail",this.width/2,this.height/2,paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    @Override
    public void visit(Player player) {
        this.playerInCity(player);
        System.out.println(player.getName()+" is just visiting the Jail");
        PlayActivity.onCompleteListener.OnComplete(player);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void imprisonPlayer(Player player)
    {
        this.playerInCity(player);
    }

    public static Jail getInstance()
    {
        return instance;
    }
}
