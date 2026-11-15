package ua.edu.nupp.kpp.client;

public class EnergyAudit {
    private Long id;
    private Long objectId;
    private String auditDate;
    private String auditor;
    private String notes;

    public EnergyAudit() { }

    public EnergyAudit(Long id, Long objectId, String auditDate, String auditor, String notes) {
        this.id = id;
        this.objectId = objectId;
        this.auditDate = auditDate;
        this.auditor = auditor;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getObjectId() { return objectId; }
    public void setObjectId(Long objectId) { this.objectId = objectId; }

    public String getAuditDate() { return auditDate; }
    public void setAuditDate(String auditDate) { this.auditDate = auditDate; }

    public String getAuditor() { return auditor; }
    public void setAuditor(String auditor) { this.auditor = auditor; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Аудит #" + id + " (" + auditDate + ")";
    }
}
