package carsharing;

import java.util.Queue;
import java.util.Scanner;

public class MenuHierarchy {
    DAODatabase db;

    public MenuHierarchy(String dbfile) {
        db = new DAODatabase(dbfile);
    }

    public void showMainMenu(){
        boolean exit = false;

        while(!exit) {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");

            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println();
                    showLoginManager(sc);
                    break;
                case 2:
                    showCustomerList(sc);
                    break;
                case 3:
                    sc.nextLine();
                    System.out.println("Enter the customer name:");
                    db.createCustomer(new Customer(0, sc.nextLine()));
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
            System.out.println();
        }
    }

    private void showCustomerList(Scanner sc) {
        Queue<Customer> customers = db.readCustomers();
        boolean exit = false;
        if(customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            return;
        }

        while(!exit) {

            System.out.println("Customer list:");
            for (Customer customer : customers) {
                System.out.println(customer.id + ". " + customer.name);
            }

            System.out.println("0. Back");
            int choice = sc.nextInt();
            if (choice == 0) return;

            Customer customer = searchCustomerById(customers, choice);
            if(customer == null){
                System.out.println("Wrong entry");
                continue;
            }

            showRentMenu(sc, customer);
            return;
        }
    }

    private void showRentMenu(Scanner sc, Customer customer) {
        boolean exit = false;

        while(!exit) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    if(customer.rentedCar != 0){
                        System.out.println("You've already rented a car!");
                        continue;
                    }
                    Company cmp = showCompanyList(sc);
                    if(cmp != null) {
                        Car car = chooseCarList(sc, cmp);
                        if(car != null){
                            customer.rentedCar = car.id;
                            db.updateCustomer(customer);
                            System.out.printf("You rented '%s'\n", car.name);
                        }
                    }
                    break;
                case 2:
                    if(customer.rentedCar == 0){
                        System.out.println("You didn't rent a car!");
                        continue;
                    }
                    customer.rentedCar = 0;
                    db.updateCustomer(customer);
                    System.out.println("You've returned a rented car!");
                    break;
                case 3:
                    if(customer.rentedCar == 0){
                        System.out.println("You didn't rent a car!");
                        continue;
                    }
                    Car car = db.readCar(customer.rentedCar);
                    Company company = db.readCompany(car.companyId);
                    System.out.println("Your rented car:");
                    System.out.println(car.name);
                    System.out.println("Company:");
                    System.out.println(company.name);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    break;
            }
        }
    }

    private void showLoginManager(Scanner sc) {
        boolean exit = false;

        while(!exit) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    Company cmp = showCompanyList(sc);
                    if(cmp != null) showCarMenu(sc, cmp);
                    break;
                case 2:
                    sc.nextLine();
                    System.out.println("Enter the company name:");
                    db.createCompany(new Company(1, sc.nextLine()));
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
            System.out.println();
        }
    }

    private Company showCompanyList(Scanner sc) {
        Queue<Company> companies = db.readCompanys();
        boolean exit = false;
        if(companies.isEmpty()) {
            System.out.println("The company list is empty");
            return null;
        }

        while(!exit) {

            System.out.println("Choose the company:");
            for (Company company : companies) {
                System.out.println(company.id + ". " + company.name);
            }

            System.out.println("0. Back");
            int choice = sc.nextInt();
            if (choice == 0) return null;

            Company company = searchCompanyById(companies, choice);
            if(company == null){
                System.out.println("Wrong entry");
                continue;
            }

            return company;
        }
        return null;
    }

    private Company searchCompanyById(Queue<Company> companies, int choice) {
        for (Company company : companies) {
            if (company.id == choice) {
                return company;
            }
        }
        return null;
    }

    private Customer searchCustomerById(Queue<Customer> customers, int choice) {
        for (Customer customer : customers) {
            if (customer.id == choice) {
                return customer;
            }
        }
        return null;
    }

    private void showCarMenu(Scanner sc, Company company) {
        boolean exit = false;

        while(!exit) {
            System.out.println();
            System.out.println(String.format("'%s' company:", company.name));
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    showCarList(company);
                    break;
                case 2:
                    sc.nextLine();
                    System.out.println("Enter the car name:");
                    String carName = sc.nextLine();
                    db.createCar(new Car(1, carName), company);
//                    return;
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void showCarList(Company company) {
        Queue<Car> cars = db.readCars(company);
        if(cars.isEmpty()) {
            System.out.println("The car list is empty!");
            return;
        }
        System.out.println("Car list:");
        for (Car car : cars) {
            System.out.println(car.id + ". " + car.name);
        }

    }

    private Car chooseCarList(Scanner sc, Company company) {
        Queue<Car> cars = db.readUnusedCars(company);
        if(cars.isEmpty()) {
            System.out.println("The car list is empty!");
            return null;
        }
        while(true) {
            System.out.println("Car list:");
            for (Car car : cars) {
                System.out.println(car.id + ". " + car.name);
            }
            int choice = sc.nextInt();
            if (choice == 0) return null;

            Car car = searCarById(cars, choice);
            if (car == null) {
                System.out.println("Wrong entry");
                continue;
            }
            return car;
        }
    }

    private Car searCarById(Queue<Car> cars, int choice) {
        for (Car car : cars) {
            if (car.id == choice) {
                return car;
            }
        }
        return null;
    }
}
