package order.com;

import java.io.FileWriter;
import java.io.IOException;

public class CreateCouponsCSV {

    public static void main(String[] args) {

        try {
            FileWriter fw = new FileWriter("coupons.csv");

           
            fw.write("save5,5\n");
            fw.write("SAVE20,20\n");
            fw.write("FESTIVE25,25\n");
            fw.write("NEWUSER15,15\n");
            fw.write("MEGA30,30\n");

            fw.close();

            System.out.println("coupons.csv file created successfully!");

        } catch (IOException e) {
            System.out.println("Error creating coupons.csv file");
            e.printStackTrace();
        }
    }
}