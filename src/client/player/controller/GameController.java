package client.player.controller;
import client.player.model.GameModel;
import client.player.view.GameView;

public class GameController {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view){
        this.model = model;
        this.view = view;


        initializeView();
    }

    public void initializeView(){

    }

    public void startGame(){

    }

    public void cancelQueue(){

    }

    public void guessLetter(char letter) {

    }

    public void updateLeaderboard() {

    }

}
