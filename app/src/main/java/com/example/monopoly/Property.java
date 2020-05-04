package com.example.monopoly;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import androidx.annotation.RequiresApi;

/**
 *
 * @author johni
 */
public class Property extends City implements visitable,buyable,buildable,mortgagable{
    private int price;
    private int rent;
    private int mortgage;
    private Boolean bought,onMortgage;
    private Player owner;
    private Boolean housable;
    private int houses;
    private int hotels;
    private int primaryRent;
    private int housePrice;
    private int house1Rent,house2Rent,house3Rent,hotelRent;
    private PlayActivity playActivity;
    private int interest;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Property(int position, String cityName, int price, int rent, int mortgage,int house1Rent,int house2Rent,int house3Rent,int hotelRent,int colorId) {
        super(position, cityName, colorId);
        this.price = price;
        this.rent = rent;
        this.primaryRent = rent;
        this.mortgage = mortgage;
        this.owner = null;
        this.bought = false;
        this.onMortgage = false;
        this.housable = false;
        this.houses = 0;
        this.hotels = 0;
        this.house1Rent = house1Rent;
        this.house2Rent = house2Rent;
        this.house3Rent = house3Rent;
        this.hotelRent = hotelRent;
        this.interest = this.mortgage / 10;
        this.playActivity = (PlayActivity)PlayActivity.getInstance();

        if(this.getPosition() < 10 )
            this.housePrice = 50;
        else if(this.getPosition() < 20)
            this.housePrice = 100;
        else if(this.getPosition() < 30)
            this.housePrice = 150;
        else if(this.getPosition() < 40)
            this.housePrice = 200;
    }

    public int getInterest() {
        return interest;
    }

    public Boolean getHousable() {
        return housable;
    }

    public int getHouses() {
        return houses;
    }

    public int getHotels() {
        return hotels;
    }

    public int getPrice() {
        return price;
    }

    public int getRent() {
        return rent;
    }

    public int getMortgage() {
        return mortgage;
    }

    public Boolean getBought() {
        return bought;
    }

    public Boolean getOnMortgage() {
        return onMortgage;
    }

    public Player getOwner() {
        return owner;
    }

