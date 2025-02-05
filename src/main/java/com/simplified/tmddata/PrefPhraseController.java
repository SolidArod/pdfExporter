package com.simplified.tmddata;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static com.simplified.tmddata.Main.*;

public class PrefPhraseController {
    private int confirmSave = 0;

    @FXML
    private CheckBox ExportToCSVToggle;
    @FXML
    private CheckBox ExportToAppToggle;
    @FXML
    private CheckBox LineEndCheckbox;
    @FXML
    private ListView FirstDataSweepListView;
    @FXML
    private ListView refinedDataSweepListView;
    @FXML
    private ListView replaceDataListView;
    @FXML
    private TextField ExportToCSVFileNameTextField;
    @FXML
    private Label AppPathLabel;
    @FXML
    private TextField AppPathTextField;
    @FXML
    private TextField startPhraseTextField;
    @FXML
    private TextField endPhraseTextField;
    @FXML
    private TextField searchPhraseTextField;
    @FXML
    private TextField delimiterTextField;
    @FXML
    private TextField dataTypeTextField;
    @FXML
    private TextField phraseToReplaceTextField;
    @FXML
    private TextField replacePhraseResultTextField;
    @FXML
    private Label ErrorTextLabel;
    @FXML
    private Button PathToAppButton;
    @FXML
    private TextArea SaveSettingsTextField;
    @FXML
    private TextArea LoadSettingsTextField;
    @FXML
    private Label SaveWarningLabel;

    @FXML
    protected void onExportToCSVToggleClick(){
        ErrorTextLabel.setVisible(false);
        ExportCSVFlag = ExportToCSVToggle.isSelected();
        ExportToCSVFileNameTextField.setVisible(ExportToCSVToggle.isSelected());
        ExportToCSVFileNameTextField.setText(ExportCSV.getAbsolutePath());

    }

    @FXML
    protected void onExportToAppToggleClick(){
        ErrorTextLabel.setVisible(false);
        ExportAppFlag = ExportToAppToggle.isSelected();
        PathToAppButton.setVisible(ExportToAppToggle.isSelected());
        AppPathLabel.setVisible(ExportToAppToggle.isSelected());
        AppPathTextField.setVisible(ExportToAppToggle.isSelected());

    }

    @FXML
    protected void onPathToAppClick(){
        ErrorTextLabel.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        do {
            Stage temp = new Stage();
            appPath = fileChooser.showOpenDialog(temp);
        } while (!(appPath.getPath().contains(".exe")));
        AppPathTextField.setText(appPath.getAbsolutePath());
    }

    @FXML
    protected void onAppPathTextFieldClick(){
        ErrorTextLabel.setVisible(false);

    }

    @FXML
    protected void onRemoveButtonDESClick(){
        ErrorTextLabel.setVisible(false);
        String selectedItem = FirstDataSweepListView.getSelectionModel().getSelectedItem().toString();
        int selectedIndex = FirstDataSweepListView.getSelectionModel().getSelectedIndex();
        FirstDataSweepListView.getItems().remove(selectedIndex);
        searchTerms.remove(selectedIndex);

    }

    @FXML
    protected void onRemoveButtonRDESClick(){
        ErrorTextLabel.setVisible(false);
        String selectedItem = refinedDataSweepListView.getSelectionModel().getSelectedItem().toString();
        int selectedIndex = refinedDataSweepListView.getSelectionModel().getSelectedIndex();
        refinedDataSweepListView.getItems().remove(selectedIndex);
        refineTerms.remove(selectedIndex);
    }

    @FXML
    protected void onRemoveButtonDRClick(){
        ErrorTextLabel.setVisible(false);
        String selectedItem = replaceDataListView.getSelectionModel().getSelectedItem().toString();
        int selectedIndex = replaceDataListView.getSelectionModel().getSelectedIndex();
        replaceDataListView.getItems().remove(selectedIndex);
        replaceTerms.remove(selectedIndex);
    }

    @FXML
    protected void onAddButtonDESClick(){
        FirstDataSweepListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ErrorTextLabel.setVisible(false);
        DataEntry dataEntry = null;
        if(!(startPhraseTextField.getText().isEmpty()) && !(endPhraseTextField.getText().isEmpty())){
            dataEntry = new DataEntry(startPhraseTextField.getText(),endPhraseTextField.getText());
        } else{
            printError("Fill phrase data");
        }

        if (dataEntry != null){
            searchTerms.add(dataEntry);
            FirstDataSweepListView.getItems().add(dataEntry.toString());
        }
    }

