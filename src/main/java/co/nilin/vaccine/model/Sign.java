package co.nilin.vaccine.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("sign")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sign {
    @Id
    long id;
    String agent;
    long act;
    String type;
    @Column("create_date")
    String createDate;
    @Column("more_info")
    String moreInfo;

}
