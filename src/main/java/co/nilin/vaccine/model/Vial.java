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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vial {
    @Id
    String id;
    @Column("ref_id")
    String refId;
    @Column("parent_id")
    long parentId;
    @Column("create_date")
    String createDate;
    @Column("more_info")
    String moreInfo;

    public Vial setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public Vial setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }
}
