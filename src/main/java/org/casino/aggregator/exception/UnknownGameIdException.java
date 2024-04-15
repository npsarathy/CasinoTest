package org.casino.aggregator.exception;

public class UnknownGameIdException extends Exception{
    public UnknownGameIdException(String message){
        super(message);
    }
}
