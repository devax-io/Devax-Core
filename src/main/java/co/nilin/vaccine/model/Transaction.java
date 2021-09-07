package co.nilin.vaccine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    long id;
    long from;
    long to;
    @Column("create_date")
    String createDate;
    double value;
    @Column("more_info")
    String moreInfo;


    public Transaction setFrom(long from) {
        this.from = from;
        return this;
    }

    public Transaction setTo(long to) {
        this.to = to;
        return this;
    }

    public Transaction setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public Transaction setValue(double value) {
        this.value = value;
        return this;
    }
}
