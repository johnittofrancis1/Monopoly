package com.example.monopoly;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 *
 * @author johni
 */
class Utilities extends Property implements visitable{
    private int multiplier;
    private PlayActivity playActivity;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Utilities(int position, String cityName) {
        super(position,cityName,150,0,75,0,0,0,0,0);
        this.multiplier = 4;
        this.playActivity = (PlayActivity)PlayActivity.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createImage() {
        this.bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //border
        Rect rect = new Rect(0,0,this.width,this.height);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect,paint);

        //station name
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setTextSize(SizeDisplay.UTILITY_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        paint.setTextAlign(Paint.Align.CENTER);

        String[] name = this.getCityName().split("\\s");
        String lastWord = name[name.length-1];
        float midHeight = SizeDisplay.UTILITY_TEXT_TOP_OFFSET*SizeDisplay.getPhoneDensity();
        for(String word : name)
        {
            canvas.drawText(word,this.width/2,midHeight,paint);
            midHeight = midHeight+SizeDisplay.UTILITY_TEXT_SEPARATOR*SizeDisplay.getPhoneDensity();
        }


        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = null;
        if(this.getCityName().equals("Electric Company"))
        {
            roughBitmap = BitmapFactory.decodeResource(res,R.drawable.bulb);
        }
        else if(this.getCityName().equals("Water Works"))
        {
            roughBitmap = BitmapFactory.decodeResource(res,R.drawable.faucet);
        }
        Bitmap trainBitmap = Bitmap.createScaledBitmap(roughBitmap,(int)(SizeDisplay.UTILITY_BITMAP_SIZE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.UTILITY_BITMAP_SIZE*SizeDisplay.getPhoneDensity()),true);
        canvas.drawBitmap(trainBitmap,SizeDisplay.UTILITY_BITMAP_LEFT_OFFSET*SizeDisplay.getPhoneDensity(),SizeDisplay.UTILITY_BITMAP_TOP_OFFSET*SizeDisplay.getPhoneDensity(),paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void visit(Player player)
    {
        System.out.println(player.getName()+" is visiting "+this.getCityName());
        this.playerInCity(player);
        if(player.equals(this.getOwner()))
        {
            System.out.println("No Rent");
            PlayActivity.onCompleteListener.OnComplete(player);
        }
        else if(this.getOwner() == null)
        {
            if(player instanceof Computer)
            {
                player.buyProperty(this);
            }
            else
            {
                PlayActivity instance = (PlayActivity)PlayActivity.getInstance();
                instance.showMessage("Does "+player.getName()+" want to Buy "+super.getCityName()+" for $"+this.getPrice(),player,this);
            }
        }
        else
        {
            System.out.println("This city "+super.getCityName()+" is owned by "+this.getOwner().getName());
            if(this.getOnMortgage())
            {
                playActivity.showNotification(this.getOwner(),this.getCityName()+" is on Mortgage.");
                PlayActivity.onCompleteListener.OnComplete(player);
            }
            else
            {
                int rent = player.getLastRoll() * this.multiplier;
                this.setRent(rent);
                player.payRent(this);
            }

        }
    }

    void increaseMultiplier() {
        this.multiplier = 10;
        playActivity.showNotification(this.getOwner(),this.getCityName() +" Multiplier is increased to "+this.multiplier);
    }
}
