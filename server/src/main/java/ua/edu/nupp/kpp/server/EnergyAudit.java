package ua.edu.nupp.kpp.server;

public class EnergyAudit {
    private long id;
    private long objectId;
    private String auditDate;
    private String auditor;
    private String notes;

    public EnergyAudit() {}

    public EnergyAudit(long id, long objectId, String auditDate, String auditor, String notes) {
        this.id = id;
        this.objectId = objectId;
        this.auditDate = auditDate;
        this.auditor = auditor;
        this.notes = notes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getObjectId() { return objectId; }
    public void setObjectId(long objectId) { this.objectId = objectId; }

    public String getAuditDate() { return auditDate; }
    public void setAuditDate(String auditDate) { this.auditDate = auditDate; }

    public String getAuditor() { return auditor; }
    public void setAuditor(String auditor) { this.auditor = auditor; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}