package order.com;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

interface DiscountPolicy {
    double applyDiscount(double amount);
}

interface Taxable {
    double calculateTax(double amount);
}



class InvalidCouponException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCouponException(String msg) {
        super(msg);
    }
}

class OutOfStockException extends Exception {

	private static final long serialVersionUID = 1L;

	public OutOfStockException(String msg) {
        super(msg);
    }
}


class Item implements Taxable {
    String id, name, category;
    double price;
    int stock;

    Item(String id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public double calculateTax(double amount) {
        return amount * 0.18;
    }
}


class OrderItem {
    Item item;
    int qty;

    OrderItem(Item item, int qty) {
        this.item = item;
        this.qty = qty;
    }

    double subtotal() {
        return item.price * qty;
    }

    double tax() {
        return item.calculateTax(subtotal());
    }
}


class Order implements DiscountPolicy {

    List<OrderItem> cart = new ArrayList<>();
    double discount = 0;

    void addToCart(Item item, int qty) throws OutOfStockException {
        if (qty > item.stock)
            throw new OutOfStockException("Stock not available");

        cart.add(new OrderItem(item, qty));
        item.stock -= qty;
    }

    void applyCoupon(String code, Map<String, Double> coupons)
            throws InvalidCouponException {

        if (!coupons.containsKey(code))
            throw new InvalidCouponException("Invalid Coupon");

        discount = coupons.get(code);
    }

    public double applyDiscount(double amount) {
        return amount * discount / 100;
    }

    double subtotal() {
        return cart.stream().mapToDouble(OrderItem::subtotal).sum();
    }

    double tax() {
        return cart.stream().mapToDouble(OrderItem::tax).sum();
    }

    double total() {
        double sub = subtotal();
        return sub - applyDiscount(sub) + tax();
    }

    void generateInvoice(String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        bw.write("------ INVOICE ------\n");
        for (OrderItem oi : cart)
            bw.write(oi.item.name + " x " + oi.qty + " = " + oi.subtotal() + "\n");

        bw.write("Subtotal : " + subtotal() + "\n");
        bw.write("Discount : " + applyDiscount(subtotal()) + "\n");
        bw.write("Tax      : " + tax() + "\n");
        bw.write("Total    : " + total() + "\n");

        bw.close();
    }
}


interface Payment {
    void pay(double amount);
}

class CardPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Card");
    }
}

class CashPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Cash");
    }
}

class PaymentFactory {
    static Payment getPayment(String type) {
        return type.equalsIgnoreCase("card")
                ? new CardPayment()
                : new CashPayment();
    }
}


public class ECommerceApp {

    static Map<String, Item> products = new HashMap<>();
    static Map<String, Double> coupons = new HashMap<>();

    public static void main(String[] args) throws Exception {

        loadProducts("products.csv");
        loadCoupons("coupons.csv");

        try (Scanner sc = new Scanner(System.in)) {
			Order order = new Order();

			while (true) {
			    System.out.println("\n1.Add to Cart  2.View Cart  3.Sort  4.Coupon  5.Checkout  6.Exit");
			    int choice = sc.nextInt();
			    sc.nextLine();

			    switch (choice) {

			        case 1: 
			            System.out.print("Product ID: ");
			            String id = sc.nextLine();
			            System.out.print("Qty: ");
			            int qty = sc.nextInt();

			            try {
			                order.addToCart(products.get(id), qty);
			                System.out.println("Added!");
			            } catch (OutOfStockException e) {
			                System.out.println(e.getMessage());
			            }
			            break;

			        case 2: 
			            for (OrderItem oi : order.cart)
			                System.out.println(oi.item.name + " x " + oi.qty);
			            break;

			        case 3: 
			        	 System.out.println("1.By Price  2.By Category");
		                    int s = sc.nextInt();

		                    Stream<Item> stream = products.values().stream();
		                    if (s == 1)
		                        stream.sorted(Comparator.comparing(i -> i.price))
		                              .forEach(i -> System.out.println(i.name + " " + i.price));
		                    else
		                        stream.sorted(Comparator.comparing(i -> i.category))
		                              .forEach(i -> System.out.println(i.name + " " + i.category));
		                    break;


			        case 4: 
			            System.out.print("Coupon: ");
			            try {
			                order.applyCoupon(sc.nextLine(), coupons);
			                System.out.println("Coupon Applied");
			            } catch (InvalidCouponException e) {
			                System.out.println(e.getMessage());
			            }
			            break;

			        case 5: 
			            String file = "invoice_" + System.currentTimeMillis() + ".txt";
			            order.generateInvoice(file);
			            System.out.println("Invoice: " + file);

			            System.out.print("Payment (card/cash): ");
			            Payment p = PaymentFactory.getPayment(sc.nextLine());
			            p.pay(order.total());
			            return;

			        case 6:
			            return;
			    }
			}
		}
    }

   

    static void loadProducts(String file) throws IOException {
        for (String line : Files.readAllLines(Paths.get(file))) {
            if (!line.contains("id")) {
                String[] p = line.split(",");
                products.put(p[0],
                        new Item(p[0], p[1], p[2],
                                Double.parseDouble(p[3]),
                                Integer.parseInt(p[4])));
            }
        }
    }

    static void loadCoupons(String file) throws IOException {
        for (String line : Files.readAllLines(Paths.get(file))) {
            if (!line.contains("coupon")) {
                String[] c = line.split(",");
                coupons.put(c[0], Double.parseDouble(c[1]));
            }
        }
    }
}
