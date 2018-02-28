
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css"></head>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>

	<p class="w3-left" style="padding-left:30px;">
	</p>
	<div class="w3-container">
		<span class="w3-center w3-large">
			<h3>${boardid }(전체 글 : ${count})
			</h3>
		</span>
		<p class="w3-right w3-padding-right-large">
			<a href="writeForm">글쓰기</a>
			<!-- writeForm.jsp->writeForm으로 수정 -->
		</p>
		
		<c:if test="${count==0 }">
		
			<table class="table-boarded" width="700">
				<tr class="w3-grey">
					<td align="center">게시판에 저장된 글이 없습니다.</td>
				</tr>
			</table>
		</c:if>
		<c:if test="${count!=0 }">
		<table class="w3-table-all" width="700">
			<tr class="w3-grey">
			<td align="center" width="50">번호</td>
			<td align="center" width="250">제목</td>
			<td align="center" width="100">작성자</td>
			<td align="center" width="150">작성일</td>
			<td align="center" width="50">조회</td>
			<td align="center" width="100">IP</td>
			</tr>
		<c:forEach var="article" items="${articleList}">
	
			<tr height="30">
			
			<td align="center" width="50">${number}</td>
			<c:set var="number" value="${number-1}"/>
			<!-- 페이지 내 number를 설정. number를 찍고 number-1을 다시 number에 넣음 -->
			<!-- 글 번호 표시 -->
			<td width="250">
			<c:if test="${article.re_level>0}">
	
				<img src="../images/level.gif" width="${5*article.re_level }" height="16">
				<img src="../images/re.gif">
			</c:if>
			<c:if test="${article.re_level==0 }">
				<img src="../images/level.gif" height="16" >
			</c:if>
			<a href= "content?num=${article.num }&pageNum=${currentPage}">
			<%-- 글 제목 클릭 시 내용보기로 넘어감 --%>
			${article.subject }</a>
			<!-- 글제목 출력 -->
			<c:if test="${article.getReadcount()>=20 }">
				<img src="../images/hot.gif" border="0" height="16">
			</c:if>
				</td>
				<td align="center" width="100">${article.writer}</td>
				<!-- 글쓴이 출력 -->
				<td align="center" width="150">${article.reg_date}</td>
            	<!-- 글쓴 날짜 출력 -->	    
				<td align="center" width="50">${article.readcount}</td>
 				<!-- 조회수 출력 -->
				<td align="center" width="100">${article.ip}</td>
				<!-- ip 출력 -->
				</tr>

		</c:forEach>
		
			</table>

				</c:if>

	</div>
<!-- 게시글 목록 페이지 하단의 페이지 이동 부분 [1][2][3]  -->
	<div class="w3-center">
		<c:if test="${count>0 }">
			<c:if test="${startPage>bottomLine }">
				<a href="list?pageNum=${startPage-bottomLine}">[이전]</a>				
				
			</c:if>
			
			<c:forEach var="i" begin="${startPage }" end="${endPage }">
			
					<a href="list?pageNum=${i}&boardid=${boardid }">
					<!-- pageNum넘김 -->
					<c:if test="${i!=currentPage }">
							[${i}]
					</c:if>
					<c:if test="${i==currentPage }">
						<font color='red'>[${i}]</font>						
					</c:if>
					</a>			
			</c:forEach>
				
				<c:if test="${endPage<pageCount}">
			
					<a href="list.jsp?pageNum=${startPage+bottomLine}">[다음]</a>	
				</c:if>

	
			</c:if>
</div>
</body>
</html>