    @FXML
    protected void onAddButtonRDESClick(){
        refinedDataSweepListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ErrorTextLabel.setVisible(false);
        RefinedDataEntry refinedDataEntry = null;
        if(!(searchPhraseTextField.getText().isEmpty()) && !(dataTypeTextField.getText().isEmpty()) && !(delimiterTextField.getText().isEmpty())){
            refinedDataEntry = new RefinedDataEntry(searchPhraseTextField.getText(),dataTypeTextField.getText(),delimiterTextField.getText());
        } else if(!(searchPhraseTextField.getText().isEmpty()) && !(dataTypeTextField.getText().isEmpty()) && LineEndCheckbox.isSelected()){
            refinedDataEntry = new RefinedDataEntry(searchPhraseTextField.getText(),dataTypeTextField.getText(),"");
        } else{
            printError("Fill refined data text fields");
        }

        if (refinedDataEntry != null){
            refineTerms.add(refinedDataEntry);
            refinedDataSweepListView.getItems().add(refinedDataEntry.toString());
        }
    }

    @FXML
    protected void onAddButtonDRClick(){
        replaceDataListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ErrorTextLabel.setVisible(false);
        Replacer replacer = null;
        if(!(phraseToReplaceTextField.getText().isEmpty()) && !(replacePhraseResultTextField.getText().isEmpty())){
            replacer = new Replacer(phraseToReplaceTextField.getText(),replacePhraseResultTextField.getText());
        }  else{
            printError("Please fill replacer text fields");
        }

        if (replacer != null){
            replaceTerms.add(replacer);
            replaceDataListView.getItems().add(replacer.toString());
        }
    }

    @FXML
    protected void onLineEndCheckboxClick(){
        ErrorTextLabel.setVisible(false);
        if(LineEndCheckbox.isSelected()){
            delimiterTextField.setText("");
            delimiterTextField.setDisable(true);
        } else {
            delimiterTextField.setDisable(false);
        }
    }

    @FXML
    protected void onPerfCancelButtonClick(){
        ErrorTextLabel.setVisible(false);
        MainPageController.closePref();
    }


    @FXML
    protected void onPerfConfirmButtonClick(){
        ErrorTextLabel.setVisible(false);
        if(ExportToCSVToggle.isSelected() && !(ExportToCSVFileNameTextField.getText().equals(ExportCSV.getAbsolutePath()))){
            if (isValidPath(ExportToCSVFileNameTextField.getText())){
                renameFile(ExportCSV.getAbsolutePath(),ExportToCSVFileNameTextField.getText(),ExportCSV);
            }else {
                printError("Export To CSV PATH NOT VALID");
            }

        } else if (ExportToAppToggle.isSelected() && !(AppPathTextField.getText().equals(appPath.getAbsolutePath()))){
            if (isValidPath(AppPathTextField.getText()) && (AppPathTextField.getText().contains(".exe"))){
                renameFile(appPath.getAbsolutePath(),AppPathTextField.getText(),appPath);
            }else {
                printError("Export To App PATH NOT VALID");
            }
        } else if (!(SaveSettingsTextField.getText().equals(Settings.getAbsolutePath()))) {
            confirmSave = 1;
            onSaveSettingsButtonClick();
        }
        MainPageController.closePref();
    }

    @FXML
    protected void onSaveSettingsTileClick(){
        ErrorTextLabel.setVisible(false);
        SaveWarningLabel.setVisible(false);
        SaveSettingsTextField.setText(Settings.getAbsolutePath());
        LoadSettingsTextField.setText(Settings.getAbsolutePath());

    }

    @FXML
    protected void onSaveSettingsButtonClick(){
        ErrorTextLabel.setVisible(false);
        SaveWarningLabel.setVisible(false);

        try{
            Scanner fileReader = new Scanner(Settings);
            if(!(fileReader.hasNextLine())){
                confirmSave = 1;
            }
        } catch (FileNotFoundException e) {
            printError("Save: File not found");
        }

        switch (confirmSave){
            case 0:
                SaveWarningLabel.setVisible(true);
                confirmSave = 1;
                return;
            case 1:
                if (!(SaveSettingsTextField.getText().equals(Settings.getAbsolutePath()))) {
                    if (isValidPath(SaveSettingsTextField.getText()) && (SaveSettingsTextField.getText().contains(".txt"))) {
                        renameFile(Settings.getAbsolutePath(), SaveSettingsTextField.getText(), Settings);
                        LoadSettingsTextField.setText(Settings.getAbsolutePath());
                    } else {
                        printError("Settings PATH NOT VALID");
                    }
                }

                saveSettings();
                confirmSave = 0;
        }

    }


