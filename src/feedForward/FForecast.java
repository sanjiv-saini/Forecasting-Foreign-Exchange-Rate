/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feedForward;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Connection;
import main.Neuron;
import main.Utility;

/**
 *
 * @author sanju singh
 */
public class FForecast {
    
       
    private final ArrayList<Neuron> inputLayer = new ArrayList<>();
    private final ArrayList<Neuron> hiddenLayer = new ArrayList<>();
    private final ArrayList<Neuron> outputLayer = new ArrayList<>();
    
    private static int currencyCol;
    private List<Double> inputValues;    
    private final Neuron bias = new Neuron();    
    private final int[] layers;    
    private String currency = "";
    
    private final HashMap<String, Double> weightUpdate = new HashMap<>();

    
    
    public FForecast(FFData data){
        
        this.layers = new int[] { data.getInputNeurons(), data.getHiddenNeurons(), data.getOutputNeurons() };
        this.currencyCol = data.getCurrency();
        this.inputValues = data.getInputValues();
        
        currency = Utility.getCurrency(currencyCol);
        
        for (int i = 0; i < layers.length; i++) {
            switch (i) {
                case 0:
                    // input layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        inputLayer.add(neuron);
                    }   break;
                case 1:
                    // hidden layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.addInConnectionsS(inputLayer);
                        neuron.addBiasConnection(bias);
                        hiddenLayer.add(neuron);
                    }   break;
                case 2:
                    // output layer
                    for (int j = 0; j < layers[i]; j++) {
                        Neuron neuron = new Neuron();
                        neuron.addInConnectionsS(hiddenLayer);
                        neuron.addBiasConnection(bias);
                        outputLayer.add(neuron);
                    }   break;
                default:
                    System.out.println("!Error NeuralNetwork init");
                    break;
            }
        }
 
        // initialize random weights
        for (Neuron neuron : hiddenLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
 
        // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;

        trainedWeights();
        updateAllWeights();
    }
    
    public Double forecast(){
        setInput(inputValues);
        activate();
        return (Utility.denormalize(getOutput()[0], currencyCol));
    }
    
    public void setInput(List<Double> inputs) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs.get(i));
        }
    }
    
    public void activate() {
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }
    
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
    
    private  String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }
    
    public void updateAllWeights() {
        // update weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
    }
    
   
 
    // trained data
    void trainedWeights() {
        weightUpdate.clear();
        String line;
        BufferedReader br = null;
        Integer n, c;
        Double w;
        File file = new File("FFresource/" + currency + ".csv");
        try {
            
             br = new BufferedReader(new FileReader(file));
             //for removing the input,hidden,output neurons count.
             br.readLine();
             
             while((line = br.readLine()) != null){
               String[] cols = line.split(",");
               n = Integer.parseInt(cols[0]);
               c = Integer.parseInt(cols[1]);
               w = Double.parseDouble(cols[2]);
               
               weightUpdate.put(weightKey(n, c), w);
           } 
            
        } catch (Exception ex) {
            Logger.getLogger(FForecast.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(FForecast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
