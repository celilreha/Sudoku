package com.celilreha.sudoku.Model;

import java.io.Serializable;

public class Difficulty implements Serializable {
    private int id;
    private int count;
    private String name;
    private  int status;


    public Difficulty(int id, int count, String name, int status) {
        this.id = id;
        this.count = count;
        this.name = name;
        this.status = status;
    }

    public Difficulty() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
