package com.aprior.apriori_ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main extends Application {

    FrequentItemsetData<String> items = null;

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root,1200, 700);

        stage.setTitle("Apriori!");
        stage.setScene(scene);
        stage.show();

        Text apriori = new Text("THE APRIORI ALGORITHM");
        headingUI(apriori,2.2,550,40);

        Text definition = new Text("Finding Frequent Itemsets Using Candidate Generation");
        headingUI(definition,1.4,480,90);

        Label trns = new Label("Input Transaction of items:");
        TextArea transactions = new TextArea();
        trns.setLabelFor(transactions);
        transactions.setPromptText("Enter the transactions, press enter for next transaction, click submit after completing");
        textAreaUI(transactions,trns,50,170,70,140,300,300);

        Label minSup = new Label("Enter minimum support count: ");
        TextField min_sup = new TextField();
        minSup.setLabelFor(min_sup);
        textfieldUI(min_sup,minSup,50,520,70,490,300,40);

        Label output = new Label("Frequent Itemsets");
        TextArea outputSets = new TextArea();
        output.setLabelFor(outputSets);
        textAreaUI(outputSets,output,400,170,420,140,300,490);

        Label inputConfidence = new Label("Input frequent itemset:");
        TextField inCon = new TextField();
        inputConfidence.setLabelFor(inCon);
        textfieldUI(inCon,inputConfidence,750,170,770,140,300,40);
        inputConfidence.setVisible(false);
        inCon.setVisible(false);

        Label outConf = new Label("Association rules with confidence ");
        TextArea outputConf = new TextArea();
        textAreaUI(outputConf,outConf,750,330,770,300,300,330);
        outConf.setVisible(false);
        outputConf.setVisible(false);

        Button checkConfidence = new Button("Check Confidence");
        buttonUI(checkConfidence,830, 230);
        checkConfidence.setVisible(false);

        Button reset = new Button("Reset");
        buttonUI(reset,200,600);


        Button Enter = new Button("Submit");
        buttonUI(Enter,80, 600);
        Enter.setOnAction(e->{
            inputConfidence.setVisible(true);
            inCon.setVisible(true);
            outConf.setVisible(true);
            outputConf.setVisible(true);
            checkConfidence.setVisible(true);
            double minSupCount = Double.parseDouble(min_sup.getText());
            PrintWriter pw = null;
            try {
                pw = new PrintWriter("UserTransactions.txt");
                BufferedWriter bw = new BufferedWriter(pw);
                bw.write(transactions.getText());
                bw.close();
                take_generate(outputSets, minSupCount);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });

        checkConfidence.setOnAction(e->{
            if(inCon.getText()==""){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Enter an itemset!");
                alert.show();
            }
            else{
                String inputfreqSet = inCon.getText();
                System.out.println(inputfreqSet);
                String[] inputs = inputfreqSet.split(",");
                outputConf.setText(null);
                confidence(inputs, outputConf);
            }
        });

        reset.setOnAction(e->{
            transactions.setText(null);
            min_sup.setText(null);
            outputSets.setText(null);
            inCon.setText(null);
            outputConf.setText(null);
            inputConfidence.setVisible(false);
            inCon.setVisible(false);
            outConf.setVisible(false);
            outputConf.setVisible(false);
            checkConfidence.setVisible(false);
        });

        root.getChildren().addAll(trns, transactions, Enter, apriori,min_sup,minSup, output, outputSets,
                inputConfidence,inCon, outConf, outputConf, checkConfidence, definition, reset);
    }

    public void take_generate(TextArea output, double minSupCount) throws FileNotFoundException {
        double min_sup = minSupCount;

        Apriori_Generator generator = new Apriori_Generator();

        TakeTransactions take = new TakeTransactions();

        List<Set<String>> itemsetList = take.Take("UserTransactions.txt");

        items = generator.generate(itemsetList,min_sup);

        int i=1;

        output.appendText("");
        for(Set<String> itemset: items.getFrequentItemsetList()){
            output.appendText(i+": " + itemset + " , support: " + items.getSupport(itemset) + "\n");
            System.out.println(i+": " + itemset + " , support: " + items.getSupport(itemset));
            i++;
        }
    }

    public void confidence(String[] itemset, TextArea output){

        Set<String> checkCon = new HashSet<>(Arrays.asList(itemset));
        FrequentItemsetData<String> frequentItems = items;

        boolean checkExists = false;
        for(Set<String> items: frequentItems.getFrequentItemsetList()){
            if(items.containsAll(checkCon)){
                checkExists = true;
                break;
            }
        }

        if(checkExists) {
            for (Set<String> items : frequentItems.getFrequentItemsetList()) {
                if (checkCon.containsAll(items) && !items.containsAll(checkCon)) {
                    int confidence = (int) ((frequentItems.getSupport(checkCon) / frequentItems.getSupport(items)) * 100);
                    for (String itemInd : items) {
                        System.out.print(itemInd + " ");
                        output.appendText(itemInd + " ");
                    }
                    System.out.print("=> ");
                    output.appendText("=> ");
                    for (String checkConInd : checkCon) {
                        if (!items.contains(checkConInd)) {
                            System.out.print(checkConInd + " ");
                            output.appendText(checkConInd + " ");
                        }
                    }
                    System.out.println("" +
                            "Confidence = " + frequentItems.getSupport(checkCon) + "/" +
                            frequentItems.getSupport(items) + "= " + confidence + "%");
                    output.appendText("Confidence = " + frequentItems.getSupport(checkCon) + "/" +
                            frequentItems.getSupport(items) + "= " + confidence + "%\n");
                }
            }
        }
        else{
            output.appendText("This is not a frequent itemlist!");
        }

    }

    private void headingUI(Text apriori, double s, int x, int y){
        apriori.setTranslateX(x);
        apriori.setTranslateY(y);
        apriori.setScaleX(s);
        apriori.setScaleY(s);
        apriori.setFill(Color.BLACK);
    }


    private void textfieldUI(TextField tf, Label label, int xTf, int yTf, int xL, int yL, int prefX, int prefY){
        label.setTranslateX(xL);
        label.setTranslateY(yL);
        label.setScaleX(1.2);
        label.setScaleY(1.2);
        tf.setTranslateX(xTf);
        tf.setTranslateY(yTf);
        tf.setPrefSize(prefX,prefY);
        tf.setFocusTraversable(false);
        tf.setStyle("-fx-font-size: 16");
    }

    private void textAreaUI(TextArea ta, Label label, int xTa, int yTa, int xL, int yL, int prefX, int prefY){
        label.setTranslateX(xL);
        label.setTranslateY(yL);
        label.setScaleX(1.2);
        label.setScaleY(1.2);
        ta.setTranslateX(xTa);
        ta.setTranslateY(yTa);
        ta.setPrefSize(prefX,prefY);
        ta.setFocusTraversable(false);
        ta.setStyle("-fx-font-size: 16");
    }

    private void buttonUI(Button Enter, int x, int y){
        Enter.setTranslateX(x);
        Enter.setTranslateY(y);
        Enter.setMinSize(100,40);
        Enter.setStyle("-fx-background-color: \n" +
                "        #c3c4c4,\n" +
                "        linear-gradient(#d6d6d6 50%, white 100%),\n" +
                "        radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);\n" +
                "    -fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0,1,1;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );");
    }

    public static void main(String[] args) throws FileNotFoundException {

        launch();

    }
}
