package co.nilin.vaccine.dto;

import co.nilin.vaccine.model.Vial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVialsRequest {
    String parentRefId;
    long manufacture;
    List<Vial> vials;

}
