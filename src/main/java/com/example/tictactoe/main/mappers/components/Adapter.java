package com.example.tictactoe.main.mappers.components;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Adapter {

    public int go(String actualStep){
        int adaptedStep = 0;
        if(countNums(actualStep) > 1){
            Scanner scanner = new Scanner(actualStep);
            List<Integer> list = new ArrayList<>();
            while (scanner.hasNextInt()) {
                list.add(scanner.nextInt());
            }

            if(list.get(0) == 2 && list.get(1) != 3){
                adaptedStep++;
                if(list.get(1) == 1) {
                    list.set(0, list.get(0) + 1);
                }
            }else if(list.get(0) == 3 && list.get(1) != 3){
                if(list.get(1) == 1) {
                    adaptedStep = list.get(0);
                }
                list.set(0, list.get(0)+1);
            }
            adaptedStep += list.get(0) * list.get(1);
        }
        else
            adaptedStep = Integer.parseInt(actualStep);

        return adaptedStep;
    }

    private int countNums(String actualStep) {
        Matcher matcher = Pattern.compile("\\d").matcher(actualStep);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }
}
