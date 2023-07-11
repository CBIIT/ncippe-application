package gov.nci.ppe.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ParticipantData")
public class ChartData {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name="MedDRADiseaseTerm")
    private String MedDRADiseaseTerm;
    public String getMedDRADiseaseTerm() {
        return MedDRADiseaseTerm;
    }

}
