package server.objects;

import WordWarZ.Player;

// In your Java code (same package as your other classes)
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


