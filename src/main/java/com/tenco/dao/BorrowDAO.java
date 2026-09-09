package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    // 2.1 - 대상 도서 대출 가능 여부 - SELECT
    // 2.2 - 도서 대출 기록 - INSERT

    // 3. 도서 반납 처리 (트랜잭션)
    // 3.1 대출 기록 확인 - SELECT
    // 3.2 반납 기록 등록 - UPDATE

    private Borrow cantBorrow (ResultSet rs) throws SQLException {
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
