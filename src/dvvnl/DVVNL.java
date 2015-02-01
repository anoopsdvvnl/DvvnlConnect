/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dvvnl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Anoop Singh
 */
public class DVVNL extends Application {

    long pcStartTime;
    long upRelianceCounter;
    long upAirtelCounter;
    long downRelianceCounter;
    long downAirtelCounter;
    String defaultGateway;

    @Override
    public void start(Stage primaryStage) {
        setDefaultGateway();
        pcStartTime = new Date().getTime();
        Thread checkRouterThread = new Thread(checkRouterTask);
        checkRouterThread.setDaemon(true);
        checkRouterThread.start();
        Thread checkServerThread = new Thread(checkServerTask);
        checkServerThread.setDaemon(true);
        checkServerThread.start();

        StackPane root = new StackPane();
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 600, 600);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        //grid.add(scenetitle, 0, 0, 2, 1);

        Label myComputer = new Label("My Computer ");
        grid.add(myComputer, 0, 1);
        Text rConnected = new Text("<=> Connected to <=>");
        rConnected.textProperty().bind(checkRouterTask.messageProperty());
        rConnected.setFont(Font.font("Tahoma", FontWeight.MEDIUM, 15));
        grid.add(rConnected, 2, 1, 1, 1);
        Text router = new Text("Router");
        router.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 20));
        grid.add(router, 3, 1, 1, 1);
        Text sConnected = new Text("<=> Connected to <=>");
        sConnected.setFont(Font.font("Tahoma", FontWeight.MEDIUM, 15));
        sConnected.textProperty().bind(checkServerTask.messageProperty());
        grid.add(sConnected, 4, 1, 1, 1);
        Text server = new Text("Server");
        server.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 20));
        grid.add(server, 5, 1, 1, 1);

        primaryStage.setScene(scene);
        primaryStage.setTitle("DVVNL");

        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    Task<Integer> checkRouterTask = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            System.out.println("Task Started");
            while (true) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                   // System.out.println("2");

                // System.out.println("3");
                if (isReachableByPing(defaultGateway)) {
                    System.out.println("Connected");
                    updateMessage("<=> Connected to <=>");
                } else {
                    System.out.println("DISconnected");
                    updateMessage("<= Not Connected =>");
                }

                    //Block the thread for a short time, but be sure
                //to check the InterruptedException for cancellation
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                }
            }
            // return iterations;
            return null;
        }
    };
    Task<Integer> checkServerTask = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            System.out.println("Task Started");
            while (true) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                   // System.out.println("2");

                // System.out.println("3");
                if (isReachableByPing("www.google.com")) {
                    System.out.println("Connected");
                    updateMessage("<=> Connected to <=>");
                } else {
                    System.out.println("DISconnected");
                    updateMessage("<= Not Connected =>");
                }

                    //Block the thread for a short time, but be sure
                //to check the InterruptedException for cancellation
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                }
            }
            // return iterations;
            return null;
        }
    };

    public static String getStatus(String url) throws IOException {

        String result = "";
        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code == 200) {
                result = "Green";
            }
        } catch (Exception e) {
            result = "->Red<-";
        }
        return result;
    }

    public boolean isReachableByPing(String host) {
        try {
            String cmd = "";
            if (System.getProperty("os.name").startsWith("Windows")) {
                // For Windows
                cmd = "ping -n 1 " + host;
            } else {
                // For Linux and OSX
                cmd = "ping -c 1 " + host;
            }

            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();

            if (myProcess.exitValue() == 0) {

                return true;
            } else {

                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public void setDefaultGateway() {
        try {
            Process result = Runtime.getRuntime().exec("ipconfig");

            BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()));

            String line = output.readLine();
            while (line != null) {
                String text = line.trim();
                if (text.startsWith("Default")) {
                    String temp = (String) text.subSequence(text.indexOf(":") + 1, text.length());
                    defaultGateway = temp.trim();
                    //  System.out.println(defaultGateway);
                    break;
                    // System.out.println("TRUE");
                } else {
                    line = output.readLine();
                }

            }
        } catch (IOException ex) {
            System.err.println("Error");
            Logger.getLogger(DVVNL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
