package org.example;

public interface StateMBean {           //declaration of the methods to be used
    String getState();

    void setState(String var1);

    void incrementOperationCounter();

    int getOperationCounter();
}
