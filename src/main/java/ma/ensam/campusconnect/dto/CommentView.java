package ma.ensam.campusconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentView {
    private final String auteur;
    private final String initiales;
    private final String contenu;
}