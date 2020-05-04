package com.example.monopoly;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 *
 * @author johni
 */
class Player implements playable,rentHighable,jailable,build,mortgages{
    private int id;
    protected int position;
    protected String name;
    protected int money;
    protected List<Property> ownedProperties;
    protected List<Property> mortgagedProperties;
    protected boolean jailed;
    protected int lastRoll;
    protected City lastCity;
    protected Map<Integer,Integer> ownColor;
    protected int ownedStations;
    protected int ownedUtilities;
    protected int turnsinJail = 0;
    protected int jailFreeCards;
    protected int totalHouses;
    protected int totalHotels;
    protected int totalBuildingValue;
    protected Bitmap playerPiece;
    protected Boolean diceToken;
    protected Map<Integer,City> cities;
    protected int count = 0;
    protected int choice = 1;
    protected int flag = 0;
    protected OnStuckListener onStuckListener;
    protected Player.notifier notifier;
    protected Player.notifier moneyNotifier;
    protected Player playerListener;
    protected PlayActivity playActivity;
    protected Player.OnCompleteListener OnCompleteListener;
    protected Comparator<Property> colorComparator;

    public Player(int id, String name) {
        this.id = id;
        this.position = 0;
        this.name = name;
        this.money = 1500;
        this.ownedProperties = new LinkedList<Property>();
        this.mortgagedProperties = new LinkedList<Property>();
        this.jailed = false;
        this.ownColor = new HashMap<>(CityGroup.getListColor());
        this.ownedStations = 0;
        this.ownedUtilities = 0;
        this.jailFreeCards = 0;
        this.totalHouses = 0;
        this.totalHotels = 0;
        this.totalBuildingValue = 0;
        this.diceToken = false;
        this.cities = new HashMap<Integer, City>(Board.getCities());

        this.playerPiece = Board.getPieces().get(this.id);

        this.moneyNotifier = PlayActivity.getMoneyNotifier();

        this.playActivity = (PlayActivity)PlayActivity.getInstance();

        this.colorComparator = new Comparator<Property>() {
            @Override
            public int compare(Property property1, Property property2) {
                return property1.getColorId() - property2.getColorId();
            }
        };
    }

    public int getPosition() {
        return position;
    }

