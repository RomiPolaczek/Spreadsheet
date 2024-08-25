package sheet.impl;

import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.layout.api.Layout;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet, Serializable {

    private Map<Coordinate, Cell> activeCells;
    private Layout layout;
    private String name;
    private int version = 1;

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
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

//    @Override
//    public void setCell(int row, int column, String value) {
//        if(row > layout.getRows())
//            throw new IndexOutOfBoundsException("Row " + row + " out of bounds");
//        if(column > layout.getColumns())
//            throw new IndexOutOfBoundsException("Column " + CoordinateImpl.convertNumberToAlphabetString(column) + " out of bounds");
//
//        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
//        Cell cell = activeCells.get(coordinate);
//
//        if(cell == null) {
//            EffectiveValue effectiveValue = new EffectiveValueImpl(CellType.setCellType(value), value); // example implementation
//            int version = this.getVersion(); // You may have a different way to manage versions
//            List<Cell> dependsOn = new ArrayList<>();
//            List<Cell> influencingOn = new ArrayList<>();
//
//            cell = new CellImpl(row, column, value, effectiveValue, version, dependsOn, influencingOn);
//            activeCells.put(coordinate, cell);
//        }
//        cell.setCellOriginalValue(value);
//        cell.calculateEffectiveValue();
//    }

    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

        SheetImpl newSheetVersion = copySheet();
        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(coordinate, newCell);

        try {
            List<Cell> cellsThatHaveChanged =
                    newSheetVersion
                            .orderCellsForCalculation()
                            .stream()
                            .filter(Cell::calculateEffectiveValue)
                            .collect(Collectors.toList());

            //version += 1;
            // successful calculation. update sheet and relevant cells version
            // int newVersion = newSheetVersion.increaseVersion();
            // cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));

            return newSheetVersion;
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }

    private List<Cell> orderCellsForCalculation() {
        // Create a map to store the in-degree of each cell
        Map<Cell, Integer> inDegree = new HashMap<>();

        // Create a map to store the adjacency list of the graph
        Map<Cell, List<Cell>> adjList = new HashMap<>();

        // Initialize the in-degree and adjacency list for each cell
        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, 0);
            adjList.put(cell, new ArrayList<>());
        }

        // Build the graph
        for (Cell cell : activeCells.values()) {
            List<Cell> dependencies = cell.getDependsOn(); // assuming getDependencies() returns a list of cells this cell depends on
            for (Cell dep : dependencies) {
                adjList.get(dep).add(cell);
                inDegree.put(cell, inDegree.get(cell) + 1);
            }
        }

        // Initialize a queue to store cells with no dependencies (in-degree 0)
        Queue<Cell> queue = new LinkedList<>();
        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Perform topological sorting
        List<Cell> sortedCells = new ArrayList<>();
        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            sortedCells.add(cell);

            // Decrease the in-degree of the neighboring cells
            for (Cell neighbor : adjList.get(cell)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // If we have sorted all cells, return the sorted list
        if (sortedCells.size() == activeCells.size()) {
            return sortedCells;
        } else {
            throw new RuntimeException("Circular dependency detected");
        }
    }

    @Override
    public SheetImpl copySheet() {
        // lots of options here:
        // 1. implement clone all the way (yac... !)
        // 2. implement copy constructor for CellImpl and SheetImpl

        // 3. how about serialization ?
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        }
        catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            e.printStackTrace();
            return this;
        }
    }

    @Override
    public void IncreaseVersion () {
        version++;
    }


}
