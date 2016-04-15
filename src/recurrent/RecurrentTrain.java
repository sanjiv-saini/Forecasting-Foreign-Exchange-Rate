/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recurrent;

import UI.MainUI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import main.Connection;
import main.Neuron;
import main.Utility;

/**
 *
 * @author sanju singh
 */
public class RecurrentTrain extends SwingWorker<Void, Void>{
    
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
    final int randomWeightMultiplier = 1;
    private List<String> dataList = new ArrayList<>();
    
    private String currency = "";
    private static int currencyCol;
 
    final double epsilon = 0.00000000001;
 
    double learningRate = 0.3f;
    double momentum = 0.8f;
 
    final List<Double> inputs;
 
    final Double expectedOutputs[];
    double resultOutputs[][] = { { -1 } };
    double output[];
    
    private int maxSteps;
    private double minError;
    private String filePath;
    
    private MainUI context;
    
    public RecurrentTrain(RecurrentData rData){
        this.layers = new int[] { rData.getInputNeurons(), rData.getHiddenNeurons1(), rData.getHiddenNeurons2(), rData.getOutputNeurons() };
        this.currencyCol = rData.getCurrency();
        this.maxSteps = rData.getEpoch();
        this.minError = rData.getMinError();
        this.filePath = rData.getFilePath();
        this.context = rData.getContext();
        
        inputs = new ArrayList<Double>();
        expectedOutputs = new Double[rData.getOutputNeurons()];
        
        switch(currencyCol){
            case 1: currency = "UsDollar";
                    break;
            case 2: currency = "BritishPound";
                    break;
            case 3: currency = "Euro";
                    break;
            case 4: currency = "Yen";
                    break;          
        }
        
        constructRNN();
        
    }    
    
    private void constructRNN(){
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
    }
    
        
    @Override
    public Void doInBackground() {
        int i;
        String line;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 1;
        BufferedReader br = null;
        
        try{
        
            System.out.println("NN Foreign Exchange Rate Forecasting.");

            File file = new File(filePath);                
            br = new BufferedReader(new FileReader(file));
            dataList.clear();

            while((line = br.readLine()) != null){
                dataList.add(line);
            }

            
            int percent;
            for (i = 0; i < maxSteps && error > minError; i++) {
                
                percent = (int)((i*100)/maxSteps);
                setProgress(Math.min(percent, 100));
                
                error = 0;
                Iterator<String> dataListItr= dataList.iterator();

                String outputString = "";
                System.out.println(i);
                inputs.clear();

                for (int p = 0; readInputOutput(dataListItr); p++) {

                    setInput(inputs);
                    activate();

                    output = getOutput();  
                    resultOutputs[0] = output;

                    outputString += getOutputString()+"\n";

                    // calculate error for every run.
                    for (int j = 0; j < expectedOutputs.length; j++) {
                        double err = Math.pow((Utility.denormalize(output[j], currencyCol)) - Utility.denormalize(expectedOutputs[j], currencyCol), 2);
                        error += err;
                    }


                    applyBackpropagation(expectedOutputs);
                }
                if((i+1) == maxSteps || error <= minError){
                    System.out.println(outputString);
                }
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(RecurrentTrain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            percent = (int)((i*100)/maxSteps);
            setProgress(Math.min(percent, 100));

            System.out.println("Sum of squared errors = " + error);
            System.out.println("##### EPOCH " + i+"\n");

            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("RNNresource/training.txt", true)))) {
                out.println("Currency: "+ currency);
                out.println("PATTERN: " + layers[0] + " " + layers[1] + " " +layers[2] + " " + layers[3]);
                out.println("Learning Rate: "+ learningRate + "  Momentum: " + momentum);
                out.println("EPOCH: " + i);
                out.println("Sum of squared errors = " + error + "\n");
            }catch (IOException e) {
                System.err.println(e);
            }
        }catch(Exception e){
            System.err.println(e);
        }
        
