package io.parkwhere.utils.datastructures;

import java.util.Comparator;

public class CircularLinkedList<E> {

    /* Head is needed to keep track of first node */
    private Node head;

    /* Size to keep track of number of elements in list.
     * This should be increased by 1 when a element is added
     * and should be reduced by 1 when a element is deleted */
    private int size = 0;

    public Node getHead() {
        return head;
    }

    /**
     * Inserts a element into a linked list at beginning.
     *
     * @param value
     */
    public void insertAtBeginning(E value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
            head.next = head;
        } else {
            Node temp = head;
            newNode.next = temp;
            head = newNode;
        }
        size++;
    }

    /**
     * Inserts a element into a linked list at tail position.
     *
     * @param value
     */
    public void add(E value) {
        Node newNode = new Node(value);
        if (null == head) {
            /* When list is empty */
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != head) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        newNode.next = head;
        size++;
    }

    /**
     * Inserts a element into a linked list at a given position.
     *
     * @param value
     * @param position
     */
    public void insertAtPosition(E value, int position) {
        if (position < 0 || position > size) {
            throw new IllegalArgumentException("Position is Invalid");
        }
        /* Conditions check passed, let's insert the node */
        Node newNode = new Node(value);
        Node tempNode = head;
        Node prevNode = null;
        for (int i = 0; i < position; i++) {
            if (tempNode.next == head) {
                break;
            }
            prevNode = tempNode;
            tempNode = tempNode.next;
        }
        prevNode.next = newNode;
        newNode.next = tempNode;
        size++;
    }

    /**
     * Method to delete an element from the
     * beginning of the circular linked list
     */
    public void deleteFromBeginning() {
        Node temp = head;
        while (temp.next != head) {
            temp = temp.next;
        }
        temp.next = head.next;
        head = head.next;
        size--;
    }

    /**
     * Method to delete an item from the circular
     * linked list at a given position
     *
     * @param position
     */
    public void deleteFromPosition(int position) {
        if (position < 0 || position >= size) {
            throw new IllegalArgumentException("Position is Invalid");
        }
        Node current = head, previous = head;
        for (int i = 0; i < position; i++) {
            if (current.next == head) {
                break;
            }
            previous = current;
            current = current.next;
        }
        if (position == 0) {
            deleteFromBeginning();
        } else {
            previous.next = current.next;
        }
        size--;
    }

    /**
     * Method to find a node on a given index
     *
     * @param index
     * @return {@link Node}
     */
    public Node searchByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index is Invalid");
        }
        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp;
    }

    /**
     * Method to find a node for a given value
     *
     * @return {@link Node}
     */
    public Node search(E value, Comparator<E> comparator) {
        Node node = head;
        while (null != node && comparator.compare(node.item, value) != 0) {
            node = node.next;
        }
        if (null != node && comparator.compare(node.item, value) == 0) {
            return node;
        }
        return null;
    }

    /**
     * Method to check size of the circular linked list
     *
     * @return {@link int}
     */
    public int size() {
        return size;
    }

    /**
     * Method to check if circular linked list is empty
     *
     * @return {@link boolean}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Method to display the circular linked list
     */
    public void display() {
        if (head == null) {
            System.out.println("List is Empty !!");
        } else {
            Node temp = head;
            int i = 0;
            while (temp.next != head) {
                System.out.println("Node " + (++i) + ". " + temp.item);
                temp = temp.next;
            }
            System.out.println("Node " + (++i) + ". " + temp.item);
        }
        System.out.println();
    }


    /**
     * Node class of a circular linked list
     * This is needed since entire linked list is a collection
     * of nodes connected to each other through links
     *
     * <br> We are keeping it generic so that it can be used with
     * Integer, String or something else </br>
     *
     * <br> Each node contains a data item and pointer to next node.
     * Since this is a Circular linked list and each node points in
     * one direction, we maintain only pointer to one (next) node.
     * Last node again points to the first node </br>
     *
     */
    public class Node {
        /* Data item in the node */
        E item;

        /* Pointer to next node */
        Node next;

        /* Constructor to create a node */
        public Node(E item) {
            this.item = item;
        }

        public E getItem() {
            return item;
        }

        public Node getNext() {
            return next;
        }

        /* toString implementation to print just the data */
        @Override
        public String toString() {
            return "Data Item = " + item;
        }
    }

}