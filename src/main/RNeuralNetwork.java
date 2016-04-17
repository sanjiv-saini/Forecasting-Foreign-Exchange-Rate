/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the edito.
 */
package main;


import java.text.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;


/**
 *
 * @author sanju singh
 */
public class RNeuralNetwork {
    static {
        Locale.setDefault(Locale.ENGLISH); //??
    }
 
    private boolean isTrained = false;
    //final DecimalFormat df; //??
    final Random rand = new Random();
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
    final int randomWeightMultiplier = 1; //??
    private List<String> dataList = new ArrayList<>();
    
    private String currency = "";
    private static int currencyCol;
 
    final double epsilon = 0.00000000001; //??
 
    double learningRate = 0.3f; //??
    double momentum = 0.8f; //??
 
    // Inputs for xor problem
    final List<Double> inputs;
 
    // Corresponding outputs, xor training data
    final Double expectedOutputs[];
    double resultOutputs[][] = { { -1 } }; // dummy init
    double output[];
 
    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<>(); //??
    
    public RNeuralNetwork(int input, int hidden1,int hidden2, int output, int currencyCol, boolean isTrained) {
        this.layers = new int[] { input, hidden1, hidden2, output };
        this.isTrained = isTrained;
        this.currencyCol = currencyCol;
       // df = new DecimalFormat("#.0#"); //??
        inputs = new ArrayList<Double>();
        expectedOutputs = new Double[output];
        
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
        for ( j = 0; j < output; j++) {
            Neuron neuron = new Neuron();
            neuron.addInConnectionsS(hiddenLayer13);
            neuron.addBiasConnection(bias);
            outputLayer.add(neuron);
        } 
 
        // initialize random weights
        for (Neuron neuron : hiddenLayer11) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer12) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer13) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer21) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer22) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : hiddenLayer23) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
 
        
         // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;
 
        if (isTrained) {
            trainedWeights();
            updateAllWeights();
        }
    }
 
    // random
    double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
    }
 
    /**
     * 
     * @param inputs
     *            There is equally many neurons in the input layer as there are
     *            in input variables
     */

