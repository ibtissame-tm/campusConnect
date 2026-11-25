package ma.ensam.campusconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MembershipView {
    private final ClubView club;
    private final String statut;   // EN_ATTENTE | ACTIVE | INACTIVE
    private final String role;     // null | RESPONSABLE_MEDIA | RESPONSABLE_ADHESION
}