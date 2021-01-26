package com.cpsu.bantaydagatviolationrecorder.Data;

public class Violation {

    public static final String TABLE_VIOLATIONS = "violations";
    public static final String TABLE_PHOTOS = "photos";

    public static final String COL_ID = "v_id";
    public static final String COL_DATETIME = "v_datetime";
    public static final String COL_NAME = "v_name";
    public static final String COL_VSL_NO = "v_vslno";
    public static final String COL_OWN_PRMT = "v_ownprmt";
    public static final String COL_VSL_PRMT = "v_vslprmt";
    public static final String COL_OTHER_COMP = "v_othercomp";

    public static final String COL_PHOTO = "p_photo";

    private String vId;
    private String dateTime;
    private String name;
    private String vesselNo;
    private int ownersPermit;
    private int vesselPermit;
    private String otherComplaints;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVesselNo() {
        return vesselNo;
    }

    public void setVesselNo(String vesselNo) {
        this.vesselNo = vesselNo;
    }

    public int getOwnersPermit() {
        return ownersPermit;
    }

    public void setOwnersPermit(int ownersPermit) {
        this.ownersPermit = ownersPermit;
    }

    public int getVesselPermit() {
        return vesselPermit;
    }

    public void setVesselPermit(int vesselPermit) {
        this.vesselPermit = vesselPermit;
    }

    public String getOtherComplaints() {
        return otherComplaints;
    }

    public void setOtherComplaints(String otherComplaints) {
        this.otherComplaints = otherComplaints;
    }
}
