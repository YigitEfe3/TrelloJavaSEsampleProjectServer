package org.example;

import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import javax.management.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class SampleProject1 implements RMIServer{
    private static MBeanServer mBeanServer = null;
    private static ObjectName objectName = null;
    private static State stateMBean = null;

    public static void main(String[] args){

        try{

            RMIServer server = new SampleProject1();                                 //To start the RMI server

            Registry registry = LocateRegistry.createRegistry(1099);            //Creates a registry on port 1099 for the RMI server

            registry.bind("Server", server);                                   //Binds the server

            System.out.println("Server Started...");

            initializeMBeanServer();                                                 //initializes the mBeanServer to get the statistics


        } catch (RemoteException | AlreadyBoundException | MalformedObjectNameException |
                 NotCompliantMBeanException | InstanceAlreadyExistsException | MBeanRegistrationException e){
            e.printStackTrace();
        }
    }

    public SampleProject1() throws RemoteException {
        UnicastRemoteObject.exportObject(this,0);                           //To start the RMI server
    }

    @Override
    public void parseXMLandWriteToDatabase(byte[] byteArray) throws IOException {    //takes the file as a byte array from the client

        File file = new File("src/main/resources/employees2.xml");          //the XML file

        if(!file.exists()){
            file.createNewFile();                                                    //if the file doesn't exist, create a new one
        }

        FileOutputStream fos;

        try{

            fos = new FileOutputStream(file);                                        //assigns the file to the stream
            fos.write(byteArray);                                                    //writes the byte array inside the file

            fos.close();                                                             //closes the stream

        } catch (IOException e){
            e.printStackTrace();
        }


        try{

            JAXBContext jaxbContext = JAXBContext.newInstance(Employees.class);      //Binds the Employee class to a JAXBContext
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();            //creates an unmarshaller
            Employees employees = (Employees)unmarshaller.unmarshal(file);           //takes the file and assigns it to an Employees object
            List<Employee> employee = employees.getEmployees();                      //gets the employees and puts them into a list of Employee objects
            Iterator var5 = employee.iterator();                                     //Iterates through the list of employees

            while(var5.hasNext()) {
                Employee emp = (Employee)var5.next();                                //Takes an employee from the list
                dbInsertNewEmployee(emp.getId(), emp.getFirstName(), emp.getLastName());     //and sends them to a method to insert into the database
            }

        } catch(JAXBException e){
            e.printStackTrace();
        }

    }

    private static void dbInsertNewEmployee(int newid, String newname, String newlastName) {
        try {
            Context context = setUpJNDI();                                                              //sets up the JNDI

            if (context != null) {
                DataSource datasource = (MysqlDataSource)context.lookup("jdbc/sampleProject1");   //lookup the dataSource

                if (datasource != null) {

                    Connection connection = datasource.getConnection();                 //create the connection
                    String sql = "INSERT INTO employee VALUES (?, ?, ?)";               //the sql for inserting the employees into the database
                    PreparedStatement statement = connection.prepareStatement(sql);     //preparing a statement using the sql

                    statement.setString(1, String.valueOf(newid));         //Telling the statement what the values (?, ?, ?) are in the sql
                    statement.setString(2, newname);
                    statement.setString(3, newlastName);

                    stateMBean.incrementOperationCounter();                             //increment the operation counter to get the statistic from the mBeanServer

                    statement.executeUpdate();                                          //execute the sql query

                    stateMBean.setState("SUCCESSFUL");                                  //set the state of the operation as successful for the mBeanServer

                } else {
                    System.out.println("DataSource is null.");
                }
            } else {
                System.out.println("Context is null.");
            }
        } catch (NamingException | SQLException var8) {
            stateMBean.setState("FAILED");                                              //set the state of the operation as failed for the mBeanServer
            var8.printStackTrace();
        }

    }

    private static Context setUpJNDI() {
        Hashtable<String, String> env = new Hashtable();                        //creates the hashtable for the environment
        env.put("java.naming.factory.initial", "com.sun.jndi.fscontext.RefFSContextFactory");
        env.put("java.naming.provider.url", "file:/D:/");

        try {
            Context context = new InitialContext(env);                          //gets the context
            MysqlDataSource dataSource = new MysqlDataSource();                 //creates a new MysqlDataSource object

            dataSource.setPortNumber(3306);                                     //initializes the datasource
            dataSource.setURL("jdbc:mysql://localhost:3306/sampleProject1");
            dataSource.setUser("root");
            dataSource.setPassword("my-secret-pw");

            context.rebind("jdbc/sampleProject1", dataSource);            //bind the datasource to a JNDI name if it is not already bound

            return context;                                                     //returns the created context if there is no problem

        } catch (NamingException var3) {
            System.out.println("Could not get the initial context.");
            var3.printStackTrace();
            return null;                                                        //returns null if a problem occurred
        }
    }

    private static void initializeMBeanServer() throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException,
                                                       MalformedObjectNameException {

        mBeanServer = ManagementFactory.getPlatformMBeanServer();           //creates the MBeanServer platform
        objectName = new ObjectName("org.example:type=State");              //initializes the object name of the MBean as State
        stateMBean = new State();                                           //creates a new State Bean
        mBeanServer.registerMBean(stateMBean, objectName);                  //registers the State Bean and it's name (State) into the mBeanServer
    }

    @Override
    public void getNumberOfOperationsAndStatusInformation(){

        try{

            //Displays the statistics of the queries

            //Displays the number of operations done
            System.out.print("Number of operations: ");
            System.out.println(mBeanServer.getAttribute(objectName, "OperationCounter"));

            //Displays the status of these operations
            System.out.print("Status: ");
            System.out.println(mBeanServer.getAttribute(objectName, "State"));

        } catch(ReflectionException | AttributeNotFoundException | InstanceNotFoundException | MBeanException e){
            e.printStackTrace();
        }
    }
}
