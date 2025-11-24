/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FrontEnd;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;


/**
 *
 * @author DELL
 */
public class ChartFrame extends JFrame {
    private String courseTitle;
    private double avgScore;
    private double avgCompletion;
    private Map<String, Double> difficultyMap;
    private Map<String, Double> studentPerformance;

    public ChartFrame(String courseTitle, double avgScore, double avgCompletion, Map<String, Double> difficultyMap, Map<String, Double> studentPerformance) {
        this.courseTitle = courseTitle;
        this.avgScore = avgScore;
        this.avgCompletion = avgCompletion;
        this.difficultyMap = difficultyMap;
        this.studentPerformance = studentPerformance;

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Performance Analytics: " + courseTitle);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Course Averages Chart (Bar)
        tabbedPane.addTab("Averages Summary", createAveragesChartPanel());

        // 2. Student Performance Chart (Bar)
        tabbedPane.addTab("Top Student Performance", createStudentPerformanceChartPanel());

        // 3. Question Difficulty Chart (Pie)
        tabbedPane.addTab("Quiz Difficulty Breakdown", createDifficultyChartPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAveragesChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(avgScore, "Average Score (%)", "Quiz Score");
        dataset.addValue(avgCompletion, "Average Completion (%)", "Lesson Completion");

        JFreeChart barChart = ChartFactory.createBarChart(
            "Course Averages: " + courseTitle,
            "Metric",
            "Percentage (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0); // Set Y-axis from 0 to 100

        return new ChartPanel(barChart);
    }

    private JPanel createStudentPerformanceChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sort and select top 5 students
        studentPerformance.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> dataset.addValue(entry.getValue(), "Average Quiz Score", entry.getKey()));

        JFreeChart barChart = ChartFactory.createBarChart(
            "Top 5 Student Average Scores",
            "Student",
            "Average Score",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
        );
        
        return new ChartPanel(barChart);
    }

    private JPanel createDifficultyChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Classify questions based on correction percentage
        long easyCount = difficultyMap.values().stream().filter(p -> p >= 75.0).count();
        long moderateCount = difficultyMap.values().stream().filter(p -> p >= 50.0 && p < 75.0).count();
        long difficultCount = difficultyMap.values().stream().filter(p -> p < 50.0).count();

        if (easyCount > 0) dataset.setValue("Easy (> 75% Correct)", easyCount);
        if (moderateCount > 0) dataset.setValue("Moderate (50-75% Correct)", moderateCount);
        if (difficultCount > 0) dataset.setValue("Difficult (< 50% Correct)", difficultCount);
        
        if (dataset.getItemCount() == 0) {
            dataset.setValue("No Quiz Data Available", 1);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Question Difficulty Breakdown",
            dataset,
            true, true, false
        );
        
        return new ChartPanel(pieChart);
    }

}
