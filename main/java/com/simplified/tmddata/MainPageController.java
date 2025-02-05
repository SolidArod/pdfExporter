package com.simplified.tmddata;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static com.simplified.tmddata.Main.*;

public class MainPageController {
    private FileChooser fileChooser = new FileChooser();
    private PDFDisplayer displayer = new PDFDisplayer();
    private PDFTextStripper pdfStripper;
    private String selectedFile = "";
    private String prevSelectedFile = "";
    private File file = null;
    private static Stage PrefStage;
    private TextArea simpText;

    @FXML
    private ListView filesList;
    @FXML
    private VBox PdfVBoxPane;
    @FXML
    private TextFlow RawPDFTextFlow;
    @FXML
    private TextFlow simplifiedTextFlow;
    @FXML
    private MenuItem openRecentMenuItem;
    @FXML
    private TabPane TextTabPane;

    @FXML
    protected void onAddPdfButtonClick(){
        filesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        file = fileChooser.showOpenDialog(SceneMap.getStage());
        if (file != null) {
            if(file.getName().contains(".pdf")) {
                try {
                    String filepath = file.getPath();
                    Main.addFile(filepath);
                    prevSelectedFile = selectedFile;
                    selectedFile = file.getName();
                    filesList.getItems().add(file.getName());
                    displayer.loadPDF(file);
                    displayer.setVisibilityOf("toolbarContainer", false);
                    displayer.setVisibilityOf("sidebarContainer", false);
                    Parent parent = displayer.toNode();
                    VBox.setVgrow(parent, Priority.ALWAYS);
                    PdfVBoxPane.getChildren().set(1, parent);

                    pdfStripper = new PDFTextStripper();
                    String parsedText = pdfStripper.getText(Loader.loadPDF(file));
                    parsedText.replaceAll("[^A-Za-z0-9. ]+", "");
                    RawPDFTextFlow.getChildren().set(0, new Text(parsedText));

                    openRecentMenuItem.setText(prevSelectedFile);
                    TextTabChanged();
                } catch (Exception e) {
                    System.out.println(e.toString());
                    System.out.println("Max number of files in program");
                }
            }
        }
    }

    @FXML
    protected void onOpenRecentMenuItemClick(){
        String selectedItem = openRecentMenuItem.getText();
        //System.out.println(selectedItem);
        if (selectedItem != null){
            setMain(selectedItem);
            openRecentMenuItem.setText(prevSelectedFile);
        }
    }

    @FXML
    protected void onListItemClick(){
        String selectedItem = (String) filesList.getSelectionModel().getSelectedItem();
        //System.out.println(selectedItem);
        if (selectedItem != null){
            setMain(selectedItem);
            openRecentMenuItem.setText(prevSelectedFile);
        }
    }

