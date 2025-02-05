package com.simplified.tmddata;

import com.sun.jna.*;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.awt.*;

import static com.sun.jna.platform.win32.WinUser.SW_RESTORE;

public class WindowJNAHandler {
    public interface User32 extends Library {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        boolean SetForegroundWindow(WinDef.HWND hWnd);
        boolean ShowWindow(WinDef.HWND hWnd, int nCmdShow);
        boolean SetWindowPos(WinDef.HWND hWnd, WinDef.HWND hWndInsertAfter, int X, int Y, int cx, int cy, int uFlags);
        int EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer data);
        int GetWindowTextA(WinDef.HWND hWnd, byte[] lpString, int nMaxCount);

    }

    public static boolean checkWindowOpen(String appName){
        User32 user32 = User32.INSTANCE;
        WinDef.HWND targetWindow = findWindowByTitle(user32, appName);

        return targetWindow != null;
    }

    public static void SwitchWindow(String appName) {
        User32 user32 = User32.INSTANCE;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        WinDef.HWND targetWindow = findWindowByTitle(user32, appName);

        if (targetWindow != null) {
            // Set focus to the target window

            boolean foreground= false;
            do{
                foreground= user32.SetForegroundWindow(targetWindow);
                //System.out.println(foreground);
            }while (!foreground);

            user32.ShowWindow(targetWindow, SW_RESTORE);

            moveWindow(targetWindow,screenWidth/2,0,screenWidth/2,screenHeight-40);

        } else {
            System.out.println("Target window not found.");
        }
    }
    public static void moveWindow(WinDef.HWND hwnd, int x, int y, int width, int height) {
        User32 user32 = User32.INSTANCE;

        // Flags:  Use a combination of flags as needed.
        int flags = WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE; // Example: Don't change Z-order or activate

        boolean result = user32.SetWindowPos(hwnd, null, x, y, width, height, flags); // hwndInsertAfter = null for no Z-order change

        if (!result && Native.getLastError() != 0) {
            int error = Native.getLastError();
            System.err.println("SetWindowPos failed: " + error);
            // Handle the error appropriately.  Common errors are invalid HWND or insufficient privileges.
        }
    }

    private static WinDef.HWND findWindowByTitle(User32 user32, String appName) {
        final User32 user = User32.INSTANCE;
        final WinDef.HWND[] result = new WinDef.HWND[1];
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            int count = 0;

            public boolean callback(WinDef.HWND hWnd, Pointer arg1) {
                byte[] windowText = new byte[512];
                user.GetWindowTextA(hWnd, windowText, 512);
                String wText = Native.toString(windowText);

                if (wText.isEmpty()) {
                    return true;
                }

                //System.out.println("Found window with text " + hWnd + ", total " + ++count + " Text: " + wText);
                if(wText.contains(appName+" -")){
                    result[0] = hWnd;
                    return false;
                } else{
                    return true;
                }
            }
        }, null);
        return result[0];
    }
}