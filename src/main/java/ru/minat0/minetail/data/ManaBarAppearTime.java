package ru.minat0.minetail.data;

public enum ManaBarAppearTime {

    FOREVER(-1),
    SHORT(3),
    MEDIUM(5),
    LONG(10);

    private final int appearTime;

    ManaBarAppearTime(int seconds) {
        this.appearTime = seconds;
    }

    public int getTime() {
        return appearTime;
    }
}
