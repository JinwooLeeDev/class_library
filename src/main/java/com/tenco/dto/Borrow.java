package com.tenco.dto;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
// 도서 대출 기록을 닫는 DTO
// DTO는 테이블과 꼭 1:1 맞출 필요가 없음
// SQL 실행 결과를 담는 그릇이므로 join으로 가져온 컬럼 결과도 담을 수 있다.

public class Borrow {
    private int id;
    private int bookId;
    private int studentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    private String bookTitle;
}
