# Bus-Koi? - Campus Bus Tracker System

📌 **Description**

"Bus Koi?" is a Java application with GUI and MySQL database to track university buses in real-time.
It enables students to view real-time locations of campus buses, 
while admins can manage bus routes, stops, and simulate movements without driver interaction.

🎯 **Features**

Admin dashboard (Add/Edit/Delete buses, routes, stops)
Student dashboard with live ETA
Real-time simulation every 10 seconds
MySQL database integration
GUI designed with Java Swing

🖥 **Tech Stack**

Java (Swing for GUI)
MySQL (XAMPP)
JDBC
ScheduledExecutorService for simulation

📂 **Folder Structure**

<pre> ```Bus Koi/
│
├── src/                    # Java source files
│   ├── db/                 # DB connection classes
│   ├── simulation/         # BusSimulator.java
│   ├── ui/                 # All GUI classes
│   └── main/               # Main.java
│
├── database/
│   ├── bus_tracker.sql     # Create tables
│   └── sample_data.sql     # Sample data to run
│
├── assets/                 # Screenshots, diagrams, logos
│   └── use_case_diagram.png
│
├── README.md
└── .gitignore ``` </pre>

⚙️ Setup Instructions

1. Clone the repository
2. Import into IntelliJ (or any Java IDE)
3. Start MySQL using XAMPP
4. Run the SQL scripts inside /database to create and populate tables
5. Launch `Main.java`

or,

1. Create a database in MySQL named `campus_bus_tracker`.
2. Run the schema:  
   `mysql -u root -p campus_bus_tracker < database/bus_tracker.sql`
3. Insert sample data:  
   `mysql -u root -p campus_bus_tracker < database/sample_data.sql`
4. Open the `src` folder in your Java IDE (IntelliJ recommended).
5. Run the main class (e.g., `Main.java` or your login form).
6. Make sure MySQL server (XAMPP) is running.


👤 **Author**

- Afia Jahin Rupali – Lead Developer & Designer

Responsible for full project development including Java coding, database design, simulation logic, and UI/UX design.  
  *(Sole contributor to this project)*

📞 Contact
For any queries-
Email: afiajahinrupali@gmail.com
