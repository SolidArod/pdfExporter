package com.simplified.tmddata;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.abs;

public class RobotHandler {
    public static Robot robot;
    public static String appName;
    public static void macro(Main.person person) {
        startPatient();

        String lastName = person.extract("Last Name");
        lastName=lastName.replace("\u00A0","");
        String firstName = person.extract("First Name");
        firstName=firstName.replace("\u00A0","");
        String middleInit = person.extract("Middle Initial");
        middleInit = middleInit.replace("\u00A0","");
        nameBox(lastName, firstName, middleInit);

        String stAddress = person.extract("Street Address");
        stAddress=stAddress.replace("\u00A0","");
        String city = person.extract("City");
        city=city.replace("\u00A0","");
        String state = person.extract("State");
        state = state.replace("\u00A0","");
        String zipcode = person.extract("Zip Code");
        zipcode = zipcode.replace("\u00A0","");
        addressBox(stAddress,city,state,zipcode);

        String gender = person.extract("Legal Sex").toUpperCase().replace(": ","");
        gender=gender.replace("\u00A0","");
        String DoB = person.extract("Date of Birth").replace(": ","");
        DoB=DoB.replace("\u00A0","");
        genderAndDoB(gender,DoB);

        openInsuranceTab();

        String[] insurance = new String[3];
        int insuranceInstance = 1;
        do{
            insurance = person.extractInsurance(insuranceInstance);
            if(insurance[0] != null){
                for (int i = 0; i < insurance.length; i++) {
                    insurance[i] = insurance[i].replace("\u00A0","");
                }
                Insurance(insurance[0],insurance[1],insurance[2],insuranceInstance);
                insuranceInstance++;
            }
        } while (insurance[0] != null);

        finishPatient();

    }

    public static void openAPP(String appPath){
        Matcher match = Pattern.compile("([^/\\\\]+)(?=\\.exe$)").matcher(appPath);
        if(match.find()){
            appName = match.group(1);
        }
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.out.println("AWTException");
        }
        if(!WindowJNAHandler.checkWindowOpen(appName)){
            try {
                Runtime.getRuntime().exec(appPath);
                robot.delay(10000);
            } catch (IOException e) {
                System.out.println("No");
            }
        }
        robot.delay(1000);
        WindowJNAHandler.SwitchWindow(appName);
    }

    public static void startPatient(){
        //open patient info and start new patient
        moveMouseRel(0.600,0.065);
        mouseClick();
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_F8);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_F8);
        robot.delay(3000);
    }

    public static void nameBox(String lastName, String firstName, String middleInit){
        //Name block
        moveMouseRel(0.650,0.1650);
        mouseClick();
        typeString(lastName.toUpperCase());
        typeString(firstName.toUpperCase());
        typeString(middleInit.toUpperCase());
    }

    public static void addressBox(String stAddress, String city, String state, String zipcode){
        //Address block
        moveMouseRel(0.650,0.2800);
        mouseClick();
        typeString(stAddress.toUpperCase());
        moveMouseRel(0.650,0.34);
        mouseClick();
        typeString(city.toUpperCase());
        moveMouseRel(0.725,0.34);
        mouseClick();
        mouseClick();
        typeString(state.toUpperCase());
        moveMouseRel(0.650,0.3625);
        mouseClick();
        typeString(zipcode);
        //moveMouseRel(0.650,0.378);
        //mouseClick();
        //typeString(phone);
    }

    public static void genderAndDoB(String gender, String DoB){
        //Gender and DoB
        moveMouseRel(0.850,0.415);
        mouseClick();
        if(gender.toUpperCase().contains("F")){
            typeString("F");
        } else {
            typeString("M");
        }
        moveMouseRel(0.850,0.455);
        mouseClick();
        mouseClick();
        typeString(DoB);
    }

    public static void openInsuranceTab(){
        //Insurance Information tab
        moveMouseRel(0.675,0.110);
        mouseClick();
    }

    public static void Insurance(String InsuranceCode, String polId, String grpId, int insuranceNo){
        //1st insurance
        double[] xPos = {0.685,0.685,0.785};
        double[] yPos = {0,0,0};
        switch (insuranceNo){
            case 1:
                yPos[0]=0.1640;
                yPos[1]=0.2320;
                yPos[2]=0.2320;
                break;
            case 2:
                yPos[0]=0.360;
                yPos[1]=0.425;
                yPos[2]=0.425;
                break;
            case 3:
                yPos[0]=0.4940;
                yPos[1]=0.555;
                yPos[2]=0.555;
                break;
        }
        moveMouseRel(xPos[0],yPos[0]);
        mouseClick();
        typeString(InsuranceCode.toUpperCase());
        moveMouseRel(xPos[1],yPos[1]);
        mouseClick();
        typeString(polId.toUpperCase());
        moveMouseRel(xPos[2],yPos[2]);
        mouseClick();
        typeString(grpId.toUpperCase());
    }

    public static void finishPatient(){
        robot.keyPress(KeyEvent.VK_F3);
        robot.keyRelease(KeyEvent.VK_F3);
    }


    public static void mouseClick(){
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void mouseDrag(double startRelativeX, double startRelativeY,double endRelativeX, double endRelativeY){
        moveMouseRel(startRelativeX,startRelativeY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        moveMouseRel(endRelativeX,endRelativeY);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void moveMouseRel(double relativeX, double relativeY){
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;


        if(abs(relativeX) <= 1.0 && abs(relativeY) <=1.0) {
            // Calculate absolute coordinates
            int x = (int) (screenWidth * abs(relativeX));
            int y = (int) (screenHeight * abs(relativeY));

            // Move the mouse to the calculated position
            robot.mouseMove(x, y);
        } else {
            System.out.println("Please use numbers between 0 and 1");
        }
    }

    public static void typeString(String stringToType){
        for (char c : stringToType.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }

        // Simulate pressing Enter
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
