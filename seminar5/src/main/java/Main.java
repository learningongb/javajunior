import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Order;
import org.hibernate.query.SortDirection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();

            try (SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {
                // Вставка тестовых данных
                insertData(sessionFactory);

                Session session = sessionFactory.openSession();
                //
                System.out.println("Выборка всех групп");
                Query query = session.createQuery("FROM Group");
                query.getResultList().forEach(System.out::println);

                System.out.println("Выборка всех студентов");
                query = session.createQuery("FROM Student");
                query.getResultList().forEach(System.out::println);

                System.out.println("Выборка студентов с условием");
                query = session.createQuery("select st from Student st inner join st.group gr where gr.name=:name")
                        .setParameter("name", "Группа 2");
                query.getResultList().forEach(System.out::println);

                System.out.println("Выборка кроме первого студента");
                query = session.createQuery("FROM Student").setFirstResult(1);
                query.getResultList().forEach(System.out::println);

                System.out.println("Выборка только первого студента");
                query = session.createQuery("FROM Student").setMaxResults(1);
                query.getResultList().forEach(System.out::println);

                System.out.println("Удаление студента");
                Student delStudent = (Student) session
                        .createQuery("FROM Student where first_name=:name")
                        .setParameter("name", "Oleg")
                        .getSingleResult();
                session.beginTransaction();
                session.remove(delStudent);
                session.getTransaction().commit();

                System.out.println("Удаление студента");
                Student updStudent = (Student) session
                        .createQuery("FROM Student where first_name=:name")
                        .setParameter("name", "Ivan")
                        .getSingleResult();
                updStudent.setFirst_name("Kirill");
                session.beginTransaction();
                session.merge(updStudent);
                session.getTransaction().commit();

                System.out.println("Выборка для проверки удаления и изменения");
                session.createQuery("FROM Student")
                        .getResultList()
                        .forEach(System.out::println);

                session.close();
            };


        }
    }

    static void insertData(SessionFactory sessionFactory) throws SQLException {
        Session session = sessionFactory.openSession();

        Group group1 = new Group();
        group1.setName("Группа 1");
        Group group2 = new Group();
        group2.setName("Группа 2");

        Student student1 = new Student();
        student1.setFirst_name("Ivan");
        student1.setSecond_name("Ivanov");
        student1.setGroup(group1);

        Student student2 = new Student();
        student2.setFirst_name("Petr");
        student2.setSecond_name("Petrov");
        student2.setGroup(group1);

        Student student3 = new Student();
        student3.setFirst_name("Mihail");
        student3.setSecond_name("Mihailov");
        student3.setGroup(group2);

        Student student4 = new Student();
        student4.setFirst_name("Oleg");
        student4.setSecond_name("Olegov");
        student4.setGroup(group2);

        Transaction transaction = session.beginTransaction();
        session.persist(group1);
        session.persist(group2);
        session.persist(student1);
        session.persist(student2);
        session.persist(student3);
        session.persist(student4);
        transaction.commit();
        session.close();

    }
}
