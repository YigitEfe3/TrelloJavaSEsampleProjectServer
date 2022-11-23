package org.example;

public class State implements StateMBean {
    private String state = null;
    private int operationCounter = 0;

    public State() {
    }

    public String getState() {                              //get the state of the operation
        return this.state;
    }

    public void setState(String newState) {                 //set the state of the operation
        this.state = newState;
    }

    public void incrementOperationCounter() {               //increment the number of operations
        ++this.operationCounter;
    }

    public int getOperationCounter() {                      //get the number of operations
        return this.operationCounter;
    }
}
