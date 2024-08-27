package expression.parser;

import expression.impl.*;
import sheet.api.CellType;
import expression.api.Expression;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.impl.SheetImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser implements Serializable {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for IDENTITY function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            String actualValue = arguments.get(0);
            if (CellType.isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (CellType.isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PLUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for PLUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new PlusExpression(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());


            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new MinusExpression(left, right);
        }
    },
    UPPER_CASE {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for UPPER_CASE function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression arg = parseExpression(arguments.get(0));

            // more validations on the expected argument types
            CellType argCellType = arg.getFunctionResultType();

            if (!argCellType.equals(CellType.STRING) && !argCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for UPPER_CASE function. Expected STRING, but got " + argCellType);
            }

            // all is good. create the relevant function instance
            return new UpperCaseExpression(arg);
        }
    },
    CONCAT {
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0));
            Expression right = parseExpression(arguments.get(1));

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.STRING) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.STRING) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected STRING, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new ConcatExpression(left, right);
        }
    },
    TIMES{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for TIMES function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for TIMES function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }

            // all is good. create the relevant function instance
            return new TimesExpression(left, right);
        }
    },
    DIVIDE{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for DIVIDE function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for DIVIDE function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }
            // all is good. create the relevant function instance
            return new DivideExpression(left, right);
        }

    },
    MOD{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MOD function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for MOD function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }
            // all is good. create the relevant function instance
            return new ModExpression(left, right);
        }
    },
    POW{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for POW function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim());
            Expression right = parseExpression(arguments.get(1).trim());

            // more validations on the expected argument types
            CellType leftCellType = left.getFunctionResultType();
            CellType rightCellType = right.getFunctionResultType();

            // support UNKNOWN type as its value will be determined at runtime
            if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
                    (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
                throw new IllegalArgumentException("Invalid argument types for POW function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
            }
            // all is good. create the relevant function instance
            return new PowExpression(left, right);
        }
    },
    ABS{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for ABS function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression arg = parseExpression(arguments.get(0).trim());

            // more validations on the expected argument types
            CellType argCellType = arg.getFunctionResultType();

            if (!argCellType.equals(CellType.NUMERIC) && !argCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for ABS function. Expected NUMERIC, but got " + arg.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new AbsExpression(arg);
        }
    },
    SUB{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for SUB function. Expected 3, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression source = parseExpression(arguments.get(0));
            Expression startIndex = parseExpression(arguments.get(1));
            Expression endIndex = parseExpression(arguments.get(2));

            CellType sourceCellType = source.getFunctionResultType();
            CellType startCellType = startIndex.getFunctionResultType();
            CellType endCellType = endIndex.getFunctionResultType();

            // more validations on the expected argument types
            if (!sourceCellType.equals(CellType.STRING) && sourceCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected the first argument to be STRING, but got " + sourceCellType);
            }

            if(!startCellType.equals(CellType.NUMERIC) && !startCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected the second argument to be NUMERIC, but got " + startCellType);
            }

            if(!endCellType.equals(CellType.NUMERIC) && !endCellType.equals(CellType.UNKNOWN)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected the third argument to be NUMERIC, but got " + endCellType);
            }

            // all is good. create the relevant function instance
            return new SubExpression(source, startIndex, endIndex);
        }
    },
    REF{
        @Override
        public Expression parse(List<String> arguments) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for REF function. Expected 1, but got " + arguments.size());
            }

            // verify indeed argument represents a reference to a cell and create a Coordinate instance. if not ok returns a null. need to verify it
            Coordinate target = CoordinateFactory.from(arguments.get(0).trim());
            if (target == null) {
                throw new IllegalArgumentException("Invalid argument for REF function. Expected a valid cell reference, but got " + arguments.get(0));
            }

            return new RefExpression(target);
        }
    }
    ;

    abstract public Expression parse(List<String> arguments);

    public static Expression parseExpression(String input) {

        if (input.startsWith("{") && input.endsWith("}")) {

            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);

            String functionName = topLevelParts.get(0).trim().toUpperCase();

            //remove the first element from the array
            topLevelParts.remove(0);
            return FunctionParser.valueOf(functionName).parse(topLevelParts);
        }

        // handle identity expression
        return FunctionParser.IDENTITY.parse(List.of(input));
    }

    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray()) {
            if (c == '{') {
                stack.push(c);
            }
            else if (c == '}') {
                stack.pop();
            }

            if (c == ',' && stack.isEmpty()) {
                // If we are at a comma and the stack is empty, it's a separator for top-level parts
                parts.add(buffer.toString());
                buffer.setLength(0); // Clear the buffer for the next part
            } else {
                buffer.append(c);
            }
        }

        // Add the last part
        if (buffer.length() > 0) {
            parts.add(buffer.toString());
        }

        return parts;
    }

//    public static void main(String[] args) {
//
//        //String input = "plus, {plus, 1, 2}, {plus, 1, {plus, 1, 2}}";
////        String input = "1";
////        parseMainParts(input).forEach(System.out::println);
//
////        String input = "{plus, 1, 2}";
//  //      String input = "{plus, {minus, 44, 22}, {plus, 1, 2}}";
//   //     String input = "{upper_case, hello world}";
////        String input = "4";
////        String input = "{MOD, 1 , 10 }";
// //       String input = "{POW, 2 , SS }";
//      //  String input = "{ABS, 2, 3}";
//        String input = "{Sub, romi  mi100, 4, 7}";
//      //  String input = "{Sub, ss, ss, w}";
//     //     String input = "{DIVIDE, 2, 0}";
//        Sheet sheet = new SheetImpl();
//      //  String input = "{concat, hello    ,world}";
//
//
//        Expression expression = parseExpression(input);
//        EffectiveValue result = null;
//        try {
//            result = expression.eval(sheet);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("result: " + result.getValue() + " of type " + result.getCellType());
//    }

}