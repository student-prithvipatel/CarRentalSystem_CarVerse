# 🚗 CarVerse - Car Rental System  

CarVerse is a **Java + MySQL-based Car Rental System** that allows customers to book, rent, return, and rate cars, while admins can manage cars, monitor rentals, and generate reports. It’s a **console-based project** built using **Object-Oriented Programming (OOP)** concepts, **JDBC**, and **custom data structures**.  

---

## ✨ Features  

### 👤 Customer Features  
- Register & Login (Email/Phone + Password)  
- Search available cars with filters (brand, type, price, seats, rating)  
- Book cars with trip details & cost calculation  
- View active and returned bookings  
- Cancel a booking (before trip starts)  
- Return cars with **real-time billing & late fee calculation**  
- Rate rented cars  
- Update profile details  
- Receive rental bill as a **file and email** (via Jakarta Mail API)  

### 🛠️ Admin Features  
- Secure Admin Login  
- Add new cars (via stored procedure)  
- View, update, and remove cars  
- View available, rented, and overdue cars  
- Update availability (if not currently booked)  
- View all registered customers (via custom linked list)  
- Generate system reports:  
  - Total cars  
  - Total rentals  
  - Available cars  
  - Currently rented cars  
  - Overdue rentals  
  - Cancelled & returned rentals  
  - Total revenue  

---

## 🏗️ Tech Stack  

- **Language:** Java 17+  
- **Database:** MySQL  
- **Libraries & Tools:**  
  - JDBC (Database Connectivity)  
  - Jakarta Mail API (Email bills)  
  - Stored Procedures & Functions in MySQL  
- **Custom Data Structures:**  
  - Linked List (for Customer records)  
  - Simple Hashing (for password storage)  

---

## 📂 Project Structure  

```text
CarVerse/
├── carverse.admin/       # Admin features
├── carverse.customer/    # Customer features
├── carverse.db/          # Database connection
├── carverse.main/        # Entry point + menus
├── carverse.model/       # Data structures & models
├── carverse.payment/     # Payment handling (Cash, Card, UPI)
├── carverse.rental/      # Rental cost calculation (Stored Procedure)
└── database/             # SQL scripts (tables, procedures, functions)
```
---

## 🗄️ Database Design  

**Main Tables:**  
- `admin` – Admin credentials  
- `customer` – Customer details  
- `car` – Car details (model, brand, type, seats, price, availability)  
- `bookings` – Active bookings (start/end time, cost, status)  
- `rental` – Returned rentals history  
- `ratings` – Customer ratings for cars  
- `payment` – Payment history & bill details  

**Stored Procedure:**  
- `addCar` → Insert new car  
- `calculate_rental_cost` → Compute rental hours, late fees, total cost  

**Function:**  
- `getAverageRating(car_id)` → Returns average rating for a car  

---

## ⚡ Setup Instructions  

### 1. Clone the Repository  
```bash
git clone https://github.com/yourusername/CarVerse.git
cd CarVerse
