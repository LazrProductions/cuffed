package com.lazrproductions.cuffed.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class ComponentUtils {
    public static List<Component> divideIntoWords(List<Component> list) {
        ArrayList<Component> newList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String[] l = list.get(i).getString().split(" ");
            Style style = list.get(i).getStyle();
            for (int j = 0; j < l.length; j++)
                newList.add(Component.literal(l[j]).withStyle(style));
        }

        return newList;
    }

    public static int getTotalHeight(@Nonnull Minecraft instance, List<Component> list, int width) {
        int totalHeight = 0;
        for (int i = 0; i < list.size(); i++)
            totalHeight += instance.font.wordWrapHeight(list.get(i), width);
        return totalHeight;
    }
}
