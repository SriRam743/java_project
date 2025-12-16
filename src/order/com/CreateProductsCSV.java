package order.com;

import java.io.FileWriter;
import java.io.IOException;

public class CreateProductsCSV {

    public static void main(String[] args) {

        try {
            FileWriter fw = new FileWriter("products.csv");

            
            fw.write("P101,Laptop,Electronics,55000,10\n");
            fw.write("P102,Smartphone,Electronics,25000,20\n");
            fw.write("P103,Headphones,Accessories,2000,30\n");
            fw.write("P104,Keyboard,Accessories,1500,25\n");
            fw.write("P105,Mouse,Accessories,800,40\n");
            fw.write("P106,Smartwatch,Wearables,12000,15\n");
            fw.write("P107,Earphones,Electronics,12000,15\n");
            fw.close();

            System.out.println("products.csv file created successfully!");

        } catch (IOException e) {
            System.out.println("Error creating CSV file");
            e.printStackTrace();
        }
    }
}