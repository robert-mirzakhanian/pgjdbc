package org.postgresql.test.jdbc2;

import java.sql.*;
import junit.framework.TestCase;

import org.postgresql.test.JDBC2Tests;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class UpdateableResultTest extends TestCase
{

  public UpdateableResultTest( String name )
  {
    super( name );
  }

  public void testUpdateable()
  {
		try
		{
			Connection con = JDBC2Tests.openDB();
      JDBC2Tests.createTable(con, "updateable","id int primary key, name text, notselected text");
      JDBC2Tests.createTable(con, "second","id1 int primary key, name1 text");

      Statement st1 = con.createStatement();
      boolean retVal = st1.execute( "insert into updateable ( id, name, notselected ) values (1, 'jake', 'avalue')" );
      assert( retVal== false );

      retVal = st1.execute( "insert into second (id1, name1) values (1, 'jake')" );
      assertTrue( !retVal );
      st1.close();

			Statement st = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
			ResultSet rs = st.executeQuery( "select id, name, notselected from updateable" );

			assertNotNull(rs);

			while (rs.next())
			{
        rs.updateInt( "id",2 );
				rs.updateString( "name","dave" );
        rs.updateRow();
        assertTrue( rs.getInt("id") == 2 );
        assertTrue( rs.getString("name").equals("dave"));
        assertTrue( rs.getString("notselected").equals("avalue") );

        rs.deleteRow();
        rs.moveToInsertRow();
        rs.updateInt("id",3);
        rs.updateString("name", "paul");

        rs.insertRow();

        assertTrue( rs.getInt("id") == 3 );
        assertTrue( rs.getString("name").equals("paul"));
        assertTrue( rs.getString("notselected") == null );

			}

			rs.close();

      rs = st.executeQuery("select id1, id, name, name1 from updateable, second" );
      try
      {
        while( rs.next() )
        {
          rs.updateInt( "id",2 );
          rs.updateString( "name","dave" );
          rs.updateRow();
        }


        assertTrue( "should not get here, update should fail", false );
      }
      catch (SQLException ex){}

      try
      {
          rs = st.executeQuery("select oid,* from updateable");
          if ( rs.first() )
          {
            rs.updateInt( "id", 3 );
            rs.updateString( "name", "dave3");
            rs.updateRow();
            assertTrue(rs.getInt("id") == 3 );
            assertTrue(rs.getString("name").equals("dave3"));

            rs.moveToInsertRow();
            rs.updateInt( "id", 4 );
            rs.updateString( "name", "dave4" );

            rs.insertRow();
            rs.updateInt("id", 5 );
            rs.updateString( "name", "dave5" );
            rs.insertRow();

            rs.moveToCurrentRow();
            assertTrue(rs.getInt("id") == 3 );
            assertTrue(rs.getString("name").equals("dave3"));

            assertTrue( rs.next() );
            assertTrue(rs.getInt("id") == 4 );
            assertTrue(rs.getString("name").equals("dave4"));

            assertTrue( rs.next() );
            assertTrue(rs.getInt("id") == 5 );
            assertTrue(rs.getString("name").equals("dave5"));

          }
      }
      catch(SQLException ex)
      {
        fail(ex.getMessage());
      }

			st.close();

      JDBC2Tests.dropTable( con,"updateable" );
			JDBC2Tests.closeDB( con );
		}
		catch (Exception ex)
		{
			fail(ex.getMessage());
		}
	}


}