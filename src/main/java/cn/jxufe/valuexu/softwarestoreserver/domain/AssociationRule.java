package cn.jxufe.valuexu.softwarestoreserver.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "association_rule")
@DynamicUpdate
@DynamicInsert
public class AssociationRule {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "software_id0")
    private long softwareId0;
    @Column(name = "software_id1")
    private long softwareId1;
    @Column(name = "software_id2")
    private long softwareId2;
    @Column(name = "software_id3")
    private long softwareId3;
    @Column(name = "software_id4")
    private long softwareId4;
    @Column(name = "support")
    private double support;
    @Column(name = "confidence")
    private double confidence;

    public AssociationRule( long softwareId0, long softwareId1, long softwareId2, long softwareId3, long softwareId4, int support, int confidence) {
        this.softwareId0 = softwareId0;
        this.softwareId1 = softwareId1;
        this.softwareId2 = softwareId2;
        this.softwareId3 = softwareId3;
        this.softwareId4 = softwareId4;
        this.support = support;
        this.confidence = confidence;
    }

    public AssociationRule() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getSoftwareId0() {
        return softwareId0;
    }

    public void setSoftwareId0(long softwareId0) {
        this.softwareId0 = softwareId0;
    }

    public long getSoftwareId1() {
        return softwareId1;
    }

    public void setSoftwareId1(long softwareId1) {
        this.softwareId1 = softwareId1;
    }

    public long getSoftwareId2() {
        return softwareId2;
    }

    public void setSoftwareId2(long softwareId2) {
        this.softwareId2 = softwareId2;
    }

    public long getSoftwareId3() {
        return softwareId3;
    }

    public void setSoftwareId3(long softwareId3) {
        this.softwareId3 = softwareId3;
    }

    public long getSoftwareId4() {
        return softwareId4;
    }

    public void setSoftwareId4(long softwareId4) {
        this.softwareId4 = softwareId4;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
