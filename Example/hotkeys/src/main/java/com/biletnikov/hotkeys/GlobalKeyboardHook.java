package com.biletnikov.hotkeys;

import java.util.List;
import java.util.ArrayList;
/**
 * Global keyboard hook at the Java side.
 * @author Sergei.Biletnikov
 */
public class GlobalKeyboardHook {

    // ----------- Java Native methods -------------

    /**
     * Checks if the hotkeys were pressed.
     * @return true if they were pressed, otherwise false
     */
    public native boolean checkHotKey();

    /**
     * Sets the hot key.
     * @param virtualKey Specifies the virtual-key code of the hot key.
     * @param alt Either ALT key must be held down.
     * @param control Either CTRL key must be held down.
     * @param shift Either SHIFT key must be held down.
     * @param win Either WINDOWS key was held down. These keys are labeled with the Microsoft Windows logo.
     * Keyboard shortcuts that involve the WINDOWS key are reserved for use by the operating system.
     * @return If the function succeeds, the return value is TRUE.
     */
    public native boolean setHotKey(int virtualKey, boolean alt, boolean control, boolean shift, boolean win);

    /**
     * Resets the installed hotkeys.
     */
    public native void resetHotKey();
    // -------------------------------------

    // GlobalKeyboardHook.dll
    private static final String KEYBOARD_HOOOK_DLL_NAME_32 = "JKeyboardHook32";
    private static final String KEYBOARD_HOOOK_DLL_NAME_64 = "JKeyboardHook64";

    /**
     * For stopping
     */
    private boolean stopFlag;

    // -------- Java listeners --------
    private List<GlobalKeyboardListener> listeners = new ArrayList<GlobalKeyboardListener>();

    static {
        // try to load 32 bit library
        boolean libLoaded = loadNativeLibrary(KEYBOARD_HOOOK_DLL_NAME_32);
        if (!libLoaded) {
            libLoaded = loadNativeLibrary(KEYBOARD_HOOOK_DLL_NAME_64);
        }
        if (!libLoaded) {
            System.err.println("Warning! : No appropriate library was loaded for keyboard hook. " +
                    "Please, check first the location of the library. It must be placed into classpath directory.");
        }

    }

    /**
     * Constructor.
     */
    public GlobalKeyboardHook() {
        // load KeyboardHookDispatcher.dll in the classpath
        stopFlag = false;
    }

    public void addGlobalKeyboardListener(GlobalKeyboardListener listener) {
        listeners.add(listener);
    }

    public void removeGlobalKeyboardListener(GlobalKeyboardListener listener) {
        listeners.remove(listener);
    }

    /**
     * Start the hook. Create and run DLLStateThread thread, for checking the DLL status.
     */
    public void startHook() {
        stopFlag = false;
        DLLStateThread currentWorker = new DLLStateThread();
        Thread statusThread = new Thread(currentWorker);
        statusThread.start();
    }

    /**
     * Finish the current KeyboardThreadWorker instance.
     */
    public void stopHook() {
        stopFlag = true;
    }

    /**
     * Sends the event notification to all listeners.
     */
    private void fireHotkeysEvent() {
        for (GlobalKeyboardListener listener : listeners) {
            listener.onGlobalHotkeysPressed();
        }
    }

    /**
     * This class is base for the thread, which monitors DLL status.
     */
    private class DLLStateThread implements Runnable {

        public void run() {
            for(;;) {
                boolean hotKeyPressed = checkHotKey();
                if (hotKeyPressed) {
                    // hot key was pressed, send the event to all listeners
                    fireHotkeysEvent();
                }
                try {
                    Thread.sleep(100); //every 100 ms check the DLL status.
                    // work unless stopFlag == false
                    if (stopFlag) {
                        resetHotKey();
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Load native library.
     * @param libName library name.
     * @return true, if library is load
     */
    private static boolean loadNativeLibrary (String libName) {
        try {
            System.loadLibrary(libName);
            System.out.println(libName + " library is loaded");
        } catch (Throwable e) {
            return false;
        }
        return true;
    }
}
