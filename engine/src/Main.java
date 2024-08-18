import impl.EngineImpl;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
//        Sheet sheet = new SheetImpl();
//        sheet.setCell(0, 0, "Hello, World!");
//
//        Cell cell = sheet.getCell(0, 0);
//        Object value = cell.getEffectiveValue().getValue();
//        System.out.println(value);
//
//        String s = cell.getEffectiveValue().extractValueWithExpectation(String.class);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the full path to the file: ");

        // Get the file path from the user
        String filePath = scanner.nextLine();

        EngineImpl engine = new EngineImpl();

        engine.LoadFile(filePath);

        System.out.println("Sheet Name: " + engine.getSheet().getName());
        System.out.println("Version: " + engine.getSheet().getVersion());
        System.out.println();

        scanner.close();///////
    }
}
