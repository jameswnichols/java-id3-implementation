import java.util.HashMap;

public record EntropyAttributeResult(double entropy, int total, HashMap<String, Integer> targetClassResults) {
}
