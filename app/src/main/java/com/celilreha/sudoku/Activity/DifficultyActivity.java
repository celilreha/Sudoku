package com.celilreha.sudoku.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.celilreha.sudoku.Adapter.Adapter_Difficulty;
import com.celilreha.sudoku.Helper.DatabaseHelper;
import com.celilreha.sudoku.Model.Difficulty;
import com.celilreha.sudoku.Model.Global;
import com.celilreha.sudoku.R;

import java.io.IOException;
import java.util.ArrayList;

public class DifficultyActivity extends AppCompatActivity {

    ListView listView_Difficulty;
    Adapter_Difficulty adapter_difficulty;
    ArrayList<Difficulty> difficulties = new ArrayList<>();
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        listView_Difficulty = findViewById(R.id.listView_Difficulty);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM Difficulty",null);

            while(c.moveToNext()){
                String name = c.getString(c.getColumnIndex("name"));
                int id = c.getInt(c.getColumnIndex("id"));
                Cursor cursor = db.rawQuery("SELECT count(*) FROM Puzzle WHERE difficulty_id="+id,null);
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                cursor.close();
                cursor = db.rawQuery("SELECT is_finished FROM Puzzle WHERE difficulty_id="+id,null);
                int finishedCount=0;
                while (cursor.moveToNext()){
                    int is_finished = cursor.getInt(cursor.getColumnIndex("is_finished"));
                    if (is_finished==1)
                        finishedCount++;
                }
                difficulties.add(new Difficulty(id,count,name,finishedCount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter_difficulty = new Adapter_Difficulty(getApplicationContext(),difficulties);
        listView_Difficulty.setAdapter(adapter_difficulty);
        listView_Difficulty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),LevelActivity.class);
                intent.putExtra("difficulty",difficulties.get(position));
                ((Global)DifficultyActivity.this.getApplication()).setDifficultyId(difficulties.get(position).getId());
                startActivity(intent);

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        int difficultyId = ((Global) this.getApplication()).getDifficultyId();
        int temp = difficultyId - 1;
        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM Difficulty where id="+difficultyId,null);
            while(c.moveToNext()){
                int id = c.getInt(c.getColumnIndex("id"));
                Cursor cursor = db.rawQuery("SELECT count(*) FROM Puzzle WHERE difficulty_id="+id,null);
                cursor.moveToFirst();
                cursor.close();
                cursor = db.rawQuery("SELECT is_finished FROM Puzzle WHERE difficulty_id="+id,null);
                int finishedCount=0;
                while (cursor.moveToNext()){
                    int is_finished = cursor.getInt(cursor.getColumnIndex("is_finished"));
                    if (is_finished==1)
                        finishedCount++;
                }
                difficulties.get(temp).setStatus(finishedCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter_difficulty = new Adapter_Difficulty(getApplicationContext(),difficulties);
        listView_Difficulty.setAdapter(adapter_difficulty);
        super.onRestart();
    }
}
