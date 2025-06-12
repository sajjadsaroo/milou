package com.milou.dao;
import com.milou.models.Email;
import com.milou.models.User;
import com.milou.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EmailDao {
    public void save(Email email) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(email);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Email findByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Email e WHERE e.message_code = :code", Email.class)
                    .setParameter("code", code)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
