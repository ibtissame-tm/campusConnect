package ma.ensam.campusconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponsableView {
    private final String nom;
    private final String initiales;
    private final String roleLabel;
}