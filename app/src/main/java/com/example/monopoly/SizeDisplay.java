package com.example.monopoly;

import android.util.Log;

public class SizeDisplay {
    public static float phoneDensity;
    public static int height,width;
    public final static float MONOPOLY_MAN_LOGO_HEIGHT = 75;
    public final static float MONOPOLY_MAN_LOGO_WIDTH = 150;
    public final static float PLAY_BUTTON_HEIGHT = 150;
    public final static float PLAY_BUTTON_WIDTH = 150;
    public final static float PLAYER_BOX_SIZE = 75;
    public final static float COIN_SIZE = 25;
    public static final float CITY_WIDTH = 27;
    public static final float CITY_HEIGHT = 50;
    public static final float CITY_COLOR_SIZE = 10;
    public static final float CITY_SQUARE_SIZE = 50;
    public static final float PLAYER_PIECE = 12;
    public static final float PLAYER_1_PIECE_LEFT_SQR = 10;
    public static final float PLAYER_1_PIECE_TOP_SQR = 28;
    public static final float PLAYER_2_PIECE_LEFT_SQR = 28;
    public static final float PLAYER_2_PIECE_TOP_SQR = 10;
    public static final float PLAYER_3_PIECE_LEFT_SQR = 10;
    public static final float PLAYER_3_PIECE_TOP_SQR = 10;
    public static final float PLAYER_4_PIECE_LEFT_SQR = 28;
    public static final float PLAYER_4_PIECE_TOP_SQR = 28;
    public static final float PLAYER_1_PIECE_LEFT = 1;
    public static final float PLAYER_1_PIECE_TOP = 31;
    public static final float PLAYER_2_PIECE_LEFT = 15;
    public static final float PLAYER_2_PIECE_TOP = 17;
    public static final float PLAYER_3_PIECE_LEFT = 1;
    public static final float PLAYER_3_PIECE_TOP = 17;
    public static final float PLAYER_4_PIECE_LEFT = 15;
    public static final float PLAYER_4_PIECE_TOP = 31;
    public static final float CITY_TEXT_SIZE = 5;
    public static final float CITY_TEXT_SEPARATOR = 10;
    public static final float CHANCE_TEXT_SIZE = 7;
    public static final float CHANCE_BITMAP_WIDTH = 17;
    public static final float CHANCE_BITMAP_HEIGHT = 25;
    public static final float COMMUNITY_TEXT_HEIGHT = 5;
    public static final float CHEST_TEXT_HEIGHT = 10;
    public static final float JAIL_TEXT_SIZE = 12;
    public static final float PARKING_IMG_OFFSET = 4;
    public static final float PARKING_TEXT_SIZE = 7;
    public static final float PARKING_TEXT_OFFSET = 7;
    public static final float JAIL_IMG_TOP_OFFSET = 12;
    public static final float INCOME_IMG_LEFT_OFFSET = 5;
    public static final float INCOME_IMG_TOP_OFFSET = 12;
    public static final float INCOME_TEXT_HEIGHT = 5;
    public static final float TAX_TEXT_HEIGHT = 10;
    public static final float START_TEXT_SIZE = 12;
    public static final float STATION_TEXT_SIZE = 4;
    public static final float STATION_TEXT_TOP_OFFSET = 8;
    public static final float STATION_TEXT_SEPARATOR = 5;
    public static final float STATION_BITMAP_SIZE = 20;
    public static final float STATION_BITMAP_LEFT_OFFSET = 3;
    public static final float STATION_BITMAP_TOP_OFFSET = 18;
    public static final float UTILITY_TEXT_SIZE = 4;
    public static final float UTILITY_TEXT_TOP_OFFSET = 8;
    public static final float UTILITY_TEXT_SEPARATOR = 5;
    public static final float UTILITY_BITMAP_SIZE = 20;
    public static final float UTILITY_BITMAP_LEFT_OFFSET = 3;
    public static final float UTILITY_BITMAP_TOP_OFFSET = 18;
    public static final float DICE_SIZE = 167;
    public static final float DICE_1_LEFT_OFFSET = 67;
    public static final float DICE_2_LEFT_OFFSET = 278;
    public static final float DICE_TOP_OFFSET = 172;



    public static void setPhoneDensity(int height,int width) {
        SizeDisplay.height = height;
        SizeDisplay.width = width;
        int size = SizeDisplay.height < SizeDisplay.width ? SizeDisplay.height : SizeDisplay.width;
        SizeDisplay.phoneDensity = (float)(size) / (float)343;
        Log.e("Density", String.valueOf(SizeDisplay.phoneDensity));
    }

    public static float getBoardSize()
    {
        float size = 9*(CITY_WIDTH * SizeDisplay.phoneDensity)+2*(CITY_SQUARE_SIZE*SizeDisplay.phoneDensity);
        return size;
    }
    public static float getCityWidth()
    {
        float width = CITY_WIDTH*SizeDisplay.phoneDensity;
        return width;
    }
    public static float getCityHeight()
    {
        float height = CITY_HEIGHT*SizeDisplay.phoneDensity;
        return height;
    }
    public static float getSqrCitySize()
    {
        float size = CITY_SQUARE_SIZE*SizeDisplay.phoneDensity;
        return size;
    }
    public static float getPhoneDensity() {
        return (phoneDensity*3);
    }
}
