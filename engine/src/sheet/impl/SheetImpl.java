package sheet.impl;

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
    public Cell getCell(Coordinate coordinate) { return this.activeCells.get(coordinate); }

    @Override
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

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
            List<Cell> dependsOn = new ArrayList<>();
            List<Cell> influencingOn = new ArrayList<>();

            cell = new CellImpl(row, column, value, version, this);
            activeCells.put(coordinate, cell);
        }
        cell.setCellOriginalValue(value);
        cell.calculateEffectiveValue();
    }

    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

        SheetImpl newSheetVersion = copySheet();
        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(coordinate, newCell);
        Boolean success = false;
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
            //cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));
            success = true;
            return newSheetVersion;
        } catch (Exception e) {
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
        // Initialize the in-degree map and adjacency list for Deps-Influence graph
        Map<Cell, Integer> inDegree = new HashMap<>();
        Map<Cell, List<Cell>> adjList = new HashMap<>();

        // Initialize in-degree and adjacency list for all active cells
        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, 0);
            adjList.put(cell, new ArrayList<>());
        }

        // Build the graph by populating adjacency list and in-degree map
        for (Cell cell : activeCells.values()) {
            // Get the list of cells that the current cell influences
            List<Cell> influencedCells = cell.getInfluencingOn(); // Assumes getInfluences() returns a list of cells influenced by this cell

            for (Cell influencedCell : influencedCells) {
                adjList.get(cell).add(influencedCell);
                inDegree.put(influencedCell, inDegree.get(influencedCell) + 1);
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

            // Reduce in-degree of all adjacent cells
            for (Cell neighbor : adjList.get(currentCell)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Check if a valid topological order exists
        if (sortedCells.size() != activeCells.size()) {
            throw new RuntimeException("Circular dependency detected in cells");
        }

        return sortedCells;
    }

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
}
