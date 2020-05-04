package com.example.monopoly;

interface playable <T extends Player>{
    Boolean buyProperty(Property property);
    Boolean payRent(Property property);
    void getRent(int rentPaid);
    void move(int dice1, int dice2, Player.OnStuckListener listener, Player.OnCompleteListener onCompleteListener);
    void movedtoCity(City city);
}
