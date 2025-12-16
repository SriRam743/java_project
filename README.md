Core Features
The system loads products and coupons from CSV files into HashMaps, supports adding items to cart with stock validation via custom OutOfStockException, and applies percentage-based discounts through the DiscountPolicy interface. Taxes at 18% are calculated per item using the Taxable interface implemented by Item. Checkout generates timestamped invoice text files and uses a PaymentFactory for polymorphic card/cash payments.​

Menu Operations
Add to Cart (1): Prompts for product ID and quantity, checks stock, deducts from inventory.

View Cart (2): Lists items and quantities.

Sort Products (3): Streams products sorted by price or category using Comparator.

Apply Coupon (4): Validates code against loaded coupons, sets discount percentage, throws InvalidCouponException if invalid.

Checkout (5): Creates invoice, processes payment, exits.

Exit (6): Terminates program.​

Technical Design
The code follows OOP principles with interfaces for extensibility (DiscountPolicy, Taxable, Payment), streams for functional operations, and file I/O via Files.readAllLines and BufferedWriter. It handles a CLI-driven loop with Scanner, assumes CSV format (e.g., products.csv: "id,name,category,price,stock"), and maintains state in static maps for simplicity.​

Sample CSV Files
Create these alongside the JAR for testing:

products.csv:

text
id,name,category,price,stock
P1,Laptop,Electronics,50000,10
P2,Mouse,Accessories,500,50
coupons.csv:
SAVE20,20
text
coupon,discount
SAVE10,10
