package co.nilin.vaccine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("lot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lot {
    @Id
    long id;
    @Column("ref_id")
    String refId;
    String manufacture;
    String pod;
    String exp;
    String orig;
    long agent;
    double cost;
    @Column("payment_proof")
    String paymentProof;
    @Column("create_date")
    String createDate;
    @Column("more_info")
    String moreInfo;
}
