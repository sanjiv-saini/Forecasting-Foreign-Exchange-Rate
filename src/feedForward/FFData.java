/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feedForward;

import UI.MainUI;
import java.util.List;

/**
 *
 * @author sanju singh
 */
public class FFData {
    private int currency;
    private int inputNeurons;
    private int hiddenNeurons;
    private int outputNeurons;
    private int epoch;
    private double minError;
    private String filePath;
    private MainUI context;
    private List<Double> inputValues;

    public List<Double> getInputValues() {
        return inputValues;
    }

    public void setInputValues(List<Double> inputValues) {
        this.inputValues = inputValues;
    }

    public MainUI getContext() {
        return context;
    }

    public void setContext(MainUI context) {
        this.context = context;
    }

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

    public int getHiddenNeurons() {
        return hiddenNeurons;
    }

    public void setHiddenNeurons(int hiddenNeurons) {
        this.hiddenNeurons = hiddenNeurons;
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
    
    
    
    
}
