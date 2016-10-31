package jp.kotmw.splatoon.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseTest {

	private static String url = "jdbc:mysql://localhost/splatplayerdatas";
	private static String user = "root";
	private static String path = "root";


	public static void TestDatabase() {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//ドライバクラスをロード

			//データベースへ接続
			con = DriverManager.getConnection("jdbc:mysql://localhost/Sample_db", user, path);
			System.out.println("MySQLに接続できました");

			String sql = "select name,bloodType,age from Sample_table";

			//ステートメントオブジェクトを作成
			ps = con.prepareStatement(sql);

			//クエリーを実行して結果のセットを取得
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				String name = rs.getString("name");
				String bloodtype = rs.getString("bloodType");
				String age = rs.getString("age");

				System.out.println("name: "+name);
				System.out.println("bloodtype: "+bloodtype);
				System.out.println("age: "+age);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null)
					ps.close();
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setPlayerData(String name, String uuid, int win, int lose, int winstreak, int maxwinstreak, boolean finalwin, int rank, int exp, int totalexp) {
		Connection con = null;
		int finalwin_i = finalwin ? 1 : 0;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			con = DriverManager.getConnection(url, user, path);

			Statement statement = con.createStatement();
			String sql = "insert into main values (\""+name+"\",\""+uuid+"\","+win+","+lose+","+winstreak+","+maxwinstreak+","+finalwin_i+","+rank+","+exp+","+totalexp+");";
			statement.executeUpdate(sql);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void UpdatePlayerFile(String uuid) {
		Connection con = null;
		Statement statement = null;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			con = DriverManager.getConnection(url, user, path);
			statement = con.createStatement();
			String sql = "select * from main where UUID = \""+uuid+"\";";
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()) {
				//PlayerFiles.UpdatePlayerFile(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(con != null)
					con.close();
				if(statement != null)
					statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
