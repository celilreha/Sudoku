package com.celilreha.sudoku.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.celilreha.sudoku.Model.Difficulty;
import com.celilreha.sudoku.R;

import java.util.ArrayList;



public class Adapter_Difficulty extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Difficulty> difficulties;

    public Adapter_Difficulty() {
    }

    public Adapter_Difficulty(Context context, ArrayList<Difficulty> difficulties) {
        this.context = context;
        this.difficulties = difficulties;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {return difficulties.size();}

    @Override
    public Object getItem(int position) {return difficulties.get(position);}

    @Override
    public long getItemId(int position) {return difficulties.get(position).getId();}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.difficulty_row,null);

        TextView tvDifficulty = view.findViewById(R.id.tvDifficulty);
        TextView tvDifficulty_Count = view.findViewById(R.id.tvDifficulty_Count);
        ProgressBar progressBar_Difficulty = view.findViewById(R.id.progressBar_Difficulty);

        tvDifficulty.setText(difficulties.get(position).getName());
        tvDifficulty_Count.setText(difficulties.get(position).getStatus()+"/"+difficulties.get(position).getCount()+" Sudoku");
        progressBar_Difficulty.setMax(difficulties.get(position).getCount());
        progressBar_Difficulty.setProgress(difficulties.get(position).getStatus());

        return view;
    }
}
