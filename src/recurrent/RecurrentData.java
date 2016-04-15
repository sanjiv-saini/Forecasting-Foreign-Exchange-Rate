/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recurrent;

import UI.MainUI;

/**
 *
 * @author sanju singh
 */
public class RecurrentData {
    private int currency;
    private int inputNeurons;
    private int hiddenNeurons1;
    private int hiddenNeurons2;
    private int outputNeurons;
    private int epoch;
    private double minError;
    private String filePath;
    private MainUI context;

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getInputNeurons() {
        return inputNeurons;
    }

    public void setInputNeurons(int inputNeurons) {
        this.inputNeurons = inputNeurons;
    }

    public int getHiddenNeurons1() {
        return hiddenNeurons1;
    }

    public void setHiddenNeurons1(int hiddenNeurons1) {
        this.hiddenNeurons1 = hiddenNeurons1;
    }

    public int getHiddenNeurons2() {
        return hiddenNeurons2;
    }

    public void setHiddenNeurons2(int hiddenNeurons2) {
        this.hiddenNeurons2 = hiddenNeurons2;
    }

    public int getOutputNeurons() {
        return outputNeurons;
    }

    public void setOutputNeurons(int outputNeurons) {
        this.outputNeurons = outputNeurons;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public double getMinError() {
        return minError;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public MainUI getContext() {
        return context;
    }

    public void setContext(MainUI context) {
        this.context = context;
    }
}
