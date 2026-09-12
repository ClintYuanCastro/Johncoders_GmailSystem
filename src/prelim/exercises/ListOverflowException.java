package prelim.exercises;

public class ListOverflowException extends Exception {
    public ListOverflowException() {
        super("List Overflow Exception");
    }

    public ListOverflowException(String message) {
        super(message);
    }
}
