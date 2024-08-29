package sheet.impl;

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
        SheetImpl newSheetVersion = copySheet();

        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(coordinate, newCell);

        try {
            newSheetVersion.updateInfluenceAndDepends();
            List<Cell> orderedCells = newSheetVersion
                    .orderCellsForCalculation();
            List <Cell> cellsThatHaveChanged = orderedCells
                                    .stream()
                                    .filter(Cell::calculateEffectiveValue)
                                    .collect(Collectors.toList());


            // successful calculation. update sheet and relevant cells version
            newSheetVersion.IncreaseVersion();
            cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newSheetVersion.getVersion()));
            newSheetVersion.numberCellsThatHaveChanged = cellsThatHaveChanged.size();
            return newSheetVersion;
        }
        catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException("Circular dependency detected in cells - a cell can not reference itself");
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
            throw new RuntimeException("Copy sheet failed.");
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

    private void updateInfluenceAndDepends(){
        List<String> refCells;
        for (Cell cell : activeCells.values()) {
            cell.getInfluencingOn().clear();
            cell.getDependsOn().clear();
            refCells = extractRefCells(cell.getOriginalValue());
            for(String refCell : refCells) {
                Coordinate coordinate = CoordinateFactory.from(refCell);
                if(getCell(coordinate) != null)
                {
                    CoordinateFactory.isValidCoordinate(coordinate, this);
                    cell.getDependsOn().add(coordinate);
                    getCell(coordinate).getInfluencingOn().add(cell.getCoordinate());
                }
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
