# Mazerr - Maze Generator
This project is a Java app which visualizes three maze-generating methods:
- *Recursive Backtracking*
- *Prim's Algorithm*
- *Kruskal's Algorithm*
## Table of Contents
1. [Features](#features)
2. [Generating Methods](#generating-methods)
3. [Installation](#installation)
4. [Contribution](#contributing)
5. [License](#license)

<img src="https://github.com/Falanger-debug/mazerr/blob/master/mazerUsage-min.jpg"/>

### Features
- **Interactive Control Panel**: Start, pause, continue generating
- **Maze Customization**: Adjust maze size, tempo and method of generating it
- **Real-time Simulation**: Generate maze and please your eyes
### Generating Methods
- **Recursive Backtracking**:
  This algorithm randomly visits neighboring cells, marking them as part of the maze path. If it encounters a cell with no unvisited neighbors, it "backtracks" to the last visited cell with unvisited neighbors and continues. This creates a maze with a single path from start to finish, without cycles.
  
 ![mazerr_recursive_backtracking-ezgif com-optimize](https://github.com/user-attachments/assets/7a1eda1e-eb44-467e-bc83-13011945a861)
 
- **Prim's Algorithm**:
  Prim's algorithm builds a maze by starting from an initial cell and adding random walls to a frontier list. For each step, it picks a random wall from the frontier, connects it to an unvisited cell, and adds new walls to the frontier. This method results in a maze with multiple paths and a more "natural" look.

  ![mazerr_prim-ezgif com-optimize](https://github.com/user-attachments/assets/6895496b-41ef-457e-8644-e45bd23d5f60)
  
- **Kruskal's Algorithm**:
  Kruskal’s algorithm generates a maze by treating each cell as a distinct set and gradually merging them. It randomly removes walls between cells, merging sets as it connects them. This continues until all cells are interconnected in a single set, creating a maze with complex paths and potential cycles.

  ![mazerr_kruskal-ezgif com-optimize](https://github.com/user-attachments/assets/de8edfea-c0e3-4075-a55c-bb813501fd01)
  
### Installation
Ensure you have Java21 or higher installed, along with Maven. Then follow these steps:
1. **Clone the repository:**
   ```
   git clone https://github.com/Falanger-debug/mazerr.git
   cd mazerr
   ```
2. **Build the project:** Maven automates dependencies and builds the project. To do so, run:
   ```
   mvn clean install
   ```
3. **Run the application:** Once the project is built, start the Spring application using Maven:
   ```
   msv spring-boot:run
   ```
4. **Access the application:** After starting, you can access the Mazerr interface in your web browser at:
   ```
   http://localhost:8080
   ```
### Contributing
1. Fork the repository.
2. Create a new branch ```"git checkout -b feature-branch```.
3. Make changes, commit them and push to your fork.
4. Open a pull request with a clear description of your changes.
## License
This project is licensed under the MIT License.
   

