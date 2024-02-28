import java.util.HashMap;

public record HighestInfoGain(double infoGain, String Class, HashMap<String, EntropyAttributeResult> attributeEntropies) {
}