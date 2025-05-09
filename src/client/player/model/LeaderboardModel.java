package client.player.model;

import WordWarZ.Player;

public class LeaderboardModel {
    private final MainMenuModel mainMenuModel;

    public LeaderboardModel(MainMenuModel mainMenuModel) {
        this.mainMenuModel = mainMenuModel;
    }

    public Player[] fetchLeaderboard()  {
            Player[] leaderboard = mainMenuModel.getLeaderboard();
            if (leaderboard == null) {

            }
            return leaderboard;
    }
}

