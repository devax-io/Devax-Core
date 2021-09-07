package co.nilin.vaccine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("account")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    long id;
    String address;
    String type;
    String name;
    String alias;
    String contact;
    @Column("create_date")
    String createDate;
    @Column("more_info")
    String moreInfo;
}
