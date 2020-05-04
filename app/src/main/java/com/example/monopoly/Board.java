package com.example.monopoly;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

/**
 *
 * @author johni
 */
public class Board  {
    private static PlayActivity playActivity;
    private static Map<Integer,City> cities;
    private static Map<Integer,Player> players;
    private static int turn;
    private Scanner scan;
    private static Jail jail;
    private Start start;
    private static CityGroup citygroup;
    private static Context context;
    private static Map<Integer, Bitmap> pieces;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Board(Context context) {
        Board.context = context;
        scan = new Scanner(System.in);
        cities = new HashMap<Integer,City>();
        players = new HashMap<Integer,Player>();
        turn = 1;
        citygroup = new CityGroup();


        playActivity = (PlayActivity)PlayActivity.getInstance();

        pieces = new HashMap<Integer,Bitmap>();
        Resources resources = Board.getContext().getResources();

        Bitmap bitmap = BitmapFactory.decodeResource(resources,R.drawable.bluepiece);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),true);
        pieces.put(1,scaledBitmap);

        bitmap = BitmapFactory.decodeResource(resources,R.drawable.redpiece);
        scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),true);
        pieces.put(2,scaledBitmap);

        bitmap = BitmapFactory.decodeResource(resources,R.drawable.yellowpiece);
        scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),true);
        pieces.put(3,scaledBitmap);

        bitmap = BitmapFactory.decodeResource(resources,R.drawable.greenpiece);
        scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),(int)(SizeDisplay.PLAYER_PIECE*SizeDisplay.getPhoneDensity()),true);
        pieces.put(4,scaledBitmap);

        InputStream fileStream = playActivity.getResources().openRawResource(R.raw.cities);
        try(BufferedReader file = new BufferedReader(new InputStreamReader(fileStream)))
        {
            String line = null;
            while((line = file.readLine()) != null)
            {
                String[] details = line.split(",");
                String className = details[0];
                int position = Integer.parseInt(details[1]);
                String cityName = details[2];
                int colorId = Integer.parseInt(details[3]);
                switch(className)
                {
                    case "monopoly.Start":
                    {
                        start = new Start(cityName);
                        cities.put(start.getPosition(),start);
                        break;
                    }
                    case "monopoly.Property":
                    {
                        int price = Integer.parseInt(details[4]);
                        int rent = Integer.parseInt(details[5]);
                        int mortgage = Integer.parseInt(details[6]);
                        int house1Rent = Integer.parseInt(details[7]);
                        int house2Rent = Integer.parseInt(details[8]);
                        int house3Rent = Integer.parseInt(details[9]);
                        int hotelRent = Integer.parseInt(details[10]);
                        Property property = new Property(position,cityName,price,rent,mortgage,house1Rent,house2Rent,house3Rent,hotelRent,colorId);
                        cities.put(property.getPosition(),property);
                        break;
                    }
                    case "monopoly.CommunityChest":
                    {
                        cities.put(position,new CommunityChest(position,cityName));
                        break;
                    }
                    case "monopoly.Chance":
                    {
                        cities.put(position,new Chance(position,cityName));
                        break;
                    }
                    case "monopoly.Others":
                    {
                        int rent = Integer.parseInt(details[4]);
                        cities.put(position,new Others(position,cityName,rent));
                        break;
                    }
                    case "monopoly.Station":
                    {
                        cities.put(position,new Station(position,cityName));
                        break;
                    }
                    case "monopoly.Utilities":
                    {
                        cities.put(position,new Utilities(position,cityName));
                        break;
                    }
                    case "monopoly.Jail":
                    {
                        cities.put(position,new Jail());
                        break;
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Monopoly";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdir();
        Log.e("XML",path);
        try
        {
            File xmlFile = new File(path,"cities.xml");
            xmlFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(xmlFile);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fileOutputStream,"UTF-8");
            serializer.startDocument(null,true);
            serializer.startTag(null,"Cities");
            for(City city : cities.values())
            {
                serializer.startTag(null,"City");
                serializer.attribute(null,"Position",String.valueOf(city.getPosition()));
                serializer.startTag(null,"Name");
                serializer.text(city.getCityName());
                serializer.endTag(null,"Name");
                serializer.startTag(null,"Color");
                serializer.text(String.valueOf(city.getColorId()));
                serializer.endTag(null,"Color");
                serializer.endTag(null,"City");
            }
            serializer.endTag(null,"Cities");
            serializer.endDocument();
            serializer.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File file = new File(path,"cities.xml");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        playActivity.startActivity(intent);*/
        /*start = new Start("Start");
        cities.put(start.getPosition(), start);

        City temp = new Property(1,"OldKent Road",60,2,30,10,30,90,250, ContextCompat.getColor(context,R.color.brown));
        cities.put(temp.getPosition(), temp);

        temp = new CommunityChest(2,"Community Chest1");
        cities.put(temp.getPosition(), temp);

        temp = new Property(3,"WhiteChapel Road",60,4,30,20,60,180,320,ContextCompat.getColor(context,R.color.brown));
        cities.put(temp.getPosition(), temp);

        temp = new Others(4,"Income Tax",200);
        cities.put(temp.getPosition(), temp);

        temp = new Station(5,"KingsCross Station");
        cities.put(temp.getPosition(), temp);

        temp = new Property(6,"TheAngel Islington",100,6,50,30,90,270,550, Color.CYAN);
        cities.put(temp.getPosition(), temp);

        temp = new Chance(7,"Chance 1");
        cities.put(temp.getPosition(), temp);

        temp = new Property(8,"Euston Road",100,6,50,30,90,270,550, Color.CYAN);
        cities.put(temp.getPosition(), temp);

        temp = new Property(9,"PentonVille Road",120,8,60,40,100,300,600, Color.CYAN);
        cities.put(temp.getPosition(), temp);

        jail = new Jail();
        cities.put(jail.getPosition(), jail);

        temp = new Property(11,"Pall Mall",140,10,70,50,150,450,750, Color.MAGENTA);
        cities.put(temp.getPosition(), temp);

        temp = new Utilities(12,"Electric Company");
        cities.put(temp.getPosition(), temp);

        temp = new Property(13,"White Hall",140,10,70,50,150,450,750, Color.MAGENTA);
        cities.put(temp.getPosition(), temp);

        temp = new Property(14,"Northumberl'd Avenue",160,12,80,60,180,500,900, Color.MAGENTA);
        cities.put(temp.getPosition(), temp);

        temp = new Station(15,"MaryleBone Station");
        cities.put(temp.getPosition(), temp);

        temp = new Property(16,"Bow Street",180,14,90,70,200,550,950,ContextCompat.getColor(context,R.color.lightorange));
        cities.put(temp.getPosition(), temp);

        temp = new CommunityChest(17,"Community Chest2");
        cities.put(temp.getPosition(), temp);

        temp = new Property(18,"Marlborough Street",180,14,90,70,200,550,950,ContextCompat.getColor(context,R.color.lightorange));
        cities.put(temp.getPosition(), temp);

        temp = new Property(19,"Vine Street",200,16,100,80,220,600,1000,ContextCompat.getColor(context,R.color.lightorange));
        cities.put(temp.getPosition(), temp);

        temp = new Others(20,"Free Parking",0);
        cities.put(temp.getPosition(), temp);

        temp = new Property(21,"Strand",220,18,110,90,250,700,1050,Color.RED);
        cities.put(temp.getPosition(), temp);

        temp = new Chance(22,"Chance 2");
        cities.put(temp.getPosition(), temp);

        temp = new Property(23,"Fleet Street",220,18,110,90,250,700,1050,Color.RED);
        cities.put(temp.getPosition(), temp);

        temp = new Property(24,"Trafalgar Square",240,20,120,100,300,750,1100,Color.RED);
        cities.put(temp.getPosition(), temp);

        temp = new Station(25,"FrenchurchSt. Station");
        cities.put(temp.getPosition(), temp);

        temp = new Property(26,"Leicester Square",260,22,130,110,330,800,1150,Color.YELLOW);
        cities.put(temp.getPosition(), temp);

        temp = new Property(27,"Conventry Street",260,22,130,110,330,800,1150,Color.YELLOW);
        cities.put(temp.getPosition(), temp);

        temp = new Utilities(28,"Water Works");
        cities.put(temp.getPosition(), temp);

        temp = new Property(29,"Piccadilly",280,24,140,120,360,850,1200,Color.YELLOW);
        cities.put(temp.getPosition(), temp);

        temp = new Others(30,"Go to Jail",0);
        cities.put(temp.getPosition(), temp);

        temp = new Property(31,"Regent Street",300,26,150,130,390,900,1275,ContextCompat.getColor(context,R.color.green));
        cities.put(temp.getPosition(), temp);

        temp = new Property(32,"Oxford Street",300,26,150,130,390,900,1275,ContextCompat.getColor(context,R.color.green));
        cities.put(temp.getPosition(), temp);

        temp = new CommunityChest(33,"Community Chest 3");
        cities.put(temp.getPosition(), temp);

        temp = new Property(34,"Bond Street",320,28,160,150,450,1000,1400,ContextCompat.getColor(context,R.color.green));
        cities.put(temp.getPosition(), temp);

        temp = new Station(35,"LiverpoolSt. Station");
        cities.put(temp.getPosition(), temp);

        temp = new Chance(36,"Chance 3");
        cities.put(temp.getPosition(), temp);

        temp = new Property(37,"Park Lane",350,35,175,175,500,1100,1500,Color.BLUE);
        cities.put(temp.getPosition(), temp);

        temp = new Others(38,"Super Tax",100);
        cities.put(temp.getPosition(), temp);

        temp = new Property(39,"Mayfair",400,50,200,200,600,1400,2000,Color.BLUE);
        cities.put(temp.getPosition(), temp);*/

        citygroup.grouping(cities);

    }

    public static Map<Integer, City> getCities() {
        return Collections.unmodifiableMap(cities);
    }

    public static Map<Integer,Player> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start() {
        for(Map.Entry<Integer,Player> entry : players.entrySet())
        {
            entry.getValue().atStart();
        }
        playerToPlay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void playerToPlay()
    {
            final Player currentPlayer = players.get(turn);
            currentPlayer.setDiceToken(true);
            playActivity.showPlayers(currentPlayer);
            turn ++;
            if(turn == players.size()+1)
            {
                turn = 1;
            }
            if(currentPlayer instanceof Computer)
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playActivity.computerPlay((Computer) currentPlayer);
                    }
                },2000);
            }
    }

    public void addPlayers(HashMap<Integer,String> names)
    {
        for(Map.Entry<Integer,String > entry : names.entrySet())
        {
            players.put(entry.getKey(),new Player(entry.getKey(),entry.getValue()));
        }
        players.get(1).setDiceToken(true);
    }

    public static Map<Integer, Bitmap> getPieces() {
        return Collections.unmodifiableMap(pieces);
    }

    public static Context getContext()
    {
        return context;
    }

    public void finishGame() {
        LinkedList<Player> orderWinning = new LinkedList<Player>();

        for(Map.Entry<Integer,Player> entry : players.entrySet())
        {
            orderWinning.add(entry.getValue());
        }

        Collections.sort(orderWinning, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return player2.getTotalValue() - player1.getTotalValue();
            }
        });
        final Dialog dialog = new Dialog(playActivity);
        dialog.setContentView(R.layout.playerlist);

        ListView listView = dialog.findViewById(R.id.listview);
        PlayerAdapter playerAdapter = new PlayerAdapter(context,orderWinning);
        listView.setAdapter(playerAdapter);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                playActivity.finish();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }

    public void computerMode() {

        players.put(1,new Player(1,"You"));
        players.put(2,new Computer());

    }
}

