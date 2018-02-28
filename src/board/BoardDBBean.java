package board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//db�� �־��ֱ⸸ �ϴ� ���α׷�. ��� ������ �۾���� ���� �ν��Ͻ��� ������ �ʿ䰡 ����. 
//�̱��Ϲ��

public class BoardDBBean {
	private static BoardDBBean instance=new BoardDBBean();
	private BoardDBBean() {
		//�ܺο��� ������ �� ������ ������ private�� ���Ƴ���
	}
	public static BoardDBBean getInstance() {
		//instance��ü�� �ּ� ��ȯ
		return instance;
	}
	
	//getConnection�޼��� > connection��ü�� �� ��ü���� ���� �ٴ� ��
	//preparedStatement �� ����� �����ϸ� ��
	public static Connection getConnection(){
		Connection con=null;
		try {
			String jdbcUrl="jdbc:oracle:thin:@localhost:1521:orcl";
			String dbID="scott";
			String dbPass="tiger";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con=DriverManager.getConnection(jdbcUrl, dbID, dbPass);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	public void insertArticle(BoardDataBean article) {
		//db ������ ���� �޼���
		String sql="";
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int number=0;
		try {//serial number �����Ű�� ���� sql�� <-num�÷� 
			pstmt=con.prepareStatement("select boardser.nextval from dual");
			rs=pstmt.executeQuery();
			if(rs.next())
				number=rs.getInt(1)+1;
			//serial�� 1���� �����ϸ� 1����
			else
				number=1;
		
		//��ۿ� ref re_level re_step
		int num=article.getNum();
		int ref=article.getRef();
		int re_step=article.getRe_step();
		int re_level=article.getRe_level();
		if(num!=0) {//��� �� ��
			sql="update board set re_step=re_step+1 where ref=? and re_step>? and boardid=?";
			//���� �۵��� step�� �������ִ� ����
			//���ε��� ����� re_step�� ���� ���ƾ� �ϱ� ������, ������ �ٸ� ��۵��� re_step�� 1�� ������Ų��.
			//�����ۼ��ϴ� ����� re_step 0(���ۿ� ���� ���) > ������ re_step 1,2 > 2,3�� ����
			//�����ۼ��ϴ� ����� re_step 1(���(re_step1)�� ���� ���) > ������ re_step 2,3 > 3,4�� ���� 
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, ref);
			pstmt.setInt(2, re_step);
			pstmt.setString(3, article.getBoardid());
			pstmt.executeUpdate();
			re_step=re_step+1;
			//���� �ۼ��ϴ� ����� re_step�� 1 ������Ŵ. 
			//�����ۼ��ϴ� ����� re_step 0(���ۿ� ���� ���) > 1
			//�����ۼ��ϴ� ����� re_step 1(���(re_step1)�� ���� ���) > 2
			re_level=re_level+1;
			//����� level. ���� 0, ���ۿ� ���� ��� 1, ���(1)������ ��� 2 ...

		}else {//���� �� ��
			ref=number;re_step=0;re_level=0;
		}
			sql="insert into board(num,writer,email,subject,passwd,reg_date,"
					+ "ref,re_step,re_level,content,ip,boardid)"
					+ "values(?,?,?,?,?,sysdate,?,?,?,?,?,?)";
			pstmt=con.prepareStatement(sql);
			
			pstmt.setInt(1, number);
			pstmt.setString(2, article.getWriter());
			pstmt.setString(3, article.getEmail());
			pstmt.setString(4, article.getSubject());
			pstmt.setString(5, article.getPasswd());
			pstmt.setInt(6, ref);	//����� ref���� ���۰� ����(������ ref������)
			pstmt.setInt(7, re_step);
			pstmt.setInt(8, re_level);
			pstmt.setString(9, article.getContent());
			pstmt.setString(10, article.getIp());
			pstmt.setString(11, article.getBoardid());
			pstmt.executeUpdate();
			
		}catch(SQLException e1) {
			e1.printStackTrace();
			
		}finally {
			close(con,rs,pstmt);	
			//�ݴ� �޼���
			//�Ű������� �־����. ������� �͵� �ް� ������
		}
	}
	public int updateArticle(BoardDataBean article) {
		Connection conn=null;
		PreparedStatement pstmt=null;
		int chk=0;
		try {
			conn=getConnection();
			String sql="update board set writer=?,email=?,subject=?,content=? where num=? and passwd=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, article.getWriter());
			pstmt.setString(2, article.getEmail());
			pstmt.setString(3, article.getSubject());
			pstmt.setString(4, article.getContent());
			pstmt.setInt(5, article.getNum());
			pstmt.setString(6, article.getPasswd());
		
			chk=pstmt.executeUpdate();
			//executeUpdate (��� row�� ������ �Ǿ����� int�� ������). 1�̸� �� ��, 0�̸� �ȵ� ��
			
		}catch(Exception e) {}
		finally {
			close(conn,null,pstmt);	
		}
		
		return chk;
	
	}
	public int getArticleCount(String boardid) {
		//�� ���� ���� count����
		int x=0;
		String sql="select nvl(count(*),0) "
				+ "from board where boardid=?";
		//* �ȵ�. ���� �������� ���� nvl()�Է�. (null�� �� 0)
		Connection con=getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int number=0;
		
		try {
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, boardid);
			//?�� parameter boardid �־���
			rs=pstmt.executeQuery();
			//�������� �� ��� �� resultSet�� ���
			if(rs.next()) {
				x=rs.getInt(1);
				//rs�� ��� �� �Խ��� �� ���� x�� �־���
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(con,rs,pstmt);
		}
		return x;
	}
	public List getArticles(int startRow,int endRow,String boardid) {
		//�Խ��� ���� startRow����  endRow���� list�� ��� ����
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		List articleList=null;
		String sql="";
		try {
			conn=getConnection();
			sql="select*from"
					+ "(select rownum rnum,a.* from"
					+ "(select num,writer,email,subject,passwd,"
					+ "reg_date,readcount,ref,re_step,re_level,content,"
					+ "ip from board where boardid=? order by ref desc,re_step)"
					+ "a) where rnum between ? and ?";
				//ref �������� ����(�ֽ� �ۺ���)���� ����. 
				//rownum ���� ���� ���� �ֽű�. startRow <- �ҷ����� ������ ���� �ֽ� ��
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, boardid);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				articleList=new ArrayList();
				//BoardDataBean article=new BoardDataBean();
				//BoardDataBean do-while�ۿ��ٰ� ���� �ȵ�. ���� ���������� �ҷ��� �����Ͱ����� ���� ������.
				//��� articleList ���� ��ü���� �ϳ��� �ν��Ͻ��� ����Ű�Ե�.
				do {
					BoardDataBean article=new BoardDataBean();
					//while�� �ݺ� �� ������ ��ü ����
					article.setNum(rs.getInt("num"));
					article.setWriter(rs.getString("writer"));
					article.setEmail(rs.getString("email"));
					article.setSubject(rs.getString("subject"));
					article.setPasswd(rs.getString("passwd"));
					article.setReg_date(rs.getTimestamp("reg_date"));
					article.setReadcount(rs.getInt("readcount"));
					article.setRef(rs.getInt("ref"));
					article.setRe_step(rs.getInt("re_step"));
					article.setRe_level(rs.getShort("re_level"));
					article.setContent(rs.getString("content"));
					article.setIp(rs.getString("ip"));
					//������ article ��ü���ٰ� ���� ��������
					articleList.add(article);
					//articleList�� ������ article��ü�� ����
				}while(rs.next());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			close(conn,rs,pstmt);
		}
		return articleList;	
	}
	public int deleteArticle(int num,String passwd,String boardid)throws Exception{
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="delete from board where num=? and passwd=?";
		int x=-1;
		try {
			conn=getConnection();
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, passwd);
			x=pstmt.executeUpdate();
			//1(����� row�� ��)�Ǵ� 0��� 
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			close(conn,rs,pstmt);
			
		}return x;
		
	}

	public BoardDataBean getArticle(int num,String boardid,String check) {
		//�۹�ȣ, �Խ��ǹ�ȣ, �Ѿ������ �������� ����(content/update)�� Ȯ���ؼ� �ش� ���ǿ� �´� BoardDataBean��ü�� ��������
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		BoardDataBean article=null;
		//�����͸� ���� BoardDataBean��ü
		String sql="";
		try {
			conn=getConnection();
			
			if(check.equals("content")) {
				//content���� �Ѿ�� ���� ���� ���� (��ȸ �� �ø��� ����)
				sql="update board set readcount=readcount+1"
						+"where num=? and boardid=?";
				//num�� boardid�� �������� �����͸� ã�� �� ��ȸ �� ����
				pstmt=conn.prepareStatement(sql);
				//parameter�� num�� boardid
				pstmt.setInt(1, num);
				pstmt.setString(2, boardid);
				pstmt.executeUpdate();
			}

			sql="select * from board where num=? and boardid=?";
			pstmt=conn.prepareStatement(sql);
			//parameter�� num�� boardid
			pstmt.setInt(1, num);
			pstmt.setString(2, boardid);
			rs=pstmt.executeQuery();	//������ ����
			
			if(rs.next()) {
				//true�� �� BoardDataBean�� �־����
				article=new BoardDataBean();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPasswd(rs.getString("passwd"));
				article.setReg_date(rs.getTimestamp("reg_date"));
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setRe_step(rs.getInt("re_step"));
				article.setRe_level(rs.getShort("re_level"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				//������ article ��ü���ٰ� ���� ��������
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			close(conn,rs,pstmt);
		}
		return article;
		
	}
	
	
	public void close(Connection con,ResultSet rs,PreparedStatement pstmt) {
		//Ŀ�ؼ� �ݴ� �޼���
		if(rs!=null)
			try {
				rs.close();
			}catch(SQLException ex) {}
		if(pstmt!=null)
			try {
				pstmt.close();
			}catch(SQLException ex) {}
		if(con!=null)
			try {
				con.close();
			}catch(SQLException ex) {}
	}
	

}
