package com.celilreha.sudoku.Model;

import android.app.Application;

public class Global extends Application {
    private int levelId, difficultyId;
    private boolean isNextEnable;

    public boolean isNextEnable() {
        return isNextEnable;
    }

    public void setNextEnable(boolean nextEnable) {
        isNextEnable = nextEnable;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(int difficultyId) {
        this.difficultyId = difficultyId;
    }
}
