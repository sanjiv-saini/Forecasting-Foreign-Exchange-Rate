package neuralNetwork;

import java.awt.Frame;
import java.text.DecimalFormat;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanju singh
 */
public class Utility {
    private static final int randomWeightMultiplier = 1;
    private static final Random rand = new Random();
    
    public static double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
    }
    
    public static Double normalize(Double d, int currencyCol){
        Double normY;
        double max = 110, min = 0;
        switch(currencyCol){
           case 1: max = 69;
                    min = 39;
                    break;
            case 2: max = 107;
                    min = 63;
                    break;
            case 3: max = 92;
                    min = 38;
                    break;
            case 4: max = 73;
                    min = 29;
                    break;    
        }
        
        normY = (((d - min)/(max - min) )*(0.9 - 0.1)) + 0.1;
        return normY;
    }
    
    public static double denormalize(Double d, int currencyCol){
        Double denormY;
        
        double max = 110, min = 0;
        switch(currencyCol){
            case 1: max = 69;
                    min = 39;
                    break;
            case 2: max = 107;
                    min = 63;
                    break;
            case 3: max = 92;
                    min = 38;
                    break;
            case 4: max = 73;
                    min = 29;
                    break;    
        }
        
        denormY = (((d - 0.1)/(0.9 - 0.1) )*(max - min)) + min;
        return denormY;        
    }   
    
    public static String getCurrency(int col){
        String currency;
        switch(col){
            case 1: currency = "UsDollar";
            break;
            case 2: currency = "BritishPound";
            break;
            case 3: currency = "Euro";
            break;
            case 4: currency = "Yen";
            break;
            default: currency = "";
        }
        return currency;
    }
    
    public static String formatDecimal(Double num){
        DecimalFormat df = new DecimalFormat("0.0000");
        return df.format(num);
    }
    
    public static String formatDate(String s) {
        s = s.trim();
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-5);
    }
    public static Frame getActiveFrame() { 
        Frame result = null; 
        Frame[] frames = Frame.getFrames(); 
        for (int i = 0; i < frames.length; i++) { 
         Frame frame = frames[i]; 
         if (frame.isVisible()) { 
          result = frame; 
         } 
        } 
        return result; 
    } 
    
}
