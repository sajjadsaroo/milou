package com.milou.utils;
import com.milou.dao.EmailDao;

public class CodeGenerator {
    private static final EmailDao emailDao = new EmailDao();
    private static int count = emailDao.count();

    public static String generate() {
        count++;
        return "msg"+ emailDao.findMaxId() + count + (int)(Math.random()*10);
    }

}