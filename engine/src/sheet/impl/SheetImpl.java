package sheet.impl;

import expression.parser.FunctionParser;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.layout.api.Layout;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet, Serializable {

    private Map<Coordinate, Cell> activeCells;
    private Layout layout;
    private String name;
    private int version = 1;
    private int numberCellsThatHaveChanged;

    public SheetImpl(){
        this.activeCells = new HashMap<>();
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public Cell getCell(Coordinate coordinate) { return this.activeCells.get(coordinate); }

    @Override
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

    @Override
    public int getNumberCellsThatHaveChanged() { return numberCellsThatHaveChanged; }

    @Override
    public void setCell(int row, int column, String value) {
        if(row > layout.getRows())
            throw new IndexOutOfBoundsException("Row " + row + " out of bounds");
        if(column > layout.getColumns())
            throw new IndexOutOfBoundsException("Column " + CoordinateImpl.convertNumberToAlphabetString(column) + " out of bounds");

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        Cell cell = activeCells.get(coordinate);

        if(cell == null) {
            EffectiveValue effectiveValue = new EffectiveValueImpl(CellType.setCellType(value), value); // example implementation
            int version = this.getVersion(); // You may have a different way to manage versions

            cell = new CellImpl(row, column, value, version, this);
            activeCells.put(coordinate, cell);
        }
        cell.setCellOriginalValue(value);
        cell.calculateEffectiveValue();
        numberCellsThatHaveChanged++;
    }

    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {

        numberCellsThatHaveChanged = 0;
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

  //      Cell originalCell = getCell(coordinate);
//        if(originalCell != null) {
//             for(Cell dependsOnCell : originalCell.getDependsOn())
//                dependsOnCell.getInfluencingOn().remove(originalCell);
//             for(Cell influenceOnCell : originalCell.getInfluencingOn())
//                 influenceOnCell.getDependsOn().remove(originalCell);
//        }

        SheetImpl newSheetVersion = copySheet();

        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        //newSheetVersion.getCell(row,column)
        newSheetVersion.activeCells.put(coordinate, newCell);

        Boolean success = false;
        try {
            newSheetVersion.updateInfluenceAndDepends();
            List<Cell> orderedCells = newSheetVersion
                    .orderCellsForCalculation();
            List <Cell> cellsThatHaveChanged = orderedCells
                                    .stream()
                                    .filter(Cell::calculateEffectiveValue)
                                    .collect(Collectors.toList());

            //version += 1;
            // successful calculation. update sheet and relevant cells version
            // int newVersion = newSheetVersion.IncreaseVersion();
            //cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));
            success = true;
            newSheetVersion.numberCellsThatHaveChanged = cellsThatHaveChanged.size();
            return newSheetVersion;
        } catch (Exception e) {
            e.printStackTrace();
            // deal with the runtime error that was discovered as part of invocation
            throw new RuntimeException(e.getMessage());
        } finally {
//            if (!success) {
//                // If an error occurred, return the original sheet
//                return this;
//            }
        }
    }

    private List<Cell> orderCellsForCalculation() {
    // Initialize the in-degree map and adjacency list for the dependency graph
    Map<Cell, Integer> inDegree = new HashMap<>();
    Map<Cell, List<Cell>> adjList = new HashMap<>();

    // Initialize in-degree and adjacency list for all active cells
    for (Cell cell : activeCells.values()) {
        inDegree.put(cell, 0); // Start with 0 in-degree for all cells
        adjList.put(cell, new ArrayList<>()); // Initialize adjacency list
    }

    // Build the graph by populating adjacency list and in-degree map
    for (Cell cell : activeCells.values()) {
        List<Coordinate> dependencies = cell.getDependsOn();

        for (Coordinate coord : dependencies) {
            Cell dependencyCell = activeCells.get(coord);
            if (dependencyCell != null) {
                adjList.get(dependencyCell).add(cell); // dependencyCell -> cell
                inDegree.put(cell, inDegree.get(cell) + 1); // Increment in-degree of cell
            }
        }
    }

    // Initialize a queue for cells with no dependencies (in-degree 0)
    Queue<Cell> queue = new LinkedList<>();
    for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
        if (entry.getValue() == 0) {
            queue.add(entry.getKey());
        }
    }

    // Perform topological sorting
    List<Cell> sortedCells = new ArrayList<>();
    while (!queue.isEmpty()) {
        Cell currentCell = queue.poll();
        sortedCells.add(currentCell);

        // Reduce in-degree of all cells that depend on the current cell
        for (Cell dependentCell : adjList.get(currentCell)) {
            inDegree.put(dependentCell, inDegree.get(dependentCell) - 1);
            if (inDegree.get(dependentCell) == 0) {
                queue.add(dependentCell);
            }
        }
    }

    // Check if a valid topological order exists (i.e., no cycles)
    if (sortedCells.size() != activeCells.size()) {
        throw new RuntimeException("Circular dependency detected in cells");
    }

    return sortedCells;
}


//    private List<Cell> orderCellsForCalculation(){
//    // Initialize the in-degree map and adjacency list for Deps-Influence graph
//    Map<Cell, Integer> inDegree = new HashMap<>();
//    Map<Cell, List<Cell>> adjList = new HashMap<>();
//
//    // Initialize in-degree and adjacency list for all active cells
//    for (Cell cell : activeCells.values()) {
//        inDegree.put(cell, 0);
//        adjList.put(cell, new ArrayList<>());
//    }
//
//    // Build the graph by populating adjacency list and in-degree map
//    for (Cell cell : activeCells.values()) {
//        // Get the list of cells that the current cell influences
//        List<Cell> influencedCells = cell.getInfluencingOn(); // Assumes getInfluences() returns a list of cells influenced by this cell
//
//        for (Cell influencedCell : influencedCells) {
//            adjList.get(cell).add(influencedCell);
//            inDegree.put(influencedCell, inDegree.get(influencedCell) + 1);
//        }
//    }
//
//    // Initialize a queue for cells with no dependencies (in-degree 0)
//    Queue<Cell> queue = new LinkedList<>();
//    for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
//        if (entry.getValue() == 0) {
//            queue.add(entry.getKey());
//        }
//    }
//
//    // Perform topological sorting
//    List<Cell> sortedCells = new ArrayList<>();
//    while (!queue.isEmpty()) {
//        Cell currentCell = queue.poll();
//        sortedCells.add(currentCell);
//
//        // Reduce in-degree of all adjacent cells
//        for (Cell neighbor : adjList.get(currentCell)) {
//            inDegree.put(neighbor, inDegree.get(neighbor) - 1);
//            if (inDegree.get(neighbor) == 0) {
//                queue.add(neighbor);
//            }
//        }
//    }
//
//    // Check if a valid topological order exists
//    if (sortedCells.size() != activeCells.size()) {
//        throw new RuntimeException("Circular dependency detected in cells");
//    }
//
//    return sortedCells;
//}
//
//    //version 1
////    private List<Cell> orderCellsForCalculation() {
////        // Initialize the in-degree map and adjacency list for the dependency graph
////        Map<Cell, Integer> inDegree = new HashMap<>();
////        Map<Cell, List<Cell>> adjList = new HashMap<>();
////
////        // Initialize in-degree and adjacency list for all active cells
////        for (Cell cell : activeCells.values()) {
////            inDegree.put(cell, 0); // Start with 0 in-degree for all cells
////            adjList.put(cell, new ArrayList<>()); // Initialize adjacency list
////        }
////
////        // Build the graph by populating adjacency list and in-degree map
////        for (Cell cell : activeCells.values()) {
////            for (Cell dependency : cell.getDependsOn()) {
////                adjList.get(dependency).add(cell); // dependency -> cell
////                inDegree.put(cell, inDegree.get(cell) + 1); // Increment in-degree of cell
////            }
////        }
////
////        // Initialize a queue for cells with no dependencies (in-degree 0)
////        Queue<Cell> queue = new LinkedList<>();
////        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
////            if (entry.getValue() == 0) {
////                queue.add(entry.getKey());
////            }
////        }
////
////        // Perform topological sorting
////        List<Cell> sortedCells = new ArrayList<>();
////        while (!queue.isEmpty()) {
////            Cell currentCell = queue.poll();
////            sortedCells.add(currentCell);
////
////            // Reduce in-degree of all cells that depend on the current cell
////            for (Cell dependentCell : adjList.get(currentCell)) {
////                inDegree.put(dependentCell, inDegree.get(dependentCell) - 1);
////                if (inDegree.get(dependentCell) == 0) {
////                    queue.add(dependentCell);
////                }
////            }
////        }
////
////        // Check if a valid topological order exists (i.e., no cycles)
////        if (sortedCells.size() != activeCells.size()) {
////            throw new RuntimeException("Circular dependency detected in cells");
////        }
////
////        return sortedCells;
////    }
//
////    private List<Cell> orderCellsForCalculation() {
////    // Initialize the in-degree map and adjacency list for the dependency graph
////    Map<Cell, Integer> inDegree = new HashMap<>();
////    Map<Cell, List<Cell>> adjList = new HashMap<>();
////
////    // Initialize in-degree and adjacency list for all active cells and their dependencies
////    for (Cell cell : activeCells.values()) {
////        inDegree.put(cell, 0); // Start with 0 in-degree for all cells
////        adjList.put(cell, new ArrayList<>()); // Initialize adjacency list
////
////        // Ensure that all dependencies are also added to the inDegree and adjList maps
////        for (Cell dependency : cell.getDependsOn()) {
////            inDegree.putIfAbsent(dependency, 0); // Add with in-degree 0 if not already present
////            adjList.putIfAbsent(dependency, new ArrayList<>()); // Ensure dependency has an adjacency list
////        }
////    }
////
////    // Build the graph by populating adjacency list and in-degree map
////    for (Cell cell : activeCells.values()) {
////        for (Cell dependency : cell.getDependsOn()) {
////            adjList.get(dependency).add(cell); // dependency -> cell
////            inDegree.put(cell, inDegree.get(cell) + 1); // Increment in-degree of cell
////        }
////    }
////
////    // Initialize a queue for cells with no dependencies (in-degree 0)
////    Queue<Cell> queue = new LinkedList<>();
////    for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
////        if (entry.getValue() == 0) {
////            queue.add(entry.getKey());
////        }
////    }
////
////    // Perform topological sorting
////    List<Cell> sortedCells = new ArrayList<>();
////    while (!queue.isEmpty()) {
////        Cell currentCell = queue.poll();
////        sortedCells.add(currentCell);
////
////        // Reduce in-degree of all cells that depend on the current cell
////        for (Cell dependentCell : adjList.get(currentCell)) {
////            inDegree.put(dependentCell, inDegree.get(dependentCell) - 1);
////            if (inDegree.get(dependentCell) == 0) {
////                queue.add(dependentCell);
////            }
////        }
////    }
////
////    // Check if a valid topological order exists (i.e., no cycles)
////    if (sortedCells.size() != activeCells.size()) {
////        throw new RuntimeException("Circular dependency detected in cells");
////    }
////
////    return sortedCells;
////}
//
//
    @Override
    public SheetImpl copySheet() {
        Boolean success = false;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            success = true;
            return (SheetImpl) ois.readObject();
        }
        catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            throw new RuntimeException(e);
        } finally {
            if (!success) {
                // If an error occurred, return the original sheet
                return this;
            }
        }
    }

    @Override
    public void IncreaseVersion () {
        version++;
    }

