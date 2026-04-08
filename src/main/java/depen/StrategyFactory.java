package depen;

public final class StrategyFactory {
    private StrategyFactory() {
    }

    public static ChoiceStrategy create(boolean intelligentStrategySelected) {
        if (intelligentStrategySelected) {
            return new Prediction();
        }

        return new RandomStrategy();
    }

    public static String getStrategyName(boolean intelligentStrategySelected) {
        return intelligentStrategySelected ? "Intelligent AI" : "Random AI";
    }
}
