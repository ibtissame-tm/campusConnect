package ma.ensam.campusconnect.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostView {
    private Long id;
    private String titre;
    private String contenu;
    private String type;               // ACTUALITE | COMPETITION
    private String visibilite;         // PUBLIQUE | PRIVEE
    private String datePublication;    // texte : "Il y a 2 h"
    private String imageUrl;
    private long likesCount;           // branchés à l'étape 3
    private long commentsCount;
    private boolean likedByMe;
    private ClubView club;
    private List<CommentView> comments = new ArrayList<>();
}