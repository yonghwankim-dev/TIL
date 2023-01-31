import java.util.List;

public class Menu {
    private List<MenuItem> itesm;

    public Menu(List<MenuItem> itesm) {
        this.itesm = itesm;
    }

    public MenuItem choose(String menuName) {
        return itesm.stream()
                .filter(menuName::equals)
                .findFirst()
                .orElse(null);
    }
}
