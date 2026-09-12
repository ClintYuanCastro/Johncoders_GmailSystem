package prelim.exercises;

public class DoublyLinkedNode<T> {
    private T data;
    private DoublyLinkedNode<T> next;
    private DoublyLinkedNode<T> previous;

    public DoublyLinkedNode(T data) {
        this.data = data;
        this.next = null;
        this.previous = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DoublyLinkedNode<T> getNext() {
        return next;
    }

    public void setNext(DoublyLinkedNode<T> node) {
        this.next = node;
    }

    public DoublyLinkedNode<T> getPrevious() {
        return previous;
    }

    public void setPrevious(DoublyLinkedNode<T> node) {
        this.previous = node;
    }
}
