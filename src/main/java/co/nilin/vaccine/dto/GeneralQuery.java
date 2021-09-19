package co.nilin.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralQuery {
    long totalLots;
    long totalVials;
    long totalInjections;
    Long totalAccounts;
    long totalTransactions;
}
