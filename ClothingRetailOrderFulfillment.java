import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Order class representing a customer order
class Order {
    private final int orderId;
    private final String customerId;
    private final String item;
    private final String shippingAddress;
    private final boolean poisonPill; // Flag to signal shutdown

    // Constructor for regular orders
    public Order(int orderId, String customerId, String item, String shippingAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.item = item;
        this.shippingAddress = shippingAddress;
        this.poisonPill = false;
    }

    // Constructor for poison pill orders
    public Order(boolean poisonPill) {
        this.orderId = -1;
        this.customerId = "N/A";
        this.item = "N/A";
        this.shippingAddress = "N/A";
        this.poisonPill = poisonPill;
    }

    public boolean isPoisonPill() {
        return poisonPill;
    }

    @Override
    public String toString() {
        if (poisonPill) {
            return "PoisonPill Order (End-of-Day Signal)";
        }
        return "OrderID: " + orderId + ", Customer: " + customerId +
               ", Item: " + item + ", Address: " + shippingAddress;
    }
}

// Thread-safe bounded buffer for orders
class OrderQueue {
    private final Queue<Order> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public OrderQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    // Add an order to the queue (blocking if full)
    public void addOrder(Order order) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.offer(order);
            System.out.println("Order added: " + order);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Retrieve and remove an order from the queue (blocking if empty)
    public Order getOrder() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            Order order = queue.poll();
            notFull.signalAll();
            return order;
        } finally {
            lock.unlock();
        }
    }
}

// Producer representing an order placement system
class OrderProducer implements Runnable {
    private final OrderQueue orderQueue;
    private final int producerId;
    private final int ordersToPlace;

    public OrderProducer(OrderQueue orderQueue, int producerId, int ordersToPlace) {
        this.orderQueue = orderQueue;
        this.producerId = producerId;
        this.ordersToPlace = ordersToPlace;
    }

    @Override
    public void run() {
        for (int i = 1; i <= ordersToPlace; i++) {
            try {
                // Create a new order with a unique order ID
                Order order = new Order(producerId * 100 + i, "Customer" + producerId,
                                        "Item" + i, "Address" + producerId);
                orderQueue.addOrder(order);
                // Simulate random order placement delay
                Thread.sleep((int)(Math.random() * 200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Producer " + producerId + " finished placing orders.");
    }
}

// Consumer representing a fulfillment worker
class OrderConsumer implements Runnable {
    private final OrderQueue orderQueue;
    private final int consumerId;

    public OrderConsumer(OrderQueue orderQueue, int consumerId) {
        this.orderQueue = orderQueue;
        this.consumerId = consumerId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = orderQueue.getOrder();
                // Check for poison pill to stop processing
                if (order.isPoisonPill()) {
                    System.out.println("Consumer " + consumerId + " received shutdown signal.");
                    break;
                }
                // Process the order (simulate processing delay)
                System.out.println("Consumer " + consumerId + " processing " + order);
                Thread.sleep((int)(Math.random() * 300));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Consumer " + consumerId + " terminated.");
    }
}

// Main class to run the order fulfillment simulation
public class ClothingRetailOrderFulfillment {
    public static void main(String[] args) throws InterruptedException {
        int queueCapacity = 5;
        int numProducers = 3;
        int ordersPerProducer = 5;
        int numConsumers = 2;

        OrderQueue orderQueue = new OrderQueue(queueCapacity);

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        // Start producer threads (order placement systems)
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(new OrderProducer(orderQueue, i + 1, ordersPerProducer));
            producers[i].start();
        }

        // Start consumer threads (fulfillment workers)
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(new OrderConsumer(orderQueue, i + 1));
            consumers[i].start();
        }

        // Wait for all producers to finish
        for (Thread p : producers) {
            p.join();
        }
        System.out.println("All producers have finished placing orders. Inserting shutdown signals...");

        // Insert a poison pill for each consumer to signal end-of-day
        for (int i = 0; i < numConsumers; i++) {
            orderQueue.addOrder(new Order(true));
        }

        // Wait for all consumers to complete processing
        for (Thread c : consumers) {
            c.join();
        }
        System.out.println("All consumers terminated. Order fulfillment process completed.");
    }
}