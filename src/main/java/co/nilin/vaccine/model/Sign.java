package co.nilin.vaccine.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class Sign {
    @Id
    long id;
    String agent;
    long act;
    String type;
    String createDate;
    String moreInfo;

}
