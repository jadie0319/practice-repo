package jadie.ticketproduct.infra.api;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoSearchRequest {
    private String query;
    private int size;
}


/*
query	String	검색을 원하는 질의어	O
category_group_code	String	카테고리 그룹 코드 카테고리로 결과 필터링을 원하는 경우 사용	X
x	String	중심 좌표의 X 혹은 경도(longitude) 값 특정 지역을 중심으로 검색할 경우 radius와 함께 사용 가능	X
y	String	중심 좌표의 Y 혹은 위도(latitude) 값 특정 지역을 중심으로 검색할 경우 radius와 함께 사용 가능	X
radius	Integer	중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 중심좌표로 쓰일 x,y와 함께 사용 (단위: 미터(m), 최소: 0, 최대: 20000)	X
rect	String	사각형의 지정 범위 내 제한 검색을 위한 좌표 지도 화면 내 검색 등 제한 검색에서 사용 가능 좌측 X 좌표, 좌측 Y 좌표, 우측 X 좌표, 우측 Y 좌표 형식	X
page	Integer	결과 페이지 번호 (최소: 1, 최대: 45, 기본값: 1)	X
size	Integer	한 페이지에 보여질 문서의 개수 (최소: 1, 최대: 45, 기본값: 15)	X
sort	String	결과 정렬 순서
distance 정렬을 원할 때는 기준 좌표로 쓰일 x, y와 함께 사용 distance 또는 accuracy(기본값: accuracy)	X
 */
