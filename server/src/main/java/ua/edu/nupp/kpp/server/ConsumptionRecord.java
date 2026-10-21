package ua.edu.nupp.kpp.server;

public class ConsumptionRecord {
    private long id;
    private long objectId;
    private String resourceType;
    private double consumptionValue;
    private String period;

    public ConsumptionRecord() {}

    public ConsumptionRecord(long id, long objectId, String resourceType, double consumptionValue, String period) {
        this.id = id;
        this.objectId = objectId;
        this.resourceType = resourceType;
        this.consumptionValue = consumptionValue;
        this.period = period;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getObjectId() { return objectId; }
    public void setObjectId(long objectId) { this.objectId = objectId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public double getConsumptionValue() { return consumptionValue; }
    public void setConsumptionValue(double consumptionValue) { this.consumptionValue = consumptionValue; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
}