// 
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
// 
//    /**
//     * Calculate the output of the neural network based on the input The forward
//     * operation
//     */
//    public void activate() {
//        for (Neuron n : hiddenLayer11)
//            n.calculateOutput();
//        for (Neuron n : hiddenLayer21)
//            n.calculateOutput();
//        for (Neuron n : hiddenLayer12)
//            n.calculateOutput();
//        for (Neuron n : hiddenLayer22)
//            n.calculateOutput();
//        for (Neuron n : hiddenLayer13)
//            n.calculateOutput();
//        for (Neuron n : outputLayer)
//            n.calculateOutput();
//    }
 
    /**
     * all output propagate back
     * 
     * @param expectedOutput
     *            first calculate the partial derivative of the error with
     *            respect to each of the weight leading into the output neurons
     *            bias is also updated here
     */
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
 
    
//    void run(int maxSteps, double minError, String fileName) throws FileNotFoundException, IOException {
//        int i;
//        String line;
//        // Train neural network until minError reached or maxSteps exceeded
//        double error = 1;
//        BufferedReader br = null;
//        
//        System.out.println("NN Foreign Exchange Rate Forecasting.");
//
//        File file = new File(fileName);                
//        br = new BufferedReader(new FileReader(file));
//        dataList.clear();
//
//        while((line = br.readLine()) != null){
//            dataList.add(line);
//        }
//
//        for (i = 0; i < maxSteps && error > minError; i++) {
//            error = 0;
//            Iterator<String> dataListItr= dataList.iterator();
//            
//            String outputString = "";
//            System.out.println(i);
//            inputs.clear();
//           
//            for (int p = 0; readInputOutput(dataListItr); p++) {
//                
//                setInput(inputs);
//                activate();
//                
//                output = getOutput();  
//                resultOutputs[0] = output;
//                
//                outputString += getOutputString()+"\n";
//                
//                // calculate error for every run.
//                for (int j = 0; j < expectedOutputs.length; j++) {
//                    double err = Math.pow((denormalize(output[j])) - denormalize(expectedOutputs[j]), 2);
//                    error += err;
//                }
//                
//                
//                applyBackpropagation(expectedOutputs);
//            }
//            if((i+1) == maxSteps || error <= minError){
//                System.out.println(outputString);
//            }
//            try {
//                br.close();
//            } catch (IOException ex) {
//                Logger.getLogger(RNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//         
//        System.out.println("Sum of squared errors = " + error);
//        System.out.println("##### EPOCH " + i+"\n");
//
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("RNNresource/training.txt", true)))) {
//            out.println("Currency: "+ currency);
//            out.println("PATTERN: " + layers[0] + " " + layers[1] + " " +layers[2] + " " + layers[3]);
//            out.println("Learning Rate: "+ learningRate + "  Momentum: " + momentum);
//            out.println("EPOCH: " + i);
//            out.println("Sum of squared errors = " + error + "\n");
//        }catch (IOException e) {
//            System.err.println(e);
//        }
//        
//      //  if (i == maxSteps) {
//        //    System.out.println("!Error training try again");
//        //} else {
//            printAllWeights();
//            
//            printWeightUpdate();
//    }
    
    
//    public Double testRun(List<Double> input){
//        setInput(input);
//        activate();
//        return (denormalize(getOutput()[0]));
//    }
    
//    private boolean readInputOutput(Iterator<String> dataListItr){
//        String line;
//               
//        try {  
//            
//                if(inputs.size() == 0){    
//                      
//                for (int i = 0; i < inputLayer1.size(); i++){
//                    if(dataListItr.hasNext()) {
//                        // use comma as separator
//                        line = dataListItr.next();
//                        String[] cols = line.split(",");
//                        inputs.add(normalize(Double.parseDouble(cols[currencyCol])));
//                        //System.out.println("Coulmn 4= " + cols[4] + " , Column 5=" + cols[5]);
//                    } else{
//                        return false;                
//                    }                
//               }
//           
//            } else{
//                //shift every input to left and add previous expected output to last
//                //and read expected output from next row.
//                inputs.remove(0);
//                inputs.add(expectedOutputs[0]);                              
//            }
//            
//            if(dataListItr.hasNext()){
//                line = dataListItr.next();
//                String[] cols = line.split(",");
//                expectedOutputs[0] = normalize(Double.parseDouble(cols[currencyCol]));                          
//            } else{
//                return false;
//            }    
//            
//        } catch (Exception ex) {
//            Logger.getLogger(RNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//        
//        return true;       
//    }
    
//    public static Double normalize(Double d){
//        Double normY;
//        double max = 110, min = 0;
//        switch(currencyCol){
//           case 1: max = 69;
//                    min = 39;
//                    break;
//            case 2: max = 107;
//                    min = 63;
//                    break;
//            case 3: max = 92;
//                    min = 38;
//                    break;
//            case 4: max = 73;
//                    min = 29;
//                    break;    
//        }
//        
//        normY = (((d - min)/(max - min) )*(0.9 - 0.1)) + 0.1;
//        return normY;
//    }
//    
//    public static double denormalize(Double d){
//        Double denormY;
//        
//        double max = 110, min = 0;
//        switch(currencyCol){
//            case 1: max = 69;
//                    min = 39;
//                    break;
//            case 2: max = 107;
//                    min = 63;
//                    break;
//            case 3: max = 92;
//                    min = 38;
//                    break;
//            case 4: max = 73;
//                    min = 29;
//                    break;    
//        }
//        
//        denormY = (((d - 0.1)/(0.9 - 0.1) )*(max - min)) + min;
//        return denormY;        
//    }   
    
         
//    private String getOutputString(){
//        
//        String outputStr = "EXPECTED: ";
//        outputStr += denormalize(expectedOutputs[0]) + " ";
//        outputStr += "ACTUAL: ";
//        outputStr += denormalize(resultOutputs[0][0]) + " ";
//              
//        return outputStr;
//    }
 
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
            Logger.getLogger(RNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(RNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 
//    public void printWeightUpdate(){
//        
//        PrintWriter printWriter = null;
//        try {
//            File file = new File("RNNresource/" + currency + ".csv");
//            file.createNewFile();
//            printWriter = new PrintWriter(file);
//            printWriter.write("" + layers[0] + "," + layers[1] + "," + layers[2] + "," + layers[3] + "\n");             
//            
//            System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
//            // weights for the hidden layer
//            for (Neuron n : hiddenLayer11) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            for (Neuron n : hiddenLayer21) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            for (Neuron n : hiddenLayer12) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            for (Neuron n : hiddenLayer22) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            for (Neuron n : hiddenLayer13) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            // weights for the output layer
//            for (Neuron n : outputLayer) {
//                ArrayList<Connection> connections = n.getAllInConnections();
//                for (Connection con : connections) {
//                    String w = "" + con.getWeight();
//                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
//                    printWriter.flush();
//                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
//                            + con.id + "), " + w + ");");
//                }
//            }
//            System.out.println();
//        } catch (IOException ex) {
//            Logger.getLogger(RNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
//        } finally{
//            try{
//                printWriter.close();
//            }catch(Exception e){}
//        }
//        
//    }
// 
//    
//    
//    public void printAllWeights() {
//        System.out.println("printAllWeights");
//        // weights for the hidden layer
//        printWeights(hiddenLayer11);
//        printWeights(hiddenLayer21);
//        printWeights(hiddenLayer12);
//        printWeights(hiddenLayer22);
//        printWeights(hiddenLayer13);
//        printWeights(outputLayer);
//
//        System.out.println();
//    }
//    
//    private void printWeights(ArrayList<Neuron> layer){
//        
//        for (Neuron n : layer) {
//            ArrayList<Connection> connections = n.getAllInConnections();
//            for (Connection con : connections) {
//                double w = con.getWeight();
//                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
//            }
//        }
//    
//    }
}
   
