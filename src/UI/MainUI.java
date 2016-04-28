package UI;

import feedForward.FFTrain;
import feedForward.FFData;
import feedForward.FForecast;
//import main.FFNeuralNetwork;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
//import sun.awt.image.ToolkitImage;
import java.awt.Graphics;  
 import javax.swing.JPanel;  
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import neuralNetwork.Utility;
import org.apache.http.StatusLine;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import recurrent.RForecast;
import recurrent.RecurrentData;
import recurrent.RecurrentTrain;
import restApi.ApiCaller;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanju singh
 */
public class MainUI extends javax.swing.JFrame {

    /**
     * Creates new form MainUI
     * 
     * 
     */
    
    private static final int DATE_COL = 0;
    private static final int INPUT_COL = 1;
    private static final int EXPECTED_OUTPUT_COL = 2;
    private static final int ACTUAL_OUTPUT_COL = 3;
 
    public MainUI() {
        try {
            this.iconImage = ImageIO.read(getClass().getResource("/resources/icon.png"));
            setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
            initComponents();
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class MyCustomFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".csv");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Text documents (*.csv)";
        }
    } 
    
    class MyfnnListener implements PropertyChangeListener{

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            jProgressBar1.setValue(progress);
//            taskOutput.append(String.format(
//                    "Completed %d%% of task.\n", task.getProgress()));
        } 
        }
    
    }
    
    class MyRnnListener implements PropertyChangeListener{

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            rProgressBar.setValue(progress);
        }
      }
    
    }
    
    
    static MyOwnFocusTraversalPolicy newPolicy;
    private int algo = 1;
    static Image iconImage;

    
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
          }
    } 
    
    public void showNetworkError(){

        statusLabel.setForeground(new java.awt.Color(204, 0, 0));
        statusLabel.setText("Error: Network Connection Problem");
    }
    
    public void showTodayRate(String[] rate){
        todayText1.setText(rate[0]);
        todayText2.setText(rate[1]);
        todayText3.setText(rate[2]);
        todayText4.setText(rate[3]);
        statusLabel.setText(" ");
   }

    public void calcForecast(String[] rate){
        try {

            Double output;
            String fileName = "Data/testing.csv";
            updateHistFile(rate, fileName);
            for(int i=0;i<4;i++){
                if(i == 0){
                    output = tmrwForecast(fileName, i);
                    tmrwText1.setText(String.valueOf(Utility.formatDecimal(output)));
                }
                
                if(i == 1){
                    output = tmrwForecast(fileName, i);
                    tmrwText2.setText(String.valueOf(Utility.formatDecimal(output)));
                }
                 
                if(i == 2){
                    output = tmrwForecast(fileName, i);
                    tmrwText3.setText(String.valueOf(Utility.formatDecimal(output)));
                }
                  
                if(i == 3){
                    output = tmrwForecast(fileName, i);
                    output = output/100;
                    tmrwText4.setText(String.valueOf(Utility.formatDecimal(output)));
                }
            }
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void updateHistFile(String[] rate, String fileName) throws FileNotFoundException, IOException{
        String todayAsString = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        
        Double yenRate = Double.parseDouble(rate[3]);
        yenRate = yenRate * 100;
        
        File histDataFile = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(histDataFile));
        
        ArrayList<String> histData = new ArrayList<>();
        histData.add(todayAsString + "," + rate[0] + "," + rate[1] + "," + rate[2] + "," + yenRate);

        String line;
        line = br.readLine();
        if(!line.split(",")[0].equals(todayAsString)){
            histData.add(line);
        }
        
        while((line = br.readLine()) != null){
            histData.add(line);
        }
        
        br.close();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)))) {
            for(int i=0;i<histData.size();i++){
                out.println(histData.get(i));
            }
           
        }catch (IOException e) {
            statusLabel.setForeground(new java.awt.Color(204, 0, 0));
            statusLabel.setText("Error writing in history data file");
            System.err.println(e);
        }
    }
    
    public Double tmrwForecast(String fileName, int curr){
        BufferedReader br = null;
        List<Double> inputValues = new ArrayList<Double>();
        String currency = "";
        int currencyCol;
        int inputCnt, hiddenCnt1, hiddenCnt2, outputCnt;
        Double output = null;
        String line;
        String[] cols;
                
        File testDataFile = new File(fileName);

        currencyCol = curr + 1;
        currency = Utility.getCurrency(currencyCol);
        
        File file = new File("RNNresource/" + currency + ".csv");
        FileReader fr;

        try {          
            br = new BufferedReader(new FileReader(file));
            cols = br.readLine().split(",");
            inputCnt = Integer.parseInt(cols[0]);
            hiddenCnt1 = Integer.parseInt(cols[1]);
            hiddenCnt2 = Integer.parseInt(cols[2]);
            outputCnt = Integer.parseInt(cols[3]); 
            br.close();
            try{
                
               br = new BufferedReader(new FileReader(testDataFile));

                   
                   for (int i = 0; i < inputCnt; i++){
                        if((line = br.readLine()) != null) {
                            // use comma as separator
                            cols = line.split(",");
                            inputValues.add(Utility.normalize(Double.parseDouble(cols[currencyCol]),currencyCol));
                        } else{
                             break;
                        }
                    }

                    Collections.reverse(inputValues);
                    
                    RecurrentData data = new RecurrentData();
                    data.setInputNeurons(inputCnt);
                    data.setHiddenNeurons1(hiddenCnt1);
                    data.setHiddenNeurons2(hiddenCnt2);
                    data.setOutputNeurons(outputCnt);
                    data.setCurrencyCol(currencyCol);
                    
                    data.setInputValues(inputValues);

                    RForecast task = new RForecast(data);
                    output = task.forecast();
                     
            }catch (FileNotFoundException ex) {
                statusLabel.setForeground(new java.awt.Color(204, 0, 0));
                statusLabel.setText("Error: History Data File Not Found");
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NumberFormatException ex) {
                statusLabel.setForeground(new java.awt.Color(204, 0, 0));
                statusLabel.setText("Error: History Data File Format Is Not Correct");
               // DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch (IOException ex) {
                statusLabel.setForeground(new java.awt.Color(204, 0, 0));
                statusLabel.setText("Error: Problem Reading History Data file");
               // DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                br.close();
            }
            
        }catch (FileNotFoundException ex) {
            statusLabel.setForeground(new java.awt.Color(204, 0, 0));
            statusLabel.setText("Error: Neural Network Is Not Trained");
            //DialogBox.setVisible(true);
            //Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            statusLabel.setForeground(new java.awt.Color(204, 0, 0));
            statusLabel.setText("Error: Problem Reading Weights File");
           //DialogBox.setVisible(true);
           // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return output;
    }
    
    class MyFrame extends javax.swing.JFrame{
         public MyFrame(){
                setUIFont (new javax.swing.plaf.FontUIResource("Segoe UI",Font.PLAIN,13));
            }        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jFrame1 = new MyFrame();
        jPanel5 = new javax.swing.JPanel();
        submitBtn = new javax.swing.JButton();
        filePath = new java.awt.TextField();
        jButton4 = new javax.swing.JButton();
        testLabel = new javax.swing.JLabel();
        fCurrencyComboBox = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        hiddenNeurons = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        fOutputNeurons = new javax.swing.JSpinner();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        inputNeurons = new javax.swing.JSpinner();
        jProgressBar1 = new JProgressBar(0, 100);
        finishBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        epochInput = new javax.swing.JSpinner();
        jLabel31 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jFrame2 = new MyFrame();
        jPanel7 = new javax.swing.JPanel();
        rSubmitBtn = new javax.swing.JButton();
        rFilePath = new java.awt.TextField();
        jButton6 = new javax.swing.JButton();
        testLabel1 = new javax.swing.JLabel();
        rCurrencyComboBox = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rHiddenNeurons1 = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        rInputNeurons = new javax.swing.JSpinner();
        rOutputNeurons = new javax.swing.JSpinner();
        rHiddenNeurons2 = new javax.swing.JSpinner();
        rProgressBar = new javax.swing.JProgressBar();
        rFinishBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        rSpinner = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        UIManager.put("TabbedPane.contentAreaColor ",ColorUIResource.BLACK);
        UIManager.put("TabbedPane.selected",ColorUIResource.BLACK);
        UIManager.put("TabbedPane.unselectedBackground",ColorUIResource.BLACK); 
        // UIManager.put("TabbedPane.background",ColorUIResource.BLUE);
        UIManager.put("TabbedPane.shadow",ColorUIResource.BLACK);
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new JPanel() {  
            public void paintComponent(Graphics g) {  
                Image img = Toolkit.getDefaultToolkit().getImage(  
                    MainUI.class.getResource("/resources/NNImage.jpg"));  
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);  
            }  
        };  ;
        setUIFont (new javax.swing.plaf.FontUIResource("Segoe UI",Font.PLAIN,14));
        jPanel14 = new javax.swing.JPanel();
        jTextArea1 = jTextArea1 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel4 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea4 = new javax.swing.JTextArea();
        jPanel6 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea5 = new javax.swing.JTextArea();
        jPanel15 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea3 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel16 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea6 = new javax.swing.JTextArea();
        jPanel17 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea7 = new javax.swing.JTextArea();
        jPanel18 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea8 = new javax.swing.JTextArea();
        jPanel19 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea9 = new javax.swing.JTextArea();
        jPanel20 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea10 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel21 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        todayText1 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel22 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        todayText2 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel23 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        todayText3 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel24 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        todayText4 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel25 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jTextArea15 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel26 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tmrwText1 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel27 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tmrwText2 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel28 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tmrwText3 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel29 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tmrwText4 = new JTextArea()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        statusLabel = new javax.swing.JLabel();
        jPanel8 = new JPanel() {
            public void paintComponent(Graphics g) {
                Image img = Toolkit.getDefaultToolkit().getImage(
                    MainUI.class.getResource("/resources/NNImage.jpg"));
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };  ;
        jPanel10 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jLabel8 = new javax.swing.JLabel();
        CurrencyComboBox = new JComboBox()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jLabel7 = new javax.swing.JLabel();
        testingDataPath = new JTextField()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        testingBrowseBtn = new javax.swing.JToggleButton();
        forecastBtn = new javax.swing.JButton();
        jScrollPane1 = new JScrollPane()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        Caret caret = new DefaultCaret()
        {
            public void focusGained(FocusEvent e)
            {
                setVisible(true);
                setSelectionVisible(true);
            }
        };
        caret.setBlinkRate( UIManager.getInt("TextField.caretBlinkRate") );

        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setCaret(caret);
        //textField.setBorder(new LineBorder(Color.BLACK));
        // textField.setBackground(Color.BLUE);

        DefaultCellEditor dce = new DefaultCellEditor( textField );
        forecastTable = new JTable();
        jPanel12 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        jPanel13 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jPanel2 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        doneButton1 = new javax.swing.JButton();
        jPanel30 = new JPanel()
        {
            protected void paintComponent(Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        ;
        graphBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        fileChooser.setFileFilter(new MyCustomFilter());

        jFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jFrame1.setTitle("Train Feed Forward Neural Network");
        jFrame1.setBackground(new java.awt.Color(102, 102, 102));
        jFrame1.setFocusTraversalPolicyProvider(true);
        jFrame1.setIconImage(iconImage);
        jFrame1.setResizable(false);
        jFrame1.setSize(new java.awt.Dimension(580, 420));

        jPanel5.setBackground(new java.awt.Color(38, 50, 56));
        jPanel5.setAlignmentX(0.0F);
        jPanel5.setAlignmentY(0.0F);
        jPanel5.setPreferredSize(new java.awt.Dimension(480, 480));

        submitBtn.setText("Start");
        submitBtn.setOpaque(false);
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitBtnActionPerformed(evt);
            }
        });

        filePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filePathFocusGained(evt);
            }
        });
        filePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filePathActionPerformed(evt);
            }
        });

        jButton4.setText("Browse");
        jButton4.setOpaque(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        fCurrencyComboBox.setBackground(new java.awt.Color(56, 56, 56, 0));
        fCurrencyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "US Dollar", "British Pound", "Euro", "Yen" }));
        fCurrencyComboBox.setOpaque(false);
        fCurrencyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fCurrencyComboBoxActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setLabelFor(fCurrencyComboBox);
        jLabel17.setText("Select Currency                                         :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("Training Data Path:");

        jPanel9.setBackground(new java.awt.Color(51, 51, 51));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Enter Neurons", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(240, 240, 240))); // NOI18N
        jPanel9.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jPanel9.setOpaque(false);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setLabelFor(inputNeurons);
        jLabel3.setText("Input Layer:");
        jLabel3.setToolTipText("");
        jLabel3.setAlignmentY(0.0F);
        jLabel3.setMaximumSize(new java.awt.Dimension(63, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(63, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(63, 14));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Hidden Layer:");

        hiddenNeurons.setModel(new javax.swing.SpinnerNumberModel(1, 1, 500, 1));
        JFormattedTextField format2 = ((JSpinner.DefaultEditor) hiddenNeurons.getEditor()).getTextField();
        format2.addFocusListener(fcsListener);
        hiddenNeurons.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        hiddenNeurons.setOpaque(false);

        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Output Layer:");

        fOutputNeurons.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        JFormattedTextField format3 = ((JSpinner.DefaultEditor) fOutputNeurons.getEditor()).getTextField();
        format3.addFocusListener(fcsListener);
        fOutputNeurons.setEnabled(false);
        fOutputNeurons.setOpaque(false);

        jLabel23.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 153, 102));
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel23.setToolTipText("<html>Enter number of neurons in input layer<br>equal to number of input.<br>Range 1 - 500</html>");
        jLabel23.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel24.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 153, 102));
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel24.setToolTipText("<html>Enter number of neurons in<br> hidden layer of neural network.<br>Range 1 - 500</html>");

        jLabel25.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 153, 102));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel25.setToolTipText("<html>Number of neurons in output layer<br> of NN, equal to number of output.</html>");

        inputNeurons.setModel(new javax.swing.SpinnerNumberModel(1, 1, 500, 1));
        JFormattedTextField format1 = ((JSpinner.DefaultEditor) inputNeurons.getEditor()).getTextField();
        format1.addFocusListener(fcsListener);
        inputNeurons.setToolTipText("");
        inputNeurons.setOpaque(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23))
                    .addComponent(inputNeurons, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24))
                    .addComponent(hiddenNeurons))
                .addGap(48, 48, 48)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fOutputNeurons, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)))
                .addGap(36, 36, 36))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(hiddenNeurons)
                            .addComponent(fOutputNeurons))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(inputNeurons))
                .addContainerGap())
        );

        jProgressBar1.setForeground(new java.awt.Color(51, 128, 244));
        jProgressBar1.setStringPainted(true);

        finishBtn.setText("Finish");
        finishBtn.setEnabled(false);
        finishBtn.setOpaque(false);
        finishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setLabelFor(epochInput);
        jLabel1.setText("Number of Epoch");

        epochInput.setModel(new javax.swing.SpinnerNumberModel(500, 1, 50000, 500));
        JFormattedTextField format0 = ((JSpinner.DefaultEditor) epochInput.getEditor()).getTextField();
        format0.addFocusListener(fcsListener);
        epochInput.setOpaque(false);

        jLabel31.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 153, 102));
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel31.setToolTipText("<html>Number of Iteration to train over training data.<br>Range 1 - 50,000</html>");
        jLabel31.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("         :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(224, 224, 224)
                .addComponent(testLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(49, 49, 49))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel31)
                                .addGap(82, 82, 82)
                                .addComponent(jLabel14))
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fCurrencyComboBox, 0, 137, Short.MAX_VALUE)
                            .addComponent(epochInput)))
                    .addComponent(jLabel6)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(filePath, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(finishBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(fCurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(epochInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(3, 3, 3)))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(2, 2, 2)
                        .addComponent(filePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finishBtn)
                    .addComponent(submitBtn))
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(testLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
            .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 425, Short.MAX_VALUE)
            .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
        );

        jFrame1.setLocationRelativeTo(null);

        jFrame2.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jFrame2.setTitle("Train Recurrent Neural Network");
        jFrame2.setBackground(new java.awt.Color(102, 102, 102));
        jFrame2.setIconImage(iconImage);
        jFrame2.setResizable(false);
        jFrame2.setSize(new java.awt.Dimension(601, 460));

        jPanel7.setBackground(new java.awt.Color(38, 50, 56));
        jPanel7.setAlignmentX(0.0F);
        jPanel7.setAlignmentY(0.0F);
        jPanel7.setFocusCycleRoot(true);
        jPanel7.setFocusTraversalPolicy(newPolicy);
        jPanel7.setPreferredSize(new java.awt.Dimension(590, 460));

        rSubmitBtn.setText("Start");
        rSubmitBtn.setOpaque(false);
        rSubmitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSubmitBtnActionPerformed(evt);
            }
        });

        rFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rFilePathFocusGained(evt);
            }
        });
        rFilePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rFilePathActionPerformed(evt);
            }
        });

        jButton6.setText("Browse");
        jButton6.setOpaque(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        rCurrencyComboBox.setBackground(new java.awt.Color(56, 56, 56, 0));
        rCurrencyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "US Dollar", "British Pound", "Euro", "Yen" }));
        rCurrencyComboBox.setOpaque(false);
        rCurrencyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rCurrencyComboBoxActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Select Currency                                           :");

        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("Training Data Path:");

        jPanel11.setBackground(new java.awt.Color(51, 51, 51));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Enter Neurons", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel11.setOpaque(false);

        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("Input Layer:");
        jLabel10.setToolTipText("");
        jLabel10.setAlignmentY(0.0F);
        jLabel10.setMaximumSize(new java.awt.Dimension(63, 14));
        jLabel10.setMinimumSize(new java.awt.Dimension(63, 14));
        jLabel10.setPreferredSize(new java.awt.Dimension(63, 14));

        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("Hidden Layer:");

        rHiddenNeurons1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 500, 1));
        JFormattedTextField format5 = ((JSpinner.DefaultEditor) rHiddenNeurons1.getEditor()).getTextField();
        format5.addFocusListener(fcsListener);
        rHiddenNeurons1.setNextFocusableComponent(rHiddenNeurons2);
        rHiddenNeurons1.setOpaque(false);

        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("Output Layer:");

        jLabel27.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 153, 102));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel27.setToolTipText("<html>Enter number of neurons in input layer<br>equal to number of input.<br>Range 1 - 500</html>");
        jLabel27.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel28.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 153, 102));
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel28.setToolTipText("<html>Enter number of neurons in<br> hidden layer of neural network.<br>Range 1 - 500</html>");

        jLabel29.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 153, 102));
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel29.setToolTipText("<html>Number of neurons in output layer<br> of NN, equal to number of output.</html>");

        rInputNeurons.setModel(new javax.swing.SpinnerNumberModel(1, 1, 500, 1));
        JFormattedTextField format4 = ((JSpinner.DefaultEditor) rInputNeurons.getEditor()).getTextField();
        format4.addFocusListener(fcsListener);
        rInputNeurons.setNextFocusableComponent(rHiddenNeurons1);
        rInputNeurons.setOpaque(false);

        rOutputNeurons.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        JFormattedTextField format7 = ((JSpinner.DefaultEditor) rOutputNeurons.getEditor()).getTextField();
        format7.addFocusListener(fcsListener);
        rOutputNeurons.setEnabled(false);
        rOutputNeurons.setNextFocusableComponent(rFilePath);
        rOutputNeurons.setOpaque(false);

        rHiddenNeurons2.setModel(new javax.swing.SpinnerNumberModel(1, 1, 500, 1));
        JFormattedTextField format6 = ((JSpinner.DefaultEditor) rHiddenNeurons2.getEditor()).getTextField();
        format6.addFocusListener(fcsListener);
        rHiddenNeurons2.setNextFocusableComponent(rOutputNeurons);
        rHiddenNeurons2.setOpaque(false);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27))
                    .addComponent(rInputNeurons, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28))
                    .addComponent(rHiddenNeurons2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rHiddenNeurons1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29))
                    .addComponent(rOutputNeurons, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rHiddenNeurons1)
                            .addComponent(rOutputNeurons))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rHiddenNeurons2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(rInputNeurons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        rProgressBar.setForeground(new java.awt.Color(51, 128, 244));
        rProgressBar.setStringPainted(true);

        rFinishBtn.setText("Finish");
        rFinishBtn.setEnabled(false);
        rFinishBtn.setOpaque(false);
        rFinishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rFinishBtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setLabelFor(rSpinner);
        jLabel2.setText("Number of Epoch");

        rSpinner.setModel(new javax.swing.SpinnerNumberModel(500, 1, 50000, 500));
        JFormattedTextField format8 = ((JSpinner.DefaultEditor) rSpinner.getEditor()).getTextField();
        format8.addFocusListener(fcsListener);
        rSpinner.setOpaque(false);

        jLabel30.setFont(new java.awt.Font("Kartika", 1, 11)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 153, 102));
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ic_info_outline_white_18dp_1x.png"))); // NOI18N
        jLabel30.setToolTipText("<html>Number of Iteration to train over training data.<br>Range 1 - 50,000</html>");
        jLabel30.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("          :");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(224, 224, 224)
                        .addComponent(testLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(210, 210, 210))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel30)
                                .addGap(88, 88, 88)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(111, 111, 111)))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rCurrencyComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(rSubmitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rFinishBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(rFilePath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(70, 70, 70))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(rCurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rSpinner)
                                .addComponent(jLabel13))
                            .addComponent(jLabel2))
                        .addGap(17, 17, 17)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6)
                    .addComponent(rFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rSubmitBtn)
                    .addComponent(rFinishBtn, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(24, 24, 24)
                .addComponent(rProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testLabel1)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrame2Layout.createSequentialGroup()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jFrame2Layout.createSequentialGroup()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Vector<Component> order = new Vector<Component>(10);
        order.add(rCurrencyComboBox);
        order.add(format8);
        order.add(format4);
        order.add(format5);
        order.add(format6);
        order.add(rFilePath);
        order.add(jButton6);
        order.add(rSubmitBtn);

        newPolicy = new MyOwnFocusTraversalPolicy(order);
        jPanel7.setFocusTraversalPolicy(newPolicy);

        jFrame2.setLocationRelativeTo(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Exchange Rate Forecast");
        setIconImage(iconImage);

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(1360, 610));

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setAlignmentX(0.0F);
        jTabbedPane1.setAlignmentY(0.0F);
        jTabbedPane1.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jTabbedPane1.setOpaque(true);

        jPanel3.setBackground(new java.awt.Color(255, 204, 0));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jPanel14.setBackground(new java.awt.Color(56, 56, 56, 30));
        jPanel14.setForeground(new java.awt.Color(255, 255, 255));
        jPanel14.setOpaque(false);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(38, 50, 56, 220));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI Semilight", 0, 28)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea1.setRows(5);
        jTextArea1.setText("Forecasting Foreign Exchange Rate Using Neural Network");
        jTextArea1.setAlignmentX(2.0F);
        jTextArea1.setAlignmentY(2.0F);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setCaretColor(new java.awt.Color(204, 255, 102));
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextArea1.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jTextArea1.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jTextArea1.setOpaque(false);
        jTextArea1.setRequestFocusEnabled(false);
        jTextArea1.setSelectedTextColor(new java.awt.Color(255, 0, 0));
        jTextArea1.setSelectionColor(new java.awt.Color(255, 51, 51));
        jTextArea1.setSelectionEnd(0);
        jTextArea1.setSelectionStart(0);
        jTextArea1.setVerifyInputWhenFocusTarget(false);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.setInheritsPopupMenu(true);
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 58));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
        });

        jTextArea4.setEditable(false);
        jTextArea4.setBackground(new java.awt.Color(255, 255, 255, 180));
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea4.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea4.setRows(5);
        jTextArea4.setText("Feed Forward Neural Network");
        jTextArea4.setAlignmentX(2.0F);
        jTextArea4.setAlignmentY(2.0F);
        jTextArea4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextArea4.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jTextArea4.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextArea4.setEnabled(false);
        jTextArea4.setFocusable(false);
        jTextArea4.setOpaque(false);
        jTextArea4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextArea4MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextArea4MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(jTextArea4, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jTextArea4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel6.setInheritsPopupMenu(true);
        jPanel6.setOpaque(false);
        jPanel6.setPreferredSize(new java.awt.Dimension(400, 58));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel6MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel6MouseEntered(evt);
            }
        });

        jTextArea5.setEditable(false);
        jTextArea5.setBackground(new java.awt.Color(255, 255, 255, 180));
        jTextArea5.setColumns(20);
        jTextArea5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea5.setForeground(new java.awt.Color(51, 51, 51));
        jTextArea5.setRows(5);
        jTextArea5.setText("Recurrent Neural Network");
        jTextArea5.setAlignmentX(2.0F);
        jTextArea5.setAlignmentY(2.0F);
        jTextArea5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextArea5.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jTextArea5.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jTextArea5.setEnabled(false);
        jTextArea5.setFocusable(false);
        jTextArea5.setOpaque(false);
        jTextArea5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextArea5MouseExited(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextArea5MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(181, Short.MAX_VALUE)
                .addComponent(jTextArea5, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jTextArea5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel15.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel15.setForeground(new java.awt.Color(51, 51, 51));
        jPanel15.setDoubleBuffered(false);
        jPanel15.setEnabled(false);
        jPanel15.setFocusable(false);
        jPanel15.setOpaque(false);

        jTextArea3.setEditable(false);
        jTextArea3.setBackground(new java.awt.Color(38, 50, 56, 0));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jTextArea3.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea3.setRows(5);
        jTextArea3.setText("CURRENCY");
        jTextArea3.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea3.setEnabled(false);
        jTextArea3.setOpaque(false);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(237, Short.MAX_VALUE)
                .addComponent(jTextArea3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel16.setBackground(new java.awt.Color(1, 87, 155, 220));
        jPanel16.setDoubleBuffered(false);
        jPanel16.setEnabled(false);
        jPanel16.setFocusable(false);
        jPanel16.setOpaque(false);

        jTextArea6.setEditable(false);
        jTextArea6.setColumns(20);
        jTextArea6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea6.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea6.setRows(5);
        jTextArea6.setText("USD / INR");
        jTextArea6.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea6.setEnabled(false);
        jTextArea6.setOpaque(false);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(244, 244, 244)
                .addComponent(jTextArea6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel17.setBackground(new java.awt.Color(1, 87, 155, 220));
        jPanel17.setDoubleBuffered(false);
        jPanel17.setEnabled(false);
        jPanel17.setFocusable(false);
        jPanel17.setOpaque(false);

        jTextArea7.setEditable(false);
        jTextArea7.setColumns(20);
        jTextArea7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea7.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea7.setRows(5);
        jTextArea7.setText("GBP / INR");
        jTextArea7.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea7.setEnabled(false);
        jTextArea7.setOpaque(false);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(244, 244, 244)
                .addComponent(jTextArea7, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea7, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        jPanel18.setBackground(new java.awt.Color(1, 87, 155, 220));
        jPanel18.setDoubleBuffered(false);
        jPanel18.setEnabled(false);
        jPanel18.setFocusable(false);
        jPanel18.setOpaque(false);

        jTextArea8.setEditable(false);
        jTextArea8.setColumns(20);
        jTextArea8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea8.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea8.setRows(5);
        jTextArea8.setText("EUR / INR");
        jTextArea8.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea8.setEnabled(false);
        jTextArea8.setOpaque(false);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addGap(244, 244, 244)
                .addComponent(jTextArea8, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel19.setBackground(new java.awt.Color(1, 87, 155, 220));
        jPanel19.setForeground(new java.awt.Color(255, 255, 255));
        jPanel19.setDoubleBuffered(false);
        jPanel19.setEnabled(false);
        jPanel19.setFocusable(false);
        jPanel19.setOpaque(false);

        jTextArea9.setEditable(false);
        jTextArea9.setColumns(20);
        jTextArea9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextArea9.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea9.setRows(5);
        jTextArea9.setText("YEN / INR");
        jTextArea9.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea9.setEnabled(false);
        jTextArea9.setOpaque(false);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGap(244, 244, 244)
                .addComponent(jTextArea9, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel20.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel20.setForeground(new java.awt.Color(51, 51, 51));
        jPanel20.setDoubleBuffered(false);
        jPanel20.setEnabled(false);
        jPanel20.setFocusable(false);
        jPanel20.setOpaque(false);

        jTextArea10.setEditable(false);
        jTextArea10.setBackground(new java.awt.Color(38, 50, 56, 0));
        jTextArea10.setColumns(20);
        jTextArea10.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jTextArea10.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea10.setRows(5);
        jTextArea10.setText("TODAY");
        jTextArea10.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea10.setEnabled(false);
        jTextArea10.setOpaque(false);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jTextArea10, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel21.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel21.setDoubleBuffered(false);
        jPanel21.setEnabled(false);
        jPanel21.setFocusable(false);
        jPanel21.setOpaque(false);

        todayText1.setEditable(false);
        todayText1.setBackground(new java.awt.Color(255, 255, 255, 0));
        todayText1.setColumns(20);
        todayText1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        todayText1.setRows(5);
        todayText1.setText("    --");
        todayText1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        todayText1.setEnabled(false);
        todayText1.setOpaque(false);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(todayText1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(todayText1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel22.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel22.setDoubleBuffered(false);
        jPanel22.setEnabled(false);
        jPanel22.setFocusable(false);
        jPanel22.setOpaque(false);

        todayText2.setEditable(false);
        todayText2.setBackground(new java.awt.Color(255, 255, 255, 0));
        todayText2.setColumns(20);
        todayText2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        todayText2.setRows(5);
        todayText2.setText("    --");
        todayText2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        todayText2.setEnabled(false);
        todayText2.setOpaque(false);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(todayText2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(todayText2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel23.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel23.setDoubleBuffered(false);
        jPanel23.setEnabled(false);
        jPanel23.setFocusable(false);
        jPanel23.setOpaque(false);

        todayText3.setEditable(false);
        todayText3.setBackground(new java.awt.Color(255, 255, 255, 0));
        todayText3.setColumns(20);
        todayText3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        todayText3.setRows(5);
        todayText3.setText("    --");
        todayText3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        todayText3.setEnabled(false);
        todayText3.setOpaque(false);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(todayText3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(todayText3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel24.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel24.setDoubleBuffered(false);
        jPanel24.setEnabled(false);
        jPanel24.setFocusable(false);
        jPanel24.setOpaque(false);

        todayText4.setEditable(false);
        todayText4.setBackground(new java.awt.Color(255, 255, 255, 0));
        todayText4.setColumns(20);
        todayText4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        todayText4.setRows(5);
        todayText4.setText("    --");
        todayText4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        todayText4.setEnabled(false);
        todayText4.setOpaque(false);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(todayText4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(todayText4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel25.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel25.setForeground(new java.awt.Color(51, 51, 51));
        jPanel25.setDoubleBuffered(false);
        jPanel25.setEnabled(false);
        jPanel25.setFocusable(false);
        jPanel25.setOpaque(false);

        jTextArea15.setEditable(false);
        jTextArea15.setBackground(new java.awt.Color(38, 50, 56, 0));
        jTextArea15.setColumns(20);
        jTextArea15.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jTextArea15.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea15.setRows(5);
        jTextArea15.setText("TOMORROW");
        jTextArea15.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jTextArea15.setEnabled(false);
        jTextArea15.setOpaque(false);
        jTextArea15.setRequestFocusEnabled(false);
        jTextArea15.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextArea15, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jTextArea15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel26.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel26.setDoubleBuffered(false);
        jPanel26.setEnabled(false);
        jPanel26.setFocusable(false);
        jPanel26.setOpaque(false);

        tmrwText1.setEditable(false);
        tmrwText1.setBackground(new java.awt.Color(255, 255, 255, 0));
        tmrwText1.setColumns(20);
        tmrwText1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tmrwText1.setRows(5);
        tmrwText1.setText("      --");
        tmrwText1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        tmrwText1.setEnabled(false);
        tmrwText1.setOpaque(false);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tmrwText1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(tmrwText1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel27.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel27.setDoubleBuffered(false);
        jPanel27.setEnabled(false);
        jPanel27.setFocusable(false);
        jPanel27.setOpaque(false);

        tmrwText2.setEditable(false);
        tmrwText2.setBackground(new java.awt.Color(255, 255, 255, 0));
        tmrwText2.setColumns(20);
        tmrwText2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tmrwText2.setRows(5);
        tmrwText2.setText("      --");
        tmrwText2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        tmrwText2.setEnabled(false);
        tmrwText2.setOpaque(false);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tmrwText2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(tmrwText2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel28.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel28.setDoubleBuffered(false);
        jPanel28.setEnabled(false);
        jPanel28.setFocusable(false);
        jPanel28.setOpaque(false);

        tmrwText3.setEditable(false);
        tmrwText3.setBackground(new java.awt.Color(255, 255, 255, 0));
        tmrwText3.setColumns(20);
        tmrwText3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tmrwText3.setRows(5);
        tmrwText3.setText("      --");
        tmrwText3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        tmrwText3.setEnabled(false);
        tmrwText3.setOpaque(false);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(tmrwText3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(tmrwText3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel29.setBackground(new java.awt.Color(255, 255, 255, 220));
        jPanel29.setDoubleBuffered(false);
        jPanel29.setEnabled(false);
        jPanel29.setFocusable(false);
        jPanel29.setOpaque(false);

        tmrwText4.setEditable(false);
        tmrwText4.setBackground(new java.awt.Color(255, 255, 255, 0));
        tmrwText4.setColumns(20);
        tmrwText4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tmrwText4.setRows(5);
        tmrwText4.setText("      --");
        tmrwText4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        tmrwText4.setEnabled(false);
        tmrwText4.setOpaque(false);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(tmrwText4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(tmrwText4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        statusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(0, 51, 0));
        statusLabel.setText("Fetching Todays Data From Internet ...");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextArea1)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(90, Short.MAX_VALUE)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusLabel)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("          Home          ", jPanel3);

        jPanel10.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel10.setDoubleBuffered(false);
        jPanel10.setEnabled(false);
        jPanel10.setFocusable(false);
        jPanel10.setOpaque(false);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Select Currency:");

        CurrencyComboBox.setBackground(new java.awt.Color(56, 56, 56, 0));
        CurrencyComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        CurrencyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "US Dollar", "British Pound", "Euro", "Yen" }));
        CurrencyComboBox.setAlignmentX(2.0F);
        CurrencyComboBox.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Testing Data:");

        testingDataPath.setBackground(new java.awt.Color(255, 255, 255, 200));
        testingDataPath.setMargin(new java.awt.Insets(2, 4, 2, 2));
        testingDataPath.setOpaque(false);
        testingDataPath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                testingDataPathFocusGained(evt);
            }
        });
        testingDataPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                testingDataPathMouseClicked(evt);
            }
        });
        testingDataPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testingDataPathActionPerformed(evt);
            }
        });

        testingBrowseBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        testingBrowseBtn.setText("Browse");
        testingBrowseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testingBrowseBtnActionPerformed(evt);
            }
        });

        forecastBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        forecastBtn.setText("Forecast");
        forecastBtn.setOpaque(false);
        forecastBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forecastBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testingDataPath, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testingBrowseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(forecastBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(CurrencyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(testingDataPath, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(testingBrowseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(forecastBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jScrollPane1.setEnabled(false);
        jScrollPane1.setFocusable(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(805, 100));

        //forecastTable.getTableHeader().setOpaque(false);
        //forecastTable.getTableHeader().setBackground(new java.awt.Color(0,150,136,220));
        forecastTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        forecastTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", ""},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "  Date", "  Input", "  Expected Output", "  Actual Output"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        forecastTable.setAlignmentX(20.0F);
        forecastTable.setAlignmentY(20.0F);
        forecastTable.setGridColor(new java.awt.Color(153, 153, 153));
        forecastTable.setIntercellSpacing(new java.awt.Dimension(20, 10));
        forecastTable.setRowHeight(30);
        forecastTable.getTableHeader().setResizingAllowed(false);
        forecastTable.getTableHeader().setReorderingAllowed(false);
        forecastTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                forecastTableFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(forecastTable);
        if (forecastTable.getColumnModel().getColumnCount() > 0) {
            forecastTable.getColumnModel().getColumn(0).setMinWidth(150);
            forecastTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            forecastTable.getColumnModel().getColumn(0).setMaxWidth(150);
            forecastTable.getColumnModel().getColumn(1).setMinWidth(550);
            forecastTable.getColumnModel().getColumn(1).setPreferredWidth(550);
            forecastTable.getColumnModel().getColumn(1).setMaxWidth(550);
            forecastTable.getColumnModel().getColumn(1).setCellEditor(dce);
        }

        jPanel12.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel12.setOpaque(false);

        jPanel13.setBackground(new java.awt.Color(56, 56, 56, 180));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Algorithm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel13.setOpaque(false);

        jRadioButton2.setBackground(new java.awt.Color(56, 56, 56, 180));
        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jRadioButton2.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButton2.setText("Recurrent Neural Network");
        jRadioButton2.setContentAreaFilled(false);
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        jRadioButton1.setBackground(new java.awt.Color(56, 56, 56, 180));
        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jRadioButton1.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Feed Forward Neural Network");
        jRadioButton1.setContentAreaFilled(false);
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(jRadioButton1)
                .addGap(102, 102, 102)
                .addComponent(jRadioButton2)
                .addContainerGap(116, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel2.setOpaque(false);

        doneButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        doneButton1.setText("Train NN");
        doneButton1.setOpaque(false);
        doneButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doneButton1MouseClicked(evt);
            }
        });
        doneButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(doneButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(doneButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel30.setBackground(new java.awt.Color(38, 50, 56, 220));
        jPanel30.setDoubleBuffered(false);
        jPanel30.setEnabled(false);
        jPanel30.setFocusable(false);
        jPanel30.setOpaque(false);

        graphBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        graphBtn.setText("Plot Graph");
        graphBtn.setOpaque(false);
        graphBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphBtnActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton1.setText("Reset");
        jButton1.setOpaque(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graphBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(graphBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1089, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(134, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        jTabbedPane1.addTab("          Forecast          ", jPanel8);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1376, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Home");

        jScrollPane2.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1371, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void testingBrowseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testingBrowseBtnActionPerformed
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            testingDataPath.setText(file.getAbsolutePath());
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_testingBrowseBtnActionPerformed

    private void doneButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButton1ActionPerformed
        
    }//GEN-LAST:event_doneButton1ActionPerformed

    private void forecastBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forecastBtnActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        if(algo == 1){
            startFForecast();
        }else{
            startRForecast();
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        
    }//GEN-LAST:event_forecastBtnActionPerformed

    private void startFForecast(){
        
        BufferedReader br = null;
        List<Double> inputValues = new ArrayList<Double>();
        String currency = "";
        int currencyCol;
        int inputCnt, hiddenCnt, outputCnt;
        double expectedOutput = 0;
        String tableRowData[] = new String[4];
        Double output;
                
        File testDataFile = new File(testingDataPath.getText());

        currencyCol = CurrencyComboBox.getSelectedIndex()+1;
        currency = Utility.getCurrency(currencyCol);
        
        File file = new File("FFresource/" + currency + ".csv");
        FileReader fr;

        String[] cols;
        try {          
            br = new BufferedReader(new FileReader(file));
            cols = br.readLine().split(",");
            inputCnt = Integer.parseInt(cols[0]);
            hiddenCnt = Integer.parseInt(cols[1]);
            outputCnt = Integer.parseInt(cols[2]); 
            br.close();
            try{
                
                File testResultFile = new File("FFresource/testing.txt");
                Files.deleteIfExists(testResultFile.toPath());

                br = new BufferedReader(new FileReader(testDataFile));

                int i=0;
                while(true){
                    readDataFromFile(br, inputCnt, inputValues, currencyCol, tableRowData);  

                    FFData data = new FFData();
                    data.setInputNeurons(inputCnt);
                    data.setHiddenNeurons(hiddenCnt);
                    data.setOutputNeurons(outputCnt);
                    data.setCurrencyCol(currencyCol);
                    data.setInputValues(inputValues);

                    FForecast task = new FForecast(data);
                    output = task.forecast();

                    tableRowData[ACTUAL_OUTPUT_COL] = Utility.formatDecimal(output);
                    fillTable(tableRowData, i);
                    i++;


                    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("FFresource/testing.txt", true)))) {
                        out.println("intput: " + tableRowData[INPUT_COL]);
                        out.println("Expected Output: " + tableRowData[EXPECTED_OUTPUT_COL]);
                        out.println("Actual: " + output + "\n");
                    }catch (IOException e) {
                        System.err.println(e);
                    }   
                    
                    
                }

                     
            }catch (FileNotFoundException ex) {
                String msg = "File " + testDataFile.getName() + " not found !!";
                JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
                //DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NumberFormatException ex) {
               String msg = "Error reading "+ testDataFile.getName() +".\nFormat is not correct !!";
               JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
              //  DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch(EOFException e){
                System.out.println("History data file is completely read.");
            }catch (IOException ex) {
                String msg = "Error reading "+ testDataFile.getName() +" !!";
                JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
               // DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
               br.close();
            }
        }catch (FileNotFoundException ex) {
            String msg = "Training weights not found for "+ currency +".\nMake sure neural network is trained !! ";
            JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
            //DialogBox.setVisible(true);
            //Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            String msg = "Error reading weights file for "+ currency +".\nMake sure neural network is trained properly !! ";
            JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
            //DialogBox.setVisible(true);
           // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private void startRForecast(){
        
        BufferedReader br = null;
        List<Double> inputValues = new ArrayList<Double>();
        String currency = "";
        int currencyCol;
        int inputCnt, hiddenCnt1, hiddenCnt2, outputCnt;
        double expectedOutput = 0;
        String tableRowData[] = new String[4];
        Double output;
                
        File testDataFile = new File(testingDataPath.getText());

        currencyCol = CurrencyComboBox.getSelectedIndex()+1;
        currency = Utility.getCurrency(currencyCol);
        
        File file = new File("RNNresource/" + currency + ".csv");
        FileReader fr;

        String[] cols;
        try {          
            br = new BufferedReader(new FileReader(file));
            cols = br.readLine().split(",");
            inputCnt = Integer.parseInt(cols[0]);
            hiddenCnt1 = Integer.parseInt(cols[1]);
            hiddenCnt2 = Integer.parseInt(cols[2]);
            outputCnt = Integer.parseInt(cols[3]); 
            br.close();
            try{
                
                File testResultFile = new File("RNNresource/testing.txt");
                Files.deleteIfExists(testResultFile.toPath());

                br = new BufferedReader(new FileReader(testDataFile));

                int i=0;
                while(true){
                    readDataFromFile(br, inputCnt, inputValues, currencyCol, tableRowData);  

                    RecurrentData data = new RecurrentData();
                    data.setInputNeurons(inputCnt);
                    data.setHiddenNeurons1(hiddenCnt1);
                    data.setHiddenNeurons2(hiddenCnt2);
                    data.setOutputNeurons(outputCnt);
                    data.setCurrencyCol(currencyCol);
                    data.setInputValues(inputValues);

                    RForecast task = new RForecast(data);
                    output = task.forecast();

                    tableRowData[ACTUAL_OUTPUT_COL] = Utility.formatDecimal(output);
                    fillTable(tableRowData, i);
                    i++;


                    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("RNNresource/testing.txt", true)))) {
                        out.println("intput: " + tableRowData[INPUT_COL]);
                        out.println("Expected Output: " + tableRowData[EXPECTED_OUTPUT_COL]);
                        out.println("Actual: " + output + "\n");
                    }catch (IOException e) {
                        System.err.println(e);
                    }              
                    
                }
                     
            }catch (FileNotFoundException ex) {
                String msg = "File " + testDataFile.getName() + " not found !!";
                JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NumberFormatException ex) {
                String msg = "Error reading "+ testDataFile.getName() +".\nFormat is not correct !!";
                JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
               // DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }catch(EOFException e){
                System.out.println("History data file is completely read.");
            }catch (IOException ex) {
                String msg = "Error reading "+ testDataFile.getName() +" !!";
                JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
               // DialogBox.setVisible(true);
               // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                br.close();
            }
            
        }catch (FileNotFoundException ex) {
            String msg = "Training weights not found for "+ currency +".\nMake sure neural network is trained !! ";
            JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
            //DialogBox.setVisible(true);
            //Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            String msg = "Error reading weights file for "+ currency +".\nMake sure neural network is trained properly !! ";
            JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
            //DialogBox.setVisible(true);
           // Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    
    }
    
    private void fillTable(String[] tableRowData, int row){
        
        DefaultTableModel model = (DefaultTableModel) forecastTable.getModel();
        if(row >= forecastTable.getRowCount())
            model.addRow(new Object[]{"", "", "","", ""});
                
        forecastTable.setValueAt(tableRowData[DATE_COL], row, DATE_COL);
        forecastTable.setValueAt(tableRowData[INPUT_COL], row, INPUT_COL);
        forecastTable.setValueAt(tableRowData[EXPECTED_OUTPUT_COL], row, EXPECTED_OUTPUT_COL);
        forecastTable.setValueAt(tableRowData[ACTUAL_OUTPUT_COL], row, ACTUAL_OUTPUT_COL);
        
    }
    
    private void readDataFromFile(BufferedReader br, int inputCnt, List<Double> inputValues,
            int currencyCol, String[] tableRowData)throws NumberFormatException, EOFException, IOException{
        String line;
        String[] cols;
        int flag = 1;
 
        if(inputValues.size() == 0){
            for (int i = 0; i < inputCnt; i++){
                if((line = br.readLine()) != null) {
                    // use comma as separator
                    cols = line.split(",");
                    inputValues.add(Utility.normalize(Double.parseDouble(cols[currencyCol]),currencyCol));
                } else{
                    throw new EOFException();
                }
            }
        } else{
            //shift every input to left and add previous expected output to last
            //and read expected output from next row.
            inputValues.remove(0);
            inputValues.add(Utility.normalize(Double.parseDouble(tableRowData[EXPECTED_OUTPUT_COL]), currencyCol));
        }

        String str;
        str = "" + Utility.formatDecimal(Utility.denormalize(inputValues.get(0),currencyCol));
        Double d;
        for(int j=1;j<inputValues.size();j++){
            d = inputValues.get(j);
            str += ", " + Utility.formatDecimal(Utility.denormalize(d,currencyCol));
        }
        tableRowData[INPUT_COL] = str;
        
        // Read expected output to display.
        if((line = br.readLine()) != null){
            cols = line.split(",");
            tableRowData[EXPECTED_OUTPUT_COL] = Utility.formatDecimal(Double.parseDouble(cols[currencyCol]));
            tableRowData[DATE_COL] = cols[0];
        } else{
            throw new EOFException();
        }
    }
    
   
    private void doneButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doneButton1MouseClicked
        // TODO add your handling code here:
        if(algo == 1)
            jFrame1.setVisible(true);
        else if(algo == 2)
            jFrame2.setVisible(true);
            
    }//GEN-LAST:event_doneButton1MouseClicked

    private void submitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBtnActionPerformed
        // TODO add your handling code here:
        
        submitBtn.setEnabled(false);
        finishBtn.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        double minErrorCondition = 0.01;

        FFData data = new FFData();

        data.setCurrencyCol(fCurrencyComboBox.getSelectedIndex()+1);
        data.setInputNeurons((Integer) inputNeurons.getValue());
        data.setHiddenNeurons((Integer) hiddenNeurons.getValue());
        data.setOutputNeurons((Integer) fOutputNeurons.getValue());
        data.setFilePath(filePath.getText());
        data.setEpoch((Integer) epochInput.getValue());
        data.setMinError(minErrorCondition);
        data.setContext(this);
        
        FFTrain task = new FFTrain(data);
        task.addPropertyChangeListener(new MyfnnListener());
        task.execute();        
        
    }//GEN-LAST:event_submitBtnActionPerformed

    private void filePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filePathActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        filePath.setText(chooseFile());
    }//GEN-LAST:event_jButton4ActionPerformed

    private String chooseFile(){
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            System.out.println("File access cancelled by user.");
            return "";
        }        
    }
    
    private void fCurrencyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fCurrencyComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fCurrencyComboBoxActionPerformed

    private void finishBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishBtnActionPerformed
        // TODO add your handling code here:
        jFrame1.dispose();
    }//GEN-LAST:event_finishBtnActionPerformed

    private void filePathFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filePathFocusGained
        // TODO add your handling code here:
        filePath.selectAll();
    }//GEN-LAST:event_filePathFocusGained

    private void rSubmitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSubmitBtnActionPerformed
        rSubmitBtn.setEnabled(false);
        rFinishBtn.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        double minErrorCondition = 0.01;

        RecurrentData data = new RecurrentData();        
       
        data.setCurrencyCol(rCurrencyComboBox.getSelectedIndex()+1);
        data.setInputNeurons((Integer) rInputNeurons.getValue());
        data.setHiddenNeurons1((Integer) rHiddenNeurons1.getValue());
        data.setHiddenNeurons2((Integer) rHiddenNeurons2.getValue());
        data.setOutputNeurons((Integer) rOutputNeurons.getValue());
        data.setFilePath(rFilePath.getText());
        data.setEpoch((Integer)rSpinner.getValue());
        data.setMinError(minErrorCondition);
        data.setContext(this);
        
        RecurrentTrain task = new RecurrentTrain(data);
        task.addPropertyChangeListener(new MyRnnListener());
        task.execute();          
    }//GEN-LAST:event_rSubmitBtnActionPerformed

    private void rFilePathFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rFilePathFocusGained
        // TODO add your handling code here:
        rFilePath.selectAll();
    }//GEN-LAST:event_rFilePathFocusGained

    private void rFilePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rFilePathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rFilePathActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        rFilePath.setText(chooseFile());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void rCurrencyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rCurrencyComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rCurrencyComboBoxActionPerformed

    private void rFinishBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rFinishBtnActionPerformed
        jFrame2.dispose();
    }//GEN-LAST:event_rFinishBtnActionPerformed

    private void testingDataPathMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_testingDataPathMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_testingDataPathMouseClicked

    private void testingDataPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testingDataPathActionPerformed
        // TODO add your handling code here:
        testingDataPath.selectAll();
    }//GEN-LAST:event_testingDataPathActionPerformed

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        // TODO add your handling code here:
        algo = 1;
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        // TODO add your handling code here:
        algo = 2;
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    private void testingDataPathFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_testingDataPathFocusGained
        // TODO add your handling code here:
        testingDataPath.selectAll();
    }//GEN-LAST:event_testingDataPathFocusGained

    private void forecastTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_forecastTableFocusLost
        forecastTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_forecastTableFocusLost

    private void graphBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphBtnActionPerformed
        int i = forecastTable.getRowCount();
        int l=0,h=0;
        if(i>30 )
        {
            String[] args = null;
            //        args[0] = (String)CurrencyComboBox.getSelectedItem();
            XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
            XYSeries dataset1 = new XYSeries("expected");
            XYSeries dataset2 = new XYSeries("actual");
            for(int j=0;j<i;j++)
            {
                double f = Double.parseDouble((String)forecastTable.getValueAt(j, 2));
                double g = Double.parseDouble((String)forecastTable.getValueAt(j, 3));
                dataset1.add(j+1,f);
                dataset2.add(j+1,g);
            }
            xySeriesCollection.addSeries(dataset1);
            xySeriesCollection.addSeries(dataset2);
            switch(CurrencyComboBox.getSelectedIndex())
            {
                case 0: l=60 ;
                h=68 ;
                break;
                case 1: l=90 ;
                h=105 ;
                break;
                case 2: l=65 ;
                h=78 ;
                break;
                case 3: l=50 ;
                h=56 ;
                break;
            }
            LineChart.main(args, xySeriesCollection,l,h);
        }
        else
        {
            String msg = "Forecast Exchange Rate First!!";
            JOptionPane.showMessageDialog(Utility.getActiveFrame(),
                msg, "Error", JOptionPane.WARNING_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_graphBtnActionPerformed

    private void jPanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseExited
        jPanel6.setBackground(new java.awt.Color(255, 255, 255, 220));
        jTextArea5.setDisabledTextColor(new java.awt.Color(51, 51, 51));
    }//GEN-LAST:event_jPanel6MouseExited

    private void jPanel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseEntered
        jPanel6.setBackground(new java.awt.Color(0,150,136,220));
        jTextArea5.setDisabledTextColor(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jPanel6MouseEntered

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        jTabbedPane1.setSelectedIndex(1);
        algo = 2;
        jRadioButton2.setSelected(true);
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jTextArea5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MouseExited
        jPanel6.setBackground(new java.awt.Color(255, 255, 255, 220));
        jTextArea5.setDisabledTextColor(new java.awt.Color(51, 51, 51));
    }//GEN-LAST:event_jTextArea5MouseExited

    private void jTextArea5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MouseEntered
        jPanel6.setBackground(new java.awt.Color(0,150,136,220));
        jTextArea5.setDisabledTextColor(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jTextArea5MouseEntered

    private void jTextArea5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MouseClicked
        jTabbedPane1.setSelectedIndex(1);
        algo = 2;
        jRadioButton2.setSelected(true);
    }//GEN-LAST:event_jTextArea5MouseClicked

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        jPanel4.setBackground(new java.awt.Color(255, 255, 255, 220));
        jTextArea4.setDisabledTextColor(new java.awt.Color(51, 51, 51));
    }//GEN-LAST:event_jPanel4MouseExited

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered
        jPanel4.setBackground(new java.awt.Color(0,150,136,220));
        jTextArea4.setDisabledTextColor(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        jTabbedPane1.setSelectedIndex(1);
        algo = 1;
        jRadioButton1.setSelected(true);
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jTextArea4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MouseExited
        jPanel4.setBackground(new java.awt.Color(255, 255, 255, 220));
        jTextArea4.setDisabledTextColor(new java.awt.Color(51, 51, 51));

    }//GEN-LAST:event_jTextArea4MouseExited

    private void jTextArea4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MouseEntered
        jPanel4.setBackground(new java.awt.Color(0,150,136,220));
        jTextArea4.setDisabledTextColor(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jTextArea4MouseEntered

    private void jTextArea4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MouseClicked
        jTabbedPane1.setSelectedIndex(1);
        algo = 1;
        jRadioButton1.setSelected(true);
    }//GEN-LAST:event_jTextArea4MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultTableModel dtm = (DefaultTableModel) forecastTable.getModel();
        dtm.setRowCount(0);
        dtm.setRowCount(4);
    }//GEN-LAST:event_jButton1ActionPerformed

    private FocusListener fcsListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            dumpInfo(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
            dumpInfo(e);
        }

        private void dumpInfo(FocusEvent e) {
          
            final Component c = e.getComponent();
            if (c instanceof JFormattedTextField) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ((JFormattedTextField) c).setText(((JFormattedTextField) c).getText());
                        ((JFormattedTextField) c).selectAll();
                    }
                });
            } 
        }

        private String name(Component c) {
            return (c == null) ? null : c.getName();
        }
        
    };
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainUI frame = new MainUI();
                frame.setVisible(true); 
                ApiCaller task = new ApiCaller(frame);
                task.execute();
            }
        });
    }

    public JButton getrFinishBtn() {
        return rFinishBtn;
    }

    public JButton getrSubmitBtn() {
        return rSubmitBtn;
    }
    
    public JButton getFinishBtn() {
        return finishBtn;
    }

    public JButton getSubmitBtn() {
        return submitBtn;
    }
      

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CurrencyComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton doneButton1;
    private javax.swing.JSpinner epochInput;
    private javax.swing.JComboBox<String> fCurrencyComboBox;
    private javax.swing.JSpinner fOutputNeurons;
    private javax.swing.JFileChooser fileChooser;
    private java.awt.TextField filePath;
    private javax.swing.JButton finishBtn;
    private javax.swing.JButton forecastBtn;
    private javax.swing.JTable forecastTable;
    private javax.swing.JButton graphBtn;
    private javax.swing.JSpinner hiddenNeurons;
    private javax.swing.JSpinner inputNeurons;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea10;
    private javax.swing.JTextArea jTextArea15;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JComboBox<String> rCurrencyComboBox;
    private java.awt.TextField rFilePath;
    private javax.swing.JButton rFinishBtn;
    private javax.swing.JSpinner rHiddenNeurons1;
    private javax.swing.JSpinner rHiddenNeurons2;
    private javax.swing.JSpinner rInputNeurons;
    private javax.swing.JSpinner rOutputNeurons;
    private javax.swing.JProgressBar rProgressBar;
    private javax.swing.JSpinner rSpinner;
    private javax.swing.JButton rSubmitBtn;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton submitBtn;
    private javax.swing.JLabel testLabel;
    private javax.swing.JLabel testLabel1;
    private javax.swing.JToggleButton testingBrowseBtn;
    private javax.swing.JTextField testingDataPath;
    private javax.swing.JTextArea tmrwText1;
    private javax.swing.JTextArea tmrwText2;
    private javax.swing.JTextArea tmrwText3;
    private javax.swing.JTextArea tmrwText4;
    private javax.swing.JTextArea todayText1;
    private javax.swing.JTextArea todayText2;
    private javax.swing.JTextArea todayText3;
    private javax.swing.JTextArea todayText4;
    // End of variables declaration//GEN-END:variables
}
