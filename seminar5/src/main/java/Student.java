import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table (name = "Student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    UUID id;

    @Column(name="first_name")
    String first_name;

    @Column(name="second_name")
    String second_name;

    @ManyToOne
    @JoinColumn(name="group_id")
    Group group;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", second_name='" + second_name + '\'' +
                ", group=" + group.getName() +
                '}';
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
