    package client.player.model;

    import WordWarZ.*;
    import org.omg.CORBA.ORB;
    import util.helpers.CORBAConnector;

    public class GameModel {
        private GameService gameService;
        private final String token;

        public GameModel(String token, ORB orb){;
            this.token = token;
            initializeCORBAConnection(orb);
        }

        private void initializeCORBAConnection(ORB orb) {
            try {
                CORBAConnector connector = new CORBAConnector(orb);
                gameService = connector.getService("GameService", GameServiceHelper::narrow);
            } catch (Exception e) {
                System.out.println("Could not connect to GameService: " + e.getMessage());
            }
        }

        public int getRemainingGuesses() throws NotLoggedIn, NotInGame {
            return gameService.getRemainingGuesses(token);
        }

        public int startRound() throws GameNotFound, NotLoggedIn, NotInGame {
            return gameService.startRound(token);
        }


        public char[] guessLetter(char letter) throws GameNotFound, NotLoggedIn, NotInGame, CharacterAlreadyGuessed {
            return gameService.guessLetter(token, letter);
        }

        public void registerCallback(ClientCallback callback) throws NotLoggedIn {
            gameService.registerCallback(token, callback);
        }

        public void displayWinnerByRound() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
            gameService.displayWinnerByRound(token);
        }

        public void displayWinnerByGame() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
            gameService.displayWinnerByGame(token);
        }

        public void endGame() throws GameNotFound, NotLoggedIn, NotInGame, GameNotFinished {
            gameService.endGame(token);
        }

        public String displayLoserByTime() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
            return gameService.displayLoserByTime(token);
        }

        public String displayWins() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
            return gameService.displayWins(token);
        }
    }
