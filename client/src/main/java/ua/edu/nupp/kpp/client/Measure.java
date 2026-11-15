package ua.edu.nupp.kpp.client;

public class Measure {
    private Long id;
    private Long auditId;
    private String title;
    private MeasureState state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public Measure() { }

    public Measure(Long id, Long auditId, String title, MeasureState state,
                   double f1, double f2, double f3, double integralIndex) {
        this.id = id;
        this.auditId = auditId;
        this.title = title;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralIndex = integralIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public MeasureState getState() { return state; }
    public void setState(MeasureState state) { this.state = state; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}
