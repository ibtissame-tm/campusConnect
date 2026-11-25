package ma.ensam.campusconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActiviteView {
    private final String label;     // "Lun", "Mar"...
    private final long count;
    private final int percent;      // hauteur de la barre, 0 à 100
}