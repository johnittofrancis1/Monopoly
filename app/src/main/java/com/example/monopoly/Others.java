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
class Others extends City implements visitable{
    private int rent;
    private PlayActivity playActivity;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Others(int position, String cityName, int rent) {
        super(position,cityName,0);
        this.rent = rent;
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

        //border the square
        Rect rect = new Rect(0,0,this.width,this.height);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(SizeDisplay.getPhoneDensity());
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect,paint);

        paint.setStrokeWidth(1);
        switch (this.getCityName()) {
            case "Free Parking": {
                //freeparking background
                Resources res = Board.getContext().getResources();
                Bitmap roughBitmap = BitmapFactory.decodeResource(res, R.drawable.freeparking);
                Bitmap freeParkingBitmap = Bitmap.createScaledBitmap(roughBitmap, (this.width * 5) / 6, (this.height * 5) / 6, true);
                canvas.drawBitmap(freeParkingBitmap, SizeDisplay.PARKING_IMG_OFFSET * SizeDisplay.getPhoneDensity(), SizeDisplay.PARKING_IMG_OFFSET * SizeDisplay.getPhoneDensity(), paint);

                //type the cityName
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(SizeDisplay.PARKING_TEXT_SIZE * SizeDisplay.getPhoneDensity());
                canvas.drawText("Free Parking", this.width / 2, SizeDisplay.PARKING_TEXT_OFFSET * SizeDisplay.getPhoneDensity(), paint);
                break;
            }
            case "Go to Jail": {
                //policeman background
                Resources res = Board.getContext().getResources();
                Bitmap roughBitmap = BitmapFactory.decodeResource(res, R.drawable.police);
                Bitmap policemanBitmap = Bitmap.createScaledBitmap(roughBitmap, (this.width * 2) / 3, (this.height * 2) / 3, true);
                canvas.drawBitmap(policemanBitmap, this.width / 6, SizeDisplay.JAIL_IMG_TOP_OFFSET * SizeDisplay.getPhoneDensity(), paint);

                //type the cityName
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(SizeDisplay.JAIL_TEXT_SIZE * SizeDisplay.getPhoneDensity());
                canvas.drawText("Go to Jail", this.width / 2, SizeDisplay.TAX_TEXT_HEIGHT * SizeDisplay.getPhoneDensity(), paint);
                break;
            }
            case "Income Tax":
            case "Super Tax": {
                //tax bitmap
                Resources res = Board.getContext().getResources();
                Bitmap roughBitmap = BitmapFactory.decodeResource(res, R.drawable.tax);
                Bitmap policemanBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (this.width / 1.5), this.height / 2, true);
                canvas.drawBitmap(policemanBitmap, SizeDisplay.INCOME_IMG_LEFT_OFFSET * SizeDisplay.getPhoneDensity(), SizeDisplay.INCOME_IMG_TOP_OFFSET * SizeDisplay.getPhoneDensity(), paint);

                if (this.getCityName().equals("Income Tax")) {
                    //type the cityName
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(SizeDisplay.CITY_TEXT_SIZE * SizeDisplay.getPhoneDensity());
                    canvas.drawText("Income", this.width / 2, SizeDisplay.INCOME_TEXT_HEIGHT * SizeDisplay.getPhoneDensity(), paint);
                    canvas.drawText("Tax", this.width / 2, SizeDisplay.TAX_TEXT_HEIGHT * SizeDisplay.getPhoneDensity(), paint);
                } else if (this.getCityName().equals("Super Tax")) {
                    //type the cityName
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(SizeDisplay.CITY_TEXT_SIZE * SizeDisplay.getPhoneDensity());
                    canvas.drawText("Super", this.width / 2, SizeDisplay.INCOME_TEXT_HEIGHT * SizeDisplay.getPhoneDensity(), paint);
                    canvas.drawText("Tax", this.width / 2, SizeDisplay.TAX_TEXT_HEIGHT * SizeDisplay.getPhoneDensity(), paint);
                }
                break;
            }
        }

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    public int getRent() {
        return rent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void visit(Player player) {
        this.playerInCity(player);
        System.out.println(player.getName()+" is now at "+this.getCityName());
        switch (this.getCityName()) {
            case "Go to Jail":
                player.jailed();
                break;
            case "Income Tax":
                playActivity.showNotification(player, player.getName() + " is taxed $200");
                player.taxed(this);
                break;
            case "Super Tax":
                playActivity.showNotification(player, player.getName() + " is taxed $100");
                player.taxed(this);
                break;
            default:
                PlayActivity.onCompleteListener.OnComplete(player);
                break;
        }
    }

}
