/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feedForward;

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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import main.Connection;
//import main.FFNeuralNetwork;
//import static main.FFNeuralNetwork.denormalize;
//import static main.FFNeuralNetwork.normalize;
import main.Neuron;
import main.Utility;

/**
 *
 * @author sanju singh
 */
public class FFTrain extends SwingWorker<Void, Void> {
    
    private List<String> dataList = new ArrayList<>();
    final List<Double> inputs;
    double output[];

    final ArrayList<Neuron> inputLayer = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<>();
    final ArrayList<Neuron> outputLayer = new ArrayList<>();
    
    double resultOutputs[][] = { { -1 } };
    final Double expectedOutputs[];
    
    private String currency = "";
    final int[] layers;
    
    double learningRate = 0.3f; //??
    double momentum = 0.8f; 
    
    private static int currencyCol;
    
    private int maxSteps;
    private double minError;
    private String filePath;
    
    private MainUI context;
    final Neuron bias = new Neuron();
    
    
    
    public FFTrain(FFData nnData){
        
        this.layers = new int[] { nnData.getInputNeurons(), nnData.getHiddenNeurons(), nnData.getOutputNeurons() };
        this.currencyCol = nnData.getCurrency();
        this.maxSteps = nnData.getEpoch();
        this.minError = nnData.getMinError();
        this.filePath = nnData.getFilePath();
        this.context = nnData.getContext();
        
        inputs = new ArrayList<Double>();
        expectedOutputs = new Double[nnData.getOutputNeurons()];
        
        currency = Utility.getCurrency(currencyCol);
        
        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
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
    }
        
    @Override
    public Void doInBackground(){
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
                        double err = Math.pow((Utility.denormalize(output[j], currencyCol) - Utility.denormalize(expectedOutputs[j], currencyCol)), 2);
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
                    Logger.getLogger(FFTrain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            percent = (int)((i*100)/maxSteps);
            setProgress(Math.min(percent, 100));

            System.out.println("Sum of squared errors = " + error);
            System.out.println("##### EPOCH " + i+"\n");

            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("FFresource/training.txt", true)))) {
                out.println("Currency: "+ currency);
                out.println("PATTERN: " + layers[0] + " " + layers[1] + " " +layers[2]);
                out.println("Learning Rate: "+ learningRate + "  Momentum: " + momentum);
                out.println("EPOCH: " + i);
                out.println("Sum of squared errors = " + error + "\n");
            }catch (IOException e) {
                    System.err.println(e);
            }
        
       }catch(Exception e){
            System.err.println(e);
       }

     //  if (i == maxSteps) {
       //    System.out.println("!Error training try again");
       //} else {
        printAllWeights();
        printWeightUpdate();
        
        return null;
   }
    
    
    @Override
    public void done() {
        context.finishFnnTask();
    }
    
   public void applyBackpropagation(Double expectedOutput[]) {
 
        // error check, normalize value ]0;1[
    /*    for (int i = 0; i < expectedOutput.length; i++) {
            double d = expectedOutput[i];
            if (d < 0 || d > 1) {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }*/
    
        int i = 0;
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double ak = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double desiredOutput = expectedOutput[i];
                
                double partialDerivative = ak * (1 - ak)
                        * (desiredOutput - ak);
                double deltaWeight = learningRate * partialDerivative * ai;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }
 
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            double aj = n.getOutput();
            double sumKoutputs = 0;
            int j = 0;
            for (Neuron out_neu : outputLayer) {
                double wjk = out_neu.getConnection(n.id).getWeight();
                double desiredOutput = (double) expectedOutput[j];
                double ak = out_neu.getOutput();
                j++;
                sumKoutputs = sumKoutputs
                        + ((desiredOutput - ak) * ak * (1 - ak) * wjk);
            }
            
            double partialDerivative = aj * (1 - aj) * sumKoutputs;
            
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double ai = con.leftNeuron.getOutput();                 
                double deltaWeight = learningRate * partialDerivative * ai;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }
    
    public void setInput(List<Double> inputs) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs.get(i));
        }
    }
    
     private boolean readInputOutput(Iterator<String> dataListItr){
        String line;
               
        try {  
            
                if(inputs.size() == 0){    
                      
                for (int i = 0; i < inputLayer.size(); i++){
                    if(dataListItr.hasNext()) {
                        // use comma as separator
                        line = dataListItr.next();
                        String[] cols = line.split(",");
                        inputs.add(Utility.normalize(Double.parseDouble(cols[currencyCol]), currencyCol));
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
            Logger.getLogger(FFTrain.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;       
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
    
    private String getOutputString(){
        
        String outputStr = "EXPECTED: ";
        outputStr += Utility.denormalize(expectedOutputs[0], currencyCol) + " ";
        outputStr += "ACTUAL: ";
        outputStr += Utility.denormalize(resultOutputs[0][0], currencyCol) + " ";
              
        return outputStr;
    }
    
    public void printWeightUpdate(){
        
        PrintWriter printWriter = null;
        try {
            File file = new File("FFresource/" + currency + ".csv");
            file.createNewFile();
            printWriter = new PrintWriter(file);
            printWriter.write("" + layers[0] + "," + layers[1] + "," + layers[2] + "\n");             
            
            System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
            // weights for the hidden layer
            for (Neuron n : hiddenLayer) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = "" + con.getWeight();//df.format(con.getWeight());
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
                    String w = "" + con.getWeight();//df.format(con.getWeight());
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            System.out.println();
        } catch (IOException ex) {
            Logger.getLogger(FFTrain.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try{
                printWriter.close();
            }catch(Exception e){e.printStackTrace();}
        }
        
    }
 
    public void printAllWeights() {
        System.out.println("printAllWeights");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        System.out.println();
    }

}
