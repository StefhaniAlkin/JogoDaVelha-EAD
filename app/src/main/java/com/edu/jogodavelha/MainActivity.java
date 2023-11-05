package com.edu.jogodavelha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference BD = FirebaseDatabase.getInstance().getReference();

    DatabaseReference playsDB = BD.child("plays");
    DatabaseReference tableDB = BD.child("table");
    private final String [] table = new String[9];
    private final ImageView [] images = new ImageView[9];
    private final TextView[] texts = new TextView[9];
    private String turn = "X";
    private int playerWins = 0;
    private int computerWins = 0;
    public boolean isGameOver = false;
    public int turnNumber = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();
        initializeViews();
        onClickViews();
        initializeTexts();
        resetGame();

    }

    private void initializeViews() {
        for (int i = 0; i < 9; i++) {
            String imageViewID = "img" + i;
            int resID = getResources().getIdentifier(imageViewID, "id", getPackageName());
            images[i] = findViewById(resID);
        }
    }

    private void initializeTexts() {
        for (int i = 0; i < 9; i++) {
            String textViewID = "txt" + i;
            int resID = getResources().getIdentifier(textViewID, "id", getPackageName());
            texts[i] = findViewById(resID);
        }
    }

    private void initializeButtons(){
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetGame());
    }

    private void onClickViews() {
        for (int x = 0; x < 9; x++) {
            final int index = x;
            images[x].setOnClickListener(v -> {
                try {
                    play(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void changeTurn() throws InterruptedException {
        turn = turn.equals("X") ? "O" : "X";
        if (turn.equals("O")) {
            computerPlay();
        }
    }

    private void play(int x) throws InterruptedException {

        if (isGameOver){
            showToast("Fim de jogo!");
            isGameOver = false;
            resetGame();
        }
        else {
            if (!Objects.equals(table[x], " ")) {
                showToast("Escolha outro espaço!");
                return;
            }

            table[x] = turn;
            texts[x].setText(turn);
            playsDB.child(String.valueOf(turnNumber)).setValue("Jogador " + turn + " marcou o campo " + (x + 1));
            tableDB.child(String.valueOf(turnNumber)).setValue(String.join(",", table));
            if (checkWinner()) {
                isGameOver = true;
                showToast("O jogador " + turn + " ganhou!");

                if (turn.equals("X")) {
                    playerWins++;
                } else {
                    computerWins++;
                }
                updateScore();
                return;
            }
            checkDraw();
            turnNumber++;
            changeTurn();
        }
    }

    private boolean checkWinner() {
        return checkRows() || checkColumns() || checkDiagonals();
    }

    private boolean checkRows() {
        for (int i = 0; i <= 6; i += 3) {
            if (checkLine(i, i+1, i+2)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; ++i) {
            if (checkLine(i, i+3, i+6)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return checkLine(0, 4, 8) || checkLine(2, 4, 6);
    }

    private boolean checkLine(int i, int j, int k) {
        return table[i].equals(table[j]) && table[j].equals(table[k]) && !table[i].equals(" ");
    }

    private void checkDraw() {
        if (isBoardFull()) {
            endInDraw();
        }
    }

    private boolean isBoardFull() {
        for (int x = 0; x < 9 ; x++) {
            if (table[x].equals(" ")) {
                return false;
            }
        }
        return true;
    }

    private void endInDraw() {
        showToast("Velha!");
        resetGame();
    }
    private void resetGame(){
        for (int x = 0; x < 9 ; x++){
            table[x] = " ";
            texts[x].setText(" ");
            turnNumber = 1;
            //cleanDB(); -> quando necessário, limpar o banco de dados
        }
    }

//    Quando necessário limpar o banco de dados, ativar:
//    private void cleanDB(){
//       playsDB.removeValue();
//       tableDB.removeValue();
//    }

    private void computerPlay() throws InterruptedException {
        while (true) {
            int x = (int) (Math.random() * 9);
            if (table[x].equals(" ")) {
                play(x);
                break;
            }
        }
    }

    private void updateScore() {
        updatePlayerScore();
        updateComputerScore();
    }

    private void updatePlayerScore() {
        TextView txtPlayerWins = findViewById(R.id.txtPlayer);
        txtPlayerWins.setText(String.valueOf(playerWins));
    }

    private void updateComputerScore() {
        TextView txtComputerWins = findViewById(R.id.txtComputer);
        txtComputerWins.setText(String.valueOf(computerWins));
    }
}
//SC3020789
