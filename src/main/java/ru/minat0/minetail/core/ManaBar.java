package ru.minat0.minetail.core;

public enum ManaBar {

    FOREVER(-1),
    SHORT(3),
    MEDIUM(5),
    LONG(10);

    private final int appearTime;

    ManaBar(int seconds) {
        this.appearTime = seconds;
    }

    public int getTime() {
        return appearTime;
    }
}
