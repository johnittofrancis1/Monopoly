package com.example.monopoly;



import android.os.Build;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import androidx.annotation.RequiresApi;

/**
 *
 * @author johni
 */
public class CityGroup {
    private static Map<Integer,Integer> listColor;
    private static Map<Property,Integer> colorCities;

    public CityGroup() {
        listColor = new HashMap<>();
        colorCities = new HashMap<Property,Integer>();
    }

    public static Map<Integer, Integer> getListColor() {
        return Collections.unmodifiableMap(listColor);
    }

    public static LinkedList<Property> getColorCities(int colorId) {
        LinkedList<Property> list = new LinkedList<Property>();
        for(Map.Entry<Property,Integer> entry : colorCities.entrySet())
        {
            if(entry.getKey().getColorId() == (colorId))
            {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void grouping(Map<Integer,City> cities)
    {
        for(Map.Entry<Integer,City> entry : cities.entrySet())
        {
            int colorId = entry.getValue().getColorId();
            if(colorId != 0)
            {
                Property property =(Property) entry.getValue();
                int num = listColor.getOrDefault(colorId,0);
                colorCities.putIfAbsent(property, colorId);
                if(num == 0)
                {
                    listColor.put(colorId,1);
                }
                else
                {
                    listColor.put(colorId, ++num);
                }
            }
        }
    }
}
