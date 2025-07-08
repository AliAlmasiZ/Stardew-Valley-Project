package models.enums;

public enum ProductQuality {
    NORMAL(1f),
    SILVER(1.25f),
    GOLD(1.5f),
    IRIDIUM(2f);

    private float value;

    ProductQuality(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public static ProductQuality getQuality(double value) {
        if (value >= 0.0f && value < 0.5f) {
            return ProductQuality.NORMAL;
        } else if (value >= 0.5f && value < 0.7f) {
            return ProductQuality.SILVER;
        } else if (value >= 0.7f && value < 0.9f) {
            return ProductQuality.GOLD;
        } else {
            return ProductQuality.IRIDIUM;
        }

    }
}