//    @Override
//    public int IncreaseVersion () {
//        return ++version;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetImpl sheet = (SheetImpl) o;
        return version == sheet.version && Objects.equals(activeCells, sheet.activeCells) && Objects.equals(layout, sheet.layout) && Objects.equals(name, sheet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeCells, layout, name, version);
    }

    private void updateInfluenceAndDepends() throws Exception {
        List<String> refCells;
        for (Cell cell : activeCells.values()) {
            cell.getInfluencingOn().clear();
            cell.getDependsOn().clear();
            refCells = extractRefCells(cell.getOriginalValue());
            for(String refCell : refCells) {
                Coordinate coordinate = CoordinateFactory.from(refCell);
                CoordinateFactory.isValidCoordinate(coordinate, this);
                cell.getDependsOn().add(coordinate);
                getCell(coordinate).getInfluencingOn().add(cell.getCoordinate());
            }
        }
    }

    public List<String> extractRefCells(String input) {
        List<String> refCells = new ArrayList<>();

        // Regular expression to match "REF" (case-insensitive) followed by a cell reference with unlimited letters
        Pattern pattern = Pattern.compile("\\bREF\\s*,\\s*([A-Z]+[0-9]+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add the cell references to the list
        while (matcher.find()) {
            refCells.add(matcher.group(1));
        }

        return refCells;
    }
}
