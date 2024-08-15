package expression.api;

import sheet.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
}
