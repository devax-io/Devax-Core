package co.nilin.vaccine.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("balance_report")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceReport {
    @Id
    long id;
    long owner;
    double balance;
    @Column("last_modified")
    String lastModified;
    @Column("more_info")
    String moreInfo;
    double blocked;

    public BalanceReport setLastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public BalanceReport setBalance(double balance) {
        this.balance = balance;
        return this;
    }
}
