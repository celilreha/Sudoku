package com.celilreha.sudoku.Model;

public class Undo {
    private int Tv_id;
    private String text;
    private Boolean noteState;

    public Undo() {
    }

    public Undo(int tv_id, String text, Boolean noteState) {
        Tv_id = tv_id;
        this.text = text;
        this.noteState = noteState;
    }

    public int getTv_id() {
        return Tv_id;
    }

    public void setTv_id(int tv_id) {
        Tv_id = tv_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getNoteState() {
        return noteState;
    }

    public void setNoteState(Boolean noteState) {
        this.noteState = noteState;
    }
}

