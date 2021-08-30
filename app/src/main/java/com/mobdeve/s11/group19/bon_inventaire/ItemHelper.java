package com.mobdeve.s11.group19.bon_inventaire;

import java.util.ArrayList;

public class ItemHelper {
    public static ArrayList<Item> loadItemData() {
        ArrayList<Item> data = new ArrayList<Item>();

        data.add(new Item(
                "armin.armode.armedian", "armin",
                "RT: Some people consider me a... #bomb",
                9999, "123",0));
        data.add(new Item(
                "wonderboy", "armin",
                "HUUUUUUH????",
                1, "123",0));
        data.add(new Item(
                "eldian.pride", "Falco Grice",
                "I'm so screwed...",
                13, "123",0));
        data.add(new Item(
                "rudolph_the_reiner", "Reiner Braun",
                "@jaegermeister awit",
                0, "123",0));
        return data;
    }
}
