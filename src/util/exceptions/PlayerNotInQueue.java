package util.exceptions;

public class PlayerNotInQueue extends RuntimeException {
    public PlayerNotInQueue(String message) {
        super(message);
    }
}