    @FXML
    protected void onLoadSettingsButtonClick(){
        ErrorTextLabel.setVisible(false);
        if (!(LoadSettingsTextField.getText().equals(Settings.getAbsolutePath()))) {
            if (isValidPath(LoadSettingsTextField.getText()) && (LoadSettingsTextField.getText().contains(".txt"))) {
                renameFile(Settings.getAbsolutePath(),LoadSettingsTextField.getText(),Settings);
            } else {
                printError("Settings PATH NOT VALID");
            }
        }

        loadSettings();
    }

    @FXML
    void initialize(){
        onSaveSettingsTileClick();
        onLoadSettingsButtonClick();
    }

    private void saveSettings() {
        try {
            TextArea textToExport = new TextArea();
            FileWriter fileWriter = new FileWriter(Settings, false);
            textToExport.appendText("Settings for Preference page: \n");
            textToExport.appendText("------------------------------Export Settings:------------------------------ \n");
            textToExport.appendText("                              -Export to CSV-                                \n");
            textToExport.appendText("Checked: \n");
            textToExport.appendText(ExportToCSVToggle.isSelected()+"\n");
            textToExport.appendText("CSV filepath and name: \n");
            textToExport.appendText(ExportCSV.getAbsolutePath()+"\n");
            textToExport.appendText("                              -Export to App-                                \n");
            textToExport.appendText("Checked: \n");
            textToExport.appendText(ExportToAppToggle.isSelected()+"\n");
            textToExport.appendText("App filepath: \n");
            if(appPath == null){
                textToExport.appendText("null");
            } else {
                textToExport.appendText(appPath.getAbsolutePath()+"\n");
            }
            textToExport.appendText("----------------------------Data Export Settings:---------------------------- \n");
            textToExport.appendText("                             -First Data Sweep-                               \n");
            textToExport.appendText("Search terms: \n");
            searchTerms.forEach((dataEntry) -> {
                try {
                    textToExport.appendText(dataEntry.toString()+"\n");
                } catch (Exception ex) {
                    printError("Error occurred while writing data entry to save file");
                }
            });
            textToExport.appendText("                            -Refined Data Sweep-                             \n");
            textToExport.appendText("Data terms: \n");
            refineTerms.forEach((dataEntry) -> {
                try {
                    textToExport.appendText(dataEntry.toString()+"\n");
                } catch (Exception ex) {
                    printError("Error occurred while writing data entry to save file");
                }
            });
            textToExport.appendText("                              -Data Replace-                                \n");
            textToExport.appendText("Replace Phrases \n");
            replaceTerms.forEach((dataEntry) -> {
                try {
                    textToExport.appendText(dataEntry.toString()+"\n");
                } catch (Exception ex) {
                    printError("Error occurred while writing data entry to save file");
                }
            });
            textToExport.appendText("---------------------------Save Settings to File:--------------------------- \n");
            textToExport.appendText("File path and name: \n");
            textToExport.appendText(Settings.getAbsolutePath()+"\n");

            fileWriter.write(textToExport.getText());

            fileWriter.close();
        } catch (IOException e) {
            printError("Save settings failed");
        }
    }

