package com.example.monopoly;

import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import java.util.Collections;
import java.util.Random;


class Computer extends Player {
    public Computer() {
        super(2,"Computer");
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
                this.checkforSameGroup(property);playActivity.showTitleDeed(this, property, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                        /*LayerDrawable layer_drawable = (LayerDrawable) ContextCompat.getDrawable(PlayActivity.getInstance(),R.drawable.popupbackground);
                        final GradientDrawable shapeDrawable = (GradientDrawable) layer_drawable
                                .findDrawableByLayerId(R.id.shape);
                        shapeDrawable.setColor(ContextCompat.getColor(PlayActivity.getInstance(),R.color.white));*/
                    PlayActivity.onCompleteListener.OnComplete(Computer.this);
                }
            });
                return true;
            }
            return false;
        }
        else
        {
            playActivity.showNotification(this,"Not enough money");
            Computer.this.mortgageProperty(new notifier() {
                @Override
                public void notified(Object obj) {
                    if(Computer.this.notifier != null)
                    {
                        Computer.this.buyProperty(property);
                        flag = 0;
                    }
                }

                @Override
                public void notifierCancelled() {
                    Log.e("Notifier","Cancelled by Computer");
                    PlayActivity.onCompleteListener.OnComplete(Computer.this);
                    flag = 1;
                }
            });
            if(flag == 1)
                return false;
            return false;
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
        this.movedtoJail();
        this.jailed = true;
        this.diceToken = false;
        this.setCount(0);
        playActivity.showNotification(this,"Imprisoned");
        int numChoices = 2;
        if(this.getJailFreeCards() > 0)
        {
            numChoices = 3;
            System.out.println(this.getName()+" has "+this.getJailFreeCards()+" Jail Free Cards");
        }
        Random random = new Random();
        choice = (random.nextInt(100)%numChoices)+1;
        if(choice == 2)
            choice = (random.nextInt(100)%numChoices)+1;
        Log.e("Computer","In Jailed selected "+choice);
        if(choice == 1)
        {
            bail(1);
        }
        else if(choice == 2)
        {
            playActivity.showNotification(Computer.this,"Not to play for next three chance unless a double");
            PlayActivity.onCompleteListener.OnComplete(this);
        }
        else if(choice == 3)
        {
            bail(2);
        }
    }

    @Override
    public void mortgageProperty(final Player.notifier notifier)
    {
        this.setNotifier(notifier);
        Property selectedProperty = null;
        int max = 0;
        for(Property property : ownedProperties)
        {
            if(!property.getOnMortgage())
            {
                System.out.println(property.getPosition()+" : "+property.getCityName()+" MortgageValue : "+property.getMortgage());
                if(property.getMortgage() >= max)
                {
                    selectedProperty = property;
                    max = property.getMortgage();
                }
            }

        }
        System.out.println("Choose the property to Mortgage :");
        if(selectedProperty != null)
        {
            selectedProperty.mortgage(Computer.this);
            playActivity.showNotification(Computer.this,selectedProperty.getCityName()+" is mortgaged");
            Computer.this.mortgagedProperties.add(selectedProperty);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifier.notified(null);
                }
                }, 2000);
        }
        else
        {
            removeNotifier();
            playActivity.showNotification(this,"No properties to Mortgage");
        }

    }

    public void releaseProperties()
    {
        Property selectedProperty = null;
        int max = 0;
        for(Property property : this.mortgagedProperties)
        {
            int releasePrice = property.getMortgage() + property.getInterest();
            if( releasePrice > max   && (releasePrice <= this.getMoney()))
            {
                selectedProperty = property;
                max = releasePrice;
            }
        }
        if(selectedProperty != null)
        {
            if(selectedProperty.releaseMortgage(Computer.this))
                Computer.this.mortgagedProperties.remove(selectedProperty);
        }
        else
            playActivity.showNotification(this,"No property is released");
    }

}
