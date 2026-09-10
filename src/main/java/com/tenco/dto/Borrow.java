package com.tenco.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
// 도서 대출 기록을 담는 DTO
// DTO는 테이블과 꼭 1:1 맞출 필요가 없습니다.
// SQL 실행 결과를 담는 그릇 이므로 JOIN 으로 가져온 컬럼 결과도 담을 있습니다.
public class Borrow {

    private int id;
    private int bookId;
    private int studentId;
    private String bookTitle;
    private String studentName;
    private LocalDate borrowDate;
    private Date returnDate;
}