    public void loadSettings() {
        FirstDataSweepListView.getItems().clear();
        searchTerms = FXCollections.observableArrayList();
        refinedDataSweepListView.getItems().clear();
        refineTerms = FXCollections.observableArrayList();
        replaceDataListView.getItems().clear();
        replaceTerms = FXCollections.observableArrayList();
        try {
            Scanner fileReader = new Scanner(Settings);

            if(!(fileReader.hasNextLine())){
                printError("Settings File empty");
                return;
            }

            fileReader.nextLine(); //Settings for Preference page:
            fileReader.nextLine(); //------------------------------Export Settings:


            fileReader.nextLine(); //                              -Export to CSV-
            fileReader.nextLine(); //Checked:
            boolean CSVsetting = fileReader.nextBoolean(); //true or false
            fileReader.nextLine(); //pass the bool
            ExportCSVFlag = CSVsetting;
            ExportToCSVToggle.selectedProperty().set(CSVsetting);
            ExportToCSVFileNameTextField.setVisible(CSVsetting);

            fileReader.nextLine(); //CSV filepath and name:
            String CSVpathLoad = fileReader.nextLine(); //CSV path and name
            ExportToCSVFileNameTextField.setText(CSVpathLoad);
            renameFile(ExportCSV.getAbsolutePath(),CSVpathLoad,ExportCSV);


            fileReader.nextLine(); //                              -Export to App-
            fileReader.nextLine(); //Checked:
            boolean APPsetting = fileReader.nextBoolean(); //true or false
            fileReader.nextLine(); //pass the bool
            ExportAppFlag = APPsetting;
            ExportToAppToggle.selectedProperty().set(APPsetting);
            PathToAppButton.setVisible(APPsetting);
            AppPathLabel.setVisible(APPsetting);
            AppPathTextField.setVisible(APPsetting);

            fileReader.nextLine(); //App filepath:
            String APPpathLoad = fileReader.nextLine(); //Actual app path
            AppPathTextField.setText(APPpathLoad);
            if(!(APPpathLoad.equals("null"))) {
                if (appPath == null) {
                    appPath = new File(APPpathLoad);
                } else {
                    renameFile(appPath.getAbsolutePath(), APPpathLoad, appPath);
                }
            }

            fileReader.nextLine(); //----------------------------Data Export Settings:
            fileReader.nextLine(); //                             First Data Sweep
            fileReader.nextLine(); //Search terms:
            while(fileReader.hasNextLine()){
                String data = fileReader.nextLine();
                if(data.contains("-Refined Data Sweep-")){
                    break;
                }
                String[] dataDelimit = data.split("; ");
                DataEntry dataEntry = new DataEntry(dataDelimit[0],dataDelimit[1]);
                if (dataEntry != null){
                    searchTerms.add(dataEntry);
                    FirstDataSweepListView.getItems().add(dataEntry.toString());
                }
            }

            fileReader.nextLine(); //Data terms
            while(fileReader.hasNextLine()){
                String data = fileReader.nextLine();
                if(data.contains("-Data Replace-")){
                    break;
                }
                String[] dataDelimit = data.split("; ");
                RefinedDataEntry refinedDataEntry;
                try {
                    refinedDataEntry = new RefinedDataEntry(dataDelimit[0],dataDelimit[1], dataDelimit[2]);
                } catch (Exception e) {
                    refinedDataEntry = new RefinedDataEntry(dataDelimit[0],dataDelimit[1], "");
                }
                if (refinedDataEntry != null){
                    refineTerms.add(refinedDataEntry);
                    refinedDataSweepListView.getItems().add(refinedDataEntry.toString());
                }
            }

            fileReader.nextLine(); //Replace Phrases
            while(fileReader.hasNextLine()){
                String data = fileReader.nextLine();
                if(data.contains("-Save Settings to File:-")){
                    break;
                }
                String[] dataDelimit = data.split("->");
                Replacer replacer = new Replacer(dataDelimit[0],dataDelimit[1]);
                if (replacer != null){
                    replaceTerms.add(replacer);
                    replaceDataListView.getItems().add(replacer.toString());
                }
            }

            fileReader.nextLine(); //File path and name:
            String SettingspathLoad = fileReader.nextLine(); //CSV path and name
            renameFile(Settings.getAbsolutePath(),SettingspathLoad,Settings);
            onSaveSettingsTileClick();

            fileReader.close();
        } catch (Exception e) {
            System.out.println("Load settings failed");
        }
    }

    public void printError(String error){
        System.out.println(error);
        ErrorTextLabel.setText(error);
        ErrorTextLabel.setVisible(true);
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    public void renameFile(String oldPathString, String newPathString, File fileChanged){
        File rename = new File(newPathString);
        Path oldPath = Paths.get(oldPathString);
        Path newPath = Paths.get(newPathString);
        try {
            Files.move(oldPath, oldPath.resolveSibling(newPath));
            fileChanged.renameTo(rename);
            fileChanged = new File(rename.getName());
        } catch (IOException e) {
            printError("rename file error");
        }
    }
}
