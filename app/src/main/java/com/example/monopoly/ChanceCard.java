package com.example.monopoly;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.RequiresApi;

/**
 *
 * @author johni
 */
public class ChanceCard{
    private int id;
    private String details;
    private Player visitor;
    private Chance chance;
    private PlayActivity playActivity;

    public ChanceCard(Chance chance,int id, String details) {
        this.chance = chance;
        this.id = id;
        this.details = details;
        this.playActivity = (PlayActivity)PlayActivity.getInstance();
    }

    public void callFunction() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        visitor = this.chance.getVisitor();
        String name = String.valueOf(this.id) ;
        Method method = ChanceCard.class.getMethod("Chance"+name);
        method.invoke(this);
    }

    public String getDetails() {
        return details;
    }

    public void Chance0()
    {
        PlayActivity.onCompleteListener.OnComplete(visitor);
        visitor.addJailFreeCards();

    }
    public void Chance1()
    {
        if(visitor.getTotalHouses() > 0)
        {
            Bank.payPlayer(visitor,150);
        }
        else
        {
            playActivity.showNotification(visitor,visitor.getName()+" has got no houses");
        }
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance2()
    {
        visitor.payBank(15);
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance3()
    {
        Start start = (Start)Board.getCities().get(0);
        visitor.movedtoCity(start);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Chance4()
    {
        visitor.jailed();
    }

    public void Chance5()
    {
        if(visitor!=null)
        {
            int houses = visitor.getTotalHouses();
            int hotels = visitor.getTotalHotels();
            int amount = 0;
            if(houses != 0)
            {
                amount += houses*40;
                playActivity.showNotification(visitor,visitor.getName()+" has got "+houses+" Hotels");
                visitor.payBank(houses*40);
                if(hotels != 0)
                {
                    amount += hotels*115;
                    System.out.println(visitor.getName()+" has got "+hotels+" Hotels\nPaying $"+hotels*115);
                    visitor.payBank(hotels*115);
                }
                else
                {
                    playActivity.showNotification(visitor,visitor.getName()+" has got no Hotels");
                }

                playActivity.showNotification(visitor,"Paying $"+amount);
            }
            else
            {
                playActivity.showNotification(visitor,visitor.getName()+" has got no Houses");
            }
        }
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance6()
    {
        Property mayFair = (Property)Board.getCities().get(39);
        visitor.movedtoCity(mayFair);
    }

    public void Chance7()
    {
        Bank.payPlayer(visitor, 50);
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance8()
    {
        visitor.payBank(20);
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance9()
    {
        Bank.payPlayer(visitor, 100);
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance10()
    {
        visitor.payBank(150);
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance11()
    {
        int currentPosition = visitor.getPosition();
        Property trafalgar = (Property) Board.getCities().get(24);
        if(currentPosition > trafalgar.getPosition())
        {
            Start start = (Start)Board.getCities().get(0);
            start.crossed(visitor);
        }
        visitor.movedtoCity(trafalgar);
    }

    public void Chance12()
    {
        int currentPosition = visitor.getPosition();
        Property marylebone = (Property) Board.getCities().get(15);
        if(currentPosition > marylebone.getPosition())
        {
            Start start = (Start)Board.getCities().get(0);
            start.crossed(visitor);
        }
        visitor.movedtoCity(marylebone);
    }

    public void Chance13()
    {
        int houses = visitor.getTotalHouses();
        int hotels = visitor.getTotalHotels();
        int amount =0;
        if(houses != 0)
        {
            playActivity.showNotification(visitor,visitor.getName()+" has got "+houses+" Hotels");
            visitor.payBank(houses*25);
            amount += houses*25;
            if(hotels != 0)
            {
                amount += hotels*100;
                playActivity.showNotification(visitor,visitor.getName()+" has got "+hotels+" Hotels");
                visitor.payBank(hotels*100);
            }
            else
            {
                playActivity.showNotification(visitor,visitor.getName()+" has got no Hotels");
            }
            playActivity.showNotification(visitor,"Paying Bank $"+amount);
        }
        else
        {
            playActivity.showNotification(visitor,visitor.getName()+" has got no Houses");
        }
        PlayActivity.onCompleteListener.OnComplete(visitor);
    }

    public void Chance14()
    {
        int currentPosition = visitor.getPosition();
        Property pallmall = (Property) Board.getCities().get(11);
        if(currentPosition > pallmall.getPosition())
        {
            Start start = (Start)Board.getCities().get(0);
            start.crossed(visitor);
        }
        visitor.movedtoCity(pallmall);
    }

    public void Chance15()
    {
        int currentPosition = visitor.getPosition();
        int targetPosition = currentPosition - 3;
        if(targetPosition < 0)
        {
            targetPosition = 40 + targetPosition;
        }
        City city = Board.getCities().get(targetPosition);
        visitor.movedtoCity(city);
    }

    @Override
    public String toString() {
        return "ChanceCard " + "id= " + id + ", details = " + details;
    }


}
