package com.celilreha.sudoku.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.celilreha.sudoku.Helper.DatabaseHelper;
import com.celilreha.sudoku.Model.Global;
import com.celilreha.sudoku.Model.Level;
import com.celilreha.sudoku.Model.Sudoku;
import com.celilreha.sudoku.Model.Undo;
import com.celilreha.sudoku.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayActivity extends AppCompatActivity {
    Level level;
    LinearLayout linearLayout_Play;
    int[] btn_ids = new int[12];
    int[][] tv_ids = new int[9][9];
    int[] knownTvIds;
    int currentTvId=0;
    int numbForChanges;
    Sudoku s;
    int levelId;
    int difficultyId;
    boolean isNoteActive=false;
    String puzzleSolving;
    ArrayList<Undo> last3Move=new ArrayList<>();
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public boolean isValidSudoku(String sudoku) {
        char [][] board = new char[9][9];
        for (int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = sudoku.charAt((i*9)+j);
            }
        }
        // check each column
        for (int i = 0; i < 9; i++) {
            boolean[] m = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    if (m[(int) (board[i][j] - '1')]) {
                        return false;
                    }
                    m[(int) (board[i][j] - '1')] = true;
                }
            }
        }
        //check each row
        for (int j = 0; j < 9; j++) {
            boolean[] m = new boolean[9];
            for (int i = 0; i < 9; i++) {
                if (board[i][j] != '.') {
                    if (m[(int) (board[i][j] - '1')]) {
                        return false;
                    }
                    m[(int) (board[i][j] - '1')] = true;
                }
            }
        }
        //check each 3*3 matrix
        for (int block = 0; block < 9; block++) {
            boolean[] m = new boolean[9];
            for (int i = block / 3 * 3; i < block / 3 * 3 + 3; i++) {
                for (int j = block % 3 * 3; j < block % 3 * 3 + 3; j++) {
                    if (board[i][j] != '.') {
                        if (m[(int) (board[i][j] - '1')]) {
                            return false;
                        }
                        m[(int) (board[i][j] - '1')] = true;
                    }
                }
            }
        }
        return true;
    }
    public void clearTvBackground(){
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                TextView tv = findViewById(tv_ids[i][j]);
                tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }
    public boolean isKnown(int n){
        for (int i=0;i<knownTvIds.length;i++){
            if (n==knownTvIds[i]){
                return true;
            }
        }
        return false;
    }
    public String sortNote(String note){
        note=note.replaceAll(" ","");
        int[] num = new int[note.length()];
        for (int i = 0; i < note.length(); i++){
            num[i] = Character.getNumericValue(note.charAt(i));
        }
        Arrays.sort(num);
        note="";
        for (int i = 0; i < num.length; i++){
            note+=num[i]+"";
        }
        note=note.replaceAll("([1-9])\\1+","$1");
        int[] num2 = new int[note.length()];
        for (int i = 0; i < note.length(); i++){
            num2[i] = Character.getNumericValue(note.charAt(i));
        }
        note="";
        for (int i = 0; i < num2.length; i++){
            note+=num2[i]+" ";
        }
        return note;
    }
    public void writeNote(String note){
            TextView textView = findViewById(currentTvId);
            textView.setText(note);
            textView.setTextSize(9);
            textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            textView.setGravity(Gravity.LEFT|Gravity.TOP);
    }
    public void findSameNumbers(){
        if (currentTvId!=0) {
            TextView currentTv = findViewById(currentTvId);
            String tempTv = currentTv.getText().toString();
            if (!tempTv.equals("")) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        TextView tv = findViewById(tv_ids[i][j]);
                        String temp = tv.getText().toString();
                        if (temp.equals(tempTv)) {
                            if(currentTvId==tv_ids[i][j]){
                                tv.setBackgroundColor(Color.parseColor("#888888"));
                                continue;
                            }
                            tv.setBackgroundColor(Color.parseColor("#6D8AE2"));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        linearLayout_Play = findViewById(R.id.linearLayout_Play);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        difficultyId = ((Global) PlayActivity.this.getApplication()).getDifficultyId();
        if (((Global)PlayActivity.this.getApplication()).isNextEnable()){
            ((Global)PlayActivity.this.getApplication()).setNextEnable(false);
            try {
                databaseHelper = new DatabaseHelper(getApplicationContext());
                db = databaseHelper.getReadableDatabase();
                levelId = ((Global) PlayActivity.this.getApplication()).getLevelId();
                Cursor c = db.rawQuery("SELECT * FROM Puzzle where level_id=" + levelId + " and difficulty_id=" + difficultyId, null);
                c.moveToNext();
                int id = c.getInt(c.getColumnIndex("id"));
                String puzzleUnsolved = c.getString(c.getColumnIndex("puzzle_unsolved"));
                String puzzleSolving = c.getString(c.getColumnIndex("puzzle_solving"));
                int status = c.getInt(c.getColumnIndex("is_finished"));
                boolean isFinished = false;
                if (status == 1)
                    isFinished = true;
                char[] arr = new char[puzzleUnsolved.length()];
                for (int i = 0; i < arr.length; i++) {
                    if (puzzleUnsolved.charAt(i) == '0')
                        arr[i] = '0';
                    else
                        arr[i] = '1';
                }
                s = new Sudoku(id, puzzleUnsolved, puzzleSolving, isFinished);
                s.setKnownIds(new String(arr));
                this.setTitle("Puzzle " + levelId);
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            level = (Level) getIntent().getSerializableExtra("level");
            this.setTitle("Puzzle " + level.getId());
            try {
                databaseHelper = new DatabaseHelper(getApplicationContext());
                db = databaseHelper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT * FROM Puzzle where id=" + level.getPuzzleId(), null);
                c.moveToNext();
                int id = c.getInt(c.getColumnIndex("id"));
                levelId = c.getInt(c.getColumnIndex("level_id"));
                Log.d("qwer","asd "+levelId);
                String puzzleUnsolved = c.getString(c.getColumnIndex("puzzle_unsolved"));
                String puzzleSolving = c.getString(c.getColumnIndex("puzzle_solving"));
                int status = c.getInt(c.getColumnIndex("is_finished"));
                boolean isFinished = false;
                if (status == 1)
                    isFinished = true;
                char[] arr = new char[puzzleUnsolved.length()];
                for (int i = 0; i < arr.length; i++) {
                    if (puzzleUnsolved.charAt(i) == '0')
                        arr[i] = '0';
                    else
                        arr[i] = '1';
                }
                s = new Sudoku(id, puzzleUnsolved, puzzleSolving, isFinished);
                s.setKnownIds(new String(arr));
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String puzzleUnsolved=s.getPuzzleUnsolved();
        puzzleSolving =s.getPuzzleSolving();
        int knownIdCount=s.getKnownIds().length() - s.getKnownIds().replace("1", "").length();
        knownTvIds=new int[knownIdCount];
        char[] puzzleUnsolvedArray =puzzleUnsolved.toCharArray();
        final char[] puzzleSolvingArray = puzzleSolving.toCharArray();
        int count=0;
        int countForKnown=0;
        for (int i=0;i<9;i++){
            LinearLayout linearLayout=new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setBackgroundColor(Color.parseColor("#000000"));
            if (i==0){
                linearLayout.setPadding(3, 3, 3, 1);
            }else if(i==8){
                linearLayout.setPadding(3, 0, 3, 3);
            }else{
                linearLayout.setPadding(3, 0, 3, 1);
            }
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_Play.addView(linearLayout);
            for (int j=0;j<9;j++) {
                final TextView valueTV = new TextView(getApplicationContext());
                int id= R.id.tvSudoku+Integer.parseInt((i)+""+(j));
                valueTV.setId(id);
                if(puzzleUnsolvedArray[count]=='0'){
                    if (puzzleSolvingArray[count]=='0')
                        valueTV.setText("");
                    else
                        valueTV.setText(puzzleSolvingArray[count]+"");

                    valueTV.setTextColor(Color.parseColor("#0000FF"));
                }else{
                    valueTV.setText(puzzleUnsolvedArray[count]+"");
                    valueTV.setTextColor(Color.parseColor("#000000"));
                    knownTvIds[countForKnown]=valueTV.getId();
                    countForKnown++;
                }
                count++;
                tv_ids[i][j]=valueTV.getId();
                valueTV.setPadding(3,3,3,3);
                valueTV.setTextSize(24);
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setBackgroundColor(Color.parseColor("#FFFFFF"));
                LinearLayout.LayoutParams layoutParams1 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1);
                if ((j==2||j==5)&&(i==2||i==5)){
                    layoutParams1.setMargins(0,0,3,3);
                }else if ((j==8)&&(i==2||i==5)){
                    layoutParams1.setMargins(0,0,0,3);
                }else if (j==2||j==5){
                    layoutParams1.setMargins(0,0,3,0);
                }else if ((i==2||i==5) && j<8){
                    layoutParams1.setMargins(0,0,1,3);
                }else if (j<8){
                    layoutParams1.setMargins(0,0,1,0);
                }
                valueTV.setLayoutParams(layoutParams1);
                linearLayout.addView(valueTV);
            }
        }
        count=0;
        for (int i=0;i<2;i++){
            LinearLayout linearLayout1=new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout1.setPadding(0,10,0,10);
            linearLayout1.setLayoutParams(layoutParams1);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_Play.addView(linearLayout1);
            for (int j=0;j<6;j++){
                if (i==1 & j==3){
                    Button button = new Button(getApplicationContext());
                    int id = R.id.btn_Sudoku+(count+1);
                    button.setText("clear");
                    button.setAllCaps(false);
                    button.setId(id);
                    LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
                    layoutParams2.setMargins(0,0,10,0);
                    button.setLayoutParams(layoutParams2);
                    button.setBackgroundResource(R.drawable.button_bg);
                    btn_ids[count]=button.getId();
                    linearLayout1.addView(button);
                    count++;
                }else if (i==1 & j==4){
                    Button button = new Button(getApplicationContext());
                    int id = R.id.btn_Sudoku+(count+1);
                    button.setText("note");
                    button.setAllCaps(false);
                    button.setId(id);
                    //button.setTextColor(getResources().getColor(R.color.white));
                    LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
                    layoutParams2.setMargins(0,0,10,0);
                    button.setLayoutParams(layoutParams2);
                    button.setBackgroundResource(R.drawable.button_bg);
                    btn_ids[count]=button.getId();
                    linearLayout1.addView(button);
                    count++;
                }else if (i==1 & j==5){
                    Button button = new Button(getApplicationContext());
                    int id = R.id.btn_Sudoku+(count+5);
                    button.setText("undo");
                    button.setAllCaps(false);
                    button.setId(id);
                    //button.setTextColor(getResources().getColor(R.color.white));
                    LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
                    button.setLayoutParams(layoutParams2);
                    button.setBackgroundResource(R.drawable.button_bg);
                    btn_ids[count]=button.getId();
                    linearLayout1.addView(button);
                    count++;
                }else {
                    Button button = new Button(getApplicationContext());
                    int id = R.id.btn_Sudoku+(count+1);
                    button.setText(""+(count+1));
                    button.setTextSize(24);
                    button.setTextColor(Color.parseColor("#000000"));
                    button.setId(id);
                    LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
                    button.setBackgroundResource(R.drawable.button_bg);
                    btn_ids[count]=button.getId();
                    if (j!=5){
                        layoutParams2.setMargins(0,0,10,0);
                    }
                    button.setLayoutParams(layoutParams2);
                    linearLayout1.addView(button);
                    count++;
                }
            }
        }
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++) {
                final TextView tv = findViewById(tv_ids[i][j]);
                final int finalI = i;
                final int finalJ = j;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNoteActive)
                            Toast.makeText(getApplicationContext(), "Note Mode is Disable", Toast.LENGTH_SHORT).show();
                        isNoteActive=false;
                        clearTvBackground();
                        currentTvId=tv_ids[finalI][finalJ];
                        findSameNumbers();
                        tv.setBackgroundColor(Color.parseColor("#888888"));

                    }
                });
            }
        }
        for (int i=0;i<12;i++){
            Button button=findViewById(btn_ids[i]);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentTvId!=0) {
                        if (!isKnown(currentTvId)) {
                            TextView tv = findViewById(currentTvId);
                            int t=currentTvId-R.id.tvSudoku;
                            int getI=(t%100)/10;
                            int getJ=t%10;
                            numbForChanges = getI*9+getJ;
                            if (finalI == 9) {//clear button
                                if (isNoteActive)
                                    Toast.makeText(getApplicationContext(), "Note Mode is Disable", Toast.LENGTH_SHORT).show();
                                isNoteActive=false;
                                String temp=tv.getText().toString();
                                tv.setText("");
                                tv.setTextSize(24);
                                puzzleSolvingArray[numbForChanges]='0';
                                puzzleSolving = String.valueOf(puzzleSolvingArray);
                                clearTvBackground();
                                tv.setBackgroundColor(Color.parseColor("#888888"));
                                if(last3Move.size()==0) {
                                    last3Move.add(new Undo(currentTvId, temp, isNoteActive));
                                }else if(last3Move.size()==1) {
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }else if (last3Move.size()==2){
                                    last3Move.add(2,last3Move.get(1));
                                    last3Move.remove(1);
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }else{
                                    last3Move.remove(2);
                                    last3Move.add(2,last3Move.get(1));
                                    last3Move.remove(1);
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }
                            }else if (finalI == 10) {//note button
                                if (!isNoteActive)
                                    Toast.makeText(getApplicationContext(), "Note Mode is Enable", Toast.LENGTH_SHORT).show();
                                isNoteActive=true;
                                clearTvBackground();
                                findSameNumbers();
                                tv.setBackgroundColor(Color.parseColor("#888888"));
                            }else if (finalI == 11) {//undo button
                                if (last3Move.size()==0){
                                    Toast.makeText(getApplicationContext(), "No right to undo", Toast.LENGTH_SHORT).show();
                                }else{
                                    currentTvId=last3Move.get(0).getTv_id();
                                    TextView tvUndo=findViewById(currentTvId);
                                    if (last3Move.get(0).getNoteState()){
                                        String note=last3Move.get(0).getText();
                                        note=sortNote(note);
                                        writeNote(note);
                                        clearTvBackground();
                                        tvUndo.setBackgroundColor(Color.parseColor("#888888"));
                                        isNoteActive=true;
                                    }else{
                                        tvUndo.setText(last3Move.get(0).getText());
                                        tvUndo.setTextSize(24);
                                        tvUndo.setGravity(Gravity.CENTER);
                                        clearTvBackground();
                                        findSameNumbers();
                                        tvUndo.setBackgroundColor(Color.parseColor("#888888"));
                                        if (isNoteActive)
                                            Toast.makeText(getApplicationContext(), "Note Mode is Disable", Toast.LENGTH_SHORT).show();
                                        isNoteActive=false;
                                    }
                                    if (last3Move.size()==1) {
                                        last3Move.clear();
                                    }else if(last3Move.size()==2){
                                        Undo u=last3Move.get(1);
                                        last3Move.clear();
                                        last3Move.add(u);
                                    }else{
                                        Undo u=last3Move.get(1);
                                        Undo u2=last3Move.get(2);
                                        last3Move.clear();
                                        last3Move.add(u);
                                        last3Move.add(u2);
                                    }
                                }
                            } else {
                                String temp=tv.getText().toString();
                                if (isNoteActive){
                                    clearTvBackground();
                                    String note=tv.getText().toString();
                                    note+="" + (finalI + 1);
                                    note=sortNote(note);
                                    writeNote(note);
                                    tv.setBackgroundColor(Color.parseColor("#888888"));
                                }else {
                                    tv.setText("" + (finalI + 1));
                                    tv.setTextSize(24);
                                    tv.setGravity(Gravity.CENTER);
                                    clearTvBackground();
                                    findSameNumbers();
                                    tv.setBackgroundColor(Color.parseColor("#888888"));
                                }
                                if(last3Move.size()==0) {
                                    last3Move.add(new Undo(currentTvId, temp, isNoteActive));
                                }else if(last3Move.size()==1) {
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }else if (last3Move.size()==2){
                                    last3Move.add(2,last3Move.get(1));
                                    last3Move.remove(1);
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }else{
                                    last3Move.remove(2);
                                    last3Move.add(2,last3Move.get(1));
                                    last3Move.remove(1);
                                    last3Move.add(1,last3Move.get(0));
                                    last3Move.remove(0);
                                    last3Move.add(0,new Undo(currentTvId, temp, isNoteActive));
                                }
                            }
                            if(tv.getText().toString().equals(""))
                                puzzleSolvingArray[numbForChanges]='0';
                            else
                                puzzleSolvingArray[numbForChanges]=tv.getText().toString().charAt(0);
                            puzzleSolving = String.valueOf(puzzleSolvingArray);
                            try {
                                databaseHelper = new DatabaseHelper(getApplicationContext());
                                db = databaseHelper.getReadableDatabase();
                                db.execSQL("UPDATE Puzzle SET puzzle_solving='"+ puzzleSolving +"'  WHERE id="+s.getId());
                                if(puzzleSolving.contains("0"))
                                    db.execSQL("UPDATE Puzzle SET is_finished=0 WHERE id=" + s.getId());
                                else{
                                    if (isValidSudoku(puzzleSolving)){
                                        db.execSQL("UPDATE Puzzle SET is_finished=1 WHERE id=" + s.getId());
                                        db.close();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                                        builder.setTitle("Congratulations! ");
                                        builder.setMessage("What do you want to do?");
                                        builder.setNegativeButton("Homepage", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        databaseHelper = new DatabaseHelper(getApplicationContext());
                                        db = databaseHelper.getReadableDatabase();
                                        Cursor c = db.rawQuery("SELECT max(level_id) FROM Puzzle where difficulty_id="+difficultyId,null);
                                        c.moveToNext();
                                        int maxLevelId = c.getInt(0);
                                        if (levelId == maxLevelId) {
                                            builder.setPositiveButton("Next Puzzle", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(getApplicationContext(),"You finished all puzzles in this difficulty. You are redirected to the previous menu.",Toast.LENGTH_SHORT).show();
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {
                                                            Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }, 3000);

                                                }
                                            });
                                        }else {
                                            builder.setPositiveButton("Next Puzzle", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ((Global) PlayActivity.this.getApplication()).setNextEnable(true);
                                                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        builder.show();
                                    }
                                }
                                db.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                }
            });
        }

    }

    @Override
    protected void onPause() {
        if(((Global) PlayActivity.this.getApplication()).isNextEnable())
            ((Global) this.getApplication()).setLevelId(levelId+1);
        else
            ((Global) this.getApplication()).setLevelId(levelId);
        ((Global) this.getApplication()).setDifficultyId(difficultyId);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