        printAllWeights();
        printWeightUpdate();
        return null;
    }
    
     @Override
    public void done() {
        context.finishRnnTask();
    }
    
    private boolean readInputOutput(Iterator<String> dataListItr){
        String line;
               
        try {  
            
                if(inputs.size() == 0){    
                      
                for (int i = 0; i < inputLayer1.size(); i++){
                    if(dataListItr.hasNext()) {
                        // use comma as separator
                        line = dataListItr.next();
                        String[] cols = line.split(",");
                        inputs.add(Utility.normalize(Double.parseDouble(cols[currencyCol]),currencyCol));
                        //System.out.println("Coulmn 4= " + cols[4] + " , Column 5=" + cols[5]);
                    } else{
                        return false;                
                    }                
               }
           
            } else{
                //shift every input to left and add previous expected output to last
                //and read expected output from next row.
                inputs.remove(0);
                inputs.add(expectedOutputs[0]);                              
            }
            
            if(dataListItr.hasNext()){
                line = dataListItr.next();
                String[] cols = line.split(",");
                expectedOutputs[0] = Utility.normalize(Double.parseDouble(cols[currencyCol]), currencyCol);                          
            } else{
                return false;
            }    
            
        } catch (Exception ex) {
            Logger.getLogger(RecurrentTrain.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;       
    }
    
    public void setInput(List<Double> inputs) {
        int i;
        for (i = 0; i < inputLayer1.size(); i++) {
            inputLayer1.get(i).setOutput(inputs.get(i));
            inputLayer2.get(i).setOutput(inputs.get(i));
            inputLayer3.get(i).setOutput(inputs.get(i));
        }
    }
 
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
 
    /**
     * Calculate the output of the neural network based on the input The forward
     * operation
     */
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
    
    private String getOutputString(){
        
        String outputStr = "EXPECTED: ";
        outputStr += Utility.denormalize(expectedOutputs[0], currencyCol) + " ";
        outputStr += "ACTUAL: ";
        outputStr += Utility.denormalize(resultOutputs[0][0], currencyCol) + " ";
              
        return outputStr;
    }
    
    public void applyBackpropagation(Double expectedOutput[]) {
 
        ArrayList<Double> nextLayerDelValues1 = new ArrayList<>();
        ArrayList<Double> nextLayerDelValues2 = new ArrayList<>();
    
        double ak,ai,desiredOutput,partialDerivative,deltaWeight,newWeight;
        double aj,sumKoutputs,wjk;
        int i = 0,j;
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                ak = n.getOutput();
                ai = con.leftNeuron.getOutput();
                desiredOutput = expectedOutput[i];
                
                partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                deltaWeight = -learningRate * partialDerivative;
                newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }
 
        // update weights for the hidden layer
        nextLayerDelValues1.clear();
        for (Neuron n : hiddenLayer13) {
            aj = n.getOutput();
            sumKoutputs = 0;
            j = 0;
            for (Neuron out_neu : outputLayer) {
                wjk = out_neu.getConnection(n.id).getWeight();
                desiredOutput = (double) expectedOutput[j];
                ak = out_neu.getOutput();
                j++;
                sumKoutputs = sumKoutputs
                        + ((desiredOutput - ak) * ak * (1 - ak) * wjk);
            }
            
            partialDerivative = aj * (1 - aj) * sumKoutputs;
            nextLayerDelValues1.add(partialDerivative);
            
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                ai = con.leftNeuron.getOutput();                 
                deltaWeight = learningRate * partialDerivative * ai;
                newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
        
        
        hiddenLayerBackpropagation(nextLayerDelValues1, nextLayerDelValues2, hiddenLayer22, hiddenLayer13);
        
        hiddenLayerBackpropagation(nextLayerDelValues2, nextLayerDelValues1, hiddenLayer12, hiddenLayer22);
        
        hiddenLayerBackpropagation(nextLayerDelValues1, nextLayerDelValues2, hiddenLayer21, hiddenLayer12);
        
        hiddenLayerBackpropagation(nextLayerDelValues2, nextLayerDelValues1, hiddenLayer11, hiddenLayer21);       
       
    }
    
    
    void hiddenLayerBackpropagation(ArrayList<Double> nextLayerDelValueRead,
                ArrayList<Double> nextLayerDelValueWrite, ArrayList<Neuron> currentLayer, ArrayList<Neuron> nextLayer){
        
        double ai,partialDerivative,deltaWeight,newWeight;
        double aj,sumKoutputs,wjk;
        int j;
        
        nextLayerDelValueWrite.clear();
        for (Neuron n : currentLayer) {
            aj = n.getOutput();
            sumKoutputs = 0;
            j = 0;
            for (Neuron hidden_neu : nextLayer) {
                wjk = hidden_neu.getConnection(n.id).getWeight();
                sumKoutputs = sumKoutputs
                        + (nextLayerDelValueRead.get(j) * wjk);
                j++;
            }
            
            partialDerivative = aj * (1 - aj) * sumKoutputs;
            nextLayerDelValueWrite.add(partialDerivative);
            
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                ai = con.leftNeuron.getOutput();                 
                deltaWeight = learningRate * partialDerivative * ai;
                newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }   
    }
    
    public void printWeightUpdate(){
        
        PrintWriter printWriter = null;
        try {
            File file = new File("RNNresource/" + currency + ".csv");
            file.createNewFile();
            printWriter = new PrintWriter(file);
            printWriter.write("" + layers[0] + "," + layers[1] + "," + layers[2] + "," + layers[3] + "\n");             
            
            System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
            // weights for the hidden layer
            for (Neuron n : hiddenLayer11) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            for (Neuron n : hiddenLayer21) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            for (Neuron n : hiddenLayer12) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            for (Neuron n : hiddenLayer22) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            for (Neuron n : hiddenLayer13) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            // weights for the output layer
            for (Neuron n : outputLayer) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            System.out.println();
        } catch (IOException ex) {
            Logger.getLogger(RecurrentTrain.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try{
                printWriter.close();
            }catch(Exception e){}
        }
        
    }
 
    
    
    public void printAllWeights() {
        System.out.println("printAllWeights");
        // weights for the hidden layer
        printWeights(hiddenLayer11);
        printWeights(hiddenLayer21);
        printWeights(hiddenLayer12);
        printWeights(hiddenLayer22);
        printWeights(hiddenLayer13);
        printWeights(outputLayer);

        System.out.println();
    }
    
    private void printWeights(ArrayList<Neuron> layer){
        
        for (Neuron n : layer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
    
    }
}
