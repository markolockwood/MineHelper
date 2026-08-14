package com.minehelper.client.feature.keybind;

/**
 * Represents a single custom key binding: a key code mapped to a command.
 */
public class KeyBind {
    private int keyCode;
    private String command;

    public KeyBind() {
    }

    public KeyBind(int keyCode, String command) {
        this.keyCode = keyCode;
        this.command = command;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