    @FXML
    protected void onPrefClick(){
        try {
            PrefPhraseController loadsettings =  new PrefPhraseController();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("perfPhrase.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            PrefStage = new Stage();
            PrefStage.setTitle("Preferences");
            PrefStage.setScene(scene);
            PrefStage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    protected void onDeleteFileClick(){
        String selectedItem = (String) filesList.getSelectionModel().getSelectedItem();
        while (selectedItem != null) {
            int selectedItemIndex = filesList.getSelectionModel().getSelectedIndex();
            if (selectedItem != null) {
                if (selectedItem.equals(prevSelectedFile)){
                    openRecentMenuItem.setText("");
                    prevSelectedFile = "";
                } else if (selectedItem.equals(selectedFile)) {
                    selectedFile = "";
                }
                filesList.getItems().remove(selectedItemIndex);
                Main.removeFile(selectedItem);

                Pane emptyWeb = new Pane();
                PdfVBoxPane.getChildren().set(1, emptyWeb);
                RawPDFTextFlow.getChildren().set(0, new Text());
            }
            selectedItem = (String) filesList.getSelectionModel().getSelectedItem();
        }

    }

    @FXML
    protected void onSelectAllClick(){
        filesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filesList.getSelectionModel().selectAll();
    }

    @FXML
    protected void onUnselectAllClick(){
        filesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filesList.getSelectionModel().clearSelection();
    }

    @FXML
    protected void TextTabChanged(){
        if(TextTabPane.getSelectionModel().getSelectedItem().getText().equals("Simplified Text")){
            simplifyText();
        }
    }

    @FXML
    protected void onExportClick(){
        if(ExportCSVFlag && !dataExtract.isEmpty()){
            try {
                FileWriter fileWriter = new FileWriter(ExportCSV, false);
                fileWriter.write(simpText.getText());
            } catch (IOException e) {
                System.out.println("Error writing to CSV");
            }
        }
        if(ExportAppFlag && !dataExtract.isEmpty()){
            RobotHandler.openAPP(appPath.getAbsolutePath());
            for (person person:dataExtract){
                RobotHandler.macro(person);
            }
        }
    }

    protected void simplifyText(){
        if(searchTerms.isEmpty() &&  filesList.getItems().isEmpty()){
            simplifiedTextFlow.getChildren().set(0,new Text("Please load PDF and add preferences to settings"));
        } else if (filesList.getItems().isEmpty()) {
            simplifiedTextFlow.getChildren().set(0,new Text("Please load PDF"));
        } else if (searchTerms.isEmpty()) {
            simplifiedTextFlow.getChildren().set(0,new Text("Add preferences to settings"));
        } else if (dataExtract.isEmpty()){
            simpText = new TextArea();
            boolean header = true;
            ObservableList<Node> nodes =  RawPDFTextFlow.getChildren();
            StringBuilder sb = new StringBuilder();
            for (Node node : nodes) { sb.append((((Text)node).getText()));  }
            String txt = sb.toString();
            Scanner scan = new Scanner(txt);

            simplifiedTextFlow.getChildren().set(0,new Text(simpText.getText()));

            if(scan.hasNextLine()) {
                person person= new person(searchTerms,refineTerms,replaceTerms);
                StringBuilder concat =  new StringBuilder(scan.nextLine());
                int linecount = 1;
                while (scan.hasNextLine()) {
                    for (DataEntry searchItem : person.searchTerms) {
                        if(!(person.tempSearch.contains(searchItem))) {
                            while (!(concat.toString().contains(searchItem.start)) || !(concat.toString().contains(searchItem.end))){
                                concat.append("\n");
                                if(scan.hasNextLine()){
                                    linecount++;
                                    concat.append(scan.nextLine());
                                } else {
                                    break;
                                }
                                if(linecount > 5 && !(concat.toString().contains(searchItem.start)) ){
                                    linecount = 1;
                                    concat =  new StringBuilder(scan.nextLine());
                                }
                            }
                            try {
                                linecount = 0;
                                String substring = concat.substring(concat.indexOf(searchItem.start), concat.indexOf(searchItem.end)).trim();
                                person.addResult(substring, searchItem);
                            } catch (Exception e) {
                                break;
                            }

                        }

                        if(!(person.searchTerms.size() > person.resultTerms.size())){
                            concat =  new StringBuilder(scan.nextLine());
                            if(!(person.refineTerms.isEmpty())){
                                person.secondPass();
                            }
                            dataExtract.add(person);
                            if(!(person.finalTerms.isEmpty())) {
                                if (header) {
                                    for (Final data : person.finalTerms) {
                                        if (data.dataName.contains("\\n")) {
                                            simpText.appendText("City/State/Zip, ");
                                        } else {
                                            simpText.appendText(data.dataName + "; ");
                                        }
                                    }
                                    header = false;
                                }
                            }
                            simpText.appendText("\n");
                            for (Final data : person.finalTerms) {
                                simpText.appendText(data.data + "; ");
                            }
                            person= new person(searchTerms,refineTerms,replaceTerms);
                        }
                    }
                }
            }

            simplifiedTextFlow.getChildren().set(0,new Text(simpText.getText()));

        }
    }

    protected File getFile(String filename){
        return file = new File(Main.getFile(filename));
    }

    public static void closePref(){
        PrefStage.close();
    }

    protected void setMain(String selectedItem){
        if(!(selectedFile.equals(selectedItem))){
            file = getFile(selectedItem);
            try {
                displayer.loadPDF(file);
                displayer.setVisibilityOf("toolbarContainer", false);
                displayer.setVisibilityOf("sidebarContainer", false);
                Parent parent = displayer.toNode();
                PdfVBoxPane.getChildren().set(1,parent);

                pdfStripper = new PDFTextStripper();
                String parsedText = pdfStripper.getText(Loader.loadPDF(file));
                System.out.println(parsedText.replaceAll("[^A-Za-z0-9. ]+", ""));
                RawPDFTextFlow.getChildren().set(0,new Text(parsedText));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            prevSelectedFile = selectedFile;
            selectedFile = selectedItem;
        }
    }
}
