package co.nilin.vaccine.dto;

import co.nilin.vaccine.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest   {
    List<Transaction> transfers;
    long manufacture;

}
