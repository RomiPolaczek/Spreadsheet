package sheet.api;

public interface EffectiveValue {
    CellType getCellType();
    Object getValue();
    <T> T extractValueWithExpectation(Class<T> type);
    boolean equals(Object o);
    int hashCode();
}