    public int getTotalBuildingValue() {
        return totalBuildingValue;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public int getId() {
        return id;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public int getJailFreeCards() {
        return jailFreeCards;
    }

    public int getTotalHouses() {
        return totalHouses;
    }

    public int getNumMortgagedProperties(){
        return mortgagedProperties.size();
    }

    public int getNumOwnedProperties(){ return this.ownedProperties.size(); }

    public int getTotalHotels() {
        return totalHotels;
    }

    public Boolean getDiceToken() {
        return diceToken;
    }

    public void setDiceToken(Boolean diceToken) {
        this.diceToken = diceToken;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected void addMoney(int amount)
    {
        this.money += amount;
        this.moneyNotifier.notified(null);
    }

    protected void reduceMoney(int amount)
    {
        this.money -= amount;
        this.moneyNotifier.notified(null);
    }

    public int getTotalPropertyValue() {
        int totalPropertyValue =  0;
        for(Property property : this.ownedProperties)
            totalPropertyValue += property.getPrice();
        return totalPropertyValue;
    }

    public int getMortgagedValue() {
        int mortgagedValue = 0;
        for(Property property : this.mortgagedProperties)
            mortgagedValue += property.getMortgage() + (property.getMortgage()/10);
        return mortgagedValue;
    }

    @Override
    public void move(int dice1,int dice2,OnStuckListener listener,OnCompleteListener onCompleteListener) {
        this.setOnStuckListener(listener,this);
        this.setOnCompleteListener(onCompleteListener);
        lastRoll = dice1+dice2;
        this.count++;
        int flag;

        if(dice1 == dice2)
        {
            this.turnsinJail = 0;
            if(this.count == 3)
            {
                this.count = 0;
                this.jailed();
                if(this.onStuckListener != null)
                {
                    this.onStuckListener.OnStuck(playerListener);
                }
                this.removeStuckListener();
                return;
            }
            System.out.println(this.name+" rolled a double");
            if(this.jailed)
            {
                playActivity.showNotification(this,"Freed from Jail");
                this.jailed = false;
            }
            this.diceToken = true;
            flag = 1;
        }
        else
        {
            if(this.jailed)
            {
                turnsinJail++;
                if(turnsinJail == 3)
                {
                    this.bail(1);
                    this.turnsinJail = 0;
                    if(this.onStuckListener != null)
                    {
                        this.onStuckListener.OnStuck(playerListener);
                    }
                    this.removeStuckListener();
                    return;
                }
                playActivity.showNotification(this,"Turns in Jail : "+turnsinJail );
                if(this.OnCompleteListener != null)
                {
                    this.OnCompleteListener.OnComplete(this);
                }
                return;
            }
            this.diceToken = false;
            flag = 0;
        }
        if(flag == 1)
        {
            if(this.onStuckListener != null)
            {
                this.onStuckListener.OnAnotherChance(playerListener);
                playActivity.showNotification(this,"Another Chance");
            }
        }
        else
        {
            if(this.onStuckListener != null)
            {
                this.onStuckListener.OnStuck(playerListener);
            }
            this.removeStuckListener();
        }
        if(!this.jailed)
        {
            this.position += dice1+dice2;
            if(this.position == 40)
            {
                this.position -= 40;
                Start start = Start.getInstance();
                this.movedtoCity(start);
            }
            else if(this.position > 39)
            {
                this.position -= 40;
                Start start = Start.getInstance();
                start.crossed(this);
                this.movedtoCity(cities.get(this.position));
            }
            else
            {
                this.movedtoCity(cities.get(this.position));
            }
        }
    }

    @Override
    public void movedtoCity(City city)
    {
        if(this.lastCity != null)
        {
            this.lastCity.updatePlayerList(this);
        }
        this.lastCity = city;
        this.position = city.getPosition();
        System.out.println(this.name+" is moved to "+city.getCityName()+" : "+this.position);
        city.visit(this);
    }

    @Override
    public Boolean buyProperty(final Property property) {
        if(this.money >= property.getPrice())
        {
            if(property.buy(this))
            {
                playActivity.showNotification(this,property.getCityName()+" is bought");
                this.payBank(property.getPrice());
                ownedProperties.add(property);
                Collections.sort(ownedProperties, this.colorComparator);
                this.checkforSameGroup(property);
                playActivity.showTitleDeed(this, property, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        PlayActivity.onCompleteListener.OnComplete(Player.this);
                    }
                });
                return true;
            }
            return false;
        }
        else
        {
            playActivity.showNotification(this,"Not enough money");
            LinkedList<String> choices = new LinkedList<String>();
            choices.add("Do you want to mortgage Property");
            choices.add("No");
            playActivity.showChoices(choices, this, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    choice = playActivity.choiceSelected;
                    if(choice == 1)
                    {
                        Player.this.mortgageProperty(new notifier() {
                            @Override
                            public void notified(Object obj) {
                                if(Player.this.notifier != null)
                                {
                                    Player.this.buyProperty(property);
                                    flag = 0;
                                }
                            }

                            @Override
                            public void notifierCancelled() {
                                flag = 1;
                                PlayActivity.onCompleteListener.OnComplete(Player.this);
                            }
                        });
                    }
                }
            });
            if(flag == 1)
                return false;
            return false;
        }
    }

    @Override
    public Boolean payRent(final Property property) {
        Player owner = property.getOwner();

        if(this.money >= property.getRent())
        {
            this.reduceMoney(property.getRent());
            playActivity.showNotification(this,property.getRent()+" is paid to "+owner.getName());
            owner.getRent(property.getRent());
            PlayActivity.onCompleteListener.OnComplete(this);
            return true;
        }
        else
        {
            playActivity.showNotification(this,"Not enough money");
            this.mortgageProperty(new notifier() {
                @Override
                public void notified(Object obj) {
                    if(Player.this.notifier != null)
                    {
                        Player.this.payRent(property);
                        flag = 0;
                    }
                }

                @Override
                public void notifierCancelled() {
                    Player.this.reduceMoney(property.getRent());
                    PlayActivity.onCompleteListener.OnComplete(Player.this);
                }
            });
            return false;
        }
    }

    @Override
    public void getRent(int rentPaid) {
        addMoney(rentPaid);
    }

    @Override
    public void checkforSameGroup(Property property) {

        int color = property.getColorId();
        if((property instanceof Station))
        {
            this.ownedStations ++;
            upStations();
        }
        else if((property instanceof Utilities))
        {
            this.ownedUtilities ++;
            upUtilities();
        }
        else
        {
            if(color != 0)
            {
                int num = ownColor.get(color);
                ownColor.put(color, num-1);
            }
            for(Map.Entry<Integer,Integer> entry : ownColor.entrySet())
            {
                int value = entry.getValue();
                if(value == 0)
                {
                    increaseRent(entry.getKey());
                }
            }
        }

    }

    @Override
    public void increaseRent(int color) {
        ListIterator iterator = ownedProperties.listIterator();
        while(iterator.hasNext())
        {
            Property currentCity = (Property)iterator.next();
            int currentColor = currentCity.getColorId();
            if(color != 0)
            {
                if(color == (currentColor))
                {
                    if(!currentCity.getHousable())
                    {
                        currentCity.buildable();
                        currentCity.doubleRent();
                    }
                }
            }

        }
    }

    @Override
    public void upStations() {
        ListIterator iterator = ownedProperties.listIterator();
        while(iterator.hasNext())
        {
            Property currentCity = (Property)iterator.next();
            if(currentCity instanceof Station)
            {
                Station station = (Station)currentCity;
                if(ownedStations > 1)
                    station.doubleRent(this.ownedStations);
            }
        }
    }

    @Override
    public void upUtilities() {
        ListIterator iterator = ownedProperties.listIterator();
        while(iterator.hasNext())
        {
            Property currentCity = (Property)iterator.next();
            if(currentCity instanceof Utilities)
            {
                Utilities utility = (Utilities)currentCity;
                if(ownedUtilities > 1)
                    utility.increaseMultiplier();
            }
        }
    }

    @Override
    public void bail(int choice)
    {
        if(choice == 1)
        {
            if(50 <= this.money)
            {
                this.payBank(50);
                playActivity.showNotification(this,"Paid $50 for bail");
                this.jailed = false;
                PlayActivity.onCompleteListener.OnComplete(this);
            }
            else {
                playActivity.showNotification(this,"Not enough money");
                this.mortgageProperty(new notifier() {
                    @Override
                    public void notified(Object obj) {
                        if(Player.this.notifier != null)
                        {
                            Player.this.bail(1);
                            flag = 0;
                        }
                    }

                    @Override
                    public void notifierCancelled() {
                        Player.this.reduceMoney(50);
                        PlayActivity.onCompleteListener.OnComplete(Player.this);
                    }
                });
            }
        }
        else if(choice == 2)
        {
            if(this.jailFreeCards > 0)
            {
                this.jailFreeCards -= 1;
                playActivity.showNotification(this,"Used One Jail Free Card");
                this.jailed = false;
                PlayActivity.onCompleteListener.OnComplete(this);
            }
        }
    }

    @Override
    public void jailed()
    {
        if(this.onStuckListener != null)
        {
            this.onStuckListener.OnStuck(playerListener);
            Log.e("Stuck","OnSTuck Called here: Jail");
        }
        this.removeStuckListener();
        removeStuckListener();
        LinkedList<String>  choices = new LinkedList<String>();
        this.movedtoJail();
        this.jailed = true;
        this.diceToken = false;
        this.setCount(0);
        playActivity.showNotification(this,"Imprisoned");
        choices.add("1.Do you wish toPay $50 to free you");
        choices.add("2.No");
        System.out.println("1.Do you wish toPay $50 to free you\n2.No: ");
        if(this.getJailFreeCards() > 0)
        {
            System.out.println("\n3.Use Jail Free Card");
            choices.add("3.Use Jail Free Card");
            System.out.println(this.getName()+" has "+this.getJailFreeCards()+" Jail Free Cards");
        }
        playActivity.showChoices(choices,this, new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                choice = playActivity.choiceSelected;
                Log.e("ChoiceSelected", String.valueOf(choice));
                if(choice == 1)
                {
                    bail(1);
                }
                else if(choice == 2)
                {
                    playActivity.showNotification(Player.this,"Not to play for next three chance unless a double");
                    PlayActivity.onCompleteListener.OnComplete(Player.this);
                }
                else if(choice == 3)
                {
                    bail(2);
                }
            }
        });

    }

    protected void movedtoJail() {
        if(this.lastCity != null)
        {
            this.lastCity.updatePlayerList(this);
        }
        this.lastCity = cities.get(10);
        Jail jail = Jail.getInstance();
        this.position = jail.getPosition();
        System.out.println(this.name+" is moved to "+jail.getCityName()+" : "+this.position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jail.imprisonPlayer(this);
        }
    }

    @Override
    public void addJailFreeCards()
    {
        this.jailFreeCards ++;
        playActivity.showNotification(this,this.jailFreeCards+" Jail Free Cards");
    }

    @Override
    public HashMap<Property,Integer> Build(Property property) {
        int currentHouses = property.getHouses();
        int currentColor = property.getColorId();
        int iter = 1;
        int min = currentHouses;
        HashMap<Property,Integer> buildables = new HashMap<Property,Integer>();
        if(property.getHousable())
        {
            LinkedList<Property> colorProperties = new LinkedList<Property>();
            ListIterator iterator = ownedProperties.listIterator();
            colorProperties.add(property);
            while(iterator.hasNext())
            {
                Property iterCity = (Property)iterator.next();
                int otherColor = iterCity.getColorId();
                if(otherColor != 0)
                {
                    if(currentColor == (otherColor) && (!property.getCityName().equals(iterCity.getCityName())))
                    {
                        colorProperties.add(iterCity);
                        int otherHouses = iterCity.getHouses();
                        min = otherHouses < min ? otherHouses:min;
                    }
                }
            }
            iterator = colorProperties.listIterator();
            while(iterator.hasNext())
            {
                Property iterCity = (Property)iterator.next();
                if(min == iterCity.getHouses() && (!iterCity.getOnMortgage()) )
                {
                    buildables.putIfAbsent(iterCity, iter++);
                }
            }
        }
        return buildables;
    }


    @Override
    public void houseBuilt(int houseValue) {
        this.totalHouses++;
        this.totalBuildingValue += houseValue;
    }

    @Override
    public void hotelBuilt(int hotelValue)
    {
        this.totalHotels++;
        this.totalBuildingValue += hotelValue;
    }

    @Override
    public void mortgageProperty(final Player.notifier notifier)
    {
        this.setNotifier(notifier);
        flag = 0;
        LinkedList<Property> list = new LinkedList<Property>();
        for(Property property : ownedProperties)
        {
            if(!property.getOnMortgage())
            {
                System.out.println(property.getPosition()+" : "+property.getCityName()+" MortgageValue : "+property.getMortgage());
                list.add(property);
            }

        }
        if(list.size() > 0)
        {
            System.out.println("Choose the property to Mortgage :");
            playActivity.showProperties("Choose to Mortgage",list,this, new Dialog.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Property selectedProperty = playActivity.selectedProperty;
                    if(selectedProperty != null)
                    {
                        selectedProperty.mortgage(Player.this);
                        playActivity.showNotification(Player.this,selectedProperty.getCityName()+" is mortgaged");
                        Player.this.mortgagedProperties.add(selectedProperty);
                        Collections.sort(Player.this.mortgagedProperties,colorComparator);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                notifier.notified(null);
                            }
                        }, 2000);
                    }
                    else {
                        removeNotifier();
                    }
                }
            });
        }
        else
        {
            removeNotifier();
            playActivity.showNotification(this,"No properties to Mortgage");
        }

    }

    public void taxed(final Others other)
    {
        if(this.money >= other.getRent())
        {
            this.payBank(other.getRent());
            PlayActivity.onCompleteListener.OnComplete(this);

        }
        else
        {
            playActivity.showNotification(this,"Not enough money");
                this.mortgageProperty(new notifier() {
                    @Override
                    public void notified(Object obj) {
                        Player.this.taxed(other);
                        flag = 0;
                    }

                    @Override
                    public void notifierCancelled() {
                        flag = 1;
                        PlayActivity.onCompleteListener.OnComplete(Player.this);
                    }
                });
            if(flag == 1)
                return;
        }
    }

    public void payBank(int housePrice) {
        this.reduceMoney(housePrice);
        Bank.debitMoney(this,housePrice);
    }

    public void birthdayTreat(Player player)
    {
        this.reduceMoney(10);
        player.getRent(10);
        playActivity.showNotification(this,"Gifted $10 to "+player.getName());
    }

    public void showOwnedProperties()
    {
        playActivity.showOwnedProperties(ownedProperties,this);
    }

    public void releaseProperties()
    {
        playActivity.showProperties("Mortgaged Properties", mortgagedProperties, this, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Property selectedProperty = playActivity.selectedProperty;
                if(selectedProperty != null)
                {
                    if(selectedProperty.releaseMortgage(Player.this))
                        Player.this.mortgagedProperties.remove(selectedProperty);
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }


    public interface OnStuckListener {
        void OnStuck(Player player);
        void OnAnotherChance(Player player);
    }

    protected void setOnStuckListener(Player.OnStuckListener listener,Player player)
    {
        this.onStuckListener = listener;
        playerListener = player;
    }

    protected void removeStuckListener() {
        this.onStuckListener = null;
        playerListener = null;
    }

    public interface notifier{
        void notified(Object obj);
        void notifierCancelled();
    }

    public void setNotifier(Player.notifier notifier)
    {
        this.notifier = notifier;
    }

    public void removeNotifier() {
        this.notifier.notifierCancelled();
        this.notifier = null;
    }

    public interface OnCompleteListener
    {
        void OnComplete(Player player);
    }

    protected void setOnCompleteListener(Player.OnCompleteListener listener)
    {
        this.OnCompleteListener = listener;
    }

    protected void removePlayerListener()
    {
        this.OnCompleteListener = null;
    }

    public void atStart()
    {
        Start start = (Start)Start.getInstance();
        this.lastCity = start;
        int targetPosition = start.getPosition();
        this.position = targetPosition;
        start.playerInCity(this);
    }

    public int getTotalValue()
    {
        int totValue = this.getMoney() + this.getTotalPropertyValue() + this.getTotalBuildingValue() - this.getMortgagedValue();
        return totValue;
    }
}
