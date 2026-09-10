package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    // 1.1 현재 대출중인 도서 목록 조회
    // 1.2 join해서 도서 이름까지 출력
    public List<Borrow> getBorrowedBooks() {
        List<Borrow> borrowList = new ArrayList<>();
        String sql = """
                select br.*, b.title as book_title
                from borrows br
                inner join books b
                    on br.book_id = b.id
                where br.return_date is null
                """;
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        borrowList.add(cantBorrow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return borrowList;
    }

    // 2. 도서 대출 기능 (트랜잭션)
    // 처리 순서
    // 1. DB 연결을 얻고 자동 커밋을 끈다. (트랜잭션 시작)
    // 2. 도서가 존재하고 대출 가능한 상태인지 확인 -- SELECT
    // 3. borrows 테이블에 대출 기록 -- INSERT
    // 4. books 테이블의 available을 false로 변경 -- UPDATE
    // 5. 2번부터 4번까지 모두 성공하면 commit, 하나라도 실패하면 rollback
    // 6. 자동커밋을 원래대로 돌리고, 연결을 닫는다.
    public void borrowBook(int bookId, int studentId) throws SQLException {

        Connection conn = null;
        // try-with-resources로 선언하지 않은 이유
        // catch 블록에서 rollback을 호출하려면 conn 변수가 catch 안에서도 보여야 한다.
        // 그래서 try 바깥에 선언하고 finally에서 직접 닫는다.
        try {
            // 1. 트랜잭션 시작
            conn = DatabaseUtil.getConnection();
            // 기본값 autoCommit = true 이고, 이 상태에서는 SQL 한줄 한줄마다 즉시 확정(반영)이 된다.
            // 이 값 false로 변경하면 commit()을 호출하기 전까지 모든 변경 사항이 임시 상태로 남게된다.
            conn.setAutoCommit(false);
            // 2.1 - 대상 도서 대출 가능 여부 - SELECT
            String checkSql = """
                    select available from books where id = ?
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("존재하지 않는 도서입니다 : " + bookId);
                    }
                    if (rs.getBoolean("available")) {
                        throw new SQLException("현재 대출중인 도서입니다. 반납 후 이용가능합니다.");
                    }
                }
            }
            // 3. 코드가 여기까지 내려온다면 대출 가능한 bookId 이다. --> 대출 기록 처리
            String borrowSql = """
                    insert into borrows (book_id, student_id, borrow_date)
                    values (?, ?, ?)
                    """;
            PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql);
            borrowPstmt.setInt(1, bookId);
            borrowPstmt.setInt(2, studentId);
            // java.time.LocalDate 를 JDBC가 이해하는 java.sql.Date로 변환해야한다.
            borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
            int rows = borrowPstmt.executeUpdate();
            if (rows < 0) {
                throw new SQLException("적용된 기록이 없습니다.");
            }
            // 4. 도서 상태 변경 (대출 불가로 해당 도서 처리)
            String updateSql = """
                    update books set availble = false
                    where id = ?
                    """;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();
            }
            // 5. 여기까지 모두 성공했다면 확정
            conn.commit();

        } catch (Exception e) {
            // 5. 하나라도 실패 시 rollback 처리
            if (conn != null) {
                conn.rollback();
            }

            throw new RuntimeException(e);
            //conn.rollback();
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // 3. 도서 반납 처리 (트랜잭션)
    public void returnBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            String checkSql = """
                    select *
                    from borrows
                    where student_id = ?
                      and book_id = ?
                      and return_date is null
                    """;

            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, studentId);
                checkPstmt.setInt(2, bookId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("대출중인 도서가 없습니다.");
                    }
                    // 추후 단계에서 어느 행을 수정할지 알아야 하므로 id를 미리 꺼내둔다.
                    bookId = rs.getInt("id");
                }
            }

            String updateSql = """
                    update borrows
                    set return_date = current_date
                    where student_id = ?
                      and book_id = ?
                      and return_date is null;
                    """;

            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, studentId);
                updatePstmt.setInt(2, bookId);
                updatePstmt.executeUpdate();
            }

            String availableSql = """
                    update books
                    set available = true
                    where id = ?
                    """;
            try (PreparedStatement availablePstmt = conn.prepareStatement(availableSql)) {
                availablePstmt.setInt(1, bookId);
                availablePstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    // 1. DB 연결을 얻고 자동 커밋을 끈다.
    // 2. 이 학생이 이 도서를 빌린 뒤 아직 반납하지 않은 기록이 있는지 확인 -- select
    // 3. 찾는 대출 기록의 return_date를 오늘 날짜로 update 한다. -- update
    // 4. books 테이블에 available을 true 변경 -- update
    // 5. 2~4 까지 모두 성공하면 commit, 하나라도 실패 시 rollback 처리
    // 6. 자동 커밋을 원래대로 되돌리고 연결을 닫는다.

    private Borrow cantBorrow(ResultSet rs) throws SQLException {
        Borrow borrow = new Borrow();
        borrow.setId(rs.getInt("id"));
        borrow.setBookId(rs.getInt("book_id"));
        borrow.setStudentId(rs.getInt("student_id"));
        borrow.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        borrow.setReturnDate(null);
        borrow.setBookTitle(rs.getString("book_title"));
        return borrow;
    }
}