    protected void setRent(int rent)
    {
        this.rent = rent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void visit(final Player player)
    {
        this.playerInCity(player);
        System.out.println(player.getName()+" is visiting "+super.getCityName());
        if(player.equals(owner))
        {
            System.out.println("No Rent");
            if(this.housable)
            {
                this.findPropertytobuildHouse(player);
            }
            else
                PlayActivity.onCompleteListener.OnComplete(player);
        }
        else if(owner == null)
        {
            if(player instanceof Computer)
            {
                player.buyProperty(this);
            }
            else
                playActivity.showMessage("Does "+player.getName()+" want to Buy "+Property.super.getCityName()+" for $"+Property.this.price,player,Property.this);
        }
        else
        {
            System.out.println("This city "+super.getCityName()+" is owned by "+this.owner.getName());
            if(this.onMortgage)
            {
                playActivity.showNotification(this.getOwner(),this.getCityName()+" is on Mortgage.");
                PlayActivity.onCompleteListener.OnComplete(player);
            }
            else
            {
                player.payRent(this);
            }
        }
    }

    @Override
    final public Boolean buy(Player player) {
        if(!this.bought)
        {
            this.owner = player;
            this.bought = true;
            return this.bought;
        }
        else
        {
            System.out.println("This city is already owned by "+this.owner.toString());
            return this.bought;
        }
    }

    @Override
    public void buildable() {
        this.housable = true;
        System.out.println(this.getCityName()+" is buildable");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void findPropertytobuildHouse(final Player player) {
        HashMap<Property,Integer> buildables = player.Build(this);
        System.out.println("Choose");
        LinkedList<Property> list = new LinkedList<Property>();
        for(Map.Entry<Property,Integer> entry : buildables.entrySet())
        {
            System.out.println(entry.getValue()+entry.getKey().getCityName()+"Houses : "+entry.getKey().getHouses());
            list.add(entry.getKey());
        }
        playActivity.showProperties("Choose to Build",list,player, new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Property selectedProperty = playActivity.selectedProperty;
                selectedProperty.buildHouses(player);
                PlayActivity.onCompleteListener.OnComplete(player);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Boolean buildHouses(Player player)
    {
        if(this.houses < 3)
        {
            switch (++this.houses)
            {
                case 1:
                    this.rent = house1Rent;
                    break;
                case 2:
                    this.rent = house2Rent;
                    break;
                case 3:
                    this.rent = house3Rent;
                    break;
            }
            player.houseBuilt(this.housePrice);
            player.payBank(this.housePrice);
            playActivity.showNotification(this.owner,"House is Built");
            PlayActivity.updateBoard();
            playActivity.showNotification(this.owner,this.getCityName()+" Rent is increased to "+this.rent);
            return true;
        }
        else
        {
            if(this.hotels == 0)
            {
                this.hotels = 1;
                this.rent = this.hotelRent;
                player.hotelBuilt(this.housePrice);
                player.payBank(this.housePrice);
                playActivity.showNotification(this.owner,this.getCityName()+" Rent is increased to "+this.rent);
                return true;

            }
            else
            {
                playActivity.showNotification(this.owner,"No more hotels");
                return false;
            }
        }
    }

    @Override
    public void mortgage(Player player) {
        if(!this.onMortgage)
        {
            this.onMortgage = true;
            Bank.payPlayer(player, this.mortgage);
        }
    }

    public Boolean releaseMortgage(Player player) {
        if(this.onMortgage)
        {
            if(player.getMoney() >= (this.mortgage+interest))
            {
                this.onMortgage = false;
                player.payBank(this.mortgage+interest);
                return true;
            }
            else{

                this.onMortgage = true;
                playActivity.showNotification(this.owner,player.getName()+" does not have enough money");
            }
        }
        return false;
    }
    public void doubleRent()
    {
        this.rent = this.rent * 2;
        playActivity.showNotification(this.owner,this.getCityName()+" Rent is doubled to "+this.rent);
    }

    @Override
    public String toString() {
        return "Property"+ "Position=" + super.getPosition() + " CityName=" + super.getCityName()+ " Price=" + price + " Rent=" + rent + " Mortgage=" + mortgage + ", Color=" + super.getColorId();
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
        if(this.houses == 0)
        {
            return this.rotateBitmap(this.generatedBitmaps.getOrDefault(key,this.bitmap));
        }
        else
        {
            Bitmap bitmap = this.generatedBitmaps.getOrDefault(key,this.bitmap);
            Canvas canvas = new Canvas(bitmap);
            Resources res = Board.getContext().getResources();
            Bitmap roughBitmap = BitmapFactory.decodeResource(res,R.drawable.house);
            Bitmap house = Bitmap.createScaledBitmap(roughBitmap,18,18,true);
            roughBitmap = BitmapFactory.decodeResource(res,R.drawable.hotel);
            Bitmap hotel = Bitmap.createScaledBitmap(roughBitmap,18,18,true);

            if(this.houses == 1)
            {
                canvas.drawBitmap(house,2,6,null);
            }
            else if(this.houses == 2)
            {
                canvas.drawBitmap(house,2,6,null);
                canvas.drawBitmap(house,22,6,null);
            }
            else if(this.houses == 3)
            {
                canvas.drawBitmap(house,2,6,null);
                canvas.drawBitmap(house,22,6,null);
                canvas.drawBitmap(house,42,6,null);
                if(this.hotels == 1)
                {
                    canvas.drawBitmap(hotel,62,6,null);
                }
            }
            return this.rotateBitmap(bitmap);
        }

    }

    public int getPrimaryRent() {
        return primaryRent;
    }

    public int getHousePrice() {
        return housePrice;
    }

    public int getHouse1Rent() {
        return house1Rent;
    }

    public int getHouse2Rent() {
        return house2Rent;
    }

    public int getHouse3Rent() {
        return house3Rent;
    }

    public int getHotelRent() {
        return hotelRent;
    }
}
