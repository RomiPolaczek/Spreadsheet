# Spreadsheet Application ("Sheet-Cell")


## 🚀 Overview
The **Sheet-Cell** project is a Java-based implementation of a fully functional spreadsheet application. This project explores the foundational and advanced features of spreadsheet software, such as formula handling, version control, and collaborative editing. Developed as a modular and scalable system, the project supports multiple use cases, including personal data management, visualization, and team collaboration.

### 🌟 Key Features
1. **🧮 Functions and Formulas**:
   - Includes a variety of built-in functions for arithmetic, string manipulation, and logical operations.
   - Designed with the Open/Closed Principle (OCP), allowing easy extension with new functions directly in the codebase.

2. **🔄 Versioning**:
   - Tracks all spreadsheet changes.
   - Enables users to view the spreadsheet's state at any prior version.

3. **🌐 Client-Server Architecture**:
   - Supports multiple clients editing the same spreadsheet simultaneously.
   - Server: Hosted on a Tomcat server, managing spreadsheet data and user permissions.
   - Client: A desktop application built using JavaFX for graphical interaction.
   - Communication is achieved through HTTP, ensuring reliable data exchange.

4. **🔒 Permissions Model**:
   - Customizable access control for each spreadsheet.
   - Defines who can view or edit a given sheet.

5. **💾 Import and Export**:
   - Allows saving and loading spreadsheets to/from files.
   - Supported formats include XML and JSON for interoperability and data management.

6. **⚡ Concurrency and Multithreading**:
   - Engineered to handle concurrent user updates while ensuring data consistency.
   - Implements synchronization mechanisms to manage shared resources safely.

## ⚙️ How to Run

### Prerequisites
- **Java**: Ensure JDK 21+ is installed.
- **Server Deployment**: Install and configure a Tomcat server for hosting the application backend.
- **Runtime Dependencies**: Include JavaFX runtime for graphical features.
- **Input Files**: Prepare spreadsheets in XML or JSON format.

### Steps
1. **Server Setup**:
   - Deploy the server-side JAR to Tomcat and start the server.

2. **Run the Client**:
   - Launch the desktop application:
     ```bash
     java -jar gui-client.jar
     ```

3. **Console Interface**:
   - Use the console application for command-line interaction:
     ```bash
     java -jar console-app.jar
     ```

4. **File Management**:
   - Import or export spreadsheets using supported file formats.

### Note:
For client-server usage, ensure the server is running and accessible to clients. Modify configuration files to specify server addresses and ports.

## 🌈  Advanced Capabilities
1. **🔍 Dynamic Analysis**:
   - Highlights dependencies and influences between cells.
   - Displays a clear relationship network to assist in understanding spreadsheet logic.

2. **📏 Range Operations**:
   - Allows defining and managing ranges (e.g., `A1..C5`).
   - Supports operations like filtering, sorting, and applying functions across ranges.

3. **📊 Graph Creation**:
   - Visualize spreadsheet data using bar and line charts.
   - Define X and Y axes through ranges for flexible graphing.

## 🏗️ Project Architecture
The application follows a modular structure for better scalability and maintainability:
- **Engine**: Core logic for spreadsheets, including formula evaluation, dependency management, and versioning.
- **UI**: Console-based and JavaFX graphical interfaces.
- **Server**: Manages client requests and maintains centralized spreadsheet data.
- **Data Handling**: Processes XML and JSON formats for importing/exporting spreadsheets.

## 💪 Challenges and Solutions
1. **Concurrency**:
   - Implemented locks and thread-safe data structures to handle concurrent updates.
   - Optimized multithreading to maintain performance while ensuring data integrity.

2. **Extensibility**:
   - Designed the system to be easily extendable with additional functions and features.
   - Used clean coding practices to adhere to modular design principles.

3. **User Experience**:
   - Focused on intuitive UI/UX for both console and GUI interfaces.
   - Provided meaningful error messages and feedback for better usability.

## 📌 Use Cases
1. **Individual Productivity**:
   - Manage personal data and perform calculations.
2. **Team Collaboration**:
   - Edit and share spreadsheets in real time.
3. **Data Visualization**:
   - Create graphs and charts to analyze trends and insights.

