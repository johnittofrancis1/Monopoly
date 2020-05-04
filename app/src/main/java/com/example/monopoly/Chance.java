package com.example.monopoly;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

/**
 *
 * @author johni
 */
public class Chance extends City implements visitable{
    private static List<ChanceCard> cards;
    private static int iter  = 0;
    private static Player visitor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Chance(int position, String cityName) {
        super(position, cityName, 0);
        cards = new LinkedList<ChanceCard>();

        ChanceCard temp = new ChanceCard(this,0,"Get out of Jail Free Card.\nThis Card may be kept until needed or traded");
        cards.add(temp);

        temp = new ChanceCard(this,1,"Your Building Loan matures.\nRecieve $150");
        cards.add(temp);

        temp = new ChanceCard(this,2,"Speeding Fine $15");
        cards.add(temp);

        temp = new ChanceCard(this,3,"Advance to 'GO'.\nCollect $200");
        cards.add(temp);

        temp = new ChanceCard(this,4,"Go to Jail.Move Directly to Jail.\nDo not Pass 'Go'.Do not Collect $200");
        cards.add(temp);

        temp = new ChanceCard(this,5,"You are assessed for Street Repairs.\n$40 per House. $115 per Hotel");
        cards.add(temp);

        temp = new ChanceCard(this,6,"Advance to MayFair");
        cards.add(temp);

        temp = new ChanceCard(this,7,"Bank pays you Dividend of $50");
        cards.add(temp);

        temp = new ChanceCard(this,8,"Drunk In Charge.Fine $20");
        cards.add(temp);

        temp = new ChanceCard(this,9,"You have won a Cross Word Competition.\nCollect $100");
        cards.add(temp);

        temp = new ChanceCard(this,10,"Pay School Fees of $150");
        cards.add(temp);

        temp = new ChanceCard(this,11,"Advance to Trafalgar Square.\nIf you pass 'Go' Collect $200");
        cards.add(temp);

        temp = new ChanceCard(this,12,"Take a Trip to MaryleBone Station and If you pass 'Go'.\nCollect $200");
        cards.add(temp);

        temp = new ChanceCard(this,13,"Make General Repairs on all of your Buildings for.\nFor each House.Pay $25\nFor each Hotel.Pay $100");
        cards.add(temp);

        temp = new ChanceCard(this,14,"Advance to Pall Mall.\nIf you Pass 'Go'.Collect $200");
        cards.add(temp);

        temp = new ChanceCard(this,15,"Go Back Three Spaces");
        cards.add(temp);

        Collections.shuffle(cards, new Random(100));

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

        //chance type
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLUE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(SizeDisplay.CHANCE_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        canvas.drawText("Chance",this.width/2,(int)(SizeDisplay.CITY_TEXT_SEPARATOR*SizeDisplay.getPhoneDensity()),paint);

        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.question);
        Bitmap chanceBitmap = Bitmap.createScaledBitmap(roughBitmap,(int)(SizeDisplay.CHANCE_BITMAP_WIDTH*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.CHANCE_BITMAP_HEIGHT*SizeDisplay.getPhoneDensity()),true);
        canvas.drawBitmap(chanceBitmap,SizeDisplay.CITY_TEXT_SIZE*SizeDisplay.getPhoneDensity(),(int)(SizeDisplay.CHANCE_BITMAP_WIDTH*SizeDisplay.getPhoneDensity()),paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    public Player getVisitor() {
        if(visitor == null)
        {
            System.out.println("No visitor");
        }
        return visitor;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void visit(Player player){
        this.playerInCity(player);
        visitor = player;
        System.out.println(player.getName()+" is now at "+this.getCityName());
        this.takeCard(visitor);
    }

    public void takeCard(Player player) {
        visitor = player;
        final ChanceCard card = cards.get(iter++);
        PlayActivity instance = (PlayActivity)PlayActivity.getInstance();
        instance.showCard(visitor,"Chance",card.getDetails(),new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                System.out.println(card);
                try {
                    card.callFunction();
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(Chance.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Chance.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Chance.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Chance.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(iter == 16)
                    iter = 0;
            }
        });
    }

}
