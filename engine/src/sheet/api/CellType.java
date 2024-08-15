package sheet.api;

public enum CellType {
    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class) ;

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }

    public static boolean isBoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

    public static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    public boolean isString(String value) {
//        return !isBoolean(value) && !isNumber(value);
//    }

    public static CellType setCellType(String value){
        if(isBoolean(value)){
            return CellType.BOOLEAN;
        }
        if(isNumber(value)){
            return CellType.NUMERIC;
        }
        return CellType.STRING;
    }
}
