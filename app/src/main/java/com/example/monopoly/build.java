package com.example.monopoly;

import java.util.HashMap;

interface build {
    HashMap<Property,Integer> Build(Property property);
    void houseBuilt(int houseValue);
    void hotelBuilt(int hotelValue);
}
