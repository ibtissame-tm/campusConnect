package ma.ensam.campusconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentRowView {
    private final Long id;
    private final String nom;
    private final String initiales;
    private final long clubsCount;   // clubs où l'étudiant est membre actif
}