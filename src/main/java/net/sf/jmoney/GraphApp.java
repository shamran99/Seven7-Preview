package net.sf.jmoney;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphApp extends Application {

    private static final Logger logger = LogManager.getLogger(GraphApp.class);
    private static XYChart.Series series = new XYChart.Series();
    private static volatile boolean javaFxLaunched = false;

    public void resetSeries() {
        logger.info("series reset");
        series = new XYChart.Series();
    }

    public void addValue(String xAxis, long yAxis) {
        double doubleVal = Double.parseDouble(Currency.getCurrencyForCode("GBP").format(yAxis).replaceAll(",", ""));
        logger.info("Adding values to XYChart. xAxis {} yAxis {}", xAxis, doubleVal);
        XYChart.Data data = new XYChart.Data(xAxis, doubleVal);
        series.getData().add(data);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Amounts");
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Months (Last date)");
        //creating the chart
        final LineChart<String, Number> lineChart =
                new LineChart<String, Number>(xAxis, yAxis);

        lineChart.setTitle("Account Amounts");

        series.setName("Account Amounts");

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * The following method will execute if only on first launch.
     * Because launch method can be executed only once.
     *
     * @param applicationClass
     */
    public static void appLaunch(Class<? extends Application> applicationClass) {
        if (!javaFxLaunched) {
            logger.info("First launch thread will be created...");
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(applicationClass)).start();
            javaFxLaunched = true;
            logger.info("First launch thread created. Application launched.");
        } else {
            Platform.runLater(() -> {
                try {
                    logger.info("Next launch threads will be created...");
                    Application application = applicationClass.newInstance();
                    Stage primaryStage = new Stage();
                    application.start(primaryStage);
                    logger.info("Next launch threads created.");
                } catch (Exception e) {
                    logger.error("Caught exception on Platform.run later. Details: {}", e);
                }
            });
        }
    }
}

