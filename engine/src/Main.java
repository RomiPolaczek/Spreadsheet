import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.impl.SheetImpl;

public class Main {
    public static void main(String[] args) {
        Sheet sheet = new SheetImpl();
        sheet.setCell(0, 0, "Hello, World!");

        Cell cell = sheet.getCell(0, 0);
        Object value = cell.getEffectiveValue().getValue();
        System.out.println(value);

        String s = cell.getEffectiveValue().extractValueWithExpectation(String.class);
    }
}
