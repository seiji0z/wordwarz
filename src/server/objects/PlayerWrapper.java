package server.objects;

import WordWarZ.Player;

public class PlayerWrapper {
    private final Player corbaPlayer;

    public PlayerWrapper(Player corbaPlayer) {
        this.corbaPlayer = corbaPlayer;
    }

    public String getUsername() {
        return corbaPlayer.username;
    }

    public int getWins() {
        return corbaPlayer.wins;
    }
}


