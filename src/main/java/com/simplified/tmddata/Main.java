package com.simplified.tmddata;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main extends Application {
    public static class DataEntry{
        public String start;
        public String end;

        public DataEntry(String start, String end){
            this.start = start;
            this.end = end;

        }

        public String toString(){
            return start + "; " + end;
        }
    }

    public static class RefinedDataEntry{
        public String searchTerm;
        public String dataTypeName;
        public String delimiter;

        public RefinedDataEntry(String searchTerm, String dataTypeName, String delimiter){
            this.searchTerm = searchTerm;
            this.dataTypeName = dataTypeName;
            this.delimiter = delimiter;

        }

        public String toString(){
            return searchTerm + "; " + dataTypeName + "; " + delimiter;
        }
    }

    public static class Replacer{
        public String start;
        public String end;

        public Replacer(String start, String end){
            this.start = start;
            this.end = end;

        }

        public String toString(){
            return start + "->" + end;
        }
    }

    public static class Final{
        public String dataName;
        public String data;

        public Final(String dataName, String data){
            this.dataName = dataName;
            this.data = data;

        }

        public String toString(){
            return dataName + ": " + data;
        }
    }

    public static class person{
        public ObservableList<DataEntry> searchTerms;
        public ObservableList<DataEntry> tempSearch = FXCollections.observableArrayList();
        public ObservableList<RefinedDataEntry> refineTerms;
        public ObservableList<Replacer> replaceTerms;
        public ObservableList<String> resultTerms = FXCollections.observableArrayList();
        public ObservableList<Final> finalTerms = FXCollections.observableArrayList();

        public person(ObservableList<DataEntry> searchTerms, ObservableList<RefinedDataEntry> refineTerms,ObservableList<Replacer> replaceTerms){
            this.searchTerms =searchTerms;
            this.refineTerms =refineTerms;
            this.replaceTerms = replaceTerms;
        }

        public String toString(){
            TextArea text = new TextArea();
            if(finalTerms.isEmpty()) {
                for (String term : resultTerms) {
                    text.appendText(term + "; ");
                }
            } else {
                for (Final term : finalTerms) {
                    text.appendText(term.toString() + "; ");
                }
            }
            return text.getText();
        }

        public void addResult(String data, DataEntry removeFromTemp){
            resultTerms.add(data);
            tempSearch.add(removeFromTemp);
        }

        public void secondPass(){
            for (String result : resultTerms) {
                ArrayList<String> dataDelimiters = new ArrayList<>();
                ArrayList<String> dataNames = new ArrayList<>();
                String[] multipleLines;
                String removeSearchTerm = result;
                String matchingSearch = "";
                Final finalData;
                for(RefinedDataEntry term:refineTerms){
                        if (result.contains(term.searchTerm)) {
                            if(term.dataTypeName.charAt(term.dataTypeName.length()-1)==':'){
                                term.dataTypeName=term.dataTypeName.substring(0, term.dataTypeName.length()-1);
                            }
                            if (removeSearchTerm.contains(term.searchTerm)) {
                                matchingSearch=term.searchTerm;
                                removeSearchTerm = removeSearchTerm.substring(term.searchTerm.length(), result.length());
                                if(removeSearchTerm.charAt(0)=='\n' || removeSearchTerm.charAt(0)==','){
                                    removeSearchTerm = removeSearchTerm.substring(1);
                                }
                            }
                            if (term.delimiter.isEmpty()) {
                                dataNames.add(term.dataTypeName);
                                dataDelimiters.add("\n");
                            } else {
                                dataNames.add(term.dataTypeName);
                                dataDelimiters.add(term.delimiter);
                            }
                        }
                }
                if(removeSearchTerm.contains("\n")){
                    multipleLines=  removeSearchTerm.split("\n");
                    for (int i = 0; i < multipleLines.length; i++) {
                        if(matchingSearch.contains("Address")){
                            finalData =  new Final(dataNames.get(0), multipleLines[0]);
                            i++;
                            finalTerms.add(finalData);

                            String patternString = String.join("|", dataDelimiters);
                            Pattern pattern = Pattern.compile(patternString);

                            String[] resultDelimited = pattern.split(multipleLines[i]);
                            resultDelimited = squish(resultDelimited,dataNames.size()-1);
                            for (int j = dataNames.size()-2; j >= 0; j--) {
                                try{
                                    resultDelimited[j] = replace(resultDelimited[j]);
                                    finalData =  new Final(dataNames.get(j+1), resultDelimited[j]);
                                } catch (Exception e) {
                                    finalData =  new Final(dataNames.get(j), "");
                                }
                                finalTerms.add(finalData);
                            }

                        } else if (matchingSearch.contains("Insurance")) {
                            String patternString = String.join("|", dataDelimiters);
                            Pattern pattern = Pattern.compile(patternString);

                            String[] resultDelimited = pattern.split(multipleLines[i]);
                            for (int j = 0; j < dataNames.size(); j++) {
                                try{
                                    if(containsChar(resultDelimited[j], (char) -96) && j >0){
                                        resultDelimited[j] = resultDelimited[j].replace((char) -96, '\0');
                                    }
                                    resultDelimited[j] = replace(resultDelimited[j]);
                                    finalData =  new Final(dataNames.get(j), resultDelimited[j]);
                                } catch (Exception e) {
                                    finalData =  new Final(dataNames.get(j), "");
                                }
                                finalTerms.add(finalData);
                            }
                        }
                    }
                } else if(dataDelimiters.isEmpty()){
                    for(DataEntry search:searchTerms){
                        if(removeSearchTerm.contains(search.start)){
                            removeSearchTerm = removeSearchTerm.substring(search.start.length(),result.length());
                            if(removeSearchTerm.charAt(removeSearchTerm.length()-1)==','){
                                removeSearchTerm = removeSearchTerm.substring(0, removeSearchTerm.length()-2);
                            }
                            if(search.start.charAt(search.start.length()-1)==':'){
                                search.start=search.start.substring(0, search.start.length()-1);
                            }
                            removeSearchTerm = replace(removeSearchTerm);
                            finalData =  new Final(search.start, removeSearchTerm);
                            finalTerms.add(finalData);
                            break;
                        }
                    }
                }else{
                    String patternString = String.join("|", dataDelimiters);
                    Pattern pattern = Pattern.compile(patternString);

                    String[] resultDelimited = pattern.split(removeSearchTerm);

                    for (int i = 0; i < dataNames.size(); i++) {
                        try{
                            if(resultDelimited[i].charAt(resultDelimited[i].length()-1)==','){
                                resultDelimited[i] = resultDelimited[i].substring(0, resultDelimited[i].length()-1);
                            }
                            resultDelimited[i] = replace(resultDelimited[i]);
                            finalData =  new Final(dataNames.get(i), resultDelimited[i]);
                        } catch (Exception e) {
                            finalData =  new Final(dataNames.get(i), "");
                        }
                        finalTerms.add(finalData);
                    }

                }
            }
        }

        public String replace(String stringToReplace){
            for(Replacer termToReplace: replaceTerms){
                if(stringToReplace.contains(termToReplace.start)){
                    return termToReplace.end;
                }

            }
            return stringToReplace;
        }
        public String extract(String dataName){
            for (Final dataNames:finalTerms){
                if(dataNames.dataName.toUpperCase().contains(dataName.toUpperCase())){
                    return dataNames.data;
                }
            }
            return "";
        }

        public String[] extractInsurance(int insureNo){
            String[] result = new String[3];
            int countNameInstance =0;
            int countPolInstance =0;
            int countGrpInstance =0;
            for (Final dataNames:finalTerms){
                if(dataNames.dataName.toUpperCase().contains("INSURANCE NAME")){
                    countNameInstance++;
                    if(countNameInstance == insureNo){
                        result[0]=dataNames.data;
                    }
                }
                if(dataNames.dataName.toUpperCase().contains("INSURANCE POLICY NUMBER")){
                    countPolInstance++;
                    if(countPolInstance == insureNo){
                        result[1]=dataNames.data;
                    }
                }
                if(dataNames.dataName.toUpperCase().contains("INSURANCE GROUP NUMBER")){
                    countGrpInstance++;
                    if(countGrpInstance == insureNo){
                        result[2]=dataNames.data;
                    }
                }
            }
            return result;
        }
    }

    private static String[] files = new String[100];
    private static int filesLength = 0;
    public static File ExportCSV = new File("ExportCSV.csv");
    public static File Settings = new File("Settings.txt");
    public static File appPath;
    public static boolean ExportCSVFlag = false;
    public static boolean ExportAppFlag = false;
    public static ObservableList<DataEntry> searchTerms = FXCollections.observableArrayList();
    public static ObservableList<RefinedDataEntry> refineTerms = FXCollections.observableArrayList();
    public static ObservableList<Replacer> replaceTerms = FXCollections.observableArrayList();
    public static ObservableList<person> dataExtract = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws IOException {
        SceneMap sceneMap = new SceneMap(stage);

        //FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("SplashScreen.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(),320,240);
        //sceneMap.addScene("splash",scene);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Startup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),1200,900);

        SceneMap.addScene("data",scene);


        stage.setTitle("data");
        SceneMap.setScenes("data");
        stage.show();

    }

    public static void addFile(String filepath) throws Exception {
        for (int i = 0; i < files.length; i++) {
            if (files[i] == null){
                filesLength++;
                files[i] = filepath;
                return;
            }
        }
        throw new Exception("Files are full");
    }

    public static String getFile(String filename){
        for (int i = 0; i < filesLength; i++) {
            if (files[i].contains(filename)){
                return files[i];
            }
        }
        return "";
    }

    public static void removeFile(String filename){
        int runCount = 0;
        if(filesLength == 1){
            files[0]= null;
            filesLength--;
        } else {
            for (int i = 0; i < filesLength; i++) {
                if (files[i].contains(filename) && files[i] != null && runCount < 1) {
                    // shifting elements
                    for (int k = i; k <= filesLength - 1; k++) {
                        files[k] = files[k + 1];
                    }
                    filesLength--;
                    runCount++;
                    i--;
                }
            }
        }
    }

    public static String[] squish(String[] bigArray, int size){
        if(!(size >= bigArray.length)) {
            String[] smallerResultDelimited = new String[size];
            StringBuilder sb = new StringBuilder();
            int reverseIndex = bigArray.length - 1;
            int index = 0;
            while (bigArray.length > size - 2) {
                if (reverseIndex >= size - 1) {
                    smallerResultDelimited[(size - 1) - (index)] = bigArray[reverseIndex];
                } else if (reverseIndex > -1) {
                    if (sb.length() == 0) {
                        sb.insert(0, bigArray[reverseIndex]);
                    } else {
                        sb.insert(0, " ");
                        sb.insert(0, bigArray[reverseIndex]);
                    }
                } else {
                    smallerResultDelimited[0] = sb.toString();
                    bigArray = smallerResultDelimited;
                    return smallerResultDelimited;

                }
                reverseIndex--;
                index++;
            }
            return null;
        } else{
            return bigArray;
        }
    }

    public static boolean containsChar(String str, char c) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch();
    }


}
