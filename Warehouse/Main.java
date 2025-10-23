package Warehouse;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<Integer, Warehouse> warehouses = new HashMap<>();
        AlertService alertService = new AlertService();

        loadWarehousesFromFile(warehouses);

        System.out.println("===== Warehouse Inventory Tracker =====");

        while (true) {
            System.out.println("\n==== Warehouse Inventory Menu ====");
            System.out.println("1. Add Warehouse");
            System.out.println("2. View All Warehouses");
            System.out.println("3. Add Product to Warehouse");
            System.out.println("4. Receive Shipment");
            System.out.println("5. Fullfill order");
            System.out.println("6. View All Products");
            System.out.println("7. Exit (Leave the Wavehouse)");
            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newLine(enter symbol after int);

            switch (choice) {
                case 1:
                    System.out.print("Enter Warehouse ID: ");
                    int wid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Warehouse Name: ");
                    String wname = sc.nextLine();
                    Warehouse w = new Warehouse(wid, wname);
                    w.addObserver(alertService);
                    warehouses.put(wid, w);
                    System.out.println("Warehouse '" + wname + "' added successfully!");
                    break;

                case 2:
                    if (warehouses.isEmpty()) {
                        System.out.println("No warehouses available.");
                    } else {
                        System.out.println("Available Warehouses:");
                        for (Warehouse wh : warehouses.values()) {
                            System.out.println("ID: " + wh.getId() + " | Name: " + wh.getName());
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Warehouse ID: ");
                    wid = sc.nextInt();
                    sc.nextLine();
                    Warehouse wh = warehouses.get(wid);
                    if (wh == null) {
                        System.out.println(" Warehouse not found!");
                        break;
                    }
                    System.out.print("Enter Product ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Product Name: ");
                    String pname = sc.nextLine();
                    System.out.print("Enter Initial Quantity: ");
                    int qty = sc.nextInt();
                    System.out.print("Enter Reorder Threshold: ");
                    int threshold = sc.nextInt();
                    sc.nextLine();
                    wh.addProduct(new Product(pid, pname, qty, threshold));
                    break;

                case 4:
                    System.out.print("Enter Warehouse ID: ");
                    wid = sc.nextInt();
                    sc.nextLine();
                    wh = warehouses.get(wid);
                    if (wh == null) {
                        System.out.println(" Warehouse not found!");
                        break;
                    }
                    System.out.print("Enter Product ID: ");
                    pid = sc.nextInt();
                    System.out.print("Enter Quantity to Add: ");
                    qty = sc.nextInt();
                    sc.nextLine();
                    wh.receiveShipment(pid, qty);
                    break;

                case 5:
                    System.out.print("Enter Warehouse ID: ");
                    wid = sc.nextInt();
                    sc.nextLine();
                    wh = warehouses.get(wid);
                    if (wh == null) {
                        System.out.println(" Warehouse not found!");
                        break;
                    }
                    System.out.print("Enter Product ID: ");
                    pid = sc.nextInt();
                    System.out.print("Enter Quantity to Fulfill: ");
                    qty = sc.nextInt();
                    sc.nextLine();
                    wh.fulfillOrder(pid, qty);
                    break;

                case 6:
                    System.out.print("Enter Warehouse ID: ");
                    wid = sc.nextInt();
                    sc.nextLine();
                    wh = warehouses.get(wid);
                    if (wh == null) {
                        System.out.println(" Warehouse not found!");
                        break;
                    }
                    wh.viewAllProducts();
                    break;

                case 7:
                    saveWarehousesToFile(warehouses);
                    System.out.println("Exiting....");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }

    }

    private static void saveWarehousesToFile(Map<Integer, Warehouse> warehouses) {
        try {
            PrintWriter out = new PrintWriter("warehouseData.txt");

            for (Warehouse wh : warehouses.values()) {
                out.println("Warehouse ID: " + wh.getId());
                out.println("Warehouse Name: " + wh.getName());
                for (Product p : wh.getAllProducts().values()) {
                    out.println(p.getId() + "," + p.getName() + "," + p.getQuantity() + "," + p.getThreshold());
                }
                out.println("---");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error in Saving Data : " + e.getMessage());
        }
    }

    private static void loadWarehousesFromFile(Map<Integer, Warehouse> warehouses) {
        try {
            Scanner sc = new Scanner(new File("warehouseData.txt"));
            Warehouse currentWarehouse = null;
            int warehouseId = 0;
            String warehouseName = null;
            AlertService alertService = new AlertService();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (line.startsWith("Warehouse ID: ")) {
                    warehouseId = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Warehouse Name: ")) {
                    warehouseName = line.split(":")[1].trim();
                    currentWarehouse = new Warehouse(warehouseId, warehouseName);
                    currentWarehouse.addObserver(alertService);
                    warehouses.put(warehouseId, currentWarehouse);
                } else if (line.equals("---")) {
                    currentWarehouse = null;
                } else if (currentWarehouse != null) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    int threshold = Integer.parseInt(parts[3]);
                    currentWarehouse.addProduct(new Product(id, name, qty, threshold));
                }
            }

            System.out.println("Warehouses Loaded Successfully from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Starting fresh.");
        }
    }
}
