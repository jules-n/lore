package year.exp.lore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    String name;
    String surname;
    String fathername;

    @Override
    public String toString() {
        return surname + " " + name + " " + fathername;
    }
}
