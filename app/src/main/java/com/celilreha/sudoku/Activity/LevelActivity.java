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
import com.celilreha.sudoku.Adapter.Adapter_Level;
import com.celilreha.sudoku.Helper.DatabaseHelper;
import com.celilreha.sudoku.Model.Difficulty;
import com.celilreha.sudoku.Model.Global;
import com.celilreha.sudoku.Model.Level;
import com.celilreha.sudoku.R;

import java.io.IOException;
import java.util.ArrayList;

public class LevelActivity extends AppCompatActivity {
    ListView listView_Level;
    Difficulty difficulty;
    int difficultyId;
    String difficultyName;
    ArrayList<Level> levels = new ArrayList<>();
    Adapter_Level adapter_level;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        listView_Level = findViewById(R.id.listView_Level);
        difficulty = (Difficulty)getIntent().getSerializableExtra("difficulty");
        difficultyId = ((Global)this.getApplication()).getDifficultyId();
        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
            Log.d("qwwr",((Global)this.getApplication()).getDifficultyId()+"");
            Cursor c = db.rawQuery("SELECT * FROM Difficulty WHERE id="+difficultyId,null);
            c.moveToNext();
            difficultyName = c.getString(c.getColumnIndex("name"));
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setTitle(difficultyName);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
            Cursor c =db.rawQuery("SELECT id,level_id,is_finished,puzzle_unsolved,puzzle_solving FROM Puzzle WHERE difficulty_id="+difficultyId,null);
            while (c.moveToNext()){
                int temp = c.getInt(c.getColumnIndex("is_finished"));
                boolean isFinished;
                if(temp==0)
                    isFinished = false;
                else
                    isFinished = true;
                int id = c.getInt(c.getColumnIndex("level_id"));
                int puzzleId = c.getInt(c.getColumnIndex("id"));
                String puzzleUnsolved=c.getString(c.getColumnIndex("puzzle_unsolved"));
                String puzzleSolving=c.getString(c.getColumnIndex("puzzle_solving"));
                int unsolvedCount = 0;
                int solvingCount = 0;
                for (int i = 0; i<puzzleSolving.length();i++){
                    if (puzzleUnsolved.charAt(i)=='0')
                        unsolvedCount++;
                    if (puzzleSolving.charAt(i)=='0')
                        solvingCount++;
                }
                int status = unsolvedCount - solvingCount;
                levels.add(new Level(id,puzzleId,status,unsolvedCount,isFinished));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter_level = new Adapter_Level(getApplicationContext(),levels);
        listView_Level.setAdapter(adapter_level);
        listView_Level.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),PlayActivity.class);
                intent.putExtra("level",levels.get(position));
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        Intent intent = new Intent(getApplicationContext(),DifficultyActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),DifficultyActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        int levelId = ((Global) this.getApplication()).getLevelId();
        int temp = levelId - 1;
        try {
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
            Cursor c =db.rawQuery("SELECT is_finished,puzzle_unsolved,puzzle_solving FROM Puzzle WHERE difficulty_id="+((Global) this.getApplication()).getDifficultyId()+" and level_id="+levelId,null);
            while (c.moveToNext()){
                int temp2 = c.getInt(c.getColumnIndex("is_finished"));
                boolean isFinished;
                if(temp2==0)
                    isFinished = false;
                else
                    isFinished = true;
                String puzzleUnsolved=c.getString(c.getColumnIndex("puzzle_unsolved"));
                String puzzleSolving=c.getString(c.getColumnIndex("puzzle_solving"));
                int unsolvedCount = 0;
                int solvingCount = 0;
                for (int i = 0; i<puzzleSolving.length();i++){
                    if (puzzleUnsolved.charAt(i)=='0')
                        unsolvedCount++;
                    if (puzzleSolving.charAt(i)=='0')
                        solvingCount++;
                }
                int status = unsolvedCount - solvingCount;
                levels.get(temp).setStatus(status);
                levels.get(temp).setDone(isFinished);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter_level = new Adapter_Level(getApplicationContext(),levels);
        listView_Level.setAdapter(adapter_level);
        super.onRestart();
    }

    @Override
    protected void onPause() {
        ((Global) this.getApplication()).setDifficultyId(difficultyId);
        super.onPause();
    }
}
