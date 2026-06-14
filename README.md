# Hotel Management System

This is a simple **Hotel Management System** I built using **Java and JavaFX** to manage hotel room operations through a GUI.

The main goal of this project was to apply concepts of **OOP, Generics, Enums, Multithreading, and Synchronization** in a practical application instead of just learning them theoretically.

## What this project does

The application allows a user to:

* Add new rooms with different room types
* View all rooms / only booked rooms / only available rooms
* Search for a room using Room ID
* Book a room for a customer
* Checkout a customer and free the room again

Each room has:

* Room ID
* Room Type
* Price
* Availability status

## Room Types

I added 5 room categories with different prices:

* Single — ₹1500
* Double — ₹2500
* Triple — ₹4000
* Deluxe — ₹5000
* Suite — ₹8000

## Concepts Used

Some important Java concepts I used in this project:

* **Enum** → For defining room types and prices
* **Classes & Objects** → For room details and application logic
* **Generics** → In the Manager class to manage rooms
* **ArrayList** → To store room data
* **Multithreading** → For operations like booking and checkout
* **Synchronization** → To avoid conflicts when multiple threads access room data

## Why I made this

I wanted to build something that feels like a real-world system instead of a basic console program. This project helped me understand how Java concepts work together in an actual application with a user interface.

## Future Improvements

Things I can add later:

* Database integration
* Login system
* Payment/Billing
* Customer records
* Better UI design
