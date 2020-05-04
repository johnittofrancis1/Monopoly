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
class Station extends Property implements visitable{
    private PlayActivity playActivity;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Station(int position, String cityName) {
        super(position,cityName,200,25,100,50,100,200,0,0);
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

        //border
        Rect rect = new Rect(0,0,this.width,this.height);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect,paint);

        //station name
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setTextSize(SizeDisplay.STATION_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        paint.setTextAlign(Paint.Align.CENTER);

        String[] name = this.getCityName().split("\\s");
        String lastWord = name[name.length-1];
        float midHeight = SizeDisplay.STATION_TEXT_TOP_OFFSET*SizeDisplay.getPhoneDensity();
        for(String word : name)
        {
            canvas.drawText(word,this.width/2,midHeight,paint);
            midHeight = midHeight+(int)(SizeDisplay.STATION_TEXT_SEPARATOR*SizeDisplay.getPhoneDensity());
        }


        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.train);
        Bitmap trainBitmap = Bitmap.createScaledBitmap(roughBitmap,(int)(SizeDisplay.STATION_BITMAP_SIZE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.STATION_BITMAP_SIZE*SizeDisplay.getPhoneDensity()),true);
        canvas.drawBitmap(trainBitmap,SizeDisplay.STATION_BITMAP_LEFT_OFFSET*SizeDisplay.getPhoneDensity(),SizeDisplay.STATION_BITMAP_TOP_OFFSET*SizeDisplay.getPhoneDensity(),paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    public void doubleRent(int ownedStations)
    {
        if(ownedStations == 2)
            this.setRent(this.getHouse1Rent());
        else if(ownedStations == 3)
            this.setRent(this.getHouse2Rent());
        else if(ownedStations == 4)
            this.setRent(this.getHouse3Rent());
        playActivity.showNotification(this.getOwner(),this.getCityName()+" Rent is increased to "+this.getRent());
    }
}
