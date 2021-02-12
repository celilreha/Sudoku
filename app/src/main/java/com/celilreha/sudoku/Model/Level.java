package com.celilreha.sudoku.Model;

import java.io.Serializable;

public class Level implements Serializable {
    private int id, puzzleId, status,blankCount;
    private boolean isDone;

    public Level(int id, int puzzleId, int status, int blankCount, boolean isDone) {
        this.id = id;
        this.puzzleId = puzzleId;
        this.status = status;
        this.blankCount = blankCount;
        this.isDone = isDone;
    }

    public Level() {
    }

    public int getBlankCount() {
        return blankCount;
    }

    public void setBlankCount(int blankCount) {
        this.blankCount = blankCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(int puzzleId) {
        this.puzzleId = puzzleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
