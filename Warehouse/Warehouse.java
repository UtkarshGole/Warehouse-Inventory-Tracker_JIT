package Warehouse;
import java.util.*;
class Warehouse {
    private int id;
    private String name;
    private Map<Integer, Product>  products = new HashMap<>();
    private List<StockObserver> observers = new ArrayList<>();
    
    public Warehouse(int id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){ 
        return name;
    }

    public int getId(){
        return id;
    }
    public void addObserver(StockObserver observer){
        observers.add(observer);
    }
    
    // Add new Product
    public void addProduct(Product product){
        if(products.containsKey(product.getId())){
            System.out.println("Product with ID " + product.getId() + "already exists!");
            return;
        }
        products.put(product.getId(), product);
        System.out.println("Added Product : "+product.getName() + " to " + name);
    }

    // Receive Shipment (Increase stock)
    public void receiveShipment(int productId, int quantity){
        Product product = products.get(productId);
        if(product == null){
            System.out.println("Product not found in " + name);
            return;
        }
        product.setQuantity(product.getQuantity() + quantity);
        System.out.println("Received Shipment: " + quantity + " units of " + product.getName() +"in" + name +  "\n(Total: "+ product.getQuantity() + ")");
    }

    //Fullfill Order (decrease stock)
    public void fulfillOrder(int productId, int quantity){
        Product product = products.get(productId);
        if(product == null){
            System.out.println("Invalid Product ID!");
            return;
        }

        if(product.getQuantity() < quantity){
            System.out.println("Insufficient stock for " + product.getName());
            return;
        }

        product.setQuantity(product.getQuantity() - quantity);
        System.out.println("Fulfilled order : " + quantity + " units of "+product.getName() + " (Remaining : " + product.getQuantity() + ")");
    
       if(product.getQuantity() < product.getThreshold()){
          notifyObservers(product);
       }   
    }

    public Map<Integer, Product> getAllProducts() {
       return products;  
    }

    // Notify Observers
    private void notifyObservers(Product product){
        for(StockObserver observer : observers){
            observer.onLowStock(product);
        }
    }


    public void viewAllProducts(){
        if(products.isEmpty()){
            System.out.println("No products available in inventory.");
            return;
        }
        
        System.out.printf("%-5s %-20s %-10s %-10s", "ID", "Name", "Quantity", "Threshold");
        System.out.println("\n------------------------------------------------------------");
        
        for(Product p : products.values()){
           System.out.printf("%-5d %-20s %-10d %-10d%n", 
                          p.getId(), p.getName(), p.getQuantity(), p.getThreshold());
        }
    }
}
