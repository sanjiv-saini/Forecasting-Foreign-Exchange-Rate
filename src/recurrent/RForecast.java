/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recurrent;

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
public class RForecast {
    
    final ArrayList<Neuron> inputLayer1 = new ArrayList<>();
    final ArrayList<Neuron> inputLayer2 = new ArrayList<>();
    final ArrayList<Neuron> inputLayer3 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer11 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer12 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer13 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer21 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer22 = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer23 = new ArrayList<>();
    final ArrayList<Neuron> outputLayer = new ArrayList<>();
    final Neuron bias = new Neuron();
    final int[] layers;
    
    private List<Double> inputValues;
    
    private String currency = "";
    private static int currencyCol;
    
    final HashMap<String, Double> weightUpdate = new HashMap<>(); 
    
    public RForecast(RecurrentData rData){
    
        this.layers = new int[] { rData.getInputNeurons(), rData.getHiddenNeurons1(),
            rData.getHiddenNeurons2(), rData.getOutputNeurons() };
        this.currencyCol = rData.getCurrency();
        this.inputValues = rData.getInputValues();
        
        currency = Utility.getCurrency(currencyCol);

     
       /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
         // timestamp 1
        int j=0;
         
        for ( j = 0; j < layers[0]; j++) {
            Neuron neuron = new Neuron();
            inputLayer1.add(neuron);
        } 
        for ( j = 0; j < layers[1]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(inputLayer1);
            neuron.addBiasConnection(bias);
            hiddenLayer11.add(neuron);
        }
         for ( j = 0; j < layers[2]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(hiddenLayer11);
            neuron.addBiasConnection(bias);
            hiddenLayer21.add(neuron);
        } 


        //timestamp 2 
        for ( j = 0; j < layers[0]; j++) {
            Neuron neuron = new Neuron();
            inputLayer2.add(neuron);
        }
        for ( j = 0; j < layers[1]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(inputLayer2);
            neuron.addInConnectionsS(hiddenLayer21);
            neuron.addBiasConnection(bias);
            hiddenLayer12.add(neuron);
        }
        for ( j = 0; j < layers[2]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(hiddenLayer12);
            neuron.addBiasConnection(bias);
            hiddenLayer22.add(neuron);
        }


        //timestamp 3
        for ( j = 0; j < layers[0]; j++) {
            Neuron neuron = new Neuron();
            inputLayer3.add(neuron);
        }
        for ( j = 0; j < layers[1]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(inputLayer3);
            neuron.addInConnectionsS(hiddenLayer22);
            neuron.addBiasConnection(bias);
            hiddenLayer13.add(neuron);
        } 
        for ( j = 0; j < layers[3]; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(hiddenLayer13);
            neuron.addBiasConnection(bias);
            outputLayer.add(neuron);
        } 
 
        // initialize random weights
        for (Neuron neuron : hiddenLayer11) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer12) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer13) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer21) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer22) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = Utility.getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer23) {
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
        setInputValues(inputValues);
        activate();
        double[] output = getOutput();
        return (Utility.denormalize(output[0], currencyCol));
    }
    
    public void setInputValues(List<Double> inputs) {
        int i;
        for (i = 0; i < inputLayer1.size(); i++) {
            inputLayer1.get(i).setOutput(inputs.get(i));
            inputLayer2.get(i).setOutput(inputs.get(i));
            inputLayer3.get(i).setOutput(inputs.get(i));
        }
    }
    
    public void activate() {
        for (Neuron n : hiddenLayer11)
            n.calculateOutput();
        for (Neuron n : hiddenLayer21)
            n.calculateOutput();
        for (Neuron n : hiddenLayer12)
            n.calculateOutput();
        for (Neuron n : hiddenLayer22)
            n.calculateOutput();
        for (Neuron n : hiddenLayer13)
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
    
       String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }
 
    /**
     * Take from hash table and put into all weights
     */
    public void updateAllWeights() {        
        updateWeights(outputLayer);
        updateWeights(hiddenLayer13);
        updateWeights(hiddenLayer22);
        updateWeights(hiddenLayer12);
        updateWeights(hiddenLayer21);
        updateWeights(hiddenLayer11);
    }
    
    private void updateWeights(ArrayList<Neuron> layer){
        for (Neuron n : layer) {
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
        File file = new File("RNNresource/" + currency + ".csv");
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
            Logger.getLogger(RForecast.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(RForecast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
