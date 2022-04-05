package com.example.tictactoe.main.exceptions;

public class OutOfBoundsExcp extends Exception{
    public OutOfBoundsExcp() {
        super("Index out of bounds, try again");
    }
}
