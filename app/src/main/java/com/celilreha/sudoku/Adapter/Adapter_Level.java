package com.celilreha.sudoku.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.celilreha.sudoku.Model.Difficulty;
import com.celilreha.sudoku.Model.Level;
import com.celilreha.sudoku.R;

import java.util.ArrayList;

public class Adapter_Level extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Level> levels;
    Difficulty difficulty;

    public Adapter_Level() {
    }

    public Adapter_Level(Context context, ArrayList<Level> levels) {
        this.context = context;
        this.levels = levels;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {return levels.size();}

    @Override
    public Object getItem(int position) {return levels.get(position);}

    @Override
    public long getItemId(int position) {return levels.get(position).getId();}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.level_row,null);
        TextView tvLevel = view.findViewById(R.id.tvLevel);
        TextView tvLevel_Finish = view.findViewById(R.id.tvLevel_Finish);
        ImageView ivLevel = view.findViewById(R.id.ivLevel);
        ProgressBar progressBar_Level = view.findViewById(R.id.progressBar_Level);

        tvLevel.setText("Puzzle "+levels.get(position).getId());
        ivLevel.setImageResource(R.drawable.check);
        progressBar_Level.setMax(levels.get(position).getBlankCount());
        progressBar_Level.setProgress(levels.get(position).getStatus());
        if (levels.get(position).isDone()){
            tvLevel_Finish.setText("Finished");
            ivLevel.setVisibility(View.VISIBLE);
        }else{
            tvLevel_Finish.setText("Unfinished");
            ivLevel.setVisibility(View.GONE);
        }

        return view;
    }
}
