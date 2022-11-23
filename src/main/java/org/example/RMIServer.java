package org.example;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer  extends Remote {

    void parseXMLandWriteToDatabase(byte[] byteArray) throws IOException;

    void getNumberOfOperationsAndStatusInformation() throws RemoteException;
}
