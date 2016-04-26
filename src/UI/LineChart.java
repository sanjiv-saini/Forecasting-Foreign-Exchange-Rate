package UI;

import java.awt.Color;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;


public class LineChart extends javax.swing.JFrame
{
    public LineChart()
    {
        super("");
    }
   void createChart( String applicationTitle , String chartTitle , XYDataset dataset, int l, int h  )
   {
      JFreeChart lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "Instances","Exchange Rate",
         dataset,
         PlotOrientation.VERTICAL,
         true,true,false);
      ChartPanel chartPanel = new ChartPanel( lineChart );
      final XYPlot plot = lineChart.getXYPlot();
      ValueAxis axis = plot.getRangeAxis();
      axis.setUpperBound(h);
      axis.setLowerBound(l);
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   
   
   public static void main( String[ ] args , XYDataset dataset, int l, int h) 
   {
      
 //     String a = args[0].concat("/INR");
      LineChart chart = new LineChart();
      chart.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      chart.createChart("","", dataset,l,h);
      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}