package com.example.monopoly;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johni
 */
public class Bank{
    private static int money=3000;
    private static PlayActivity playActivity = (PlayActivity)PlayActivity.getInstance();
    public static int getMoney() {
        return money;
    }

    public static void debitMoney(Player player,int amount)
    {
        playActivity.showNotification(player,player.getName()+" pays Bank $"+amount);
        money += amount;
    }

    public static void payPlayer(Player player,int amount)
    {
        playActivity.showNotification(player,"Bank pays "+player.getName()+" $"+amount);
        money -= amount;
        player.getRent(amount);
    }

}

