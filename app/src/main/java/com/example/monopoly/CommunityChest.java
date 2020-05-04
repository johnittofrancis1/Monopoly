package com.example.monopoly;

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
class CommunityChest extends City implements visitable{
    private static List<CommunityChestCard> cards;
    private static int iter  = 0;
    private static Player visitor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CommunityChest(int position, String cityName) {
        super(position,cityName,0);
        cards = new LinkedList<CommunityChestCard>();

        CommunityChestCard temp = new CommunityChestCard(this,0,"From Sale of Stock.You get $50");
        cards.add(temp);

        temp = new CommunityChestCard(this,1,"Pay your Insurance Premium $50");
        cards.add(temp);

        temp = new CommunityChestCard(this,2,"Bank Error in your Favour.Collect $200");
        cards.add(temp);

        temp = new CommunityChestCard(this,3,"Pay a $10 Fine or Take a Chance Card");
        cards.add(temp);

        temp = new CommunityChestCard(this,4,"Go Back to Old Kent Road");
        cards.add(temp);

        temp = new CommunityChestCard(this,5,"Go to Jail.Move Directly to Jail.\nDo not Pass 'GO'.Do not Collect $200");
        cards.add(temp);

        temp = new CommunityChestCard(this,6,"Recieve Interest on 7% Preference Shares $25");
        cards.add(temp);

        temp = new CommunityChestCard(this,7,"Pay Hospital $100");
        cards.add(temp);

        temp = new CommunityChestCard(this,8,"Income Tax Refund.Collect $20");
        cards.add(temp);

        temp = new CommunityChestCard(this,9,"Advance to 'Go'.Collect $200");
        cards.add(temp);

        temp = new CommunityChestCard(this,10,"It is your Birthday.Collect $10 from Each Player");
        cards.add(temp);

        temp = new CommunityChestCard(this,11,"Annuity Matures.Collect $100");
        cards.add(temp);

        temp = new CommunityChestCard(this,12,"You inherit $100");
        cards.add(temp);

        temp = new CommunityChestCard(this,13,"Doctor's Fee.Pay $50");
        cards.add(temp);

        temp = new CommunityChestCard(this,14,"You have won Second Prize in a Beauty Contest.\nCollect $10");
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
        paint.setColor(Color.parseColor("#8B4513"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(SizeDisplay.CITY_TEXT_SIZE*SizeDisplay.getPhoneDensity());
        canvas.drawText("Community",this.width/2, SizeDisplay.COMMUNITY_TEXT_HEIGHT*SizeDisplay.getPhoneDensity(),paint);
        canvas.drawText("Chest",this.width/2,SizeDisplay.CHEST_TEXT_HEIGHT*SizeDisplay.getPhoneDensity(),paint);

        Resources res = Board.getContext().getResources();
        Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.chest);
        Bitmap treasureBitmap = Bitmap.createScaledBitmap(roughBitmap,(int)(SizeDisplay.CHANCE_BITMAP_WIDTH*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.CHANCE_BITMAP_HEIGHT*SizeDisplay.getPhoneDensity()),true);
        canvas.drawBitmap(treasureBitmap,SizeDisplay.CITY_TEXT_SIZE*SizeDisplay.getPhoneDensity(),(int)(SizeDisplay.CHANCE_BITMAP_WIDTH*SizeDisplay.getPhoneDensity()),paint);

        generatedBitmaps.putIfAbsent(0,this.bitmap);
        this.generateBitmaps();
    }

    public static Player getVisitor() {
        return visitor;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void visit(Player player) {
        this.playerInCity(player);
        visitor = player;
        System.out.println(player.getName()+" is now at "+this.getCityName());
        final CommunityChestCard card = cards.get(iter++);
        PlayActivity instance = (PlayActivity)PlayActivity.getInstance();
        instance.showCard(visitor, "Community Chest", card.getDetails(), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {System.out.println(card);
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
                if(iter == 15)
                    iter = 0;
            }
        });

    }

    public static void printCards()
    {
        for( CommunityChestCard card : cards)
            System.out.println(card);
    }
}

