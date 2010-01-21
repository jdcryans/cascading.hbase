/*
 * Copyright (c) 2007-2009 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package cascading.hbase;

import java.io.IOException;

import cascading.flow.Flow;
import cascading.tuple.TupleEntryIterator;
import org.apache.hadoop.hbase.HBaseClusterTestCase;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 */
public class HBaseTestCase extends HBaseClusterTestCase
  {
  public HBaseTestCase( int i, boolean b )
    {
    super( i, b );
    }

  protected void verifySink( Flow flow, int expects ) throws IOException
    {
    int count = 0;

    TupleEntryIterator iterator = flow.openSink();

    while( iterator.hasNext() )
      {
      count++;
      System.out.println( "iterator.next() = " + iterator.next() );
      }

    iterator.close();

    assertEquals( "wrong number of values in " + flow.getSink().toString(), expects, count );
    }

  protected void loadTable( String tableName, String charCol, int size ) throws IOException
    {
    HTable table = new HTable( conf, tableName );

    for( int i = 0; i < size; i++ )
      {
      byte[] bytes = Bytes.toBytes( Integer.toString( i ) );
      Put put = new Put( bytes );

      put.add(KeyValue.parseColumn(Bytes.toBytes(charCol))[0], KeyValue.parseColumn(Bytes.toBytes(charCol))[1] , bytes );

      table.put( put );
      }

    table.close();
    }

  protected void verify( String tableName, String charCol, int expected ) throws IOException
    {
    byte[][] columns = Bytes.toByteArrays( new String[]{charCol} );

    HTable table = new HTable( conf, tableName );
    Scan scanner = new Scan();
    scanner.addColumns(columns);
    ResultScanner rs = table.getScanner(scanner);
    int count = 0;
    for( Result rowResult : rs )
      {
      count++;
      byte[][] bytes = KeyValue.parseColumn( Bytes.toBytes(charCol ) );
      System.out.println( "rowResult = " + rowResult.getValue(bytes[0], bytes[1] ) );
      }

    rs.close();

    assertEquals( "wrong number of rows", expected, count );
    }
  }
