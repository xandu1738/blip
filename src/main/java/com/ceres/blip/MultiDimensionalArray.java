package com.ceres.blip;

import java.util.Arrays;

public class MultiDimensionalArray {
    public static void main(String[] args) {
        int[][] multi = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        //Adding a new row in the array
        int[][] newArray = new int[multi.length + 1][3];
        for (int i = 0; i < multi.length; i++) {
            newArray[i] = Arrays.copyOf(multi[i], multi[i].length);
        }

        newArray[3] = new int[]{10, 11, 12};

        for (int[] ints : newArray) {
            for (int anInt : ints) {
                System.out.print(anInt + ",");
            }
        }
    }
}
