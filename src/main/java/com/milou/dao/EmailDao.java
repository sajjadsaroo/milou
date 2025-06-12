package com.milou.dao;
import com.milou.models.Email;
import com.milou.models.User;
import com.milou.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class EmailDao {
    public void save(Email email) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            List<User> persistentRecipients = new ArrayList<>();
            for (User u : email.getRecipients()) {
                User persistentUser = session.get(User.class, u.getId());
                if (persistentUser != null) {
                    persistentRecipients.add(persistentUser);
                }
            }
            email.setRecipients(persistentRecipients);

            session.persist(email);
            transaction.commit();
        } catch (Exception e) {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
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

    public int count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(e) FROM Email e", Long.class)
                    .uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Long findMaxId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long res =  session.createQuery("SELECT max(e.id) FROM Email e", Long.class)
                    .uniqueResult();
            if(res != null)
                return res;

            return 0L;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
