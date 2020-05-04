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
import androidx.core.content.res.ResourcesCompat;

/**
 *
 * @author johni
 */
public class Start extends City implements visitable{
    private static Start instance;
    private PlayActivity playActivity;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Start(String cityName) {
        super(0, cityName,0);
        instance = this;
        this.width = 150;
        this.height = 150;
        this.left = 0;
        this.top = 0;
        this.right = 150;
        this.bottom = 150;
        this.angle = 0;
        this.playActivity = (PlayActivity)PlayActivity.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createImage() {
        Typeface monopolyBold = ResourcesCompat.getFont(PlayActivity.getInstance(),R.font.monopolybold);
        this.bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(monopolyBold);

        Rect rect = new Rect(0,0,this.width,this.height);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect,paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor("#1d2951"));
        paint.setStrokeWidth(1);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(SizeDisplay.START_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        canvas.drawText("Go",this.width*2/3,this.height/2,paint);

        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.uparrow);
        Bitmap arrowBitmap = Bitmap.createScaledBitmap(roughBitmap,this.width/3,this.height*2/3,true);

        canvas.drawBitmap(arrowBitmap,this.width/6,this.height/6,paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }
    @Override
    public void visit(Player player)
    {
        this.playerInCity(player);
        playActivity.showNotification(player,"200 will be credited.");
        Bank.payPlayer(player, 200);
        PlayActivity.onCompleteListener.OnComplete(player);
    }

    public static Start getInstance() {
        return instance;
    }

    public void crossed(Player player) {
        playActivity.showNotification(player,"200 will be credited.");
        Bank.payPlayer(player, 200);
    }
}
