package com.example.tresrayas;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random random = new Random();
    private Button chessBt;
    private TextView tv1; //TextView para mostrar el turno, quien gana etc.
    private boolean isEasyMode = true;//Por defecto, empezar con nivel facil
    private MediaPlayer vicotrySoundPlayer;// para play los sinidos victoria
    private MediaPlayer lostSoundPlayer;// para play los sinidos de perder
    private MediaPlayer clickSoundPlayer;// para play los sinidos de click
    private MediaPlayer drawSoundPlayer;// para play los sinidos de empatar

    private static final int vacio = 0; // 空格的标记 // signo de cuadro vacio
    private static final int jugador = 1; // 玩家的标记 //signo de jugador
    private static final int robot = 2; // AI的标记 //signo de robot
    private int[][] tableroStatus; // 0 - 空, 1 - 玩家, 2 - AI, 0 nulo, 1 jugador, 2 IA
    private boolean isPlayerTurn = true; // 初始轮到玩家 // inici con jugador, True: turno de jugador, False: Turno de AI
    private boolean isGameActive = true; // 游戏是否进行中 // game status
    private int playerSymbol = R.drawable.circulo; // 玩家的符号 //simbolo de jugador
    private int aiSymbol = R.drawable.cruz; // AI的符号 //simbolo de IA
    private final int tableroSybol = R.drawable.white; //simbolo de tablero
    ImageButton b1, b2, b3, b4, b5, b6, b7, b8, b9; //9 cuadrito de la tablerito
    Button reiniciBt, StartBt, lvlBt; //Botones necesarios para reiniciar, empezar, cambia nivel.
    GridLayout gridLayout; //Laypout de tablero
    boolean isPlayerUseX = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Iniciamos los variables y objetos
        tableroStatus = new int[3][3];
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        tv1 = findViewById(R.id.tv1);
        chessBt = findViewById(R.id.chessBt);
        reiniciBt = findViewById(R.id.reiniciBt);
        StartBt = findViewById(R.id.StartBt);
        lvlBt = findViewById(R.id.lvlBt);
        reiniciBt.setVisibility(View.INVISIBLE);
        lvlBt.setVisibility(View.INVISIBLE);
        gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setVisibility(View.GONE);
        chessBt.setVisibility(View.VISIBLE);
        vicotrySoundPlayer = MediaPlayer.create(this, R.raw.victory_sound);
        lostSoundPlayer = MediaPlayer.create(this, R.raw.lost_sound);
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound);
        drawSoundPlayer = MediaPlayer.create(this, R.raw.draw_sound);
        tv1.setText(R.string.ChangeChess);
        tv1.setVisibility(View.VISIBLE);
        resetTablero();
    }

    //  Metodos para play sonidos
    private void playVictorySound() {
        if (vicotrySoundPlayer != null) {
            vicotrySoundPlayer.start();
        }
    }

    private void playLostSound() {
        if (lostSoundPlayer != null) {
            lostSoundPlayer.start();
        }
    }

    private void playClickSound() {
        if (clickSoundPlayer != null) {
            clickSoundPlayer.start();
        }
    }

    private void playDrawSound() {
        if (drawSoundPlayer != null) {
            drawSoundPlayer.start();
        }
    }


    // 设置简单模式 setFacilMode
    private void setEasyMode() {
        isEasyMode = true;
    }

    // 设置困难模式
    private void setHardMode() {
        isEasyMode = false;
    }


    //resetear el tablero. Ponerlos todos en blanco.
    private void resetTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tableroStatus[i][j] = vacio;
            }
        }
        isGameActive = true;
        isPlayerTurn = true; //Despres iniciar, empezar con el turno de jugador
        resetButtonImages();
    }

    //Metodos para resetear los imagens buttons
    private void resetButtonImages() {
        b1.setImageResource(tableroSybol);
        b2.setImageResource(tableroSybol);
        b3.setImageResource(tableroSybol);
        b4.setImageResource(tableroSybol);
        b5.setImageResource(tableroSybol);
        b6.setImageResource(tableroSybol);
        b7.setImageResource(tableroSybol);
        b8.setImageResource(tableroSybol);
        b9.setImageResource(tableroSybol);
    }


    //Metodo para cambiar el nivel de AI, Puede ser Facil o Dificil
    // 更改AI难度级别
    public void changeLvl(View view) {
        String currentText = lvlBt.getText().toString();
        if (currentText.equalsIgnoreCase("Facil")) {
            lvlBt.setText(R.string.HardLvl);
            setHardMode();
            restartGame(view);  //reiniciar el tablero y estatus cuando cambiamos el nivel.
        } else {
            lvlBt.setText(R.string.EasyLvl);
            setEasyMode();
            restartGame(view);  //reiniciar el tablero y estatus cuando cambiamos el nivel.
        }
    }


    // AI的回合，根据难度级别来决定AI的移动
    //turno de Bot, segun el nivel, hay 2 modos.
    private void aiTurn() {
        new Handler().postDelayed(new Runnable() { // El delay, antes de Bot poner la pieza, espera 2 a 5 segundos
            @Override
            public void run() {
                if (isEasyMode) {
                    //Si el easy modo esta activado. Bot usa logica de Facil(aleatorio).
                    easyAiMove();
                    tv1.setText(R.string.jugador_turn);
                } else {
                    //Si no, usa la logica de modo dificl
                    hardAiMove();
                    tv1.setText(R.string.jugador_turn);
                }
                //Revisa si gana el Bot despres de ponerse la pieza
                if (checkWin(robot, true)) {
                    isGameActive = false;
                    playLostSound(); // Play sonido de perder
                    tv1.setText(R.string.ai_win); // actualizar el TextView
                } else if (isTableroFull()) {
                    isGameActive = false;
                    playDrawSound(); // Play sonido de empatar
                    tv1.setText(R.string.game_draw); // actualizar el TextView
                }
            }
        }, random.nextInt(3000) + 2000); // 2s to 5s
    }


    // East Ai logic, selecciona un cuadrido vacio y ponerse(aleatorio)
    private void easyAiMove() {
        if (!isGameActive || isTableroFull()) {
            return; //Si game estatus esta desactivado o tablero esta lleno, vuelve
        }
        //Si no, selecciona un sitio aleatorio
        Random random = new Random();
        int row, col;
        //Si el centro de tablero es vacio. Bot lo pone pieza alli
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (tableroStatus[row][col] != vacio);

        while (tableroStatus[row][col] != vacio) {
            row = random.nextInt(3);
            col = random.nextInt(3);
        }
        if (tableroStatus[1][1] == vacio) {
            row = 1;
            col = 1;
        }
        makeMove(row, col, robot); //Pasamos los parametro para el makemove metodo lo pone
        ImageButton selectedButton = getButtonByRowColumn(row, col);
        if (selectedButton != null) {
            selectedButton.setImageResource(aiSymbol);
        }
    }


    // Logica de modo dificil. Intenta ganar. Si no, intenta defender que no pierde. Si no, aleatorio
    private void hardAiMove() {
        // Revisa si gana, si es que sí, gana.
        //Si puede ganar, gana
        if (checkAndMakeMove(robot, true)) {
            return;
        }

        // 如果不能赢棋，再检查是否可以阻止玩家赢棋
        //Si no gana, revisa a ver si jugador esta ganando.
        if (checkAndMakeMove(jugador, false)) {
            return;
        }

        // 如果以上都不满足，就执行随机移动
        //Si no gana o jugador no gana, pone aleatorio
        easyAiMove();
    }


    //Metodo para implementar la logica de modo dificil. //isWinningMove--> True: Intenta ganar, False: Intente defenderse(que oponente no gana)
    private boolean checkAndMakeMove(int player, boolean isWinningMove) {
        //Iterate el tablero para buscar un sitio que puede ganar.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tableroStatus[i][j] == vacio) {
                    tableroStatus[i][j] = player;
                    boolean canWin = checkWin(player, false); // pasamos updateUI como false, para que no se simula como jugador
                    tableroStatus[i][j] = vacio; //anula el move

                    //Si puede ganar aqui, lo pone la pieza
                    if (canWin && isWinningMove) {
                        makeMove(i, j, robot);
                        return true;
                    }

                    //Si no puede ganar, y player es igual a jugador.
                    if (!isWinningMove && player == jugador) {
                        tableroStatus[i][j] = jugador;
                        canWin = checkWin(jugador, false);
                        tableroStatus[i][j] = vacio;

                        if (canWin) {
                            makeMove(i, j, robot);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    //Metodo universal para poner la pieza. dando fila, columna, y quien esta poniendo(jugador o Bot).
    private void makeMove(int row, int col, int player) {
        tableroStatus[row][col] = player;
        ImageButton selectedButton = getButtonByRowColumn(row, col); //Atraves de indice

        if (selectedButton != null) { //Si no es nulo
            if (player == jugador) {
                selectedButton.setImageResource(playerSymbol);
            } else {
                selectedButton.setImageResource(aiSymbol);
            }
            selectedButton.setEnabled(false);

            if (checkWin(player, true)) {
                isGameActive = false;
                if (player == jugador) {
                    playVictorySound();
                    tv1.setText(R.string.jugador_win);
                } else {
                    playLostSound();
                    tv1.setText(R.string.ai_win);
                }
            } else if (isTableroFull()) {
                isGameActive = false;
                playDrawSound();
                tv1.setText(R.string.game_draw);
            } else {
                isPlayerTurn = !isPlayerTurn;
            }
            playClickSound();
        }

    }

    //Revisa si hay espacio Vacio
    private boolean isTableroFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tableroStatus[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    //Empezar la partida
    public void startGame(View view) {
        //Eliminamos el buton start y liberar su recurso y espacio
        StartBt.setVisibility(View.INVISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        reiniciBt.setVisibility(View.VISIBLE);
        lvlBt.setVisibility(View.VISIBLE);
        if (isPlayerUseX) {
            playerSymbol = R.drawable.cruz;
            aiSymbol = R.drawable.circulo;
            Toast toast1 = Toast.makeText(getApplicationContext(), "Jugador usa X ", Toast.LENGTH_LONG);
            toast1.show();
            // tv1.setText("Jugador usa X");
        }

        chessBt.setVisibility(View.INVISIBLE);
        tv1.setText(R.string.jugador_turn);

    }

    //Para eligir el fitcher //Tip: es mayuscula de o, no es numero 0;
    public void choosePieza(View view) {
        String currentText = chessBt.getText().toString();
        if (currentText.equalsIgnoreCase("O")) {
            chessBt.setText(R.string.Chess2);
            isPlayerUseX = true;
        } else {
            chessBt.setText(R.string.Chess1);
            isPlayerUseX = false;
        }


    }


    //Reiniciar la partida
    // 重置游戏
    public void restartGame(View view) {
        resetTablero(); // reinicia tablero status
        isGameActive = true; // game status activado
        isPlayerTurn = true; // turno de jugador
        tv1.setText(R.string.jugador_turn);

        // 重置所有按钮的图像和状态 // reset all imagine button al estado de inicial y status
        b1.setImageResource(tableroSybol);
        b1.setEnabled(true);
        b2.setImageResource(tableroSybol);
        b2.setEnabled(true);
        b3.setImageResource(tableroSybol);
        b3.setEnabled(true);
        b4.setImageResource(tableroSybol);
        b4.setEnabled(true);
        b5.setImageResource(tableroSybol);
        b5.setEnabled(true);
        b6.setImageResource(tableroSybol);
        b6.setEnabled(true);
        b7.setImageResource(tableroSybol);
        b7.setEnabled(true);
        b8.setImageResource(tableroSybol);
        b8.setEnabled(true);
        b9.setImageResource(tableroSybol);
        b9.setEnabled(true);
    }

    //Metodo para
    public void onButtonClick(View view) {
        if (!isGameActive || !isPlayerTurn)
            return; //si match no esta activado o no toca turno de jugador, return
        tv1.setText(R.string.ai_turn);
        ImageButton button = (ImageButton) view;
        int row = -1, col = -1;

        int id = view.getId();
        if (id == R.id.b1) {
            row = 0;
            col = 0;
        } else if (id == R.id.b2) {
            row = 0;
            col = 1;
        } else if (id == R.id.b3) {
            row = 0;
            col = 2;
        } else if (id == R.id.b4) {
            row = 1;
            col = 0;
        } else if (id == R.id.b5) {
            row = 1;
            col = 1;
        } else if (id == R.id.b6) {
            row = 1;
            col = 2;
        } else if (id == R.id.b7) {
            row = 2;
            col = 0;
        } else if (id == R.id.b8) {
            row = 2;
            col = 1;
        } else if (id == R.id.b9) {
            row = 2;
            col = 2;
        }

        //si el cuadrido seleccionado es vacio y  fila y columna son vacios. Poner la pieza
        if (row >= 0 && col >= 0 && tableroStatus[row][col] == vacio) {
            makeMove(row, col, jugador); // Poner la pieza

            // 检查玩家是否赢得了游戏
            // Revisa si ganar el jugador
            if (checkWin(jugador, true)) {
                //Block de si gana el jugador
                isGameActive = false; //set Game status a false. Es decir la partida esta acabada
                playVictorySound();//Play sonido de victoria
                tv1.setText(R.string.jugador_win);//Mostrar en pantalla
            } else if (isTableroFull()) {
                //Block de si el tablero esta lleno:
                isGameActive = false; //set Game status a false. Es decir la partida esta acabada
                playDrawSound();//sonido de empatar
                tv1.setText(R.string.game_draw); //Mostrar con textView
            } else {
                isPlayerTurn = false;
                aiTurn(); // Toca turno de Bot
            }
        }
    }


    private boolean checkWin(int player, boolean updateUI) {
        // Define los variables
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1, x3 = -1, y3 = -1;

        // Revisa el tablero
        for (int i = 0; i < 3; i++) {
            // Revisa fila
            if (tableroStatus[i][0] == player && tableroStatus[i][1] == player && tableroStatus[i][2] == player) {
                x1 = i;
                y1 = 0;
                x2 = i;
                y2 = 1;
                x3 = i;
                y3 = 2;
                break;
            }
            // Revisa columna
            if (tableroStatus[0][i] == player && tableroStatus[1][i] == player && tableroStatus[2][i] == player) {
                x1 = 0;
                y1 = i;
                x2 = 1;
                y2 = i;
                x3 = 2;
                y3 = i;
                break;
            }
        }
        // Revisa diagonal
        if (tableroStatus[0][0] == player && tableroStatus[1][1] == player && tableroStatus[2][2] == player) {
            x1 = 0;
            y1 = 0;
            x2 = 1;
            y2 = 1;
            x3 = 2;
            y3 = 2;
        } else if (tableroStatus[0][2] == player && tableroStatus[1][1] == player && tableroStatus[2][0] == player) {
            x1 = 0;
            y1 = 2;
            x2 = 1;
            y2 = 1;
            x3 = 2;
            y3 = 0;
        }

        // Si gana
        if (x1 != -1) {
            if (updateUI) {
                actulizaLineaGanada(x1, y1, x2, y2, x3, y3);
            }
            return true;
        }

        return false;
    }


    private void actulizaLineaGanada(int x1, int y1, int x2, int y2, int x3, int y3) {
        ImageButton button1 = getButtonByRowColumn(x1, y1);
        ImageButton button2 = getButtonByRowColumn(x2, y2);
        ImageButton button3 = getButtonByRowColumn(x3, y3);

        if (button1 != null && button2 != null && button3 != null) {
            // Segun el ganador, pone diferente imagens
            if (tableroStatus[x1][y1] == jugador) {
                button1.setImageResource(R.drawable.red_circulo); // jugador gana
            } else if (tableroStatus[x1][y1] == robot) {// Bot gana
                button1.setImageResource(R.drawable.red_cruz);
            }

            if (tableroStatus[x2][y2] == jugador) {
                button2.setImageResource(R.drawable.red_circulo); // jugador gana
            } else if (tableroStatus[x2][y2] == robot) { // Bot gana
                button2.setImageResource(R.drawable.red_cruz);
            }

            if (tableroStatus[x3][y3] == jugador) {
                button3.setImageResource(R.drawable.red_circulo); // Jugador gana
            } else if (tableroStatus[x3][y3] == robot) {// Bot gana
                button3.setImageResource(R.drawable.red_cruz);
            }
        }
    }

    //Return el imageButton dando el valor de fila y columna.
    private ImageButton getButtonByRowColumn(int row, int col) {
        if (row == 0 && col == 0) return b1;
        if (row == 0 && col == 1) return b2;
        if (row == 0 && col == 2) return b3;
        if (row == 1 && col == 0) return b4;
        if (row == 1 && col == 1) return b5;
        if (row == 1 && col == 2) return b6;
        if (row == 2 && col == 0) return b7;
        if (row == 2 && col == 1) return b8;
        if (row == 2 && col == 2) return b9;
        System.err.println("getButtonByIndex method error ");
        return null; //return nulo en caso de no coincidir o error
    }


    //Para apagar el MediaPlayers, evitar el leak de memoria
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vicotrySoundPlayer != null) {
            vicotrySoundPlayer.release();
            vicotrySoundPlayer = null;
        }
        if (lostSoundPlayer != null) {
            lostSoundPlayer.release();
            lostSoundPlayer = null;
        }
        if (clickSoundPlayer != null) {
            clickSoundPlayer.release();
            clickSoundPlayer = null;
        }
        if (drawSoundPlayer != null) {
            drawSoundPlayer.release();
            drawSoundPlayer = null;
        }
    }


}