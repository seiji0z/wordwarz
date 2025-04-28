    package client.player.model;

    import WordWarZ.GameService;
    import WordWarZ.GameServiceHelper;
    import org.omg.CORBA.ORB;
    import util.helpers.CORBAConnector;

    public class QueueModel {
        private final String playerToken;
        private GameService gameService;

        public QueueModel(String token, ORB orb) {
            this.playerToken = token;
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

        public void startGame() {
            try {
                gameService.startGame(playerToken);
            } catch (Exception e) {
                System.out.println("Error starting game: " + e.getMessage());
            }
        }
    }
