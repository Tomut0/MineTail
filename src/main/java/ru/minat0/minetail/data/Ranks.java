package ru.minat0.minetail.data;

public enum Ranks {
    S(100),
    A(75),
    B(50),
    C(25),
    D(10);

    private final int questCompleted;

    Ranks(int questCompleted) {
        this.questCompleted = questCompleted;
    }

    public int getQuestCompleted() {
        return questCompleted;
    }
}
