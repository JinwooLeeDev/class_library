package com.tenco.dao;

import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // TODO - 추후 사용하는 측 확인해서 리턴 타입 결정
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
        List<Student> studentList = new ArrayList<>();
        String sql = """
                SELECT * FROM students ORDER BY id
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // 자료구조에 생성된 Student 객체를 하나씩 추가 함.
                studentList.add(createStudent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return studentList;
    }


    // 학번으로 학생 조회
    public Student getStudentByStudentId(String studentId) {
        String sql = """
                SELECT *
                FROM students
                WHERE student_id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            // 굳이 결과집합이 단일행이라면 While 구문을 사용할 필요가 없다.
            if (rs.next()) {
                return createStudent(rs);
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

}
