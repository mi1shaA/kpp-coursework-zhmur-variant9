package ua.edu.nupp.kpp.server;

public class Measure {
    private long id;
    private long auditId;
    private String title;
    private String state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public Measure() {}

    public Measure(long id, long auditId, String title, String state, double f1, double f2, double f3) {
        this.id = id;
        this.auditId = auditId;
        this.title = title;
        setState(state);
        setFactors(f1, f2, f3);
    }

    public void setFactors(double f1, double f2, double f3) {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException("Factors must be in range 0-100");
        }
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralIndex = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
    }

    public void setState(String state) {
        if (!state.equals("PROPOSED") && !state.equals("APPROVED") &&
                !state.equals("IMPLEMENTATION") && !state.equals("COMPLETED")) {
            throw new IllegalArgumentException("Invalid lifecycle state: " + state);
        }
        this.state = state;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAuditId() { return auditId; }
    public void setAuditId(long auditId) { this.auditId = auditId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getState() { return state; }

    public double getF1() { return f1; }
    public double getF2() { return f2; }
    public double getF3() { return f3; }
    public double getIntegralIndex() { return integralIndex; }
}