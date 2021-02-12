package com.celilreha.sudoku.Model;

public class Sudoku {
    private int id;
    private String knownIds;
    private String puzzleUnsolved;
    private String puzzleSolving;
    private boolean isFinished;

    public Sudoku() {
    }

    public Sudoku(int id, String puzzleUnsolved, String puzzleSolving, boolean isFinished) {
        this.id = id;
        this.puzzleUnsolved = puzzleUnsolved;
        this.puzzleSolving = puzzleSolving;
        this.isFinished = isFinished;

    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKnownIds() {
        return knownIds;
    }

    public void setKnownIds(String knownIds) {
        this.knownIds = knownIds;
    }

    public String getPuzzleUnsolved() {
        return puzzleUnsolved;
    }

    public void setPuzzleUnsolved(String puzzleUnsolved) {
        this.puzzleUnsolved = puzzleUnsolved;
    }

    public String getPuzzleSolving() {
        return puzzleSolving;
    }

    public void setPuzzleSolving(String puzzleSolving) {
        this.puzzleSolving = puzzleSolving;
    }
}
