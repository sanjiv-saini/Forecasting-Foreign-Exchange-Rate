///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the edito.
// */
//package main;
//
//
//import UI.MainUI;
//import java.text.*;
//import java.util.*;
//import java.io.*;
//import static java.lang.Math.random;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.JLabel;
//
//
///**
// *
// * @author sanju singh
// */
//public class FFNeuralNetwork {
//    static {
//        Locale.setDefault(Locale.ENGLISH); //??
//    }
// 
//    private boolean isTrained = false;
//    //final DecimalFormat df; //??
////    final Random rand = new Random();
////    final ArrayList<Neuron> inputLayer = new ArrayList<>();
////    final ArrayList<Neuron> hiddenLayer = new ArrayList<>();
////    final ArrayList<Neuron> outputLayer = new ArrayList<>();
//    final Neuron bias = new Neuron();
////    final int[] layers;
//    final int randomWeightMultiplier = 1; //??
//   // private List<String> dataList = new ArrayList<>();
//    
////    private String currency = "";
////    private static int currencyCol;
// 
//    final double epsilon = 0.00000000001; //??
// 
////    double learningRate = 0.3f; //??
////    double momentum = 0.8f; //??
// 
//    // Inputs for xor problem
//    final List<Double> inputs;
// 
//    // Corresponding outputs, xor training data
////    final Double expectedOutputs[];
//    double resultOutputs[][] = { { -1 } }; // dummy init
//    double output[];
// 
//    // for weight update all
////    final HashMap<String, Double> weightUpdate = new HashMap<>(); //??
//    
//    public FFNeuralNetwork(int input, int hidden, int output, int currencyCol, boolean isTrained) {
//        this.layers = new int[] { input, hidden, output };
//        this.isTrained = isTrained;
//        this.currencyCol = currencyCol;
//       // df = new DecimalFormat("#.0#"); //??
//        inputs = new ArrayList<Double>();
//        expectedOutputs = new Double[output];
//        
//        switch(currencyCol){
//            case 1: currency = "UsDollar";
//                    break;
//            case 2: currency = "BritishPound";
//                    break;
//            case 3: currency = "Euro";
//                    break;
//            case 4: currency = "Yen";
//                    break;          
//        }
// 
//        /**
//         * Create all neurons and connections Connections are created in the
//         * neuron class
//         */
//        for (int i = 0; i < layers.length; i++) {
//            switch (i) {
//                case 0:
//                    // input layer
//                    for (int j = 0; j < layers[i]; j++) {
//                        Neuron neuron = new Neuron();
//                        inputLayer.add(neuron);
//                    }   break;
//                case 1:
//                    // hidden layer
//                    for (int j = 0; j < layers[i]; j++) {
//                        Neuron neuron = new Neuron();
//                        neuron.addInConnectionsS(inputLayer);
//                        neuron.addBiasConnection(bias);
//                        hiddenLayer.add(neuron);
//                    }   break;
//                case 2:
//                    // output layer
//                    for (int j = 0; j < layers[i]; j++) {
//                        Neuron neuron = new Neuron();
//                        neuron.addInConnectionsS(hiddenLayer);
//                        neuron.addBiasConnection(bias);
//                        outputLayer.add(neuron);
//                    }   break;
//                default:
//                    System.out.println("!Error NeuralNetwork init");
//                    break;
//            }
//        }
// 
//        // initialize random weights
//        for (Neuron neuron : hiddenLayer) {
//            ArrayList<Connection> connections = neuron.getAllInConnections();
//            for (Connection conn : connections) {
//                double newWeight = getRandom();
//                conn.setWeight(newWeight);
//            }
//        }
//        for (Neuron neuron : outputLayer) {
//            ArrayList<Connection> connections = neuron.getAllInConnections();
//            for (Connection conn : connections) {
//                double newWeight = getRandom();
//                conn.setWeight(newWeight);
//            }
//        }
// 
//        // reset id counters
//        Neuron.counter = 0;
//        Connection.counter = 0;
// 
//        if (isTrained) {
//            trainedWeights();
//            updateAllWeights();
//        }
//    }
//// 
//    // random
////    double getRandom() {
////        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
////    }
// 
//    /**
//     * 
//     * @param inputs
//     *            There is equally many neurons in the input layer as there are
//     *            in input variables
//     */
////    public void setInput(List<Double> inputs) {
////        for (int i = 0; i < inputLayer.size(); i++) {
////            inputLayer.get(i).setOutput(inputs.get(i));
////        }
////    }
// 
////    public double[] getOutput() {
////        double[] outputs = new double[outputLayer.size()];
////        for (int i = 0; i < outputLayer.size(); i++)
////            outputs[i] = outputLayer.get(i).getOutput();
////        return outputs;
////    }
// 
//    /**
//     * Calculate the output of the neural network based on the input The forward
//     * operation
//     */
////    public void activate() {
////        for (Neuron n : hiddenLayer)
////            n.calculateOutput();
////        for (Neuron n : outputLayer)
////            n.calculateOutput();
////    }
//// 
//    /**
//     * all output propagate back
//     * 
//     * @param expectedOutput
//     *            first calculate the partial derivative of the error with
//     *            respect to each of the weight leading into the output neurons
//     *            bias is also updated here
//     */
////    public void applyBackpropagation(Double expectedOutput[]) {
//// 
////        // error check, normalize value ]0;1[
////    /*    for (int i = 0; i < expectedOutput.length; i++) {
////            double d = expectedOutput[i];
////            if (d < 0 || d > 1) {
////                if (d < 0)
////                    expectedOutput[i] = 0 + epsilon;
////                else
////                    expectedOutput[i] = 1 - epsilon;
////            }
////        }*/
////    
////        int i = 0;
////        for (Neuron n : outputLayer) {
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                double ak = n.getOutput();
////                double ai = con.leftNeuron.getOutput();
////                double desiredOutput = expectedOutput[i];
////                
////                double partialDerivative = ak * (1 - ak)
////                        * (desiredOutput - ak);
////                double deltaWeight = learningRate * partialDerivative * ai;
////                double newWeight = con.getWeight() + deltaWeight;
////                con.setDeltaWeight(deltaWeight);
////                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
////            }
////            i++;
////        }
//// 
////        // update weights for the hidden layer
////        for (Neuron n : hiddenLayer) {
////            double aj = n.getOutput();
////            double sumKoutputs = 0;
////            int j = 0;
////            for (Neuron out_neu : outputLayer) {
////                double wjk = out_neu.getConnection(n.id).getWeight();
////                double desiredOutput = (double) expectedOutput[j];
////                double ak = out_neu.getOutput();
////                j++;
////                sumKoutputs = sumKoutputs
////                        + ((desiredOutput - ak) * ak * (1 - ak) * wjk);
////            }
////            
////            double partialDerivative = aj * (1 - aj) * sumKoutputs;
////            
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                double ai = con.leftNeuron.getOutput();                 
////                double deltaWeight = learningRate * partialDerivative * ai;
////                double newWeight = con.getWeight() + deltaWeight;
////                con.setDeltaWeight(deltaWeight);
////                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
////            }
////        }
////    }
// 
//  /*  public void run(int maxSteps, double minError, String fileName) throws FileNotFoundException, IOException {
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
//        int percent;
//
//        for (i = 0; i < maxSteps && error > minError; i++) {
//            
//            percent = ((int)(i/maxSteps))*100;
//           
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
//                Logger.getLogger(FFNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        percent = ((int)(i/maxSteps))*100;
//         
//        System.out.println("Sum of squared errors = " + error);
//        System.out.println("##### EPOCH " + i+"\n");
//
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("FFresource/training.txt", true)))) {
//            out.println("Currency: "+ currency);
//            out.println("PATTERN: " + layers[0] + " " + layers[1] + " " +layers[2]);
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
//    }*/
//    
//    
////    public Double testRun(List<Double> input){
////        setInput(input);
////        activate();
////        return (denormalize(getOutput()[0]));
////    }
//    
////    private boolean readInputOutput(Iterator<String> dataListItr){
////        String line;
////               
////        try {  
////            
////                if(inputs.size() == 0){    
////                      
////                for (int i = 0; i < inputLayer.size(); i++){
////                    if(dataListItr.hasNext()) {
////                        // use comma as separator
////                        line = dataListItr.next();
////                        String[] cols = line.split(",");
////                        inputs.add(normalize(Double.parseDouble(cols[currencyCol])));
////                        //System.out.println("Coulmn 4= " + cols[4] + " , Column 5=" + cols[5]);
////                    } else{
////                        return false;                
////                    }                
////               }
////           
////            } else{
////                //shift every input to left and add previous expected output to last
////                //and read expected output from next row.
////                inputs.remove(0);
////                inputs.add(expectedOutputs[0]);                              
////            }
////            
////            if(dataListItr.hasNext()){
////                line = dataListItr.next();
////                String[] cols = line.split(",");
////                expectedOutputs[0] = normalize(Double.parseDouble(cols[currencyCol]));                          
////            } else{
////                return false;
////            }    
////            
////        } catch (Exception ex) {
////            Logger.getLogger(FFNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
////            return false;
////        }
////        
////        return true;       
////    }
//    
////    public static Double normalize(Double d){
////        Double normY;
////        double max = 110, min = 0;
////        switch(currencyCol){
////           case 1: max = 69;
////                    min = 39;
////                    break;
////            case 2: max = 107;
////                    min = 63;
////                    break;
////            case 3: max = 92;
////                    min = 38;
////                    break;
////            case 4: max = 73;
////                    min = 29;
////                    break;    
////        }
////        
////        normY = (((d - min)/(max - min) )*(0.9 - 0.1)) + 0.1;
////        return normY;
////    }
////    
////    public static double denormalize(Double d){
////        Double denormY;
////        
////        double max = 110, min = 0;
////        switch(currencyCol){
////            case 1: max = 69;
////                    min = 39;
////                    break;
////            case 2: max = 107;
////                    min = 63;
////                    break;
////            case 3: max = 92;
////                    min = 38;
////                    break;
////            case 4: max = 73;
////                    min = 29;
////                    break;    
////        }
////        
////        denormY = (((d - 0.1)/(0.9 - 0.1) )*(max - min)) + min;
////        return denormY;        
////    }   
////    
//         
////    private String getOutputString(){
////        
////        String outputStr = "EXPECTED: ";
////        outputStr += denormalize(expectedOutputs[0]) + " ";
////        outputStr += "ACTUAL: ";
////        outputStr += denormalize(resultOutputs[0][0]) + " ";
////              
////        return outputStr;
////    }
// 
//    String weightKey(int neuronId, int conId) {
//        return "N" + neuronId + "_C" + conId;
//    }
// 
//    /**
//     * Take from hash table and put into all weights
//     */
////    public void updateAllWeights() {
////        // update weights for the output layer
////        for (Neuron n : outputLayer) {
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                String key = weightKey(n.id, con.id);
////                double newWeight = weightUpdate.get(key);
////                con.setWeight(newWeight);
////            }
////        }
////        // update weights for the hidden layer
////        for (Neuron n : hiddenLayer) {
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                String key = weightKey(n.id, con.id);
////                double newWeight = weightUpdate.get(key);
////                con.setWeight(newWeight);
////            }
////        }
////    }
//// 
////    // trained data
////    void trainedWeights() {
////        weightUpdate.clear();
////        String line;
////        BufferedReader br = null;
////        Integer n, c;
////        Double w;
////        File file = new File("FFresource/" + currency + ".csv");
////        try {
////            
////             br = new BufferedReader(new FileReader(file));
////             //for removing the input,hidden,output neurons count.
////             br.readLine();
////             
////             while((line = br.readLine()) != null){
////               String[] cols = line.split(",");
////               n = Integer.parseInt(cols[0]);
////               c = Integer.parseInt(cols[1]);
////               w = Double.parseDouble(cols[2]);
////               
////               weightUpdate.put(weightKey(n, c), w);
////           } 
////            
////        } catch (Exception ex) {
////            Logger.getLogger(FFNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
////        } finally{
////            try {
////                br.close();
////            } catch (IOException ex) {
////                Logger.getLogger(FFNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
////    }
// 
////    public void printWeightUpdate(){
////        
////        PrintWriter printWriter = null;
////        try {
////            File file = new File("FFresource/" + currency + ".csv");
////            file.createNewFile();
////            printWriter = new PrintWriter(file);
////            printWriter.write("" + layers[0] + "," + layers[1] + "," + layers[2] + "\n");             
////            
////            System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
////            // weights for the hidden layer
////            for (Neuron n : hiddenLayer) {
////                ArrayList<Connection> connections = n.getAllInConnections();
////                for (Connection con : connections) {
////                    String w = "" + con.getWeight();//df.format(con.getWeight());
////                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
////                    printWriter.flush();
////                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
////                            + con.id + "), " + w + ");");
////                }
////            }
////            // weights for the output layer
////            for (Neuron n : outputLayer) {
////                ArrayList<Connection> connections = n.getAllInConnections();
////                for (Connection con : connections) {
////                    String w = "" + con.getWeight();//df.format(con.getWeight());
////                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
////                    printWriter.flush();
////                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
////                            + con.id + "), " + w + ");");
////                }
////            }
////            System.out.println();
////        } catch (IOException ex) {
////            Logger.getLogger(FFNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
////        } finally{
////            try{
////                printWriter.close();
////            }catch(Exception e){e.printStackTrace();}
////        }
////        
////    }
//// 
////    public void printAllWeights() {
////        System.out.println("printAllWeights");
////        // weights for the hidden layer
////        for (Neuron n : hiddenLayer) {
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                double w = con.getWeight();
////                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
////            }
////        }
////        // weights for the output layer
////        for (Neuron n : outputLayer) {
////            ArrayList<Connection> connections = n.getAllInConnections();
////            for (Connection con : connections) {
////                double w = con.getWeight();
////                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
////            }
////        }
////        System.out.println();
////    }
//}
//   
