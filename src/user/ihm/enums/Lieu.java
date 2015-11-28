package user.ihm.enums;

/**
 * Created by Yoan on 28/11/2015.
 */
public enum Lieu {
    Toulouse("Toulouse"),
    Bordeaux("Bordeaux"),
    Paris("Paris"),
    Aucun("Aucun");

    private final String name;

    private Lieu(String s) {
        name = s;
    }



    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
