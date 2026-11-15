package ua.edu.nupp.kpp.client;

public class ConsumptionRecord {
    private Long id;
    private Long objectId;
    private String resourceType;
    private double consumptionValue;
    private String period;

    public ConsumptionRecord() { }

    public ConsumptionRecord(Long id, Long objectId, String resourceType, double consumptionValue, String period) {
        this.id = id;
        this.objectId = objectId;
        this.resourceType = resourceType;
        this.consumptionValue = consumptionValue;
        this.period = period;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getObjectId() { return objectId; }
    public void setObjectId(Long objectId) { this.objectId = objectId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public double getConsumptionValue() { return consumptionValue; }
    public void setConsumptionValue(double consumptionValue) { this.consumptionValue = consumptionValue; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
}
