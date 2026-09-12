package prelim.exercises;

import java.util.NoSuchElementException;

public class MyDoublyLinkedList<E> implements MyList<E> {
    private DoublyLinkedNode<E> head;
    private DoublyLinkedNode<E> tail;
    private int size;

    public MyDoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void insert(E data) throws ListOverflowException {
        DoublyLinkedNode<E> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrevious(tail);
            tail = newNode;
        }
        size++;
    }

    public void insertFirst(E data) {
        DoublyLinkedNode<E> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrevious(newNode);
            head = newNode;
        }
        size++;
    }

    @Override
    public E getElement(int index) throws NoSuchElementException {
        if (index < 0 || index >= size) {
            throw new NoSuchElementException("Index " + index + " out of bounds!");
        }
        DoublyLinkedNode<E> curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.getNext();
        }
        return curr.getData();
    }

    @Override
    public boolean delete(E data) {
        if (head == null) {
            return false;
        }

        DoublyLinkedNode<E> curr = head;
        while (curr != null) {
            if (curr.getData() != null && curr.getData().equals(data)) {
                if (curr == head && curr == tail) {
                    head = null;
                    tail = null;
                } else if (curr == head) {
                    head = head.getNext();
                    head.setPrevious(null);
                } else if (curr == tail) {
                    tail = tail.getPrevious();
                    tail.setNext(null);
                } else {
                    curr.getPrevious().setNext(curr.getNext());
                    curr.getNext().setPrevious(curr.getPrevious());
                }
                size--;
                return true;
            }
            curr = curr.getNext();
        }
        return false;
    }

    @Override
    public int search(E data) {
        DoublyLinkedNode<E> curr = head;
        int idx = 0;
        while (curr != null) {
            if (curr.getData() != null && curr.getData().equals(data)) {
                return idx;
            }
            curr = curr.getNext();
            idx++;
        }
        return -1;
    }

    @Override
    public String toString() {
        String s = "[";
        DoublyLinkedNode<E> curr = head;
        while (curr != null) {
            s += curr.getData();
            if (curr.getNext() != null) {
                s += ", ";
            }
            curr = curr.getNext();
        }
        s += "]";
        return s;
    }
}
