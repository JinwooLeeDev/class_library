package com.tenco.view;

import com.tenco.dto.Student;
import com.tenco.service.LibraryService;

import java.util.Scanner;

// 사용자의 입출력을 처리하는 View 클래스

// [역할]
// 키보드 입력을 받아 Service 에 넘기고, 결과를 화면에 출력한다.
// SQL 을 직접 실행하지 않고, 업무 규칙토 판단하지 않습니다.
//  "빈 값인가", "숫자인가" 같은 입력 형식을 검사하고 서비스단에 맞는 객체내 값을 구해서 일을 위임한다.
public class LibraryView {

    private final LibraryService libraryService = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 현재 로그인한 학생 정보가 null 아니라면 로그인된 상태로 보면 된다.
    // 만약 null 이라면 로그인이 필요한 기능에서 로그인 요청을 먼저 유도 해야 한다.
    private Integer currentStudentId = null;
    private String currentStudentName = null;
    private Student currentStudent = null;

    // 프로그램 메인 루프
    // [처리순서]
    // 1. 메뉴를 출력한다.
    // 2. 번호를 입력 받는다
    // 3. 번호에 맞는 메서드를 호출한다
    // 4. 호출 중 SQLException 이 나면 에러 메세지를 출력하고 다시 1번으로 돌아간다.
    // 5. 0번을 입력하면 프로그램 종료 또는 return 루프를 빠져 나간다.
    public void start() {
        while (true) {
            // 메뉴 출력
            // 사용자 입력값 받기

            // 예외 처리가 반드시 필요 하다.
//            switch ("선택번호") {
//                case 1: 기능 호출
//            }

        }
    }

    // 해당 기능을 각각에 메서드로 설계

}
