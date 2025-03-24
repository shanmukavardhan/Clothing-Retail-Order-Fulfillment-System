# Clothing Retail Order Fulfillment System

## Overview
This project simulates a clothing retail order fulfillment system that efficiently processes customer orders in a concurrent environment. It implements a Producer-Consumer model using Java threads, thread synchronization, and a bounded buffer, while handling graceful shutdown through a poison pill mechanism.

## Table of Contents
- [Overview](#overview)
- [Project Description](#project-description)
- [Features](#features)
- [Architecture](#architecture)
- [Code Structure](#code-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Team Members](#team-members)
- [Challenges and Discussion](#challenges-and-discussion)
- [Conclusion](#conclusion)
- [License](#license)

## Project Description
This project simulates the operational workflow of a clothing retail system where customer orders are continuously generated and processed concurrently. The simulation involves multiple producer threads (order placement systems) that create orders and add them to a shared bounded queue, and multiple consumer threads (fulfillment workers) that process these orders. The system addresses real-world challenges such as concurrency, buffer management, and graceful termination.

## Features
- **Concurrent Order Processing:** Multiple threads handle order creation and processing simultaneously.
- **Thread Synchronization:** Uses locks and condition variables to ensure safe access to the shared order queue.
- **Bounded Buffer Management:** Implements a finite queue to prevent overflow and underflow.
- **Graceful Shutdown:** Introduces a poison pill mechanism to signal consumers when to terminate.
- **Realistic Simulation:** Incorporates random delays in order placement and processing to mimic real-world conditions.

## Architecture
The system is designed using the Producer-Consumer paradigm:
- **OrderQueue:** A thread-safe bounded buffer that stores orders.
- **OrderProducer:** Producer threads simulate customer order placements by creating and enqueuing orders.
- **OrderConsumer:** Consumer threads dequeue and process orders, detecting the poison pill to shutdown.
- **Poison Pill Mechanism:** A special order object used to signal consumer threads for a graceful shutdown after processing all orders.

## Code Structure
The project is implemented in Java and comprises the following key components:
- **Order Class:** Represents a customer order, including details like order ID, customer ID, item, shipping address, and a flag for the poison pill.
- **OrderQueue Class:** A thread-safe queue built using Java’s `LinkedList`, `ReentrantLock`, and condition variables.
- **OrderProducer Class:** Simulates order generation with random delays and adds orders to the queue.
- **OrderConsumer Class:** Processes orders retrieved from the queue and terminates upon receiving a poison pill.
- **Main Class:** Coordinates the startup and shutdown of producer and consumer threads, managing the overall simulation lifecycle.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or later
- Command-line terminal

### Installation
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/shanmukavardhan/ClothingRetailOrderFulfillment.git
   cd ClothingRetailOrderFulfillment
   ```
2. **Compile the Code:**
   ```bash
   javac ClothingRetailOrderFulfillment.java
   ```
3. **Run the Application:**
   ```bash
   java ClothingRetailOrderFulfillment
   ```

## Usage
When you run the application, the following will occur:
- **Producers:** Multiple producer threads generate orders and add them to the bounded queue.
- **Consumers:** Consumer threads retrieve and process orders.
- **Shutdown:** Once all orders are placed, the main thread inserts a poison pill for each consumer to signal shutdown.
- **Output:** The console will display the order placements, processing activities, and shutdown signals.

## Team Members
This project was developed by:
- **Suman Panigrahi**  
  Roll Number: CB.EN.U4CSE22444  
  GitHub: [suman1406](https://github.com/suman1406)
- **N Sai Kiran Varma**  
  Roll Number: CB.EN.U4CSE22424
- **B. Shanmuka Vardhan**  
  Roll Number: CB.EN.U4CSE22461  
  GitHub: [shanmukavardhan](https://github.com/shanmukavardhan)

## Challenges and Discussion
During the development process, the following challenges were addressed:
- **Concurrency and Thread Management:** Ensuring safe and efficient access to the order queue, preventing race conditions and deadlocks.
- **Queue Overflow/Underflow:** Implementing a bounded buffer capable of handling varying order volumes without performance degradation.
- **Graceful Shutdown:** Developing a robust poison pill mechanism to allow consumer threads to terminate only after all orders are processed.
- **Resource Contention:** Managing context switching and resource allocation effectively to optimize throughput.

## Conclusion
The Clothing Retail Order Fulfillment System demonstrates the practical application of concurrent programming concepts in Java. By efficiently managing multiple threads and synchronizing shared resources, the system ensures timely and reliable order processing. This project offers valuable insights into solving real-world challenges such as thread synchronization, buffer management, and graceful shutdown in multi-threaded environments.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
