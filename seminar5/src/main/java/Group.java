import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="StudentsGroup")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    UUID id;

    @Column
    String name;

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
