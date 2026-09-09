package com.tenco.dao;

import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 학생 등록 기능
    public int addStudent(Student student) {
        int rows = 0;
        String sql = """
                INSERT INTO students(name, student_id)
                VALUES (?, ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());
                // INSERT, UPDATE, DELETE 에 사용 해야 함.
                rows = pstmt.executeUpdate();
                System.out.println(rows + " 행이 추가 되었습니다");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    // 학생 전체 조회 기능
    public List<Student> getAllStudent() {
        // select * from students;
        List<Student> studentList = new ArrayList<>();
        // 중간에 빈 로직을 다른 예제 코드 보면서 완성해주세요
        String sql = """
                select * from students
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    studentList.add(createStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return studentList;
    }
    // 학번으로 학생 조회 --> 로그인
    public Student searchStudent(String studentId) {
        String sql = """
            SELECT * FROM students WHERE student_id = ?
            """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return createStudent(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Student createStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setStudentId(rs.getString("student_id"));
        return student;
    }



    // 테스트 코드 작성
    public static void main(String[] args) {
        // 샘플값 준비
//        Student student = new Student("티모2", "90230002");
//        StudentDAO studentDAO = new StudentDAO();
//        studentDAO.addStudent(student);

    }

}
