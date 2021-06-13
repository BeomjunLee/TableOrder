# TableOrder
테이블의 QR 코드를 이용해 자리에서 핸드폰으로 주문을 할 수 있는 시비스입니다.(진행중)

- JAVA 11
- Gradle
- Spring Boot
- Mysql
- React
- Android 

<br>

## WBS
[WBS 링크](https://docs.google.com/spreadsheets/d/10HF0dJNTQ_49pUTCzdZiGQYHhlVW0PRulTBQQK-qIwQ/edit?usp=sharing)

<br>

## APP 이용 방법
1. 사용자가 매장 QR코드를 인식하면 해당 매장의 메뉴 페이지로 이동한다.
   - 메뉴 페이지에는 매장에 대한 설명 및 대표 메뉴, 사이드 메뉴, 모든 메뉴가 표시된다.
   - 메뉴 클릭시 메뉴에 대한 상세 설명 및 리뷰가 표시된다.

2. 메뉴페이지에 있는 메뉴를 바로 주문하거나 장바구니에 담을 수 있다.
   - 메뉴 페이지에는 주문하기 버튼, 장바구니에 담기 버튼 및 아이콘이 존재한다.
   - 주문하기 버튼 클릭시 요청사항을 작성할 수 있으며 주문이 완료 됐다는 알림 문구가 표기된다.

3. 장바구니에 담은 모든 메뉴를 장바구니 페이지에서 요청사항과 함께 한번에 주문 가능하며 주문이 완료 됐다는 알림 문구가 표시됨과 동시에 주문내역 페이지로 이동한다.
   - 장바구니 페이지에는 주문하기 버튼, 장바구니에 담은 메뉴를 취소할 수 있는 버튼이 존재한다.
   
4. 주문 내역 페이지에서 주문 상세 정보 및 주문 완료 문구가 띄워지며 주문 취소 및, 주문 내역 삭제가 가능하다.
   - 주문 상세 정보는 주문 메뉴, 총 결제금액, 문의사항이 표기된다.
   - 주문 취소 버튼 및 주문 내역 삭제 버튼이 존재한다.

<br>

## ADMIN 이용 방법
### 메인 페이지
1. 사용자가 서비스를 이용하기 위해서는 회원가입을 통해 
    아이디를 생성하고 로그인을 해야한다.
    - 로그인 생성: 우측 상단 'login' 버튼
    - 회원가입: 우측 상단 'sign-up' 버튼
2. 로그인을 하면 관리 페이지(Management)이동 하는 버튼이 생긴다.
     - 인증을 받아야만 보여지는 버튼이다.

<br>

### 관리 페이지
<strong>[테이블]</strong> -> 사용자가 테이블의 상태를 확인 및 테이블 수정이 가능한 페이지
1. 식당의 테이블 갯수에 따라 사용자가 수정 할 수 있다.
    - 우측 상단 '테이블 수정' 버튼 기능.
2. 테이블을 클릭했을 경우 해당 테이블의 자세한 정보들이 우측 view에 표시된다.
   - 정보: 주문내역, 요청사항 그리고 가격 합계
3. 테이블의 상태에 따라 색이 다르다.
    - 파란색: 접수만 되어있다는 표시
    - 노란색: 손님들이 주문한 음식을 조리중이라는 표시
    - 빨간색: 조리중과 조리완료 이 두가지 상태를 가지고 있는 표시
    - 회색   : 조리완료라는 상태 표시

<br>

<strong>[메뉴]</strong> -> 사용자가 메뉴를 설정할 수 있는 페이지이다.
1. '카테고리 추가' 버튼을 통해 사용자가 필요한 카테고리 추가할 수 있다.
    - 우측 상단 '카테고리 추가'
2. 오른쪽 view를 통해 메뉴를 추가할 수 있다.
    - 이미지, 음식이름, 가격, 음식 설명, 음식 분류
  
<br>

<strong>[접수내역]</strong> -> 주방에서 현재 접수, 조리중, 조리완료를 확인할 수 있는 페이지
1. '접수'는 현재 접수된 사항을 확인 할 수 있고 '조리시작'버튼을 누르면 '접수 상태'가 '조리중 상태'로 변경된다.
2.  손님이 특정 음식을 빼고 싶다고 할 시 '수정' 버튼을 눌러 특정 음식을 삭제 할 수 있다.
3.  손님이 주문을 빼고 싶을 시 '삭제' 버튼을 눌러 해당 접수 내역을 삭제 할 수 있다.
4.  접수내역이 조리중으로 이동되었을 시 해당 접수내역의 음식, 수량을 수정할 수 없다.
       - 수정: 음식들 우측에 x표시가 나타나고 누른다음 수정 완료를 클릭하여 해당 음식을 접수내역에서 삭제 및 수량 조절을 할 수 있다.
       - 삭제: 접수 내역을 삭제한다.
       - 시간표시: 우측 상단에 해당 접수가 얼마나 경과되었는지 확인 할 수 있다.
<br>

### Entity 매핑
<img src="https://user-images.githubusercontent.com/69130921/121806580-5eec5880-cc8b-11eb-97c4-c45b4688ea8d.png"><br>

