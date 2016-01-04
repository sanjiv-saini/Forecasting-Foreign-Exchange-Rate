/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the edito.
 */
package exchangerateforecast;


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
public class NeuralNetwork {
    static {
        Locale.setDefault(Locale.ENGLISH); //??
    }
 
    private boolean isTrained = false;
    final DecimalFormat df; //??
    final Random rand = new Random();
    final ArrayList<Neuron> inputLayer = new ArrayList<>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<>();
    final ArrayList<Neuron> outputLayer = new ArrayList<>();
    final Neuron bias = new Neuron();
    final int[] layers;
    final int randomWeightMultiplier = 1; //??
 
    final double epsilon = 0.00000000001; //??
 
    final double learningRate = 0.9f; //??
    final double momentum = 0.7f; //??
 
    // Inputs for xor problem
    final List<Double> inputs;
 
    // Corresponding outputs, xor training data
    final Double expectedOutputs[];
    double resultOutputs[][] = { { -1 } }; // dummy init
    double output[];
 
    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<>(); //??
    
    public NeuralNetwork(int input, int hidden, int output, boolean isTrained) {
        this.layers = new int[] { input, hidden, output };
        this.isTrained = isTrained;
        df = new DecimalFormat("#.0#"); //??
        inputs = new ArrayList<Double>();
        expectedOutputs = new Double[output];
 
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
    public void setInput(List<Double> inputs) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs.get(i));
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
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }
 
    /**
     * all output propagate back
     * 
     * @param expectedOutput
     *            first calculate the partial derivative of the error with
     *            respect to each of the weight leading into the output neurons
     *            bias is also updated here
     */
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
                
                // MAIN ALGORITHM
                double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
                // END
            }
            i++;
        }
 
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double aj = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuron out_neu : outputLayer) {
                    double wjk = out_neu.getConnection(n.id).getWeight();
                    double desiredOutput = (double) expectedOutput[j];
                    double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }
 
                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }
 
    void run(int maxSteps, double minError, String fileName, JLabel testLabel) {
        int i;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 101;
        BufferedReader br = null;
        
        System.out.println("NN Foreign Exchange Rate Forecasting.");

        for (i = 0; i < maxSteps && error > minError; i++) {
            try {
                File file = new File(fileName);
                
                br = new BufferedReader(new FileReader(file));
                error = 0;
                
                String outputString = "";  
                testLabel.setText((i/maxSteps) * 100 + "%");
                for (int p = 0; readInputOutput(br); p++) {
                    
                    setInput(inputs);
                    activate();
                    
                    output = getOutput();
                    resultOutputs[0] = output;
                    
                    outputString += getOutputString()+"\n";
                    
                    // calculate error for every run.
                    for (int j = 0; j < expectedOutputs.length; j++) {
                        double err = Math.pow((output[j] * 100) - (expectedOutputs[j] * 100), 2);
                        error += err;
                    }
                    
                    
                    applyBackpropagation(expectedOutputs);
                }
                
                if((i+1) == maxSteps || error <= minError){
                    System.out.println(outputString);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
         
        System.out.println("Sum of squared errors = " + error);
        System.out.println("##### EPOCH " + i+"\n");
        
      //  if (i == maxSteps) {
        //    System.out.println("!Error training try again");
        //} else {
            printAllWeights();
            
            printWeightUpdate();
    }
    
    
    public Double testRun(List<Double> input){
        setInput(input);
        activate();
        return (getOutput()[0] * 100);
    }
    
    private boolean readInputOutput(BufferedReader br){
        String line;
       
        
        try {  
            
            if(inputs.size() ==0){               
            
                for (int i = 0; i < inputLayer.size(); i++){
                    if((line = br.readLine()) != null) {
                        // use comma as separator
                        String[] cols = line.split(",");
                        inputs.add(Double.parseDouble(cols[1])/100);
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
            
            if((line = br.readLine()) != null){
                String[] cols = line.split(",");
                expectedOutputs[0] = Double.parseDouble(cols[1])/100;                          
            } else{
                return false;
            }    
            
        } catch (Exception ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;       
    }
     
    private String getOutputString(){
        
        String outputStr = "EXPECTED: ";
        outputStr += expectedOutputs[0] * 100 + " ";
        outputStr += "ACTUAL: ";
        outputStr += resultOutputs[0][0] * 100 + " ";
              
        return outputStr;
    }
 
    String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }
 
    /**
     * Take from hash table and put into all weights
     */
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
        File file = new File("outputWeights.csv");
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
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 
    public void printWeightUpdate(){
        
        PrintWriter printWriter = null;
        try {
            File file = new File("outputWeights.csv");
            file.createNewFile();
            printWriter = new PrintWriter(file);
            printWriter.write("" + layers[0] + "," + layers[1] + "," + layers[2] + "\n");             
            
            System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
            // weights for the hidden layer
            for (Neuron n : hiddenLayer) {
                ArrayList<Connection> connections = n.getAllInConnections();
                for (Connection con : connections) {
                    String w = df.format(con.getWeight());
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
                    String w = df.format(con.getWeight());
                    printWriter.write(""+ n.id + "," + con.id + "," + w +"\n");
                    printWriter.flush();
                    System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                            + con.id + "), " + w + ");");
                }
            }
            System.out.println();
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
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
   
