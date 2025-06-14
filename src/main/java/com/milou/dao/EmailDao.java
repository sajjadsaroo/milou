package com.milou.dao;

import com.milou.models.Email;
import com.milou.models.User;
import com.milou.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
            Long res = session.createQuery("SELECT max(e.id) FROM Email e", Long.class)
                    .uniqueResult();
            if (res != null)
                return res;

            return 0L;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Email> findReceivedEmailsByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT e FROM Email e JOIN e.recipients r WHERE r.id = :userId ORDER BY e.timestamp DESC";

            return session.createQuery(hql, Email.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error finding received emails for user ID " + userId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Optional<Email> findEmailByCodeForUser(Long userId, String code) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Load full email with all recipients and sender
            String hql = "SELECT DISTINCT e FROM Email e " +
                    "LEFT JOIN FETCH e.sender s " +
                    "LEFT JOIN FETCH e.recipients r " +
                    "WHERE e.message_code = :mCode";

            Optional<Email> emailOptional = session.createQuery(hql, Email.class)
                    .setParameter("mCode", code)
                    .getResultStream()
                    .findFirst();

            if (emailOptional.isEmpty()) {
                transaction.rollback();
                return Optional.empty();
            }

            Email email = emailOptional.get();

            boolean isSender = email.getSender().getId().equals(userId);
            boolean isRecipient = email.getRecipients().stream()
                    .anyMatch(u -> u.getId().equals(userId));

            if (!isSender && !isRecipient) {
                transaction.rollback();
                return Optional.empty();
            }

            // فقط برای دریافت‌کننده ها وضعیت خوانده شدن را آپدیت کن
            if (isRecipient) {
                String sql = "UPDATE email_recipient SET is_read = 1 WHERE user_id = :userId AND email_id = :emailId";

                session.createNativeQuery(sql)
                        .setParameter("userId", userId)
                        .setParameter("emailId", email.getId())
                        .executeUpdate();
            }

            transaction.commit();
            return Optional.of(email);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            System.err.println("Error finding/updating email with code " + code + " for user ID " + userId);
            e.printStackTrace();
            return Optional.empty();
        }
    }

//    public Optional<Email> findEmailByCodeForUser(Long userId , String code) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//
//
//            String hql = "SELECT DISTINCT e FROM Email e " +
//                    "LEFT JOIN FETCH e.sender s " +
//                    "LEFT JOIN FETCH e.recipients r " +
//                    "WHERE e.message_code = :mCode AND (s.id = :userId OR r.id = :userId)";
//
//            return session.createQuery(hql, Email.class)
//                    .setParameter("mCode", code)
//                    .setParameter("userId", userId)
//                    .getResultStream()
//                    .findFirst();
//
//        } catch (Exception e) {
//            System.err.println("Error finding email with code " + code + " for user ID " + userId);
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }

    public List<Email> findSentEmails(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT e FROM Email e WHERE e.sender.id = :userId ORDER BY e.timestamp DESC";

            return session.createQuery(hql, Email.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error finding sent emails for user ID " + userId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Email> findUnreadEmailsByUserIdNative(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String sql = "SELECT e.* FROM emails e " +
                    "JOIN email_recipient er ON e.id = er.email_id " +
                    "WHERE er.user_id = :userId AND er.is_read = 0 " +
                    "ORDER BY e.timestamp DESC";

            return session.createNativeQuery(sql, Email.class)
                    .setParameter("userId", userId)
                    .getResultList();

        } catch (Exception e) {
            System.err.println("Error finding unread emails for user ID " + userId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
