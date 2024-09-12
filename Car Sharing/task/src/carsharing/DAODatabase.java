package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

interface DAOBase{
    void createCompany(Company company);
    void updateCompany(Company company);
    void deleteCompany(Company company);
    void createCar(Car car, Company company);
    Queue<Company> readCompanys();
    Queue<Customer> readCustomers();
    Queue<Car> readCars(Company company);
}

class Company{
    String name;
    int id;
    public Company(int id, String name){
        this.name = name;
        this.id = id;
    }
}

class Car{
    String name;
    int id;
    int companyId = 0;

    public Car(int id, String name){
        this.name = name;
        this.id = id;
        this.companyId = id;
    }
}

class Customer{
    int id;
    String name;
    int rentedCar = 0;

    public Customer(int id, String name){
        this.id = id;
        this.name = name;
    }
}

public class DAODatabase implements DAOBase{
    private DBManager dbManager;

    public DAODatabase(String dbfile){
        try {
            dbManager = new DBManager(dbfile);
/*
            dbManager.executeUpdate("DROP TABLE IF EXISTS CUSTOMER");
            dbManager.executeUpdate("DROP TABLE IF EXISTS CAR");
            dbManager.executeUpdate("DROP TABLE IF EXISTS COMPANY");


 */
            dbManager.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY " +
                    "(ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    " NAME VARCHAR(255) NOT NULL UNIQUE)");
            dbManager.executeUpdate("CREATE TABLE IF NOT EXISTS CAR (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "NAME VARCHAR(255) UNIQUE NOT NULL, " +
                    "COMPANY_ID INT NOT NULL, " +
                    "CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))");
            dbManager.executeUpdate("CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "NAME VARCHAR(255) UNIQUE NOT NULL, " +
                    "RENTED_CAR_ID INT, " +
                    "CONSTRAINT fk_car FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))");

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("Something went wrong while creating the database");
        }
    }

    @Override
    public void createCompany(Company company) {
        try {
            String sql = String.format("INSERT INTO COMPANY (NAME) VALUES ('%s')", company.name);
            dbManager.executeSql(sql);
            System.out.println("The company was created!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("there was an error while adding date to the database");
        }
    }

    @Override
    public void updateCompany(Company company) {

    }

    @Override
    public void deleteCompany(Company company) {

    }

    @Override
    public void createCar(Car car, Company company) {
        try {
            String sql = String.format("INSERT INTO CAR (NAME, COMPANY_ID) VALUES ('%s', %d)", car.name, company.id);
            dbManager.executeSql(sql);
            System.out.println("The car was added!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("there was an error while adding date to the database");
        }

    }

    @Override
    public Queue<Company> readCompanys() {
        Queue<Company> companyQueue = new LinkedList<>();

        try {
            System.out.println();
            ResultSet s = dbManager.getResultSet("SELECT * from COMPANY ORDER BY ID");
            while(s.next()){
                companyQueue.add(new Company(s.getInt("ID"), s.getString("NAME")));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("error while reading the database");
        }
        return companyQueue;
    }

    @Override
    public Queue<Car> readCars(Company company) {
        Queue<Car> carQueue = new LinkedList<>();

        try {
            System.out.println();
            ResultSet s = dbManager.getResultSet(String.format("SELECT * from CAR WHERE COMPANY_ID = %d ORDER BY ID", company.id));
            int index = 1;
            while(s.next()){
                Car car = new Car(s.getInt("ID"), s.getString("NAME"));
                car.id = index;
                carQueue.add(car);
                index++;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("error while reading the database");
        }
        return carQueue;
    }

    public Queue<Car> readUnusedCars(Company company) {
        Queue<Car> carQueue = new LinkedList<>();

        try {
            System.out.println();
            String sql = String.format("SELECT CAR.id as id, CAR.name as name, CAR.company_id as company_id " +
                    "FROM CAR LEFT JOIN CUSTOMER ON CAR.id = CUSTOMER.rented_car_id " +
                    "WHERE company_id = %d AND CUSTOMER.rented_car_id is null", company.id);
            ResultSet s = dbManager.getResultSet(sql);
            int index = 1;
            while(s.next()){
                Car car = new Car(s.getInt("ID"), s.getString("NAME"));
                car.id = index;
                carQueue.add(car);
                index++;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("error while reading the database");
        }
        return carQueue;
    }

    public Car readCar(int id) {
        if(id == 0) return null;
        try {
            ResultSet s = dbManager.getResultSet(String.format("SELECT * from CAR WHERE ID = %d", id));
            if(s.next()){
                Car car = new Car(s.getInt("ID"), s.getString("NAME"));
                car.companyId = s.getInt("COMPANY_ID");
                return car;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){}
        return null;
    }


    public Company readCompany(int id){
        if(id == 0) return null;
        try {
            ResultSet s = dbManager.getResultSet(String.format("SELECT * from COMPANY WHERE ID = %d", id));
            if(s.next()) return new Company(s.getInt("ID"), s.getString("NAME"));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){}
        return null;
    }

    public Customer readCustomer(int id) {
        if(id == 0) return null;
        try {
            ResultSet s = dbManager.getResultSet(String.format("SELECT * from CUSTOMER WHERE ID = %d", id));
            if(s.next()){
                Customer customer = new Customer(s.getInt("ID"), s.getString("NAME"));
                customer.rentedCar = s.getInt("RENTED_CAR_ID");
                return customer;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){}
        return null;
    }


    public void createCustomer(Customer customer) {
        try {
            String sql = String.format("INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES ('%s', null)", customer.name);
            dbManager.executeSql(sql);
            System.out.println("The customer was added!");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("there was an error while adding date to the database");
        }
    }

    public void updateCustomer(Customer customer) {
        try{
            if(customer.rentedCar == 0){
                dbManager.executeSql(String.format("UPDATE CUSTOMER " +
                        "SET RENTED_CAR_ID = NULL " +
                        "WHERE ID = %d", customer.id));
            }else {
                dbManager.executeSql(String.format("UPDATE CUSTOMER " +
                        "SET RENTED_CAR_ID = %d " +
                        "WHERE ID = %d", customer.rentedCar, customer.id));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Queue<Customer> readCustomers() {
        Queue<Customer> customerQueue = new LinkedList<>();

        try {
            System.out.println();
            ResultSet s = dbManager.getResultSet("SELECT * from CUSTOMER ORDER BY ID");
            while(s.next()){
                Customer c = new Customer(s.getInt("ID"), s.getString("NAME"));
                c.rentedCar = s.getInt("RENTED_CAR_ID");
                customerQueue.add(c);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(Exception e){
            System.out.println("error while reading the database");
        }
        return customerQueue;
    }

}
