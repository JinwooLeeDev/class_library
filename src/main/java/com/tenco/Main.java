package com.tenco;

import com.tenco.dao.BookDAO;
import com.tenco.dao.BorrowDAO;
import com.tenco.dao.StudentDAO;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;

import java.util.List;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {

        // 학생 전체 조회 테스트
        StudentDAO studentDAO = new StudentDAO();
        BookDAO bookDAO = new BookDAO();
        BorrowDAO borrowDAO = new BorrowDAO();
//        List<Student> studentList = studentDAO.getAllStudent();
//
//        for (Student student : studentList) {
//            System.out.println(student.toString());
//        }

        System.out.println(studentDAO.searchStudent("20230002"));

        System.out.println(bookDAO.getAllBooks());
        System.out.println(bookDAO.searchBooksByTitle("소프트웨어 공학"));


        System.out.println(borrowDAO.getBorrowedBooks());

    }
}