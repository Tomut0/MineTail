package ru.minat0.minetail.core;

public enum Ranks {
    SS(99999999, 99999999),
    S(100, 15),
    A(75, 10),
    B(50, 5),
    C(25, 3),
    D(10, 1);

    private final int questCompleted;
    private final int magicLevel;

    Ranks(int questCompleted, int magicLevel) {
        this.questCompleted = questCompleted;
        this.magicLevel = magicLevel;
    }

    public int getQuestCompleted() {
        return questCompleted;
    }

    public int getMagicLevel() {
        return magicLevel;
    }
}
